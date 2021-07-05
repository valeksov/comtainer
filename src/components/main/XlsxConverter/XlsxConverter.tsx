import { Button, Input } from '@material-ui/core';
import React, { useCallback, useState } from 'react';
import { GenerateJSONFromXls } from 'services/generate-json';
import { useRootStore } from 'store/StoreProvider';
import { downloadZipFile, exportToJson } from 'utils';
import XLSX from 'xlsx';
import styles from './XlsxConverter.module.scss';

const XlsxConverterComponent = () => {
    const { generalStore, containersStore } = useRootStore();
    const [selectedFile, setSelectedFile] = useState(null);

    const handleFileUpload = e => {
        setSelectedFile(e.target.files[0]);
    };

    const handleFileConversion = useCallback(() => {
        if (!selectedFile) {
            return;
        }

        const fileReader = new FileReader();
        fileReader.readAsBinaryString(selectedFile);

        fileReader.onload = async event => {
            const fileData = event.target.result;
            const finalJSON = GenerateJSONFromXls.generateFinalJSON(XLSX.read(fileData, { type: 'binary' }));

            // Export the json file.
            // exportToJson(finalJSON);
            generalStore.showSuccessMessage(
                'XLSX file is successfully converted! Please wait for the zipped files to start downloading!'
            );

            // Call the backend service to receive the zipped file with the load plan.
            const zipResponse = await containersStore.getLoadingPlan(finalJSON);
            downloadZipFile(zipResponse);
        };
    }, [selectedFile]);

    return (
        <div className={styles.container}>
            <Input type="file" color="primary" onChange={handleFileUpload} />
            <Button variant="contained" color="primary" onClick={handleFileConversion}>
                Convert
            </Button>
        </div>
    );
};

const XlsxConverter = XlsxConverterComponent;
export { XlsxConverter };
