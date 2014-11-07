package at.ac.tuwien.ase09.data;


import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Dependent
@Named("SimpleItemProcessor")
public class SimpleItemProcessor implements ItemProcessor {

	@Override
	public Object processItem(Object item) throws Exception {
		return item;
	}

}
