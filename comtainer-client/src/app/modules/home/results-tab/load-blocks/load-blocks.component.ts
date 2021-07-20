import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-load-blocks',
  templateUrl: './load-blocks.component.html',
  styleUrls: ['./load-blocks.component.css'],
})
export class LoadBlocksComponent implements OnInit, OnChanges {
  @Input() loadBlocksData: any;

  currentContainerData: any;
  loadBlocksImages = [];

  constructor() {}

  ngOnInit() {
  }

  ngOnChanges() {

    this.currentContainerData = this.loadBlocksData;

    this.loadBlocksImages = [];
    this.currentContainerData?.loadPlan?.loadPlanSteps?.forEach((step: any) => {
      this.loadBlocksImages.push(step.image);
    });
  }
}
