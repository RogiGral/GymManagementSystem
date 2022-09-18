import { Component, OnInit } from '@angular/core';
import {Role} from "../enum/role.enum";
import {AuthenticationService} from "../service/authentication.service";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

  private titleSubject = new BehaviorSubject<String>('Welcome');
  public titleAction$ = this.titleSubject.asObservable();

  constructor(private authenticationService: AuthenticationService,) { }

  ngOnInit(): void {
  }

  public changeTitle(title:string): void{
    this.titleSubject.next(title);
  }

  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN || this.getUserRole() === Role.SUPER_ADMIN;
  }

  public get isCoach(): boolean {
    return this.isAdmin || this.getUserRole() === Role.COACH;
  }

  public get isAdminOrCoach(): boolean {
    return this.isAdmin || this.isCoach;
  }

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }

}
