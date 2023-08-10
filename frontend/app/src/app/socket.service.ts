import {Injectable} from '@angular/core';
import {Observable, Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SocketService {
  private ws: WebSocket;
  private topics = new Map<string, Subject<any>>();

  constructor() {
    this.ws = new WebSocket("ws://localhost:8080/echo3");
    this.ws.addEventListener("message", (message: MessageEvent) => {
      const packet = JSON.parse(message.data) as {topic: string, payload: any};
      const subject = this.topics.get(packet.topic);
      if (subject != undefined) subject.next(packet.payload);
      else console.error("No listener for packet: ", packet);
    });
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
    const packet = {topic, payload};
    this.ws.send(JSON.stringify(packet));
  }
}
