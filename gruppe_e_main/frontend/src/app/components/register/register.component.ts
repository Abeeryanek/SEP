import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';
import {User} from '../../models/User';
import {UserRole} from '../../models/UserRole';
import {RegisterService} from '../../services/register.service';
import {Car} from '../../models/Car';

@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [FormsModule, CommonModule]
})
export class RegisterComponent {

  user: User = {
    username: '',
    firstname: '',
    lastname: '',
    email: '',
    birthdate: '',
    role: UserRole.KUNDE,
    carType: Car.NULL,
    password: ''
  };

  confirmPassword: string = ''
  profilePicture: File | null = null

  errors = {
    username: '',
    firstname: '',
    lastname: '',
    email: '',
    birthdate: '',
    role: '',
    carType: '',
    password: '',
    confirmPassword: ''
  };

  constructor(private router: Router, private registerService: RegisterService) {}

  get isDriver(): boolean {
    return this.user.role === UserRole.FAHRER;
  }

  onRegister() {
    let valid = true;
    this.resetErrors();

    if (!this.user.username.trim()) {
      this.errors.username = 'Username is required';
      valid = false;
    }

    if (!this.user.firstname.trim()) {
      this.errors.firstname = 'First name is required';
      valid = false;
    }

    if (!this.user.lastname.trim()) {
      this.errors.lastname = 'Last name is required';
      valid = false;
    }

    if (!this.user.email.includes('@')) {
      this.errors.email = 'Valid email is required';
      valid = false;
    }

    if (!this.user.birthdate) {
      this.errors.birthdate = 'Birthdate is required';
      valid = false;
    }

    if (!this.user.role) {
      this.errors.role = 'Please select a role';
      valid = false;
    }
    if (this.user.password.length < 8) {
      this.errors.password = 'Password must be at least 8 characters';
      valid = false;
    }
    if (this.user.password !== this.confirmPassword) {
      this.errors.confirmPassword = 'Passwords do not match';
      valid = false;
    }
    if (!valid) {
      setTimeout(() => {
        const firstError = document.querySelector('.error');
        if (firstError) {
          firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
      }, 100);
      return;
    }
    console.log('Registration data:', this.user);
    this.registerService.register(this.user, this.profilePicture)
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.profilePicture = file;
    }
  }

  private resetErrors() {
    this.errors = {
      username: '',
      firstname: '',
      lastname: '',
      email: '',
      birthdate: '',
      role: '',
      carType: '',
      password: '',
      confirmPassword: ''
    };
  }
  navigateHome() {
    this.router.navigate(['/home']);
  }
}
