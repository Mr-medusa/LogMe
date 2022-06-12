package red.medusa.logme;

import red.medusa.logme.logable.Subject;

/**
 * @author huguanghui
 * @date 2022/6/12
 */
public class TraceTest {
    static LogMe logMe2 = SubjectFactory.getLogMe(LogMeTest.class);

    public static void main(String[] args) throws InterruptedException {
        Subject fibonacci = logMe2.newSubject("Fibonacci");
        logMe2.i(fibonacci, "one").prepareChildren();
        long fibonacci1 = fibonacci(5);
        logMe2.i(fibonacci, "res is => " + fibonacci1);
        logMe2.print();

        System.out.println("");
        LogMe hello = LogMe.getLogMe("Hello");
        fibonacci = hello.newSubject("Fibonacci2");
        hello.i(fibonacci, "one").prepareChildren();
        long fibonacci2 = fibonacci2(5);
        hello.i(fibonacci, "res is => " + fibonacci2);
        hello.print();
    }

    public static long fibonacci(long number) {
        if ((number == 0) || (number == 1)) {
            logMe2.childI("final number is " + number);
            return number;
        } else {
            logMe2.childParameterI(String.valueOf(number),"number is " + number + "," + " number - 1 is " + (number - 1) + "," + "number - 2 is " + (number - 2))
                    .prepareParameterChildren(String.valueOf(number));

            return fibonacci(number - 1) + fibonacci(number - 2);
        }
    }


    public static long fibonacci2(long number) {
        if ((number == 0) || (number == 1)) {
            logMe2.childI("final number is " + number);
            return number;
        } else {
            logMe2.childI("number is " + number + "," + " number - 1 is " + (number - 1) + "," + "number - 2 is " + (number - 2)).prepareChildren();
            return fibonacci2(number - 1) + fibonacci2(number - 2);
        }
    }
}
