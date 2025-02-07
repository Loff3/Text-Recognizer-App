# Text Recognizer App
### Create PDF's by scanning text using your phones camera
## Key Features

- **Text Scanning:** Leverages Google ML Kit to detect and extract text.
- **PDF Conversion:** Uses iTextPDF to convert text into a PDF file.
- **File Saving:** Employs the Storage Access Framework (SAF) for choosing where to store the PDF.
- **Modern UI:** Built with Jetpack Compose and follows a Single Activity Architecture.

## Main Components

- **MainActivity & PermissionHandler:** Initialize the app and manage camera permissions.
- **Navigation:** Handles moving between different screens.
- **LandingScreen:** Welcomes users with a custom font and gradient background.
- **CameraTextRecognitionScreen & TextRecognitionAnalyzer:** Core functionality for text scanning.
- **DocumentSharedViewModel & DocumentEditingScreen:** Share data between components and convert scanned text to PDF.

## Setup

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/text-recognizer.git

![Screenshot of the app](https://github.com/Loff3/Text-Recognizer-App/blob/main/app/src/main/res/screenshots/1.png)
![Screenshot of the app](app\src\main\res\screenshots\2.png)
![Screenshot of the app](app\src\main\res\screenshots\3.png)
![Screenshot of the app](app\src\main\res\screenshots\4.png)

