import java.nio.file.*;
import java.util.*;

public interface MossInterface {
    /* guarentee threshold - if a string is as long as t, MOSS will find it */
    public static final int t = 10;

    /* noise threshold - should be high enough to eliminate coincidental matches */
    public static final int k = 5;

    /* window size - number of consecutive hashes of k-grams for winnowing */
    public static final int w = t - k + 1;

    /* Takes a java (or other language) file and returns a string stripped
     * of names and comments. */
    public String tokenize(Path file);

    public List<Integer> generateKGramHashes(String tokenizedString);
    
    /* Use the winnowing algo to make a fingerprint of the document. */
    public List<Integer> fingerprint(List<Integer> kGramHashes);

    /* Compute the similarity between two document fingerprints. */
    public static Double score(List<Integer> fingerprintOne, List<Integer> fingerprintTwo) {
        // not quite jaccard similarity but nearly
        int union = fingerprintOne.size() + fingerprintTwo.size();
        int intersection = 0;
        
        for (Integer i : fingerprintOne) {
            if (fingerprintTwo.contains(i)) {
                intersection++;
                union--;
            }
        }

        System.out.println(intersection + " / " + union);
        return Double.valueOf(intersection) / Double.valueOf(union);
    }
}