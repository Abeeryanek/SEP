import {OfferStatus} from './OfferStatus';

export interface RideOffer {
  id: number;
  driverName: string;
  driverId: number;
  rideRequestId: number;
  rating: number;
  totalTrips: number;
  drivenDistance: number;
  customerId: number;
  status: OfferStatus;
}
