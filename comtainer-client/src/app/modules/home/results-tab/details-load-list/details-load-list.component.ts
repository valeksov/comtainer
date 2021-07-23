import { Component, Input, OnChanges, OnInit } from '@angular/core';

@Component({
  selector: 'app-details-load-list',
  templateUrl: './details-load-list.component.html',
  styleUrls: ['./details-load-list.component.css'],
})
export class DetailsLoadListComponent implements OnInit, OnChanges {
  @Input() loadListData: any;
  @Input() groups: any;

  displayedColumns: string[] = [
    'number',
    'color',
    'groupName',
    'groupAlias',
    'cargo',
    'quantity',
    'pieces',
    'volume',
    'weight',
  ];

  dataSource: LoadListItem[];

  constructor() {}

  ngOnInit() {}

  ngOnChanges() {
    this.dataSource = this.loadListData?.loadPlan?.items;
  }

  calculateVolume(element: LoadListItem) {
    const volume =
      (element.width / 1000) *
      (element.height / 1000) *
      (element.length / 1000) *
      element.quantity;
    return volume;
  }
}

export interface LoadListItem {
  cargoStyle: number;
  color: string;
  groupName: string;
  groupAlias: string;
  height: number;
  id: string;
  length: number;
  maxLayer: number;
  name: string;
  quantity: number;
  rotatable: boolean;
  selfStackable: true;
  stackable: boolean;
  weight: number;
  width: number;
}
