# Documentación Técnica — Sistema de Facturación Electrónica DIAN
## Implementación del Patrón de Diseño Decorator (GoF)

---

### Autores / Equipo de Desarrollo
- **Cristian Santiago Parra** (Líder de Proyecto)
- **William Chavez Bravo** (Desarrollador / Arquitecto)
- **Oscar Felipe Hernandez** (Desarrollador / Diseñador)

---

## 1. Introducción y Propósito del Proyecto

El **Electronic Invoice Composer** es un sistema académico diseñado para demostrar la aplicabilidad práctica, elegancia y potencia del **Patrón de Diseño Estructural Decorator** en un escenario real y complejo: el ecosistema de **Facturación Electrónica en Colombia (DIAN)**.

En el dominio tributario colombiano, una factura comercial básica sufre variaciones y adiciones de responsabilidades según:
- El régimen fiscal del emisor y del receptor (Responsable de IVA, No Responsable, Régimen Simple RST).
- Actividades económicas y municipios (Tarifas de ICA y ReteICA).
- Retenciones fiscales aplicables (Retención en la fuente a título de renta, ReteIVA).
- Ajustes comerciales (Descuentos a pie de factura o condicionados).
- Eventos de corrección contable (Notas Débito y Crédito vinculadas a un CUFE).
- Requisitos de seguridad jurídica e informática (Firma digital X.509, hash SHA-384 del CUFE, generación de XML UBL 2.1).
- Trámites de interoperabilidad y notificación (Validación previa ante DIAN, despacho de correo electrónico de cortesía con representación gráfica y XML adjunto).

Intentar modelar todas estas permutaciones mediante **herencia tradicional** provocaría una **explosión combinatoria de subclases** (ej. `FacturaConIva`, `FacturaConIvaYRetefuente`, `FacturaConIvaRetefuenteYDescuento`, etc.). El **Patrón Decorator** resuelve este problema permitiendo envolver dinámicamente un objeto base con capas adicionales de comportamiento en tiempo de ejecución, respetando el principio **Open/Closed (SOLID)**.

---

## 2. Arquitectura del Patrón Decorator

### 2.1 Estructura de Clases e Interfaces

```
                           +------------------------+
                           |   <<interface>>        |
                           |   InvoiceComponent     |
                           +------------------------+
                           | + calculateTotal()     |
                           | + getLineDetails()     |
                           | + getMetadata()        |
                           | + getInvoiceData()     |
                           +------------------------+
                                      ^
                                      |
         +----------------------------+----------------------------+
         |                                                         |
+------------------------+                               +------------------------+
|      BasicInvoice      |                               |    InvoiceDecorator    |
|  (Concrete Component)  |                               |   (Decorator Base)     |
+------------------------+                               +------------------------+
| - invoiceData          |                               | # wrappedComponent     |
| + calculateTotal()     |                               +------------------------+
| + getLineDetails()     |                               | + calculateTotal()     |
+------------------------+                               | + getLineDetails()     |
                                                         +------------------------+
                                                                     ^
                                                                     |
  +------------------------------------------------------------------+------------------------------------------------------------------+
  |                   |                  |                     |                   |                   |               |               |
+---------------+ +------------------+ +-----------------+ +-----------------+ +-------------------+ +---------------+ +---------------+ +---------------+
| VatDecorator  | | WithholdingDec.  | | IndAndCommTaxDec| | CommercialDisc. | | CreditNoteDec.    | | DebitNoteDec. | | DigitalSign.  | | DianSubmiss.  |
+---------------+ +------------------+ +-----------------+ +-----------------+ +-------------------+ +---------------+ +---------------+ +---------------+
```

### 2.2 Roles del Patrón Implementados

1. **Component (`InvoiceComponent`):**
   - Interfaz común que define el contrato de una factura: cálculo del total (`calculateTotal()`), obtención del desglose de conceptos (`getLineDetails()`), metadata acumulada (`getMetadata()`) y acceso a los datos de cabecera (`getInvoiceData()`).

2. **Concrete Component (`BasicInvoice`):**
   - Representa la factura electrónica pura sin agregados impositivos ni administrativos. Calcula el subtotal bruto a partir del valor de las líneas de producto (`quantity * unitPrice`).

3. **Decorator Base (`InvoiceDecorator`):**
   - Clase abstracta que implementa `InvoiceComponent` y mantiene una referencia interna (`wrappedComponent`) al componente que decora. Delega las operaciones básicas por defecto, permitiendo que las subclases agreguen responsabilidades antes o después de delegar.

4. **Decoradores Concretos (9 Implementaciones):**
   - **`VatDecorator` (IVA):** Calcula el Impuesto sobre las Ventas (general 19%, diferencial 5%) sobre la base gravable y suma el tributo.
   - **`WithholdingDecorator` (Retención en la Fuente):** Aplica retenciones en la fuente a título de Renta (ej. 2.5%, 3.5%) o ReteIVA (15% del IVA), restándolas del total a pagar por el cliente.
   - **`IndustryAndCommerceTaxDecorator` (ICA):** Deduce el gravamen municipal (expresado comúnmente en por mil o porcentaje) según la actividad económica y municipio.
   - **`CommercialDiscountDecorator` (Descuento Comercial):** Aplica deducciones comerciales (porcentaje o valor fijo) antes de la liquidación de tributos, modificando la base gravable.
   - **`CreditNoteDecorator` (Nota Crédito):** Aplica una disminución al valor de la factura, referenciando el motivo contable (anulación parcial, devolución).
   - **`DebitNoteDecorator` (Nota Débito):** Suma valores por concepto de intereses de mora, gastos de cobranza o ajustes contables.
   - **`DigitalSignatureDecorator` (Firma Digital):** Simula la generación de la firma digital X.509 sobre el documento, estampando el hash de seguridad criptográfica y timestamp.
   - **`DianSubmissionDecorator` (Transmisión DIAN):** Simula el consumo del web service de validación previa ante la DIAN, generando un `TrackId`, estado de aprobación y registro de validación previa.
   - **`CustomerEmailDecorator` (Copia de Cortesía):** Simula el despacho de la factura aprobada al correo electrónico del adquiriente, adjuntando la representación gráfica (PDF) y el XML UBL 2.1.

5. **Factoría de Composición Dinámica (`DecoratorFactory`):**
   - Registro desacoplado que instancia y encadena los decoradores en tiempo de ejecución a partir de una lista de configuraciones (`DecoratorConfigDto`), evitando estructuras monolíticas `if/else` o `switch`.

---

## 3. Mapeo de Normativa Tributaria Colombiana

| Concepto Fiscal | Base Normativa | Regla de Negocio Implementada | Decorador Asociado |
| :--- | :--- | :--- | :--- |
| **Obligación de Facturar y Validación Previa** | Estatuto Tributario Art. 616-1; Resolución DIAN 000165 de 2023 | La factura debe estructurarse electrónicamente, firmarse y validarse antes de ser expedida al cliente. | `DigitalSignatureDecorator`, `DianSubmissionDecorator` |
| **Impuesto sobre las Ventas (IVA)** | Estatuto Tributario Art. 468 | Tarifa general del 19% o tarifas diferenciales calculadas sobre la base gravable. | `VatDecorator` |
| **Retención en la Fuente (Renta)** | Estatuto Tributario Art. 365, 392 | Mecanismo de recaudo anticipado deducible del saldo neto a pagar. | `WithholdingDecorator` |
| **Impuesto de Industria y Comercio (ICA)** | Ley 14 de 1983, Acuerdos Municipales | Impuesto territorial aplicado según actividad económica (CIIU) y municipio. | `IndustryAndCommerceTaxDecorator` |
| **Descuentos Comerciales** | Estatuto Tributario Art. 454 | Descuentos condicionados o incondicionados que afectan la base antes de impuestos. | `CommercialDiscountDecorator` |
| **Notas de Ajuste (Crédito / Débito)** | Anexo Técnico Facturación Electrónica DIAN | Mecanismos formales para ajustar saldos, vinculados documentalmente a la factura previa. | `CreditNoteDecorator`, `DebitNoteDecorator` |
| **Copia de Cortesía al Adquiriente** | Decreto Único Reglamentario 1625 de 2016 | Obligación del emisor de enviar el contenedor electrónico (.zip con XML + PDF) al correo del cliente. | `CustomerEmailDecorator` |

---

## 4. Stack Tecnológico y Componentes del Sistema

### Backend (Java & Spring Boot)
- **Java 17+** / **Spring Boot 3.3.4**
- **Arquitectura Limpia & DDD:** Separación estricta entre capa de dominio (`domain`), lógica del patrón (`decorator`), transferencia de datos (`dto`), servicios (`service`), controladores (`controller`) y excepciones (`exception`).
- **Validaciones:** `jakarta.validation` para validación estricta de entradas.
- **XML UBL 2.1:** Generador de estructura XML de simulación académica (`XmlGenerator`).
- **Pruebas Unitarias:** JUnit 5 con suite completa de pruebas (`DecoratorPatternTest`) cubriendo cada decorador individual, composiciones complejas, orden de ejecución y manejo de errores.

### Frontend (Angular 18+)
- **Framework:** Angular con componentes reactivos y tipado estricto en TypeScript.
- **Diseño UI/UX Institucional:**
  - Paleta de color formal: Azul Marino Institucional (`#1e3a8a`, `#0f172a`), bordes finos, fondo blanco puro para áreas de trabajo, y tipografía en escala de grises oscuros / negro para máxima legibilidad.
  - Formulario dinámico para ingresar datos del emisor, cliente e ítems de factura.
  - Catálogo interactivo de capas/decoradores con switches activos, controles de tarifas y parámetros en tiempo real.
  - Línea de tiempo visual del orden de la cadena de decoradores (pipeline de procesamiento).
  - Resumen financiero en vivo (Subtotal, Descuentos, IVA, Retenciones, Notas, Total Neto).
  - Pestañas de previsualización:
    - **Representación Gráfica (PDF / Factura Institucional)** con formato oficial DIAN, código QR simulado y tabla de retenciones.
    - **Esquema XML Estructurado** con formateo de sintaxis.
    - **Pipeline de Decoradores** que explica el flujo paso a paso de la llamada recursiva del patrón.

---

## 5. Guía de Ejecución y Despliegue Local

### Requisitos Previos
- **Java JDK 17** o superior instalado.
- **Maven 3.8+** (o el wrapper de Maven).
- **Node.js 18+** y **npm 9+**.

### 5.1 Ejecución del Backend

1. Dirigirse al directorio del backend:
   ```bash
   cd electronic-invoice-composer/backend
   ```
2. Ejecutar las pruebas unitarias:
   ```bash
   mvn test
   ```
3. Iniciar el servidor Spring Boot:
   ```bash
   mvn spring-boot:run
   ```
   *El backend quedará escuchando en `http://localhost:8080`.*

### 5.2 Ejecución del Frontend

1. Dirigirse al directorio del frontend:
   ```bash
   cd electronic-invoice-composer/frontend
   ```
2. Instalar dependencias (si no se han instalado):
   ```bash
   npm install
   ```
3. Iniciar el servidor de desarrollo Angular:
   ```bash
   npm start
   ```
   *El frontend estará disponible en el navegador en `http://localhost:4200`.*

---

## 6. Endpoints de la API REST

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/invoices/decorators` | Lista todos los decoradores disponibles con sus tipos, parámetros requeridos y descripciones. |
| `POST` | `/api/invoices/preview` | Compone la factura con la cadena de decoradores solicitada en memoria y devuelve el resultado sin persistirlo. |
| `POST` | `/api/invoices/compose` | Compone la factura con la cadena de decoradores y la almacena en el repositorio en memoria. |
| `GET` | `/api/invoices/{id}` | Recupera una factura previamente compuesta por su identificador. |
| `GET` | `/api/invoices` | Lista todas las facturas procesadas en la sesión. |

---

## 7. Conclusiones

La implementación de este proyecto demuestra que el **Patrón Decorator**:
1. **Elimina la rigidez** de los modelos jerárquicos basados en herencia en contextos donde los requisitos fiscales cambian con frecuencia.
2. **Permite la composición dinámica**: los decoradores pueden agregarse, eliminarse o reordenarse en tiempo de ejecución según el contexto de cada transacción.
3. **Mantiene alta cohesión y bajo acoplamiento**: cada clase decoradora tiene una única responsabilidad bien definida (principio de responsabilidad única - SRP).
