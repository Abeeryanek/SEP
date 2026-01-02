import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {KundeComponent} from '../kunde/kunde.component';
import {FahrerComponent} from '../fahrer/fahrer.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  imports: [CommonModule, RouterModule, KundeComponent, FahrerComponent]
})
export class DashboardComponent {
  profileImageUrl: string = '';
  role: string | null = null;

  constructor(private router: Router) {
    this.role = localStorage.getItem('role');
  }

  navigateDashboard() {
    this.router.navigate(['/dashboard']);
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.router.navigate(['/login']);
  }

  // goToProfile() {
  //   this.router.navigate(['/kunde/profile']);
  // }

  goToMap() {
    this.router.navigate(['/map']);
  }

  goToRideRequest() {
    // Später: Route für Fahranfrage anpassen
    this.router.navigate(['/ride-request']);
  }

  goToActiveRide() {
    this.router.navigate(['/active-ride']);
  }

  goToFahrerRideRequests() {
    this.router.navigate(['/fahrer/ride-requests']);
  }

  goToKundeRideOffers() {
    this.router.navigate(['/kunde/ride-offers']);
  }

  goToGeldkonten() {
    this.router.navigate(['/geldkonten']);
  }

  goToFahrthistorie() {
    this.router.navigate(['/fahrthistorie']);
  }

  goToFahrerStatistiken() {
    this.router.navigate(['/fahrer/statistiken']);
  }

  goToFahrerLeaderBoard() {
    this.router.navigate(['/fahrerleaderboard'])
  }
}
