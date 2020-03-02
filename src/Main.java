import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {

        SPN_CTR spn = new SPN_CTR();

        spn.decrypt(Files.readString(Paths.get("chiffre.txt")));

        spn.decrypt(spn.encrypt("Encryption and decryption test"));

        spn.decrypt(Files.readString(Paths.get("decryptme.txt")));


    }
}
