package com.birdbraintechnologies;

import java.io.*;
import javax.sound.sampled.*;

import com.springai.OpenAiAudio;

public class HummingbirdAudio {

    private OpenAiAudio audioSettings;

    public HummingbirdAudio(String text, String voice, float speed){
        audioSettings = new OpenAiAudio(text, voice, speed);
    }

    public byte[] getAudioFile()
    {
        return audioSettings.createAudio();
    }

    public static void playAudio(byte[] sound)
    {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(sound);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bais);
            
            AudioFormat baseFormat = ais.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED, 
                baseFormat.getSampleRate(),
                16, // 16-bit
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false
            );
        
            AudioInputStream dais = AudioSystem.getAudioInputStream(decodedFormat, ais);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        
            line.open(decodedFormat);
            line.start();
        
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = dais.read(buffer)) != -1) {
                line.write(buffer, 0, bytesRead);
            }
        
            line.drain();
            line.close();
            ais.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] wavFileToByteArray(String filePath) {
        try {
            // Open the audio file
            File file = new File(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);

            // Read audio data into a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // Close the streams
            audioInputStream.close();
            return byteArrayOutputStream.toByteArray();

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            return null; // Return null if there's an error
        }
    }

    
}
