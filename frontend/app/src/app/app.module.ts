import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './ui/app.component';
import { BoardComponent } from './ui/board/board.component';
import { GameComponent } from './ui/game/game.component';

@NgModule({
  declarations: [
    AppComponent,
    BoardComponent,
    GameComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
