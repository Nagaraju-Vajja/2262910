import { TestBed } from '@angular/core/testing';

import { AdditionalLoginService } from './additional-login.service';

describe('AdditionalLoginService', () => {
  let service: AdditionalLoginService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdditionalLoginService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
