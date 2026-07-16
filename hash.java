import java.io.*;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.*;

// Hash function (djb2 equivalent in Java)
public class hash {

    public static long hash(String str) {
        long hash = 5381;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            hash = ((hash << 5) + hash) + c;
        }
        return hash;
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String user, passwd;
        
        File fptr;
        int n;

        System.out.println("Select Option : \n\t(1.) Sign in \n\t(2.) Sign up");
        n = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Sign in
        if (n == 1) {
            System.out.print("Enter your username : ");
            user = scanner.nextLine();
            System.out.print("Enter your password : ");
            passwd = scanner.nextLine();

            fptr = new File(user);
            if (!fptr.exists()) {
                System.out.println("\nUsername and/or password is incorrect.");
                return;
            }

            // Read hashed password & salt from the file
            List<String> lines = Files.readAllLines(fptr.toPath());
            if (lines.isEmpty()) {
                System.out.println("File is empty. Something went wrong.");
                return;
            }
            
            String[] parts = lines.get(0).split(" ");
            long ulpwd = Long.parseLong(parts[0]); // stored hash password
            String salt = parts[1]; // stored salt

            // Generate hash of the input password with stored salt
            long hashpwd = hash(passwd + salt);

            // Compare passwords
            if (ulpwd == hashpwd) {
                System.out.println("\nYou are successfully logged in.");
            } else {
                System.out.println("\nUsername and/or password is incorrect.");
            }
        }

        // Sign up
        else if (n == 2) {
            System.out.print("Enter your username : ");
            user = scanner.nextLine();
            System.out.print("Enter your password : ");
            passwd = scanner.nextLine();

            fptr = new File(user);
            if (fptr.exists()) {
                System.out.println("\nUsername already registered.");
                return;
            }

            // Generate salt
            String salt = generateSalt(32);

            // Generate hash of the password with the generated salt
            long hashpwd = hash(passwd + salt);

            // Write hash password and salt to the file
            try (FileWriter writer = new FileWriter(fptr)) {
                writer.write(hashpwd + " " + salt);
            }
        }

        // Invalid input
        else {
            System.out.println("Incorrect Input.");
        }
    }

    // Function to generate a random salt of the given length
    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < length; i++) {
            salt.append((char) (random.nextInt(75) + '0')); // Random character
        }
        return salt.toString();
    }
}
