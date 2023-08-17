import {Component} from '@angular/core';

@Component({
  selector: 'app-rich-text-test',
  templateUrl: './rich-text-test.component.html',
  styleUrls: ['./rich-text-test.component.scss']
})
export class RichTextTestComponent {
  sampleNode = {
    "type": "container",
    "components": [
      {
        "jsonClassDiscriminator": "jupiterpi.dune.richtext.PlayerComponent",
        "type": "player",
        "name": "JupiterPi"
      },
      {
        "jsonClassDiscriminator": "jupiterpi.dune.richtext.TextComponent",
        "type": "text",
        "text": " played ",
        "bold": false
      },
      {
        "jsonClassDiscriminator": "jupiterpi.dune.richtext.EnumeratedComponent",
        "type": "agent_card",
        "id": "DAGGER"
      },
      {
        "jsonClassDiscriminator": "jupiterpi.dune.richtext.TextComponent",
        "type": "text",
        "text": " on ",
        "bold": false
      },
      {
        "jsonClassDiscriminator": "jupiterpi.dune.richtext.EnumeratedComponent",
        "type": "agent_action",
        "id": "COLLECT_TROOPS"
      },
      {
        "jsonClassDiscriminator": "jupiterpi.dune.richtext.TextComponent",
        "type": "text",
        "text": " and placed ",
        "bold": false
      },
      {
        "jsonClassDiscriminator": "jupiterpi.dune.richtext.TextComponent",
        "type": "text",
        "text": "4",
        "bold": true
      },
      {
        "jsonClassDiscriminator": "jupiterpi.dune.richtext.TextComponent",
        "type": "text",
        "text": " troops into conflict",
        "bold": false
      }
    ]
  };
}
