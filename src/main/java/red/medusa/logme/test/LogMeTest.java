package red.medusa.logme.test;

import org.junit.Test;
import red.medusa.logme.LogMe;
import red.medusa.logme.SubjectFactory;
import red.medusa.logme.format.PlainLogFormat;
import red.medusa.logme.logable.Subject;

import java.util.Arrays;

/**
 * @author Mr.Medusa
 * @date 2022/6/6
 */
public class LogMeTest {
    static LogMe logMe = SubjectFactory.getLogMe(LogMeTest.class,new PlainLogFormat(System.out));
    static LogMe logMe2 = SubjectFactory.getLogMe(LogMeTest.class);

    @Test
    public void baseTest(){
        logMe.createCurrentSubject("Hello");
        logMe.i("1");
        logMe.i("2");
        logMe.i("3");
        logMe.i("4");
        for (int i = 0; i < 4; i++) {
            logMe.i3("Hello","4_"+i);
        }
        logMe.i("5");
        for (int i = 0; i < 4; i++) {
            logMe.i3("Hello2","5_"+i);
        }
        logMe.i("6").prepareChildren();
        logMe.i("7");
        logMe.i("8").back();
        logMe.i("9");
        logMe.child().i("10");
        logMe.print();
    }

    @Test
    public void prepareChildrenTest() throws InterruptedException {
        Subject fibonacci1 = logMe.newSubject("Fibonacci");
        Subject fibonacci2 = logMe.newSubject("Fibonacci2").mount(fibonacci1);
        Subject fibonacci3 = logMe.newSubject("Fibonacci3");
        logMe.i(fibonacci1, "1");
        logMe.i(fibonacci1, "2");

        logMe.i(fibonacci2, "3");
        logMe.i(fibonacci2, "4");

        logMe.i(fibonacci3, "5");
        logMe.i(fibonacci3, "6");

        Subject fibonacci = logMe2.newSubject("Fibonacci");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logMe2.i(fibonacci, "one").prepareChildren();
                long fibonacci1 = fibonacci(3);
                logMe2.i(fibonacci, "res is => " + fibonacci1);
            }
        };
        Thread[] threads = new Thread[2];
        for (int i = 0; i < 2; i++) {
            threads[i] = new Thread(runnable);
        }

        Arrays.stream(threads).forEach(Thread::start);
        Thread.sleep(30);

        logMe.setChild(logMe2);
        logMe.print();
        logMe2.print();
    }

    public static long fibonacci(long number) {
        hellot();
        if ((number == 0) || (number == 1)) {
            logMe2.childI("final number is " + number);
            return number;
        } else {
            logMe2.childI("number is " + number + "," + " number - 1 is " + (number - 1) + "," + "number - 2 is " + (number - 2)).prepareChildren();
            return fibonacci(number - 1) + fibonacci(number - 2);
        }
    }

    public static void hellot() {
        logMe2.i(logMe2.getLogLine().getSubject(), "Hellot");
    }

}










