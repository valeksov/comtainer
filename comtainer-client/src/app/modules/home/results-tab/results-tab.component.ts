import { Component, Input, OnChanges, OnInit } from '@angular/core';
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

  constructor(private store: Store<AppState>) {
    this.store.select('loadPlan').subscribe((plan) => {
      this.extractNeededData(plan);
    });
  }

  ngOnInit() {}

  private extractNeededData(planData: any) {
    this.containers = planData.containers;
  }

  onClickToRipple(container: any) {
    this.selectedContainer = container;
    this.selectedContainerName = this.selectedContainer.id;
    this.extractSummary(this.selectedContainer);
  }

  extractSummary(containerData: any) {
    this.summaryData = {
      data: containerData.loadPlan.stats,
      containerName: this.selectedContainerName
    };
  }
}
export interface ISummary {
  name: string;
  used: number;
  max: number;
  free: number;
  usedInPercent: number;
  freeInPercent: number;
}

export interface LoadList {
  number: string;
  color: string;
  group: string;
  groupAlias: string;
  cargo: string;
  qty: string;
  pieces: string;
  volume: string;
  weight: string;
}
