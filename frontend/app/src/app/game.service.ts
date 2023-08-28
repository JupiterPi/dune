import {Injectable} from '@angular/core';
import {SocketService} from "./socket.service";
import {BehaviorSubject} from "rxjs";
import {RequestsService} from "./requests.service";

export type Game = {
  players: Player[],
  allies: {
    IMPERATOR?: string,
    SPACING_GUILD?: string,
    BENE_GESSERIT?: string,
    FREMEN?: string,
  },
  control: {
    ARRAKEEN?: string,
    CARTHAG?: string,
    IMPERIAL_BASIN?: string,
  },
  highCouncilMembers: string[],
  aggregatedSpice: {
    GREAT_PLAIN: number,
    HAGGA_BASIN: number,
    IMPERIAL_BASIN: number,
  },
}

export type Player = {
  name: string,
  color: string,
  leader: Leader,
  solari: number,
  spice: number,
  water: number,
  militaryStrength: number,
  victoryPoints: number,
  influenceLevels: {
    IMPERATOR: number,
    SPACING_GUILD: number,
    BENE_GESSERIT: number,
    FREMEN: number,
  },
}

export type Leader = {
  id: string,
  name: string,
}

export type PlayerGame = {
  hand: AgentCard[],
  intrigueCards: IntrigueCard[],
}

export type AgentCard = {
  id: string,
  title: string,
  agentSymbols: string[],
}

export type IntrigueCard = {
  id: string,
  title: string,
}

export type RequestHandler = (payload: any) => any;

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private game: BehaviorSubject<Game> = new BehaviorSubject<Game>({
    players: [],
    allies: {
      IMPERATOR: undefined,
      SPACING_GUILD: undefined,
      BENE_GESSERIT: undefined,
      FREMEN: undefined,
    },
    control: {
      ARRAKEEN: undefined,
      CARTHAG: undefined,
      IMPERIAL_BASIN: undefined,
    },
    highCouncilMembers: [],
    aggregatedSpice: {
      GREAT_PLAIN: 0,
      HAGGA_BASIN: 0,
      IMPERIAL_BASIN: 0,
    },
  });
  private playerGame: BehaviorSubject<PlayerGame> = new BehaviorSubject<PlayerGame>({
    hand: [],
    intrigueCards: [],
  });
  private requestHandlers = new Map<string, RequestHandler>();

  constructor(private socket: SocketService) {
    this.socket.onMessage("game").subscribe(game => {
      this.game.next(game as Game);
    });
    this.socket.onMessage("playerGame").subscribe(playerGame => {
      this.playerGame.next(playerGame as PlayerGame);
    });
    this.socket.onMessage("request").subscribe((request: {type: string, payload: any, id: string}) => {
      const response = this.requestHandlers.get(request.type)?.call(null, request.payload);
      this.socket.sendMessage("request", {
        requestId: request.id,
        content: response
      });
    });
  }

  getGame() {
    return this.game;
  }

  getPlayerGame() {
    return this.playerGame;
  }

  handleRequests(type: string, handler: RequestHandler) {
    this.requestHandlers.set(type, handler);
  }
}
