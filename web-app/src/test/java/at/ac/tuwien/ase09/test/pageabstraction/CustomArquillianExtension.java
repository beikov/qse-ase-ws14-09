/**
 * 
 */
package at.ac.tuwien.ase09.test.pageabstraction;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.graphene.location.LocationEnricher;
import org.jboss.arquillian.graphene.spi.enricher.SearchContextTestEnricher;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 07.10.2014
 */
public class CustomArquillianExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(SearchContextTestEnricher.class, CustomSearchContextTestEnricher.class);
        builder.service(SearchContextTestEnricher.class, CustomWebElementEnricher.class);
    }
}
