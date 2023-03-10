package by.bsuir.fitness.mail;

/**
 * The type Sending email.
 */
public class SendingEmail {

    /**
     * Verify.
     *
     * @param login     the login
     * @param userEmail the user email
     * @param userHash  the user hash
     */
    public static void verify(String login, String userEmail, String userHash) {
        String message = "Your Verification Link : " + "http://localhost:8080/trainingWeb_war_exploded/controller?" +
                "command=activate&key1=" + userEmail + "&key2=" + login + "&key3=" + userHash;
        GoogleMailThread thread = new GoogleMailThread("007checkmailphp", "375299300520", "Email Verification Link",
                userEmail, message);
        thread.start();
    }

    /**
     * Restore password.
     *
     * @param login     the login
     * @param userEmail the user email
     * @param userHash  the user hash
     */
    public static void restorePassword(String login, String userEmail, String userHash) {
        String message = "Link to restore your password: " + "http://localhost:8080/trainingWeb_war_exploded" +
                "/controller?command=password_restore&key1=" + userEmail + "&key2=" + login + "&key3=" + userHash;
        GoogleMailThread thread = new GoogleMailThread("007checkmailphp", "375299300520", "Password Restore Link",
                userEmail, message);
        thread.start();
    }
}
