package hospitalsystem.util;

public class Math {
    public static int numberOfDigits(int number){
        int digits = 0;
        number = java.lang.Math.abs(number);

        while (number > 0) {
            digits++;
            number = number / 10;
        }

        return digits;
    }
}
