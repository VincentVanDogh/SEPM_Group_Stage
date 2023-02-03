import { TestBed } from '@angular/core/testing';

import { MerchArticleService } from './merch-article.service';

describe('MerchArticleService', () => {
  let service: MerchArticleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MerchArticleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
