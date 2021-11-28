package utils;

public class ReflectUtils {
    public static <T> T newInstance(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            ErrorUtils.throwAsRuntimeException(e);
            return null;
        }
    }

    public static Object newInstance(String cls) {
        try {
            return newInstance(Class.forName(cls));
        } catch (ClassNotFoundException e) {
            ErrorUtils.throwAsRuntimeException(e);
            return null;
        }
    }
}
