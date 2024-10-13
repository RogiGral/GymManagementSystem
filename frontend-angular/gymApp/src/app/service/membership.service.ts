import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {IMembershipType, IUserMembership} from "../model/membership_model";
import {CustomHttpResponse} from "../model/custom-http-response_model";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {User} from "../model/user_model";
import {IWorkout} from "../model/workout_model";

@Injectable({
  providedIn: 'root'
})
export class MembershipService {

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
  public getUserMembership(userId: number): Observable<IUserMembership>{
    return this.http.get<IUserMembership>(`${this.host}/userMembership/${userId}`);
  }
  public joinMembership(formData: FormData): Observable<IUserMembership> {
    return this.http.post<IUserMembership>(`${this.host}/userMembership/add`,formData)
  }
  public leaveMembership(userId: number): Observable<CustomHttpResponse> {
    return this.http.delete<CustomHttpResponse>(`${this.host}/userMembership/delete/${userId}`)
  }

  public createPaymentIntent(formData: FormData): Observable<string> {
    return this.http.post<string>(`${this.host}/userMembership/create_payment_intent`,formData)
  }
  public confirmPaymentIntent(formData: FormData): Observable<string> {
    return this.http.post<string>(`${this.host}/userMembership/confirm_payment`,formData)
  }
  public cancelPaymentIntent(formData: FormData): Observable<string> {
    return this.http.post<string>(`${this.host}/userMembership/cancel_payment`,formData)
  }

  createMembershipTypeFormDate(oldMembershipTypeName: string, membershipType: IMembershipType): FormData {
    const formData = new FormData();
    oldMembershipTypeName.length > 1? formData.append('oldName', oldMembershipTypeName) : '';
    formData.append('name', membershipType.name);
    formData.append('description', membershipType.description);
    formData.append('type', membershipType.type);
    formData.append('price', membershipType.price.toString());
    formData.append('validityPeriodNumber', membershipType.validityPeriodNumber.toString());
    formData.append('validityUnitOfTime', membershipType.validityUnitOfTime.toString());
    return formData;
  }

  createMembershipFormJoinData(user: User,membershipType: IMembershipType): FormData {
    const formData = new FormData();
    formData.append('membershipTypeId', membershipType.id.toString());
    formData.append('userId', user.id.toString());
    return formData;
  }

  createPaymentIntentFormData(currency: string, membershipPrice: number,customerId: string): FormData {
    const formData = new FormData();
    formData.append('membershipPrice', membershipPrice.toString());
    formData.append('currency', currency);
    formData.append('customerId', customerId);
    return formData;
  }
  setPaymentIntentStatusFormData(paymentIntentId: string): FormData {
    const formData = new FormData();
    formData.append('paymentIntentId', paymentIntentId);
    return formData;
  }

}
