import { Component, OnInit } from '@angular/core';
import { SearchComponent } from "../search/search.component";
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserService } from '../../services/UserService';
import { DropdownComponent } from "../dropdown/dropdown.component";


@Component({
  selector: 'app-kunde',
  imports: [SearchComponent, RouterModule, CommonModule, DropdownComponent],
  templateUrl: './kunde.component.html',
  styleUrl: './kunde.component.css',
  standalone: true
})

export class KundeComponent implements OnInit{

  constructor(public userService: UserService){}

  ngOnInit(): void {
      const username = this.userService.username;
      if (username) {
        this.userService.loadProfile(username);
      }
  }

}
