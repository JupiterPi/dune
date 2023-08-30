import {Component} from '@angular/core';
import {RequestsService} from "../../requests.service";
import {Router} from "@angular/router";
import {AuthService} from "../../auth.service";
import {isNonNull} from "../../../util";
import {filter} from "rxjs";

@Component({
  selector: 'app-lobby',
  templateUrl: './lobby.component.html',
  styleUrls: ['./lobby.component.scss']
})
export class LobbyComponent {
  isLoggedIn = false;
  username?: string;

  gameId?: number;

  constructor(private requests: RequestsService, private router: Router, private auth: AuthService) {
    auth.isLoggedIn$.pipe(filter(isNonNull)).subscribe(isLoggedIn => this.isLoggedIn = isLoggedIn);
    auth.credentials$.pipe(filter(isNonNull)).subscribe(credentials => this.username = credentials.name);
  }

  // auth

  register() {
    const name = prompt("Username:");
    const password = prompt("Password:");
    if (name == null || password == null) return;
    this.auth.register({name, password}).subscribe();
  }

  login() {
    const name = prompt("Username:");
    const password = prompt("Password:");
    if (name == null || password == null) return;
    this.auth.login({name, password}).subscribe(valid => {
      if (!valid) alert("Invalid credentials!");
    });
  }

  changePassword() {
    const newPassword = prompt("New password:");
    if (newPassword == null) return;
    this.auth.changePassword(newPassword).subscribe();
  }

  logout() {
    this.auth.logout();
  }

  // game

  createGame() {
    this.requests.createGame().subscribe(gameId => {
      this.gameId = gameId;
    });
  }

  get gameLink() {
    //TODO tmp
    return `https://dune-game.com/game?join=${this.gameId}`;
  }

  joinGame() {
    this.router.navigate(["game"], {queryParams: { "join": this.gameId }});
  }
}
