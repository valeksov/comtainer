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

  ngOnInit() {}

  ngOnChanges() {
    this.currentContainerData = this.loadBlocksData;
    console.log(this.currentContainerData);
    this.loadBlocksImages = [];

    this.currentContainerData?.loadPlan?.loadPlanSteps?.forEach(
      (step: any, index) => {
        // console.log(step, index);
        let block = {
          image: '',
          cargo: [],
          index: 0,
        };
        block.image = step.image;
        block.index = index + 1;
        console.log('Each Step', step);
        const counts = { };
        step.items.forEach((item: any) => {
          counts[item] = (counts[item.cargo.name] || 0) + 1;

          // let c = {};
          // c.name = item.cargo.name;

          // (c.cargoes = item.cargo.name), (c.qnty = 1);
          // block.cargo.push(c);
          console.log(counts.item);
        });
        this.loadBlocksImages.push(block);
      }
    );
    // console.log(this.loadBlocksImages);
  }
}
