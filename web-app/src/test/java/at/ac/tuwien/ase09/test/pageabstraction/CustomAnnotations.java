/**
 *
 */
package at.ac.tuwien.ase09.test.pageabstraction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.arquillian.graphene.proxy.GrapheneProxyInstance;
import org.jboss.arquillian.graphene.spi.findby.ImplementsLocationStrategy;
import org.jboss.arquillian.graphene.spi.findby.LocationStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.pagefactory.ByChained;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 03.10.2014
 */
public class CustomAnnotations {

    private final Field field;
    private final How defaultElementLocatingStrategy;
    private final WebElement rootElement;

    public CustomAnnotations(Field field, How defaultElementLocatingStrategy) {
        this(field, defaultElementLocatingStrategy, null);
    }

    public CustomAnnotations(Field field, How defaultElementLocatingStrategy, WebElement rootElement) {
        this.field = field;
        this.defaultElementLocatingStrategy = defaultElementLocatingStrategy;
        this.rootElement = rootElement;
    }

    public boolean isLookupCached() {
        return (field.getAnnotation(CacheLookup.class) != null);
    }

    public By buildBy() {
        assertValidAnnotations();

        By by = null;

        by = checkAndProcessEmptyFindBy();

        FindBys webDriverFindBys = field.getAnnotation(FindBys.class);
        if (by == null && webDriverFindBys != null) {
            by = buildByFromFindBys(webDriverFindBys);
        }

        FindBy findBy = field.getAnnotation(FindBy.class);
        if (by == null && findBy != null) {
            NamingContainer namingContainerAnnotation = field.getAnnotation(NamingContainer.class);
            String namingContainer = null;
            if (namingContainerAnnotation != null) {
                namingContainer = namingContainerAnnotation.value();
            }

            by = buildByFromFindBy(findBy, namingContainer);
        }

        for (Annotation annotation : field.getAnnotations()) {
            ImplementsLocationStrategy strategy = annotation.annotationType().getAnnotation(ImplementsLocationStrategy.class);
            if (strategy != null) {
                by = buildByFromLocationStrategy(strategy, annotation);
            }
        }

        if (by == null) {
            by = buildByFromDefault();
        }

        if (by == null) {
            throw new IllegalArgumentException("Cannot determine how to locate element " + field);
        }

        return by;
    }

    private By checkAndProcessEmptyFindBy() {
        By result = null;

        FindBy findBy = field.getAnnotation(FindBy.class);
        if (findBy != null) {
            int numberOfValues = assertValidFindBy(findBy);
            if (numberOfValues == 0) {
                result = buildByFromDefault();
            }
        }

        return result;
    }

    protected By buildByFromDefault() {
        String using = field.getName();
        return getByFromHow(defaultElementLocatingStrategy, using, null);
    }

    protected By buildByFromLocationStrategy(ImplementsLocationStrategy strategy, Annotation annotation) {
        try {
            LocationStrategy transformer = strategy.value().newInstance();
            return transformer.fromAnnotation(annotation);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot use locationStrategy " + strategy + " on annotation " + annotation + " on field " + field + ": " + e.getMessage(), e);
        }
    }

    protected By buildByFromFindBys(FindBys webDriverFindBys) {
        assertValidFindBys(webDriverFindBys);

        FindBy[] findByArray = webDriverFindBys.value();
        By[] byArray = new By[findByArray.length];
        for (int i = 0; i < findByArray.length; i++) {
            byArray[i] = buildByFromFindBy(findByArray[i], "");
        }

        return new ByChained(byArray);
    }

    protected By buildByFromFindBy(FindBy findBy, String namingContainer) {
        assertValidFindBy(findBy);

        By ans = buildByFromShortFindBy(findBy, namingContainer);
        if (ans == null) {
            ans = buildByFromLongFindBy(findBy, namingContainer);
        }

        return ans;
    }

    protected By buildByFromLongFindBy(FindBy findBy, String namingContainer) {
        How how = findBy.how();
        String using = findBy.using();

        switch (how) {
            case CLASS_NAME:
                return By.className(using);

            case CSS:
                return cssSelector(rootElement, using);

            case ID:
                return id(using, namingContainer);

            case RELATIVE_ID:
                if (using.isEmpty()) {
                    throw new IllegalStateException("Empty id not supported in FindBy relative id");
                }
                return relativeId(rootElement, using, namingContainer);

            case ID_OR_NAME:
                return new ByIdOrName(using);

            case LINK_TEXT:
                return By.linkText(using);

            case NAME:
                return By.name(using);

            case PARTIAL_LINK_TEXT:
                return By.partialLinkText(using);

            case TAG_NAME:
                return By.tagName(using);

            case XPATH:
                return xpath(rootElement, using);

            default:
                // Note that this shouldn't happen (eg, the above matches all
                // possible values for the How enum)
                throw new IllegalArgumentException("Cannot determine how to locate element " + field);
        }
    }

    private By getByFromHow(How how, String using, String namingContainer) {
        switch (how) {
            case CLASS_NAME:
                return By.className(using);

            case CSS:
                return cssSelector(rootElement, using);

            case ID:
                return id(using, namingContainer);

            case RELATIVE_ID:
                if (using.isEmpty()) {
                    throw new IllegalStateException("Empty id not supported in FindBy relative id");
                }
                return relativeId(rootElement, using, namingContainer);

            case ID_OR_NAME:
                return new ByIdOrName(using);

            case LINK_TEXT:
                return By.linkText(using);

            case NAME:
                return By.name(using);

            case PARTIAL_LINK_TEXT:
                return By.partialLinkText(using);

            case TAG_NAME:
                return By.tagName(using);

            case XPATH:
                return xpath(rootElement, using);

            default:
                // Note that this shouldn't happen (eg, the above matches all
                // possible values for the How enum)
                throw new IllegalArgumentException("Cannot determine how to locate element " + field);
        }
    }

    protected By buildByFromShortFindBy(FindBy findBy, String namingContainer) {

        if (!"".equals(findBy.className())) {
            return By.className(findBy.className());
        }

        if (!"".equals(findBy.css())) {
            return cssSelector(rootElement, findBy.css());
        }

        if (!"".equals(findBy.id())) {
            return id(findBy.id(), namingContainer);
        }

        if (!"".equals(findBy.relativeId())) {
            if (findBy.relativeId().isEmpty()) {
                throw new IllegalStateException("Empty id not supported in FindBy relative id");
            }
            return relativeId(rootElement, findBy.relativeId(), namingContainer);
        }

        if (!"".equals(findBy.linkText())) {
            return By.linkText(findBy.linkText());
        }

        if (!"".equals(findBy.name())) {
            return By.name(findBy.name());
        }

        if (!"".equals(findBy.partialLinkText())) {
            return By.partialLinkText(findBy.partialLinkText());
        }

        if (!"".equals(findBy.tagName())) {
            return By.tagName(findBy.tagName());
        }

        if (!"".equals(findBy.xpath())) {
            return xpath(rootElement, findBy.xpath());
        }

        // Fall through
        return null;
    }

    private void assertValidAnnotations() {
        FindBys findBys = field.getAnnotation(FindBys.class);

        FindBy findBy = field.getAnnotation(FindBy.class);

        if (findBys != null && findBy != null) {
            throw new IllegalArgumentException("If you use a '@FindBys' annotation, " + "you must not also use a '@FindBy' annotation");
        }
    }

    private void assertValidFindBys(FindBys webDriverFindBys) {
        for (FindBy webDriverFindBy : webDriverFindBys.value()) {
            assertValidFindBy(webDriverFindBy);
        }
    }

    private int assertValidFindBy(FindBy findBy) {
        if (findBy.how() != null) {
            if (findBy.using() == null) {
                throw new IllegalArgumentException("If you set the 'how' property, you must also set 'using'");
            }
        }

        Set<String> finders = new HashSet<String>();
        if (!"".equals(findBy.using())) {
            finders.add("how: " + findBy.using());
        }
        if (!"".equals(findBy.className())) {
            finders.add("class name:" + findBy.className());
        }
        if (!"".equals(findBy.css())) {
            finders.add("css:" + findBy.css());
        }
        if (!"".equals(findBy.id())) {
            finders.add("id: " + findBy.id());
        }
        if (!"".equals(findBy.relativeId())) {
            finders.add("relativeId: " + findBy.relativeId());
        }
        if (!"".equals(findBy.linkText())) {
            finders.add("link text: " + findBy.linkText());
        }
        if (!"".equals(findBy.name())) {
            finders.add("name: " + findBy.name());
        }
        if (!"".equals(findBy.partialLinkText())) {
            finders.add("partial link text: " + findBy.partialLinkText());
        }
        if (!"".equals(findBy.tagName())) {
            finders.add("tag name: " + findBy.tagName());
        }
        if (!"".equals(findBy.xpath())) {
            finders.add("xpath: " + findBy.xpath());
        }

        // A zero count is okay: it means to look by name or id.
        if (finders.size() > 1) {
            throw new IllegalArgumentException(String.format("You must specify at most one location strategy. Number found: %d (%s)", finders.size(), finders.toString()));
        }

        return finders.size();
    }

    public static abstract class NamingContainerAwareBy extends By {
        protected final WebElement rootElem;

        public NamingContainerAwareBy(WebElement rootElem) {
            this.rootElem = rootElem;
        }

        protected abstract NamingContainerAwareWebElement wrap(WebElement elem, String rootNamingContainer);

        protected List<WebElement> wrap(List<WebElement> elems, String rootNamingContainer) {
            List<WebElement> result = new ArrayList<WebElement>();
            for (WebElement webElem : elems) {
                result.add(wrap(webElem, rootNamingContainer));
            }
            return result;
        }

        protected String getRootNamingContainer() {
            if (rootElem instanceof GrapheneProxyInstance) {
                WebElement webElem = ((GrapheneProxyInstance) rootElem).unwrap();
                if (webElem instanceof NamingContainerAwareWebElement) {
                    return ((NamingContainerAwareWebElement) webElem).getNamingContainer();
                }
            }
            return null;
        }

    }

    public static class ById extends NamingContainerAwareBy {
        private final String id;
        private final String namingContainer;

        public ById(String id, String namingContainer) {
            super(null);
            this.namingContainer = namingContainer;
            this.id = id;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            return wrap(new ById(id).findElements(context), getRootNamingContainer());
        }

        protected NamingContainerAwareWebElement wrap(WebElement elem, String rootNamingContainer) {
            if (namingContainer == null) {
                return new NamingContainerAwareWebElement(elem, null);
            } else if (namingContainer.isEmpty()) {
                return new NamingContainerAwareWebElement(elem, id);
            } else {
                return new NamingContainerAwareWebElement(elem, namingContainer);
            }
        }

        @Override
        public String toString() {
            return "ById: " + id;
        }
    }

    public static class ByRelativeId extends NamingContainerAwareBy {

        private final String relativeId;
        private final String namingContainer;

        public ByRelativeId(WebElement rootElem, String relativeId, String namingContainer) {
            super(rootElem);
            this.relativeId = relativeId;
            this.namingContainer = namingContainer;
        }

        protected NamingContainerAwareWebElement wrap(WebElement elem, String rootNamingContainer) {
            if (rootNamingContainer == null || rootNamingContainer.isEmpty()) {
                if (namingContainer == null || namingContainer.isEmpty()) {
                    throw new IllegalStateException("FindBy relative id used without root naming container");
                } else {
                    return new NamingContainerAwareWebElement(elem, namingContainer);
                }
            } else {
                if (namingContainer == null) {
                    return new NamingContainerAwareWebElement(elem, rootNamingContainer);
                } else if (namingContainer.isEmpty()) {
                    return new NamingContainerAwareWebElement(elem, rootNamingContainer + ":" + relativeId);
                } else {
                    return new NamingContainerAwareWebElement(elem, rootNamingContainer + ":" + namingContainer);
                }
            }
        }

        private String getAbsoluteId(String rootNamingContainer) {
            if(rootNamingContainer == null) {
                rootNamingContainer = "";
            }
            String[] idPathParts = relativeId.split(":");
            int numUpSteps = 0;
            int i;
            for (i = 0; i < idPathParts.length; i++) {
                if ("..".equals(idPathParts[i])) {
                    numUpSteps++;
                } else {
                    break;
                }
            }

            String[] namingContainerParts = rootNamingContainer.split(":");
            if (numUpSteps > namingContainerParts.length) {
                throw new IllegalStateException("Relative id [" + relativeId + "] not applicable to root naming container [" + rootNamingContainer + "]");
            }
            StringBuilder sb = new StringBuilder();
            if (numUpSteps < namingContainerParts.length) {
                sb.append(namingContainerParts[0]);
                for (int j = 1; j < namingContainerParts.length - numUpSteps; j++) {
                    sb.append(':').append(namingContainerParts[j]);
                }
            }
        
            if (i < idPathParts.length) {
                if(!(numUpSteps < namingContainerParts.length)){
                    sb.append(idPathParts[i]);
                    i++;
                }
                for (; i < idPathParts.length; i++) {
                    if ("..".equals(idPathParts[i])) {
                        throw new IllegalArgumentException("Invalid relative id [" + relativeId + "]");
                    } else {
                        sb.append(':').append(idPathParts[i]);
                    }
                }
            }

            return sb.toString();
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            String rootNamingContainer = getRootNamingContainer();
            if (context instanceof FindsById) {
                return wrap(((FindsById) context).findElementsById(getAbsoluteId(rootNamingContainer)), rootNamingContainer);
            }
            return wrap(((FindsByXPath) context).findElementsByXPath(".//*[@id = '" + getAbsoluteId(rootNamingContainer) + "']"), rootNamingContainer);
        }

        @Override
        public String toString() {
            return "ByRelativeId: absolute=[" + getAbsoluteId(getRootNamingContainer()) + "], relative=[" + relativeId + "], namingContainer=[" + getRootNamingContainer() + "]";
        }
    }

    public static class ByXPath extends NamingContainerAwareBy {

        private final Pattern paramPattern = Pattern.compile("\\{([^{}]+)\\}");
        private final String xpathExpression;

        public ByXPath(WebElement rootElem, String xpathExpression) {
            super(rootElem);
            this.xpathExpression = xpathExpression;
        }

        private String parameterize(String s, String rootId) {
            Matcher matcher = paramPattern.matcher(s);
            StringBuilder builder = new StringBuilder();
            int i = 0;
            while (matcher.find()) {
                if (!"rootId".equals(matcher.group(1))) {
                    throw new IllegalStateException("Unknown variable [" + matcher.group(1) + "]");
                }
                builder.append(s.substring(i, matcher.start()));
                if (rootId == null) {
                    builder.append(matcher.group(0));
                } else {
                    builder.append(rootId);
                }
                i = matcher.end();
            }
            builder.append(s.substring(i, s.length()));
            return builder.toString();
        }

        protected NamingContainerAwareWebElement wrap(WebElement elem, String rootNamingContainer) {
            return new NamingContainerAwareWebElement(elem, rootNamingContainer);
        }

        private String getResolvedXpathExpression() {
            String effectiveNamingContainer = null;
            String rootNamingContainer = getRootNamingContainer();
            if (rootNamingContainer == null || rootNamingContainer.isEmpty()) {
                effectiveNamingContainer = "";
            } else {
                effectiveNamingContainer = rootNamingContainer;
            }
            return parameterize(xpathExpression, effectiveNamingContainer);
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            return wrap(((FindsByXPath) context).findElementsByXPath(getResolvedXpathExpression()), getRootNamingContainer());
        }

        @Override
        public String toString() {
            return "ByXPath: " + getResolvedXpathExpression();
        }
    }

    public static class ByCssSelector extends NamingContainerAwareBy {
        private final Pattern paramPattern = Pattern.compile("\\{([^{}]+)\\}");
        private final Pattern idPattern = Pattern.compile("#([^\\s]+)");
        private final String cssSelector;

        public ByCssSelector(WebElement rootElem, String cssSelector) {
            super(rootElem);
            this.cssSelector = cssSelector;
        }

        private String parameterize(String s, String rootId) {
            Matcher matcher = paramPattern.matcher(s);
            StringBuilder builder = new StringBuilder();
            int i = 0;
            while (matcher.find()) {
                if (!"rootId".equals(matcher.group(1))) {
                    throw new IllegalStateException("Unknown variable [" + matcher.group(1) + "]");
                }
                builder.append(s.substring(i, matcher.start()));
                if (rootId == null) {
                    builder.append(matcher.group(0));
                } else {
                    builder.append(rootId);
                }
                i = matcher.end();
            }
            builder.append(s.substring(i, s.length()));
            return builder.toString();
        }

        private String escape(String cssSelector) {
            Matcher matcher = idPattern.matcher(cssSelector);
            StringBuilder escapedCssSelector = new StringBuilder();
            int i = 0;
            while (matcher.find()) {
                String group = matcher.group(1);
                if (group == null) {
                    escapedCssSelector.append(matcher.group());
                } else {
                    escapedCssSelector.append(cssSelector.substring(i, matcher.start(1)));
                    escapedCssSelector.append(group.replaceAll(":", "\\\\:"));
                    escapedCssSelector.append(cssSelector.substring(matcher.end(1), matcher.end(0)));
                }

                i = matcher.end();
            }
            escapedCssSelector.append(cssSelector.substring(i, cssSelector.length()));
            return escapedCssSelector.toString();
        }

        protected NamingContainerAwareWebElement wrap(WebElement elem, String rootNamingContainer) {
            return new NamingContainerAwareWebElement(elem, rootNamingContainer);
        }

        private String getResolvedCssSelector() {
            String effectiveNamingContainer = null;
            String rootNamingContainer = getRootNamingContainer();
            if (rootNamingContainer == null || rootNamingContainer.isEmpty()) {
                effectiveNamingContainer = "";
            } else {
                effectiveNamingContainer = rootNamingContainer;
            }
            // parameterize and escape colons
            String resolvedCssSelector = parameterize(cssSelector, effectiveNamingContainer);
            return escape(resolvedCssSelector);
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            return wrap(((FindsByCssSelector) context).findElementsByCssSelector(getResolvedCssSelector()), getRootNamingContainer());
        }

        @Override
        public String toString() {
            return "ByCss: " + getResolvedCssSelector();
        }
    }

    public static By cssSelector(final WebElement rootElem, final String xpathExpression) {
        if (xpathExpression == null) {
            throw new IllegalArgumentException("Cannot find elements when the XPath expression is null.");
        }

        return new ByCssSelector(rootElem, xpathExpression);
    }

    public static By xpath(final WebElement rootElem, final String xpathExpression) {
        if (xpathExpression == null) {
            throw new IllegalArgumentException("Cannot find elements when the XPath expression is null.");
        }

        return new ByXPath(rootElem, xpathExpression);
    }

    public static By relativeId(final WebElement rootElem, final String id, final String namingContainer) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot find elements with a null id attribute.");
        }

        return new ByRelativeId(rootElem, id, namingContainer);
    }

    public static By id(final String id, final String namingContainer) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot find elements with a null id attribute.");
        }

        return new ById(id, namingContainer);
    }
}
