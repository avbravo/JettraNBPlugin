# JettraNBPlugin

## Descripción General
`JettraNBPlugin` (Jettra NB Designer) es un complemento/plugin para el IDE Apache NetBeans. Ayuda a los desarrolladores a construir rápidamente aplicaciones JettraStack a través de la autogeneración de código, plantillas y herramientas visuales en el editor.

## Detalles Específicos
- **Arquitectura general**: Arquitectura basada en los módulos nativos del NetBeans RCP.
- **Dependencias clave**: APIs internas de Apache NetBeans (FileSystems, DataSystems, Nodes, Windows).
- **Roles dentro del sistema**: Agilizar la experiencia de desarrollo (DX - Developer Experience) abstrayendo el setup inicial de repositorios, controladores, vistas y configuraciones.

## Características Detalladas
- **Asistentes (Wizards)**: Generadores paso a paso para crear un microservicio completo o archivos de configuración.
- **Plantillas (Templates)**: Proporciona las plantillas para generar código estándar conforme a los patrones estipulados por JettraStack (ej. la creación de un nuevo `CrudView`).
- **Integración con Maven**: Analiza el POM del proyecto para autoconfigurar las dependencias.

## Guía de Entrenamiento (AI / Nuevas Características)
- Cuando el framework JettraStack introduzca un cambio estructural importante, las plantillas en `JettraNBPlugin` deben actualizarse.
- Toda nueva característica debe usar las APIs estandarizadas de NetBeans y manejar los hilos visuales del editor (EDT) de forma asíncrona para no congelar el IDE.
