import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MerchPurchasesComponent } from './merch-purchases.component';

describe('MerchPurchasesComponent', () => {
  let component: MerchPurchasesComponent;
  let fixture: ComponentFixture<MerchPurchasesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MerchPurchasesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MerchPurchasesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
