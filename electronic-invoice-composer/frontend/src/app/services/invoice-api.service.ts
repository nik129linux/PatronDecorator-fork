import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ComposeInvoiceRequest, InvoiceResponse } from '../models/invoice.model';

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
}
