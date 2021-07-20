import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class MenageDrawnImagesService {
  LOAD_PLAN_URL =
    'https://containerdrawingapi-v2.conveyor.cloud/api/drawing/post';

  //   LOAD_PLAN_URL_LOCAL = 'http://localhost:36023/api/drawing/post';
  LOAD_PLAN_URL_LOCAL_JSON = 'http://20.67.96.160:36024/api/Drawing/post';
  constructor(private http: HttpClient) {}

  getLoadPlan(data: any) {
    return this.http
      .post(
        this.LOAD_PLAN_URL_LOCAL_JSON,
        { ...data },
        { observe:  'response'}
      )
      .pipe(
        retry(1)
          );
  }
}
