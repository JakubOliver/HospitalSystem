package hospitalsystem.util;

/**
 * Custom math util class containing math methods useful across the project (which do not appear inbuild libraries).
 */
public class Math {
    /**
     * Private constructor of the math class. (This class should not be instantiable, therefor has private/unreachable constructor)
     */
    private Math(){}

    /**
     * Returns number of digits that the number have.
     *
     * @param number Number for which we want to know number of digits.
     * @return number of digits that the provided number have.
     */
    public static int numberOfDigits(int number){
        //In the code we could use decadic logarithm and rounding, but creating own method made me more sense,
        //because calling the function is better for context while reading the code and the inbuild log
        //function uses floating point number and in this case we do not need that.

        int digits = 0;
        number = java.lang.Math.abs(number);

        while (number > 0) {
            digits++;
            number = number / 10;
        }

        return digits;
    }
}
