import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

/**
 * CS180 -  *
 * Explain briefly the functionality of the program.
 *
 * @author Han Wang, wang2786@purdue.edu, LE1, Lab L03
 * @version 4/12/16.
 */
public class DynamicBuffer {
    public Email[] buffer;
    private int initSize;

    public DynamicBuffer(int initSize) {
        this.initSize = initSize;
        buffer = new Email[initSize];
    }

    public int numElements() {
        int count = 0;
        for (Email e : buffer) {
            if (e != null) {
                count++;
            }
        }
        return count;
    }

    public int getBufferSize() {
        return buffer.length;
    }

    public void add(Email email) {
        buffer[numElements()] = email;

        //if array full, double buffer size
        if (numElements() == initSize) {
            buffer = Arrays.copyOf(buffer, initSize * 2);
            initSize = initSize * 2;
        }
    }

    public boolean remove(int index) {
        //check index validity
        if (buffer[index] == null) {
            return false;
        }
        //move element to the left
        System.arraycopy(buffer, index + 1, buffer, index, buffer.length - 1 - index);

        //if num of emails smaller than 1/4 of the size
        //cut array length in half
        if (numElements() <= (buffer.length / 4)) {
            buffer = Arrays.copyOf(buffer, buffer.length / 2);
        }
        return true;
    }

    public Email[] getNewest(int n) {
        Email[] token;
        //check if n is valid
        if (n < 0 || numElements() == 0) {
            return null;
        }
        //if valid
        else {
            //initialize token array to store result
            token = new Email[n];

            //Return all emails if n is greater than the number of emails in the buffer
            if (n > numElements()) {
                for (int i = 0; i < numElements(); i++) {
                    token[i] = buffer[i];
                }
                token = Arrays.copyOf(token, token.length - (n - numElements()));
                Collections.reverse(Arrays.asList(token));
            } else {
                Email[] tempToken = new Email[numElements()];
                System.arraycopy(buffer, 0, tempToken, 0, numElements());
                Collections.reverse(Arrays.asList(tempToken));
                System.arraycopy(tempToken, 0, token, 0, n);
            }
        }
        return token;
    }
}
