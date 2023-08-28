import {Component} from '@angular/core';
import {RequestsService} from "../../requests.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-lobby',
  templateUrl: './lobby.component.html',
  styleUrls: ['./lobby.component.scss']
})
export class LobbyComponent {
  gameId?: number;

  constructor(private requests: RequestsService, private router: Router) {}

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
