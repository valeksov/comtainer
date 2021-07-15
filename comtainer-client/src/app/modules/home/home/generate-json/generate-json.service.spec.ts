/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { GenerateJsonService } from './generate-json.service';

describe('Service: GenerateJson', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GenerateJsonService]
    });
  });

  it('should ...', inject([GenerateJsonService], (service: GenerateJsonService) => {
    expect(service).toBeTruthy();
  }));
});
