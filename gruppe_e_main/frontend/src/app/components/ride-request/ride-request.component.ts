import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {Router, RouterModule} from '@angular/router';
import { AddressValidationService, AddressSuggestion } from '../../services/address-validation.service';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import {RoutePlan} from '../../models/RoutePlan';
import {RouteService} from '../../services/route.service';
import {MapComponent} from '../map/map.component';
import {latLng} from 'leaflet';

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
  routePlan?: RoutePlan;  //Fahrtplanung
}

@Component({
  selector: 'app-ride-request',
  templateUrl: './ride-request.component.html',
  styleUrls: ['./ride-request.component.scss'],
  imports: [CommonModule, FormsModule, RouterModule, MapComponent]
})
export class RideRequestComponent implements OnInit {
  // API-URL
  apiUrl = 'http://localhost:8080/api/ride-requests';

  // Formularfelder
  start: LocationInput = { type: 'address', address: '' };
  destination: LocationInput = { type: 'address', address: '' };
  stopovers: LocationInput[] = [];
  vehicleClass = 'KLEIN';

  // Adressvalidierung
  startSuggestions: AddressSuggestion[] = [];
  destinationSuggestions: AddressSuggestion[] = [];
  private startAddressSubject = new Subject<string>();
  private destinationAddressSubject = new Subject<string>();

  // POI Vorschläge
  startPOISuggestions: AddressSuggestion[] = [];
  destinationPOISuggestions: AddressSuggestion[] = [];
  private startPOISubject = new Subject<string>();
  private destinationPOISubject = new Subject<string>();

  // State
  errorMsg = '';
  successMsg = '';
  allRequests: RideRequest[] = [];
  showModal = false;
  showMap = false;

  //Fahrtplanung
  routePlan: RoutePlan = {};


  @ViewChild('modal') modalRef!: ElementRef;

  constructor(
    private http: HttpClient,
    private addressValidationService: AddressValidationService,
    private routeService: RouteService,
    private router: Router
  ) {
    // Startpunkt Adressvalidierung
    this.startAddressSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(address => {
      if (address && address.length > 2) {
        this.validateStartAddress(address);
      } else {
        this.startSuggestions = [];
      }
    });

    // Zielpunkt Adressvalidierung
    this.destinationAddressSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(address => {
      if (address && address.length > 2) {
        this.validateDestinationAddress(address);
      } else {
        this.destinationSuggestions = [];
      }
    });

    // Startpunkt POI Validierung
    this.startPOISubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(query => {
      if (query && query.length > 2) {
        this.searchStartPOI(query);
      } else {
        this.startPOISuggestions = [];
      }
    });

    // Zielpunkt POI Validierung
    this.destinationPOISubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(query => {
      if (query && query.length > 2) {
        this.searchDestinationPOI(query);
      } else {
        this.destinationPOISuggestions = [];
      }
    });
  }

  ngOnInit() {
    this.loadAllRequests();
  }

  onStartAddressChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.startAddressSubject.next(input.value);
  }

  onDestinationAddressChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.destinationAddressSubject.next(input.value);
  }

  onStartPOIChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.startPOISubject.next(input.value);
  }

  onDestinationPOIChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.destinationPOISubject.next(input.value);
  }

  validateStartAddress(address: string) {
    this.addressValidationService.validateAddress(address)
      .subscribe({
        next: (suggestions) => {
          this.startSuggestions = suggestions;
        },
        error: (error) => {
          console.error('Fehler bei der Adressvalidierung:', error);
          this.startSuggestions = [];
        }
      });
  }

  validateDestinationAddress(address: string) {
    this.addressValidationService.validateAddress(address)
      .subscribe({
        next: (suggestions) => {
          this.destinationSuggestions = suggestions;
        },
        error: (error) => {
          console.error('Fehler bei der Adressvalidierung:', error);
          this.destinationSuggestions = [];
        }
      });
  }

  searchStartPOI(query: string) {
    this.addressValidationService.searchPOI(query)
      .subscribe({
        next: (suggestions) => {
          this.startPOISuggestions = suggestions;
        },
        error: (error) => {
          console.error('Fehler bei der POI-Suche:', error);
          this.startPOISuggestions = [];
        }
      });
  }

  searchDestinationPOI(query: string) {
    this.addressValidationService.searchPOI(query)
      .subscribe({
        next: (suggestions) => {
          this.destinationPOISuggestions = suggestions;
        },
        error: (error) => {
          console.error('Fehler bei der POI-Suche:', error);
          this.destinationPOISuggestions = [];
        }
      });
  }

  selectStartSuggestion(suggestion: AddressSuggestion) {
    this.start.address = suggestion.display_name;
    this.start.lat = suggestion.lat;
    this.start.lng = suggestion.lon;
    this.startSuggestions = [];
  }

  selectDestinationSuggestion(suggestion: AddressSuggestion) {
    this.destination.address = suggestion.display_name;
    this.destination.lat = suggestion.lat;
    this.destination.lng = suggestion.lon;
    this.destinationSuggestions = [];
  }

  selectStartPOI(suggestion: AddressSuggestion) {
    this.start.poi = suggestion.display_name;
    this.start.lat = suggestion.lat;
    this.start.lng = suggestion.lon;
    this.startPOISuggestions = [];
  }

  selectDestinationPOI(suggestion: AddressSuggestion) {
    this.destination.poi = suggestion.display_name;
    this.destination.lat = suggestion.lat;
    this.destination.lng = suggestion.lon;
    this.destinationPOISuggestions = [];
  }

  addStopover() {
    this.stopovers.push({ type: 'address', address: '' });
  }

  completeStopoverAddress(i: number, address: string) {
    this.routeService.convertAddress(address).subscribe({
      next:(result)=> {
        this.stopovers[i].address = result.display_name;
        this.stopovers[i].lat = result.lat;
        this.stopovers[i].lng = result.lng;
        this.errorMsg = '';
      },
      error:(err)=>{
        window.alert('Zwischenstopp ' + (i+1) + ': ' + err.message);

      }
    })
  }

  removeStopover(i: number) {
    this.stopovers.splice(i, 1);
  }

  useGeolocation(target: LocationInput) {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(pos => {
        target.lat = pos.coords.latitude;
        target.lng = pos.coords.longitude;
        target.type = 'geo';
        this.successMsg = 'Position übernommen!';
      }, () => {
        this.errorMsg = 'Geoposition konnte nicht ermittelt werden.';
      });
    } else {
      this.errorMsg = 'Geolocation wird nicht unterstützt.';
    }
  }

  createRequest() {
    // Validierung: Start und Ziel müssen ausgefüllt sein
    const isStartValid = this.isLocationFilled(this.start);
    const isDestinationValid = this.isLocationFilled(this.destination);
    if (!isStartValid || !isDestinationValid) {
      this.errorMsg = 'Bitte geben Sie mindestens eine Adresse für Start- und Zielpunkt an.';
      this.successMsg = '';
      return;
    }
    if (!this.start.lat || !this.start.lng || !this.destination.lat || !this.destination.lng) {
      this.errorMsg = 'Die Adresse wurde nicht richtig eingegeben. Bitte wählen Sie die Adresse aus der Vorschlagsbox aus.';
      this.successMsg = '';
      return;
    }
    const body: RideRequest = {
      start: this.start,
      destination: this.destination,
      stopovers: this.stopovers,
      vehicleClass: this.vehicleClass,
      //Fahrtplanung
      routePlan: {
        startPoint: {lat: this.start.lat!, lng: this.start.lng!},
        endPoint: {lat: this.destination.lat!, lng: this.destination.lng!},
        stopovers: this.stopovers
          .filter((stopover) => !!stopover.lat && !!stopover.lng)
          .map(stopover =>({
          lat: stopover.lat!,
          lng: stopover.lng!
        }))
      }
      //Fahrtplanung
    };
    this.http.post<RideRequest>(this.apiUrl, body, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    }).subscribe({
      next: req => {
        this.successMsg = 'Fahranfrage erfolgreich erstellt!';
        this.errorMsg = '';
        this.start = { type: 'address', address: '' };
        this.destination = { type: 'address', address: '' };
        this.stopovers = [];
        this.vehicleClass = 'KLEIN';
        this.routePlan = {}; //Fahrtplanung
        this.loadAllRequests();
      },
      error: err => {
        this.errorMsg = err.error?.message || 'Fehler beim Erstellen der Fahranfrage.';
        this.successMsg = '';
      }
    });
  }

  // Hilfsfunktion zur Validierung von LocationInput
  isLocationFilled(loc: LocationInput): boolean {
    if (!loc) return false;
    switch (loc.type) {
      case 'address':
        return !!loc.address && loc.address.trim().length > 0;
      case 'poi':
        return !!loc.poi && loc.poi.trim().length > 0;
      case 'coords':
      case 'geo':
        return typeof loc.lat === 'number' && typeof loc.lng === 'number';
      default:
        return false;
    }
  }

  getStopovers(): any[] {
    return Array.isArray(this.allRequests[0]?.stopovers) ? this.allRequests[0]!.stopovers! : [];
  }

  openModal() {
    this.showModal = true;
    this.loadAllRequests();
  }

  closeModal() {
    this.showModal = false;
  }

  loadAllRequests() {
    this.http.get<RideRequest[]>(this.apiUrl, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    }).subscribe({
      next: reqs => {
        // Felder parsen, falls sie als String vorliegen
        this.allRequests = reqs.map(req => {
          let start = req.start;
          let destination = req.destination;
          let stopovers = req.stopovers;
          let routePlan = req.routePlan;
          if (typeof start === 'string') {
            try { start = JSON.parse(start); } catch {}
          }
          if (typeof destination === 'string') {
            try { destination = JSON.parse(destination); } catch {}
          }
          if (typeof stopovers === 'string') {
            try { stopovers = JSON.parse(stopovers); } catch {}
          }
          return { ...req, start, destination, stopovers };
        });
      },
      error: () => this.allRequests = []
    });
  }

  deleteRequestById(id: number) {
    this.http.delete(`${this.apiUrl}/${id}`, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    }).subscribe({
      next: () => {
        this.loadAllRequests();
      },
      error: () => {
        this.errorMsg = 'Fehler beim Löschen der Fahranfrage.';
      }
    });
  }

  completeRequestById(id: number) {
    this.http.post(`${this.apiUrl}/complete/${id}`, {}, {
      headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
    }).subscribe({
      next: () => {
        this.loadAllRequests();
      },
      error: () => {
        this.errorMsg = 'Fehler beim Abschließen der Fahranfrage.';
      }
    });
  }

  getAbgeschlosseneRequests(): RideRequest[] {
    return this.allRequests.filter(r => r.status === 'ABGESCHLOSSEN');
  }

  getAktiveRequests(): RideRequest[] {
    return this.allRequests.filter(r => r.status === 'AKTIV');
  }

  hasActiveRequest(): boolean {
    return this.getAktiveRequests().length > 0;
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
  showRouteByCreatingRequest() {
    if (!this.start.lat || !this.start.lng || !this.destination.lat || !this.destination.lng) {
      this.errorMsg = 'Die Adresse wurde nicht richtig eingegeben. Bitte wählen Sie die Adresse aus der Vorschlagsbox aus.';
      this.successMsg = '';
      return;
    }
    const rideRequest: RideRequest = {
      vehicleClass: this.vehicleClass,
      routePlan: {
        startPoint: {lat: this.start.lat!, lng: this.start.lng!},
        endPoint: {lat: this.destination.lat!, lng: this.destination.lng!},
        stopovers: this.stopovers
          .filter((stopover) => !!stopover.lat && !!stopover.lng)
          .map(stopover =>({
            lat: stopover.lat!,
            lng: stopover.lng!
          }))
      }
    }
    this.routeService.addRoutePlanToRideRequest(rideRequest).subscribe({
      next: (updatedRideRequest: any)=>{
        this.routePlan = updatedRideRequest.routePlan!;
        this.showMap = true;
      },
      error:(error)=>{
        this.errorMsg = error;
        this.successMsg = '';
      }
    });
  }

  showRouteByHistory(request: RideRequest) {
    this.routePlan = request.routePlan!;
    this.showMap = true;
  }

  closeMap() {
    this.showMap = false;
  }
}
