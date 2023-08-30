import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../environments/environment";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class RequestsService {
  root = environment.root;
  authHeaders?: any;

  constructor(private http: HttpClient, private auth: AuthService) {
    this.auth.getAuthHeaders().subscribe(authHeaders => {
      this.authHeaders = authHeaders;
    });
  }

  createGame() {
    return new Observable<number>(subscriber => {
      this.http.post<{gameId: number}>(`${this.root}/games/create`, null).subscribe(data => {
        subscriber.next(data.gameId);
      });
    });
  }

  startGame(gameId: number) {
    return this.http.post(`${this.root}/games/${gameId}/start`, null, {responseType: "text", headers: this.authHeaders});
  }
}
