import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { HomeComponent } from './modules/home/home/home.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './modules/shared/material';
import { ContainersTableComponent } from './modules/home/containers-table/containers-table.component';
import { HttpClientModule } from '@angular/common/http';
import { LoginComponent } from './modules/login/login.component';
import { AppRoutingModule } from './app-routing.module';
import { ReactiveFormsModule } from '@angular/forms';
import { HeaderComponent } from './modules/header/header.component';
import { ResultsTabComponent } from './modules/home/results-tab/results-tab.component';
import { DetailsLoadListComponent } from './modules/home/results-tab/details-load-list/details-load-list.component';
import { DetailsSummaryComponent } from './modules/home/results-tab/details-summary/details-summary.component';
import { UploadXlsComponent } from './modules/upload-xls/upload-xls.component';

import { StoreModule } from '@ngrx/store';
import { loadPlanReducer } from './store/loadPlan.reducer';
import { LoadBlocksComponent } from './modules/home/results-tab/load-blocks/load-blocks.component';
import { SafePipe } from './modules/shared/safe.pipe';
import { ToastrModule } from 'ngx-toastr';
import { DecimalPipe } from '@angular/common';

@NgModule({
  declarations: [
    SafePipe,
    AppComponent,
    HomeComponent,
    ContainersTableComponent,
    LoginComponent,
    HeaderComponent,
    ResultsTabComponent,
    DetailsLoadListComponent,
    DetailsSummaryComponent,
    UploadXlsComponent,
    LoadBlocksComponent,
  ],
  imports: [
    MaterialModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    ReactiveFormsModule,
    StoreModule.forRoot({
      loadPlan: loadPlanReducer,
    }),
    ToastrModule.forRoot({
      timeOut: 3000,
      positionClass: 'toast-top-center',
      preventDuplicates: true
    }),
  ],
  providers: [DecimalPipe],
  bootstrap: [AppComponent],
  entryComponents: [UploadXlsComponent],
})
export class AppModule {}
