import { DecimalPipe } from '@angular/common';
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
  groups = [];
  selectedContainer: any;
  selectedContainerName: string = '';
  summaryData: any;
  loadListData: any;
  loadBlocksData: any;
  selectView: string = '3D_View';

  containerStats: any;

  constructor(
    private store: Store<AppState>,
    private roundingPipe: DecimalPipe
  ) {
    this.store.select('loadPlan').subscribe((plan) => {
      this.extractNeededData(plan);
    });
  }

  ngOnInit() {}

  changeContainerView(view: string): void {
    this.selectView = view;
  }

  private extractNeededData(planData: any) {
    this.containers = planData.containers;
    this.groups = planData.groups;
    this.containerStats = planData?.containers?.loadPlan?.stats;
  }

  createContainerMetaData(container: any) {
    const statsObj = container.loadPlan.stats;
    // extract cargoes count
    const cargoes = statsObj.filter((stat: any) => stat.name === 'Cargoes');
    const cargoesCount = cargoes[0].used;

    // extract volume
    const volumeObj = statsObj.filter(
      (stat: any) => stat.name === 'Volume (m3)'
    );
    const usedVolume = volumeObj[0].used / 1000000000;
    const roundedVolume = this.roundingPipe.transform(usedVolume, '1.2-2');
    // extract weight
    const weightObj = statsObj.filter(
      (stat: any) => stat.name === 'Weight (kg)'
    );

    const totalWeight = weightObj[0].used;
    const roundedWeight = this.roundingPipe.transform(totalWeight);

    return (
      'Cargoes: ' +
      cargoesCount +
      '; ' +
      roundedVolume +
      '(m3); ' +
      roundedWeight +
      '(kg)'
    );
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
