/**
 * CS180 -  *
 * Explain briefly the functionality of the program.
 *
 * @author Han Wang, wang2786@purdue.edu, LE1, Lab L03
 * @version 3/29/16.
 */

import java.util.Date;

public class Email {
    public String recipient;
    public String sender;
    public long id;
    public String message;
    public Date date;


    public Email(String recipient, String sender, long id, String message) {
        this.recipient = recipient;
        this.sender = sender;
        this.id = id;
        this.message = message;
        this.date = new Date();

    }

    public long getID() {
        return id;
    }

    public String getOwner() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    //“540077535771753178;Sun Oct 18 01:16:36 EDT 2015; From: root “This is the mail body.”
    public String toString() {
        return getID() + ";" + date.toString() + "; " + "From: " + getSender()
                + " \"" + getMessage() + "\"";
    }


    public static void main(String[] args) {
        Email e = new Email("ni", "wo", 123, "dd");

    }
}
