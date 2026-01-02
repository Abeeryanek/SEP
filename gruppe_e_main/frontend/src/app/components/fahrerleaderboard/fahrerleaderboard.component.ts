import { Component, OnInit, ViewChild,AfterViewInit} from '@angular/core';
import { MatSortModule,MatSort } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import {FahrerleaderboardService} from '../../services/fahrerleaderboard.service';
import {Fahrleaderboard} from '../../models/Fahrerleaderboard';


@Component({
  selector: 'app-fahrerleaderboard',
  styleUrl: 'fahrerleaderboard.component.scss',
  templateUrl: 'fahrerleaderboard.component.html',
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, MatTableModule, MatSortModule],
})
export class FahrerleaderboardComponent implements OnInit,AfterViewInit {
  displayedColumns:string []= [
    'driverUsername','driverFullName','totalDistanceTravelled','avgRating','totalDurationTravelled','totalRidesTravelled',
    'totalMoney']

  dataSource= new MatTableDataSource<Fahrleaderboard>()

  @ViewChild(MatSort) sort!:MatSort

  constructor(private fahrtenService: FahrerleaderboardService) {}

  ngOnInit() {

    this.fahrtenService.getFahrtleaderboard().subscribe(data => {
      this.dataSource.data = data;
    });
  }
  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }
  applyFilter(event: Event) {
    this.dataSource.filter = (event.target as HTMLInputElement).value.trim().toLowerCase();
  }
}
