import cloneDeep from 'clone-deep';
import { StringifiedBooleanOptions } from './generate-json.types';

export const getConvertedRowValue = value => {
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

export const getTransformedCargoData = rows => {
    // Get the Cargo/Groups data in format similar to the other sheets(with correct columnName values).
    const cargoData = [];

    const rowsCopy = cloneDeep(rows);
    const columnNames = { ...rowsCopy[0] };

    // Remove the first 2 rows, as they are holding the sheet metadata(columns names and types).
    rowsCopy.shift();
    rowsCopy.shift();

    rowsCopy.forEach(row => {
        const data = {};

        Object.entries(row).forEach(([key, value]) => {
            const colKey = columnNames[key];
            data[colKey] = getConvertedRowValue(value);
        });

        cargoData.push(data);
    });

    return cargoData;
};
