import java.nio.file.*;
import java.util.*;

class Moss implements MossInterface { 
    // some contructors to use later   
    public Moss(Path dir) {}
    public Moss() {}

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
        Queue<Integer> window = new LinkedList<>();
        StringBuffer sBuffer = new StringBuffer(tokenizedString);

        int minHash = Integer.MAX_VALUE;
        int minHashLocalIndex = 0;
        
        // set up the first window
        for (int i = 0; i < w; i++) {
            int kGramHash = sBuffer.substring(0, k).hashCode();
            if (kGramHash < minHash) {
                minHash = kGramHash;
                minHashLocalIndex = i;
            }
            window.add(kGramHash);
            sBuffer = sBuffer.deleteCharAt(0);
        }

        // add the min hash from the first window
        fingerprint.add(minHash);

        while (sBuffer.length() >= k) {
            // remove the last element from the window and add the next kGramHash
            int kGramHash = sBuffer.substring(0, k).hashCode();
            window.remove();
            window.add(kGramHash);
            sBuffer = sBuffer.deleteCharAt(0);
            minHashLocalIndex--;

            if (minHashLocalIndex < 0) {
                minHash = Integer.MAX_VALUE;
                int localIndex = 0;
                for (Integer hash : window) {
                    minHashLocalIndex++;
                    if (hash < minHash) {
                        minHash = hash;
                        minHashLocalIndex = localIndex;
                    }
                    localIndex++;
                }
                fingerprint.add(minHash);
            } else {
                if (kGramHash < minHash) {
                    minHash = kGramHash;
                    minHashLocalIndex = w;
                    fingerprint.add(minHash);
                } 
            }
        }

        return fingerprint;
    }

    public Double score(List<Integer> fingerprintOne, List<Integer> fingerprintTwo) {
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

    public static void main(String[] args) {
        Moss m = new Moss();
        String tokenizedStringOne = m.tokenize(Paths.get("AltMoss.java"));
        String tokenizedStringTwo = m.tokenize(Paths.get("Moss.java"));

        List<Integer> fingerprintOne = m.fingerprint(tokenizedStringOne);
        List<Integer> fingerprintTwo = m.fingerprint(tokenizedStringTwo);
        

        System.out.println(fingerprintOne);
        System.out.println(fingerprintTwo);

        System.out.println(m.score(fingerprintOne, fingerprintTwo));
    }
}
