import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

public class SPN {
    String key = "00111010100101001101011000111111";
    BigInteger rand = BigInteger.valueOf(0b0000010011010010);
    int rounds = 4;

    private String[] SBox_x = { its(0x0), its(0x1), its(0x2), its(0x3), its(0x4), its(0x5), its(0x6), its(0x7), its(0x8), its(0x9), its(0xA), its(0xB), its(0xC), its(0xD), its(0xE), its(0xF) };
    private String[] SBox_Sx = { its(0xE), its(0x4), its(0xD), its(0x1), its(0x2), its(0xF), its(0xB), its(0x8), its(0x3), its(0xA), its(0x6), its(0xC), its(0x5), its(0x9), its(0x0), its(0x7) };

    private int[] beta_x = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
    private int[] beta_Bx = { 0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15 };

    private String[] roundKeys_encr = new String[rounds + 1];

    private String its(int i){
        return String.format("%4s", Integer.toBinaryString(i)).replace(' ', '0');
    }

    private String format(String i, int length){
        return String.format("%" + length + "s", i).replace(' ', '0');
    }

    private void calcRoundKeys() {
        for (int i = 0; i <= rounds; i++) {
            roundKeys_encr[i] = key.substring(4 * i, 4 * i + 16);
        }
    }
//
    private String betaPerm(String old) {
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < beta_x.length; i++) {
            newString.append(old.charAt(beta_Bx[i]));
        }
        return newString.toString();
    }
//
    private String sBox_encr(String in) {
        String[] inSplit = in.split("(?<=\\G.{4})");
        StringBuilder sb = new StringBuilder();
        for (String s : inSplit) {
            sb.append(SBox_Sx[Arrays.asList(SBox_x).indexOf(s)]);
        }
        return sb.toString();
    }

    private String xor(String a, String b){
        return format(new BigInteger(a, 2).xor(new BigInteger(b, 2)).toString(2), 16);
    }
//
    public String decrypt(String chiffre) {
        calcRoundKeys();
        StringBuilder y = new StringBuilder();
//        //Splitting into pieces of 16 bits
        int round = 0;
        BigInteger randIn = null;
        boolean gotRand = false;
        for (String xs : chiffre.split("(?<=\\G.{16})")) {
            //Get "random" y_-1
            if (!gotRand) {
                randIn = new BigInteger(xs, 2);
                gotRand = true;
                continue;
            }
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
            tmp = sBox_encr(tmp);
            tmp = xor(tmp, roundKeys_encr[rounds]);
            //=> z_i
            tmp = xor(xs, tmp);
            y.append(String.format("%16s", tmp.replace(' ', '0')));

            round++;
        }

//
        //TODO REMOVE tailing 100000
        String y_out = y.toString();

        while(y_out.charAt(y_out.length()-1) == '0'){
            y_out = y_out.substring(0, y_out.length()-1);
        }
        y_out = y_out.substring(0, y_out.length()-1);

        StringBuilder sb = new StringBuilder(); // Some place to store the chars

        for(int i = 0; i<y_out.length()/8; i++){
            String toAppend = Character.toString((char) Integer.parseInt(y_out.substring(8 * i, 8 * i + 8), 2));
            sb.append(toAppend);
        }

        System.out.println("Decrypted: " + sb.toString());
        return sb.toString();
    }
//
    public String encrypt(String text) {
        System.out.println("Input: " + text);
        calcRoundKeys();

        //Convert Text to ASCII and then to binary string
        //Leading 0 goes missing
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            //Converting to ASCII with leading 0s
            sb.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        //Fill to be divisable by 16
        String inByteString = sb.toString();
        inByteString += "1";
        if (inByteString.length() % 16 != 0)
            inByteString += "0".repeat(inByteString.length() % 16 - 1);


        StringBuilder y = new StringBuilder(String.format("%16s", rand.toString(2)).replace(' ', '0'));
        //Splitting into pieces of 16 bits


        int round = 0;
        for (String xs : inByteString.split("(?<=\\G.{16})")) {

            String tmp = format(rand.add(BigInteger.valueOf(round)).toString(2), 16);

//             Encrypt x_i
            //Init Weissschritt
            tmp = xor(tmp, roundKeys_encr[0]);
            //r-1 regular rounds
            for (int i = 1; i < rounds; i++) {
                tmp = sBox_encr(tmp);
                tmp = betaPerm(tmp);
                tmp = xor(tmp, roundKeys_encr[i]);
            }
            tmp = sBox_encr(tmp);
            tmp = xor(tmp, roundKeys_encr[rounds]);
            //=> z_i
            tmp = xor(xs, tmp);
            //=> y_i
            y.append(String.format("%16s", tmp.replace(' ', '0')));

            round++;

        }

        System.out.println("Encrypted: " + y.toString());
        return y.toString();

    }
}
