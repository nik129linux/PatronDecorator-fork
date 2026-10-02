import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'builder',
    loadComponent: () => import('./pages/builder/builder.component').then(m => m.BuilderComponent)
  },
  {
    path: 'pattern',
    loadComponent: () => import('./pages/pattern/pattern.component').then(m => m.PatternComponent)
  },
  {
    path: 'architecture',
    loadComponent: () => import('./pages/architecture/architecture.component').then(m => m.ArchitectureComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
