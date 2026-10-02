import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { InvoiceApiService } from '../../services/invoice-api.service';
import {
  ApiError, InvoiceResponse, QuickComposeRequest, RegimeSummary, TaxRegimeType, TemplateSummary
} from '../../models/invoice.model';

/**
 * Quick compose: issue an invoice from a template (Prototype), a tax regime (Abstract Factory)
 * and a few overrides; the backend assembles it with the Builder.
 */
@Component({
  selector: 'app-quick',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe],
  template: `
    <section class="quick">
      <h1>Quick compose</h1>
      <p class="lead">
        Pick a template to clone, choose a tax regime, change only what differs and preview the invoice.
      </p>

      <p class="notice" *ngIf="loadError">{{ loadError }}</p>

      <div class="grid">
        <form class="card" (ngSubmit)="submit(false)">
          <label>Template (cloned, the stored model is never changed)
            <select name="template" [(ngModel)]="templateId">
              <option value="">None, start empty</option>
              <option *ngFor="let t of templates" [value]="t.id">{{ t.name }}</option>
            </select>
          </label>
          <p class="hint" *ngIf="selectedTemplate">{{ selectedTemplate.description }}</p>

          <label>Tax regime (replaces the decorator chain)
            <select name="regime" [(ngModel)]="regime">
              <option value="">Keep the template chain</option>
              <option *ngFor="let r of regimes" [value]="r.type">{{ r.displayName }}</option>
            </select>
          </label>
          <div class="chips" *ngIf="selectedRegime">
            <span class="chip" *ngFor="let d of selectedRegime.defaultDecorators">{{ d.type }}</span>
            <span class="hint" *ngIf="selectedRegime.defaultDecorators.length === 0">No layers</span>
          </div>

          <h2>Overrides (optional)</h2>
          <label>Invoice number
            <input name="number" [(ngModel)]="invoiceNumber" placeholder="FE-0001">
          </label>
          <div class="row">
            <label>Customer name <input name="cname" [(ngModel)]="customerName"></label>
            <label>Customer NIT <input name="cnit" [(ngModel)]="customerNit"></label>
          </div>
          <label>Customer email <input name="cemail" type="email" [(ngModel)]="customerEmail"></label>
          <div class="row three">
            <label>Item description <input name="idesc" [(ngModel)]="itemDescription"></label>
            <label>Quantity <input name="iqty" type="number" min="1" [(ngModel)]="itemQuantity"></label>
            <label>Unit price <input name="iprice" type="number" min="0" [(ngModel)]="itemPrice"></label>
          </div>

          <div class="actions">
            <button type="submit" class="primary" [disabled]="busy">Preview</button>
            <button type="button" (click)="submit(true)" [disabled]="busy">Issue and store</button>
          </div>
        </form>

        <div class="card" aria-live="polite">
          <p class="notice error" *ngIf="error">
            {{ error.message }}
            <span *ngFor="let d of error.details"><br>{{ d }}</span>
          </p>

          <ng-container *ngIf="result; else empty">
            <h2>Result <small *ngIf="result.id">stored as {{ result.id }}</small></h2>
            <p class="hint">{{ result.description }}</p>
            <div class="chips">
              <span class="chip" *ngFor="let d of result.appliedDecorators">{{ d }}</span>
            </div>
            <table>
              <tr *ngFor="let line of result.breakdown">
                <td>{{ line.label }}</td>
                <td class="num">{{ line.amount | currency:'COP':'symbol-narrow':'1.0-0' }}</td>
              </tr>
              <tr class="total">
                <td>Total</td>
                <td class="num">{{ result.total | currency:'COP':'symbol-narrow':'1.0-0' }}</td>
              </tr>
            </table>
          </ng-container>
          <ng-template #empty>
            <p class="hint" *ngIf="!error">The preview appears here.</p>
          </ng-template>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .quick { max-width: 1100px; margin: 0 auto; padding: 24px 16px; color: #000; }
    h1 { color: #0b2a55; margin: 0 0 4px; }
    h2 { color: #0b2a55; font-size: 1.05rem; margin: 18px 0 8px; }
    h2 small { font-weight: 400; color: #4a5a73; }
    .lead, .hint { color: #4a5a73; }
    .grid { display: grid; gap: 20px; grid-template-columns: 1fr; }
    @media (min-width: 900px) { .grid { grid-template-columns: 1fr 1fr; } }
    .card { background: #fff; border: 2px solid #0b2a55; border-radius: 6px; padding: 18px; }
    label { display: block; margin-bottom: 10px; font-size: 0.9rem; font-weight: 600; }
    input, select { display: block; width: 100%; margin-top: 4px; padding: 8px; border: 1px solid #0b2a55; border-radius: 4px; font: inherit; }
    .row { display: grid; gap: 12px; grid-template-columns: 1fr 1fr; }
    .row.three { grid-template-columns: 2fr 1fr 1fr; }
    .actions { display: flex; gap: 10px; margin-top: 12px; }
    button { padding: 9px 16px; border: 2px solid #0b2a55; border-radius: 4px; background: #fff; color: #0b2a55; font-weight: 600; cursor: pointer; }
    button.primary { background: #0b2a55; color: #fff; }
    button:disabled { opacity: 0.5; cursor: wait; }
    .chips { display: flex; flex-wrap: wrap; gap: 6px; margin: 4px 0 8px; }
    .chip { border: 1px solid #0b2a55; border-radius: 12px; padding: 2px 10px; font-size: 0.78rem; color: #0b2a55; }
    table { width: 100%; border-collapse: collapse; margin-top: 8px; }
    td { padding: 6px 4px; border-bottom: 1px solid #d5dbe6; }
    .num { text-align: right; font-variant-numeric: tabular-nums; }
    .total td { font-weight: 700; border-top: 2px solid #0b2a55; border-bottom: none; }
    .notice { border: 1px solid #0b2a55; padding: 8px 12px; border-radius: 4px; }
    .notice.error { border-color: #b00020; color: #b00020; }
  `]
})
export class QuickComponent implements OnInit {
  templates: TemplateSummary[] = [];
  regimes: RegimeSummary[] = [];
  loadError = '';

  templateId = '';
  regime: TaxRegimeType | '' = '';
  invoiceNumber = '';
  customerName = '';
  customerNit = '';
  customerEmail = '';
  itemDescription = '';
  itemQuantity = 1;
  itemPrice: number | null = null;

  busy = false;
  result: InvoiceResponse | null = null;
  error: ApiError | null = null;

  constructor(private api: InvoiceApiService) {}

  ngOnInit(): void {
    forkJoin({ templates: this.api.getTemplates(), regimes: this.api.getRegimes() }).subscribe({
      next: ({ templates, regimes }) => {
        this.templates = templates;
        this.regimes = regimes;
      },
      error: () => (this.loadError = 'Could not load templates and regimes. Is the backend running?')
    });
  }

  get selectedTemplate(): TemplateSummary | undefined {
    return this.templates.find(t => t.id === this.templateId);
  }

  get selectedRegime(): RegimeSummary | undefined {
    return this.regimes.find(r => r.type === this.regime);
  }

  /** Builds the request sending only the overrides the user actually filled. */
  buildRequest(persist: boolean): QuickComposeRequest {
    const request: QuickComposeRequest = { persist };
    if (this.templateId) request.templateId = this.templateId;
    if (this.regime) request.regime = this.regime;
    if (this.invoiceNumber.trim()) request.invoiceNumber = this.invoiceNumber.trim();
    if (this.customerName.trim() && this.customerNit.trim()) {
      request.customer = {
        name: this.customerName.trim(),
        nit: this.customerNit.trim(),
        email: this.customerEmail.trim(),
        address: '', city: '', phone: ''
      };
    }
    if (this.itemDescription.trim() && this.itemPrice !== null && this.itemQuantity >= 1) {
      request.items = [{
        description: this.itemDescription.trim(),
        quantity: this.itemQuantity,
        unitPrice: this.itemPrice
      }];
    }
    return request;
  }

  submit(persist: boolean): void {
    this.busy = true;
    this.error = null;
    this.api.quickCompose(this.buildRequest(persist)).subscribe({
      next: response => {
        this.result = response;
        this.busy = false;
      },
      error: (e: HttpErrorResponse) => {
        this.result = null;
        this.busy = false;
        this.error = e.error?.message
          ? (e.error as ApiError)
          : { code: 'NETWORK', message: 'Could not reach the server.', details: [], timestamp: '' };
      }
    });
  }
}
