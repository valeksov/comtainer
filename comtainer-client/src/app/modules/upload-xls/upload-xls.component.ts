import { Component, OnInit } from '@angular/core';
import { MenageDrawnImagesService } from '../core/services/menage-drawn-images.service';
import { GenerateJsonService } from '../home/home/generate-json/generate-json.service';
import { ConvertedXlsDto } from '../home/home/generate-json/generate-json.types';
import * as XLSX from 'xlsx';

@Component({
  selector: 'app-upload-xls',
  templateUrl: './upload-xls.component.html',
  styleUrls: ['./upload-xls.component.css'],
})
export class UploadXlsComponent implements OnInit {
  convertedJson = {};
  dataForGeneratingImages!: ConvertedXlsDto;
  selectedFile: File;

  constructor(
    private generateJsonService: GenerateJsonService,
    private httpLoadPlanService: MenageDrawnImagesService
  ) {}

  ngOnInit() {}

  onUploadFile(e: any) {
    this.selectedFile = e.target.files[0];
  }

  handleFileConversion() {

    if (this.selectedFile === undefined) {
      return;
    }

    const fileReader = new FileReader();

    fileReader.readAsBinaryString(this.selectedFile);

    fileReader.onload = async (event) => {
      let binaryData = event.target?.result;

      const jsonObject: ConvertedXlsDto =
        this.generateJsonService.generateFinalJSON(
          XLSX.read(binaryData, { type: 'binary' })
        );
      this.dataForGeneratingImages = jsonObject;
      const data = this.httpLoadPlanService.getLoadPlan(
        this.dataForGeneratingImages
      );

      console.log('return data', data);
    };
  }
}
