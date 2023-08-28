import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class RequestsService {
  root = environment.root;

  constructor(private http: HttpClient) {}

  createGame() {
    return new Observable<number>(subscriber => {
      this.http.post<{gameId: number}>(`${this.root}/games/create`, null).subscribe(data => {
        subscriber.next(data.gameId);
      });
    });
  }

  startGame(gameId: number) {
    return this.http.post<void>(`${this.root}/games/${gameId}/start`, null);
  }
}
