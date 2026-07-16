import java.io.IOException;

public interface Authentication {
    void signIn() throws IOException;
    void signUp() throws IOException;
}
