
import { RideMatchVO } from "./RideMatchVO";
import { SimulationStatus } from "./SimulationStatus";

export type IncomingSimulationMessage =
  | SimulationSpeedMessage
  | SimulationIndexMessage
  | PaymentRequestMessage
  | PaymentCompletedMessage
  | RouteUpdateMessage;

export interface SimulationSpeedMessage {
  type: 'SPEED_CHANGE';
  rideMatchId: number;
  speed: number;
}

export interface SimulationIndexMessage {
  type: 'INDEX_CHANGE';
  rideMatchId: number;
  currentSimulationIndex: number;
  simulationStatus: SimulationStatus;
  speed: number;
}

export interface PaymentRequestMessage {
  type: 'REQUEST_PAYMENT';
  rideMatchId: number;
  amount: number;
}

export interface PaymentCompletedMessage {
  type: 'COMPLETE';
  rideMatchId: number;
}

export interface RouteUpdateMessage {
  type: 'UPDATE_ROUTE';
  rideMatchId: number;
  rideMatchVO: RideMatchVO
}
