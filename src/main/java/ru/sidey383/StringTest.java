package ru.sidey383;

public class StringTest {

    public static void main(String[] args) {
        String s3 = new String("Hello");
        String s4 = new String("Hello");
        System.out.println(s3 == s4);
        System.out.println(s3.equals(s4));
        equalsBenchmark(s3, s4);
        equalsBenchmark(s3, s4);
        equalsBenchmark(s3, s4);

        long start = System.currentTimeMillis();
        equalsBenchmark(s3, s4);
        System.out.println(System.currentTimeMillis() - start);
        s3 = s3.intern();
        s4 = s4.intern();
        System.out.println(s3 == s4);
        System.out.println(s3.equals(s4));
        equalsBenchmark(s3, s4);
        start = System.currentTimeMillis();
        equalsBenchmark(s3, s4);
        System.out.println(System.currentTimeMillis() - start);
    }

    private static void equalsBenchmark(String s1, String s2) {
        for (int i = 0; i < 100000000; i++) {
            s1.equals(s2);
        }
    }

}
