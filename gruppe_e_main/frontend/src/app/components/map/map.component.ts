import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import {PointType} from '../../models/PointType';
import {NgIf} from '@angular/common';
import {LeafletMouseEvent, Util} from 'leaflet';
import {RouteService} from '../../services/route.service';
import {RideMatchVO} from '../../models/RideMatchVO';
import {HttpClient} from '@angular/common/http';
import 'leaflet.marker.slideto';
import {RoutePlan} from '../../models/RoutePlan';
import {Router} from '@angular/router';

declare let L: any;

declare module 'leaflet' {
  interface Marker {
    slideTo(latlng: L.LatLngExpression, options?: any): this;
  }
}
@Component({
  selector: 'app-map',
  imports: [NgIf],
  templateUrl: './map.component.html',
  standalone: true,
  styleUrl: './map.component.scss',
})
export class MapComponent
  implements AfterViewInit, OnDestroy, OnInit, OnChanges
{
  //Durchführung einer Fahrt
  @Input() rideData!: RideMatchVO;
  @Input() simulationMode = false;
  @Input() controlCommand!: 'start' | 'pause' | null;
  @Input() simulationSpeed: number = 10;
  @Input() simulationIndex: number = 0;
  @Output() simulationCompleted = new EventEmitter<RideMatchVO>();
  @Output() simulationStateChanged = new EventEmitter<boolean>();
  @Output() routePointsChanged = new EventEmitter<L.LatLng[]>();
  marker!: L.Marker;
  simulationTimer: any;
  simulatedRoutePoints: L.LatLng[] = [];
  isSimulationRunning = false;
  startMarker!: L.Marker;
  endMarker!: L.Marker;
  stopoverMarkers: L.Marker[] = [];
  nextStopoverIndex = 0;
  avgDistanceBetweenPoints = 0;
  route!: RideMatchVO;
  suppressNextRouteUpdate = false;
  currentPositions: L.LatLng[] = [];

  //Fahrtplanung
  map!: L.Map;
  control!: L.Control;
  startPoint: L.LatLng | null = null;
  endPoint: L.LatLng | null = null;
  stopovers: L.LatLng[] = [];
  currentType: PointType = PointType.STARTPOINT;
  @Input() routePlan: RoutePlan | null = null;
  @Input() showOnlyMode = false;
  protected readonly PointType = PointType;

  constructor(
    private routeService: RouteService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {}

  ngOnChanges(changes: SimpleChanges) {
    if (
      changes['rideData'] &&
      this.rideData?.startPoint &&
      this.rideData?.endPoint
    ) {
      if (this.suppressNextRouteUpdate) {
        this.suppressNextRouteUpdate = false;
        return;
      }
      if (this.marker && this.marker.getLatLng() !==this.simulatedRoutePoints[0]) {
        this.currentPositions.push(this.marker.getLatLng());
      }

      this.resetMap();
      this.loadRouteFromRideData();
      this.seekTo(this.simulationIndex);
    }

    if (changes['controlCommand']) {
      const command = changes['controlCommand'].currentValue;
      switch (command) {
        case 'start':
          this.startSimulation();
          break;
        case 'pause':
          this.pauseSimulation();
          break;
      }
    }

    if (changes['simulationIndex']) {
      this.seekTo(this.simulationIndex);
    }
  }

  ngAfterViewInit(): void {
    this.initMap();

    // wenn Durchführung einer Fahrt und Daten vorhanden
    if (this.simulationMode && this.rideData) {
      this.loadRouteFromRideData();
    }
    if (this.routePlan) {
      this.loadDataFromRoutePlan(this.routePlan);
    }
  }

  loadDataFromRoutePlan(routePlan: RoutePlan) {
    this.startPoint = L.latLng(routePlan.startPoint);
    this.endPoint = L.latLng(routePlan.endPoint);
    this.stopovers = routePlan.stopovers!.map((stopover) => L.latLng(stopover));
    this.loadRoute();
    this.setPinsForAllPoints();
  }

  //Durchführung einer Fahrt

  calculateDelayMs(): number {
    const minDurationSec = 3;
    const maxDurationSec = 30;

    const normalized = Math.max(0, Math.min(100, this.simulationSpeed));
    const durationSec =
      maxDurationSec - (normalized / 100) * (maxDurationSec - minDurationSec);

    if (this.simulatedRoutePoints.length <= 1) return 1000;

    return Math.max(
      50,
      Math.floor((durationSec * 1000) / this.simulatedRoutePoints.length)
    );
  }

  loadRouteFromRideData() {
    const start = this.rideData.startPoint;
    const end = this.rideData.endPoint;
    const stopovers = this.rideData.stopovers;

    this.startPoint = new L.LatLng(start.lat, start.lng);
    this.endPoint = new L.LatLng(end.lat, end.lng);
    if (stopovers) {
      this.stopovers = stopovers.map(
        (stopover) => new L.LatLng(stopover.lat, stopover.lng)
      );
    }

    this.loadRoute();
    this.setPinsForAllPoints();
  }

  seekTo(index: number) {
    if (!this.simulatedRoutePoints || this.simulatedRoutePoints.length === 0) {
      return;
    }

    if (index < 0 || index >= this.simulatedRoutePoints.length) {
      return;
    }

    this.simulationIndex = index;

    const point = this.simulatedRoutePoints[index];

    if (!this.marker) {
      this.marker = L.marker(point, {
        icon: L.icon({
          iconUrl: 'assets/leaflet/car.png',
          iconSize: [25, 41],
        }),
      }).addTo(this.map);
    } else {
      this.marker.setLatLng(point);
    }
  }

  startSimulation() {
    if (this.isSimulationRunning) {
      return;
    }

    this.isSimulationRunning = true;
    this.simulationStateChanged.emit(true);

    if (!this.simulatedRoutePoints || this.simulatedRoutePoints.length === 0) {
      return;
    }

    if (!this.marker) {
      this.marker = L.marker(this.simulatedRoutePoints[0], {
        icon: L.icon({
          iconUrl: 'assets/leaflet/car.png',
          iconSize: [25, 41],
        }),
      }).addTo(this.map);
    }

    this.simulationTimer = setInterval(() => {
      this.simulationIndex++;

      if (this.simulationIndex >= this.simulatedRoutePoints.length) {
        clearInterval(this.simulationTimer);
        this.isSimulationRunning = false;

        if (!this.route) {
          this.route = {
            id: this.rideData.id,
            startPoint: this.rideData.startPoint,
            endPoint: this.rideData.endPoint,
            stopovers: [...(this.rideData.stopovers || [])],
            totalDistanceKm: this.rideData.totalDistanceKm,
            totalDurationMin: this.rideData.totalDurationMin,
            expectedPrice: this.rideData.expectedPrice,
            simulationStatus: this.rideData.simulationStatus,
            paymentStatus: this.rideData.paymentStatus,
            ratingCustomer: null,
            ratingDriver: null,
          };
        }

        this.simulationCompleted.emit(this.route);
        return;
      }

      const nextPoint = this.simulatedRoutePoints[this.simulationIndex];

      const dynamicSpeedMs = this.calculateDelayMs();

      this.marker.slideTo(nextPoint, {
        duration: dynamicSpeedMs,
        keepAtCenter: false,
        easing: (t: number) => t * t * (3 - 2 * t),
      });

      const nextStopover = this.stopovers[this.nextStopoverIndex];
      if (
        nextStopover &&
        nextPoint.distanceTo(nextStopover) < this.avgDistanceBetweenPoints * 1.3
      ) {
        this.nextStopoverIndex++;
      }

      if (this.simulationIndex % 5 === 0) {
        this.http
          .put('http://localhost:8080/simulation/updateIndex', {
            rideMatchId: this.rideData.id,
            currentSimulationIndex: this.simulationIndex,
          })
          .subscribe({
            next: () => {},
            error: (err) =>
              console.error('Failed to update simulation index:', err),
          });
      }
    }, this.calculateDelayMs());
  }

  pauseSimulation() {
    clearInterval(this.simulationTimer);
    this.isSimulationRunning = false;
    this.simulationStateChanged.emit(false);
  }

  //Fahrtplanung
  initMap() {
    this.map = L.map('map', {
      center: [49.420318, 8.687872],
      zoom: 13,
      minZoom: 2,
      maxZoom: 18, //street-level detail
    });

    //reference: https://leaflet-extras.github.io/leaflet-providers/preview/
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(this.map);

    this.map.on('click', (event: L.LeafletMouseEvent) => {
      this.onClickMap(event);
    });
  }

  onClickMap(event: LeafletMouseEvent) {
    let clickedPoint = event.latlng;

    switch (this.currentType) {
      case PointType.STARTPOINT:
        if (this.startPoint) {
          window.alert(
            "Startpunkt bereits gewählt. 'Route zurücksetzen' klicken, um neu zu wählen."
          );
          return;
        }
        this.startPoint = clickedPoint;
        break;

      case PointType.ENDPOINT:
        if (this.endPoint) {
          window.alert(
            "Endpunkt bereits gewählt. 'Route zurücksetzen' klicken, um neu zu wählen."
          );
          return;
        }
        this.endPoint = clickedPoint;
        break;

      case PointType.STOPOVER:
        this.stopovers.push(clickedPoint);
        break;
    }

    this.setPin(clickedPoint, this.currentType);
  }

  loadRoute() {

    if (!this.startPoint || !this.endPoint) {
      window.alert('Startpunkt und Ziel müssen ausgewählt werden');
      return;
    }

    let waypoints: L.LatLng[] = this.getWayPoints();

    let plan = new L.Routing.Plan(waypoints, {
      createMarker: () => {
        return false;
      },
    });

    this.control = L.Routing.control({
      router: L.Routing.osrmv1({
        serviceUrl: `http://router.project-osrm.org/route/v1/`,
      }),
      lineOptions: {
        styles: [{ color: 'blue', weight: 7 }],
        extendToWaypoints: true,
        missingRouteTolerance: 0,
      },
      fitSelectedRoutes: true,
      show: false,
      waypoints: waypoints,
      plan: plan,
      showAlternatives: false,
    })

      //Durchführung einer Fahrt
      .on('routesfound', (e: any) => {
        const coordinates = e.routes[0].coordinates;

        const N = 5;
        this.simulatedRoutePoints = coordinates
          .filter((p: any, index: number) => index % N === 0)
          .map((p: any) => L.latLng(p.lat, p.lng));

        const lastCoord = coordinates[coordinates.length - 1];
        const lastPoint = L.latLng(lastCoord.lat, lastCoord.lng);

        this.simulatedRoutePoints.push(lastPoint);

        this.routePointsChanged.emit(this.simulatedRoutePoints);

        let totalDistance = 0;
        for (let i = 1; i < this.simulatedRoutePoints.length; i++) {
          totalDistance += this.simulatedRoutePoints[i].distanceTo(
            this.simulatedRoutePoints[i - 1]
          );
        }
        this.avgDistanceBetweenPoints =
          totalDistance / (this.simulatedRoutePoints.length - 1);
      })
      //Fahrtplanung
      .on('routingerror', function () {
        alert(
          'Route konnte nicht gefunden werden. Bitte überprüfe die Start-, Zwischen- und Zielpunkte.'
        );
      })
      .addTo(this.map);

    this.map.off('click');
  }

  resetMap() {
    this.map.eachLayer((layer) => {
      if (!(layer instanceof L.TileLayer) && layer !== this.marker) {
        this.map.removeLayer(layer);
      }
    });

    this.startPoint = null;
    this.stopovers = [];
    this.endPoint = null;
    this.currentType = PointType.STARTPOINT;

    //turn off the event listener in initMap()
    this.map.off('click');
    this.map.on('click', (event: LeafletMouseEvent) => {
      this.onClickMap(event);
    });
  }

  setPinsForAllPoints() {
    if (this.startPoint && this.endPoint) {
      this.startMarker = this.setPin(this.startPoint, PointType.STARTPOINT);
      this.endMarker = this.setPin(this.endPoint, PointType.ENDPOINT);
    }
    for (const stopover of this.stopovers) {
      const isNotStopover = this.currentPositions.some((point)=>stopover.equals(point));
      if (!isNotStopover){
        this.stopoverMarkers.push(this.setPin(stopover, PointType.STOPOVER));
      }
    }
  }

  setPin(point: L.LatLng, type: PointType) {
    let iconUrl = 'assets/leaflet/';

    switch (type) {
      case PointType.STARTPOINT:
        iconUrl += 'pin-icon-start.png';
        break;

      case PointType.ENDPOINT:
        iconUrl += 'pin-icon-end.png';
        break;

      case PointType.STOPOVER:
        iconUrl += 'pin-icon-wpt.png';
        break;
    }
    const myIcon = L.icon({
      iconUrl: iconUrl,
      iconSize: [25, 41],
      iconAnchor: [12, 41], // wie margin-left: -12px, margin-up: -41px
    });

    return L.marker(point, {
      icon: myIcon,
    }).addTo(this.map);
  }

  changeEndpoint() {
    this.map.off('click');
    this.map.on('click', (event: L.LeafletMouseEvent) => {
      this.addCurrentPositionAsStopover();
      this.endPoint = event.latlng;
      this.map.removeLayer(this.endMarker);
      this.endMarker = this.setPin(this.endPoint, PointType.ENDPOINT);
      this.map.removeControl(this.control);
      this.loadRoute();
      this.updateRoute();
    });
  }

  addStopover() {
    this.map.off('click');
    this.map.on('click', (event: LeafletMouseEvent) => {
      this.addCurrentPositionAsStopover();
      const stopover = event.latlng;
      this.stopovers.push(stopover);
      this.stopoverMarkers.push(this.setPin(stopover, PointType.STOPOVER));
      this.map.removeControl(this.control);
      this.loadRoute();
      this.updateRoute();
    });
  }

  addCurrentPositionAsStopover() {
    if (!this.marker) return;
    const currentPos = this.marker.getLatLng();
    const nextStopover = this.stopovers[this.nextStopoverIndex];
    if (nextStopover && currentPos.distanceTo(nextStopover) < this.avgDistanceBetweenPoints * 1.3) return;
    if (this.stopovers.some((stopover)=>stopover.equals(currentPos))) return;
    this.stopovers.splice(this.nextStopoverIndex, 0, currentPos);
    const invisibleMarker = this.addInvisibleMarker(currentPos);
    this.stopoverMarkers.splice(this.nextStopoverIndex, 0, invisibleMarker);
    this.nextStopoverIndex++;
  }

  deleteStopover() {

    this.stopoverMarkers.forEach((marker) => {
      marker.off('dblclick');
      marker.on('dblclick', () => {
        this.addCurrentPositionAsStopover();
        const markerIndex = this.stopoverMarkers.indexOf(marker);
        console.log("markerindex:"+markerIndex)
        if (markerIndex < this.nextStopoverIndex) {
          window.alert('This is passed and cannot be deleted.');
          return;
        }
        this.stopoverMarkers.splice(markerIndex, 1);

        this.stopovers.splice(this.stopovers.indexOf(marker.getLatLng()), 1);
        this.map.removeLayer(marker);
        this.map.removeControl(this.control);
        this.loadRoute();
        this.updateRoute();
      });
    });
  }

  addInvisibleMarker(point: L.LatLng) {
    return L.marker([point.lat, point.lng], {
      opacity: 0,
      icon: L.icon({
        iconUrl: 'assets/leaflet/pin-icon-wpt.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41],
      }),
    }).addTo(this.map);
  }

  updateRoute() {
    this.stopovers.forEach((stopover, index) => {
      console.log(`Stopover ${index}:`, stopover);
    });
    this.route = {
      id: this.rideData.id,
      startPoint: this.rideData.startPoint,
      endPoint: {
        lat: this.endPoint!.lat,
        lng: this.endPoint!.lng,
      },
      stopovers: this.stopovers.map((stopover) => ({
        lat: stopover.lat,
        lng: stopover.lng,
      })),
      totalDistanceKm: 0,
      totalDurationMin: 0,
      expectedPrice: 0,
      simulationStatus: this.rideData.simulationStatus,
      paymentStatus: this.rideData.paymentStatus,
      ratingCustomer: null,
      ratingDriver: null,
    };

    this.suppressNextRouteUpdate = true;

    this.http
      .put('http://localhost:8080/simulation/updateRoute', this.route)
      .subscribe({
        next: () => console.log('Route updated in backend'),
        error: (err) => console.error('Failed to update route:', err),
      });
  }

  getWayPoints(): L.LatLng[] {
    let points: L.LatLng[] = [];
    if (this.startPoint && this.endPoint) {
      points.push(this.startPoint);
      for (const stopover of this.stopovers) {
        points.push(stopover);
      }
      points.push(this.endPoint);
    }
    return points;
  }

  ngOnDestroy() {
    if (this.map) {
      this.map.off('click');
    }
    this.resetMap();
  }
}
