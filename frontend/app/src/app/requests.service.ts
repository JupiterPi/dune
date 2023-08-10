import { Injectable } from '@angular/core';
import {SocketService} from "./socket.service";
import {Subject} from "rxjs";

export type Request = {
  id: string,
  type: string,
  content: string,
}

export type Response = {
  requestId: string,
  content: string,
}

@Injectable({
  providedIn: 'root'
})
export class RequestsService {
  private requests: Subject<Request> = new Subject<Request>();

  constructor(private socket: SocketService) {
    this.socket.onMessage("request").subscribe(request => {
      this.requests.next(request as Request);
    });
  }

  handleRequests(handler: (request: Request) => Response | null) {
    this.requests.subscribe(request => {
      const response = handler(request);
      if (response != null) this.sendResponse(response);
    });
  }

  getRequests() {
    return this.requests;
  }
  sendResponse(response: Response) {
    this.socket.sendMessage("request", response);
  }
}
