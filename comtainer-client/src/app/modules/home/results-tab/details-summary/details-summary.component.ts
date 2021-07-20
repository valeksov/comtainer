import { Component, Input, OnChanges, OnInit } from '@angular/core';

@Component({
  selector: 'app-details-summary',
  templateUrl: './details-summary.component.html',
  styleUrls: ['./details-summary.component.css']
})
export class DetailsSummaryComponent implements OnInit, OnChanges {
  @Input() summaryData: any;

  summaryTabData: any;
  containerName = '';
  displayedColumns: string[] = ['name', 'max', 'used', 'free', 'usedInPercent', 'freeInPercent'];
  dataSource: any;

   constructor() {
  }

  ngOnInit() {}

  ngOnChanges(){
    this.summaryTabData = this.summaryData?.data;
    this.dataSource = this.summaryTabData;
    this.containerName = this.summaryData?.containerName;
  }
}
