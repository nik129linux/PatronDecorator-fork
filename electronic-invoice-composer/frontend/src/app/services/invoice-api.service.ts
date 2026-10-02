import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  ComposeInvoiceRequest, InvoiceResponse, QuickComposeRequest, RegimeSummary, TemplateSummary
} from '../models/invoice.model';

/**
 * Service for communicating with the Java backend REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class InvoiceApiService {

  private readonly baseUrl = environment.apiUrl + '/invoices';

  constructor(private http: HttpClient) {}

  /**
   * Preview an invoice composition without persisting.
   */
  preview(request: ComposeInvoiceRequest): Observable<InvoiceResponse> {
    return this.http.post<InvoiceResponse>(`${this.baseUrl}/preview`, request);
  }

  /**
   * Compose and persist an invoice.
   */
  compose(request: ComposeInvoiceRequest): Observable<InvoiceResponse> {
    return this.http.post<InvoiceResponse>(this.baseUrl, request);
  }

  /**
   * Retrieve a composed invoice by ID.
   */
  getById(id: string): Observable<InvoiceResponse> {
    return this.http.get<InvoiceResponse>(`${this.baseUrl}/${id}`);
  }

  /**
   * Retrieve all stored invoices.
   */
  getAll(): Observable<InvoiceResponse[]> {
    return this.http.get<InvoiceResponse[]>(this.baseUrl);
  }

  /**
   * Get available decorator types.
   */
  getAvailableDecorators(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/decorators`);
  }

  /**
   * List the invoice templates that can be cloned (Prototype).
   */
  getTemplates(): Observable<TemplateSummary[]> {
    return this.http.get<TemplateSummary[]>(`${this.baseUrl}/templates`);
  }

  /**
   * List the tax regimes with their default decorator chain (Abstract Factory).
   */
  getRegimes(): Observable<RegimeSummary[]> {
    return this.http.get<RegimeSummary[]>(`${this.baseUrl}/regimes`);
  }

  /**
   * Compose an invoice from a template, a regime and overrides (Prototype + Builder + Abstract Factory).
   */
  quickCompose(request: QuickComposeRequest): Observable<InvoiceResponse> {
    return this.http.post<InvoiceResponse>(`${this.baseUrl}/quick`, request);
  }
}
