import java.util.Objects;
import java.util.Random;

/**
 * CS180 -  *
 * Explain briefly the functionality of the program.
 *
 * @author Han Wang, wang2786@purdue.edu, LE1, Lab L03
 * @version 4/13/16.
 */
public class User {
    public String username;
    public String password;
    public DynamicBuffer inbox;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        inbox = new DynamicBuffer(10);
    }

    public String getName() {
        return username;
    }

    public boolean checkPassword(String password) {
        return password.equals(this.password);
    }

    public int numEmail() {
        return inbox.numElements();
    }

    public void receiveEmail(String sender, String message) {
        inbox.add(new Email(username, sender, new Random().nextLong(), message));
    }

    public Email[] retrieveEmail(int n) {
        return inbox.getNewest(n);
    }

    public boolean removeEmail(long emailID) {
        int index = -1;

        for (int i = 0; i < inbox.numElements(); i++) {
            if (inbox.buffer[i].getID() == emailID) {
                index = i;
            }
        }
        return index != -1 && inbox.remove(index);
    }

    public static boolean correctForm(String username, String password) {
        if (username.length() < 1 || username.length() > 20) {
            return false;
        }
        if (password.length() < 4 || password.length() > 40) {
            return false;
        }
        for (int i = 0; i < username.length(); i++) {
            if (!Character.isLetterOrDigit(username.charAt(i))) {
                return false;
            }
        }
        for (int i = 0; i < password.length(); i++) {
            if (!Character.isLetterOrDigit(password.charAt(i))) {
                return false;
            }
        }
        return true;
    }


}
