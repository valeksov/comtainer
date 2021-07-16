import { Component, Inject, OnInit, Optional } from '@angular/core';
import { MenageDrawnImagesService } from '../core/services/menage-drawn-images.service';
import { GenerateJsonService } from '../home/home/generate-json/generate-json.service';
import { ConvertedXlsDto } from '../home/home/generate-json/generate-json.types';
import * as XLSX from 'xlsx';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from 'src/app/store/app.state';
import * as LoadActions from '../../store/loadPlan.actions';
import { ILoadPlan } from 'src/app/store/loadPlan';
@Component({
  selector: 'app-upload-xls',
  templateUrl: './upload-xls.component.html',
  styleUrls: ['./upload-xls.component.css'],
})
export class UploadXlsComponent implements OnInit {
  convertedJson = {};
  dataForGeneratingImages!: ConvertedXlsDto;
  selectedFile: File;
  selectedFileName: string = '';

  constructor(
    private generateJsonService: GenerateJsonService,
    private httpLoadPlanService: MenageDrawnImagesService,
    private store: Store<AppState>,
    private dialogRef: MatDialogRef<UploadXlsComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.store.select('loadPlan');
  }

  ngOnInit() {}

  onUploadFile(e: any) {
    this.selectedFile = e.target.files[0];
    this.selectedFileName = this.selectedFile.name;
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

      const data: any = this.httpLoadPlanService.getLoadPlan(
        this.dataForGeneratingImages
      );
      this.store.dispatch(new LoadActions.AddLoadPlan(data));
      this.dialogRef.close();
    };
  }
}
