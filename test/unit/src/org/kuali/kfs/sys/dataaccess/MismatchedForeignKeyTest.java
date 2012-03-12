/*
 * Copyright 2006-2007 The Kuali Foundation
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
package org.kuali.kfs.sys.dataaccess;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.framework.persistence.platform.OracleDatabasePlatform;

@ConfigureContext
public class MismatchedForeignKeyTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MismatchedForeignKeyTest.class);

    protected UnitTestSqlDao unitTestSqlDao = null;

    public MismatchedForeignKeyTest() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        LOG.debug("setUp() starting");
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
    }
    
    /**
     * TODO: Remove once other tests are fixed
     */
    public void testNothing() {
        
    }

    /**
     * select occurrences where foreign key columns do not match the parent columns in data types or sizes.
     * TODO: Fix test once Jira KFSMI-5132 is resolved
     */
    public void NORUN_testExistingMismatchedForeignKeys() {
        if ( unitTestSqlDao.getDbPlatform() instanceof OracleDatabasePlatform ) {
        final List rows = unitTestSqlDao.sqlSelect("SELECT c.table_name AS child_table_name, cc.column_name AS child_column_name, " +
        "rc.table_name AS parent_table_name, rcc.column_name AS parent_column_name, " +
        "DECODE( cols.data_type, 'NUMBER', DECODE( cols.data_precision, NULL, cols.data_type, " + 
        "cols.data_type||'('||cols.DATA_PRECISION||','||cols.DATA_SCALE||')' ), 'CHAR', cols.data_type||'('||cols.data_length||')', " + 
        "'VARCHAR2', cols.data_type||'('||cols.data_length||')', cols.data_type ) AS child_data_type, " + 
        "DECODE( cols.data_type, 'NUMBER', DECODE( rcols.data_precision, NULL, rcols.data_type, " + 
        "rcols.data_type||'('||rcols.DATA_PRECISION||','||rcols.DATA_SCALE||')' ), " +
        "'CHAR', rcols.data_type||'('||rcols.data_length||')', 'VARCHAR2', rcols.data_type||'('||rcols.data_length||')', " + 
        "rcols.data_type ) AS parent_data_type " +
        "FROM user_constraints c, user_constraints rc, user_cons_columns cc, user_cons_columns rcc, user_tab_columns cols, user_tab_columns rcols " + 
        "WHERE c.constraint_type = 'R' AND cc.constraint_name = c.constraint_name AND rcc.constraint_name = c.r_constraint_name " + 
        "AND rcc.position = cc.position AND cols.table_name = c.table_name AND cols.column_name = cc.column_name " + 
        "AND rc.constraint_name = c.r_constraint_name AND rcols.table_name = rc.table_name AND rcols.column_name = rcc.column_name " + 
        "AND ( cols.data_type <> rcols.data_type OR NVL( cols.data_length, 0 ) <> NVL( rcols.data_length, 0 ) " + 
        "OR NVL( cols.data_precision, 0 ) <> NVL( rcols.data_precision, 0 ) OR NVL( cols.data_scale, 0 ) <> NVL( rcols.data_scale, 0 ))\n" +
        "AND c.table_name NOT LIKE 'NOTIFICATION%'"
        ); 

        StringBuffer failureMessage = new StringBuffer("Foreign Key Mismatches: ");
        
        if (rows.size() > 0) {
            for (Iterator iter = rows.iterator(); iter.hasNext();) {
                Map element = (Map) iter.next();
                failureMessage.append("\n\tCHILD table/column/data-type: ");
                failureMessage.append(element.get("CHILD_TABLE_NAME")).append("/").append(element.get("CHILD_COLUMN_NAME")).append("/").append(element.get("CHILD_DATA_TYPE"));
                failureMessage.append(" -- PARENT table/column/data-type: ");
                failureMessage.append(element.get("PARENT_TABLE_NAME")).append("/").append(element.get("PARENT_COLUMN_NAME")).append("/").append(element.get("PARENT_DATA_TYPE"));
            }
            failureMessage.append("\n");
        }

        assertEquals(failureMessage.toString(), 0, rows.size());
        } else {
            System.err.println( "Unable to test as no SQL available to test for this platform.");
        }    
    }

}
