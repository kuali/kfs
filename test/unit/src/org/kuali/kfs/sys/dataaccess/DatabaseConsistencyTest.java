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
import java.util.ArrayList;

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
    private ArrayList<DataRecord> queryResults;

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
        System.err.println( "dbType: " + dbType );
        
        if ( dbType.contains("oracle") ) {
            
            dbAsk = dbCon.createStatement();
            
            System.err.println( "Running Oracle Test" );
            queryString = new StringBuffer("select table_name, column_name, data_type," +
                    "nvl(data_precision,nvl(data_length,0)), nvl(data_scale,0)\n");
            queryString.append("from all_tab_columns\n");
            queryString.append("where table_name NOT LIKE 'KCB%'" +
                    "and table_name NOT LIKE 'NOTIFICATION%'\n");
            queryString.append("and owner='KULDEV'");
            System.err.println(queryString+"\n\n");
            
            dbAnswer = dbAsk.executeQuery(queryString.toString());
            String tempString="";
            boolean testFailed=false;
            queryResults = new ArrayList();
            while (dbAnswer.next()){
                try{
                queryResults.add(new DataRecord(dbAnswer.getString(1),dbAnswer.getString(2), dbAnswer.getString(3), dbAnswer.getInt(4), dbAnswer.getInt(5)));
        }catch (Exception e){
            System.out.println(e+dbAnswer.getString(1)+dbAnswer.getString(2)+ dbAnswer.getString(3)+ dbAnswer.getInt(4)+ dbAnswer.getInt(5));
        }
            }
            System.err.println("Size of List is "+ queryResults.size());
        
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
        
            oracleNumberTest();
        }
    
        
    public void oracleNumberTest() throws Exception {
        try{
            dbAsk = dbCon.createStatement();
            queryString = new StringBuffer("select table_name, column_name\n");
            queryString.append(" from user_tab_columns\n");
            queryString.append(" where data_type = 'NUMBER'\n");
            queryString.append(" and column_name = 'VER_NBR'\n");
            queryString.append(" and (data_precision!=8\n");
            queryString.append(" or NVL(data_scale,0) != 0)\n");
            //queryString.append(" AND owner = (SELECT user FROM dual)\n" );
            queryString.append(" and table_name not like '%$%'\n");
            // ignore these tables for now - when these tables are replaced
            // their names won't match these patterns any more
            queryString.append(" and table_name NOT LIKE 'KCB%'\n");
            queryString.append(" and table_name NOT LIKE 'NOTIFICATION%'\n");
            dbAnswer = dbAsk.executeQuery(queryString.toString());
            String tempString="";
            boolean testFailed=false;
            while (dbAnswer.next()){
                tempString = tempString+"Bad VER_NBR field ";
                tempString = tempString+dbAnswer.getString(1)+"."+dbAnswer.getString(2)+"\n";
                testFailed=true;
            }
            assertFalse(tempString,testFailed);
        }catch (Exception e){
            LOG.error( "Exception running test.  SQL:\n" + queryString.toString(), e);
            throw e;
        }
    }
    private class DataRecord {
        String name;
        String column;
        String dataType;
        int size;
        int scale;
        DataRecord(String name, String column, String dataType, int size, int scale){
            this.name=name;
            this.column=column;
            this.dataType=dataType;
            this.size=size;
            this.scale=scale;            
        }
    }

}
