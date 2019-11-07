import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LinesDaysUsedComponent } from './lines-days-used.component';

describe('LinesDaysUsedComponent', () => {
  let component: LinesDaysUsedComponent;
  let fixture: ComponentFixture<LinesDaysUsedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LinesDaysUsedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LinesDaysUsedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
