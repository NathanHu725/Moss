import java.nio.file.*;
import java.util.*;

public interface MossInterface {

    /* Takes a java (or other language) file and returns a string stripped
     * of names and comments. */
    public String tokenize(Path file);
    
    /* Use the winnowing algo to make a fingerprint of the document. */
    public List<Integer> fingerprint(String tokenizedString);

    }