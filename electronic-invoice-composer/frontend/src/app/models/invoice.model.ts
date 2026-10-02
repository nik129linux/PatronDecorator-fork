/**
 * TypeScript interfaces mirroring the Java backend DTOs.
 * All property names match the JSON serialization from Jackson.
 */

export interface Customer {
  name: string;
  nit: string;
  email: string;
  address: string;
  city: string;
  phone: string;
}

export interface Seller {
  name: string;
  nit: string;
  address: string;
  city: string;
  phone: string;
}

export interface InvoiceItem {
  description: string;
  quantity: number;
  unitPrice: number;
}

export interface InvoiceData {
  invoiceNumber: string;
  issueDate: string;
  customer: Customer;
  seller: Seller;
  items: InvoiceItem[];
}

export interface DecoratorConfig {
  type: string;
  parameters: Record<string, any>;
}

export interface ComposeInvoiceRequest {
  invoiceData: InvoiceData;
  decorators: DecoratorConfig[];
}

export interface LineDetail {
  type: string;
  label: string;
  description: string;
  amount: number;
  rate: number;
  baseAmount: number;
}

export interface InvoiceResponse {
  id: string | null;
  invoiceData: InvoiceData;
  subtotal: number;
  taxableBase: number;
  total: number;
  breakdown: LineDetail[];
  metadata: Record<string, any>;
  description: string;
  xml: string;
  appliedDecorators: string[];
}

export interface ApiError {
  code: string;
  message: string;
  details: string[];
  timestamp: string;
}

/**
 * Decorator metadata used by the UI to render decorator cards.
 */
export interface DecoratorInfo {
  type: string;
  label: string;
  description: string;
  icon: string;
  color: string;
  hasRate: boolean;
  hasAmount: boolean;
  hasEmail: boolean;
  hasReason: boolean;
  defaultRate?: number;
  defaultAmount?: number;
  category: 'tax' | 'discount' | 'adjustment' | 'process';
}

export type TaxRegimeType = 'ORDINARY' | 'NON_VAT' | 'SIMPLE';

/** A cloneable invoice template (Prototype registry entry). */
export interface TemplateSummary {
  id: string;
  name: string;
  description: string;
  regime: TaxRegimeType;
  request: ComposeInvoiceRequest;
}

/** A tax regime with the decorator chain its Abstract Factory produces. */
export interface RegimeSummary {
  type: TaxRegimeType;
  displayName: string;
  defaultDecorators: DecoratorConfig[];
}

/** Request of the quick-compose endpoint. Every field is optional. */
export interface QuickComposeRequest {
  templateId?: string;
  regime?: TaxRegimeType;
  invoiceNumber?: string;
  customer?: Customer;
  items?: InvoiceItem[];
  persist: boolean;
}
