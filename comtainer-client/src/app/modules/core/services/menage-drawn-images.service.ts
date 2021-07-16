import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class MenageDrawnImagesService {
  LOAD_PLAN_URL =
    'https://containerdrawingapi-v2.conveyor.cloud/api/drawing/post';

  LOAD_PLAN_URL_LOCAL = 'http://localhost:36023/api/drawing/post';
  constructor(private http: HttpClient) {}

  getLoadPlan(data) {
    // return this.http.post(this.LOAD_PLAN_URL, { ...data }, { responseType: 'blob' })
    // .pipe(
    // catchError(this.handleError('getLoadPlan', data))
    // )

return {
  "containers": [
      {
          "id": "40WHC 1",
          "name": "week 44 1",
          "length": 12100,
          "width": 2420,
          "height": 2690,
          "maxAllowedVolume": 78768580000,
          "maxAllowedWeight": 29370,
          "loadPlan": {
              "id": "545396c7-66f5-4663-a576-fcd67b63ebc7",
              "loadPlanSteps": [
                  {
                      "id": "16e6394f-8d99-441d-b6d3-4f670fdb369c",
                      "items": [
                          {
                              "cargo": {
                                  "id": "24",
                                  "name": "100995",
                                  "length": 1270,
                                  "width": 3050,
                                  "height": 320,
                                  "weight": 1105,
                                  "quantity": 2,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "f6dc7a",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 0,
                              "startY": 0,
                              "startZ": 0,
                              "orientation": 2,
                              "length": 3050,
                              "width": 1270,
                              "height": 320,
                              "color": "f6dc7a"
                          }
                      ],
                      "startX": 0,
                      "startY": 0,
                      "startZ": 0,
                      "length": 3050,
                      "width": 1270,
                      "height": 320
                  },
                  {
                      "id": "44332f51-3756-4142-8bf0-4f55c6aa8ecc",
                      "items": [
                          {
                              "cargo": {
                                  "id": "24",
                                  "name": "100995",
                                  "length": 1270,
                                  "width": 3050,
                                  "height": 320,
                                  "weight": 1105,
                                  "quantity": 2,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "f6dc7a",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 0,
                              "startY": 0,
                              "startZ": 320,
                              "orientation": 2,
                              "length": 3050,
                              "width": 1270,
                              "height": 320,
                              "color": "f6dc7a"
                          }
                      ],
                      "startX": 0,
                      "startY": 0,
                      "startZ": 320,
                      "length": 3050,
                      "width": 1270,
                      "height": 320
                  },
                  {
                      "id": "4c5fa83c-d708-49a2-a2d1-637fdc70f469",
                      "items": [
                          {
                              "cargo": {
                                  "id": "21",
                                  "name": "100530",
                                  "length": 800,
                                  "width": 3600,
                                  "height": 600,
                                  "weight": 200,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 0,
                              "startY": 0,
                              "startZ": 640,
                              "orientation": 2,
                              "length": 3600,
                              "width": 800,
                              "height": 600,
                              "color": "7938d6"
                          }
                      ],
                      "startX": 0,
                      "startY": 0,
                      "startZ": 640,
                      "length": 3600,
                      "width": 800,
                      "height": 600
                  },
                  {
                      "id": "1c979171-708d-4e96-90d2-8f680bd3cc60",
                      "items": [
                          {
                              "cargo": {
                                  "id": "14",
                                  "name": "101580",
                                  "length": 1200,
                                  "width": 1200,
                                  "height": 1300,
                                  "weight": 330,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "57f3c5",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 3600,
                              "startY": 0,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 1200,
                              "width": 1200,
                              "height": 1300,
                              "color": "57f3c5"
                          },
                          {
                              "cargo": {
                                  "id": "15",
                                  "name": "101580",
                                  "length": 1100,
                                  "width": 1200,
                                  "height": 1300,
                                  "weight": 270,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "57f3c5",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 3600,
                              "startY": 1200,
                              "startZ": 0,
                              "orientation": 2,
                              "length": 1200,
                              "width": 1100,
                              "height": 1300,
                              "color": "57f3c5"
                          }
                      ],
                      "startX": 3600,
                      "startY": 0,
                      "startZ": 0,
                      "length": 1200,
                      "width": 2300,
                      "height": 1300
                  },
                  {
                      "id": "12f365b5-7b26-4bfd-b4ca-e326a9d23cf3",
                      "items": [
                          {
                              "cargo": {
                                  "id": "17",
                                  "name": "100530",
                                  "length": 1030,
                                  "width": 1070,
                                  "height": 1310,
                                  "weight": 115.47,
                                  "quantity": 8,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 4800,
                              "startY": 0,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 1030,
                              "width": 1070,
                              "height": 1310,
                              "color": "7938d6"
                          },
                          {
                              "cargo": {
                                  "id": "17",
                                  "name": "100530",
                                  "length": 1030,
                                  "width": 1070,
                                  "height": 1310,
                                  "weight": 115.47,
                                  "quantity": 8,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 4800,
                              "startY": 1070,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 1030,
                              "width": 1070,
                              "height": 1310,
                              "color": "7938d6"
                          }
                      ],
                      "startX": 4800,
                      "startY": 0,
                      "startZ": 0,
                      "length": 1030,
                      "width": 2140,
                      "height": 1310
                  },
                  {
                      "id": "18fb25f9-9957-4816-a449-8e590b686200",
                      "items": [
                          {
                              "cargo": {
                                  "id": "17",
                                  "name": "100530",
                                  "length": 1030,
                                  "width": 1070,
                                  "height": 1310,
                                  "weight": 115.47,
                                  "quantity": 8,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 5830,
                              "startY": 0,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 1030,
                              "width": 1070,
                              "height": 1310,
                              "color": "7938d6"
                          },
                          {
                              "cargo": {
                                  "id": "17",
                                  "name": "100530",
                                  "length": 1030,
                                  "width": 1070,
                                  "height": 1310,
                                  "weight": 115.47,
                                  "quantity": 8,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 5830,
                              "startY": 1070,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 1030,
                              "width": 1070,
                              "height": 1310,
                              "color": "7938d6"
                          }
                      ],
                      "startX": 5830,
                      "startY": 0,
                      "startZ": 0,
                      "length": 1030,
                      "width": 2140,
                      "height": 1310
                  },
                  {
                      "id": "7b1e508b-c9da-46a8-afa4-b245f43c40ac",
                      "items": [
                          {
                              "cargo": {
                                  "id": "17",
                                  "name": "100530",
                                  "length": 1030,
                                  "width": 1070,
                                  "height": 1310,
                                  "weight": 115.47,
                                  "quantity": 8,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 6860,
                              "startY": 0,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 1030,
                              "width": 1070,
                              "height": 1310,
                              "color": "7938d6"
                          },
                          {
                              "cargo": {
                                  "id": "17",
                                  "name": "100530",
                                  "length": 1030,
                                  "width": 1070,
                                  "height": 1310,
                                  "weight": 115.47,
                                  "quantity": 8,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 6860,
                              "startY": 1070,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 1030,
                              "width": 1070,
                              "height": 1310,
                              "color": "7938d6"
                          }
                      ],
                      "startX": 6860,
                      "startY": 0,
                      "startZ": 0,
                      "length": 1030,
                      "width": 2140,
                      "height": 1310
                  },
                  {
                      "id": "bb3a94e9-fd65-441e-a409-67d01d05ae5e",
                      "items": [
                          {
                              "cargo": {
                                  "id": "17",
                                  "name": "100530",
                                  "length": 1030,
                                  "width": 1070,
                                  "height": 1310,
                                  "weight": 115.47,
                                  "quantity": 8,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 7890,
                              "startY": 0,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 1030,
                              "width": 1070,
                              "height": 1310,
                              "color": "7938d6"
                          },
                          {
                              "cargo": {
                                  "id": "17",
                                  "name": "100530",
                                  "length": 1030,
                                  "width": 1070,
                                  "height": 1310,
                                  "weight": 115.47,
                                  "quantity": 8,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 7890,
                              "startY": 1070,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 1030,
                              "width": 1070,
                              "height": 1310,
                              "color": "7938d6"
                          }
                      ],
                      "startX": 7890,
                      "startY": 0,
                      "startZ": 0,
                      "length": 1030,
                      "width": 2140,
                      "height": 1310
                  },
                  {
                      "id": "821f1049-566e-43f0-abb0-166e4e2390eb",
                      "items": [
                          {
                              "cargo": {
                                  "id": "13",
                                  "name": "101524",
                                  "length": 850,
                                  "width": 850,
                                  "height": 2140,
                                  "weight": 206,
                                  "quantity": 3,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "b97fcf",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 8920,
                              "startY": 0,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 850,
                              "width": 850,
                              "height": 2140,
                              "color": "b97fcf"
                          },
                          {
                              "cargo": {
                                  "id": "13",
                                  "name": "101524",
                                  "length": 850,
                                  "width": 850,
                                  "height": 2140,
                                  "weight": 206,
                                  "quantity": 3,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "b97fcf",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 8920,
                              "startY": 850,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 850,
                              "width": 850,
                              "height": 2140,
                              "color": "b97fcf"
                          }
                      ],
                      "startX": 8920,
                      "startY": 0,
                      "startZ": 0,
                      "length": 850,
                      "width": 1700,
                      "height": 2140
                  },
                  {
                      "id": "8ba9893b-66bd-47f5-8b1a-c210fdcc6956",
                      "items": [
                          {
                              "cargo": {
                                  "id": "8",
                                  "name": "101444",
                                  "length": 800,
                                  "width": 1200,
                                  "height": 700,
                                  "weight": 1700,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "a9adae",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 9770,
                              "startY": 0,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 800,
                              "width": 1200,
                              "height": 700,
                              "color": "a9adae"
                          },
                          {
                              "cargo": {
                                  "id": "22",
                                  "name": "100530",
                                  "length": 800,
                                  "width": 600,
                                  "height": 700,
                                  "weight": 50,
                                  "quantity": 1,
                                  "cargoStyle": 0,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 9770,
                              "startY": 1200,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 800,
                              "width": 600,
                              "height": 700,
                              "color": "7938d6"
                          }
                      ],
                      "startX": 9770,
                      "startY": 0,
                      "startZ": 0,
                      "length": 800,
                      "width": 1800,
                      "height": 700
                  },
                  {
                      "id": "a321c055-c014-4130-9de1-b1c6a845fcda",
                      "items": [
                          {
                              "cargo": {
                                  "id": "19",
                                  "name": "100530",
                                  "length": 800,
                                  "width": 1200,
                                  "height": 600,
                                  "weight": 100,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 10570,
                              "startY": 0,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 800,
                              "width": 1200,
                              "height": 600,
                              "color": "7938d6"
                          }
                      ],
                      "startX": 10570,
                      "startY": 0,
                      "startZ": 0,
                      "length": 800,
                      "width": 1200,
                      "height": 600
                  },
                  {
                      "id": "72ff715a-4b7b-4be7-bcff-fe3edb4ee94b",
                      "items": [
                          {
                              "cargo": {
                                  "id": "7",
                                  "name": "101444",
                                  "length": 800,
                                  "width": 1200,
                                  "height": 500,
                                  "weight": 2000,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "a9adae",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 10570,
                              "startY": 1200,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 800,
                              "width": 1200,
                              "height": 500,
                              "color": "a9adae"
                          }
                      ],
                      "startX": 10570,
                      "startY": 1200,
                      "startZ": 0,
                      "length": 800,
                      "width": 1200,
                      "height": 500
                  },
                  {
                      "id": "a81f29d1-7046-4041-8259-71997caeb92e",
                      "items": [
                          {
                              "cargo": {
                                  "id": "18",
                                  "name": "100530",
                                  "length": 800,
                                  "width": 1200,
                                  "height": 2100,
                                  "weight": 200,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": true,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 0,
                              "startY": 1270,
                              "startZ": 0,
                              "orientation": 2,
                              "length": 1200,
                              "width": 800,
                              "height": 2100,
                              "color": "7938d6"
                          }
                      ],
                      "startX": 0,
                      "startY": 1270,
                      "startZ": 0,
                      "length": 1200,
                      "width": 800,
                      "height": 2100
                  },
                  {
                      "id": "47f744ce-94e5-4414-8d0e-97f0d00f9512",
                      "items": [
                          {
                              "cargo": {
                                  "id": "3",
                                  "name": "101465",
                                  "length": 800,
                                  "width": 1200,
                                  "height": 560,
                                  "weight": 170,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "7aa8d5",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 1200,
                              "startY": 1270,
                              "startZ": 0,
                              "orientation": 2,
                              "length": 1200,
                              "width": 800,
                              "height": 560,
                              "color": "7aa8d5"
                          }
                      ],
                      "startX": 1200,
                      "startY": 1270,
                      "startZ": 0,
                      "length": 1200,
                      "width": 800,
                      "height": 560
                  },
                  {
                      "id": "1ea01f9d-4271-4365-ab82-3cf2eba5db69",
                      "items": [
                          {
                              "cargo": {
                                  "id": "13",
                                  "name": "101524",
                                  "length": 850,
                                  "width": 850,
                                  "height": 2140,
                                  "weight": 206,
                                  "quantity": 3,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "b97fcf",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 2400,
                              "startY": 1270,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 850,
                              "width": 850,
                              "height": 2140,
                              "color": "b97fcf"
                          }
                      ],
                      "startX": 2400,
                      "startY": 1270,
                      "startZ": 0,
                      "length": 850,
                      "width": 850,
                      "height": 2140
                  },
                  {
                      "id": "55373202-8345-4ce2-bbc5-02272eabe739",
                      "items": [
                          {
                              "cargo": {
                                  "id": "20",
                                  "name": "100530",
                                  "length": 400,
                                  "width": 400,
                                  "height": 340,
                                  "weight": 40.51,
                                  "quantity": 1,
                                  "cargoStyle": 0,
                                  "rotatable": false,
                                  "stackable": true,
                                  "selfStackable": false,
                                  "color": "7938d6",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 0,
                              "startY": 800,
                              "startZ": 640,
                              "orientation": 1,
                              "length": 400,
                              "width": 400,
                              "height": 340,
                              "color": "7938d6"
                          }
                      ],
                      "startX": 0,
                      "startY": 800,
                      "startZ": 640,
                      "length": 400,
                      "width": 400,
                      "height": 340
                  },
                  {
                      "id": "e8738bda-ef24-4489-90d2-0aa8de6e1f7f",
                      "items": [
                          {
                              "cargo": {
                                  "id": "23",
                                  "name": "101533",
                                  "length": 2180,
                                  "width": 1180,
                                  "height": 110,
                                  "weight": 68,
                                  "quantity": 2,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": true,
                                  "color": "d7f4b2",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 3600,
                              "startY": 0,
                              "startZ": 1300,
                              "orientation": 2,
                              "length": 1180,
                              "width": 2180,
                              "height": 110,
                              "color": "d7f4b2"
                          }
                      ],
                      "startX": 3600,
                      "startY": 0,
                      "startZ": 1300,
                      "length": 1180,
                      "width": 2180,
                      "height": 110
                  },
                  {
                      "id": "89cc7602-ca77-4b12-8461-cc7837dd62c6",
                      "items": [
                          {
                              "cargo": {
                                  "id": "23",
                                  "name": "101533",
                                  "length": 2180,
                                  "width": 1180,
                                  "height": 110,
                                  "weight": 68,
                                  "quantity": 2,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": true,
                                  "color": "d7f4b2",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 3600,
                              "startY": 0,
                              "startZ": 1410,
                              "orientation": 2,
                              "length": 1180,
                              "width": 2180,
                              "height": 110,
                              "color": "d7f4b2"
                          }
                      ],
                      "startX": 3600,
                      "startY": 0,
                      "startZ": 1410,
                      "length": 1180,
                      "width": 2180,
                      "height": 110
                  }
              ],
              "items": [
                  {
                      "id": "24",
                      "name": "100995",
                      "length": 1270,
                      "width": 3050,
                      "height": 320,
                      "weight": 1105,
                      "quantity": 2,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "f6dc7a",
                      "maxLayer": 0,
                      "groupId": "13",
                      "groupName": "100995"
                  },
                  {
                      "id": "21",
                      "name": "100530",
                      "length": 800,
                      "width": 3600,
                      "height": 600,
                      "weight": 200,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "7938d6",
                      "maxLayer": 0,
                      "groupId": "11",
                      "groupName": "100530"
                  },
                  {
                      "id": "14",
                      "name": "101580",
                      "length": 1200,
                      "width": 1200,
                      "height": 1300,
                      "weight": 330,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "57f3c5",
                      "maxLayer": 0,
                      "groupId": "9",
                      "groupName": "101580"
                  },
                  {
                      "id": "15",
                      "name": "101580",
                      "length": 1100,
                      "width": 1200,
                      "height": 1300,
                      "weight": 270,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "57f3c5",
                      "maxLayer": 0,
                      "groupId": "9",
                      "groupName": "101580"
                  },
                  {
                      "id": "17",
                      "name": "100530",
                      "length": 1030,
                      "width": 1070,
                      "height": 1310,
                      "weight": 115.47,
                      "quantity": 8,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "7938d6",
                      "maxLayer": 0,
                      "groupId": "11",
                      "groupName": "100530"
                  },
                  {
                      "id": "13",
                      "name": "101524",
                      "length": 850,
                      "width": 850,
                      "height": 2140,
                      "weight": 206,
                      "quantity": 3,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "b97fcf",
                      "maxLayer": 0,
                      "groupId": "8",
                      "groupName": "101524"
                  },
                  {
                      "id": "8",
                      "name": "101444",
                      "length": 800,
                      "width": 1200,
                      "height": 700,
                      "weight": 1700,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "a9adae",
                      "maxLayer": 0,
                      "groupId": "4",
                      "groupName": "101444"
                  },
                  {
                      "id": "22",
                      "name": "100530",
                      "length": 800,
                      "width": 600,
                      "height": 700,
                      "weight": 50,
                      "quantity": 1,
                      "cargoStyle": 0,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "7938d6",
                      "maxLayer": 0,
                      "groupId": "11",
                      "groupName": "100530"
                  },
                  {
                      "id": "19",
                      "name": "100530",
                      "length": 800,
                      "width": 1200,
                      "height": 600,
                      "weight": 100,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "7938d6",
                      "maxLayer": 0,
                      "groupId": "11",
                      "groupName": "100530"
                  },
                  {
                      "id": "7",
                      "name": "101444",
                      "length": 800,
                      "width": 1200,
                      "height": 500,
                      "weight": 2000,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "a9adae",
                      "maxLayer": 0,
                      "groupId": "4",
                      "groupName": "101444"
                  },
                  {
                      "id": "18",
                      "name": "100530",
                      "length": 800,
                      "width": 1200,
                      "height": 2100,
                      "weight": 200,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": true,
                      "color": "7938d6",
                      "maxLayer": 0,
                      "groupId": "11",
                      "groupName": "100530"
                  },
                  {
                      "id": "3",
                      "name": "101465",
                      "length": 800,
                      "width": 1200,
                      "height": 560,
                      "weight": 170,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "7aa8d5",
                      "maxLayer": 0,
                      "groupId": "2",
                      "groupName": "101465"
                  },
                  {
                      "id": "20",
                      "name": "100530",
                      "length": 400,
                      "width": 400,
                      "height": 340,
                      "weight": 40.51,
                      "quantity": 1,
                      "cargoStyle": 0,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "7938d6",
                      "maxLayer": 0,
                      "groupId": "11",
                      "groupName": "100530"
                  },
                  {
                      "id": "23",
                      "name": "101533",
                      "length": 2180,
                      "width": 1180,
                      "height": 110,
                      "weight": 68,
                      "quantity": 2,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": true,
                      "color": "d7f4b2",
                      "maxLayer": 0,
                      "groupId": "12",
                      "groupName": "101533"
                  }
              ],
              "stats": [
                  {
                      "name": "Length (mm)",
                      "used": 11370,
                      "max": 12100,
                      "free": 730,
                      "usedInPercent": 93.9669418334961,
                      "freeInPercent": 6.033058166503906
                  },
                  {
                      "name": "Width (mm)",
                      "used": 2400,
                      "max": 2420,
                      "free": 20,
                      "usedInPercent": 99.17355346679688,
                      "freeInPercent": 0.826446533203125
                  },
                  {
                      "name": "Height (mm)",
                      "used": 2140,
                      "max": 2690,
                      "free": 550,
                      "usedInPercent": 79.55390167236328,
                      "freeInPercent": 20.44609832763672
                  },
                  {
                      "name": "Volume (mm3)",
                      "used": 29221426000,
                      "max": 78768580000,
                      "free": 49547154000,
                      "usedInPercent": 37.09782028198242,
                      "freeInPercent": 62.90217971801758
                  },
                  {
                      "name": "Floor Area (mm2)",
                      "used": 22897800,
                      "max": 29282000,
                      "free": 6384200,
                      "usedInPercent": 78.19752502441406,
                      "freeInPercent": 21.802474975585938
                  },
                  {
                      "name": "Weight (kg)",
                      "used": 8948.26953125,
                      "max": 29370,
                      "free": 20421.73046875,
                      "usedInPercent": 30.467378616333008,
                      "freeInPercent": 69.53262138366699
                  },
                  {
                      "name": "Cargoes",
                      "used": 25,
                      "max": null,
                      "free": null,
                      "usedInPercent": null,
                      "freeInPercent": null
                  }
              ]
          }
      },
      {
          "id": "40WHC 2",
          "name": "week 44 2",
          "length": 12100,
          "width": 2420,
          "height": 2690,
          "maxAllowedVolume": 78768580000,
          "maxAllowedWeight": 29370,
          "loadPlan": {
              "id": "b5200610-9e64-451d-baa8-f317742cf2a8",
              "loadPlanSteps": [
                  {
                      "id": "e5eb219d-9d22-40d0-904d-dd270b3ec01a",
                      "items": [
                          {
                              "cargo": {
                                  "id": "26",
                                  "name": "101007",
                                  "length": 1000,
                                  "width": 1200,
                                  "height": 600,
                                  "weight": 84,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "b9374e",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 0,
                              "startY": 0,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 1000,
                              "width": 1200,
                              "height": 600,
                              "color": "b9374e"
                          }
                      ],
                      "startX": 0,
                      "startY": 0,
                      "startZ": 0,
                      "length": 1000,
                      "width": 1200,
                      "height": 600
                  },
                  {
                      "id": "af7acb0f-04ee-4a99-b495-22081b533577",
                      "items": [
                          {
                              "cargo": {
                                  "id": "25",
                                  "name": "101006",
                                  "length": 800,
                                  "width": 1200,
                                  "height": 650,
                                  "weight": 95,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "f9b4d1",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 0,
                              "startY": 1200,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 800,
                              "width": 1200,
                              "height": 650,
                              "color": "f9b4d1"
                          }
                      ],
                      "startX": 0,
                      "startY": 1200,
                      "startZ": 0,
                      "length": 800,
                      "width": 1200,
                      "height": 650
                  },
                  {
                      "id": "88c8bc85-7384-4c81-8a38-f3ab4a979743",
                      "items": [
                          {
                              "cargo": {
                                  "id": "9",
                                  "name": "101525",
                                  "length": 800,
                                  "width": 1200,
                                  "height": 1560,
                                  "weight": 180,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "f6c41b",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 800,
                              "startY": 1200,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 800,
                              "width": 1200,
                              "height": 1560,
                              "color": "f6c41b"
                          }
                      ],
                      "startX": 800,
                      "startY": 1200,
                      "startZ": 0,
                      "length": 800,
                      "width": 1200,
                      "height": 1560
                  },
                  {
                      "id": "33c6e9ca-2990-41c3-9665-1e37766050f1",
                      "items": [
                          {
                              "cargo": {
                                  "id": "10",
                                  "name": "101511",
                                  "length": 800,
                                  "width": 1200,
                                  "height": 500,
                                  "weight": 223,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "e4467d",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 1600,
                              "startY": 1200,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 800,
                              "width": 1200,
                              "height": 500,
                              "color": "e4467d"
                          }
                      ],
                      "startX": 1600,
                      "startY": 1200,
                      "startZ": 0,
                      "length": 800,
                      "width": 1200,
                      "height": 500
                  },
                  {
                      "id": "94509df1-c5f2-4197-9f7a-c62e073143f6",
                      "items": [
                          {
                              "cargo": {
                                  "id": "16",
                                  "name": "101525",
                                  "length": 800,
                                  "width": 1200,
                                  "height": 960,
                                  "weight": 98,
                                  "quantity": 1,
                                  "cargoStyle": 1,
                                  "rotatable": false,
                                  "stackable": false,
                                  "selfStackable": false,
                                  "color": "292858",
                                  "maxLayer": 0,
                                  "groupId": null,
                                  "groupName": null
                              },
                              "startX": 2400,
                              "startY": 1200,
                              "startZ": 0,
                              "orientation": 1,
                              "length": 800,
                              "width": 1200,
                              "height": 960,
                              "color": "292858"
                          }
                      ],
                      "startX": 2400,
                      "startY": 1200,
                      "startZ": 0,
                      "length": 800,
                      "width": 1200,
                      "height": 960
                  }
              ],
              "items": [
                  {
                      "id": "26",
                      "name": "101007",
                      "length": 1000,
                      "width": 1200,
                      "height": 600,
                      "weight": 84,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "b9374e",
                      "maxLayer": 0,
                      "groupId": "15",
                      "groupName": "101007"
                  },
                  {
                      "id": "25",
                      "name": "101006",
                      "length": 800,
                      "width": 1200,
                      "height": 650,
                      "weight": 95,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "f9b4d1",
                      "maxLayer": 0,
                      "groupId": "14",
                      "groupName": "101006"
                  },
                  {
                      "id": "9",
                      "name": "101525",
                      "length": 800,
                      "width": 1200,
                      "height": 1560,
                      "weight": 180,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "f6c41b",
                      "maxLayer": 0,
                      "groupId": "5",
                      "groupName": "101525"
                  },
                  {
                      "id": "10",
                      "name": "101511",
                      "length": 800,
                      "width": 1200,
                      "height": 500,
                      "weight": 223,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "e4467d",
                      "maxLayer": 0,
                      "groupId": "6",
                      "groupName": "101511"
                  },
                  {
                      "id": "16",
                      "name": "101525",
                      "length": 800,
                      "width": 1200,
                      "height": 960,
                      "weight": 98,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "292858",
                      "maxLayer": 0,
                      "groupId": "10",
                      "groupName": "101525"
                  }
              ],
              "stats": [
                  {
                      "name": "Length (mm)",
                      "used": 3200,
                      "max": 12100,
                      "free": 8900,
                      "usedInPercent": 26.44628143310547,
                      "freeInPercent": 73.55371856689453
                  },
                  {
                      "name": "Width (mm)",
                      "used": 2400,
                      "max": 2420,
                      "free": 20,
                      "usedInPercent": 99.17355346679688,
                      "freeInPercent": 0.826446533203125
                  },
                  {
                      "name": "Height (mm)",
                      "used": 1560,
                      "max": 2690,
                      "free": 1130,
                      "usedInPercent": 57.9925651550293,
                      "freeInPercent": 42.0074348449707
                  },
                  {
                      "name": "Volume (mm3)",
                      "used": 4243200000,
                      "max": 78768580000,
                      "free": 74525380000,
                      "usedInPercent": 5.3869194984436035,
                      "freeInPercent": 94.6130805015564
                  },
                  {
                      "name": "Floor Area (mm2)",
                      "used": 5040000,
                      "max": 29282000,
                      "free": 24242000,
                      "usedInPercent": 17.211938858032227,
                      "freeInPercent": 82.78806114196777
                  },
                  {
                      "name": "Weight (kg)",
                      "used": 680,
                      "max": 29370,
                      "free": 28690,
                      "usedInPercent": 2.3152875900268555,
                      "freeInPercent": 97.68471240997314
                  },
                  {
                      "name": "Cargoes",
                      "used": 5,
                      "max": null,
                      "free": null,
                      "usedInPercent": null,
                      "freeInPercent": null
                  }
              ]
          }
      },
      {
          "id": "Not Placed",
          "name": "Not Placed",
          "length": null,
          "width": null,
          "height": null,
          "maxAllowedVolume": null,
          "maxAllowedWeight": null,
          "loadPlan": {
              "id": null,
              "loadPlanSteps": null,
              "items": [
                  {
                      "id": "1",
                      "name": "101291",
                      "length": 1100,
                      "width": 1300,
                      "height": 12200,
                      "weight": 170,
                      "quantity": 2,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "20af1a",
                      "maxLayer": 0,
                      "groupId": "1",
                      "groupName": "101291"
                  },
                  {
                      "id": "2",
                      "name": "101291",
                      "length": 800,
                      "width": 1200,
                      "height": 2200,
                      "weight": 130,
                      "quantity": 2,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "20af1a",
                      "maxLayer": 0,
                      "groupId": "1",
                      "groupName": "101291"
                  },
                  {
                      "id": "11",
                      "name": "101297",
                      "length": 800,
                      "width": 1200,
                      "height": 1300,
                      "weight": 480,
                      "quantity": 3,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "af158d",
                      "maxLayer": 0,
                      "groupId": "7",
                      "groupName": "101297"
                  },
                  {
                      "id": "12",
                      "name": "101297",
                      "length": 800,
                      "width": 1200,
                      "height": 1600,
                      "weight": 600,
                      "quantity": 3,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": false,
                      "selfStackable": false,
                      "color": "af158d",
                      "maxLayer": 0,
                      "groupId": "7",
                      "groupName": "101297"
                  },
                  {
                      "id": "4",
                      "name": "101158",
                      "length": 950,
                      "width": 1250,
                      "height": 3400,
                      "weight": 25,
                      "quantity": 4,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "75c273",
                      "maxLayer": 0,
                      "groupId": "3",
                      "groupName": "101158"
                  },
                  {
                      "id": "5",
                      "name": "101158",
                      "length": 900,
                      "width": 1200,
                      "height": 2400,
                      "weight": 24,
                      "quantity": 3,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "75c273",
                      "maxLayer": 0,
                      "groupId": "3",
                      "groupName": "101158"
                  },
                  {
                      "id": "6",
                      "name": "101158",
                      "length": 900,
                      "width": 1200,
                      "height": 2400,
                      "weight": 24,
                      "quantity": 1,
                      "cargoStyle": 1,
                      "rotatable": false,
                      "stackable": true,
                      "selfStackable": false,
                      "color": "75c273",
                      "maxLayer": 0,
                      "groupId": "3",
                      "groupName": "101158"
                  }
              ],
              "stats": [
                  {
                      "name": "Cargoes",
                      "used": 18,
                      "max": null,
                      "free": null,
                      "usedInPercent": null,
                      "freeInPercent": null
                  }
              ]
          }
      }
  ],
  "config": {
      "cargoSupport": 75,
      "lightUnstackableWeightLimit": 105,
      "allowHeavierCargoOnTop": false,
      "maxHeavierCargoOnTop": 0,
      "keepGroupsTogether": true,
      "maxWeightDiffInPercent": 0,
      "maxWeightDiffInKilos": 0
  },
  "groups": [
      {
          "id": "3",
          "name": "101158",
          "items": [
              {
                  "id": "4",
                  "name": "101158",
                  "length": 950,
                  "width": 1250,
                  "height": 3400,
                  "weight": 25,
                  "quantity": 4,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": true,
                  "selfStackable": false,
                  "color": "75c273",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              },
              {
                  "id": "5",
                  "name": "101158",
                  "length": 900,
                  "width": 1200,
                  "height": 2400,
                  "weight": 24,
                  "quantity": 3,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": true,
                  "selfStackable": false,
                  "color": "75c273",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              },
              {
                  "id": "6",
                  "name": "101158",
                  "length": 900,
                  "width": 1200,
                  "height": 2400,
                  "weight": 24,
                  "quantity": 1,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": true,
                  "selfStackable": false,
                  "color": "75c273",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              }
          ],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": false,
          "groups": null
      },
      {
          "id": "5",
          "name": "101525",
          "items": [
              {
                  "id": "9",
                  "name": "101525",
                  "length": 800,
                  "width": 1200,
                  "height": 1560,
                  "weight": 180,
                  "quantity": 1,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": false,
                  "selfStackable": false,
                  "color": "f6c41b",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              }
          ],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": true,
          "groups": null
      },
      {
          "id": "6",
          "name": "101511",
          "items": [
              {
                  "id": "10",
                  "name": "101511",
                  "length": 800,
                  "width": 1200,
                  "height": 500,
                  "weight": 223,
                  "quantity": 1,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": false,
                  "selfStackable": false,
                  "color": "e4467d",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              }
          ],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": true,
          "groups": null
      },
      {
          "id": "8",
          "name": "101524",
          "items": [
              {
                  "id": "13",
                  "name": "101524",
                  "length": 850,
                  "width": 850,
                  "height": 2140,
                  "weight": 206,
                  "quantity": 3,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": false,
                  "selfStackable": false,
                  "color": "b97fcf",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              }
          ],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": true,
          "groups": null
      },
      {
          "id": "9",
          "name": "101580",
          "items": [
              {
                  "id": "14",
                  "name": "101580",
                  "length": 1200,
                  "width": 1200,
                  "height": 1300,
                  "weight": 330,
                  "quantity": 1,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": true,
                  "selfStackable": false,
                  "color": "57f3c5",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              },
              {
                  "id": "15",
                  "name": "101580",
                  "length": 1100,
                  "width": 1200,
                  "height": 1300,
                  "weight": 270,
                  "quantity": 1,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": false,
                  "selfStackable": false,
                  "color": "57f3c5",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              }
          ],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": true,
          "groups": null
      },
      {
          "id": "10",
          "name": "101525",
          "items": [
              {
                  "id": "16",
                  "name": "101525",
                  "length": 800,
                  "width": 1200,
                  "height": 960,
                  "weight": 98,
                  "quantity": 1,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": false,
                  "selfStackable": false,
                  "color": "292858",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              }
          ],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": true,
          "groups": null
      },
      {
          "id": "11",
          "name": "100530",
          "items": [
              {
                  "id": "17",
                  "name": "100530",
                  "length": 1030,
                  "width": 1070,
                  "height": 1310,
                  "weight": 115.47,
                  "quantity": 8,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": true,
                  "selfStackable": false,
                  "color": "7938d6",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              },
              {
                  "id": "18",
                  "name": "100530",
                  "length": 800,
                  "width": 1200,
                  "height": 2100,
                  "weight": 200,
                  "quantity": 1,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": true,
                  "selfStackable": true,
                  "color": "7938d6",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              },
              {
                  "id": "19",
                  "name": "100530",
                  "length": 800,
                  "width": 1200,
                  "height": 600,
                  "weight": 100,
                  "quantity": 1,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": true,
                  "selfStackable": false,
                  "color": "7938d6",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              },
              {
                  "id": "20",
                  "name": "100530",
                  "length": 400,
                  "width": 400,
                  "height": 340,
                  "weight": 40.51,
                  "quantity": 1,
                  "cargoStyle": 0,
                  "rotatable": false,
                  "stackable": true,
                  "selfStackable": false,
                  "color": "7938d6",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              },
              {
                  "id": "21",
                  "name": "100530",
                  "length": 800,
                  "width": 3600,
                  "height": 600,
                  "weight": 200,
                  "quantity": 1,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": true,
                  "selfStackable": false,
                  "color": "7938d6",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              },
              {
                  "id": "22",
                  "name": "100530",
                  "length": 800,
                  "width": 600,
                  "height": 700,
                  "weight": 50,
                  "quantity": 1,
                  "cargoStyle": 0,
                  "rotatable": false,
                  "stackable": true,
                  "selfStackable": false,
                  "color": "7938d6",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              }
          ],
          "color": null,
          "stackGroupOnly": true,
          "alreadyLoaded": true,
          "groups": null
      },
      {
          "id": "12",
          "name": "101533",
          "items": [
              {
                  "id": "23",
                  "name": "101533",
                  "length": 2180,
                  "width": 1180,
                  "height": 110,
                  "weight": 68,
                  "quantity": 2,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": false,
                  "selfStackable": true,
                  "color": "d7f4b2",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              }
          ],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": true,
          "groups": null
      },
      {
          "id": "13",
          "name": "100995",
          "items": [
              {
                  "id": "24",
                  "name": "100995",
                  "length": 1270,
                  "width": 3050,
                  "height": 320,
                  "weight": 1105,
                  "quantity": 2,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": true,
                  "selfStackable": false,
                  "color": "f6dc7a",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              }
          ],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": true,
          "groups": null
      },
      {
          "id": "14",
          "name": "101006",
          "items": [
              {
                  "id": "25",
                  "name": "101006",
                  "length": 800,
                  "width": 1200,
                  "height": 650,
                  "weight": 95,
                  "quantity": 1,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": false,
                  "selfStackable": false,
                  "color": "f9b4d1",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              }
          ],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": true,
          "groups": null
      },
      {
          "id": "15",
          "name": "101007",
          "items": [
              {
                  "id": "26",
                  "name": "101007",
                  "length": 1000,
                  "width": 1200,
                  "height": 600,
                  "weight": 84,
                  "quantity": 1,
                  "cargoStyle": 1,
                  "rotatable": false,
                  "stackable": false,
                  "selfStackable": false,
                  "color": "b9374e",
                  "maxLayer": 0,
                  "groupId": null,
                  "groupName": null
              }
          ],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": true,
          "groups": null
      },
      {
          "id": "C1",
          "name": "C1",
          "items": [],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": false,
          "groups": [
              {
                  "id": "1",
                  "name": "101291",
                  "items": [
                      {
                          "id": "1",
                          "name": "101291",
                          "length": 1100,
                          "width": 1300,
                          "height": 12200,
                          "weight": 170,
                          "quantity": 2,
                          "cargoStyle": 1,
                          "rotatable": false,
                          "stackable": true,
                          "selfStackable": false,
                          "color": "20af1a",
                          "maxLayer": 0,
                          "groupId": null,
                          "groupName": null
                      },
                      {
                          "id": "2",
                          "name": "101291",
                          "length": 800,
                          "width": 1200,
                          "height": 2200,
                          "weight": 130,
                          "quantity": 2,
                          "cargoStyle": 1,
                          "rotatable": false,
                          "stackable": true,
                          "selfStackable": false,
                          "color": "20af1a",
                          "maxLayer": 0,
                          "groupId": null,
                          "groupName": null
                      }
                  ],
                  "color": null,
                  "stackGroupOnly": false,
                  "alreadyLoaded": false,
                  "groups": null
              },
              {
                  "id": "7",
                  "name": "101297",
                  "items": [
                      {
                          "id": "11",
                          "name": "101297",
                          "length": 800,
                          "width": 1200,
                          "height": 1300,
                          "weight": 480,
                          "quantity": 3,
                          "cargoStyle": 1,
                          "rotatable": false,
                          "stackable": false,
                          "selfStackable": false,
                          "color": "af158d",
                          "maxLayer": 0,
                          "groupId": null,
                          "groupName": null
                      },
                      {
                          "id": "12",
                          "name": "101297",
                          "length": 800,
                          "width": 1200,
                          "height": 1600,
                          "weight": 600,
                          "quantity": 3,
                          "cargoStyle": 1,
                          "rotatable": false,
                          "stackable": false,
                          "selfStackable": false,
                          "color": "af158d",
                          "maxLayer": 0,
                          "groupId": null,
                          "groupName": null
                      }
                  ],
                  "color": null,
                  "stackGroupOnly": false,
                  "alreadyLoaded": false,
                  "groups": null
              }
          ]
      },
      {
          "id": "C2",
          "name": "C2",
          "items": [],
          "color": null,
          "stackGroupOnly": false,
          "alreadyLoaded": true,
          "groups": [
              {
                  "id": "2",
                  "name": "101465",
                  "items": [
                      {
                          "id": "3",
                          "name": "101465",
                          "length": 800,
                          "width": 1200,
                          "height": 560,
                          "weight": 170,
                          "quantity": 1,
                          "cargoStyle": 1,
                          "rotatable": false,
                          "stackable": false,
                          "selfStackable": false,
                          "color": "7aa8d5",
                          "maxLayer": 0,
                          "groupId": null,
                          "groupName": null
                      }
                  ],
                  "color": null,
                  "stackGroupOnly": false,
                  "alreadyLoaded": false,
                  "groups": null
              },
              {
                  "id": "4",
                  "name": "101444",
                  "items": [
                      {
                          "id": "7",
                          "name": "101444",
                          "length": 800,
                          "width": 1200,
                          "height": 500,
                          "weight": 2000,
                          "quantity": 1,
                          "cargoStyle": 1,
                          "rotatable": false,
                          "stackable": false,
                          "selfStackable": false,
                          "color": "a9adae",
                          "maxLayer": 0,
                          "groupId": null,
                          "groupName": null
                      },
                      {
                          "id": "8",
                          "name": "101444",
                          "length": 800,
                          "width": 1200,
                          "height": 700,
                          "weight": 1700,
                          "quantity": 1,
                          "cargoStyle": 1,
                          "rotatable": false,
                          "stackable": false,
                          "selfStackable": false,
                          "color": "a9adae",
                          "maxLayer": 0,
                          "groupId": null,
                          "groupName": null
                      }
                  ],
                  "color": null,
                  "stackGroupOnly": false,
                  "alreadyLoaded": false,
                  "groups": null
              }
          ]
      }
  ],
  "status": 1
}
  }
}
