import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {CustomHttpResponse} from "../model/custom-http-response_model";


@Injectable({
  providedIn: 'root'
})
export class ScoreService {
  private host = environment.apiUrl

  constructor(private http: HttpClient) { }

  public getScore(username: string): Observable<number>{
    return  this.http.get<number>(`${this.host}/score/get/${username}`);
  }
  public addScore(formdata: FormData): Observable<CustomHttpResponse>{
    return this.http.post<CustomHttpResponse>(`${this.host}/score/add`,formdata);
  }
  public removeScore(formdata: FormData): Observable<CustomHttpResponse>{
    return this.http.post<CustomHttpResponse>(`${this.host}/score/remove`,formdata);
  }
  createScoreFormData(username: string, difficulty: string): FormData {
    const formData = new FormData();
    formData.append('username', username);
    formData.append('difficulty', difficulty);
    return formData;
  }

}
