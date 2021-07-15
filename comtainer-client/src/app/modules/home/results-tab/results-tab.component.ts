import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-results-tab',
  templateUrl: './results-tab.component.html',
  styleUrls: ['./results-tab.component.css'],
})
export class ResultsTabComponent implements OnInit {
  centered = false;
  disabled = false;
  unbounded = false;

  constructor() {}

  ngOnInit() {}

  onClickToRipple() {
    console.log('Clicked');
  }
}
