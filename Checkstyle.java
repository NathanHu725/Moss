import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.io.IOException;
import java.lang.InterruptedException;
import java.util.*;

public class Checkstyle {

    private LinkedList<String> classList = new LinkedList<String>();
    private LinkedList<String> suspiciousClasses = new LinkedList<String>();

    public Checkstyle(Path dir) {
        checkstyleRun(dir);
    }

    private void checkstyleRun(Path dir) {
        String tokenizedString = tokenize(dir);
        findClasses(tokenizedString);
        //run call command to find the corresponding amount of errors per class
        //after this, have to think of a way to determine outliers probably by some standard deviation

    }

    private void findClasses(String textFile) {
        int index = 0;
        int start;
        int end;
        int brackets = 0;
        while(true) {
            if(textFile.charAt(index) == '{') break;
            index++;
        }
        start = index;
        while(index < textFile.length()) {
            if(textFile.charAt(index) == '{') {
                if(brackets == 0) {
                    end = index;
                    classList.add(textFile.substring(start,end));
                    start = index;
                    brackets++;
                } else {
                    brackets++;
                }
            } else if(textFile.charAt(index) == '}') {
                brackets--;
            }
            index++;
        }
    }

    public int callCommand(String method) throws IOException, InterruptedException {
        int numErrors = 0;
        ProcessBuilder builder = new ProcessBuilder("jar", "CheckStyle");
        builder.inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE);
        Process process = builder.start();
    
    
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        Iterator<String> linesIterator = reader.lines().iterator();
        while (linesIterator.hasNext()) {
            System.out.println(linesIterator.next());
        }
        process.waitFor();
        //this is the part that needs major work
        return numErrors;
    }

    // For now this just returns a string of the file
    private String tokenize(Path file) {
        try {
            return Files.readString(file);
        } catch (Exception e) {
            System.out.println("Could not read file");
            return null;
        }
    }

    public LinkedList<String> getSuspicious() {
        return suspiciousClasses;
    }


}