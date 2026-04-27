# Jettra NB Designer - Project Knowledge Base (skill.md)

## Project Overview
**JettraNBPlugin** is a NetBeans IDE plugin that provides a visual designer for the **JettraWUI** framework. It allows developers to design web interfaces using a drag-and-drop canvas and automatically synchronizes the design with Java source code.

## Core Architecture

### Java Side (io.jettra.nb)
- **DesignerTopComponent**: The main UI component in NetBeans. It hosts a JavaFX `WebView` to render the designer.
- **JavaConnector**: A bridge class exposed to the JavaScript environment via `webEngine.executeScript`. It handles file I/O (saving/loading Java files) and project context.
- **initFX()**: Configures the WebView, loads `index.html`, and sets up the communication bridge.

### UI Side (index.html)
The designer is a single-page application built with Vanilla JS and CSS, located in `src/main/resources/io/jettra/nb/index.html`.

#### Key Modules:
1. **Component Palette**: A list of JettraWUI components (Div, Button, Modal, etc.) that can be dragged onto the canvas.
2. **Visual Canvas**: The main area where components are rendered. Supports multiple tabs for Modals and Dialogs.
3. **Java Parser (syncCodeToCanvas)**: 
   - Parses Java code line-by-line using regular expressions.
   - Detects variable declarations, instantiations, and `.add()` calls.
   - Supports property settings like `.setStyle()`, `.setProperty()`, and `.setText()`.
   - **Important**: Uses `while(exec)` instead of `matchAll` to ensure compatibility with the JavaFX WebView (NetBeans environment).
4. **Code Generator (generateJavaCode)**: Reconstructs Java source code based on the current canvas hierarchy and component properties.

## Design Patterns & Constraints

### Compatibility
- **JavaScript Engine**: The NetBeans WebView often uses an older JavaScript engine. Avoid modern features like `matchAll` or complex spread operators if not verified.
- **Styling**: Uses CSS variables for theming (Inter font, dark mode aesthetics).

### Component Handling
- **Containers**: Types like `Div`, `Panel`, `Card`, and `UIComponent` are treated as containers that can hold children.
- **Orphans**: During parsing, components are initially created in an `orphansContainer` and moved to their target parent when an `.add()` call is detected.
- **Modals/Dialogs**: These trigger the creation of separate designer tabs (`addModalTab`) to provide a clean workspace for their content.

### Style Persistence
- The parser tracks the `lastVar` to support chained calls:
  ```java
  button.setStyle("color", "red")
        .setStyle("font-weight", "bold");
  ```
- Styles are applied directly to the DOM elements within the designer for real-time preview.

## Project Structure
- `src/main/java/io/jettra/nb/`: Contains the TopComponent and logic classes.
- `src/main/resources/io/jettra/nb/`: Contains `index.html` (the brain of the designer) and other resources.
- `pom.xml`: Maven configuration (current version: 1.0.2.17).

## Key Functions (index.html)
- `addComponent(type, parent, id, p)`: Creates a visual element on the canvas.
- `syncCodeToCanvas()`: The "Source of Truth" synchronizer.
- `generateJavaCode()`: The exporter.
- `switchDesignerTab(id)`: Handles navigation between the main page and modal contents.

## Guidelines for AI
- When modifying `index.html`, ensure all regex operations are robust and compatible with older JS engines.
- Maintain the "Premium" look: use CSS variables and modern spacing.
- Always verify that new components are added to the `containerTypes` array if they support children.
