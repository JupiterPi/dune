import {Component, Input} from '@angular/core';

export interface RichTextNode {
  type: string,
}

export interface RichTextContainer extends RichTextNode { components: RichTextNode[] }
export interface TextComponent extends RichTextNode { text: string; bold: boolean }
export interface PlayerComponent extends RichTextNode { name: string }
export interface EnumeratedComponent extends RichTextNode { id: string }

@Component({
  selector: 'app-rich-text-node',
  templateUrl: './rich-text-node.component.html',
  styleUrls: ['./rich-text-node.component.scss']
})
export class RichTextNodeComponent {
  @Input() node: RichTextNode = { type: "container" };

  asContainer() {
    return this.node as RichTextContainer;
  }

  asText() {
    return this.node as TextComponent;
  }

  asPlayer() {
    return this.node as PlayerComponent;
  }

  asEnumerated() {
    return this.node as EnumeratedComponent;
  }
  enumeratedImageResource() {
    const component = this.node as EnumeratedComponent;
    return `/assets/${component.type}/${component.id}.jpg`;
  }
}
