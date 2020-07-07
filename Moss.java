import java.io.*;
import java.nio.file.*;
import java.util.*;

class Moss implements MossInterface {
    public List<Integer> fingerprint;

    // some contructors to use later   
    public Moss(Path file) {
        String tokenizedString = tokenize(file);
        List<Integer> kGramHashes = generateKGramHashes(tokenizedString);
        fingerprint = fingerprint(kGramHashes);
    }

    // Converts a file to a string stripped of whitespace
    public String tokenize(Path file) {
        ArrayList<Character> WHITESPACE = new ArrayList<>();
        WHITESPACE.add(' ');
        WHITESPACE.add('\n');
        WHITESPACE.add('\t');

        try {
            char nextChar;
            StringBuffer str = new StringBuffer();
            BufferedReader reader = Files.newBufferedReader(file);
            while (reader.ready()) {
                nextChar = (char) reader.read();
                if (!WHITESPACE.contains(nextChar))
                    str.append(nextChar);
            }
            return str.toString();
        } catch (Exception e) {
            System.out.println("Could not read file");
            return null;
        }
    }

    // makes a list of hash values from the kgrams to then be winnowed
    public List<Integer> generateKGramHashes(String tokenizedString) {
        List<Integer> kGramHashes = new ArrayList<Integer>();
        StringBuffer str = new StringBuffer(tokenizedString);
        while (str.length() >= k) {
            kGramHashes.add(str.substring(0, k).hashCode());
            str.deleteCharAt(0);
        }
        return kGramHashes;
    }

    // do the winnowing from the list of hashes to create the fingerprint
    public List<Integer> fingerprint(List<Integer> kGramHashes) {
        List<Integer> fingerprint = new ArrayList<>();
        Queue<Integer> window = new LinkedList<>();
        int index = 0;
        int minHash = Integer.MAX_VALUE;
        int minHashLocalIndex = 0;
        
        // set up the first window
        while (index < w) {
            int kGramHash = kGramHashes.get(index);
            if (kGramHash < minHash) {
                minHash = kGramHash;
                minHashLocalIndex = index;
            }
            window.add(kGramHash);
            index++;
        }

        // add the min hash from the first window
        fingerprint.add(minHash);

        while (index < kGramHashes.size()) {
            // remove the last element from the window and add the next kGramHash
            int kGramHash = kGramHashes.get(index);
            window.remove();
            window.add(kGramHash);
            minHashLocalIndex--;

            if (minHashLocalIndex < 0) {
                minHash = Integer.MAX_VALUE;
                int localIndex = 0;
                for (Integer hash : window) {
                    if (hash <= minHash) {
                        minHash = hash;
                        minHashLocalIndex = localIndex;
                    }
                    localIndex++;
                }
                fingerprint.add(minHash);
            } else {
                if (kGramHash < minHash) {
                    minHash = kGramHash;
                    minHashLocalIndex = w - 1;
                    fingerprint.add(minHash);
                } 
            }
            index++;
        }

        return fingerprint;
    }

    public static void main(String[] args) {
        Moss m1 = new Moss(Paths.get("AltMoss.java"));
        Moss m2 = new Moss(Paths.get("Moss.java"));
        Moss m3 = new Moss(Paths.get("TestFiles/testFile1.txt"));
    }
}
