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
openai.api.key=your-openai-key-here
```

This file is used by `OpenAiAudio.java` to load your API key.

Alternatively, you can set the key as an environment variable:

```bash
export SPRING_API_KEY=your-openai-key-here
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

// Spin motor 1 at full speed
robot.setMotorVelocity(1, 255);

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
HummingbirdAudio ttsRobot = new HummingbirdAudio();
ttsRobot.speakFromApi("Hello world. I am a robot powered by OpenAI.");
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

```bash
java MyProject
```

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
