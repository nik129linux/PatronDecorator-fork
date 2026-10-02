# Sistema de Facturación Electrónica DIAN (Colombia)
## Taller: Patrones Estructurales y Decorativos

El desarrollo de este sistema resuelve uno de los retos más complejos en el software financiero: la adaptabilidad a reglas tributarias heterogéneas y en constante evolución. Utilizando un objeto base () acoplado a Decoradores Complejos, el sistema permite ensamblar de forma dinámica y en tiempo de ejecución comportamientos fiscales avanzados (como IVA, ICA, retenciones en la fuente, descuentos, notas de ajuste, firma digital y validación previa ante la DIAN) sin alterar el núcleo de la lógica de negocio.

Objeto base: Factura básica
Decoradores complejos:
Con retención en la fuente
Con IVA / ICA
Con descuento comercial
Con nota crédito / nota débito
Con firma digital (XML firmado)
Con envío automático a la DIAN
Con copia de correo al cliente

Por qué es complejo: Combina reglas tributarias, validaciones y múltiples capas de comportamiento que cambian según el tipo de cliente y régimen.

Frontend: Formulario de facturación donde se activan/desactivan las capas y se genera el XML/PDF en tiempo real.

### Nombres: Equipo de Desarrollo:

Cristian Santiago Parra (Líder de Proyecto)

William Chavez Bravo (Desarrollador / Arquitecto)

Oscar Felipe Hernandez (Desarrollador / Diseñador)

## Módulos Funcionales, de diseño y normativos.

### 1. Marco Legal y Normativo de Referencia (Colombia)
El sistema debe cumplir de forma rigurosa con:
- **Estatuto Tributario (E.T.):** Artículo 616-1 (Obligación de facturar, validación previa y requisitos de la factura y documentos electrónicos).
- **Resolución DIAN Vigente:** Resolución 000165 de 2023 (y sus modificaciones técnicas), que regula los anexos técnicos de facturación electrónica, notas crédito, notas débito y validación previa.
- **Decreto Único Reglamentario (DUR):** Decreto 1625 de 2016 en materia fiscal y tributaria.

### 2. Stack Tecnológico y Arquitectura
- **Backend:** Java (con Spring Boot), aplicando Domain-Driven Design (DDD), arquitectura limpia y uso de DTOs (Data Transfer Objects) para desacoplar las capas de negocio.
- **Frontend:** Angular, con componentes reactivos, tipado estricto en TypeScript y una interfaz interactiva de alta fluidez.
- **Rendimiento:** Optimizado estrictamente para garantizar **baja latencia** en el procesamiento, armado de estructuras XML, firma criptográfica y comunicación transaccional.

### 3. Abstracciones y Modelado de Reglas Fiscales (Backend)
El sistema debe estructurar clases, entidades y servicios que reflejen las siguientes abstracciones tributarias colombianas:
- **Tipos de Regímenes / Contribuyentes:** 
  - Responsables de IVA (Régimen Ordinario).
  - No Responsables de IVA.
  - Contribuyentes del Régimen Simple de Tributación (RST).
- **Cálculos y Capas Impositivas:**
  - Impuesto sobre las Ventas (IVA) con manejo de tarifas (general, reducida, exenta, excluida).
  - Impuesto de Industria y Comercio (ICA) parametrizable por municipio/actividad económica.
  - Retenciones en la fuente (Retefuente a título de Renta, ReteIVA, ReteICA).
  - Descuentos comerciales (condicionados e incondicionados) aplicados antes o después de impuestos según la norma.
- **Documentos Electrónicos y Seguridad:**
  - Emisión de Factura Electrónica de Venta, Nota Crédito y Nota Débito vinculadas mediante el **CUFE** (Código Único de Factura Electrónica) y **CUDE**.
  - Generación de **Firma Digital (XML firmado)** mediante certificados X.509 válidos para los requerimientos de seguridad de la DIAN.
- **Integraciones:**
  - Envío automático de documentos al servicio de validación previa de la DIAN.
  - Envío automatizado de copia de cortesía por correo electrónico al adquiriente (incluyendo adjuntos XML y representación gráfica PDF).

### 4. Interfaz de Usuario y Experiencia (Frontend)
- **Formulario Dinámico:** Módulo interactivo que active o desactive capas impositivas, retenciones y tipos de notas según el régimen del emisor y el adquirente.
- **Vista Previa en Tiempo Real:** Generación concurrente de la representación gráfica (PDF) y previsualización del esquema XML estructurado.
- **Diseño UI/UX:**
  - Estética formal e institucional.
  - Paleta de colores: **Azul oscuro** para contenedores principales y bordes; **Blanco** para los espacios de contenido general.
  - Tipografía predominantemente en **negro** para asegurar una legibilidad impecable.

### 5. Estándares de Código y Documentación
- **Código Fuente:** Redactado completamente en **inglés** (nombres de clases, métodos, variables, atributos y endpoints).
- **Comentarios:** Explicaciones técnicas y de negocio redactadas en **español** dentro del código.
- **Documentación:** Incluir un archivo `documentation.md` detallado en **español**, explicando el mapeo de las normas tributarias implementadas, el flujo de validación ante la DIAN y la guía de despliegue del sistema.


---

## Creational patterns added (workshop extension)

The project already applies **Decorator** (`decorator/`). This extension adds three creational patterns
around one new feature, **quick compose**: issue an invoice from a saved template, a tax regime and a few
overrides, instead of filling the whole form by hand.

```
POST /api/invoices/quick  {templateId?, regime?, invoiceNumber?, seller?, customer?, items?, persist?}
        |
        |-- 1. Prototype ........ clone the template (never touches the stored original)
        |-- 2. Builder .......... load the clone, apply the overrides, validate everything at once
        |-- 3. Abstract Factory . if a regime was chosen, replace the chain with that regime's default chain
        `-- 4. InvoiceService ... preview or persist (existing code, unchanged)
```

| Pattern | Where (`electronic-invoice-composer/backend/src/main/java/com/invoicecomposer/`) | What it does here | Author |
|---|---|---|---|
| **Prototype** | `creational/prototype/` (`Prototype`, `InvoiceTemplate`, `InvoiceTemplateRegistry`) plus `copy()` / `deepCopy()` on `Seller`, `Customer`, `InvoiceItem`, `InvoiceData`, `DecoratorConfigDto`, `ComposeInvoiceRequest` | A registry of master invoice templates that only hands out deep copies. Cloning a template lets you issue recurring invoices without editing the stored model. | Nicolas Casanova |
| **Builder** | `creational/builder/InvoiceRequestBuilder` | Assembles a `ComposeInvoiceRequest` step by step (number, date, seller, customer, items, decorators) and validates all rules in `build()`, reporting every problem at once. | Samuel Vallejo |
| **Abstract Factory** | `creational/abstractfactory/` (`TaxRegimeFactory`, one concrete factory per `TaxRegimeType`, and the products `IndirectTaxLayer`, `WithholdingLayer`, `ComplianceLayer`) | One factory per tax regime (ordinary, non-VAT, Simple regime) creates a consistent family: taxes, withholdings and compliance steps. `defaultChain()` is the regime's decorator chain. | Samuel Vallejo |
| **Feature glue** | `service/QuickComposeService`, `controller/QuickComposeController`, `dto/QuickComposeRequest`, `dto/TemplateSummary`, `dto/RegimeSummary` | Orchestrates the three patterns and exposes the endpoints below. | Nicolas Casanova |

### New endpoints

| Method | Path | Returns |
|---|---|---|
| GET | `/api/invoices/templates` | The cloneable templates (id, name, regime, request) |
| GET | `/api/invoices/regimes` | Each tax regime and its default decorator chain |
| POST | `/api/invoices/quick` | An `InvoiceResponse`: 200 when previewed, 201 when `persist` is true |

### Frontend

The Angular app gets a "Quick compose" panel (`frontend/src/app/`): pick a template and/or a regime, edit the
fields, and see the preview. Implemented by Nicolas Casanova after the backend lands.

### Status

- [x] Contract and skeletons for Builder and Abstract Factory
- [x] Prototype and the quick-compose feature (backend)
- [ ] Builder implementation
- [ ] Abstract Factory implementation (3 concrete factories)
- [x] Quick-compose panel in Angular (`/quick`)
