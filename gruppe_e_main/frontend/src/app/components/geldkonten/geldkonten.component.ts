import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { GeldkontenService } from '../../services/geldkonten.service';

@Component({
  selector: 'app-geldkonten',
  standalone: true,
  templateUrl: './geldkonten.component.html',
  styleUrl: './geldkonten.component.scss',
  imports: [FormsModule, CommonModule]
})
export class GeldkontenComponent {
  error = '';
  money: number | ''='';
  username: string = localStorage.getItem('username') || '';
  kontostand: number=0;


  constructor(private geldkontenService: GeldkontenService) {}
  geldAufladen() {
    if (!this.money) {
      this.error = 'Bitte geben Sie einen gültigen Betrag ein!';
      return;
    }
    this.error = '';
    this.geldkontenService.recharge(this.username, this.money).subscribe({
      next: (response) => {
        alert(response.message);
        this.money = '';
        this.kontostand = response.balance;
      },
      error: (err) => {
        if (err.error && err.error.details) {
          this.error = err.error.details;
        }
      }
    });
  }
}
