import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class NotificationService {

  private stompClient: Client | null = null;
  private notificationSubject = new Subject<any>();
  notifications$ = this.notificationSubject.asObservable();


  connect() {
    const username = localStorage.getItem('username');      // schon beim Login gespeichert
    if (!username) {                                        // Fallback-Schutz
      console.warn('Kein Username im LocalStorage – keine WS-Verbindung aufgebaut.');
      return;
    }

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay   : 5000
    });

    this.stompClient.onConnect = () => {
      //persönliches topic
      this.stompClient!.subscribe(
        `/topic/ride-offer/${username}`,
        (msg: IMessage) => {
          const data = JSON.parse(msg.body);
          this.notificationSubject.next(data);
        }
      );
    };

    this.stompClient.activate();
  }

  disconnect() {
    this.stompClient?.deactivate();
  }
}
