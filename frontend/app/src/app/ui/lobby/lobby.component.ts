import {Component} from '@angular/core';
import {RequestsService} from "../../requests.service";
import {Router} from "@angular/router";
import {AuthService} from "../../auth.service";

@Component({
  selector: 'app-lobby',
  templateUrl: './lobby.component.html',
  styleUrls: ['./lobby.component.scss']
})
export class LobbyComponent {
  gameId?: number;

  constructor(private requests: RequestsService, private router: Router, private auth: AuthService) {
    this.auth.getIsLoggedIn().subscribe(isLoggedIn => {
      if (!isLoggedIn) {
        const name = prompt("Username:")!!;
        const password = prompt("Password:")!!;
        this.auth.login({name, password}).subscribe(valid => {
          if (!valid) alert("Invalid credentials! Refresh to log in again.");
        });
      } else console.log("is logged in");
    });
  }

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
