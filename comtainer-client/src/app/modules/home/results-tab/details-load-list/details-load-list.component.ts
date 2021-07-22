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
  // calculatedQnty: any = [];

  constructor() {}

  ngOnInit() {}

  ngOnChanges() {
    this.dataSource = this.loadListData?.loadPlan?.items;
    // this.calculateQuantity();
    // console.log('DS', this.dataSource);
    // console.log('Gp', this.groups);
  }

  // calculateQuantity() {
  //   this.groups.forEach((group: any) => {
  //     group.items.forEach((item: any) => {
  //       this.calculatedQnty.push(item);
  //     });
  //     // group.items.forEach(({ name, quantity: pieces }) => {
  //     //   this.calculatedQnty.push({ name, pieces });
  //     // });
  //   });
  //   const result: any = [];
  //   this.calculatedQnty.forEach((item: any) => {
  //     if (![item.name]) {
  //       item.name = { name: item.name, pieces: 0 };
  //       result.push(item.name);
  //     }
  //     item.pieces += item.pieces;
  //   }, Object.create(null));
  //   // result = [];

  //   // arr.forEach(function (a) {
  //   //     if (!this[a.name]) {
  //   //         this[a.name] = { name: a.name, contributions: 0 };
  //   //         result.push(this[a.name]);
  //   //     }
  //   //     this[a.name].contributions += a.contributions;
  //   // }, Object.create(null));

  //   console.log(result);
  //   // this.mergeArr();
  // }

  // mergeArr() {
  //   const sortedQty = [...this.calculatedQnty].sort((a, b) =>
  //     a.name > b.name ? -1 : 1
  //   );
  //   const sortedData = [...this.dataSource].sort((a, b) =>
  //     a.name > b.name ? -1 : 1
  //   );
  //   // console.log(sortedQty);
  //   // console.log(sortedData);
  //   // const newData = this.dataSource.sort().forEach((element: any) => {
  //   //   this.calculatedQnty.sort().forEach((entry: any) => {
  //   //     if (element.name === entry.name) {
  //   //       Object.assign({},  element, entry.pieces)
  //   //       // element.pieces = entry.pieces;
  //   //     }
  //   //   });
  //   // });

  //   // console.log('new data:', newData);
  // }

  // showQuantity(element: any) {}

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

// {
//   "id": "24",
//   "name": "100995",
//   "length": 1270,
//   "width": 3050,
//   "height": 320,
//   "weight": 1105,
//   "quantity": 2,
//   "cargoStyle": 1,
//   "rotatable": false,
//   "stackable": true,
//   "selfStackable": false,
//   "color": "f6dc7a",
//   "maxLayer": 0,
//   "groupId": "13",
//   "groupName": "100995"
// }
