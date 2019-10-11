import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AnyliticsDashboardComponent } from './analytics-dashboard.component';

describe('AnyliticsDashboardComponent', () => {
  let component: AnyliticsDashboardComponent;
  let fixture: ComponentFixture<AnyliticsDashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AnyliticsDashboardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnyliticsDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
