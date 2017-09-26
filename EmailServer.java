import javax.xml.soap.MessageFactory;
import java.io.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * <b> CS 180 - Project 4 - Email Server Skeleton </b>
 * <p>
 * <p>
 * This is the skeleton code for the EmailServer Class. This is a private email
 * server for you and your friends to communicate.
 *
 * @author (Your Name) <(YourEmail@purdue.edu)>
 * @version (Today's Date)
 * @lab (Your Lab Section)
 */

public class EmailServer {
    // Useful constants
    public static final String FAILURE = "FAILURE";
    public static final String DELIMITER = "\t";
    public static final String SUCCESS = "SUCCESS";
    public static final String CRLF = "\r\n";
    public User[] users;
    public DynamicBuffer buffer;
    public int maxUsers;
    public ArrayList<String> al;
    public File input;
    public boolean usingFile;

    // Used to print out extra information
    private boolean verbose = false;


    public EmailServer() {
        maxUsers = 100;
        users = new User[1];
        users[0] = new User("root", "cs180");
        buffer = new DynamicBuffer(10);
        this.usingFile = false;
    }

    public EmailServer(String fileName) throws IOException {
        maxUsers = 100;
        users = new User[1];
        users[0] = new User("root", "cs180");
        buffer = new DynamicBuffer(10);
        input = new File(fileName);
        this.usingFile = true;
        if (input.exists()) {
            try {
                al = new ArrayList<String>();
                //counter for users, start from 1 cuz 0 is root;
                int usersCount = 1;
                String[] token;
                FileReader fr = new FileReader(input);
                Scanner sc = new Scanner(fr);
                while (sc.hasNextLine()) {
                    al.add(sc.nextLine());
                }
                //jacquimo,password1
                //simpleUser,password2
                //testAdmin,password3
                //user,password4

                for (int i = 0; i < al.size(); i++) {
                    //check if arraylist[i] is valid
                    if (al.get(i).contains(",")) {
                        //check if arraylist[i] is valid
                        if (al.get(i).length() != 0 && al.get(i) != null) {
                            //split
                            token = al.get(i).split(",");
                            //expand
                            users = Arrays.copyOf(users, users.length + 1);
                            //add users
                            users[usersCount++] = new User(token[0], token[1]);
                        } else {
                            break;
                        }
                    } else {
                        continue;
                    }

                }
                sc.close();
                fr.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            }
        } else {
            input = new File(fileName);
            input.createNewFile();
            PrintWriter pw = new PrintWriter(input);
            for (int i = 0; i < users.length; i++) {
                pw.printf(users[i].getName() + "," + users[i].password + CRLF);
            }
            pw.close();

        }
    }

    /**
     * Replaces "poorly formatted" escape characters with their proper
     * values. For some terminals, when escaped characters are
     * entered, the terminal includes the "\" as a character instead
     * of entering the escape character. This function replaces the
     * incorrectly inputed characters with their proper escaped
     * characters.
     *
     * @param str - the string to be edited
     * @return the properly escaped string
     */
    private static String replaceEscapeChars(String str) {
        str = str.replace("\\r\\n", "\r\n"); // may not be necessary, but just in case
        str = str.replace("\\r", "\r");
        str = str.replace("\\n", "\n");
        str = str.replace("\\t", "\t");
        str = str.replace("\\f", "\f");

        return str;
    }

    /**
     * This main method is for testing purposes only.
     *
     * @param args - the command line arguments
     */
    public static void main(String[] args) {
        (new EmailServer()).run();
    }

    public void run() {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.printf("Input Server Request: ");
            String command = in.nextLine();

            command = replaceEscapeChars(command);

            if (command.equalsIgnoreCase("kill") || command.equalsIgnoreCase("kill\r\n"))
                break;

            if (command.equalsIgnoreCase("verbose") || command.equalsIgnoreCase("verbose\r\n")) {
                verbose = !verbose;
                System.out.printf("VERBOSE has been turned %s.\n\n", verbose ? "on" : "off");
                continue;
            }

            String response = null;
            try {
                response = parseRequest(command);
            } catch (Exception ex) {
                response = ErrorFactory.makeErrorMessage(ErrorFactory.UNKNOWN_ERROR,
                        String.format("An exception of %s occurred.", ex.getClass().toString()));
            }

            // change the formatting of the server response so it prints well on the terminal
            // (for testing purposes only)
            //if (response.startsWith("SUCCESS" + DELIMITER))
            //	response = response.replace(DELIMITER, NEWLINE);
            if (response.startsWith(FAILURE) && !DELIMITER.equals("\t"))
                response = response.replace(DELIMITER, "\t");

            if (verbose)
                System.out.print("response: ");
            System.out.printf("\"%s\"\n\n", response);
        }

        in.close();
    }

    //ADD-USER/troot/tcs180/r/n
    public String addUser(String[] args) {
        String username = args[1];
        String password = stripRN(args[2]);

        if (exist(username)) {
            return ErrorFactory.makeErrorMessage(-22, "User already exist.");
        } else {
            if ((!username.matches("[A-Za-z0-9]+")) || username.length() < 1 || username.length() > 20)
                return ErrorFactory.makeErrorMessage(-23, "Username error!");
            if ((!password.matches("[A-Za-z0-9]+")) || password.length() < 4 || password.length() > 40)
                return ErrorFactory.makeErrorMessage(-23, "Password error!");
            users = Arrays.copyOf(users, users.length + 1);
            users[users.length - 1] = new User(username, password);
        }
        if (usingFile) {
            try {
                al = new ArrayList<>();
                String lastfile = "";
                FileReader fr = new FileReader(input.getName());
                Scanner sc = new Scanner(fr);
                while (sc.hasNextLine()) {
                    al.add(sc.nextLine());
                }
                for (int i = 0; i < al.size() - 1; i++) {
                    String[] token = al.get(i).split(",");
                    String tempUserName = token[0];
                    String tempPassword = token[1];
                    lastfile = lastfile + tempUserName + "," + tempPassword + "\n";
                }

                PrintWriter pw = new PrintWriter(input.getName());
                pw.append(lastfile).append(username).append(",").append(password).append("\n");

                pw.close();
                sc.close();
                fr.close();

            } catch (Exception e) {
                System.out.println("Exception has benn thrown.");
            }
        }
        return "SUCCESS\r\n";
    }


    //GET-All-USERS\troot\tcs180\r\n
    //"SUCCESS\troot\tuser1\tuser2\r\n"
    public String getAllUsers(String[] args) {
        String result = "";
        String username = args[1];
        String password = stripRN(args[2]);

        if (!exist(username)) {
            return ErrorFactory.makeErrorMessage(-20);
        }
        for (int i = 0; i < users.length; i++) {
            if (exist(username) && users[i].getName().equals(username)) {
                if (!checkAuth(username, password)) {
                    return ErrorFactory.makeErrorMessage(-21);
                } else {
                    for (int j = 0; j < users.length; j++) {
                        result = result + "\t" + users[j].getName();
                    }
                }
            }
        }
        return "SUCCESS" + result + "\r\n";
    }

    //DELETE-USER\tusername\tpassword\r\n
    public String deleteUser(String[] args) {
        String username = args[1];
        String password = stripRN(args[2]);

        //prevent from delete root
        if (username.equals("root"))
            return ErrorFactory.makeErrorMessage(-23, "CANNOT delete root!");

        if (!exist(username)) {
            return ErrorFactory.makeErrorMessage(-20);
        } else {
            for (int i = 0; i < users.length; i++) {
                if (checkAuth(username, password) && users[i].getName().equals(username) && !(users[i].getName().equals
                        ("root"))) {
                    users[i] = null;
                    if (i < username.length()) {
                        for (int j = i + 1; j < users.length; j++) {
                            users[j] = users[j - 1];
                        }
                        users = Arrays.copyOf(users, users.length - 1);
                        break;
                    }
                }
                if (i == users.length)
                    return ErrorFactory.makeErrorMessage(-21);

            }

        } //ADD-USER\twang\t1234\r\n
        //GET-ALL-USERS\troot\tcs180\r\n
        //DELETE-USER\twang\t1234\r\n

        if (usingFile) {
            try {
                al = new ArrayList<>();
                String lastfile = "";
                FileReader fr = new FileReader(input.getName());
                Scanner sc = new Scanner(fr);
                while (sc.hasNextLine()) {
                    al.add(sc.nextLine());
                }
                //remove
                for (int i = 0; i < al.size() - 1; i++) {
                    String[] token = al.get(i).split(",");
                    String tempUserName = token[0];
                    String tempPassword = token[1];
                    if (tempUserName.equals(username) && tempPassword.equals(tempPassword)) {
                        al.remove(i);
                    }
                }
                //store
                for (int i = 0; i < al.size() - 1; i++) {
                    String[] token = al.get(i).split(",");
                    String tempUserName = token[0];
                    String tempPassword = token[1];
                    lastfile = lastfile + tempUserName + "," + tempPassword + "\n";
                }

                PrintWriter pw = new PrintWriter(input.getName());
                pw.write(lastfile);

                pw.close();
                fr.close();
                sc.close();

            } catch (Exception e) {
                System.out.println("Exception has benn thrown.");
            }
        }
        return "SUCCESS\r\n";
    }

    //SEND-EMAIL\tusersname\tpassword\trecipient\tmessage\r\n;

    public String sendEmail(String[] args) {
        String username = args[1];
        String password = args[2];
        String recipient = args[3];
        String message = stripRN(args[4]);

        if (!exist(username)) {
            return ErrorFactory.makeErrorMessage(-20, "Users name do not exist");
        }

        for (User user : users) {
            if (!checkAuth(username, password)) {
                return ErrorFactory.makeErrorMessage(-21);
            }
            if (user.getName().equals(username) && user.checkPassword(password)) {
                if (!exist(recipient)) {
                    return ErrorFactory.makeErrorMessage(-20, "Recipient do not exist");
                }
                for (User user1 : users) {
                    if (user1.getName().equals(recipient)) {
                        user1.receiveEmail(username, message);
                    }
                }
            }
            //If the recipient does not exist, an appropriate error message is returned.
        }
        return "SUCCESS\r\n";

    }

    //GET-EMAILS\tusername\tpassword\tnumMessages\r\n
    public String getEmails(String[] args) {
        String username = args[1];
        String password = args[2];
        String numMesssages = stripRN(args[3]);
        int num = Integer.parseInt(numMesssages);
        //If the number of emails is negative, it returns an error
        if (num < 1) {
            return ErrorFactory.makeErrorMessage(-23);
        }

        Email[] token;
        String success = "SUCCESS";

        if (!exist(username)) {
            return ErrorFactory.makeErrorMessage(-20, "Users name do not exist");
        }
        for (int i = 0; i < users.length; i++) {
            if (!checkAuth(username, password)) {
                return ErrorFactory.makeErrorMessage(-21);
            }
            //pass
            if (users[i].getName().equals(username) && users[i].checkPassword(password)) {
                //If the requested number is greater than the number of emails available,
                //it returns as many emails as possible.
                token = users[i].retrieveEmail(num);
                if (token == null) {
                    return "SUCCESS\r\n";
                }
                for (int j = 0; j < token.length; j++) {
                    success = success + "\t" + token[j].toString();
                }
            }
        }
        return success + "\r\n";

    }

    //DELETE-EMAIL\tusersname\tpassword\temailID\r\n
    public String deleteEmail(String[] args) {
        String username = args[1];
        String password = args[2];
        String emailID;
        if (args[3].contains("\r\n")) {
            emailID = args[3].substring(0, args[2].length() - 2);
        } else {
            emailID = args[3];
        }

        long id = Long.parseLong(emailID);

        if (!exist(username)) {
            return ErrorFactory.makeErrorMessage(-20);
        } else {
            for (User user : users) {
                if (!checkAuth(username, password)) {
                    return ErrorFactory.makeErrorMessage(-21);
                }
                //pass
                if (user.getName().equals(username) && user.checkPassword(password)) {
                    //if empty
                    if (user.inbox.buffer == null || user.inbox.numElements() == 0) {
                        return ErrorFactory.makeErrorMessage(-23, "No Email to delete");
                    }
                }
                //find email using id
                for (int j = 0; j < user.numEmail(); j++) {
                    if (id == user.inbox.buffer[j].getID()) {
                        user.removeEmail(id);
                        return "SUCCESS\r\n";
                    }
                }
            }
        }
        return "SUCCESS\r\n";
    }

    //new method: find users by name
    public User findUserByName(String name) {
        for (User u : users) {
            if (u.getName().equals(name))
                return u;
        }
        return null;
    }

    //new method: check if users exist
    public boolean exist(String username) {
        for (int i = 0; i < users.length; i++) {
            if (users[i].getName().equals(username)) {
                return true;
            }
            if (!(users[i].getName().equals(username))) {
                if (i == users.length - 1) {
                    return false;
                }
            }
        }
        return true;
    }

    //new method: strip "\r\n"
    public String stripRN(String input) {
        String output = "";
        if (input.contains("\r\n")) {
            output = input.substring(0, input.length() - 2);
        } else {
            output = input;
        }
        return output;
    }

    //new method: check authentication
    public boolean checkAuth(String username, String password) {
        for (User user : users) {
            if (user.getName().equals(username) && !user.checkPassword(password)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines which client command the request is using and calls
     * the function associated with that command.
     *
     * @param request - the full line of the client request (CRLF included)
     * @return the server response
     */

    //ADD-USER\twang\t1234\r\n
    public String parseRequest(String request) {
        request = replaceEscapeChars(request);
        if (!(request.substring(request.length() - 2, request.length()).equals("\r\n"))) {
            return ErrorFactory.makeErrorMessage(-10);
        }
        String[] req = request.split("\t");
        switch (req[0]) {
            case "ADD-USER": {
                if (req.length != 3) {
                    return ErrorFactory.makeErrorMessage(-10);
                }
                return addUser(req);
            }
            case "GET-ALL-USERS": {
                if (req.length != 3) {
                    return ErrorFactory.makeErrorMessage(-10);
                }
                return getAllUsers(req);
            }
            case "DELETE-USER": {
                if (req[1].equals("root")) {
                    return ErrorFactory.makeErrorMessage(-23, "You are not authorized to remove root user!");
                }
                if (exist(req[1])) {
                    if (req.length != 3) {
                        return ErrorFactory.makeErrorMessage(-10);
                    }
                    return deleteEmail(req);
                } else {
                    return ErrorFactory.makeErrorMessage(-20);
                }
            }
            case "SEND-EMAIL": {
                if (req.length != 5) {
                    return ErrorFactory.makeErrorMessage(-10);
                }
                return sendEmail(req);
            }
            case "GET-EMAILS": {
                if (req.length != 4 || !req[3].endsWith("\r\n")) {
                    return ErrorFactory.makeErrorMessage(-10);
                }
                return getEmails(req);
            }
            case "DELETE-EMAIL": {
                if (req.length != 4) {
                    return ErrorFactory.makeErrorMessage(-10);
                }
                return deleteEmail(req);
            }
            default: {
                if (req[0].endsWith("\r\n")) {
                    return ErrorFactory.makeErrorMessage(-10);
                }
                return ErrorFactory.makeErrorMessage(-11);
            }
        }
    }
}

