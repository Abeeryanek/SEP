import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import {Router, RouterModule} from '@angular/router';
import {RoutePlan} from '../models/RoutePlan';
import {RouteService} from '../services/route.service';
import {MapComponent} from '../components/map/map.component';

interface LocationInput {
  type: 'geo' | 'address' | 'poi' | 'coords';
  address?: string;
  poi?: string;
  lat?: number;
  lng?: number;
}

interface RideRequest {
  id?: number;
  start?: LocationInput;
  destination?: LocationInput;
  stopovers?: LocationInput[];
  vehicleClass: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
  routePlan: RoutePlan | null;
}

@Component({
  selector: 'app-active-ride',
  standalone: true,
  templateUrl: './active-ride.component.html',
  styleUrls: ['./active-ride.component.scss'],
  imports: [CommonModule, RouterModule, MapComponent]
})
export class ActiveRideComponent implements OnInit {
  apiUrl = 'http://localhost:8080/api/ride-requests/active';
  activeRequest: RideRequest | null = null;
  errorMsg = '';
  successMsg = '';
  showMap = false;

  constructor(private http: HttpClient,
              private routeService: RouteService,   //Fahrtplanung
              private router: Router) {}

  ngOnInit() {
    this.loadActiveRequest();
  }

  loadActiveRequest() {
    this.http.get<RideRequest>(this.apiUrl, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    }).subscribe({
      next: req => {
        if (!req) {
          this.activeRequest = null;
          return;
        }
        // Felder parsen, falls sie als String vorliegen
        let start = req.start;
        let destination = req.destination;
        let stopovers = req.stopovers;
        if (typeof start === 'string') {
          try { start = JSON.parse(start); } catch {}
        }
        if (typeof destination === 'string') {
          try { destination = JSON.parse(destination); } catch {}
        }
        if (typeof stopovers === 'string') {
          try { stopovers = JSON.parse(stopovers); } catch {}
        }
        this.activeRequest = { ...req, start, destination, stopovers };
      },
      error: () => {
        this.activeRequest = null;
      }
    });
  }

  deleteRequest() {
    if (!this.activeRequest?.id) return;
    this.http.delete(`http://localhost:8080/api/ride-requests/${this.activeRequest.id}`, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    }).subscribe({
      next: () => {
        this.successMsg = 'Fahranfrage gelöscht.';
        this.activeRequest = null;
      },
      error: () => {
        this.errorMsg = 'Fehler beim Löschen der Fahranfrage.';
      }
    });
  }

  completeRequest() {
    if (!this.activeRequest?.id) return;
    this.http.post(`http://localhost:8080/api/ride-requests/complete/${this.activeRequest.id}`, {}, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    }).subscribe({
      next: () => {
        this.successMsg = 'Fahranfrage abgeschlossen.';
        this.loadActiveRequest();
      },
      error: () => {
        this.errorMsg = 'Fehler beim Abschließen der Fahranfrage.';
      }
    });
  }

  getLocationString(location?: LocationInput): string {
    if (!location) return '';
    if (location.type === 'address') return location.address || '';
    if (location.type === 'poi') return location.poi || '';
    if (location.type === 'coords' || location.type === 'geo') {
      return (location.lat !== undefined && location.lng !== undefined)
        ? `${location.lat}, ${location.lng}`
        : '';
    }
    return '';
  }

  //Fahrtplanung
  showRouteOnMap() {
    this.showMap = true;
  }

  closeMap() {
    this.showMap = false;
  }
}
