/**
 * 
 */
package at.ac.tuwien.ase09.test.pageabstraction;

import java.lang.reflect.Method;

import org.jboss.arquillian.graphene.proxy.GrapheneProxy;
import org.jboss.arquillian.graphene.proxy.Interceptor;
import org.jboss.arquillian.graphene.proxy.InvocationContext;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import at.ac.tuwien.ase09.test.pageobject.PageElement;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 27.10.2014
 */
public class CustomSearchContextInterceptor implements Interceptor {

    @Override
    public Object intercept(final InvocationContext context) throws Throwable {
        GrapheneProxy.FutureTarget future = new GrapheneProxy.FutureTarget() {
            @Override
            public Object getTarget() {
                return context.getProxy();
            }
        };
        if (methodsEqual(context.getMethod(), SearchContext.class.getDeclaredMethod("findElement", By.class))) {
            return CustomWebElementUtils.findElement(context.getGrapheneContext(), (By) context.getArguments()[0], future);
        } else if (methodsEqual(context.getMethod(), SearchContext.class.getDeclaredMethod("findElements", By.class))) {
            return CustomWebElementUtils.findElementsLazily(context.getGrapheneContext(), (By) context.getArguments()[0], future);
        } else if (methodsEqual(context.getMethod(), PageElement.class.getDeclaredMethod("isDisplayed"))) {
            try {
                return context.invoke();
            } catch (NoSuchElementException e) {
                return false;
            }
        } else {
            return context.invoke();
        }
    }

    protected static boolean methodsEqual(Method first, Method second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (!first.getName().equals(second.getName())) {
            return false;
        }
        if (first.getParameterTypes().length != second.getParameterTypes().length) {
            return false;
        }
        for (int i = 0; i < first.getParameterTypes().length; i++) {
            if (!first.getParameterTypes()[i].equals(second.getParameterTypes()[i])) {
                return false;
            }
        }
        return true;
    }

}
