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
        "type": "player",
        "name": "JupiterPi"
      },
      {
        "type": "text",
        "text": " played ",
        "bold": false
      },
      {
        "type": "agent_card",
        "id": "DAGGER"
      },
      {
        "type": "text",
        "text": " on ",
        "bold": false
      },
      {
        "type": "agent_action",
        "id": "COLLECT_TROOPS"
      },
      {
        "type": "text",
        "text": " and placed ",
        "bold": false
      },
      {
        "type": "text",
        "text": "4",
        "bold": true
      },
      {
        "type": "text",
        "text": " troops into conflict",
        "bold": false
      }
    ]
  };
}
