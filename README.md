# Hummingbird TTS Robot

This Java project combines physical computing using the Hummingbird Duo robot with text-to-speech (TTS) capabilities using the Spring AI (OpenAI) API. It provides an educational framework to create interactive, speaking robots for classrooms, demos, or experimentation.

---

## Features

- Control **motors**, **servos**, **LEDs**, and **sensors** via `HummingbirdRobot`
- Generate **speech from text** using `OpenAiAudio` and `HummingbirdAudio`
- Play TTS audio through robot speakers or system audio
- Simple structure for easy integration in student projects

---

## Requirements

- Java 8 or later
- Hummingbird Duo hardware
- Dependencies:
  - Hummingbird Java SDK (`edu.cmu.ri.createlab.hummingbird`)
  - Internet access for Spring AI TTS
  - `application.properties` file for storing the OpenAI key

---

## Setup Instructions

### 1. Clone and Compile

```bash
javac *.java
```

### 2. Add OpenAI API Key

Create a file named `application.properties` in your working directory:

```properties
spring.application.name=texttospeech
ai.provider=openai
ai.openai.api-key=your_key_here
```

This file is used by `OpenAiAudio.java` to load your API key.

```bash
  OpenAiAudioApi openAiAudioApi = new OpenAiAudioApi("key");
```

---

## Class Overview

### 🧠 `HummingbirdRobot.java`

Controls all Hummingbird Duo hardware. Key methods:

```java
HummingbirdRobot robot = new HummingbirdRobot();

// Move servo 1 to position 90
robot.setServoPosition(1, 90);

// Turn LED 1 to full brightness
robot.setLED(1, 255);

// Read sensor value on port 1
int sensorVal = robot.getSensorValue(1);

// Stop everything immediately
robot.emergencyStop();

// Disconnect when done
robot.disconnect();
```

---

### 🔊 `HummingbirdAudio.java`

Combines TTS and audio playback:

```java
      // Instantiate the Hummingbird object (establishes a connection to the Hummingbird)
      HummingbirdRobot hummingbird = new HummingbirdRobot();
      String[] aiVoices = {"ALLOY","ASH","CORAL","ECHO","FABLE","NOVA","ONYX","SAGE","SHIMMER"};

      //This String is what the AI Voice will say
      String aiVoiceText = "Testing";

      //Hummingbird Audio Arguments: Text to say, Voice and Speed (float).
      //Select a voice from the aiVoices String array
      HummingbirdAudio sound = new HummingbirdAudio(aiVoiceText,aiVoices[0],1.0f);

      //This code will play the voices in a thread at the same time the hummingbird is running
      byte[] soundFile = sound.getAudioFile();
      Thread audioThread = new Thread(() -> HummingbirdAudio.playAudio(soundFile));
      audioThread.start();
```

This uses:

- `OpenAiAudio` → fetches WAV audio from Spring API
- `HummingbirdRobot` → plays the byte array using `playClip(byte[])`

---

### 🌐 `OpenAiAudio.java`

Handles the text-to-speech API call:

```java
OpenAiAudio audioGen = new OpenAiAudio();
byte[] audio = audioGen.getSpeech("This is a robot speaking!");
```

Internally loads API key like this:

```java
Properties props = new Properties();
props.load(new FileInputStream("application.properties"));
String apiKey = props.getProperty("openai.api.key");
```

---

## Demo Program

Run `MyProject.java` for a full demonstration:

This will:
1. Connect to the Hummingbird Duo
2. Move servos and activate LEDs
3. Use Spring API to generate audio
4. Play that audio via robot
5. Disconnect safely

---

## Troubleshooting

- **Hangs on launch**: Make sure the robot is connected via USB before starting.
- **No speech**: Verify internet access and correct API key in `application.properties`.
- **No audio**: Ensure `playClip()` is being called with valid WAV data.
- **Robot not found**: Some systems may require admin permissions for USB access.

---

## Acknowledgments

- 🎓 **BirdBrain Technologies** – for designing the Hummingbird Duo robotics platform
- 🏫 **CMU Create Lab** – for the original Hummingbird Java SDK
- 🧠 **Spring AI** – for enabling seamless integration with OpenAI services
- 💡 **OpenAI** – for providing the LLM and TTS capabilities
- 🙌 Inspired by educators integrating physical computing + AI in the classroom

---

## License

This code is intended for educational use. Please consult the LICENSE file if included.
