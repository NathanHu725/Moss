import java.util.*;
import java.io.File;
import structure5.Association;

public class MossUserInterface {

    public static void main(String[] args) {

        System.out.println("Welcome to the Williams Moss plagarism detection system.");
        System.out.println("Please enter the path of the folder for the files you would like to evaluate below:");
        Scanner in = new Scanner(System.in);
        String folderPath = in.nextLine();
        double scoreThreshold = 0;

        while(true) {
            System.out.println("What would you like the lowest score threshold to be?");
            try {
                scoreThreshold = Double.parseDouble(in.nextLine());
                if(scoreThreshold > 1 || scoreThreshold < 0) throw new NullPointerException();
                break;
            } catch(Exception e) {
                System.out.println("That was not a valid score threshold, please try again.");
            }
        }

        System.out.println("Would you like to set your own t and k values? (Yes or No)");
        String answerOne = in.nextLine();

        //this is where we can change the preset t and k value. Making the program know when to use which constructor is a lot more difficult
        int tValue = 15;
        int kValue = 8;

        if(answerOne.charAt(0) == 'y' || answerOne.charAt(0) == 'Y') {
            while(true) {
                System.out.println("What would you like the t value to be?");
                try {
                    tValue = in.nextInt();
                    break;
                } catch (Exception e) {
                    System.out.println("That was not a valid t value, please try again.");
                }
            }
            while(true) {
                System.out.println("What would you like the k value to be?");
                try {
                    kValue = in.nextInt();
                    break;
                } catch (Exception e) {
                    System.out.println("That was not a valid k value, please try again.");
                }
            }
        }

        Vector<Association<File, List<Integer>>> fingerprints = new Vector<Association<File, List<Integer>>>();
        File folder = new File(folderPath);
        File[] filesToBeScanned = folder.listFiles();

        for(File file : filesToBeScanned) {
            Moss current = new Moss(file.toPath(), tValue, kValue);
            fingerprints.add(new Association<File, List<Integer>>(file, current.getFingerprint()));
        }

        System.out.println("The following pairs of documents are suspiciously close:");

        for(int a = 0; a < fingerprints.size(); a++) {
            for(int b = a + 1; b < fingerprints.size(); b++) {
                if(score(fingerprints.get(a).getValue(), fingerprints.get(b).getValue()) > scoreThreshold) {
                    System.out.println(fingerprints.get(a).getKey().getName() + " and " + fingerprints.get(b).getKey().getName());
                }
            }
        }
    }

    /* Compute the similarity between two document fingerprints. */
    private static Double score(List<Integer> fingerprintOne, List<Integer> fingerprintTwo) {
        int union = fingerprintOne.size() + fingerprintTwo.size();
        int intersection = 0;
        
        for (Integer i : fingerprintOne) {
            if (fingerprintTwo.contains(i)) {
                intersection++;
                union--;
            }
        }

        return Double.valueOf(intersection) / Double.valueOf(union);
    }

}