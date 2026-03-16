package bg.sofia.uni.fmi.mjt.vault.server.session;

public class ClientSession {
    private boolean authenticated;
    private String username;
    private String masterPassword;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void login(String username, String masterPassword) {
        this.authenticated = true;
        this.username = username;
        this.masterPassword = masterPassword;
    }

    public void logout() {
        this.authenticated = false;
        this.username = null;
        this.masterPassword = null;
    }

    public String getUsername() {
        return username;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

}
