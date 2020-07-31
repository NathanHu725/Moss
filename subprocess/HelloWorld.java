public class HelloWorld {

    // Takes an optional parameter, n, which specifies
    // the number of times to print "Hello World"
    public static void main(String[] args) {
	int n = 1;

	if (args.length > 0) {
	    try {
		n = Integer.valueOf(args[0]);
	    } catch (NumberFormatException e) {
		n = 1;
	    }
	}

	for (int i = 0; i < n; i++) {
	    System.out.println("Hello World");
	}
    }
}
