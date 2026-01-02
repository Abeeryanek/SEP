import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RideOffer } from '../../models/RideOffer';
import { RideOfferService } from '../../services/rideoffer.service';
import { NotificationService } from '../../services/notification.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-kunde-ride-offers',
  standalone: true,
  templateUrl: './kunde-ride-offers.component.html',
  styleUrls: ['./kunde-ride-offers.component.scss'],
  imports: [CommonModule]
})
export class KundeRideOffersComponent implements OnInit, OnDestroy {
  rideOffers: RideOffer[] = [];
  message: string = '';
  statusMessage: string = '';
  sortColumn: string = 'rating';
  sortAsc: boolean = false;
  notification: string = '';

  private notificationSub?: Subscription;

  constructor(
    private rideOfferService: RideOfferService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.notificationService.connect();
    this.notificationSub = this.notificationService.notifications$.subscribe((rideOffer: RideOffer) => {
      this.notification =
        `Neues Fahrtangebot von ${rideOffer.driverName}! Bewertung: ${rideOffer.rating}, Fahrten: ${rideOffer.totalTrips}, Gesamtdistanz: ${rideOffer.drivenDistance} km.`;
      this.getAllPendingRideOffers();
      setTimeout(() => this.notification = '', 10000);
    });
    this.getAllPendingRideOffers();
  }

  ngOnDestroy(): void {
    this.notificationSub?.unsubscribe();
    this.notificationService.disconnect();
  }

  getAllPendingRideOffers() {
    const customerUserName = localStorage.getItem('username')!;
    this.rideOfferService.getAllPendingOffersForCustomer(customerUserName).subscribe({
      next: (offers: RideOffer[]) => {
        this.rideOffers = offers;
        this.message = `Sie haben ${offers.length} offene Fahrtangebote`;
      },
      error: (err) => {
        this.message = err?.error?.message || 'Fehler beim Laden der Fahrtangebote.';
        this.rideOffers = [];
      }
    });
  }

  sortBy(column: string) {
    if (this.sortColumn === column) {
      this.sortAsc = !this.sortAsc;
    } else {
      this.sortColumn = column;
      this.sortAsc = false;
    }
  }

  getSortedOffers(): RideOffer[] {
    if (!this.rideOffers) return [];
    return [...this.rideOffers].sort((a, b) => {
      let valA: any = a[this.sortColumn as keyof RideOffer];
      let valB: any = b[this.sortColumn as keyof RideOffer];
      if (typeof valA === 'string') valA = valA.toLowerCase();
      if (typeof valB === 'string') valB = valB.toLowerCase();
      if (valA < valB) return this.sortAsc ? -1 : 1;
      if (valA > valB) return this.sortAsc ? 1 : -1;
      return 0;
    });
  }

  acceptPendingOffer(rideOffer: RideOffer) {
    this.rideOfferService.acceptPendingOffer(rideOffer.id).subscribe({
      next: () => {
        this.statusMessage = "Sie haben ein Fahrtangebot akzeptiert.";
        this.getAllPendingRideOffers();
      },
      error: (err) => {
        this.statusMessage = err?.error?.message || 'Fehler beim Annehmen des Fahrtangebots.';
      }
    });
  }

  rejectPendingOffer(rideOffer: RideOffer) {
    this.rideOfferService.rejectPendingOffer(rideOffer.id, rideOffer.customerId).subscribe({
      next: () => {
        this.statusMessage = "Sie haben das Fahrtangebot abgelehnt.";
        this.getAllPendingRideOffers();
      },
      error: (err) => {
        this.statusMessage = err?.error?.message || 'Fehler beim Ablehnen des Fahrtangebots.';
      }
    });
  }
}
