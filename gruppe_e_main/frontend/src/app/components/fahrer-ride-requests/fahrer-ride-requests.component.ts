import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AddressValidationService, AddressSuggestion } from '../../services/address-validation.service';
import {RoutePlan} from '../../models/RoutePlan';
import {RideOffer} from '../../models/RideOffer';
import {RideOfferService} from '../../services/rideoffer.service';

interface FahrerRideRequest {
  id: number;
  createdAt: string;
  startLat: number;
  startLng: number;
  customerName: string;
  customerRating: number;
  vehicleClass: string;
  distance?: number;  // Optional distance field
  //Fahrtplanung-- CHI
  totalDistanceKm: number;
  durationMin: number;
  price: number;
  //Fahrtplanung-- CHI
  hasBeenAccepted: boolean;
}

@Component({
  selector: 'app-fahrer-ride-requests',
  templateUrl: './fahrer-ride-requests.component.html',
  styleUrls: ['./fahrer-ride-requests.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class FahrerRideRequestsComponent implements OnInit {
  rideRequests: FahrerRideRequest[] = [];
  errorMsg: string = '';

  // Fahrer-Position
  positionType: 'geo' | 'address' | 'poi' | 'coords' = 'geo';
  driverLat: number = 52.52;
  driverLng: number = 13.405;
  driverAddress: string = '';
  driverPOI: string = '';

  // Vorschläge
  addressSuggestions: AddressSuggestion[] = [];
  poiSuggestions: AddressSuggestion[] = [];

  // Sortierung
  sortColumn: string = 'id';
  sortAsc: boolean = true;

  // API-URL
  apiUrl = 'http://localhost:8080/api/ride-requests/open';

  rideOffer: RideOffer | null = null; //joseph

  constructor(private addressValidationService: AddressValidationService, private http: HttpClient, private rideOfferService: RideOfferService) {}

  ngOnInit() {
    this.loadRideRequests();
    this.getPendingRideOffer(); //joseph
  }

  loadRideRequests() {
    this.http.get<FahrerRideRequest[]>(this.apiUrl, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    }).subscribe({
      next: (requests) => {
        this.rideRequests = requests;
        // Berechne die Entfernungen für jede Anfrage
        this.rideRequests.forEach(request => {
          request.distance = this.calcDistance(
            this.driverLat,
            this.driverLng,
            request.startLat,
            request.startLng
          );
        });
        this.errorMsg = '';
      },
      error: (error) => {
        console.error('Fehler beim Laden der Fahranfragen:', error);
        this.errorMsg = 'Fehler beim Laden der Fahranfragen. Bitte versuchen Sie es später erneut.';
        this.rideRequests = [];
      }
    });
  }

  recalculateDistances() {
    this.rideRequests.forEach(request => {
      request.distance = this.calcDistance(
        this.driverLat,
        this.driverLng,
        request.startLat,
        request.startLng
      );
    });
  }

  setPositionByGeolocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(pos => {
        this.driverLat = pos.coords.latitude;
        this.driverLng = pos.coords.longitude;
        this.recalculateDistances();
      });
    } else {
      alert('Geolocation wird nicht unterstützt.');
    }
  }

  onAddressInput(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    if (value.length > 2) {
      this.addressValidationService.validateAddress(value).subscribe(suggestions => {
        this.addressSuggestions = suggestions;
      });
    } else {
      this.addressSuggestions = [];
    }
  }

  selectAddressSuggestion(suggestion: AddressSuggestion) {
    this.driverAddress = suggestion.display_name;
    this.driverLat = suggestion.lat;
    this.driverLng = suggestion.lon;
    this.addressSuggestions = [];
    this.recalculateDistances();
  }

  onPOIInput(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    if (value.length > 2) {
      this.addressValidationService.searchPOI(value).subscribe(suggestions => {
        this.poiSuggestions = suggestions;
      });
    } else {
      this.poiSuggestions = [];
    }
  }

  selectPOISuggestion(suggestion: AddressSuggestion) {
    this.driverPOI = suggestion.display_name;
    this.driverLat = suggestion.lat;
    this.driverLng = suggestion.lon;
    this.poiSuggestions = [];
    this.recalculateDistances();
  }

  setPositionManual(lat: string, lng: string) {
    this.driverLat = parseFloat(lat);
    this.driverLng = parseFloat(lng);
    this.recalculateDistances();
  }

  // Haversine-Formel zur Entfernungsberechnung
  calcDistance(lat1: number, lng1: number, lat2: number, lng2: number): number {
    const R = 6371; // km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLng = (lng2 - lng1) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
              Math.sin(dLng/2) * Math.sin(dLng/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
  }

  getSortedRequests(): FahrerRideRequest[] {
    const sorted = [...this.rideRequests].sort((a, b) => {
      let valA: any = a[this.sortColumn as keyof FahrerRideRequest];
      let valB: any = b[this.sortColumn as keyof FahrerRideRequest];
      if (this.sortColumn === 'createdAt') {
        valA = new Date(valA).getTime();
        valB = new Date(valB).getTime();
      }
      if (valA < valB) return this.sortAsc ? -1 : 1;
      if (valA > valB) return this.sortAsc ? 1 : -1;
      return 0;
    });
    return sorted;
  }

  sortBy(column: string) {
    if (this.sortColumn === column) {
      this.sortAsc = !this.sortAsc;
    } else {
      this.sortColumn = column;
      this.sortAsc = true;
    }
  }

  //Alle 3 Methoden gehören Joseph
  createRideOffer(requestId: number) {
    const driverUserName = localStorage.getItem("username");
    this.rideOfferService.createRideOffer(requestId, driverUserName!).subscribe({
      next: (rideOffer: RideOffer)=>{
        console.log(rideOffer);
        this.ngOnInit();
      },
      error: (err) => {
        alert(err.error.message);
      }
    })
  }

  withDrawRideOffer(offerId: number) {
    const driverUserName = localStorage.getItem("username");
    this.rideOfferService.withDrawRideOffer(offerId, driverUserName!).subscribe({
      next:()=>{
        this.ngOnInit();
      },
      error: (err)=>{
        alert(err.error.message);
      }
    })
  }

  getPendingRideOffer() {
    const driverUserName = localStorage.getItem('username');
    this.rideOfferService.getPendingOffer(driverUserName!).subscribe({
      next: (rideOffer: RideOffer) => {
        this.rideOffer = rideOffer;
      },
      error: () => {
        this.rideOffer = null;
      }
    });
  }

}
