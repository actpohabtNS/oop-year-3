package task11;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter seed: ");
        long seed = Long.parseLong(scanner.nextLine());
        Random random = new Random(seed);

        System.out.print("Enter message: ");
        String message = scanner.nextLine();
        System.out.println("Original message: ");
        System.out.println(message);

        try {
            OutputStream os = new FileOutputStream("Hello world!");
            OutputStream bos = new BufferedOutputStream(os);
            EncryptStream eos = new EncryptStream(bos);

            for (int i = 0; i < message.length(); i++) {
                int z = random.nextInt();
                eos.write(message.charAt(i), z);
            }

            eos.close();

            InputStream is = new FileInputStream("Hello world!");
            InputStream bis = new BufferedInputStream(is);
            DecryptStream dis = new DecryptStream(bis);

            scanner.close();

            random = new Random(seed);

            System.out.println("Decrypted message: ");
            int c;
            while ((c = dis.read(random.nextInt())) != -1) {
                System.out.print((char) c);
            }

            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
