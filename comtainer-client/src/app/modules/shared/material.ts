import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule  } from '@angular/material/input';
import {MatDividerModule} from '@angular/material/divider';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatRippleModule} from '@angular/material/core';
import {MatDialogModule} from '@angular/material/dialog';
import {MatChipsModule} from '@angular/material/chips';


@NgModule({
  imports: [
    MatCardModule,
    MatTabsModule,
    MatCheckboxModule,
    MatTableModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatDividerModule,
    MatButtonModule,
    MatTooltipModule,
    MatGridListModule,
    MatRippleModule,
    MatDialogModule,
    MatChipsModule
  ],

  exports: [
    MatCardModule,
    MatTabsModule,
    MatCheckboxModule,
    MatTableModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatDividerModule,
    MatButtonModule,
    MatTooltipModule,
    MatGridListModule,
    MatRippleModule,
    MatDialogModule,
    MatChipsModule
  ],

  providers: [],
  bootstrap: [],
})
export class MaterialModule {}
