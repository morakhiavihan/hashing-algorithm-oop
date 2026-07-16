import java.io.IOException;
import java.util.Scanner;

public class hash2 {
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Select Option : \n\t(1.) Sign in \n\t(2.) Sign up");
        int opt = sc.nextInt();
        sc.nextLine(); 

        System.out.print("Enter your username: ");
        String username = sc.nextLine();
        System.out.print("Enter your password: ");
        String password = sc.nextLine();

        AuthenticatedUser user = new AuthenticatedUser(username, password);

        try {
            switch (opt) {
                case 1:
                    user.signIn();
                    break;
                case 2:
                    user.signUp();
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1 or 2.");
                    break;

            }
        } catch (IOException e) {
            System.out.println("An error occurred !");
        }
    }
}
