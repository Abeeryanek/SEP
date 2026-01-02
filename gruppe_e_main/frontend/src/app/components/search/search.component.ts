import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserRole } from '../../models/UserRole';
import { Profile } from '../../models/Profile';
import { UserService } from '../../services/UserService';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css',
})
export class SearchComponent {
  username: string = '';


  constructor(
    public userService: UserService,
    private http: HttpClient,
    private router: Router
  ) {}

  closeSearch() {
    this.userService.searchOpen = false;
  }

  goToProfile() {
    const name = this.username.trim();
    if (!name) {
      alert('Please enter a username');
      return;
    }

    this.http.get<Profile>('http://localhost:8080/users/search/' + name)
      .subscribe({
        next: (res) => {
          this.userService.profile = res;
          const role = res.role;
          console.log('Profile picture:', res.profilePictureBase64);

          if (role === UserRole.KUNDE) {
            this.router.navigate(['/kunde/profile', name]);
          } else if (role === UserRole.FAHRER) {
            this.router.navigate(['/fahrer/profile', name]);
          } else {
            alert('Not known role');
          }
        },
         error: (err) => {
        if (err.status === 404) {
          alert('User not found');
        } else {
          console.error('Error:', err);
          alert('Request failed');
        }
      },
      });
  }
}
