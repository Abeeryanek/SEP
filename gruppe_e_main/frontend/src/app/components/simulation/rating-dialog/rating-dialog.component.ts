import { NgFor } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-rating-dialog',
  imports: [FormsModule, NgFor],
  templateUrl: './rating-dialog.component.html',
  styleUrl: './rating-dialog.component.scss'
})
export class RatingDialogComponent {

@Input() rideMatchId!: number;
@Output() ratingSubmitted = new EventEmitter<number>();
rating: number = 0;
stars = [1, 2, 3, 4, 5];
hoverRating = 0;


constructor(private http: HttpClient){}

setRating(i: number){
  this.rating = i;
}

setHover(i: number){
  this.hoverRating = i;
}

clearHover(){
  this.hoverRating = 0;
}

submitRating(){
  const role = localStorage.getItem('role');

  const payload= {
    "id": this.rideMatchId,
    "role": role,
    "rating": this.rating
  };

  this.http.put("http://localhost:8080/simulation/rate", payload).subscribe({
    next: () => {
      console.log('Rating submitted successfully');
      this.ratingSubmitted.emit();
    },
    error: (err) => {
      console.error('Error submitting rating:', err);
    }
  });

}

}
