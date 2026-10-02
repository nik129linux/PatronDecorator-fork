# PatronDecorator-
### Sistema de Facturación Electrónica DIAN (Colombia)

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

Christian Santiago Parra (Líder de Proyecto)

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
