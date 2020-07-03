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
            return null;
        }
    }

    // This is probabaly very innefieicent,
    // I just wanted to give it a quick run through to see how it worked
    public List<Integer> fingerprint(String tokenizedString) {
        List<Integer> fingerprint = new ArrayList<Integer>();
        Queue<Integer> window = new LinkedList<>();
        StringBuffer sBuffer = new StringBuffer(tokenizedString);

        int index = 0;
        int currentIndex = 0;
        int lastIndex = 0;

        while(index < w) {
            window.add(sBuffer.substring(0, k).hashCode());
            sBuffer = sBuffer.deleteCharAt(0);
            index++;
        }

        while(sBuffer.length() >= w) {
            int minHash = Integer.MAX_VALUE;
            int localIndex = 0;
            for (Integer hashValue: window) {
                if (hashValue < minHash) {
                    minHash = hashValue;
                    currentIndex = index - w + localIndex;
                }
                localIndex++;
            }

            if (currentIndex != lastIndex ){//|| index == w) {
                lastIndex = currentIndex;
                fingerprint.add(minHash);
            }

            window.remove();
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
        String tokenizedStringOne = m.tokenize(Paths.get("TestFiles/testFile1.txt"));
        String tokenizedStringTwo = m.tokenize(Paths.get("TestFiles/testFile2.txt"));

        List<Integer> fingerprintOne = m.fingerprint(tokenizedStringOne);
        List<Integer> fingerprintTwo = m.fingerprint(tokenizedStringTwo);

        System.out.println(m.score(fingerprintOne, fingerprintTwo));

    }
}
