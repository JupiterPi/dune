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
    if (!this.auth.loggedIn) {
      const name = prompt("Username:")!!;
      const password = prompt("Password:")!!;
      this.auth.login({name, password}).subscribe();
    }
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
    this.router.navigate(["game"], {queryParams: { "join": this.gameId }})
  }
}
