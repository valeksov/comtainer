import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { AppState } from 'src/app/store/app.state';
import { UploadXlsComponent } from '../upload-xls/upload-xls.component';
import * as LoadActions from '../../store/loadPlan.actions';
@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  constructor(
    private store: Store<AppState>,
    private router: Router,
    public dialog: MatDialog
  ) {}

  ngOnInit() {}

  openDialog() {
    this.dialog.open(UploadXlsComponent);
  }

  onExitClick() {
    this.router.navigateByUrl('login');
  }

  clearCurrentState() {
    this.store.dispatch(new LoadActions.ClearGlobalState());
  }
}
