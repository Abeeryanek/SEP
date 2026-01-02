import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Chat } from '../models/chat.model';
import SockJS from 'sockjs-client';
import { Client, Message, StompSubscription } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private chatsSubject = new BehaviorSubject<Chat[]>([]);
  public chats$ = this.chatsSubject.asObservable();

  private stompClient?: Client;
  connected = false;
  private stompSubscription?: StompSubscription;

  connect(rideMatchId: number) {
    // Leere aktuelle Liste beim Connect einer neuen Fahrt
    this.chatsSubject.next([]);

    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        this.connected = true;
        // Vorherige Subscription aufräumen!
        if (this.stompSubscription) {
          try {
            this.stompSubscription.unsubscribe();
          } catch (e) {
            console.warn('Fehler beim Unsubscribe:', e);
          }
        }
        // Nur EIN subscribe aufs Topic!
        this.stompSubscription = this.stompClient!.subscribe(
            `/topic/messages/${rideMatchId}`,
            (message: Message) => {
              const data = JSON.parse(message.body);
              this.addOrUpdateMessage(data);
            }
        );
      }
    });
    this.stompClient.activate();
  }

  disconnect() {
    if (this.stompSubscription) {
      try {
        this.stompSubscription.unsubscribe();
      } catch (e) {
        console.warn('Fehler beim Unsubscribe:', e);
      }
      this.stompSubscription = undefined;
    }
    this.stompClient?.deactivate();
    this.connected = false;
    this.chatsSubject.next([]);
  }

  sendMessage(chat: Chat) {
    console.log('sendMessage aufgerufen', chat.message);
    if (!this.stompClient?.connected) return;
    this.stompClient.publish({
      destination: `/app/sendMessage/${chat.rideMatchId}`,
      body: JSON.stringify(chat)
    });
  }

  editMessage(chatId: number, newMessage: string, userId: number, rideMatchId: number) {
    if (!this.stompClient?.connected) return;
    this.stompClient.publish({
      destination: `/app/editMessage/${rideMatchId}`,
      body: JSON.stringify({
        chatId,
        newMessage,
        userId
      })
    });
  }

  deleteMessage(chatId: number, userId: number, rideMatchId: number) {
    if (!this.stompClient?.connected) return;
    this.stompClient.publish({
      destination: `/app/deleteMessage/${rideMatchId}`,
      body: JSON.stringify({
        chatId,
        userId
      })
    });
  }

  markAsRead(chatId: number, rideMatchId: number) {
    if (!this.stompClient?.connected) return;
    this.stompClient.publish({
      destination: `/app/markAsRead/${rideMatchId}`,
      body: JSON.stringify(chatId)
    });
  }

  // Incoming Chat kann ein Chat-Objekt oder eine gelöschte Id sein (number)
  private addOrUpdateMessage(message: Chat | number) {
    let current = this.chatsSubject.getValue();
    if (typeof message === 'number') {
      // Entferne gelöschte Nachricht
      current = current.filter(chat => chat.id !== message);
      this.chatsSubject.next(current);
      return;
    }
    // Hinzufügen oder updaten
    const idx = current.findIndex(chat => chat.id === message.id);
    if (idx >= 0) current[idx] = message;
    else current.push(message);
    this.chatsSubject.next([...current]);
  }
}
