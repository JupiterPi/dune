import { Injectable } from '@angular/core';

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

@Injectable({
  providedIn: 'root'
})
export class GameService {}
