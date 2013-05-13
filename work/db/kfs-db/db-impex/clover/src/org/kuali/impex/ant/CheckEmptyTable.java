/*
 * Copyright 2012 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
