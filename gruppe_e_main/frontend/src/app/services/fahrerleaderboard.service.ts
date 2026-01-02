import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Fahrleaderboard} from '../models/Fahrerleaderboard';

@Injectable({
  providedIn: 'root'
})
export class FahrerleaderboardService {
  constructor(private http: HttpClient) {}

  getFahrtleaderboard (){
    return this.http.get<Fahrleaderboard[]>('http://localhost:8080/fahrleaderboard')
  }
}
