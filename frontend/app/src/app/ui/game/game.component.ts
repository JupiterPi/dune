import { Component, OnInit } from '@angular/core';
import {Game, GameService, PlayerGame} from "../../game.service";
import {RequestsService} from "../../requests.service";

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit {
  game?: Game;
  playerGame?: PlayerGame;

  constructor(private gameService: GameService, private requests: RequestsService) {}

  ngOnInit(): void {
    this.gameService.getGame().subscribe(game => {
      this.game = game;
    });
    this.gameService.getPlayerGame().subscribe(playerGame => {
      this.playerGame = playerGame;
    });

    /*this.requests.handleRequests(request => {
      console.log(request);
      return {
        requestId: request.id,
        content: "reponsee"
      };
    });*/

    this.requests.getRequests().subscribe(request => {
      this.requests.sendResponse({
        requestId: request.id,
        content: "responsee2"
      });
    });
  }
}
