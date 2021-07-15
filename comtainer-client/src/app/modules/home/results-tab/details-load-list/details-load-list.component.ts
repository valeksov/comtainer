import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-details-load-list',
  templateUrl: './details-load-list.component.html',
  styleUrls: ['./details-load-list.component.css'],
})
export class DetailsLoadListComponent implements OnInit {
  displayedColumns: string[] = ['number', 'color', 'group', 'groupAlias', 'cargo', 'qty', 'pieces', 'volume', 'weight'];
  dataSource = ELEMENT_DATA;

  constructor() {}

  ngOnInit() {}
}

export interface Container {
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

const ELEMENT_DATA: Container[] = [
  {
    number: '1',
    color: '4b31a7',
    group: '101291',
    groupAlias: 'C1',
    cargo: '5272',
    qty: '8',
    pieces: '4',
    volume: '11.55',
    weight: '923.76'
  },
  {
    number: '2',
    color: 'C4D210',
    group: '101294',
    groupAlias: 'B1',
    cargo: '5274',
    qty: '3',
    pieces: '7',
    volume: '112.55',
    weight: '342.76'
  },
  {
    number: '3',
    color: 'b69a9a',
    group: '101222',
    groupAlias: 'D1',
    cargo: '5273',
    qty: '18',
    pieces: '2',
    volume: '41.55',
    weight: '1223.76'
  }
];


// •	Used Volume: in cubic mm (Sum of all cargo pieces volumes from the steps)
// •	Used Floor Area: in square mm (Sum of all cargo pieces areas: length*width after rotation if any from the steps with StartZ/height coordinate=0)
// •	Free Floor Area: in square mm (Container Floor Area: length*width - Used Floor Area)
// •	Used Floor Area: in percent from the container floor area
// •	Used Length in mm - Max EndX coordinate (StartX+length) after rotation if any from the cargo piece placed in the container
// •	Free Length in mm - Container Length - Used Length
// •	Used Length in percent from the Container Length
// •	Used Width in mm - Max EndY coordinate (StartY+width) after rotation if any from the cargo piece placed in the container
// •	Free Width in mm - Container Width - Used Width
// •	Used Width in percent from the Container Width	
// •	Used Height in mm - Max EndZ coordinate (StartZ+height) after rotation if any from the cargo piece placed in the container
// •	Free Height in mm - Container Height - Used Height
// •	Used Height in percent from the Container Height
