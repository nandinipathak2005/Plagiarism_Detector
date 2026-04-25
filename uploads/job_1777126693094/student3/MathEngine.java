public class MathEngine {

    // completely different naming + structure
    public int sumValues(int a, int b) {
        return a + b;
    }

    // different control flow + logging style
    public Integer safeDivide(int numerator, int denominator) {
        if (denominator == 0) {
            System.out.println("Invalid operation: division by zero");
            return null;
        }
        return numerator / denominator;
    }

    // iterative approach replaced with different loop style
    public int computePower(int base, int exponent) {

        if (exponent == 0) return 1;

        int result = base;
        int i = 1;

        while (i < exponent) {
            result *= base;
            i++;
        }

        return result;
    }

    // entry point
    public static void main(String[] args) {

        MathEngine engine = new MathEngine();

        System.out.println("Power Output: " + engine.computePower(2, 3));
        System.out.println("Sum Output: " + engine.sumValues(5, 7));
        System.out.println("Division Output: " + engine.safeDivide(10, 2));
    }
}
