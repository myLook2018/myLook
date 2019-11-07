import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PieByGenderComponent } from './pie-by-gender.component';

describe('PieByGenderComponent', () => {
  let component: PieByGenderComponent;
  let fixture: ComponentFixture<PieByGenderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PieByGenderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PieByGenderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
