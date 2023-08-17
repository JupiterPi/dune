import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './ui/app.component';
import { BoardComponent } from './ui/board/board.component';
import { GameComponent } from './ui/game/game.component';
import {RouterModule} from "@angular/router";
import { RichTextTestComponent } from './ui/rich-text/rich-text-test/rich-text-test.component';
import { RichTextNodeComponent } from './ui/rich-text/rich-text-node/rich-text-node.component';

@NgModule({
  declarations: [
    AppComponent,
    BoardComponent,
    GameComponent,
    RichTextTestComponent,
    RichTextNodeComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot([
      { path: "game", component: GameComponent },
      { path: "rich-text-test", component: RichTextTestComponent },
    ])
  ],
  providers: [],
  bootstrap: [AppComponent],
  exports: [RouterModule]
})
export class AppModule { }
