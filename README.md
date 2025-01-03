# Nested Notes Android App

## Overview
This Android application allows users to create and manage notes with the unique ability to include **nested expandable comments**. Written in Java, the app provides an intuitive and structured approach to note-taking, enabling users to better organize and expand their ideas.

## Features
- **Nested Expandable Comments**: Users can insert expandable comments within their notes, which can be expanded or minimized.
- **Hierarchical Nesting**: Expandable comments can contain other expandable comments, creating a hierarchical structure for better organization.
- **Interactive UI**:
    - A button allows users to easily insert an expandable comment.
    - Clickable symbols (`■`, `◆`) let users expand or minimize comments dynamically.

## How It Works
1. **Inserting Expandable Comments**:
    - Tap the "Insert Expandable Comment" button to add a `■` to your note at the current cursor position.
    - Clicking `■` will expand the comment, turning it into `◆` and revealing its content.

2. **Expanding and Minimizing Comments**:
    - Click `◆` to minimize the comment, hiding its content and reverting it to `■`.
    - Comments can be nested indefinitely, allowing for hierarchical structures.

## Technologies Used
- **Java**: Core development language for the app.
- **SQLite**: Used for persistent storage of notes and their structures.
- **Android SDK**: For building the user interface and handling app functionality.

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Schakirov/Nestednotes.git
   ```
2. Open the project in Android Studio.
3. Build and run the app on an emulator or physical device.

## Usage
- Start the app to view or create notes.
- Use the "Insert Expandable Comment" button to add expandable comments within your notes.

## Contributing
Contributions are welcome! Feel free to fork the repository and submit a pull request with your improvements or bug fixes.

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

