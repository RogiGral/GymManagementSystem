import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {CustomHttpResponse} from "../model/custom-http-response_model";

@Injectable({
  providedIn: 'root'
})
export class QrcodeService {

  private host = environment.apiUrl

  constructor(private http: HttpClient) { }

  public getQrcode(uuid: string): Observable<any>{
    return this.http.get<any>(`${this.host}/qrcode/${uuid}`);
  }
  public addQrCode(formdata: FormData): Observable<any>{
    return this.http.post<any>(`${this.host}/qrcode/add`,formdata);
  }

  createQrCodeFormData(uuid: string, data: string): FormData {
    const formData = new FormData();
    formData.append('uuid', uuid);
    formData.append('data', data);
    return formData;
  }
}
