/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.ase09.test.pageabstraction;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.context.GrapheneContext;
import org.jboss.arquillian.graphene.enricher.AbstractSearchContextEnricher;
import org.jboss.arquillian.graphene.enricher.ReflectionHelper;
import org.jboss.arquillian.graphene.proxy.GrapheneProxyInstance;
import org.jboss.arquillian.graphene.spi.configuration.GrapheneConfiguration;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Moritz Becker
 */
public class CustomWebElementEnricher extends AbstractSearchContextEnricher {
    private static Logger LOG = Logger.getLogger(CustomWebElementEnricher.class.getName());
    @Inject
    private Instance<GrapheneConfiguration> configuration;

    public CustomWebElementEnricher() {
    }

    // because of testing
    public CustomWebElementEnricher(Instance<GrapheneConfiguration> configuration) {
        this.configuration = configuration;
    }

    @Override
    public void enrich(final SearchContext searchContext, Object target) {
        try {
            List<Field> fields = ReflectionHelper.getFieldsWithAnnotation(target.getClass(), FindBy.class);
            for (Field field : fields) {
                GrapheneContext grapheneContext = searchContext == null ? null : ((GrapheneProxyInstance) searchContext).getContext();
                final SearchContext localSearchContext;
                if (grapheneContext == null) {
                    grapheneContext = GrapheneContext.getContextFor(ReflectionHelper.getQualifier(field.getAnnotations()));
                    localSearchContext = grapheneContext.getWebDriver(SearchContext.class);
                } else {
                    localSearchContext = searchContext;
                }
                //by should never by null, by default it is ByIdOrName using field name
                CustomAnnotations annotations;
                if (searchContext instanceof WebElement) {
                    annotations = new CustomAnnotations(field, How.valueOf(configuration.get().getDefaultElementLocatingStrategy().name()), (WebElement) searchContext);
                } else {
                    annotations = new CustomAnnotations(field, How.valueOf(configuration.get().getDefaultElementLocatingStrategy().name()));
                }
                By rootBy = annotations.buildBy();
                
                // WebElement
                if (field.getType().isAssignableFrom(WebElement.class)) {
                    LOG.info("Create web element " + field.getDeclaringClass().toString() + "." + field.getName());
                    WebElement element = CustomWebElementUtils.findElementLazily(rootBy, localSearchContext);
                    setValue(field, target, element);
                    // List<WebElement>
                } else if (field.getType().isAssignableFrom(List.class)
                        && getListType(field).isAssignableFrom(WebElement.class)) {
                    LOG.info("Create web element list " + field.getDeclaringClass().toString() + "." + field.getName());
                    List<WebElement> elements = CustomWebElementUtils.findElementsLazily(rootBy, localSearchContext);
                    setValue(field, target, elements);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public int getPrecedence() {
        return 1;
    }
}
