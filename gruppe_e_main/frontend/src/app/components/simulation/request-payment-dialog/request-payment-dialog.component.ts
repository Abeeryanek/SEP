import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-request-payment-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './request-payment-dialog.component.html',
  styleUrl: './request-payment-dialog.component.scss'
})
export class RequestPaymentDialogComponent {

  @Input() ridePrice!: number;
  @Input() rideId!: number;
  @Output() paymentRequested = new EventEmitter<void>();
  requestSent = false;

  constructor(private http: HttpClient) {}

  onRequestPayment(){

    this.http.put('http://localhost:8080/simulation/requestPayment/' + this.rideId, {}).subscribe({
      next: () => {
        console.log('Payment request successful!');
        this.requestSent = true;

        setTimeout(() => {
          this.requestSent = false;
          this.paymentRequested.emit();
        }, 3000);
      },
      error: (err) => {
        console.error('Payment request failed:', err);
        alert('Payment request failed!');
      }
    });
  }

}
