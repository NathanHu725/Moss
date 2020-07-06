import java.io.*;
import java.nio.file.*;
import java.util.*;

class AltMoss implements AltMossInterface {
    // some contructors to use later
    public AltMoss(Path dir) {
    }

    public AltMoss() {
    }

    // For now this just returns a string of the file
    public BufferedReader tokenize(Path file) {
        try {
            return Files.newBufferedReader(file);
        } catch (Exception e) {
            System.out.println("Could not read file");
            return null;
        }
    }

    // A bunch of duplicate code but maybe faster
    public List<Integer> fingerprint(BufferedReader tokenizedReader) {
        int nextChar;
        List<Integer> fingerprint = new ArrayList<Integer>();
        Queue<Integer> window = new LinkedList<>();
        StringBuffer kGram = new StringBuffer(k);

        // set up the first kGram
        for (int i = 0; i < k; i++) {
            try {
                nextChar = tokenizedReader.read();
                kGram.append((char) nextChar);
            } catch (IOException e) {
                System.out.println("Reader did not have enough text");
                return null;
            }
        }

        // set up the first window
        for (int i = 0; i < w; i++) {
            // add the hashCode to the window
            window.add(kGram.toString().hashCode());

            // update the kGram
            kGram.deleteCharAt(0);
            try {
                nextChar = tokenizedReader.read();
                kGram.append((char)nextChar);
            } catch (IOException e) {
                System.out.println("Reader did not have enough text");
                return null;
            }
        }

        int minHash = Integer.MAX_VALUE;
        int minHashLocalIndex = -1;
        int kGramHash = 0; // this num doesnt matter

        try {
            while (tokenizedReader.ready()) {
                // add the fingerprint from the current window if need be
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

                // remove the last element from the window and add the next kGramHash
                kGram.deleteCharAt(0);
                if ((nextChar = tokenizedReader.read()) != -1) {
                    kGram.append((char) nextChar);
                } else
                    return fingerprint;

                kGramHash = kGram.toString().hashCode();
                window.remove();
                window.add(kGramHash);

                minHashLocalIndex--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // need to do last window

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
        for (int i = 0; i < 1000; i++) {
        AltMoss m = new AltMoss();
        BufferedReader tokenizedStringOne = m.tokenize(Paths.get("AltMoss.java"));
        BufferedReader tokenizedStringTwo = m.tokenize(Paths.get("Moss.java"));

        List<Integer> fingerprintOne = m.fingerprint(tokenizedStringOne);
        List<Integer> fingerprintTwo = m.fingerprint(tokenizedStringTwo);
        }

        //System.out.println(fingerprintOne);
        //System.out.println(fingerprintTwo);

        //System.out.println(m.score(fingerprintOne, fingerprintTwo));
    }
}
