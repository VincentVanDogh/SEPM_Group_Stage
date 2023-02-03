import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StagePlanSingleGenericComponent } from './stage-plan-single-generic.component';

describe('StagePlanSingleGenericComponent', () => {
  let component: StagePlanSingleGenericComponent;
  let fixture: ComponentFixture<StagePlanSingleGenericComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StagePlanSingleGenericComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StagePlanSingleGenericComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
