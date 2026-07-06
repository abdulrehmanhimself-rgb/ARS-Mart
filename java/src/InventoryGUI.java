import java.io.*;

public class InventoryGUI {

    public static void main(String[] args) {

        try {

            Process p = Runtime.getRuntime().exec(
                "cpp\\build\\main.exe"
            );

            BufferedReader reader =
                new BufferedReader(
                    new InputStreamReader(p.getInputStream())
                );

            String line;

            while((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }

        }
        catch(IOException e)
        {
        }
    }
}