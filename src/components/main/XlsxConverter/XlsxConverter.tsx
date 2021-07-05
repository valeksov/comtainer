import { Button, Input } from '@material-ui/core';
import React, { useCallback, useState } from 'react';
import { GenerateJSONFromXls } from 'services/generate-json';
import { ConvertedXlsDto } from 'services/generate-json/generate-json.types';
import { useRootStore } from 'store/StoreProvider';
import { exportToFile } from 'utils';
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
            const jsonObject: ConvertedXlsDto = GenerateJSONFromXls.generateFinalJSON(
                XLSX.read(fileData, { type: 'binary' })
            );

            generalStore.showSuccessMessage(
                `The input file is successfully converted!
                Please wait for the exported zip file to start downloading!`
            );

            // Get the load plan data.
            const loadPlan = await containersStore.getLoadPlan(jsonObject);
            exportToFile(loadPlan.data, 'loadPlan', 'zip');
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
