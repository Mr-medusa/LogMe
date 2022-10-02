package red.medusa.logme;

import red.medusa.logme.color.ConsoleStr;
import red.medusa.logme.format.PlainLogFormat;
import red.medusa.logme.format.PrettyLogFormat;
import red.medusa.logme.logable.Subject;
import red.medusa.logme.logable.message.ParamMsg;

/**
 * @author huguanghui
 * @date 2022/6/12
 */
public class TraceTest {
    static LogMe logMe = SubjectFactory.getLogMe(LogMeTest.class);
    static LogMe hello = LogMe.getLogMe("Hello");
    public static void main(String[] args) {
        logMe.setLogFormat(new PrettyLogFormat(System.out));
        Subject fibonacciSubject = logMe.newSubject("Fibonacci");
        logMe.withParamContext(() -> {
            logMe.i(fibonacciSubject, "one").prepareChildren();
            long result = fibonacci(5);
            logMe.i(fibonacciSubject, "res is => " + result);
        });
        logMe.print();

        System.out.println("");

        hello.setLogFormat(new PlainLogFormat(System.out));
        Subject fibonacciSubject2 = hello.newSubject("Fibonacci2");
        hello.i(fibonacciSubject2, "one").prepareChildren();
        long fibonacci2 = fibonacci2(5);
        hello.i(fibonacciSubject2, "res is => " + fibonacci2);
        hello.print();
    }

    public static long fibonacci(long number) {
        if ((number == 0) || (number == 1)) {
            logMe.childI("final number is " + number);
            return number;
        } else {
            logMe.childParameterI(new ParamMsg(number, "number is "+ number,true)
                    .highlight(ConsoleStr::reverse)
                    .append(number - 1, ", number - 1 is " + (number - 1),false)
                    .append(number - 2, ", number - 2 is " + (number - 2),false)
            ).prepareParameterChildren(number);

            return fibonacci(number - 1) + fibonacci(number - 2);
        }
    }


    public static long fibonacci2(long number) {
        if ((number == 0) || (number == 1)) {
            hello.childI("final number is " + number);
            return number;
        } else {
            hello.childI("number is " + number + "," + " number - 1 is " + (number - 1) + "," + "number - 2 is " + (number - 2)).prepareChildren();
            return fibonacci2(number - 1) + fibonacci2(number - 2);
        }
    }
}
