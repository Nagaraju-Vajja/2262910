import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent {
  showLogout: boolean = false;

  constructor(private router: Router) {}

  ngOnInit() {
    // Subscribe to route changes
    this.router.events.subscribe(() => {
      this.checkLoginRoute();
    });

  
    this.checkLoginRoute();
  }

  checkLoginRoute() {
    this.showLogout = !this.router.url.includes('/login');
  }

  logout() {
    this.router.navigate(['/login']);
  }
}

