import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BarByAgeComponent } from './bar-by-age.component';

describe('BarByAgeComponent', () => {
  let component: BarByAgeComponent;
  let fixture: ComponentFixture<BarByAgeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BarByAgeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BarByAgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
