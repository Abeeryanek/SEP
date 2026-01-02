import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';
import { provideCharts, BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { FahrthistorieService } from '../../services/fahrthistorie.service';

@Component({
  selector: 'app-fahrer-statistiken',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatSelectModule,
    MatFormFieldModule,
    MatCardModule,
    BaseChartDirective
  ],
  providers: [provideCharts()],
  templateUrl: './fahrer-statistiken.component.html',
  styleUrls: ['./fahrer-statistiken.component.scss']
})
export class FahrerStatistikenComponent implements OnInit {
  
  // Zeitraum-Auswahl
  viewMode: 'monthly' | 'daily' = 'monthly';
  selectedYear: number = new Date().getFullYear();
  selectedMonth: number = new Date().getMonth();
  
  // Verfügbare Jahre und Monate
  availableYears: number[] = [];
  months = [
    { value: 0, name: 'Januar' },
    { value: 1, name: 'Februar' },
    { value: 2, name: 'März' },
    { value: 3, name: 'April' },
    { value: 4, name: 'Mai' },
    { value: 5, name: 'Juni' },
    { value: 6, name: 'Juli' },
    { value: 7, name: 'August' },
    { value: 8, name: 'September' },
    { value: 9, name: 'Oktober' },
    { value: 10, name: 'November' },
    { value: 11, name: 'Dezember' }
  ];

  // Chart-Konfigurationen
  public earningsChartData: ChartConfiguration<'line'>['data'] = {
    datasets: [
      {
        data: [],
        label: 'Einnahmen (€)',
        borderColor: '#4CAF50',
        backgroundColor: 'rgba(76, 175, 80, 0.1)',
        tension: 0.4
      }
    ],
    labels: []
  };

  public distanceChartData: ChartConfiguration<'line'>['data'] = {
    datasets: [
      {
        data: [],
        label: 'Distanz (km)',
        borderColor: '#2196F3',
        backgroundColor: 'rgba(33, 150, 243, 0.1)',
        tension: 0.4
      }
    ],
    labels: []
  };

  public timeChartData: ChartConfiguration<'line'>['data'] = {
    datasets: [
      {
        data: [],
        label: 'Fahrzeit (Min)',
        borderColor: '#FF9800',
        backgroundColor: 'rgba(255, 152, 0, 0.1)',
        tension: 0.4
      }
    ],
    labels: []
  };

  public ratingChartData: ChartConfiguration<'line'>['data'] = {
    datasets: [
      {
        data: [],
        label: 'Bewertung',
        borderColor: '#9C27B0',
        backgroundColor: 'rgba(156, 39, 176, 0.1)',
        tension: 0.4
      }
    ],
    labels: []
  };

  public chartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'top'
      }
    },
    scales: {
      x: {
        display: true,
        title: {
          display: true,
          text: this.viewMode === 'monthly' ? 'Monat' : 'Tag',
          color: '#bfa046',
          font: { weight: 'bold', size: 16 }
        },
        ticks: {
          color: '#6d5c1f',
          font: { size: 13 }
        },
        grid: {
          color: '#f5e7b2'
        }
      },
      y: {
        beginAtZero: true,
        title: {
          display: false
        },
        ticks: {
          color: '#6d5c1f',
          font: { size: 13 }
        },
        grid: {
          color: '#f5e7b2'
        }
      }
    }
  };

  private fahrtenData: any[] = [];

  constructor(private fahrthistorieService: FahrthistorieService) {}

  ngOnInit(): void {
    this.loadFahrtenData();
  }

  private loadFahrtenData(): void {
    const username = localStorage.getItem('username');
    if (username) {
      this.fahrthistorieService.getFahrten(username).subscribe({
        next: (data) => {
          this.fahrtenData = data;
          this.generateAvailableYears();
          this.updateCharts();
        },
        error: (error) => {
          console.error('Fehler beim Laden der Fahrten:', error);
        }
      });
    }
  }

  private generateAvailableYears(): void {
    const years = new Set<number>();
    this.fahrtenData.forEach(fahrt => {
      const year = new Date(fahrt.updateTime).getFullYear();
      years.add(year);
    });
    const currentYear = new Date().getFullYear();
    // Füge die letzten 5 Jahre hinzu
    for (let y = currentYear; y > currentYear - 5; y--) {
      years.add(y);
    }
    this.availableYears = Array.from(years).sort((a, b) => b - a);
    if (this.availableYears.length > 0 && !this.availableYears.includes(this.selectedYear)) {
      this.selectedYear = this.availableYears[0];
    }
  }

  onViewModeChange(): void {
    this.updateCharts();
  }

  onYearChange(): void {
    this.updateCharts();
  }

  onMonthChange(): void {
    this.updateCharts();
  }

  private updateCharts(): void {
    if (this.viewMode === 'monthly') {
      this.updateMonthlyCharts();
    } else {
      this.updateDailyCharts();
    }
  }

  private updateMonthlyCharts(): void {
    const monthlyData = this.aggregateMonthlyData();

    // Labels als Strings
    const labels = monthlyData.labels.map((l: any) => l.toString());
    this.earningsChartData.labels = labels;
    this.distanceChartData.labels = labels;
    this.timeChartData.labels = labels;
    this.ratingChartData.labels = labels;

    // ChartOptions als neues Objekt setzen
    this.chartOptions = {
      ...this.chartOptions,
      scales: {
        ...this.chartOptions.scales,
        ['x']: {
          ...this.chartOptions.scales?.['x'],
          title: {
            ...this.chartOptions.scales?.['x']?.title,
            text: 'Monat',
            display: true
          }
        }
      }
    };

    this.earningsChartData.datasets[0].data = monthlyData.earnings;
    this.distanceChartData.datasets[0].data = monthlyData.distances;
    this.timeChartData.datasets[0].data = monthlyData.times;
    this.ratingChartData.datasets[0].data = monthlyData.ratings;
  }

  private updateDailyCharts(): void {
    const dailyData = this.aggregateDailyData();

    // Labels als Strings
    const labels = dailyData.labels.map((l: any) => l.toString());
    this.earningsChartData.labels = labels;
    this.distanceChartData.labels = labels;
    this.timeChartData.labels = labels;
    this.ratingChartData.labels = labels;

    // ChartOptions als neues Objekt setzen
    this.chartOptions = {
      ...this.chartOptions,
      scales: {
        ...this.chartOptions.scales,
        ['x']: {
          ...this.chartOptions.scales?.['x'],
          title: {
            ...this.chartOptions.scales?.['x']?.title,
            text: 'Tag',
            display: true
          }
        }
      }
    };

    this.earningsChartData.datasets[0].data = dailyData.earnings;
    this.distanceChartData.datasets[0].data = dailyData.distances;
    this.timeChartData.datasets[0].data = dailyData.times;
    this.ratingChartData.datasets[0].data = dailyData.ratings;
  }

  private aggregateMonthlyData(): any {
    const monthlyData: { [key: number]: any } = {};
    
    // Filtere Daten für das ausgewählte Jahr
    const filteredData = this.fahrtenData.filter(fahrt => {
      const fahrtDate = new Date(fahrt.updateTime);
      return fahrtDate.getFullYear() === this.selectedYear;
    });

    // Initialisiere alle Monate mit 0
    for (let i = 0; i < 12; i++) {
      monthlyData[i] = {
        earnings: 0,
        distances: 0,
        times: 0,
        ratings: [],
        count: 0
      };
    }

    // Aggregiere Daten
    filteredData.forEach(fahrt => {
      const month = new Date(fahrt.updateTime).getMonth();
      monthlyData[month].earnings += Number(fahrt.ridePrice);
      monthlyData[month].distances += Number(fahrt.totalDistanceKm);
      monthlyData[month].times += Number(fahrt.durationMin);
      if (fahrt.ratingDriver) {
        monthlyData[month].ratings.push(Number(fahrt.ratingDriver));
      }
      monthlyData[month].count++;
    });

    // Berechne Durchschnittsbewertungen
    const labels = this.months.map(m => m.name);
    const earnings = Object.values(monthlyData).map(m => m.earnings);
    const distances = Object.values(monthlyData).map(m => m.distances);
    const times = Object.values(monthlyData).map(m => m.times);
    const ratings = Object.values(monthlyData).map(m => 
      m.ratings.length > 0 ? m.ratings.reduce((a: number, b: number) => a + b, 0) / m.ratings.length : 0
    );

    return { labels, earnings, distances, times, ratings };
  }

  private aggregateDailyData(): any {
    const dailyData: { [key: string]: any } = {};

    // Filtere Daten für den ausgewählten Monat und Jahr
    const filteredData = this.fahrtenData.filter(fahrt => {
      const fahrtDate = new Date(fahrt.updateTime);
      return fahrtDate.getFullYear() === this.selectedYear &&
             fahrtDate.getMonth() === this.selectedMonth;
    });

    // Aggregiere Daten nach Tagen
    filteredData.forEach(fahrt => {
      const date = new Date(fahrt.updateTime);
      const dayKey = date.getDate().toString();

      if (!dailyData[dayKey]) {
        dailyData[dayKey] = {
          earnings: 0,
          distances: 0,
          times: 0,
          ratings: [],
          count: 0
        };
      }

      dailyData[dayKey].earnings += Number(fahrt.ridePrice);
      dailyData[dayKey].distances += Number(fahrt.totalDistanceKm);
      dailyData[dayKey].times += Number(fahrt.durationMin);
      if (fahrt.ratingDriver) {
        dailyData[dayKey].ratings.push(Number(fahrt.ratingDriver));
      }
      dailyData[dayKey].count++;
    });

    // Bestimme die Anzahl der Tage im Monat
    const daysInMonth = new Date(this.selectedYear, this.selectedMonth + 1, 0).getDate();
    const labels = Array.from({ length: daysInMonth }, (_, i) => (i + 1).toString());

    // Fülle die Daten-Arrays für jeden Tag (auch wenn keine Daten vorhanden)
    const earnings = labels.map(day => dailyData[day]?.earnings ?? 0);
    const distances = labels.map(day => dailyData[day]?.distances ?? 0);
    const times = labels.map(day => dailyData[day]?.times ?? 0);
    const ratings = labels.map(day =>
      dailyData[day]?.ratings?.length > 0
        ? dailyData[day].ratings.reduce((a: number, b: number) => a + b, 0) / dailyData[day].ratings.length
        : 0
    );

    return { labels, earnings, distances, times, ratings };
  }
}
