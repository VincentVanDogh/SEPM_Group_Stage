import { TestBed } from '@angular/core/testing';

import { TicketService } from './ticket.service';

describe('TicketAcquisitionService', () => {
  let service: TicketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TicketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
