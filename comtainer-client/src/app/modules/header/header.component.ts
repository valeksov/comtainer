import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { UploadXlsComponent } from '../upload-xls/upload-xls.component';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  constructor(public dialog: MatDialog, private router: Router) {}

  ngOnInit() {}

  openDialog() {
    this.dialog.open(UploadXlsComponent);
  }

  onExitClick() {
    this.router.navigateByUrl('login');
  }
}
