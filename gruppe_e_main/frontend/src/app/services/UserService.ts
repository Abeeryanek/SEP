import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UserRole } from '../models/UserRole';

import { HttpClient } from '@angular/common/http';

import { Profile } from '../models/Profile';
import { User } from '../models/User';

@Injectable({
  providedIn: 'root',
})
export class UserService {


  profile: Profile = {
    username: '',
    role: UserRole.KUNDE,
    firstname: '',
    lastname: '',
    email: '',
    birthdate: '',
    profilePictureBase64: null,
    carType: null,
    rating: 0,
    totalTrips: 0,
    balance: 0,
    drivenDistance: 0,
  };

  profileLoaded = false;
  dropdownOpen = false;
  searchOpen = false;

  constructor(private router: Router, private http: HttpClient) {}

  get username(): string | null {
    return localStorage.getItem('username');
  }

  openDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  toggleSearch() {

    this.searchOpen = !this.searchOpen;
  }

  loadProfile(username: string) {
    this.http
      .get<Profile>('http://localhost:8080/users/search/' + username)
      .subscribe({
        next: (res) => {
          this.profile = res;
          console.log('Profile picture:', res.profilePictureBase64);
          this.profileLoaded = true;
        },
        error: () => {
          alert('Request failed');
        },
      });
  }

  goToProfile() {

    const username = localStorage.getItem('username');

    if (!username) {
      console.warn('No username found');
      return;
    }

    if (this.profileLoaded) {
      this.navigateToProfile();
      return;
    }

    this.http
      .get<Profile>('http://localhost:8080/users/search/' + username)
      .subscribe({
        next: (res) => {
          this.profile = res;
          this.profileLoaded = true;
          this.navigateToProfile();
        },
        error: () => {
          alert('Request failed');
        },
      });
  }

  private navigateToProfile() {
    const role = this.profile.role;
    const username = this.profile.username;

    if (role === UserRole.KUNDE) {
      this.router.navigate(['/kunde/profile', username]);
    } else if (role === UserRole.FAHRER) {
      this.router.navigate(['/fahrer/profile', username]);
    } else {
      alert('Not known role');
    }
  }

  goToRideSimulation() {
    this.router.navigate(['/ride-simulation']);
  }

  logout() {
    localStorage.clear();

    this.router.navigate(['/home']);
  }
}
