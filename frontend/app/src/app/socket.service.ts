import {Injectable} from '@angular/core';
import {Observable, Subject} from "rxjs";
import {environment} from "../environments/environment";

interface PlayerCreateDTO {
  name: string;
  color: string;
  leader: string;
}

@Injectable({
  providedIn: 'root'
})
export class SocketService {
  private ws?: WebSocket;
  private topics = new Map<string, Subject<any>>();
  private messageQueue: string[] = [];

  connect(gameId: number, playerConfiguration: PlayerCreateDTO) {
    this.ws = new WebSocket(`ws://${environment.host}/games/${gameId}/join`);
    this.ws.addEventListener("message", (message: MessageEvent) => {
      const packet = JSON.parse(message.data) as {topic: string, payload: any};
      const subject = this.topics.get(packet.topic);
      if (subject != undefined) subject.next(packet.payload);
      else console.error("No listener for packet: ", packet);
    });
    this.ws.addEventListener("open", () => {
      this.ws!!.send(JSON.stringify(playerConfiguration));
      this.messageQueue.forEach(message => this.ws!!.send(message));
    });

    //TODO tmp
    this.ws.addEventListener("close", event => console.log("Closed:", event.code, "reason", event.reason, event.wasClean, "."));
  }

  onMessage(topic: string): Observable<any> {
    const subject = this.topics.get(topic);
    if (subject == undefined) {
      const newSubject = new Subject<any>();
      this.topics.set(topic, newSubject);
      return newSubject;
    } else {
      return subject;
    }
  }

  sendMessage(topic: string, payload: any) {
    const message = JSON.stringify({topic, payload});
    if (this.ws != undefined) {
      this.ws.send(message);
    } else {
      this.messageQueue.push(message);
    }
  }
}
