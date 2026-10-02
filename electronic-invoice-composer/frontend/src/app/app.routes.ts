import { Routes } from '@angular/router';

/**
 * The home, builder, pattern and architecture pages were routed here but never committed,
 * which made the whole app fail to build. Only the quick compose page exists for now.
 */
export const routes: Routes = [
  {
    path: 'quick',
    loadComponent: () => import('./pages/quick/quick.component').then(m => m.QuickComponent)
  },
  {
    path: '**',
    redirectTo: 'quick'
  }
];
