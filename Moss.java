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
        String tokenizedStringOne = m.tokenize(Paths.get("TestFiles/testFile3.java"));
        String tokenizedStringTwo = m.tokenize(Paths.get("TestFiles/testFile4.java"));

        List<Integer> fingerprintOne = m.fingerprint(tokenizedStringOne);
        List<Integer> fingerprintTwo = m.fingerprint(tokenizedStringTwo);

        System.out.println(m.score(fingerprintOne, fingerprintTwo));

    }
}
