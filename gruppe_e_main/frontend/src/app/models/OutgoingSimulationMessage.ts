import { LatLng } from "./LatLng";

export type OutgoingSimulationMessage = 
| SpeedChangeMessage;

export interface SpeedChangeMessage {
    type: 'SPEED_CHANGE';
    rideMatchId: number;
    speed: number;
}