/**
 * 
 */
package at.ac.tuwien.ase09.test.pageobject;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.WebElement;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 23.09.2014
 */
public abstract class AbstractTab extends AbstractPageElement {

    public boolean isExpanded(WebElement tabLink) {
        String expanded = tabLink.getAttribute("expanded");
        return expanded != null && "true".equals(expanded);
    }
}
