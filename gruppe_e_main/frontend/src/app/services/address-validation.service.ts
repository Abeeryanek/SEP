import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface AddressSuggestion {
  display_name: string;
  lat: number;
  lon: number;
  type: string;
  category?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AddressValidationService {
  private readonly NOMINATIM_URL = 'https://nominatim.openstreetmap.org/search';
  private readonly GERMANY_BOUNDS = {
    minLat: 47.270111,
    maxLat: 55.058383,
    minLon: 5.866342,
    maxLon: 15.041896
  };

  constructor(private http: HttpClient) {}

  validateAddress(address: string): Observable<AddressSuggestion[]> {
    const params = {
      q: address,
      countrycodes: 'de',
      format: 'json',
      limit: '5',
      addressdetails: '1',
      'accept-language': 'de'
    };

    return this.http.get<any[]>(this.NOMINATIM_URL, { params }).pipe(
      map(results => results
        .filter(result => this.isInGermany(result.lat, result.lon))
        .map(result => ({
          display_name: result.display_name,
          lat: parseFloat(result.lat),
          lon: parseFloat(result.lon),
          type: result.type
        }))
      )
    );
  }

  searchPOI(query: string): Observable<AddressSuggestion[]> {
    const params = {
      q: query,
      countrycodes: 'de',
      format: 'json',
      limit: '5',
      addressdetails: '1',
      'accept-language': 'de'
    };

    return this.http.get<any[]>(this.NOMINATIM_URL, { params }).pipe(
      map(results => results
        .filter(result => this.isInGermany(result.lat, result.lon))
        .map(result => ({
          display_name: result.display_name,
          lat: parseFloat(result.lat),
          lon: parseFloat(result.lon),
          type: result.type,
          category: result.class
        }))
      )
    );
  }

  private isInGermany(lat: number, lon: number): boolean {
    return lat >= this.GERMANY_BOUNDS.minLat &&
           lat <= this.GERMANY_BOUNDS.maxLat &&
           lon >= this.GERMANY_BOUNDS.minLon &&
           lon <= this.GERMANY_BOUNDS.maxLon;
  }
} 