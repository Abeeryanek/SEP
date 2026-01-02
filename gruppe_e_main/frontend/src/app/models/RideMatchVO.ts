

import { LatLng } from "./LatLng";
import { PaymentStatus } from "./PaymentStatus";
import { SimulationStatus } from "./SimulationStatus";

//Joseph
export interface User {
  id: number;

}
export interface RideMatchVO {
    id: number;
    startPoint: LatLng;
    stopovers?: LatLng[],
    endPoint: LatLng;
    customer?: User; //Joseph
    driver?: User;//Joseph
    totalDistanceKm: number;
    totalDurationMin: number;
    expectedPrice: number;
    simulationStatus: SimulationStatus;
    paymentStatus: PaymentStatus;
    ratingCustomer: number | null;
    ratingDriver: number | null
}
