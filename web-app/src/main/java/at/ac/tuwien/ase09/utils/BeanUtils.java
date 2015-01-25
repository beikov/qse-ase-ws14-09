package at.ac.tuwien.ase09.utils;

import java.util.Set;

import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.api.provider.BeanManagerProvider;

public final class BeanUtils {

	public static final void removeInstance(String name) {
		BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();
		Set<Bean<?>> beans = beanManager.getBeans(name);
		Bean<?> bean = beanManager.resolve(beans);
		((AlterableContext) beanManager.getContext(bean.getScope())).destroy(bean);
	}
}
