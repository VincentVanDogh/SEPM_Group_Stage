import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StagePlanSinglePriceConfigComponent } from './stage-plan-single-price-config.component';

describe('StagePlanSinglePriceConfigComponent', () => {
  let component: StagePlanSinglePriceConfigComponent;
  let fixture: ComponentFixture<StagePlanSinglePriceConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StagePlanSinglePriceConfigComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StagePlanSinglePriceConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
