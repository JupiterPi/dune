import {Component, ElementRef, Inject, OnInit} from '@angular/core';
import {Game} from "../../game.service";
import {compareNumbers} from "@angular/compiler-cli/src/version_helpers";

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss']
})
export class BoardComponent implements OnInit {
  state: Game = {
    players: [
      {
        name: "Player 1",
        color: "#F26968",
        leader: {
          id: "ATREIDES_PAUL",
          name: "Paul Atreides",
        },
        solari: 1,
        spice: 1,
        water: 1,
        militaryStrength: 0,
        victoryPoints: 1,
        influenceLevels: {
          IMPERATOR: 0,
          SPACING_GUILD: 1,
          BENE_GESSERIT: 2,
          FREMEN: 3,
        },
      },
      {
        name: "Player 2",
        color: "#75AFD4",
        leader: {
          id: "HARKONNEN_BEAST",
          name: "Glossu \"Beast\" Harkonnen",
        },
        solari: 1,
        spice: 1,
        water: 1,
        militaryStrength: 1,
        victoryPoints: 1,
        influenceLevels: {
          IMPERATOR: 0,
          SPACING_GUILD: 2,
          BENE_GESSERIT: 4,
          FREMEN: 6,
        },
      },
    ],
    allies: {
      IMPERATOR: undefined,
      SPACING_GUILD: undefined,
      BENE_GESSERIT: undefined,
      FREMEN: undefined,
    },
    control: {
      ARRAKEEN: "Player 1",
      CARTHAG: "Player 2",
      IMPERIAL_BASIN: undefined,
    },
    highCouncilMembers: ["Player 2"],
    aggregatedSpice: {
      GREAT_PLAIN: 0,
      HAGGA_BASIN: 1,
      IMPERIAL_BASIN: 2,
    }
  }

  constructor(@Inject(ElementRef) private elementRef: ElementRef) {}

  actions = [
    "CONSPIRACY", "WEALTH", "HEIGHLINER", "FOLD_SPACE", "SELECTIVE_BREEDING", "SECRETS", "TOUGH_WARRIORS", "STILLSUIT", "HIGH_COUNCIL", "SPEAKER_HALL", "MENTAT", "COLLECT_TROOPS", "SWORD_MASTER", "SIETCH_TABR", "RESEARCH_CENTER", "CARTHAG", "ARRAKEEN", "GREAT_PLAIN", "HAGGA_BASIN", "IMPERIAL_BASIN", "SELL_SPICE", "MAKE_DEAL"
  ];
  factions = [
    "IMPERATOR", "SPACING_GUILD", "BENE_GESSERIT", "FREMEN"
  ];
  controls = [
    "ARRAKEEN", "CARTHAG", "IMPERIAL_BASIN"
  ];
  sandworms = [
    "GREAT_PLAIN", "HAGGA_BASIN", "IMPERIAL_BASIN"
  ];

  ngOnInit(): void {
    this.prepareSVGElements();
    this.renderGame(this.state);
  }

  private prepareSVGElements() {
    this.applyToElement("svg *", e => {
      e.setAttribute("style", "");
    });

    // actions
    this.actions.forEach( action => this.applyToElement(`#action_${action}`, e => {
      e.classList.add("action");
    }));

    // controls
    this.controls.forEach( control => {
      this.applyToElement(`#control_${control}`, e => {
        e.classList.add("control");
      });
    });

    // sandworms (+ text)
    this.sandworms.forEach( sandworm => {
      this.applyToElement(`#sandworm_${sandworm}`, e => {
        e.classList.add("sandworm");
      });
      this.applyToElement(`#sandworm_text_${sandworm}`, e => {
        e.classList.add("sandworm-text");
      });
    });

    // high council, garrisons (+ text), conflict zones (+ text)
    [1, 2, 3, 4].forEach( i => {
      this.applyToElement(`#high_council_${i}`, e => {
        e.classList.add("high_council");
      });
      this.applyToElement(`#garrison_${i}`, e => {
        e.classList.add("garrison");
      });
      this.applyToElement(`#garrison_text_${i}`, e => {
        e.classList.add("garrison-text");
      });
      this.applyToElement(`#conflict_zone_${i}`, e => {
        e.classList.add("conflict_zone");
      });
      this.applyToElement(`#conflict_zone_text_${i}`, e => {
        e.classList.add("conflict_zone-text");
      });
    });

    // influence levels (+ dots)
    this.factions.forEach( faction => {
      [0, 1, 2, 3, 4, 5, 6].forEach( level => {
        this.applyToElement(`#influence_${faction}_${level}`, e => {
          e.classList.add("influence_level");
        });

        const dotsGroup = this.getElement(`#influence_dots_${faction}_${level}`);
        const dots = Array.from(dotsGroup.children);
        dots.sort((a, b) => {
          return parseInt(a.getAttribute("cx") ?? "0") - parseInt(b.getAttribute("cx") ?? "0");
        });
        [0, 1, 2, 3].forEach(i1 => {
          dots[i1].setAttribute("id", `influence_dot_${faction}_${level}_${i1+1}`);
          dots[i1].classList.add("influence_dot");
        });
      });
    });

    // military strength (+ dots)
    for (let i = 0; i <= 20; i++) {
      this.applyToElement(`#military_strength_${i}`, e => {
        e.classList.add("military_strength");
      });

      const dotsGroup = this.getElement(`#military_strength_dots_${i}`);
      const dots = Array.from(dotsGroup.children);
      dots.sort((a, b) => {
        return (
          (parseInt(a.getAttribute("cy") ?? "0") - parseInt(b.getAttribute("cy") ?? "0"))*10
          + parseInt(a.getAttribute("cy") ?? "0") - parseInt(b.getAttribute("cy") ?? "0")
        );
      });
      [0, 1, 2, 3].forEach(i1 => {
        dots[i1].setAttribute("id", `military_strength_dot_${i}_${i1+1}`);
        dots[i1].classList.add("military_strength_dot");
      });
    }

    // victory points (+ dots)
    for (let i = 0; i <= 12; i++) {
      this.applyToElement(`#victory_points_${i}`, e => {
        e.classList.add("victory_points");
      });

      const dotsGroup = this.getElement(`#victory_points_dots_${i}`);
      const dots = Array.from(dotsGroup.children);
      dots.sort((a, b) => {
        return parseInt(a.getAttribute("cy") ?? "0") - parseInt(b.getAttribute("cy") ?? "0");
      });
      [0, 1, 2, 3].forEach(i1 => {
        dots[i1].setAttribute("id", `victory_points_dot_${i}_${i1+1}`);
        dots[i1].classList.add("victory_points_dot");
      });
    }
  }

  private renderGame(game: Game) {
    const players = game.players;

    // actions (pre)
    this.actions.forEach( action => {
      this.applyToElement(`#action_${action}`, e => {
        e.classList.toggle("available", true);
      });
    });

    // allies...

    // control
    this.controls.forEach( control => {
      this.applyToElement(`#control_${control}`, e => {
        // @ts-ignore
        const controlPlayer = game.control[control];
        const taken = controlPlayer != undefined;
        if (taken) {
          e.setAttribute("fill", players.find( p => p.name == controlPlayer )?.color ?? "");
        } else {
          e.setAttribute("fill", "");
        }
        e.classList.toggle("taken", taken);
      });
    });

    // high council members
    players.forEach( (player, i) => {
      this.applyToElement(`#high_council_${i+1}`, e => {
        const taken = game.highCouncilMembers.includes(player.name);
        if (taken) {
          e.setAttribute("fill", player.color);
        } else {
          e.setAttribute("fill", "");
        }
        e.classList.toggle("taken", taken);
      });
    });

    // aggregated spice
    this.sandworms.forEach( sandworm => {
      // @ts-ignore
      const aggregatedSpice = game.aggregatedSpice[sandworm];
      this.applyToElement(`#sandworm_${sandworm}`, e => {
        e.classList.toggle("active", aggregatedSpice > 0);
      });
      this.applyToElement(`#sandworm_text_${sandworm}`, e => {
        e.classList.toggle("active", aggregatedSpice > 0);
        e.innerHTML = aggregatedSpice;
      });
    });

    // influence
    this.factions.forEach( faction => {
      [0, 1, 2, 3, 4, 5, 6].forEach( level => {
        this.applyToElement(`#influence_dot_${faction}_${level}`, e => {
          e.classList.toggle("active", false);
        });
      });
    });
    players.forEach( (player, i) => {
      this.factions.forEach(faction => {
        // @ts-ignore
        const level = player.influenceLevels[faction];
        this.applyToElement(`#influence_dot_${faction}_${level}_${i+1}`, e => {
          e.setAttribute("fill", player.color);
          e.classList.toggle("active", true);
        });
      });
    });

    // military strength
    for (let i = 0; i <= 20; i++) {
      [1, 2, 3, 4].forEach(i1 => {
        this.applyToElement(`#military_strength_dot_${i}_${i1}`, e => {
          e.classList.toggle("active", false);
        });
      });
    }
    players.forEach( (player, i) => {
      this.applyToElement(`#military_strength_dot_${player.militaryStrength}_${i+1}`, e => {
        e.setAttribute("fill", player.color);
        e.classList.toggle("active", true);
      });
    });

    // victory points
    for (let i = 0; i <= 12; i++) {
      [1, 2, 3, 4].forEach(i1 => {
        this.applyToElement(`#victory_points_dot_${i}_${i1}`, e => {
          e.classList.toggle("active", false);
        });
      });
    }
    players.forEach( (player, i) => {
      this.applyToElement(`#victory_points_dot_${player.victoryPoints}_${i+1}`, e => {
        e.setAttribute("fill", player.color);
        e.classList.toggle("active", true);
      });
    });
  }

  private applyToElement(selector: string, operation: (e: SVGElement) => void) {
    const element = this.elementRef.nativeElement as HTMLDivElement;
    let elements = element.querySelectorAll(selector);
    elements.forEach(e => operation(e as SVGElement));
  }

  private getElement(selector: string) {
    const e = this.elementRef.nativeElement as HTMLDivElement;
    return e.querySelector(selector) as SVGElement;
  }
}
