import {HttpClient} from '@angular/common/http';
import {Component, OnInit, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {RideMatchVO} from '../../../models/RideMatchVO';
import {MapComponent} from '../../map/map.component';
import SockJS from 'sockjs-client';
import {Client, Message} from '@stomp/stompjs';
import {SimulationStatus} from '../../../models/SimulationStatus';
import {NgIf} from '@angular/common';
import {SimulationMessageType} from '../../../models/SimulationMessageType';
import {PaymentDialogComponent} from '../payment-dialog/payment-dialog.component';
import {UserRole} from '../../../models/UserRole';
import {RequestPaymentDialogComponent} from '../request-payment-dialog/request-payment-dialog.component';
import {RatingDialogComponent} from '../rating-dialog/rating-dialog.component';
import {UserService} from '../../../services/UserService';
import {LatLng} from '../../../models/LatLng';
import {IncomingSimulationMessage} from '../../../models/IncomingSimulationMessage';
import {OutgoingSimulationMessage} from '../../../models/OutgoingSimulationMessage';
import {PaymentStatus} from '../../../models/PaymentStatus';
import {ChatComponent} from '../../chat/chat.component';

@Component({
  selector: 'app-ride-simulation',
  imports: [
    FormsModule,
    MapComponent,
    NgIf,
    PaymentDialogComponent,
    RequestPaymentDialogComponent,
    RatingDialogComponent,
    ChatComponent,
  ],
  templateUrl: './ride-simulation.component.html',
  styleUrl: './ride-simulation.component.scss',
})
export class RideSimulationComponent implements OnInit {
  simulationSpeed = 10;

  rideData!: RideMatchVO;
  currentUserId: number =-1 // Joseph //Standardwert weil number nie null sein darf

  private stompClient!: Client;
  showChat = true;//Joseph checkt wann chat erscheinen soll oder nicht
  showPaymentDialog = false;
  showRequestDialog = false;
  showRatingDialog = false;

  rideDataLoaded = false;
  viewInitialized = false;

  controlCommand: 'start' | 'pause' | null = null;
  currentSimulationIndex = 0;
  isSimulationRunning = false;
  role = localStorage.getItem('role');

  startPoint!: LatLng;
  routePoints: LatLng[] = [];

  @ViewChild(MapComponent) mapComponent!: MapComponent;

  constructor(
    private router: Router,
    private http: HttpClient,
  ) {}

  ngOnInit() {
    const username = localStorage.getItem('username');
    /**
     * Joseph
     */
    console.log("LocalStorage userId:", localStorage.getItem('userId'));
    const userIdFromStorage = localStorage.getItem('userId');
    this.currentUserId = userIdFromStorage ? Number(userIdFromStorage) : -1;
    //Ende

    if (username) {
      this.http
        .get<RideMatchVO>('http://localhost:8080/simulation/match/' + username)
        .subscribe({
          next: (res) => {
            this.rideData = res;
            this.rideDataLoaded = true;
            this.startPoint = res.startPoint;
            this.connectWebSocket();
            if(res.simulationStatus === SimulationStatus.COMPLETED
            && res.paymentStatus === PaymentStatus.UNPAID){

              if(this.role === UserRole.KUNDE){
                this.showPaymentDialog = true;
              }

            }
          },
          error: (err) => {
            console.error('Error:', err.error);
            alert('No ride match found!');
          },
        });
    } else {
      console.warn('No username in local storage found');
    }
  }
  connectWebSocket() {
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        this.stompClient.subscribe(
          '/topic/simulation/' + this.rideData.id,
          (message: Message) => {
            const msg = JSON.parse(message.body) as IncomingSimulationMessage;
            console.log('Received message:', msg);

            switch (msg.type) {
              case 'SPEED_CHANGE':
                if (msg.speed != this.simulationSpeed) {
                  this.simulationSpeed = msg.speed;
                  this.controlCommand = 'pause';
                  setTimeout(() => {
                    this.controlCommand = 'start';
                  });
                }
                break;

              case 'INDEX_CHANGE':
                this.currentSimulationIndex = msg.currentSimulationIndex;
                this.simulationSpeed = msg.speed;

                if (msg.simulationStatus === SimulationStatus.IN_PROGRESS) {
                  this.controlCommand = 'start';
                } else if (msg.simulationStatus === SimulationStatus.PAUSED) {
                  this.controlCommand = 'pause';
                } else if (
                  msg.simulationStatus === SimulationStatus.COMPLETED
                ) {
                  const role = localStorage.getItem('role');
                  if (role === UserRole.FAHRER) {
                    this.showRequestDialog = true;
                  }
                }
                break;

              case 'UPDATE_ROUTE':
                this.rideData = {...msg.rideMatchVO};
                break;

              case 'REQUEST_PAYMENT':
                const role = localStorage.getItem('role');
                if (role === UserRole.KUNDE) {
                  this.showPaymentDialog = true;
                }
                break;

              case 'COMPLETE':
                if (!this.showPaymentDialog) {
                  this.showRatingDialog = true;
                }
                break;

              default:
                console.warn('Unknown message type:', msg);
            }
          }
        );
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
      },
    });

    console.log('Activating WebSocket connection...');
    this.stompClient.activate();
  }

  startSimulation() {
    this.http
      .put('http://localhost:8080/simulation/start/' + this.rideData.id, {})
      .subscribe({
        next: () => {
          console.log('Start command sent to backend'),
            this.showChat = true; //Joseph von Anfang muss Chat da sein
        },
        error: (err) => console.error('Failed to start:', err),
      });
  }

  pauseSimulation() {
    this.http
      .put('http://localhost:8080/simulation/pause', {
        rideMatchId: this.rideData.id,
        currentSimulationIndex: this.currentSimulationIndex,
      })
      .subscribe({
        next: () => console.log('Pause command sent to backend'),
        error: (err) => console.error('Failed to pause:', err),
      });
  }

  resumeSimulation() {
    this.http
      .put('http://localhost:8080/simulation/resume/' + this.rideData.id, {})
      .subscribe({
        next: () => {
          console.log('Resume command sent to backend');
          this.controlCommand = 'start';
        },
        error: (err) => console.error('Failed to resume:', err),
      });
  }

  onSpeedChange() {
    if (this.stompClient && this.stompClient.connected) {
      const message: OutgoingSimulationMessage = {
        rideMatchId: this.rideData.id,
        speed: this.simulationSpeed,
        type: SimulationMessageType.SPEED_CHANGE,
      };
      this.stompClient.publish({
        destination: '/app/simulation/speed',
        body: JSON.stringify(message),
      });
    }
  }

  onSimulationCompleted(finalRoute: RideMatchVO) {

    this.http
      .put('http://localhost:8080/simulation/complete', finalRoute)
      .subscribe({
        next: () => console.log('Backend notified of simulation completion'),
        error: (err) => console.error('Error notifying backend:', err),
      });
  }

  onPaymentCompleted() {
    this.showPaymentDialog = false;
    this.showRatingDialog = true;
  }

  onRatingSubmitted() {
    this.showRatingDialog = false;
    this.router.navigate(['/dashboard']);
  }

  onSimulationStateChanged(running: boolean) {
    this.isSimulationRunning = running;
  }

  onRoutePointsChanged(points: L.LatLng[]){
    this.routePoints = points.map(p => ({
      lat: p.lat,
      lng: p.lng
    }));
  }

  ngOnDestroy() {
    if (this.stompClient && this.stompClient.active) {
      this.stompClient.deactivate();
    }
  }

  changeEndPoint() {
    this.mapComponent.changeEndpoint();
  }

  addStopover() {
    this.mapComponent.addStopover();
  }

  deleteStopover() {
    this.mapComponent.deleteStopover();
  }
}
