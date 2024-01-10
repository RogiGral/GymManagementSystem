import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {IMembershipType} from "../model/membership_model";
import {CustomHttpResponse} from "../model/custom-http-response_model";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class MembershipTypeService {

  private host = environment.apiUrl

  constructor(private http: HttpClient) { }

  public getMembership(): Observable<IMembershipType[]>{
    return  this.http.get<IMembershipType[]>(`${this.host}/membershipType/list`);
  }
  public addMembershipType(formdata: FormData): Observable<IMembershipType>{
    return  this.http.post<IMembershipType>(`${this.host}/membershipType/add`,formdata);
  }
  public updateMembershipType(formdata: FormData): Observable<IMembershipType>{
    return  this.http.post<IMembershipType>(`${this.host}/membershipType/update`,formdata);
  }
  public deleteMembershipType(id: number): Observable<CustomHttpResponse>{
    return this.http.delete<CustomHttpResponse>(`${this.host}/membershipType/delete/${id}`)
  }

  createMembershipTypeFormDate(oldMembershipTypeName:any, membershipType: IMembershipType): FormData {
    const formData = new FormData();
    formData.append('oldName', oldMembershipTypeName);
    formData.append('name', membershipType.name);
    formData.append('description', membershipType.description);
    formData.append('type', membershipType.type);
    formData.append('price', membershipType.price.toString());
    formData.append('numberOfMonths', membershipType.numberOfMonths.toString());
    return formData;
  }
}
