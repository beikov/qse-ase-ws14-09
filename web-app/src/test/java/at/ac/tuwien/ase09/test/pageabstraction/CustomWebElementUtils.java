/**
 * 
 */
package at.ac.tuwien.ase09.test.pageabstraction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.arquillian.graphene.GrapheneElement;
import org.jboss.arquillian.graphene.context.GrapheneContext;
import org.jboss.arquillian.graphene.enricher.SearchContextInterceptor;
import org.jboss.arquillian.graphene.enricher.StaleElementInterceptor;
import org.jboss.arquillian.graphene.enricher.WebElementUtils;
import org.jboss.arquillian.graphene.enricher.WrapsElementInterceptor;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.arquillian.graphene.intercept.InterceptorBuilder;
import org.jboss.arquillian.graphene.proxy.GrapheneContextualHandler;
import org.jboss.arquillian.graphene.proxy.GrapheneProxy;
import org.jboss.arquillian.graphene.proxy.GrapheneProxyInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.ByIdOrName;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 07.10.2014
 */
public final class CustomWebElementUtils {
    private static Logger LOG = Logger.getLogger(CustomWebElementUtils.class.getName());
    private static final String EMPTY_FIND_BY_WARNING = " Be aware of the fact that fields anotated with empty "
            + "@FindBy were located by default strategy, which is ByIdOrName with field name as locator! ";

    private static final Class<?>[] INTERFACES_PROXY_SHOULD_IMPLEMENET = {Locatable.class,
        WrapsElement.class, FindsByClassName.class, FindsByCssSelector.class, FindsById.class, FindsByLinkText.class,
        FindsByName.class, FindsByTagName.class, FindsByXPath.class, GrapheneElement.class };

    private CustomWebElementUtils() {
    }

    public static WebElement findElement(GrapheneContext context, final By by, final GrapheneProxy.FutureTarget searchContextFuture) {
        // Here the web element has to be found to ensure that SearchContext throws
        // NoSuchElementException if there is no element with the given By locator.
        dropProxyAndFindElement(by, (SearchContext) searchContextFuture.getTarget());
        return findElement(context, new GrapheneProxy.FutureTarget() {
            @Override
            public Object getTarget() {
                return dropProxyAndFindElement(by, (SearchContext) searchContextFuture.getTarget());
            }
        });
    }

    public static List<WebElement> findElementsLazily(final GrapheneContext context, final By by, final GrapheneProxy.FutureTarget searchContextFuture) {
        List<WebElement> elements = GrapheneProxy.getProxyForHandler(GrapheneContextualHandler.forFuture(context, new GrapheneProxy.FutureTarget() {
            @Override
            public Object getTarget() {
                List<WebElement> result = new ArrayList<WebElement>();
                return dropProxyAndFindElements(by, (SearchContext) searchContextFuture.getTarget());
            }
        }), List.class);
        GrapheneProxyInstance proxy = (GrapheneProxyInstance) elements;
        proxy.registerInterceptor(new StaleElementInterceptor());
        return elements;
    }

    public static WebElement findElementLazily(GrapheneContext context, final By by, final GrapheneProxy.FutureTarget searchContextFuture, final int indexInList) {
        return findElement(context, new GrapheneProxy.FutureTarget() {
            @Override
            public Object getTarget() {
                return dropProxyAndFindElements(by, (SearchContext) searchContextFuture.getTarget()).get(indexInList);
            }
        });
    }

    public static WebElement findElementLazily(final By by, final SearchContext searchContext, final int indexInList) {
        return findElement(getContext(searchContext), new GrapheneProxy.FutureTarget() {
            @Override
            public Object getTarget() {
                return dropProxyAndFindElements(by, searchContext).get(indexInList);
            }
        });
    }

    public static WebElement findElementLazily(final By by, final SearchContext searchContext) {
        return findElement(getContext(searchContext), new GrapheneProxy.FutureTarget() {
            private WebElement cachedElement;
            @Override
            public Object getTarget() {
                if(cachedElement != null) {
                    // try to provoke a StaleElementReferenceException
                    try{
                        cachedElement.isDisplayed();
                        // element is still valid
                        return cachedElement;
                    }catch(StaleElementReferenceException e){
                        // ignore
                    }
                }
                try {
                    cachedElement = dropProxyAndFindElement(by, searchContext);
                    return cachedElement;
                } catch (NoSuchElementException ex) {
                    throw new NoSuchElementException((by instanceof ByIdOrName ? EMPTY_FIND_BY_WARNING : "") + ex.getMessage(),
                            ex);
                }
            }
        });
    }

    public static List<WebElement> findElementsLazily(final By by, final SearchContext searchContext) {
        return findElementsLazily(getContext(searchContext), by, new GrapheneProxy.FutureTarget() {
            @Override
            public Object getTarget() {
                return searchContext;
            }
        });
    }

    protected static WebElement findElement(GrapheneContext context, final GrapheneProxy.FutureTarget target) {
        final WebElement element = GrapheneProxy.getProxyForFutureTarget(context, target, WebElement.class, INTERFACES_PROXY_SHOULD_IMPLEMENET);
        final GrapheneProxyInstance elementProxy = (GrapheneProxyInstance) element;

        InterceptorBuilder b = new InterceptorBuilder();
        b.interceptInvocation(WrapsElement.class, new WrapsElementInterceptor(elementProxy)).getWrappedElement();

        elementProxy.registerInterceptor(b.build());
        elementProxy.registerInterceptor(new StaleElementInterceptor());
        elementProxy.registerInterceptor(new CustomSearchContextInterceptor());
        return element;
    }

    protected static WebElement dropProxyAndFindElement(By by, SearchContext searchContext) {
        if (searchContext instanceof GrapheneProxyInstance) {
            LOG.finest("FindElement " + by.toString());
            SearchContext unwrapped = (SearchContext) ((GrapheneProxyInstance) searchContext).unwrap();
            return unwrapped.findElement(by);
        } else {
            LOG.finest("FindElement " + by.toString());
            return searchContext.findElement(by);
        }
    }

    protected static List<WebElement> dropProxyAndFindElements(By by, SearchContext searchContext) {
        if (searchContext instanceof GrapheneProxyInstance) {
            if (by instanceof ByJQuery) {
                return by.findElements(searchContext);
            } else {
                List<WebElement> webElems = ((SearchContext) ((GrapheneProxyInstance) searchContext).unwrap()).findElements(by);
                return webElems;
            }
        } else {
            return searchContext.findElements(by);
        }
    }

    protected static GrapheneContext getContext(Object object) {
        if (!GrapheneProxy.isProxyInstance(object)) {
            throw new IllegalArgumentException("The parameter [object] has to be instance of " + GrapheneProxyInstance.class.getName() + ", but it is not. The given object is " + object + ".");
        }
        return ((GrapheneProxyInstance) object).getContext();
    }

}