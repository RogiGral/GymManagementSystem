import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
//import {BarcodeFormat} from "@zxing/library";
import jsQR from 'jsqr';
import {Role} from "../enum/role.enum";
import {AuthenticationService} from "../service/authentication.service";
import {CryptoService} from "../service/crypto.service";

@Component({
  selector: 'app-barcode-scanner',
  templateUrl: './qrcode-scanner.component.html',
  styleUrls: ['./qrcode-scanner.component.css']
})
export class QrcodeScannerComponent implements OnInit {
  @ViewChild('video') video: ElementRef;
  @ViewChild('canvas') canvas: ElementRef;
  qrResult: string = '';
  videoStream: any;

  constructor(
    private authenticationService: AuthenticationService,
    private cryptoService: CryptoService
  ) { }

  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN || this.getUserRole() === Role.SUPER_ADMIN;
  }

  public get isCoach(): boolean {
    return this.isAdmin || this.getUserRole() === Role.COACH;
  }

  public get isAdminOrCoach(): boolean {
    return this.isAdmin || this.isCoach;
  }

  ngOnInit(): void {
  }

  onStartVideo() {
    navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } })
      .then(stream => {
        this.videoStream = stream;
        this.video.nativeElement.srcObject = stream;
        this.video.nativeElement.setAttribute('playsinline', true); // Required to avoid blocking on iOS devices
        this.video.nativeElement.play();
        requestAnimationFrame(this.scan.bind(this)); // Start scanning
      })
      .catch(err => {
        console.error('Error accessing the camera: ', err);
      });
  }

  async scan() {
    const canvasElement = this.canvas.nativeElement;
    const videoElement = this.video.nativeElement;
    const canvasContext = canvasElement.getContext('2d');

    if (videoElement.readyState === videoElement.HAVE_ENOUGH_DATA) {
      canvasElement.width = videoElement.videoWidth;
      canvasElement.height = videoElement.videoHeight;
      canvasContext.drawImage(videoElement, 0, 0, canvasElement.width, canvasElement.height);

      const imageData = canvasContext.getImageData(0, 0, canvasElement.width, canvasElement.height);
      const code = jsQR(imageData.data, imageData.width, imageData.height);

      if (code) {
        this.cryptoService.decryptData(JSON.stringify(code))
          .then(encodedData => {
            this.qrResult = encodedData;
          })
          .catch(error => {
            console.log(error)
          });
        console.log('QR Code detected: ', this.qrResult);
      } else {
        requestAnimationFrame(this.scan.bind(this)); // Continue scanning
      }
    } else {
      requestAnimationFrame(this.scan.bind(this)); // Retry if video is not ready
    }
  }

  onStopVideo() {
    if (this.videoStream) {
      this.videoStream.getTracks().forEach((track: any) => track.stop());
    }
  }

  ngOnDestroy() {
    this.onStopVideo(); // Clean up when the component is destroyed
  }

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId)!.click();
  }
}
