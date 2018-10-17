package Interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A temporary cli for testing purposes
 */
public class Cli {
    private static final Logger logger = LoggerFactory.getLogger("general");

    private Cli() {} // Do not have to initialize

    public static void start(String[] args) {
        try {
            processHelp(args[0]);

        } catch (Throwable e) {
            logger.error("Error parsing command line: [{}]", e.getMessage());
            System.exit(1);
        }
    }

    // show help
    private static void processHelp(String arg) {
        if ("--help".equals(arg)) {
            printHelp();

            System.exit(1);
        }
    }

    // show help
    private static void processConfig(String arg) {
        if ("-config".equals(arg)) {
            logger.info("Overriding config file with CLI options: {}", "nn");

            System.exit(1);
        }
    }

    private static void printHelp() {
        System.out.println("--help                       -- this help message ");
        System.out.println("-config <name_of_this_peer>  -- this will refer to the json file in the resources folder");
        System.out.println();
        System.out.println("e.g: carbc  -config peer1 ");
        System.out.println();
    }
}
