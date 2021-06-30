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

    generateItem = row => {
        return {
            id: row.itemId,
            name: row.itemName,
            length: row['length_(mm)'],
            width: row['width_(mm)'],
            height: row['height_(mm)'],
            weight: row['weight_(kg)'],
            quantity: row.quantity,
            cargoStyle: row.cargoStyle,
            rotatable: row.rotatable,
            stackable: row.stackable,
            color: row.color ? `#${row.color}` : null,
            selfStackable: row.selfStackable,
        };
    };

    generateGroups = (rows): Array<Group> => {
        const groups = [];

        // Stores the group data in the following format {'groupId1': 'groupDataObj1', 'groupId2: 'groupDataObj2'}.
        const groupsMap = new Map();
        // Stores the alias groups data in the following format
        // {'groupAliasId1': ['groupId1', 'groupId1'], 'groupAliasId2: ['groupId1']}.
        const aliasGroups = {};

        rows.forEach(row => {
            const isGroupAlreadyInMap = groupsMap.has(row.groupId);

            // Add new group to groupsMap.
            if (!isGroupAlreadyInMap) {
                groupsMap.set(row.groupId, {
                    id: row.groupId,
                    name: row.groupName,
                    items: [this.generateItem(row)],
                    color: null,
                    stackGroupOnly: row.stackGroupOnly,
                    alreadyLoaded: row.alreadyLoaded,
                });
            }

            // Update the group data for groupsMap.
            if (isGroupAlreadyInMap) {
                const groupData = cloneDeep(groupsMap.get(row.groupId));

                groupsMap.set(row.groupId, {
                    ...groupData,
                    items: [...groupData.items, this.generateItem(row)],
                });
            }

            // TODO vasko - export the aliasGroups generating to different function.
            // Store alias groups data in aliasGroups object if there are any alias groups.
            if (row.groupAlias) {
                const isAlreadyInAliasGroups = Boolean(aliasGroups[row.groupAlias]);

                // Add new alias group to the aliasGroup object.
                if (!isAlreadyInAliasGroups) {
                    aliasGroups[row.groupAlias] = [row.groupId];
                }

                if (isAlreadyInAliasGroups) {
                    const groupIds = [...aliasGroups[row.groupAlias]];

                    // Check if the groupId is already in the alias group as we don't need duplicated group ids.
                    // If it's not in the alias group - add it.
                    if (!groupIds.includes(row.groupId)) {
                        aliasGroups[row.groupAlias] = [...groupIds, row.groupId];
                    }
                }
            }
        });

        // Get the alias groups in here.
        const hasAliasGroups = Boolean(Object.keys(aliasGroups).length);

        if (hasAliasGroups) {
            // Iterate the alias groups.
            Object.entries(aliasGroups).forEach(([key, value]) => {
                const currentAliasGroup = aliasGroups[key];
                const regularGroups = [];

                // Get all regular groups that are included in the alias group.
                currentAliasGroup.forEach(g => {
                    const regularGroup = groupsMap.get(g);
                    regularGroups.push(regularGroup);
                    groupsMap.delete(g);
                });

                // Create new alias group.
                const group = {
                    id: key,
                    name: 'hard coded',
                    items: [],
                    color: null,
                    stackGroupOnly: false,
                    alreadyLoaded: false,
                    groups: cloneDeep(regularGroups),
                };

                groupsMap.set(group.id, { ...group });
            });
        }

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
