import { Component, Input, OnInit } from '@angular/core';
// import * as XLSX from 'xlsx';
// import { Sheet, WorkBook, WorkSheet } from 'xlsx';
// import {
//   AliasGroup,
//   Config,
//   Container,
//   ConvertedXlsDto,
//   Group,
//   Item,
//   RegularGroup,
//   SheetOptions,
// } from '../../generate-json/generate-json.types';
// import {
//   getConvertedRowValue,
//   getTransformedCargoData,
// } from '../../generate-json/generate-json.utils';

@Component({
  selector: 'app-generate-json',
  templateUrl: './generate-json.component.html',
  styleUrls: ['./generate-json.component.css']
})


export class GenerateJsonComponent implements OnInit {
  // @Input() convertedJSON: ConvertedXlsDto;

  constructor() { }

  ngOnInit() {
  }

}

// type GenericAliasGroupObject = { [key: string]: Array<string | number> };

// const INITIAL_JSON: ConvertedXlsDto = {
//   containers: [],
//   config: null,
//   groups: [],
// };