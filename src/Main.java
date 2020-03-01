import java.math.BigInteger;

public class Main {

    public static void main(String[] args) {
	    //TODO Read in chiffre.txt as bytes
	    //TODO Clean up
        byte[] test = {0b0011, 0b0011, 0b0011, 0b0011, 0b0011, 0b0011, 0b0011, 0b0011, 0b0011, 0b0011, 0b0011};
        System.out.println(new BigInteger(test));
        for(byte i : new BigInteger(test).toByteArray()) System.out.print(i);

        System.out.println(BigInteger.ONE.xor(BigInteger.TEN));
        System.out.println(0b1111);
        String bytes = BigInteger.valueOf(0b1111).toString(2);

        System.out.println(bytes);
        System.out.println(new BigInteger(bytes, 2));
        System.out.print(new String(bytes.getBytes()));

        System.out.println("\n" + new BigInteger("A".getBytes()).toString(2));
    }
}
