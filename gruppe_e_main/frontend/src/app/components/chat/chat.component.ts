import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { Chat } from '../../models/chat.model';
import { ChatService } from '../../services/chat.service';
import { RideMatchVO } from '../../models/RideMatchVO';
import { Subscription } from 'rxjs';
import { FormsModule } from "@angular/forms";
import { NgClass, NgIf, NgForOf } from "@angular/common";

@Component({
  selector: 'app-chat',
  templateUrl: 'chat.component.html',
  styleUrls: ['./chat.component.css'],
  imports: [
    NgClass, NgIf, NgForOf, FormsModule
  ],
  standalone: true
})
export class ChatComponent implements OnInit, OnDestroy {
  @Input() rideMatch!: RideMatchVO;  // Fahrt-VO mit customer/driver
  @Input() myUserId!: number;

  chats: Chat[] = [];
  messageText: string = '';
  private chatSub!: Subscription;

  constructor(public chatService: ChatService) {}

  ngOnInit(): void {
    console.log('ChatComponent initialisiert mit:', this.rideMatch);
    if (this.rideMatch?.id && this.myUserId > 0) {
      this.chatService.connect(this.rideMatch.id); // Verbinde WS für Fahrt
    }
    this.chatSub = this.chatService.chats$.subscribe(
      chats => this.chats = chats
    );
  }

  ngOnDestroy(): void {
    this.chatService.disconnect();
    if (this.chatSub) this.chatSub.unsubscribe();
  }

  sendMessage(): void {
    if (!this.messageText.trim()) return;
    const chat: Chat = {
      id: 0,
      message: this.messageText,
      senderId: this.myUserId,
      receiverId: this.getReceiverId(),
      rideMatchId: this.rideMatch.id,
      isRead: false,
    };
    this.chatService.sendMessage(chat);
    this.messageText = '';
  }

  editMessage(chat: Chat): void {
    if (chat.senderId !== this.myUserId || chat.isRead) return;
    const newMsg = prompt('Editiere Nachricht:', chat.message);
    if (newMsg && newMsg.trim()) {
      this.chatService.editMessage(chat.id, newMsg, this.myUserId, this.rideMatch.id);
    }
  }

  deleteMessage(chat: Chat): void {
    if (chat.senderId !== this.myUserId || chat.isRead) return;
    this.chatService.deleteMessage(chat.id, this.myUserId, this.rideMatch.id);
  }

  markAsRead(chat: Chat): void  {
    if (chat.receiverId === this.myUserId && !chat.isRead) {
      chat.isRead = true;
      this.chatService.markAsRead(chat.id, this.rideMatch.id);
    }
  }

  getReceiverId(): number {
    if (!this.rideMatch || !this.rideMatch.customer || !this.rideMatch.driver) return -1;
    const customerId = this.rideMatch.customer.id;
    const driverId = this.rideMatch.driver.id;
    return (this.myUserId === customerId) ? driverId : customerId;
  }
}
