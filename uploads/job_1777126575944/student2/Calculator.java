public class Calculator {

    // reused logic (similarity part)
    public int add(int x, int y) {
        return x + y;
    }

    // changed logic (reduces similarity)
    public int divide(int a, int b) {
        if (b == 0) return 0;
        return a / b;
    }

    public int power(int base, int exp) {
        int result = 1;
        for (int i = 0; i < exp; i++) {
            result *= base;
        }
        return result;
    }

    public static void main(String[] args) {
        Calculator calc = new Calculator();
        System.out.println(calc.power(2, 3));
    }
}
