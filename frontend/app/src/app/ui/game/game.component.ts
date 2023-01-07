import { Component, OnInit } from '@angular/core';
import {Game, GameService, PlayerGame} from "../../game.service";

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit {
  game?: Game;
  playerGame?: PlayerGame;

  constructor(private gameService: GameService) {}

  ngOnInit(): void {
    this.gameService.getGame().subscribe(game => {
      this.game = game;
    });
    this.gameService.getPlayerGame().subscribe(playerGame => {
      this.playerGame = playerGame;
    });
  }
}
