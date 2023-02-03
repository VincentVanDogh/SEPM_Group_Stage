import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MerchandiseArticleComponent } from './merchandise-article.component';

describe('MerchandiseArticleComponent', () => {
  let component: MerchandiseArticleComponent;
  let fixture: ComponentFixture<MerchandiseArticleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MerchandiseArticleComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MerchandiseArticleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
