import { XlsxConverter } from 'components/main/XlsxConverter';
import React from 'react';
import styles from './App.module.scss';

function App() {
    return (
        <div className={styles.container}>
            <XlsxConverter />
        </div>
    );
}

export default App;
