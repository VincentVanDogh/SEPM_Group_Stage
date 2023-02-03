import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketGenerateComponent } from './ticket-generate.component';

describe('TicketGenerateComponent', () => {
  let component: TicketGenerateComponent;
  let fixture: ComponentFixture<TicketGenerateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketGenerateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketGenerateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
