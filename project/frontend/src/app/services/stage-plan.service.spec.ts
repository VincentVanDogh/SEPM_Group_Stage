import { TestBed } from '@angular/core/testing';

import { StagePlanService } from './stage-plan.service';

describe('StagePlanService', () => {
  let service: StagePlanService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StagePlanService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
