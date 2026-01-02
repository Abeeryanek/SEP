import { Component, OnInit, ViewChild,AfterViewInit} from '@angular/core';
import { MatSortModule,MatSort } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import {FahrthistorieService} from '../../services/fahrthistorie.service';

@Component({
  selector: 'app-fahrthistorie',
  styleUrl: 'fahrthistorie.component.scss',
  templateUrl: 'fahrthistorie.component.html',
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, MatTableModule, MatSortModule],
})
export class FahrthistorieComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = [
    'id',
    'updateTime',
    'durationMin',
    'ridePrice',
    'ratingCustomer',
    'ratingDriver',
    'customerFullName',
    'customerUsername',
    'driverFullName',
    'driverUsername',
    'totalDistanceKm'
  ];

  dataSource = new MatTableDataSource<any>();

  @ViewChild(MatSort) sort!: MatSort;

  constructor(private fahrtenService: FahrthistorieService) {}

  ngOnInit() {
    const username = localStorage.getItem('username') || '';

    this.fahrtenService.getFahrten(username).subscribe(data => {
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
