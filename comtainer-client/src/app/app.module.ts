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

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    ContainersTableComponent,
    LoginComponent,
    HeaderComponent,
    ResultsTabComponent,
    DetailsLoadListComponent,
    DetailsSummaryComponent,
    UploadXlsComponent
  ],
  imports: [
    MaterialModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [UploadXlsComponent]
})
export class AppModule {}
