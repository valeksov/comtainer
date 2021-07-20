import { Component, Inject, OnInit, Optional } from '@angular/core';
import { MenageDrawnImagesService } from '../core/services/menage-drawn-images.service';
import { GenerateJsonService } from '../home/home/generate-json/generate-json.service';
import { ConvertedXlsDto } from '../home/home/generate-json/generate-json.types';
import * as XLSX from 'xlsx';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from 'src/app/store/app.state';
import * as LoadActions from '../../store/loadPlan.actions';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-upload-xls',
  templateUrl: './upload-xls.component.html',
  styleUrls: ['./upload-xls.component.css'],
})
export class UploadXlsComponent implements OnInit {
  loading = false;
  convertedJson = {};
  dataForGeneratingImages!: ConvertedXlsDto;
  selectedFile: File;
  selectedFileName: string = '';
  loadPlanResponse: any;
  constructor(
    private generateJsonService: GenerateJsonService,
    private httpLoadPlanService: MenageDrawnImagesService,
    private store: Store<AppState>,
    private dialogRef: MatDialogRef<UploadXlsComponent>,
    private infoService: ToastrService,
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
    this.loading = true;
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

      this.httpLoadPlanService
        .getLoadPlan(this.dataForGeneratingImages)
        .subscribe((loadPlan: any) => {
          console.log(loadPlan);
          if (loadPlan.status === 200 && loadPlan.statusText === 'OK') {
            this.loadPlanResponse = loadPlan.body;
            this.store.dispatch(
              new LoadActions.AddLoadPlan(this.loadPlanResponse)
            );
            this.dialogRef.close();
            this.loading = false;
            this.infoService.success('Successfully converted');
          } else {
            console.log('ERROR:', loadPlan.status);
            this.infoService.error('Error during converting');
            this.loading = false;
          }
        });
    };
  }
}
