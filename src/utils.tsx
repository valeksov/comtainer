import { saveAs } from 'file-saver';

// TODO vasko - add typescript later.
export const exportToJson = (objectData, fileName = 'file') => {
    const filename = `${fileName}.json`;
    const contentType = 'application/json;charset=utf-8;';

    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
        const blob = new Blob([decodeURIComponent(encodeURI(JSON.stringify(objectData)))], { type: contentType });
        navigator.msSaveOrOpenBlob(blob, filename);
    } else {
        const a = document.createElement('a');
        a.download = filename;
        a.href = 'data:' + contentType + ',' + encodeURIComponent(JSON.stringify(objectData));
        a.target = '_blank';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }
};

// TODO vasko - add typescript later.
export const downloadZipFile = (data, dataType = 'application/octet-stream', filename = 'result') => {
    if (!data) {
        return;
    }

    saveAs(new Blob([data], { type: dataType }), filename + '.zip');
};
