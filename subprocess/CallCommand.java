import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.io.IOException;
import java.lang.InterruptedException;


public class CallCommand {
    public static void callCommand() throws IOException, InterruptedException {
	ProcessBuilder builder = new ProcessBuilder("java", "HelloWorld");
	builder.inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE);
	Process process = builder.start();


	BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	Iterator<String> linesIterator = reader.lines().iterator();
	while (linesIterator.hasNext()) {
	    System.out.println(linesIterator.next());
	}

	process.waitFor();
    }
    
    public static void main(String[] args) {
	try {
	    callCommand();
	} catch (Exception e) {
	    System.out.println("replace this with better debugging");
	}
    }
}
