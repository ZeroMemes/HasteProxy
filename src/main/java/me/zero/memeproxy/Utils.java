package me.zero.memeproxy;

/**
 * @author Brady
 * @since 8/14/2018
 */
public final class Utils {

    private Utils() {}

    public static void sleep() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
