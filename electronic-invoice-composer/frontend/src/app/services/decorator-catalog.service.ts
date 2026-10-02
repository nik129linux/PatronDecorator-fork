import { Injectable } from '@angular/core';
import { DecoratorConfig, DecoratorInfo } from '../models/invoice.model';

/**
 * Service managing the decorator catalog and the active decorator chain.
 * Provides all UI metadata for rendering decorator cards in Spanish.
 */
@Injectable({
  providedIn: 'root'
})
export class DecoratorCatalogService {

  private readonly catalog: DecoratorInfo[] = [
    {
      type: 'DISCOUNT',
      label: 'Descuento Comercial',
      description: 'Aplica un descuento comercial sobre el subtotal de la factura.',
      icon: '🏷️',
      color: '#34c759',
      hasRate: true,
      hasAmount: false,
      hasEmail: false,
      hasReason: false,
      defaultRate: 0.10,
      category: 'discount'
    },
    {
      type: 'VAT',
      label: 'IVA',
      description: 'Agrega el Impuesto al Valor Agregado sobre la base gravable.',
      icon: '💰',
      color: '#007aff',
      hasRate: true,
      hasAmount: false,
      hasEmail: false,
      hasReason: false,
      defaultRate: 0.19,
      category: 'tax'
    },
    {
      type: 'WITHHOLDING',
      label: 'Retención en la Fuente',
      description: 'Aplica retención en la fuente sobre la base gravable.',
      icon: '📋',
      color: '#5856d6',
      hasRate: true,
      hasAmount: false,
      hasEmail: false,
      hasReason: false,
      defaultRate: 0.025,
      category: 'tax'
    },
    {
      type: 'ICA',
      label: 'ICA (Industria y Comercio)',
      description: 'Aplica el impuesto de Industria y Comercio. La tarifa varía según la jurisdicción.',
      icon: '🏛️',
      color: '#ff9500',
      hasRate: true,
      hasAmount: false,
      hasEmail: false,
      hasReason: false,
      defaultRate: 0.00414,
      category: 'tax'
    },
    {
      type: 'CREDIT_NOTE',
      label: 'Nota Crédito',
      description: 'Registra un ajuste de nota crédito que reduce el valor de la factura.',
      icon: '📝',
      color: '#ff3b30',
      hasRate: false,
      hasAmount: true,
      hasEmail: false,
      hasReason: true,
      defaultAmount: 0,
      category: 'adjustment'
    },
    {
      type: 'DEBIT_NOTE',
      label: 'Nota Débito',
      description: 'Registra un ajuste de nota débito que incrementa el valor de la factura.',
      icon: '📄',
      color: '#ff6b35',
      hasRate: false,
      hasAmount: true,
      hasEmail: false,
      hasReason: true,
      defaultAmount: 0,
      category: 'adjustment'
    },
    {
      type: 'DIGITAL_SIGNATURE',
      label: 'Firma Digital',
      description: 'Simula la firma digital del documento electrónico. (Simulación académica)',
      icon: '🔐',
      color: '#af52de',
      hasRate: false,
      hasAmount: false,
      hasEmail: false,
      hasReason: false,
      category: 'process'
    },
    {
      type: 'DIAN_SUBMISSION',
      label: 'Envío a DIAN',
      description: 'Simula el envío de la factura electrónica a la DIAN. (Simulación académica)',
      icon: '🏢',
      color: '#0a84ff',
      hasRate: false,
      hasAmount: false,
      hasEmail: false,
      hasReason: false,
      category: 'process'
    },
    {
      type: 'CUSTOMER_EMAIL',
      label: 'Correo al Cliente',
      description: 'Simula el envío de la factura al correo electrónico del cliente.',
      icon: '📧',
      color: '#30d158',
      hasRate: false,
      hasAmount: false,
      hasEmail: true,
      hasReason: false,
      category: 'process'
    }
  ];

  getCatalog(): DecoratorInfo[] {
    return [...this.catalog];
  }

  getByType(type: string): DecoratorInfo | undefined {
    return this.catalog.find(d => d.type === type);
  }

  /**
   * Builds a DecoratorConfig from the decorator info and user-provided values.
   */
  buildConfig(type: string, params: Record<string, any>): DecoratorConfig {
    return { type, parameters: params };
  }

  /**
   * Returns labels for decorator types (used in Spanish UI).
   */
  getStatusLabels(): Record<string, string> {
    return {
      'PENDING': 'Pendiente',
      'SENDING': 'Enviando',
      'ACCEPTED': 'Aceptado',
      'REJECTED': 'Rechazado',
      'DELIVERED': 'Entregado',
      'SIMULATED': 'Simulado'
    };
  }

  /**
   * Returns the Spanish translation for a breakdown line type.
   */
  getLineTypeLabel(type: string): string {
    const labels: Record<string, string> = {
      'ITEM': 'Ítem',
      'SUBTOTAL': 'Subtotal',
      'TAX_VAT': 'IVA',
      'TAX_WITHHOLDING': 'Retención',
      'TAX_ICA': 'ICA',
      'DISCOUNT': 'Descuento',
      'CREDIT_NOTE': 'Nota Crédito',
      'DEBIT_NOTE': 'Nota Débito',
      'SIGNATURE': 'Firma Digital',
      'DIAN_SUBMISSION': 'Envío DIAN',
      'EMAIL_NOTIFICATION': 'Correo Electrónico'
    };
    return labels[type] || type;
  }
}
