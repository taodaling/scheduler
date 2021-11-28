package utils;

public class ErrorUtils {
    public static void throwAsRuntimeException(Object e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else if (e instanceof Error) {
            throw (Error) e;
        } else {
            throw new RuntimeException((Exception) e);
        }
    }
}
