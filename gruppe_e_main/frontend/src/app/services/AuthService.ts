
import { Injectable } from '@angular/core';


@Injectable({ providedIn: 'root' })
export class AuthService {

  //Methode um user id aus token zu extrahieren
  getUserId(): number | null {
    const token = localStorage.getItem('token');
    if (!token) return null;
    try {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      return decoded.sub ? Number(decoded.sub) : null;
    } catch {
      return null;
    }
  }
}
