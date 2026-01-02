export interface Chat {
  id: number;
  message: string;
  senderId: number;
  receiverId: number;
  rideMatchId: number;      // <--- neu, Pflicht!
  isRead?: boolean;
  createdAt?: string;       // Optional, falls du Timestamps nutzt
  updatedAt?: string;
}

export interface User {
  id: number;
}
