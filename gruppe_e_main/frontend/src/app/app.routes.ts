import { HomeComponent } from './components/components/home/home.component';
import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/components/login/login.component';
import { Routes } from '@angular/router';
import { KundeComponent } from './components/kunde/kunde.component';
import { KundeProfileComponent } from './components/kunde/kunde-profile/kunde-profile.component';
import { FahrerComponent } from './components/fahrer/fahrer.component';
import { FahrerProfileComponent } from './components/fahrer/fahrer-profile/fahrer-profile.component';

import {MapComponent} from './components/map/map.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AuthGuard } from './guards/auth.guard';
import { RideRequestComponent } from './components/ride-request/ride-request.component';
import { RideSimulationComponent } from './components/simulation/ride-simulation/ride-simulation.component';
import { FahrerRideRequestsComponent } from './components/fahrer-ride-requests/fahrer-ride-requests.component';
import {GeldkontenComponent} from './components/geldkonten/geldkonten.component';
import {KundeRideOffersComponent} from './components/kunde-ride-offers/kunde-ride-offers.component';
import {FahrthistorieComponent} from './components/fahrthistorie/fahrthistorie.component';
import { FahrerStatistikenComponent } from './components/fahrer-statistiken/fahrer-statistiken.component';
import {FahrerleaderboardComponent} from './components/fahrerleaderboard/fahrerleaderboard.component';

export const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'map', component: MapComponent, canActivate: [AuthGuard] },
  { path: 'ride-request', component: RideRequestComponent, canActivate: [AuthGuard] },
  { path: 'ride-simulation', component: RideSimulationComponent },
  { path: 'kunde/profile/:username', component: KundeProfileComponent },
  { path: 'fahrer/profile/:username', component: FahrerProfileComponent },
  { path: 'fahrer/ride-requests', component: FahrerRideRequestsComponent },
  { path: 'kunde/ride-offers', component: KundeRideOffersComponent },
  { path: 'geldkonten', component: GeldkontenComponent},
  { path: 'fahrthistorie', component: FahrthistorieComponent},
  { path: 'fahrer/statistiken', component: FahrerStatistikenComponent, canActivate: [AuthGuard] },
  { path: 'fahrerleaderboard', component: FahrerleaderboardComponent},
  // {
  //   path: 'kunde',
  //   component: KundeComponent,
  //   canActivate: [AuthGuard],
  //   children: [
  //     { path: 'profile/:username', component: KundeProfileComponent },
  //   ],
  // },
  // {
  //   path: 'fahrer',
  //   component: FahrerComponent,
  //   canActivate: [AuthGuard],
  //   children: [
  //     { path: 'profile/:username', component: FahrerProfileComponent },
  //   ],
  // },
  { path: 'active-ride', canActivate: [AuthGuard], loadComponent: () => import('./pages/active-ride.component').then(m => m.ActiveRideComponent) },

  { path: '', redirectTo: 'home', pathMatch: 'full' }, // Default route
  { path: '**', redirectTo: 'home' }, // Fallback route

];
