import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Store } from '@ngrx/store';
import { AppState } from 'src/app/store/app.state';

@Component({
  selector: 'app-results-tab',
  templateUrl: './results-tab.component.html',
  styleUrls: ['./results-tab.component.css'],
})
export class ResultsTabComponent implements OnInit {
  centered = false;
  disabled = false;
  unbounded = false;

  loadPlanAllData: any;
  containers = [];
  selectedContainer: any;
  selectedContainerName: string = '';
  summaryData: any;
  loadListData: any;
  loadBlocksData: any;
  selectView: string = '3D_View';

  constructor(private store: Store<AppState>) {
    this.store.select('loadPlan').subscribe((plan) => {
      this.extractNeededData(plan);
    });
  }

  ngOnInit() {}

  // changeContainerView(view: string) {
  //   // this.selectView = view;
  // }

  changeContainerView(view: string): void {
    console.log('View:', view);
    this.selectView = view;
    
  }

  private extractNeededData(planData: any) {
    this.containers = planData.containers;
    console.log('Containers:', this.containers);
  }

  onClickToRipple(container: any) {
    this.selectedContainer = container;
    this.selectedContainerName = this.selectedContainer.id;
    this.extractSummary(this.selectedContainer);
    this.extractLoadListData(this.selectedContainer);
    this.extractLoadBlocksData(this.selectedContainer);
  }

  extractSummary(containerData: any) {
    this.summaryData = {
      data: containerData.loadPlan.stats,
      containerName: this.selectedContainerName,
    };
  }

  extractLoadListData(containerData: any) {
    this.loadListData = containerData;
  }

  extractLoadBlocksData(containerData: any) {
    this.loadBlocksData = containerData;
  }
}
