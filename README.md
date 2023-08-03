# Noteworthy
Noteworthy is a simple AI assisted note taking app that uses OpenAI's DaVinci GPT-3 LLM model to provide AI based autocompletions, suggestions, and summaries for your notes. For privacy, notes can be created in private mode, which will not be sent to OpenAI, but will not be able to use the AI features. Notes can also be created using voice input, can be favorited, and categorized for filtering.

Notes are synced from device to device with a cloud backup, and are searchable by title and content.

Noteworthy is built in Kotlin using the Jetpack Compose UI toolkit and Material 2 design system. Google Firebase is used for authentication, cloud storage, database, and serverless functions. Algolia is used for full text search. OpenAI is used for AI based autocompletion, suggestions, and summaries. Google's SpeechRecognizer API is used for voice recognition which supports both cloud based and on-device speech recognition (if available).
## Example Gif
![Alt Text](https://github.com/JetLiTheQT/Noteworthy/blob/main/noteworthy.0bc69d5dc7a14673dfac.gif)

## Setup
To configure the OpenAI API, add your API key to the global gradle properties (`.gradle/gradle.properties`) with the value `OPENAI_API_KEY="KEY_HERE"`
