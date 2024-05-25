import {Component, OnDestroy, OnInit} from '@angular/core';
import {Role} from "../enum/role.enum";
import {AuthenticationService} from "../service/authentication.service";
import {BehaviorSubject, Subscription} from "rxjs";
import {MembershipService} from "../service/membership.service";
import {IUserMembership} from "../model/membership_model";
import {ScoreService} from "../service/score.service";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit, OnDestroy {

  private titleSubject = new BehaviorSubject<String>('Welcome');
  public userScore: number;
  public titleAction$ = this.titleSubject.asObservable();
  public userMembership: IUserMembership;
  private subscriptions: Subscription[] = [];

  constructor(private authenticationService: AuthenticationService,
              private membershipService: MembershipService,
              private scoreService: ScoreService) { }

  ngOnInit(): void {
    this.hasUserMembership()
    this.getUserScore()
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public changeTitle(title:string): void{
    this.titleSubject.next(title);
  }

  public hasUserMembership()
  {
    const userId = this.authenticationService.getUserFromLocalCache().id;
    this.subscriptions.push(
      this.membershipService.getUserMembership(userId).subscribe(
      (response: IUserMembership) => {
        this.userMembership = response;
      }
    ))

  }
  private getUserScore() {
    this.subscriptions.push(
      this.scoreService.getScore(this.authenticationService.getUserFromLocalCache().username).subscribe(
        (response: number) => {
          this.userScore = response;
        }
      ))
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
