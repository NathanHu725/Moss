import java.nio.file.*;
import java.util.*;
import java.io.BufferedReader;

public interface AltMossInterface {
    /* guarentee threshold - if a string is as long as t, MOSS will find it */
    public static final int t = 10;

    /* noise threshold - should be high enough to eliminate coincidental matches */
    public static final int k = 5;

    /* window size - number of consecutive hashes of k-grams for winnowing */
    public static final int w = t - k + 1;

    /* Takes a java (or other language) file and returns a string stripped
     * of names and comments. */
    public BufferedReader tokenize(Path file);
    
    /* Use the winnowing algo to make a fingerprint of the document. */
    public List<Integer> fingerprint(BufferedReader tokenizedString);

    /* Compute the similarity between two document fingerprints. */
    public Double score(List<Integer> fingerprintOne, List<Integer> fingerprintTwo);

}