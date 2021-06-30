import React from 'react';
import { Button, Input } from '@material-ui/core';
import XLSX from 'xlsx';
import styles from './styles/App.module.scss';

function App() {
    const convertedXlsToJson = {
        containers: [],
        config: {},
        groups: [],
    };

    let selectedFile;

    const handleFileUpload = e => {
        selectedFile = e.target.files[0];
        console.log({ selectedFile });
    };

    const handleXlsConversion = () => {
        if (!selectedFile) {
            return;
        }

        const fileReader = new FileReader();
        fileReader.readAsBinaryString(selectedFile);

        fileReader.onload = event => {
            const data = event.target.result;

            const workbook = XLSX.read(data, { type: 'binary' });
            workbook.SheetNames.forEach(sheet => {
                if (sheet === 'Containers') {
                    const rowData = XLSX.utils.sheet_to_json(workbook.Sheets[sheet]);

                    rowData.forEach(col => {
                        convertedXlsToJson.containers.push({
                            id: col['id'],
                            name: col['name'],
                            length: col['length_(mm)'],
                            width: col['width_(mm)'],
                            height: col['height_(mm)'],
                            maxAllowedVolume: col['maxAllowedVolume_(mm3)'],
                            maxAllowedWeight: col['maxAllowedWeigth_(kg)'],
                            loadPlan: null,
                        });
                    });
                }

                if (sheet === 'Config') {
                    const rowData = XLSX.utils.sheet_to_json(workbook.Sheets[sheet]);
                    // Remove the first row from the data, as it is for description of the columns value types.
                    rowData.shift();

                    convertedXlsToJson.config = {
                        cargoSupport: rowData[0]['cargoSupport'],
                        lightUnstackableWeightLimit: rowData[0]['lightUnstackableWeightLimit'],
                        allowHeavierCargoOnTop: rowData[0]['allowHeavierCargoOnTop'],
                        maxHeavierCargoOnTop: rowData[0]['maxHeavierCargoOnTop'],
                        keepGroupsTogether: rowData[0]['keepGroupsTogether'],
                    };
                }

                // if (sheet === 'Containers') {
                // }

                console.log({ sheet });
                // let rowObject = XLSX.utils.sheet_to_row_object_array(workbook.Sheets[sheet]);
                // console.log(rowObject);
            });
        };
    };

    return (
        <div className={styles.container}>
            <Input type="file" color="primary" onChange={handleFileUpload} />
            <Button variant="contained" color="primary" onClick={handleXlsConversion}>
                Convert
            </Button>
        </div>
    );
}

export default App;
