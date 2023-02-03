import { TestBed } from '@angular/core/testing';

import { MerchPurchaseService } from './merch-purchase.service';

describe('MerchPurchaseService', () => {
  let service: MerchPurchaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MerchPurchaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
