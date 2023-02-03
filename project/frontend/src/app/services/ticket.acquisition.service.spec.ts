import { TestBed } from '@angular/core/testing';

import { TicketAcquisitionService } from './ticket.acquisition.service';

describe('TicketAcquisitionService', () => {
  let service: TicketAcquisitionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TicketAcquisitionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
