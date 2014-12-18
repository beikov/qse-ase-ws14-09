package at.ac.tuwien.ase09.dialect;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.type.descriptor.sql.LongVarcharTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class CustomPostgreSQLDialect extends PostgreSQL82Dialect {

	@Override
	public boolean supportsIfExistsBeforeConstraintName() {
		return true;
	}
	
	@Override
	public SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
		if (Types.CLOB == sqlCode) {
			return LongVarcharTypeDescriptor.INSTANCE;
		}
		
		return super.getSqlTypeDescriptorOverride(sqlCode);
	}
}
