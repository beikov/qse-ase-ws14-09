package at.ac.tuwien.ase09.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.ejb.EJBException;

import at.ac.tuwien.ase09.exception.AppException;

import com.blazebit.exception.ExceptionUtils;
import com.googlecode.catchexception.internal.ExceptionProcessingInterceptor;

public class EjbAwareThrowableProcessingInterceptor<E extends Exception> extends ExceptionProcessingInterceptor<E> {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Throwable>[] UNWRAP_WITH_CORE = (Class<? extends Throwable>[]) new Class<?>[]{InvocationTargetException.class, EJBException.class, AppException.class};
    @SuppressWarnings("unchecked")
    private static final Class<? extends Throwable>[] UNWRAP_WITHOUT_CORE = (Class<? extends Throwable>[]) new Class<?>[]{InvocationTargetException.class, EJBException.class};
    
    private final Class<? extends Throwable>[] unwraps;
    
    /**
     * @param target
     * @param clazz
     * @param assertThrowable
     */
    public EjbAwareThrowableProcessingInterceptor(Object target, Class<E> clazz) {
        super(target, clazz, true);
        unwraps = AppException.class.isAssignableFrom(clazz) ? UNWRAP_WITHOUT_CORE : UNWRAP_WITH_CORE;
    }
    
    @Override
    protected Object afterInvocationThrowsException(Exception e, Method method)
    		throws Error, Exception {
    	Exception realException = (Exception) ExceptionUtils.unwrap(e, unwraps);
        
        if (realException == null) {
            realException = e;
        }
        
        return super.afterInvocationThrowsException(realException, method);
    }

}
