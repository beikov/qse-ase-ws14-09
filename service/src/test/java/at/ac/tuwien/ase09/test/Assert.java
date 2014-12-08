package at.ac.tuwien.ase09.test;

import static org.junit.Assert.*;
import java.util.List;

import com.googlecode.catchexception.internal.SubclassProxyFactory;

public class Assert {

    // # Collection assertions

    public static void assertUnorderedEquals(List<?> a, List<?> b) {
        assertEquals(a.size(), b.size());
        assertTrue(a.containsAll(b));
    }

    public static void assertOrderedEquals(List<?> a, List<?> b) {
        assertEquals(a.size(), b.size());
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i) == null) {
                assertTrue(b.get(i) == null);
            } else {
                assertTrue(a.get(i).equals(b.get(i)));
            }
        }
    }

    // # Exception assertions

    public static <T, E extends Exception> T verifyException(T obj, Class<E> clazz) {
        if (obj == null) {
            throw new IllegalArgumentException("obj must not be null");
        }

        return new SubclassProxyFactory().<T> createProxy(obj.getClass(), new EjbAwareThrowableProcessingInterceptor<E>(obj, clazz));
    }

}
