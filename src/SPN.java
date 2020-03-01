import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

public class SPN {
    int rounds = 4;
    int n = 4;
    int m = 4;

    int s = 32;

    private byte[] SBox_x = {0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF};
    private byte[] SBox_Sx = {0xE, 0x4, 0xD, 0x1, 0x2, 0xF, 0xB, 0x8, 0x3, 0xA, 0x6, 0xC, 0x5, 0x9, 0x0, 0x7};

    private int[] beta_x = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private int[] beta_Bx = {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15};

    private BigInteger[] roundKeys_encr = new BigInteger[rounds+1];
    private BigInteger[] roundKeys_decr = new BigInteger[rounds+1];



    private void calcRoundKeys(String key){
        for(int i = 0; i <= rounds; i++){
            roundKeys_encr[i] = new BigInteger(key.substring(4*i, 4*i + 16).getBytes());
        }
        for(int i = rounds; i >= 0; i--){
            if(i==rounds) roundKeys_decr[i] = roundKeys_encr[0];
            else if(i==0) roundKeys_decr[i] = roundKeys_encr[rounds];
            else roundKeys_decr[i] = betaPerm(new BigInteger(key.substring(4*i, 4*i + 16).getBytes()));
        }
    }

    private BigInteger betaPerm(BigInteger old){
        String oldString = old.toString(2);
        StringBuilder newString = new StringBuilder();
        for(int i = 0; i<beta_x.length; i++){
            newString.append(oldString.charAt(beta_Bx[i]));
        }
        return new BigInteger(newString.toString(), 2);
    }

    private BigInteger sBox_encr(BigInteger in){
        byte[] inBytes = in.toByteArray();
        for(byte b : inBytes){
            b = SBox_Sx[Arrays.binarySearch(SBox_x, b)];
        }
        return new BigInteger(inBytes);
    }

    private BigInteger sBox_decr(BigInteger in){
        byte[] inBytes = in.toByteArray();
        for(byte b : inBytes){
            b = SBox_x[Arrays.binarySearch(SBox_Sx, b)];
        }
        return new BigInteger(inBytes);
    }


    public String decrypt(String chiffre) {
        return null;
    }

    public String encrypt(String text){
        String inByteString = new BigInteger(text.getBytes()).toString(2);
        if(inByteString.length() % 16 != 0) inByteString += "1"
        //TODO SPLIT INTO 4*4 Pieces

        // Encrypt x_i
        //Init Weissschritt
        x = x.xor(roundKeys_encr[0]);
        //r-1 regular rounds
        for(i = 1, i < rounds; i++){
            x = sBox_encr(x);
            x = betaPerm(x);
            x = x.xor(roundKeys_encr[i]);
        }
        x = sBox_encr(x);
        x = x.xor(roundKeys_encr[rounds]);

    return null;

    }
}
