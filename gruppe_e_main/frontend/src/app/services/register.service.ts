import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { Router } from '@angular/router';
import {User} from '../models/User';

@Injectable({
  providedIn: 'root',
})
export class RegisterService{

  constructor(private router: Router, private http: HttpClient) {
  }

  register(user: User, profilePicture: File | null) {
    const formData = new FormData();
    formData.append('user', new Blob([JSON.stringify(user)], {type: 'application/json'}));

    if (profilePicture) {
      formData.append('profilePicture', profilePicture);
    }

    return this.http.post("http://localhost:8080/users/register", formData, {responseType: 'text'}).subscribe({
      next: () =>{
        this.router.navigate(['/login']);
      },
      error: (err)=>{
        alert(err.error)
      }
    })
  }
}
