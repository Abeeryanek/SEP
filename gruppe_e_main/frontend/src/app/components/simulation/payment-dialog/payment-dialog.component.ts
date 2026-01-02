import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-payment-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-dialog.component.html',
  styleUrl: './payment-dialog.component.scss'
})
export class PaymentDialogComponent {

  @Input() ridePrice!: number;
  @Input() rideId!: number;
  @Output() paymentCompleted = new EventEmitter<void>();

  constructor(private http: HttpClient,
              private router: Router) {}

  onPay(){
    this.http.put('http://localhost:8080/simulation/pay/' + this.rideId, {}).subscribe({
      next: () => {
        console.log('Payment successful!');
        this.paymentCompleted.emit();
      },
      error: (err) => {
        if(err.status === 402){
          alert('Insufficient balance: please recharge');
          this.router.navigate(['/geldkonten']);
        }else if(err.status === 400){
          alert('Ride not found!');
        }else{
          alert('Payment failed!');
        }
      }
    });
  }

}
