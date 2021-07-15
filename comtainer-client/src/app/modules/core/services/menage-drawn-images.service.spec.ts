/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { MenageDrawnImagesService } from './menage-drawn-images.service';

describe('Service: MenageDrawnImages', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MenageDrawnImagesService]
    });
  });

  it('should ...', inject([MenageDrawnImagesService], (service: MenageDrawnImagesService) => {
    expect(service).toBeTruthy();
  }));
});
