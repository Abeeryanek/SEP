import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FahrthistorieService {
  constructor(private http: HttpClient) {}

  getFahrten(username: string) {
    return this.http.get<any[]>(`http://localhost:8080/fahrthistorie?username=${username}`);
  }
}
