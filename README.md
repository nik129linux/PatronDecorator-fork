# PatronDecorator-
1. Sistema de Facturación Electrónica DIAN (Colombia)

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

Nombres: William Chavez Bravo, Christian Santiago Parra (Lider), Oscar Felipe Hernandez 