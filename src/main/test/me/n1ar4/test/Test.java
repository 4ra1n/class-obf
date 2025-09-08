package me.n1ar4.test;

public class Test {
    private String message = "Hello World";
    private int counter = 0;
    private boolean flag = true;

    public void testMethodCalls() {
        System.out.println("Testing method calls");
        String result = processString(message);
        System.out.println(result);
        incrementCounter();
        System.out.println("Counter: " + counter);
    }

    public void testConditionalBranches() {
        if (flag) {
            System.out.println("Flag is true");
            if (counter > 0) {
                System.out.println("Counter is positive");
            } else {
                System.out.println("Counter is zero or negative");
            }
        } else {
            System.out.println("Flag is false");
        }
        switch (counter % 3) {
            case 0:
                System.out.println("Divisible by 3");
                break;
            case 1:
                System.out.println("Remainder 1");
                break;
            case 2:
                System.out.println("Remainder 2");
                break;
        }
    }

    public void testLoops() {
        for (int i = 0; i < 5; i++) {
            System.out.println("For loop iteration: " + i);
            if (i == 2) {
                continue;
            }
            processNumber(i);
        }
        int j = 0;
        while (j < 3) {
            System.out.println("While loop iteration: " + j);
            j++;
        }
        int k = 0;
        do {
            System.out.println("Do-while loop iteration: " + k);
            k++;
        } while (k < 2);
    }

    public void testExceptionHandling() {
        try {
            int result = 10 / counter;
            System.out.println("Division result: " + result);
        } catch (ArithmeticException e) {
            System.out.println("Division by zero error");
        } finally {
            System.out.println("Finally block executed");
        }
    }

    public void testArrayOperations() {
        int[] numbers = {1, 2, 3, 4, 5};
        for (int num : numbers) {
            System.out.println("Array element: " + num);
        }
        String[] strings = new String[3];
        strings[0] = "First";
        strings[1] = "Second";
        strings[2] = "Third";
        for (String str : strings) {
            System.out.println("String array element: " + str);
        }
    }

    private String processString(String input) {
        if (input == null) {
            return "null";
        }
        return input.toUpperCase() + "_PROCESSED";
    }

    private void incrementCounter() {
        counter++;
        if (counter > 10) {
            counter = 0;
            flag = !flag;
        }
    }

    private void processNumber(int num) {
        if (num % 2 == 0) {
            System.out.println(num + " is even");
        } else {
            System.out.println(num + " is odd");
        }
    }

    public int fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static void staticMethod() {
        System.out.println("This is a static method");
        Math.random();
        System.currentTimeMillis();
    }

    public static void main(String[] args) {
        System.out.println("=== 开始测试控制流混淆和花指令 ===");

        Test test = new Test();

        System.out.println("\n1. 测试方法调用和字段访问:");
        test.testMethodCalls();

        System.out.println("\n2. 测试条件分支:");
        test.testConditionalBranches();

        System.out.println("\n3. 测试循环结构:");
        test.testLoops();

        System.out.println("\n4. 测试异常处理:");
        test.testExceptionHandling();

        System.out.println("\n5. 测试数组操作:");
        test.testArrayOperations();

        System.out.println("\n6. 测试递归:");
        int fibResult = test.fibonacci(5);
        System.out.println("Fibonacci(5) = " + fibResult);

        System.out.println("\n7. 测试静态方法:");
        staticMethod();

        System.out.println("\n=== 测试完成 ===");
    }
}
