import {Injectable} from '@angular/core';
import {LatLng} from '../models/LatLng';
import {RoutePlan} from '../models/RoutePlan';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom, map} from 'rxjs';
import {OutgoingSimulationMessage} from '../models/OutgoingSimulationMessage';
import {SimulationMessageType} from '../models/SimulationMessageType';
import {RideMatchVO} from '../models/RideMatchVO';

@Injectable({
  providedIn: 'root',
})
export class RouteService {

  constructor(private http: HttpClient) {
  }

  convertAddress(address: string) {
    const url = 'https://nominatim.openstreetmap.org/search?format=json&q=' + encodeURIComponent(address);

    return this.http.get<any[]>(url).pipe(
      map(results => {
        const first = results[0];
        if (!first) {
          throw new Error('Keine gültige Adresse');
        }
        return {
          display_name: first.display_name,
          lat: parseFloat(first.lat),
          lng: parseFloat(first.lon),
        };
      })
    );
  }



  addRoutePlanToRideRequest(rideRequest: any) {
    return this.http.post('http://localhost:8080/api/ride-requests/route-plan', rideRequest);
  }







}
