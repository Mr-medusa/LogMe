package red.medusa.logme;

import red.medusa.logme.logable.Subject;

import java.util.Arrays;

/**
 * @author Mr.Medusa
 * @date 2022/6/6
 */
public class LogMeTest {
    public static void main(String[] args) throws InterruptedException {
        LogMe  logMe = SubjectFactory.getLogMe(LogMeTest.class);
        Subject fibonacci = logMe.newSubject("Fibonacci");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logMe.i(fibonacci, "one").prepareChildren();
                long fibonacci1 = fibonacci(3,logMe);
                logMe.i(fibonacci, "res is => " + fibonacci1);
            }
        };
        Thread[] threads = new Thread[2];
        for (int i = 0; i < 2; i++) {
            threads[i] = new Thread(runnable);
        }

        Arrays.stream(threads).forEach(Thread::start);
        Thread.sleep(30);

        logMe.print();

    }

    public static long fibonacci(long number, LogMe logMe) {
        if ((number == 0) || (number == 1)) {
            logMe.childI("final number is " + number);
            return number;
        } else {
            logMe.childI("number is " + number + "," + " number - 1 is " + (number - 1) + "," + "number - 2 is " + (number - 2)).prepareChildren();
           return fibonacci(number - 1,logMe) + fibonacci(number - 2,logMe);

        }
    }

}










