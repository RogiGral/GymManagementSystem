import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QrcodeScannerComponent } from './qrcode-scanner.component';

describe('BarcodeScannerComponent', () => {
  let component: QrcodeScannerComponent;
  let fixture: ComponentFixture<QrcodeScannerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QrcodeScannerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QrcodeScannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
