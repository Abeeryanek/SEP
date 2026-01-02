import { Component, OnInit } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/UserService';
import { CommonModule, NgIf } from '@angular/common';

@Component({
  selector: 'app-kunde-profile',
  imports: [FormsModule, NgIf, CommonModule],
  templateUrl: './kunde-profile.component.html',
  styleUrl: './kunde-profile.component.css'
})
export class KundeProfileComponent{

  // ActivatedRoute：get the parameter from url, Router: navigate to other page
  constructor(public userService: UserService) {}

}
