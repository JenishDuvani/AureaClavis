import java.awt.Graphics;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Encryption {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java Encryption <input_image> <message> <output_image>");
            System.exit(1);
        }

        String inputImagePath = args[0]; // Path to input image
        String message = args[1]; // Message to embed
        String outputImagePath = args[2]; // Path to output image

        try {
            // Open the image
            BufferedImage sourceImage = ImageIO.read(new File(inputImagePath));

            // Create a copy of the source image
            BufferedImage embeddedImage = new BufferedImage(
                    sourceImage.getWidth(), sourceImage.getHeight(), sourceImage.getType());
            Graphics g = embeddedImage.getGraphics();
            g.drawImage(sourceImage, 0, 0, null);
            g.dispose();

            // Embed the message
            embedMessage(embeddedImage, message);

            // Save the modified image
            saveImage(embeddedImage, outputImagePath);

            System.out.println("Message embedded and image saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void embedMessage(BufferedImage img, String mess) {
        int messageLength = mess.length();
        int imageWidth = img.getWidth(), imageHeight = img.getHeight(),
                imageSize = imageWidth * imageHeight;

        if (messageLength * 8 + 32 > imageSize) {
            System.out.println("Message is too long for the chosen image.");
            return;
        }

        // Embed the length of the message (32 bits)
        embedInteger(img, messageLength, 0, 0);

        // Embed the actual message byte by byte
        byte[] messageBytes = mess.getBytes();
        for (int i = 0; i < messageBytes.length; i++) {
            embedByte(img, messageBytes[i], i * 8 + 32, 0);
        }
    }

    private static void embedInteger(BufferedImage img, int n, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight(),
                startX = start / maxY, startY = start - startX * maxY, count = 0;
        for (int i = startX; i < maxX && count < 32; i++) {
            for (int j = startY; j < maxY && count < 32; j++) {
                int rgb = img.getRGB(i, j), bit = getBitValue(n, count);
                rgb = setBitValue(rgb, storageBit, bit);
                img.setRGB(i, j, rgb);
                count++;
            }
        }
    }

    private static void embedByte(BufferedImage img, byte b, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight(),
                startX = start / maxY, startY = start - startX * maxY, count = 0;
        for (int i = startX; i < maxX && count < 8; i++) {
            for (int j = startY; j < maxY && count < 8; j++) {
                int rgb = img.getRGB(i, j), bit = getBitValue(b, count);
                rgb = setBitValue(rgb, storageBit, bit);
                img.setRGB(i, j, rgb);
                count++;
            }
        }
    }

    private static void saveImage(BufferedImage img, String outputPath) {
        try {
            String extension = outputPath.substring(outputPath.lastIndexOf('.') + 1).toLowerCase();
            if (!extension.equals("png") && !extension.equals("bmp") && !extension.equals("dib")) {
                outputPath += ".png";
            }
            ImageIO.write(img, "PNG", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getBitValue(int n, int location) {
        int v = n & (int) Math.round(Math.pow(2, location));
        return v == 0 ? 0 : 1;
    }

    private static int setBitValue(int n, int location, int bit) {
        int toggle = (int) Math.pow(2, location), bv = getBitValue(n, location);
        if (bv == bit)
            return n;
        if (bv == 0 && bit == 1)
            n |= toggle;
        else if (bv == 1 && bit == 0)
            n ^= toggle;
        return n;
    }
}
