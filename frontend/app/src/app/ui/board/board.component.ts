import {Component, ElementRef, Inject, OnInit} from '@angular/core';
import {Game, GameService} from "../../game.service";

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss']
})
export class BoardComponent implements OnInit {
  constructor(@Inject(ElementRef) private elementRef: ElementRef, private game: GameService) {}

  ngOnInit(): void {
    this.prepareSVGElements();
    this.game.getGame().subscribe(game => {
      this.renderGame(game);
    });
  }

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
  lifecycle = [
    "ROUND_START", "PLAYERS", "CONFLICT", "SANDWORMS", "RECALL"
  ];
  conflict_cards = [
    "STACK", "ACTIVE"
  ];

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

    // lifecycle
    this.lifecycle.forEach( lifecycle => {
      this.applyToElement(`#lifecycle_${lifecycle}`, e => {
        e.classList.add("lifecycle");
      });
    });

    // conflict cards
    this.conflict_cards.forEach( conflict_card => {
      this.applyToElement(`#conflict_card_${conflict_card}`, e => {
        e.classList.add("conflict_card");
      });
    });
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
