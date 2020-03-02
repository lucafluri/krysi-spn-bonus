import java.math.BigInteger;
import java.util.Arrays;

public class SPN_CTR {
    String key = "00111010100101001101011000111111";
    //rand used for encryption => same as in excercise
    BigInteger rand = BigInteger.valueOf(0b0000010011010010);

    int rounds = 4;

    //Formatted SBox
    private String[] SBox_x = { its(0x0), its(0x1), its(0x2), its(0x3), its(0x4), its(0x5), its(0x6), its(0x7), its(0x8), its(0x9), its(0xA), its(0xB), its(0xC), its(0xD), its(0xE), its(0xF) };
    private String[] SBox_Sx = { its(0xE), its(0x4), its(0xD), its(0x1), its(0x2), its(0xF), its(0xB), its(0x8), its(0x3), its(0xA), its(0x6), its(0xC), its(0x5), its(0x9), its(0x0), its(0x7) };

    private int[] beta_Bx = { 0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15 };

    private String[] roundKeys_encr = new String[rounds + 1];

    //Helper function to format hex to 4 digit string representation
    private String its(int i){
        return String.format("%4s", Integer.toBinaryString(i)).replace(' ', '0');
    }

    //Helper function to format string of binary numbers with leading 0s
    private String format(String i, int length){
        return String.format("%" + length + "s", i).replace(' ', '0');
    }

    //Calculates encryption roundKeys, decryption roundkeys are not needed in CTR mode
    private void calcRoundKeys() {
        for (int i = 0; i <= rounds; i++) {
            roundKeys_encr[i] = key.substring(4 * i, 4 * i + 16);
        }
    }

    //String based Beta permutation
    private String betaPerm(String old) {
        StringBuilder newString = new StringBuilder();
        for (int beta_bx : beta_Bx) {
            newString.append(old.charAt(beta_bx));
        }
        return newString.toString();
    }

    //String based Sbox encryption => decryption never needed in CTR mode
    private String sBox_encr(String in) {
        String[] inSplit = in.split("(?<=\\G.{4})");
        StringBuilder sb = new StringBuilder();
        for (String s : inSplit) {
            sb.append(SBox_Sx[Arrays.asList(SBox_x).indexOf(s)]);
        }
        return sb.toString();
    }

    //Converts strings to bigints and xors them => returns String
    private String xor(String a, String b){
        return format(new BigInteger(a, 2).xor(new BigInteger(b, 2)).toString(2), 16);
    }

    //Calculates the current SPN step and with current random y_i and current x_i
    private String SPN(BigInteger randIn, int round, String xs){
        String tmp = format(randIn.add(BigInteger.valueOf(round)).toString(2), 16);
        // Encrypt x_i
        //Init Weissschritt
        tmp = xor(tmp, roundKeys_encr[0]);
        //r-1 regular rounds
        for (int i = 1; i < rounds; i++) {
            tmp = sBox_encr(tmp);
            tmp = betaPerm(tmp);
            tmp = xor(tmp, roundKeys_encr[i]);
        }
        //last round without bitperm
        tmp = sBox_encr(tmp);
        tmp = xor(tmp, roundKeys_encr[rounds]);
        //=> z_i
        return xor(xs, tmp);
    }

    //Decrypts chiffre String with this.key
    public String decrypt(String chiffre) {
        calcRoundKeys();
        StringBuilder y = new StringBuilder();

        int round = 0;
        BigInteger randIn = null; //Read in Y_-1
        boolean gotRand = false;
        //Splitting into pieces of 16 bits
        for (String xs : chiffre.split("(?<=\\G.{16})")) {
            //Get "random" y_-1
            if (!gotRand) {
                randIn = new BigInteger(xs, 2);
                gotRand = true;
                continue;
            }
            //Encrypting current round
            String tmp = SPN(randIn, round++, xs);
            //Appending to stringbuilder
            y.append(format(tmp, 16));
        }

        String y_out = y.toString();

        //Removing tailing 0s and one 1
        while(y_out.charAt(y_out.length()-1) == '0'){
            y_out = y_out.substring(0, y_out.length()-1);
        }
        y_out = y_out.substring(0, y_out.length()-1);

        StringBuilder sb = new StringBuilder(); // Some place to store the chars
        //Converting to ASCII characters
        for(int i = 0; i<y_out.length()/8; i++){
            String toAppend = Character.toString((char) Integer.parseInt(y_out.substring(8 * i, 8 * i + 8), 2));
            sb.append(toAppend);
        }

        System.out.println("Decrypted: " + sb.toString() + "\n");
        return sb.toString();
    }

    public String encrypt(String text) {
        System.out.println("Input: " + text);
        calcRoundKeys();

        //Convert Text to ASCII and then to binary string
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            //Converting to ASCII with leading 0s
            sb.append(format(Integer.toBinaryString(c), 8));
        }
        //Fill to be divisable by 16 with 1000...
        String inByteString = sb.toString();
        inByteString += "1";
        while(inByteString.length() % 16 != 0)
            inByteString += "0";

        //New Stringbuilder with starts with y_-1 in it
        StringBuilder y = new StringBuilder(format(rand.toString(2), 16));

        int round = 0;
        //Splitting into pieces of 16 bits
        for (String xs : inByteString.split("(?<=\\G.{16})")) {
            //encrypting key and y_-1 + i and then xor with x_i
            String tmp = SPN(rand, round++, xs);
            y.append(format(tmp, 16));
        }

        System.out.println("Encrypted: " + y.toString());
        return y.toString();

    }
}
