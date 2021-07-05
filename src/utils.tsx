import FileSaver from 'file-saver';

export const exportToFile = (data: string, filename: string, fileType: string): void => {
    FileSaver.saveAs(new Blob([data]), `${filename}.${fileType}`);
};
