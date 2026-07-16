public abstract class User {
    protected String user;
    protected String pswd;

    public User(String username, String password) { 
        this.user = username;
        this.pswd = password;
    }

    // Method overloading
    public long hashPassword(String password) {
        return hash(password);
    }

    public long hashPassword(String password, String salt) {
        return hash(password + salt);
    }

    private long hash(String str) {
        long hash = 5381;
        for (int i = 0; i < str.length(); i++) {
            hash = ((hash << 5) + hash) + str.charAt(i);
        }
        return hash;
    }
}
