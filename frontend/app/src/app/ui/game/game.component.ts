import {Component} from '@angular/core';
import {Game, GameService, PlayerGame} from "../../game.service";
import {SocketService} from "../../socket.service";
import {ActivatedRoute} from "@angular/router";
import {RequestsService} from "../../requests.service";

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent {
  gameId?: number;
  started = false;

  game?: Game;
  playerGame?: PlayerGame;

  constructor(private gameService: GameService, private socket: SocketService, private requests: RequestsService, private route: ActivatedRoute) {
    this.route.queryParams.subscribe(params => {
      this.gameId = params["join"];
      /*this.socket.connect(1 , {
        name: prompt("Player name:")!!,
        color: prompt("Color (RED, GREEN, BLUE, YELLOW):")!!,
        leader: prompt("Leader (e. g. ATREIDES_PAUL):")!!
      });*/
      this.socket.connect(this.gameId!!, {
        name: "JupiterPi",
        color: "BLUE",
        leader: "ATREIDES_PAUL"
      });
    });
    this.gameService.getGame().subscribe(game => {
      if (game.players.length > 0) this.started = true;
      this.game = game;
      console.log(game);
    });
    this.gameService.getPlayerGame().subscribe(playerGame => {
      this.playerGame = playerGame;
      console.log(playerGame);
    });

    this.gameService.handleRequests("SIMPLE_CHOICE", (args: {choices: string[], min: number, max: number}) => {
      console.log(args.choices);
      console.log(args.min);
      console.log(args.max);
      return "1";
    });
  }

  startGame() {
    this.requests.startGame(this.gameId!!).subscribe();
  }
}
