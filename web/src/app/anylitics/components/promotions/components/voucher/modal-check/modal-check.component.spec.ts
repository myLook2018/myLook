import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalCheckComponent } from './modal-check.component';

describe('ModalCheckComponent', () => {
  let component: ModalCheckComponent;
  let fixture: ComponentFixture<ModalCheckComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ModalCheckComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalCheckComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
