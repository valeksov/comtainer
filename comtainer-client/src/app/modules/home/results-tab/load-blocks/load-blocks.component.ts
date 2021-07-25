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
  constructor() {}

  ngOnInit() {}

  ngOnChanges() {
    this.currentContainerData = this.loadBlocksData;
    this.loadBlocks = [];
    this.extractSteps();
    this.mapLoadingSteps();
  }

  extractSteps() {
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
  }

  mapLoadingSteps() {
    this.loadBlocks.forEach((block: any) => {
      const cargoes = block.cargo;

      const individual = cargoes.map((cargo: any) => {
        return cargo.cargo.name;
      });
      const counts = {};

      individual.forEach(function (c) {
        counts[c] = (counts[c] || 0) + 1;
      });

      const info: any = {};
      info.items = counts;
      block.description = info;
    });
  }
}
