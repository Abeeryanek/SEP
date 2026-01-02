import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/UserService';
import { CommonModule, NgIf } from '@angular/common';

@Component({
  selector: 'app-fahrer-profile',
  imports: [FormsModule, NgIf, CommonModule],
  templateUrl: './fahrer-profile.component.html',
  styleUrl: './fahrer-profile.component.css'
})
export class FahrerProfileComponent {

  constructor(public userService: UserService) {}
 

}
