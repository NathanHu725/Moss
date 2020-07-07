import java.nio.file.*;
import java.util.*;

class Moss implements MossInterface { 

    /* guarentee threshold - if a string is as long as t, MOSS will find it */
    public int t;

    /* noise threshold - should be high enough to eliminate coincidental matches */
    public int k;

    /* window size - number of consecutive hashes of k-grams for winnowing */
    public int w = t - k + 1;

    /* score - saves the score calculated for a given document for extraction at a later time */
    public List<Integer> docFingerprint;

    // some contructors to use later   
    public Moss(Path dir, int tValue, int kValue) {
        t = tValue;
        k = kValue;
        mossRun(dir);
    }

    // For now this just returns a string of the file
    public String tokenize(Path file) {
        try {
            return Files.readString(file);
        } catch (Exception e) {
            System.out.println("Could not read file");
            return null;
        }
    }

    // This is probabaly very innefieicent,
    // I just wanted to give it a quick run through to see how it worked
    public List<Integer> fingerprint(String tokenizedString) {
        List<Integer> fingerprint = new ArrayList<Integer>();
        Vector<Integer> window = new Vector<Integer>(w);
        StringBuffer sBuffer = new StringBuffer(tokenizedString);

        int index = 0;

        while(index < w) {
            window.add(sBuffer.substring(0, k).hashCode());
            sBuffer = sBuffer.deleteCharAt(0);
            index++;
        }

        int minHashIndex = 0;

        while(sBuffer.length() >= k) {

            if(minHashIndex == 0) {
                int minHash = window.get(0);

                for(int i = 1; i < window.size(); i++) {
                    int hashValue = window.get(i);

                    if(hashValue < minHash) {
                        minHash = hashValue;
                        minHashIndex = i;
                    }

                }

                fingerprint.add(window.get(minHashIndex));
            } else {

                if(window.get(minHashIndex--) > window.get(window.size() - 1)) {
                    minHashIndex = window.size() - 1;
                    fingerprint.add(window.get(minHashIndex));
                }

            }
            window.remove(0);
            window.add(sBuffer.substring(0, k).hashCode());
            sBuffer = sBuffer.deleteCharAt(0);
            index++;
        }

        return fingerprint;
    }

    private void mossRun(Path dir) {
        String tokenizedString = tokenize(dir);
        docFingerprint = fingerprint(tokenizedString);

    }

    public List<Integer> getFingerprint() {
        return docFingerprint;
    }
}
