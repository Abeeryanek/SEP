import { Component, OnInit } from '@angular/core';

import { SearchComponent } from "../search/search.component";
import { RouterModule } from '@angular/router';
import { UserService } from '../../services/UserService';
import { DropdownComponent } from "../dropdown/dropdown.component";
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-fahrer',
  imports: [SearchComponent, RouterModule, DropdownComponent, NgIf],
  templateUrl: './fahrer.component.html',
  styleUrl: './fahrer.component.css'
})
export class FahrerComponent implements OnInit {

   constructor(public userService: UserService){}

   ngOnInit(): void {
      const username = this.userService.username;
      if (username) {
        this.userService.loadProfile(username);
      }
  }

}
