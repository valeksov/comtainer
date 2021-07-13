import { Component, OnInit } from '@angular/core';
import * as XLSX from 'xlsx';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  convertedJson = '';

  constructor() {}

  ngOnInit() {}

  onUploadFile(e: any) {
    const uploadedFile = e.target.files[0];
    const fileReader = new FileReader();
    fileReader.readAsBinaryString(uploadedFile);
    fileReader.onload = (event) => {
      let binaryData = event.target?.result;
      let workSheet = XLSX.read(binaryData, { type: 'binary' });
      workSheet.SheetNames.forEach((sheet) => {
        const data = XLSX.utils.sheet_to_json(workSheet.Sheets[sheet]);
        this.convertedJson = JSON.stringify(data); 
      });
    };
  }
}
