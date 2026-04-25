class Calculator {

    // reused concept: addition
    add(x, y) {
        return x + y;
    }

    // modified logic: safe division with error handling
    divide(a, b) {
        if (b === 0) {
            console.log("Cannot divide by zero");
            return null;
        }
        return a / b;
    }

    // changed implementation: recursive power instead of loop
    power(base, exp) {
        if (exp === 0) return 1;
        return base * this.power(base, exp - 1);
    }

}

// main execution
const calc = new Calculator();

console.log("Power result:", calc.power(2, 3));
console.log("Addition:", calc.add(5, 7));
console.log("Division:", calc.divide(10, 2));