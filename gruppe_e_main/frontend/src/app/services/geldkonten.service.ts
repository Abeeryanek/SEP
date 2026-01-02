import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GeldkontenService {
  constructor(private http: HttpClient) {}

  recharge(username: string, amount: number): Observable<any> {
    const body = { username, amount };
    return this.http.post('http://localhost:8080/api/transactions/recharge', body);
  }
}
