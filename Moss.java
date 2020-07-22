import java.nio.file.*;
import java.util.*;
import com.strobel.decompiler.*;
import java.io.*;

public class Moss {
    /* guarentee threshold - if a string is as long as t, MOSS will find it */
    public static int t;

    /* noise threshold - should be high enough to eliminate coincidental matches */
    public static int k;

    /* window size - number of consecutive hashes of k-grams for winnowing */
    public static int w = t - k + 1;

    /* the document fingerprint */
    private List<Integer> fingerprint;

    // some contructors to use later
    public Moss(Path file) {
        this(file, 15, 7);
    }

    public Moss(Path file, int t, int k) {
        Moss.t = t;
        Moss.k = k;
        String tokenizedString = tokenize(file);
        List<Integer> kGramHashes = generateKGramHashes(tokenizedString);
        fingerprint = fingerprint(kGramHashes);
    }

    /* fingerprint getter */
    public List<Integer> getFingerprint() {
        return fingerprint;
    }

    /* Compiles then decompiles a java file, overwriting the original file 
     * * Maybe the decompiler isnt perfect, but there are sometimes issues here. *
     */
    public static void compileAndDecompile(Path file) {
        final String fileName = file.toString();
        final String compiledFileName = fileName.substring(0, fileName.length() - 5) + ".class";
        final String decompiledFileName = "Decompiled_" + fileName;

        //run javac in command line
        try {
            Process p = Runtime.getRuntime().exec("javac " + fileName);
            System.out.println("Compiling...");
            p.waitFor();
        } catch (Exception e) {
            System.out.println("Compiling failed.");
            return;
        }

        Moss.decompile(decompiledFileName, compiledFileName);

    }

    // Compile all java files in a directory
    public static void compileAll(Path dir) throws Exception {
        Process p = Runtime.getRuntime().exec("javac " + dir.toString() + "*.java");
        p.waitFor();
    }

    // use procyon to decompile the file
    public static void decompile(String decompiledFileName, String compiledFileName) {
        final DecompilerSettings settings = DecompilerSettings.javaDefaults();

        try (final FileOutputStream stream = new FileOutputStream(decompiledFileName);
            final OutputStreamWriter writer = new OutputStreamWriter(stream)) {
            Decompiler.decompile(compiledFileName, new PlainTextOutput(writer), settings);
            writer.close();
        }  catch (Exception e) {
            System.out.println("Decompiling failed.");
        }
    }

    // Converts a file to a string stripped of whitespace
    public String tokenize(Path file) {
        try {
            return SimpleLexer.jLex(Files.newBufferedReader(file));
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

    /* 
     * Compute the jaccard similarity index.
     * Updated to treat the fingerprint as a set to accurately reflect the
     * jaccard coefficient.
     */
    public static Double jaccardScore(List<Integer> fingerprintA, List<Integer> fingerprintB) {
        Set<Integer> setA = new HashSet<Integer>(fingerprintA);
        Set<Integer> setB = new HashSet<Integer>(fingerprintB);
        int union = setA.size() + setB.size();
        int intersection = 0;
        
        for (Integer i : setA) {
            if (setB.contains(i)) {
                intersection++;
                union--;
            }
        }
        return Double.valueOf(intersection) / Double.valueOf(union);
    }

    /* 
     * Compute the Sørensen–Dice coefficient.
     * Updated to treat the fingerprint as a set to accurately reflect the
     * Sørensen–Dice coefficient.
     */
    public static Double sdScore(List<Integer> fingerprintA, List<Integer> fingerprintB) {
        Set<Integer> setA = new HashSet<Integer>(fingerprintA);
        Set<Integer> setB = new HashSet<Integer>(fingerprintB);
        int denominator = setA.size() + setB.size();
        int intersection = 0;
        
        for (Integer i : setA) {
            if (setB.contains(i)) {
                intersection++;
            }
        }
        return 2 * Double.valueOf(intersection) / Double.valueOf(denominator);
    }

    // debugging
    public static void main(String[] args) {
        Moss.compileAndDecompile(Paths.get("testJava.java"));
        
        Moss m1 = new Moss(Paths.get("Moss.java"));
        Moss m2 = new Moss(Paths.get("SimpleLexer.java"));
        Moss m3 = new Moss(Paths.get("testJava.java"));

        System.out.println(m1.getFingerprint());
        System.out.println(m2.getFingerprint());
        System.out.println(m3.getFingerprint());


        System.out.println("Jaccard scores");
        System.out.println(Moss.jaccardScore(m1.getFingerprint(), m2.getFingerprint()));
        System.out.println(Moss.jaccardScore(m1.getFingerprint(), m3.getFingerprint()));
        System.out.println(Moss.jaccardScore(m2.getFingerprint(), m3.getFingerprint()));


        System.out.println("Sørensen–Dice scores");
        System.out.println(Moss.sdScore(m1.getFingerprint(), m2.getFingerprint()));
        System.out.println(Moss.sdScore(m1.getFingerprint(), m3.getFingerprint()));
        System.out.println(Moss.sdScore(m2.getFingerprint(), m3.getFingerprint()));
    }
}
