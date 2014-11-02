package at.ac.tuwien.ase09.naming;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.internal.util.StringHelper;

public class CustomNamingStrategy extends DefaultNamingStrategy {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String TABLE_PREFIX = "t_";
	private static final String COLUMN_PREFIX = "c_";
	
	private String prefixTable(String tableName){
		return TABLE_PREFIX + tableName;
	}
	
	private String prefixColumn(String columnName){
		return COLUMN_PREFIX + columnName;
	}
	
	@Override
	public String columnName(String arg0) {
		return COLUMN_PREFIX + arg0;
	}

	@Override
	public String tableName(String arg0) {
		return prefixTable(arg0);
	}
	
	@Override
	public String classToTableName(String className) {
		return prefixTable(super.classToTableName(className));
	}
	
	@Override
	public String propertyToColumnName(String propertyName) {
		return prefixColumn(super.propertyToColumnName(propertyName));
	}
	
	@Override
	public String joinKeyColumnName(String joinedColumn, String joinedTable) {
		return prefixColumn(super.joinKeyColumnName(joinedColumn, joinedTable));
	}
	
	@Override
	public String collectionTableName(
			String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable,
			String propertyName
	) {
		return prefixTable(super.collectionTableName(ownerEntity, ownerEntityTable, associatedEntity, associatedEntityTable, propertyName));
	}
	
	@Override
	public String logicalColumnName(String columnName, String propertyName) {
		return prefixColumn(super.logicalColumnName(columnName, propertyName));
	}

	@Override
	public String logicalCollectionTableName(String tableName,
											 String ownerEntityTable, String associatedEntityTable, String propertyName
	) {
		return prefixTable(super.logicalCollectionTableName(tableName, ownerEntityTable, associatedEntityTable, propertyName));
	}
	
	@Override
	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
		return prefixColumn(super.logicalCollectionColumnName(columnName, propertyName, referencedColumn));
	}

}
