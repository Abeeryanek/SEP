import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { LoginService } from '../../../services/login.service';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [FormsModule, CommonModule,RouterLink]
})
export class LoginComponent {
  username = '';
  password = '';
  twoFACode = '';
  show2FA = false;
  loginError = '';
  twoFAError = '';
  superCode = '';

  constructor(private router: Router, private loginService: LoginService) {}

  initiateLogin() {
    if (!this.username || !this.password) {
      this.loginError = 'Please enter both username and password';
      return;
    }
    this.loginError = '';
    this.loginService.login(this.username, this.password).subscribe({
      next: () => {
        this.show2FA = true;
        this.loginError = '';
      },
      error: err => {
        this.loginError = err.error?.error || 'Invalid username or password';
        this.show2FA = false;
      }
    });
  }

  verify() {
    if (!this.twoFACode) {
      this.twoFAError = 'Please enter your verification code';
      return;
    }
    this.twoFAError = '';
    this.loginService.verify(this.username, this.twoFACode).subscribe({
      next: res => {
        alert('Login successful!');
        localStorage.setItem('token', res.token);
        localStorage.setItem('username', this.username);

        //  userId aus Token extrahieren und speichern
        const userId = this.getUserIdFromToken(res.token);
        if (userId !== null) {
          localStorage.setItem('userId', userId.toString());
        }
        // ende joseph

        const role = this.getRoleFromToken(res.token);
        if (role) {
          localStorage.setItem('role', role);
        }

        this.router.navigate(['/dashboard']);  // Nur ein Redirect nötig
      },
      error: err => {
        this.twoFAError = err.error?.error || 'Verification failed';
      }
    });
  }

  // Hilfsfunktion zum Parsen des JWT und Auslesen der Rolle
  getRoleFromToken(token: string): string | null {
    try {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      return decoded.role || null;
    } catch (e) {
      return null;
    }
  }

  // Hilfsfunktion für die User-ID aus JWT /joseph
  getUserIdFromToken(token: string): number | null {
    try {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      console.log('JWT Payload:', decoded);

      if (decoded.id) {
        console.log('decoded.id type:', typeof decoded.id, 'value:', decoded.id);
        return Number(decoded.id);
      }
      if (decoded.sub && !isNaN(Number(decoded.sub))) {
        console.log('decoded.sub:', decoded.sub);
        return Number(decoded.sub);
      }
      return null;
    } catch (e) {
      return null;
    }
  }


  navigateHome() {
    this.router.navigate(['/home']);
  }
}
