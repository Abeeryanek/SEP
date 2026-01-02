import { Component } from '@angular/core';
import { UserService } from '../../services/UserService';

@Component({
  selector: 'app-dropdown',
  imports: [],
  templateUrl: './dropdown.component.html',
  styleUrl: './dropdown.component.scss'
})
export class DropdownComponent {

   constructor(public userService: UserService){}

}
