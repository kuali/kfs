/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.dataaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class DatabaseConsistencyTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(DatabaseConsistencyTest.class);
    private Connection dbCon = null;
    private Statement dbAsk;
    private StringBuffer queryString;
    private ResultSet dbAnswer;
    private String dbType;

    public void setUp() throws Exception {
        super.setUp();
        DataSource mySource = SpringContext.getBean(DataSource.class);
        
        try {            
            dbCon = mySource.getConnection();
            dbType =  dbCon.getMetaData().getDatabaseProductName().toLowerCase();
        }
        catch (Exception e) {
            LOG.error( "Unable to establish connection to database.", e );
            throw e;
        }
        
    }
    public void tearDown() throws Exception {
        
            try {
                dbCon.close();
            }
            catch (SQLException e) {
                LOG.error( "Unable to establish close to database.", e );
            }
       
    }
    public void testNumber() throws Exception {
        System.err.println( "dbType: " + dbType );
        
        if ( dbType.contains("oracle") ) {
            System.err.println( "Running Oracle Test" );
            oracleNumberTest();
        }
    }
        
    public void oracleNumberTest() throws Exception {
        try{
            dbAsk = dbCon.createStatement();
            queryString = new StringBuffer("select table_name, column_name\n");
            queryString.append(" from user_tab_columns\n");
            queryString.append(" where data_type = 'NUMBER'\n");
            queryString.append(" and data_precision = 22\n");
            queryString.append(" and NVL(data_scale,0) = 0\n");
            //queryString.append(" AND owner = (SELECT user FROM dual)\n" );
            queryString.append(" and table_name not like '%$%'\n");
            queryString.append(" and table_name NOT IN ( 'EN_DOC_HDR_EXT_LONG_T','DOC_HDR_EXT_VAL' )\n");
            // ignore these tables for now - when these tables are replaced
            // their names won't match these patterns any more
            queryString.append(" and table_name NOT LIKE 'KCB%'\n");
            queryString.append(" and table_name NOT LIKE 'NOTIFICATION%'\n");
            dbAnswer = dbAsk.executeQuery(queryString.toString());
            String tempString="";
            boolean testFailed=false;
            while (dbAnswer.next()){
                tempString = tempString+"Bad number field ";
                tempString = tempString+dbAnswer.getString(1)+"."+dbAnswer.getString(2)+"\n";
                testFailed=true;
            }
            assertFalse(tempString,testFailed);
        }catch (Exception e){
            LOG.error( "Exception running test.  SQL:\n" + queryString.toString(), e);
            throw e;
        }
    }

}
