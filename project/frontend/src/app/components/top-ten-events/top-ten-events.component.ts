import {Component, OnInit} from '@angular/core';
import {EventService} from '../../services/event.service';
import {EventType} from '../../dtos/eventType';
import {ToastrService} from 'ngx-toastr';
import {Event} from '../../dtos/event';
import {Chart, registerables} from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';
import {Router} from '@angular/router';

Chart.register(...registerables);

@Component({
  selector: 'app-top-ten-events',
  templateUrl: './top-ten-events.component.html',
  styleUrls: ['./top-ten-events.component.scss']
})
export class TopTenEventsComponent implements OnInit {

  //@ViewChild('chart', {static: true}) chart!: ElementRef;
  events: Event[] = [];
  totalNrOfTicketsSold: number[] = [];
  names: string[] = [];
  eventType = EventType;
  errorMessage = '';
  active = 1;

  chart: Chart;
  /*removeChart: Promise<string> = new Promise((resolve, reject) => {
    //const chart = Chart.getChart('top10');
    if (this.chart !== undefined) {
      this.chart.destroy();
      resolve('Chart destroyed');
    }
    resolve('No chart to destroy');
  });*/

  month = 1;
  year = 2023;
  category: EventType = EventType.concert;
  validYear = true;
  currentYear = (new Date()).getFullYear();

  constructor(
    private eventService: EventService,
    private notification: ToastrService,
    private router: Router,
  ) {
  }

  ngOnInit(): void {
    this.getTop10Events(EventType.concert, this.month, this.year);
  }

  public redrawChart(): void {
    /*this.removeChart.then(string => {
      console.log(string);
    });*/
    if (this.chart !== undefined) {
      this.chart.destroy();
    }
    if (this.year >= 2010 && this.year <= this.currentYear.valueOf()) {
      this.validYear = true;
      this.getTop10Events(this.category, this.month, this.year);
    } else {
      this.validYear = false;
    }
  }

  public getTop10Events(category: EventType, month: number, year: number): void {
    this.eventService.getTop10Events(category, month, year).subscribe({
      next: data => {

        this.events = data.filter(x => x.totalNrOfTicketsSold.valueOf() >= 1);
        this.totalNrOfTicketsSold = this.events.map(x => x.totalNrOfTicketsSold);
        this.names = this.events.map(x => x.name);

        if (data.length >= 1) {
          this.chart = new Chart('top10', {
            type: 'bar',
            plugins: [ChartDataLabels],
            data: {
              labels: this.names,
              datasets: [
                {
                  label: 'Sales',
                  data: this.totalNrOfTicketsSold,
                  borderColor: 'blue',
                  backgroundColor: '#021f54',
                  borderWidth: 0,
                  borderRadius: 5,
                  borderSkipped: false,
                },
              ]
            },
            options: {
              //indexAxis: 'y',
              layout: {
                padding: {
                  top: 40
                }
              },
              aspectRatio: 3,
              animation: false,
              onClick: event => this.handleClick(event),
              plugins: {
                title: {
                  display: true,
                  font: {
                    size: 20
                  },
                  text: 'Number of tickets sold by event',
                  padding: {
                    bottom: 50
                  }
                },
                legend: {
                  display: false
                },
                datalabels: {
                  color: 'white',
                  clamp: true,
                  anchor: 'end',
                  align: 'bottom',
                  padding: 15,
                  font: {
                    size: 15
                  }
                },
                tooltip: {
                  enabled: true
                }
              },
              scales: {
                x: {
                  grid: {
                    display: false
                  },
                  ticks: {
                    font: {
                      size: 20
                    },
                    //callback: (value, index, ticks) => [this.names[value], this.totalNrOfTicketsSold[value] + ' tickets sold']
                  }
                },
                y: {
                  ticks: {
                    display: false
                  },
                  display: false
                }
              }
            }
          });
        }
      },
      error: error => {
        console.error('Error finding top ten events by category: ', error);
        this.notification.error(this.errorMessage);
      }
    });
  }

  handleClick(click): void {
    const points = this.chart.getElementsAtEventForMode(click, 'nearest', {intersect: true}, true);
    if (points.length) {
      const eventId = this.events[points[0].index].id;
      this.router.navigate(['/events/detail/' + eventId]);
    }
  }

  /*drawChart(): void {
    const canvas = this.chart.nativeElement;
    const ctx = canvas.getContext('2d');

    for (let i = 0; i < this.events.length; i++) {

      ctx.fillStyle = 'rgba(0, 0, 200, 0.5)';
      ctx.fillRect(50 * i, 50, 50, 50);
    }
  }*/

}
