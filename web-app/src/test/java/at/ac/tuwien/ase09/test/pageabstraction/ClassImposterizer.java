/**
 * 
 */
package at.ac.tuwien.ase09.test.pageabstraction;

import org.jboss.arquillian.graphene.shaded.net.sf.cglib.proxy.MethodInterceptor;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 07.10.2014
 */
class ClassImposterizer extends org.jboss.arquillian.graphene.cglib.ClassImposterizer {

    static final ClassImposterizer INSTANCE = new ClassImposterizer();

    <T> T imposterise(MethodInterceptor interceptor, Class<T> mockedType, Class<?>... ancillaryTypes) {
        return INSTANCE.imposteriseProtected(interceptor, mockedType, ancillaryTypes);
    }
}
