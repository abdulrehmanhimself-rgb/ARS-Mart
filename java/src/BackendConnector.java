//BackendConnector.java
import java.io.*;
import java.nio.charset.StandardCharsets;

public class BackendConnector {

    static final String EXE_PATH =
        "cpp\\build\\main.exe";

    static final File WORKING_DIR =
        new File(".");

    public static String runCommand(String commandArgs) {
        try {
            // Execute via cmd.exe with a single command line string (most compatible on Windows)
            String cmdLine = "\"" + EXE_PATH + "\"";
            if (commandArgs != null && !commandArgs.trim().isEmpty()) {
                cmdLine += " " + commandArgs.trim();
            }

            ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe", "/c", cmdLine
            );

            pb.directory(WORKING_DIR);
            pb.redirectErrorStream(true);

            System.out.println("[BackendConnector] cmdLine=" + cmdLine);

            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            )) {
                String line;
                boolean first = true;
                while ((line = reader.readLine()) != null) {
                    if (!first) output.append("\n");
                    output.append(line); // preserve exact token formatting from C++
                    first = false;
                }
            }


            process.waitFor();

            String result = output.toString().trim();
            System.out.println("[BackendConnector] raw-output='" + result + "'");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}