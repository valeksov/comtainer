import { Button, Input } from '@material-ui/core';
import React, { useCallback, useState } from 'react';
import { GenerateJSONFromXls } from 'services/generate-json';
import { useRootStore } from 'store/StoreProvider';
import { exportToJson } from 'utils';
import XLSX from 'xlsx';
import styles from './XlsxConverter.module.scss';

const XlsxConverterComponent = () => {
    const { generalStore } = useRootStore();

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
            exportToJson(finalJSON);
            generalStore.showSuccessMessage('XLSX file is successfully converted!');
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
