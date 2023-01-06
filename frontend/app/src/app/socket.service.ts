import {Injectable, OnDestroy} from '@angular/core';
import * as SockJS from "sockjs-client";
import {CompatClient, Stomp, StompSubscription} from "@stomp/stompjs";
import {BehaviorSubject, filter, first, Observable, switchMap} from "rxjs";
import {environment} from "../environments/environment";

export enum SocketClientState {
  ATTEMPTING, CONNECTED
}

@Injectable({
  providedIn: 'root'
})
export class SocketService implements OnDestroy {
  private client: CompatClient;
  private state: BehaviorSubject<SocketClientState>;

  constructor() {
    this.client = Stomp.over(new SockJS(environment.socketUrl));
    this.state = new BehaviorSubject<SocketClientState>(SocketClientState.ATTEMPTING);
    this.client.connect({}, () => {
      this.state?.next(SocketClientState.CONNECTED);
    });
  }

  private connect(): Observable<CompatClient> {
    return new Observable<CompatClient>(observer => {
      this.state?.pipe(
        filter(state => state === SocketClientState.CONNECTED)
      ).subscribe(() => {
        observer.next(this.client);
      });
    });
  }

  onMessage(topic: string): Observable<any> {
    return this.connect().pipe(
      first(),
      switchMap(client => {
        return new Observable<any>(observer => {
          const subscription: StompSubscription = client.subscribe(topic, message => {
            observer.next(JSON.parse(message.body));
          });
          return () => client.unsubscribe(subscription.id);
        });
      })
    );
  }

  sendMessage(topic: string, payload: any) {
    this.connect()
      .pipe(first())
      .subscribe(client => client.send(topic, {}, JSON.stringify(payload)));
  }

  ngOnDestroy(): void {
    this.connect().pipe(first()).subscribe(client => client.disconnect());
  }
}
