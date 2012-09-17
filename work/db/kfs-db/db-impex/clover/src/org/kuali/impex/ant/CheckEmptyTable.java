package org.kuali.impex.ant;

import java.sql.Connection;

import org.apache.tools.ant.BuildException;
import org.kuali.impex.ETLHelper;


/**
 * Checks whether given table is empty.  If so it sets a property with the name TABLE_NAME.empty
 * for use by conditional checks later.
 *  
 * @author jonathan
 *
 */
public class CheckEmptyTable extends EtlJdbcTask {

@Override
	public void execute() throws BuildException {
		Connection con = getConnection();
		if ( ETLHelper.isTableEmpty(con, getSchemaName(), getTableName() ) ) {
			getProject().setProperty( getTableName() + ".empty", "true");
		}
	}
}
