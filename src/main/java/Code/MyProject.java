
package Code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.birdbraintechnologies.HummingbirdAudio;
import com.birdbraintechnologies.HummingbirdRobot;

public class MyProject
   {

   public static void main(final String[] args) throws IOException
      {
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
   
      // Print instructions for exiting
      System.out.println("");
      System.out.println("Press ENTER to quit.");

      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      while (true)
         {
         // check whether the user pressed a key, if so, break out of the loop
         if (in.ready())
            {
            break;
            }
         //Write you code here for the Hummingbird Robot sensors, lights and movement:
         // hummingbird.setLED(1, 255);
         // sleep(6000);
         // hummingbird.setLED(1,0);
         // hummingbird.setLED(2,255);
         // sleep(2000);
         // hummingbird.setLED(2,0);
         // hummingbird.setLED(3,255);
         
         // hummingbird.setServoPosition(1, 0);
         // hummingbird.setLED(1, 255);
         // sleep(6000);
         // hummingbird.setLED(1,0);
         // hummingbird.setLED(2,255);
         // sleep(2000);
         // hummingbird.setLED(2,0);
         // hummingbird.setLED(3,255);
         // hummingbird.setServoPosition(1, 180);
         }

        // Disconnect - if you miss this call the Hummingbird will continue doing stuff for five more seconds
        // you may also get a java error.
      hummingbird.disconnect();
      }

   // Pause for 1 second
   private static void sleep(int milliseconds)
      {
      try
         {
         Thread.sleep(milliseconds);
         }
      catch (InterruptedException e)
         {
         System.err.println("InterruptedException while sleeping!");
         }
      }

   }
