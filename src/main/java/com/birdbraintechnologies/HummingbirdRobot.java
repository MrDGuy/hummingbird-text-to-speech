package com.birdbraintechnologies;

import java.awt.Color;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesSpecification;
import org.hid4java.ScanMode;

/**
 * Created by Tom Lauwers on 10/27/2019.
 */
public class HummingbirdRobot {

    private static final Integer VENDOR_ID = 0x2354;
    private static final Integer PRODUCT_ID = 0x2222;
    private static final int PACKET_LENGTH = 8;
    public static final String SERIAL_NUMBER = null;


    private HidDevice HIDHummingbird;
    private HidServices hidServices;
    private byte reportCounter = 0x00;

    /**
     * Creates the Hummingbird object and automatically connects
     * to a Hummingbird.
     */
    public HummingbirdRobot()
    {
        // Configure to use custom specification
        HidServicesSpecification hidServicesSpecification = new HidServicesSpecification();
        hidServicesSpecification.setAutoShutdown(true);
        //hidServicesSpecification.setScanInterval(500);
        //hidServicesSpecification.setPauseInterval(5000);
        hidServicesSpecification.setScanMode(ScanMode.NO_SCAN);

        // Get HID services using custom specification
        hidServices = HidManager.getHidServices(hidServicesSpecification);
        Connect();
    }

    // Returns true if a Hummingbird is connected, false otherwise
    public boolean isConnected()
    {
        if(HIDHummingbird != null)
            return HIDHummingbird.isOpen();
        else
            return false;
    }
    // Returns true if a Hummingbird successfully connected
    private boolean Connect()
    {
        // Only connect if you aren't currently connected
        if(!isConnected()) {
            HIDHummingbird = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, SERIAL_NUMBER);
            // If hidServices returned an HIDHummingbird it means one is attached to the computer, so open it
            if(HIDHummingbird != null) {
                System.out.println("Connecting Hummingbird...");
                if (!HIDHummingbird.isOpen()) {
                    HIDHummingbird.open();
                }
                return true;
            }
            else {
                System.out.println("No Hummingbird detected, please connect a Hummingbird and run the program again.");
                return false;
            }
        }
        else {
            return true; // because if isConnected is true, it means that a Hummingbird is already connected
        }
    }

    /**
     * Sets the LEDs specified by the given <code>mask</code> to the given <code>intensities</code>.
     * Returns the current intensities as an array of integers if the command succeeded, <code>null</code> otherwise.
     *
     * @param mask boolean array signifying which LEDs to activate (array element 0 is LED port 1)
     * @param intensities array signifying LED intensity (range is 0 to 255)
     */
    public int[] setLEDs(final boolean[] mask, final int[] intensities)
    {
        for(int i = 0; i < 4; i++)
        {
            if(mask[i])
            {
                setLED(i+1, intensities[i]); // Starting at port 1, hence i+1
            }
        }
        return getLEDState();
    }

    /** Returns <code>true</code> if motor power is plugged in; <code>false</code> otherwise. */
    public boolean isMotorPowerEnabled()
    {
        int [] vals = getSensorValues();

        if(vals != null) {
            // Checks if the voltage is above a threshold to determine if motor power is plugged in
            if (getSensorValues()[4] > 80)
                return true;
        }
        return false;
    }

    /**
     * Sets the motors specified by the given <code>mask</code> to the given <code>velocities</code>.  Returns
     * the current velocities as an array of integers if the command succeeded, <code>null</code> otherwise.
     *
     * @param mask boolean array signifying which motors to activate (array element 0 is motor port 1)
     * @param velocities array signifying motor velocity (range is -255 to 255)
     */
    public int[] setMotorVelocities(final boolean[] mask, final int[] velocities)
    {
        for(int i = 0; i < 2; i++)
        {
            if(mask[i])
            {
                setMotorVelocity(i+1, velocities[i]); // Starting at port 1, hence i+1
            }
        }
        return getMotorState();
    }

    /**
     * Sets the servo motors specified by the given <code>mask</code> to the given <code>positions</code>.  Returns
     * the current positions as an array of integers if the command succeeded, <code>null</code> otherwise.
     *
     * @param mask boolean array signifying which servos to activate (array element 0 is servo port 1)
     * @param positions array signifying servo position (range is 0 to 255)
     */
    public int[] setServoPositions(final boolean[] mask, final int[] positions)
    {
        for(int i = 0; i < 4; i++)
        {
            if(mask[i])
            {
                setServoPosition(i+1, positions[i]); // Starting at port 1, hence i+1
            }
        }
        return getServoState();
    }

    
       /**
     * Sets the motor specified by the given <code>motorId</code> to the given (signed) <code>velocity</code>.  Returns
     * <code>true</code> if the command succeeded, <code>false</code> otherwise.
     *
     * @param motorId the motor to control [1 or 2]
     * @param velocity the signed velocity [-255 to 255]
     *
     * @throws IllegalArgumentException if the <code>motorId</code> specifies an invalid port
     */
    public boolean setMotorVelocity(final int motorId, final int velocity)
    {
        if(motorId < 1 || motorId > 2 || velocity < -255 || velocity > 255)
        {
            System.out.println("One or more parameters out of range when setting gear motor");
            return false;
        }
        // Send the M command
        byte[] command = new byte[PACKET_LENGTH];
        command[0] = 'M'; // Sets a Motor
        command[1] = (byte)(motorId + 47); //setting is based on ASCII 0 to 1, so need to convert from decimal 1 to 2
        if(velocity < 0)
        {
            command[2] = '1'; // move backwards
            command[3] = (byte)(velocity*-1);
        }
        else {
            command[2] = '0'; // move forwards
            command[3] = (byte) (velocity);
        }
        return writeHB(command);
    }

    /**
     * Gets all of the sensor values state.  Returns <code>null</code> if an error occurred while getting the state.
     *
     * @return array containing all four sensor values, array element 0 corresponds to port 1
     */
    public int[] getSensorValues()
    {
        // Send the G3 command
        byte[] command = new byte[PACKET_LENGTH];
        command[0] = 'G'; // Sets an LED
        command[1] = '3'; //setting is based on ASCII 0 to 3, so need to convert from decimal 1 to 4
        byte[] data = readHB(command);
        if(data != null) {
            int[] sensorVals = new int[data.length];
            for (int i = 0; i < data.length; i++) {
                sensorVals[i] = (int) (data[i] & 0xFF);
            }
            return sensorVals;
        }
        return null;
    }

    
    /**
     * Sets the full-color LED specified by the given <code>ledId</code> to the given red, green, and blue intensities.
     * Returns <code>true</code> if the command succeeded, <code>false</code> otherwise.
     *
     * @param ledId the LED to control [1 or 2]
     * @param red the intensity of the LED's red component [0 to 255]
     * @param green the intensity of the LED's green component [0 to 255]
     * @param blue the intensity of the LED's blue component [0 to 255]
     *
     * @throws IllegalArgumentException if the <code>ledId</code> specifies an invalid port
     */
    public boolean setFullColorLED(final int ledId, final int red, final int green, final int blue)
    {
        if(ledId < 1 || ledId > 2 || red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255)
        {
            System.out.println("One or more parameters out of range when setting full color LED");
            return false;
        }

        // Send the O command
        byte[] command = new byte[PACKET_LENGTH];
        command[0] = 'O'; // Sets a full color LED
        command[1] = (byte)(ledId + 47); //setting is based on ASCII 0 to 1, so need to convert from decimal 1 to 2
        command[2] = (byte) red;
        command[3] = (byte) green;
        command[4] = (byte) blue;
        return writeHB(command);
    }

    /**
     * Disconnects the Hummingbird from your program. Call at the end of your program.
     */

    public void disconnect()
    {
        // Send the reset command
        byte[] message = new byte[PACKET_LENGTH];
        message[0] = 'R'; // Turns off motors, shuts off the Hummingbird
        writeHB(message);
        // Shut down and rely on auto-shutdown hook to clear HidApi resources
        hidServices.shutdown();
        HIDHummingbird.close();
    }

    /**
     * Sets the vibration motor specified by the given <code>motorId</code> to the given <code>intensity</code>.  Returns
     * <code>true</code> if the command succeeded, <code>false</code> otherwise.
     *
     * @param motorId the motor to control [1 or 2]
     * @param intensity the intensity of vibration [0 to 255]
     *
     * @throws IllegalArgumentException if the <code>motorId</code> specifies an invalid port
     */
    public boolean setVibrationMotorSpeed(final int motorId, final int intensity) {
        if (motorId < 1 || motorId > 2 || intensity < 0 || intensity > 255) {
            System.out.println("One or more parameters out of range when setting vibration motor");
            return false;
        }
        // Send the M command
        byte[] command = new byte[PACKET_LENGTH];
        command[0] = 'V'; // Sets a Motor
        command[1] = (byte) (motorId + 47); //setting is based on ASCII 0 to 1, so need to convert from decimal 1 to 2
        command[2] = (byte) intensity;
        return writeHB(command);
    }
    

    /**
     * Sets the vibration motors specified by the given <code>mask</code> to the given <code>intensities</code>.  Returns
     * the current intensities as an array of integers if the command succeeded, <code>null</code> otherwise.
     *
     * @param mask boolean array signifying which motors to activate (array element 0 is vibration motor port 1)
     * @param intensities array signifying motor intensities position (range is 0 to 255)
     */
    public int[] setVibrationMotorSpeeds(final boolean[] mask, final int[] intensities)
    {
        for(int i = 0; i < 2; i++)
        {
            if(mask[i])
            {
                setVibrationMotorSpeed(i+1, intensities[i]); // Starting at port 1, hence i+1
            }
        }
        return getVibrationMotorState();
    }

    /**
     * Sets the servo specified by the given <code>servoId</code> to the given <code>position</code>.  Returns
     * <code>true</code> if the command succeeded, <code>false</code> otherwise.
     *
     * @param servoId the servo to control [1 to 4]
     * @param position the position [0 to 255]
     *
     * @throws IllegalArgumentException if the <code>servoId</code> specifies an invalid port
     */
    public boolean setServoPosition(final int servoId, final int position)
    {
        if (servoId < 1 || servoId > 4 || position < 0 || position > 255) {
            System.out.println("One or more parameters out of range when setting servo motor");
            return false;
        }
        // Send the M command
        byte[] command = new byte[PACKET_LENGTH];
        command[0] = 'S'; // Sets a servo Motor
        command[1] = (byte) (servoId + 47); //setting is based on ASCII 0 to 1, so need to convert from decimal 1 to 2
        command[2] = (byte) position;
        return writeHB(command);
    }

    /**
     * Returns the value of the given port id; returns <code>-1</code> if an error occurred while trying to read the value.
     *
     * @return the value of the sensor, range is 0 to 255
     *
     */
    public Integer getSensorValue(final int analogInputPortId)
    {
        if(analogInputPortId > 0 && analogInputPortId < 5) {
            return getSensorValues()[analogInputPortId - 1];
        }
        else
        {
            System.out.println("Sensor port out of range - must be between 1 and 4");
            return null;
        }
    }

    /**
     * Sets the full-color LEDs specified by the given <code>mask</code> to the given {@link Color colors}. Returns the
     * current colors as an array of {@link Color colors} if the command succeeded, <code>null</code> otherwise.
     *
     * @return an array of current LED colors.
     */
    public Color[] setFullColorLEDs(final boolean[] mask, final Color[] colors)
    {
        for(int i = 0; i < 2; i++)
        {
            if(mask[i])
            {
                setFullColorLED(i+1, colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue()); // Starting at port 1, hence i+1
            }
        }
        // Convert RGB values to colors
        int[] fullColorState = getFullColorLEDState();
        Color[] colorState = new Color[2];
        colorState[0] = new Color(fullColorState[0], fullColorState[1], fullColorState[2]);
        colorState[1] = new Color(fullColorState[3], fullColorState[4], fullColorState[5]);
        return colorState;
    }

    /**
     * Sets the LED specified by the given <code>ledId</code> to the given <code>intensity</code>.  Returns
     * <code>true</code> if the command succeeded, <code>false</code> otherwise.
     *
     * @param ledId the LED to control [1, 2, 3, or 4]
     * @param intensity the intensity [0 to 255]
     *
     * @throws IllegalArgumentException if the <code>ledId</code> specifies an invalid port
     */
    public boolean setLED(final int ledId, final int intensity)
    {
        if(ledId < 1 || ledId > 4 || intensity < 0 || intensity > 255)
        {
            System.out.println("One or more parameters out of range when setting  LED");
            return false;
        }
        // Send the L command
        byte[] command = new byte[PACKET_LENGTH];
        command[0] = 'L'; // Sets an LED
        command[1] = (byte)(ledId + 47); //setting is based on ASCII 0 to 3, so need to convert from decimal 1 to 4
        command[2] = (byte)intensity;
        return writeHB(command);
    }

    /**
     * Command to get state of the Full color LEDs
     */
    private int[] getFullColorLEDState()
    {
        // Send the G0 command
        byte[] command = new byte[PACKET_LENGTH];
        command[0] = 'G'; // Get state
        command[1] = '0'; // Full color LEDs are in byte 0
        byte[] data = readHB(command);
        if(data != null)
        {
            int[] fullColorData = new int[6];
            for(int i = 0; i < 6; i++)
            {
                fullColorData[i] = (int) (data[i]  & 0xFF);
            }
            return fullColorData;
        }
        return null;
    }

    /**
     * Command to get state of the regular LEDs
     */
    private int[] getLEDState()
    {
        // Send the G0 command
        byte[] command = new byte[PACKET_LENGTH];
        command[0] = 'G'; // Get state
        command[1] = '0'; // Full color LEDs are in byte 0
        byte[] data1 = readHB(command);
        if(data1 != null)
        {
            // Send the G1 command
            command = new byte[PACKET_LENGTH];
            command[0] = 'G'; // Get state
            command[1] = '1'; // Full color LEDs are in byte 0
            byte[] data2 = readHB(command);
            if(data2 != null) {
                int[] LEDData = new int[4];
                LEDData[0] = data1[6]; // LED 1 is the 7th byte in the first array
                LEDData[1] = data2[0];
                LEDData[2] = data2[1];
                LEDData[3] = data2[2];
                return LEDData;
            }
        }
        return null;
    }

    /**
     * Command to get state of the servos
     */
    private int[] getServoState()
    {
        // Send the G1 command
        byte[] command = new byte[PACKET_LENGTH];
        command[0] = 'G'; // Get state
        command[1] = '1'; // Servos are in array 1
        byte[] data = readHB(command);
        if(data != null)
        {
            int[] ServoData = new int[4];
            for(int i = 0; i < 4; i++)
            {
                ServoData[i] = (int) (data[i+3] & 0xFF); // servo settings are bytes 3 to 7
            }
            return ServoData;
        }
        return null;
    }

    /**
     * Command to get state of the gear motors
     */
    private int[] getMotorState()
    {
        // Send the G2 command
        byte[] command = new byte[PACKET_LENGTH];
        command[0] = 'G'; // Get state
        command[1] = '2'; // Motors are in array 2
        byte[] data = readHB(command);
        if(data != null)
        {
            int[] MotorData = new int[2];

            // converting gear motors to -255 to 255
            if(data[0] != 0) {
                MotorData[0] = -(int)(data[1] & 0xFF);
            }
            else {
                MotorData[0] = (int)(data[1] & 0xFF);
            }
            if(data[2] != 0) {
                MotorData[1] = -(int)(data[3] & 0xFF);
            }
            else {
                MotorData[1] = (int)(data[3] & 0xFF);
            }

            return MotorData;
        }
        return null;
    }
    /**
     * Command to get state of the vibration motors
     */
    private int[] getVibrationMotorState()
    {
        // Send the G2 command
        byte[] command = new byte[PACKET_LENGTH];
        command[0] = 'G'; // Get state
        command[1] = '2'; // Motors are in array 2
        byte[] data = readHB(command);
        if(data != null)
        {
            int[] MotorData = new int[2];

            MotorData[0] = data[4];
            MotorData[1] = data[5];

            return MotorData;
        }
        return null;
    }
    /**
     * Command to write a command to Hummingbird
     */
    private boolean writeHB(byte[] command) {
        if (!HIDHummingbird.isOpen()) {
            System.out.println("Hummingbird not connected");
        } else {
            int val = HIDHummingbird.write(command, PACKET_LENGTH, (byte) 0x00);
            if (val < 0) {
                System.err.println(HIDHummingbird.getLastErrorMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Command to read a sensor from Hummingbird
     */
    private byte[] readHB(byte[] command) {
        if (!HIDHummingbird.isOpen()) {
            System.out.println("Hummingbird not connected");
        }
        else {
            // This is a hack to ensure that each return report is different from the one before. If the sensors haven't changed, the return report won't either, causing problems.
            command[7] = reportCounter;
            reportCounter++;

            int val = HIDHummingbird.write(command, PACKET_LENGTH, (byte) 0x00);
            if (val < 0) {
                System.err.println(HIDHummingbird.getLastErrorMessage());
            }
            //sleep(10);
            byte data[] = new byte[PACKET_LENGTH];
            // This method reads the returned report, or returns null if it has timed out after 50 ms
            val = HIDHummingbird.read(data,50);
            switch (val) {
                case -1:
                    System.err.println(HIDHummingbird.getLastErrorMessage());
                    break;
                case 0:
                    System.err.println("No sensor data received");
                    break;
            }
            return data;
        }
        return null;
    }

    /**
     * Turns off all motors, vibrations motors, LEDs, and full-color LEDs. Returns <code>true</code> if the command
     * succeeded, <code>false</code> otherwise.
     */
    public boolean emergencyStop()
    {
        // Send the X command
        byte[] message = new byte[PACKET_LENGTH];
        message[0] = 'X'; // Turns off motors, shuts off the Hummingbird
        return writeHB(message);
    }


}
