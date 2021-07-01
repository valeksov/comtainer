import { Button, Input } from '@material-ui/core';
import React, { useCallback, useState } from 'react';
import { GenerateJSONFromXls } from 'services/generate-json';
import XLSX from 'xlsx';
import styles from './styles/App.module.scss';

function App() {
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
            console.log({ finalJSON });
            // TODO vasko - uncomment if we need the functionality to download the json data as a .json file.
            // exportToJson(finalJSON);
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
}

export default App;
