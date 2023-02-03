import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StagePlanSingleSpecificComponent } from './stage-plan-single-specific.component';

describe('StagePlanSingleSpecificComponent', () => {
  let component: StagePlanSingleSpecificComponent;
  let fixture: ComponentFixture<StagePlanSingleSpecificComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StagePlanSingleSpecificComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StagePlanSingleSpecificComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
