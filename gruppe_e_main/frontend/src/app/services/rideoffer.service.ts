import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RideOffer} from '../models/RideOffer';

@Injectable({
  providedIn: 'root',
})
export class RideOfferService {

  constructor(private http: HttpClient) {
  }

  createRideOffer(requestId: number, driverUserName: string) {
    return this.http.post<RideOffer>(`http://localhost:8080/api/rideoffer/create/${driverUserName}/${requestId}`, null);
  }

  withDrawRideOffer(offerId: number, driverUserName: string) {
    return this.http.delete(`http://localhost:8080/api/rideoffer/withdraw/${offerId}/${driverUserName}`);
  }

  getPendingOffer(driverUserName: string) {
    return this.http.get<RideOffer>(`http://localhost:8080/api/rideoffer/driver/${driverUserName}/pending-offer`);
  }

  getAllPendingOffersForCustomer(customerUserName: string) {
    return this.http.get<RideOffer[]>(`http://localhost:8080/api/rideoffer/customer/${customerUserName}/pending-offers`);
  }

  acceptPendingOffer(offerId: number) {
    return this.http.post<RideOffer>(`http://localhost:8080/api/rideoffer/accept/${offerId}`, null);
  }

  rejectPendingOffer(offerId: number, customerId: number) {
    return this.http.post(`http://localhost:8080/api/rideoffer/reject/${offerId}/${customerId}`, null);
  }
}
