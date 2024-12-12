import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Decryption {

    private BufferedImage image = null;

    public static void main(String[] args) {
        Decryption decryption = new Decryption();
        decryption.run();
    }

    // Method to run the decryption process
    public void run() {
        try {
            // Step 1: Read the image from file
            String imagePath = getImagePath();
            image = ImageIO.read(new File(imagePath));

            // Step 2: Decode the message from the image
            String decodedMessage = decodeMessage();

            // Step 3: Print the decoded message
            if (decodedMessage != null) {
                System.out.println("Decoded Message: ");
                System.out.println(decodedMessage);
            } else {
                System.out.println("No hidden message found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading the image file.");
            e.printStackTrace();
        }
    }

    // Method to ask the user for the image file path
    private String getImagePath() {
        // In a real program, you can use a scanner to read the file path from the command line
        System.out.println("Enter the path of the image containing the hidden message:");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            return br.readLine();  // Read the path input from the user
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Method to decode the hidden message from the image
    private String decodeMessage() {
        if (image == null) {
            return null;
        }

        // Step 1: Extract the message length (first 32 bits)
        int messageLength = extractInteger(image, 0, 0);

        // Step 2: Extract the message itself
        byte[] messageBytes = new byte[messageLength];
        for (int i = 0; i < messageLength; i++) {
            messageBytes[i] = extractByte(image, i * 8 + 32, 0);
        }

        // Convert the byte array to a string and return it
        return new String(messageBytes);
    }

    // Method to extract a 32-bit integer (used to store the message length)
    private int extractInteger(BufferedImage img, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight();
        int startX = start / maxY, startY = start - startX * maxY, count = 0;
        int length = 0;
        for (int i = startX; i < maxX && count < 32; i++) {
            for (int j = startY; j < maxY && count < 32; j++) {
                int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
                length = setBitValue(length, count, bit);
                count++;
            }
        }
        return length;
    }

    // Method to extract a byte (used to store each character of the message)
    private byte extractByte(BufferedImage img, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight();
        int startX = start / maxY, startY = start - startX * maxY, count = 0;
        byte b = 0;
        for (int i = startX; i < maxX && count < 8; i++) {
            for (int j = startY; j < maxY && count < 8; j++) {
                int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
                b = (byte) setBitValue(b, count, bit);
                count++;
            }
        }
        return b;
    }

    // Method to get the bit value from an integer
    private int getBitValue(int n, int location) {
        return (n & (1 << location)) == 0 ? 0 : 1;
    }

    // Method to set the bit value in an integer
    private int setBitValue(int n, int location, int bit) {
        if (bit == 0) {
            n &= ~(1 << location);  // Clear the bit at location
        } else {
            n |= (1 << location);   // Set the bit at location
        }
        return n;
    }
}
