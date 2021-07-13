import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatTableModule} from '@angular/material/table';

@NgModule({
  imports: [MatCardModule, MatTabsModule, MatCheckboxModule, MatTableModule],

  exports: [MatCardModule, MatTabsModule, MatCheckboxModule, MatTableModule],

  providers: [],
  bootstrap: [],
})
export class MaterialModule {}
