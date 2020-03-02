import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {

        SPN spn = new SPN();
        String toEncrypt = "Hello there";
        spn.decrypt(spn.encrypt(toEncrypt));

        String input = Files.readString(Paths.get("chiffre.txt"));
        spn.decrypt(input);


    }
}
