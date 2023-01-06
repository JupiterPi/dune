import { Component, OnInit } from '@angular/core';
import {Game, GameService} from "../../game.service";

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit {
  game?: Game;

  constructor(private gameService: GameService) {}

  ngOnInit(): void {
    this.gameService.getGame().subscribe(game => {
      this.game = game;
    });
  }
}
