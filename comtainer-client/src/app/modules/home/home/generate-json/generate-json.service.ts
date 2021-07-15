import { Injectable } from '@angular/core';
import * as XLSX from 'xlsx';
import { Sheet, WorkBook, WorkSheet } from 'xlsx';
import {
  AliasGroup,
  Config,
  Container,
  ConvertedXlsDto,
  Group,
  Item,
  RegularGroup,
  SheetOptions,
} from './generate-json.types';
import {
  getConvertedRowValue,
  getTransformedCargoData,
} from './generate-json.utils';

@Injectable({
  providedIn: 'root',
})
export class GenerateJsonService {
  convertedJSON: ConvertedXlsDto;

  constructor() {
    this.convertedJSON = INITIAL_JSON;
  }

  getGroupsDataMap = (rows: WorkSheet): Map<number | string, Group> => {
    // Stores the group data in the following format {'groupId1': 'groupDataObj1', 'groupId2: 'groupDataObj2'}.
    const groupsMap = new Map();

    rows.forEach((row: XLSX.Sheet) => {
      const isGroupAlreadyInMap = groupsMap.has(row.groupId);

      // Add new group to groupsMap.
      if (!isGroupAlreadyInMap) {
        const group: RegularGroup = {
          id: row.groupId.toString(),
          name: row.groupName.toString(),
          items: [this.generateItem(row)],
          color: null,
          stackGroupOnly: row.stackGroupOnly,
          alreadyLoaded: false,
        };

        groupsMap.set(row.groupId, group);
      }

      // Update the group data for groupsMap.
      if (isGroupAlreadyInMap) {
        // const group = cloneDeep(groupsMap.get(row.groupId));
        const group = JSON.parse(JSON.stringify(groupsMap.get(row.groupId)));
        const updatedGroup: RegularGroup = {
          ...group,
          items: [...group.items, this.generateItem(row)],
        };

        groupsMap.set(row.groupId, updatedGroup);
      }
    });

    return groupsMap;
  };

  getAliasGroupDataObject = (rows: WorkSheet): GenericAliasGroupObject => {
    // Stores the alias groups data in the following format
    // {'groupAliasId1': ['groupId1', 'groupId1'], 'groupAliasId2: ['groupId1']}.
    const aliasGroups = {};

    rows.forEach((row: WorkSheet) => {
      if (!row.groupAlias) {
        return;
      }

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
    });

    return aliasGroups;
  };

  generateItem = (row: Sheet): Item => {
    return {
      id: row.itemId.toString(),
      name: row.itemName.toString(),
      length: row['length_(mm)'],
      width: row['width_(mm)'],
      height: row['height_(mm)'],
      weight: row['weight_(kg)'],
      quantity: row.quantity,
      cargoStyle: row.cargoStyle,
      rotatable: row.rotatable,
      stackable: row.stackable,
      color: row.color || null,
      selfStackable: row.selfStackable,
      maxLayer: row.maxLayer,
    };
  };

  generateConfig = (rows: WorkSheet): Config => {
    // const clonedRows = cloneDeep(rows);
    const clonedRows = JSON.parse(JSON.stringify(rows));

    // Remove the first row, as it is holding the sheet metadata(about the column types).
    clonedRows.shift();

    // Get the row that contains the sheet data.
    const row = clonedRows[0];

    return {
      cargoSupport: row['cargoSupport'],
      lightUnstackableWeightLimit: row['lightUnstackableWeightLimit'],
      maxHeavierCargoOnTop: row['maxHeavierCargoOnTop'],
      maxWeightDiffInPercent: row['maxWeightDiffInPercent'],
      maxWeightDiffInKilos: row['maxWeightDiffInKilos'],
      allowHeavierCargoOnTop: getConvertedRowValue(
        row['allowHeavierCargoOnTop']
      ),
      keepGroupsTogether: getConvertedRowValue(row['keepGroupsTogether']),
    };
  };

  generateContainers = (rows: WorkSheet): Array<Container> => {
    const containers:
      | Container[]
      | {
          id: any;
          name: any;
          length: any;
          width: any;
          height: any;
          maxAllowedVolume: any;
          maxAllowedWeight: any;
          loadPlan: null;
        }[] = [];
    // const clonedRows = cloneDeep(rows);
    const clonedRows = JSON.parse(JSON.stringify(rows));

    clonedRows.forEach((row: { [x: string]: any }) => {
      containers.push({
        id: row['id'].toString(),
        name: row['name'].toString(),
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

  generateGroups = (rows: WorkSheet): Array<Group> => {
    const groups = [];

    const groupsMap: Map<number | string, Group> = this.getGroupsDataMap(rows);
    const aliasGroups: GenericAliasGroupObject =
      this.getAliasGroupDataObject(rows);

    const hasAliasGroups = Boolean(Object.keys(aliasGroups).length);

    // Check if we have alias groups in the sheet.
    if (hasAliasGroups) {
      // Iterate the alias groups.
      Object.entries(aliasGroups).forEach(([key, value]) => {
        const currentAliasGroup = aliasGroups[key];
        const regularGroups: (Group | undefined)[] = [];

        // Get all regular groups that are included in the alias group. Add them to regularGroups list
        // and remove them from groupsMap, as they are no longer considered as a spearate groups but
        // will be placed under in the new alias group in the groups list property.
        currentAliasGroup.forEach((g) => {
          const regularGroup = groupsMap.get(g);
          regularGroups.push(regularGroup);
          groupsMap.delete(g);
        });

        // Create new alias group.
        const group: AliasGroup = {
          id: key.toString(),
          name: key.toString(),
          items: [],
          color: null,
          groups: JSON.parse(JSON.stringify(regularGroups)),
          stackGroupOnly: false,
          alreadyLoaded: false,
        };

        // Add the alias group to groupsMap.
        groupsMap.set(group.id, { ...group });
      });
    }

    // Get final group values as an array.
    for (const value of groupsMap.values()) {
      groups.push(value);
    }

    return groups;
  };

  generateFinalJSON = (workbook: WorkBook): ConvertedXlsDto => {
    // Iterate over the xls file sheets and get the data in JSON format.
    workbook.SheetNames.forEach((sheet) => {
      if (sheet === SheetOptions.Config) {
        this.convertedJSON.config = this.generateConfig(
          XLSX.utils.sheet_to_json(workbook.Sheets[sheet])
        );
      }

      if (sheet === SheetOptions.Containers) {
        this.convertedJSON.containers = this.generateContainers(
          XLSX.utils.sheet_to_json(workbook.Sheets[sheet])
        );
      }

      if (sheet === SheetOptions.Cargos) {
        this.convertedJSON.groups = this.generateGroups(
          getTransformedCargoData(
            XLSX.utils.sheet_to_json(workbook.Sheets[sheet])
          )
        );
      }
    });

    return this.convertedJSON;
  };
}

type GenericAliasGroupObject = { [key: string]: Array<string | number> };

const INITIAL_JSON: ConvertedXlsDto = {
  containers: [],
  config: null,
  groups: [],
};
