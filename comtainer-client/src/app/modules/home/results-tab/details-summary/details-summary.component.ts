import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-details-summary',
  templateUrl: './details-summary.component.html',
  styleUrls: ['./details-summary.component.css']
})
export class DetailsSummaryComponent implements OnInit {

  displayedColumns: string[] = ['name', 'secondName', 'max', 'used', 'notUsed', 'usedInPerc', 'notUsedInPerc'];
  dataSource = ELEMENT_DATA;

  constructor() {}

  ngOnInit() {}
}

export interface Summary {
  name: string;
  secondName: string;
  max: string;
  used: string;
  notUsed: string;
  usedInPerc: string;
  notUsedInPerc: string;
}

const ELEMENT_DATA: Summary[] = [
  {
    name: 'container1',
    secondName: '40DV',
    max: '12,000.00',
    used: '12.65',
    notUsed: '54.75',
    usedInPerc: '46.67',
    notUsedInPerc: '81.23',
  },
  {
    name: 'container1',
    secondName: '40DV',
    max: '2,340.20',
    used: '2,345.90',
    notUsed: '130.00',
    usedInPerc: '18.77',
    notUsedInPerc: '14.85',
  }
];
