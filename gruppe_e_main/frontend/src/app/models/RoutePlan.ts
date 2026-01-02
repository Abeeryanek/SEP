import {LatLng} from './LatLng';

export interface RoutePlan{
  id?: number,
  startPoint?: LatLng,
  endPoint?: LatLng,
  stopovers?: LatLng[],
  totalDistanceKm?: number,
  durationMin?: number,
  price?: number
}
