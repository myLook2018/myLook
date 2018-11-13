import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PromotionsBarComponent } from './promotions-bar.component';

describe('PromotionsBarComponent', () => {
  let component: PromotionsBarComponent;
  let fixture: ComponentFixture<PromotionsBarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PromotionsBarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PromotionsBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
