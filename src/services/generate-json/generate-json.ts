import cloneDeep from 'clone-deep';
import XLSX from 'xlsx';
import { Config, Container, ConvertedXlsDto, Group, SheetOptions } from './generate-json.types';

// TODO vasko - see why i cant use private, public methods with typescript in here.

const INITIAL_JSON: ConvertedXlsDto = {
    containers: [],
    config: null,
    groups: [],
};

enum StringifiedBooleanOptions {
    True = 'true',
    False = 'false',
}

class GenerateJSONFromXlsService {
    convertedJSON: ConvertedXlsDto;

    constructor() {
        this.convertedJSON = INITIAL_JSON;
    }

    getConvertedRowValue = (value: any): boolean => {
        // Since the xls table data holds boolean values as strings('true', 'false'),
        // we need to convert them to booleans for the constructed JSON to work properly.
        // If the value is not a stringified boolean, just return it without any other manipulations.
        if (value === StringifiedBooleanOptions.True) {
            return true;
        }

        if (value === StringifiedBooleanOptions.False) {
            return false;
        }

        return value;
    };

    getTransformedCargoData = rows => {
        // Get the cargo/groups data in format similar to the other sheets, with correct columnName values.
        const cargoData = [];

        const clonedRows = cloneDeep(rows);
        const columnNames = { ...clonedRows[0] };

        // Remove the first 2 rows, as they are holding the sheet metadata(columns names and types).
        clonedRows.shift();
        clonedRows.shift();

        clonedRows.forEach(row => {
            const data = {};

            Object.entries(row).forEach(([key, value]) => {
                const colKey = columnNames[key];
                data[colKey] = this.getConvertedRowValue(value);
            });

            cargoData.push(data);
        });

        return cargoData;
    };

    generateConfig = (rows): Config => {
        const clonedRows = cloneDeep(rows);

        // Remove the first row, as it is holding the sheet metadata(about the column types).
        clonedRows.shift();

        // Get the row that contains the sheet data.
        const row = clonedRows[0];

        return {
            cargoSupport: row['cargoSupport'],
            lightUnstackableWeightLimit: row['lightUnstackableWeightLimit'],
            maxHeavierCargoOnTop: row['maxHeavierCargoOnTop'],
            allowHeavierCargoOnTop: this.getConvertedRowValue(row['allowHeavierCargoOnTop']),
            keepGroupsTogether: this.getConvertedRowValue(row['keepGroupsTogether']),
        };
    };

    generateContainers = (rows): Array<Container> => {
        const containers = [];
        const clonedRows = cloneDeep(rows);

        clonedRows.forEach(row => {
            containers.push({
                id: row['id'],
                name: row['name'],
                length: row['length_(mm)'],
                width: row['width_(mm)'],
                height: row['height_(mm)'],
                maxAllowedVolume: row['maxAllowedVolume_(mm3)'],
                maxAllowedWeight: row['maxAllowedWeigth_(kg)'],
                loadPlan: null,
            });
        });

        return containers;
    };

    generateGroups = (rows): Array<Group> => {
        const groups = [];

        const groupsMap = new Map();
        const aliasMap = new Map();

        rows.forEach(rowData => {
            // Check for alias groups and store rowData for each alias group.
            if (rowData.groupAlias) {
                if (!aliasMap.has(rowData.groupAlias)) {
                    aliasMap.set(rowData.groupAlias, [{ ...rowData }]);
                }

                if (aliasMap.has(rowData.groupAlias)) {
                    const copiedData = cloneDeep(aliasMap.get(rowData.groupAlias));
                    aliasMap.set(rowData.groupAlias, [...copiedData, { ...rowData }]);
                }
            }

            // There is no such group - create new one.
            if (!groupsMap.has(rowData.groupId)) {
                groupsMap.set(rowData.groupId, {
                    id: rowData.groupId,
                    name: rowData.groupName,
                    items: [
                        {
                            id: rowData.itemId,
                            name: rowData.itemName,
                            length: rowData['length_(mm)'],
                            width: rowData['width_(mm)'],
                            height: rowData['height_(mm)'],
                            weight: rowData['weight_(kg)'],
                            quantity: rowData.quantity,
                            cargoStyle: rowData.cargoStyle,
                            rotatable: rowData.rotatable,
                            stackable: rowData.stackable,
                            color: rowData.color ? `#${rowData.color}` : null,
                            selfStackable: rowData.selfStackable,
                        },
                    ],
                    color: null,
                    stackGroupOnly: rowData.stackGroupOnly,
                    alreadyLoaded: rowData.alreadyLoaded,
                });
            }

            // There already is such a group - update the group items data.
            if (groupsMap.has(rowData.groupId)) {
                const groupData = groupsMap.get(rowData.groupId);

                groupsMap.set(rowData.groupId, {
                    ...groupData,
                    items: [
                        ...groupData.items,
                        {
                            id: rowData.itemId,
                            name: rowData.itemName,
                            length: rowData['length_(mm)'],
                            width: rowData['width_(mm)'],
                            height: rowData['height_(mm)'],
                            weight: rowData['weight_(kg)'],
                            quantity: rowData.quantity,
                            cargoStyle: rowData.cargoStyle,
                            rotatable: rowData.rotatable,
                            stackable: rowData.stackable,
                            selfStackable: rowData.selfStackable,
                        },
                    ],
                });
            }
        });

        // Get the alias groups in here.

        // Get final group values.
        for (const value of groupsMap.values()) {
            groups.push(value);
        }

        return groups;
    };

    generateFinalJSON = (workbook): ConvertedXlsDto => {
        // Iterate over the xls file sheets and get the data in JSON format.
        workbook.SheetNames.forEach(sheet => {
            if (sheet === SheetOptions.Config) {
                this.convertedJSON.config = this.generateConfig(XLSX.utils.sheet_to_json(workbook.Sheets[sheet]));
            }

            if (sheet === SheetOptions.Containers) {
                this.convertedJSON.containers = this.generateContainers(
                    XLSX.utils.sheet_to_json(workbook.Sheets[sheet])
                );
            }

            if (sheet === SheetOptions.Cargos) {
                this.convertedJSON.groups = this.generateGroups(
                    this.getTransformedCargoData(XLSX.utils.sheet_to_json(workbook.Sheets[sheet]))
                );
            }
        });

        return this.convertedJSON;
    };
}

const GenerateJSONFromXls = new GenerateJSONFromXlsService();
export { GenerateJSONFromXls };
