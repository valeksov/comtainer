import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { asapScheduler } from 'rxjs';

@Component({
  selector: 'app-load-blocks',
  templateUrl: './load-blocks.component.html',
  styleUrls: ['./load-blocks.component.css'],
})
export class LoadBlocksComponent implements OnInit, OnChanges {
  @Input() loadBlocksData: any;

  currentContainerData: any;
  loadBlocks = [];
  loadBlocksDescription = [];
  constructor() {}

  ngOnInit() {}

  ngOnChanges() {
    this.currentContainerData = this.loadBlocksData;
    this.loadBlocks = [];

    this.currentContainerData?.loadPlan?.loadPlanSteps?.forEach(
      (step: any, index: number) => {
        let block = {
          image: '',
          cargo: [],
          index: 0,
        };
        block.image = step.image;
        block.index = index + 1;
        block.cargo.push(...step.items);
        this.loadBlocks.push(block);
      }
    );
    this.mapLoadingSteps();
  }

  mapLoadingSteps() {
    this.loadBlocksDescription = [];
    this.loadBlocks.forEach((block: any, index: number) => {
      const cargoes = block.cargo;
      cargoes.forEach((k: any) => {
        const info: any = {};
        info.name = k.cargo.name;
        info.index = index + 1;
        this.loadBlocksDescription.push(info);
      });
    });
    this.calculateBlocks();
  }

  calculateBlocks() {
    //TODO
  }
}
