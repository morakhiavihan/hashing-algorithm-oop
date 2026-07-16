import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.List;

public class AuthenticatedUser extends User implements Authentication {
    private final File MyFile;

    public AuthenticatedUser(String username, String password) {
        super(username, password);
        this.MyFile = new File(username+".txt");
    }

    @Override
    public void signIn() throws IOException {
        if (!MyFile.exists()) {
            System.out.println("Username and/or password is incorrect.");
            return;
        }

        List<String> lines = Files.readAllLines(MyFile.toPath());
        if (lines.isEmpty()) {
            System.out.println("Data file is empty.");
            return;
        }

        String[] parts = lines.get(0).split(" ");
        long storedHash = Long.parseLong(parts[0]);
        String salt = parts[1];

        long inputHash = hashPassword(this.pswd, salt);

        if (storedHash == inputHash) {
            System.out.println("You are successfully logged in.");
        } else {
            System.out.println("Username and/or password is incorrect.");
        }
    }

    @Override
    public void signUp() throws IOException {
        if (MyFile.exists()) {
            System.out.println("Username already registered.");
            return;
        }

        String salt = generateSalt(32);
        long hashedPassword = hashPassword(this.pswd, salt);

        try (FileWriter writer = new FileWriter(MyFile)) {
            writer.write(hashedPassword + " " + salt);
        }

        System.out.println("User registered successfully.");
    }

    private String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < length; i++) {
            salt.append((char) (random.nextInt(75) + '0'));
        }
        return salt.toString();
    }
}
