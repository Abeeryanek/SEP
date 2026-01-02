import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FahrerStatistikenComponent } from './fahrer-statistiken.component';

describe('FahrerStatistikenComponent', () => {
  let component: FahrerStatistikenComponent;
  let fixture: ComponentFixture<FahrerStatistikenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FahrerStatistikenComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FahrerStatistikenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
