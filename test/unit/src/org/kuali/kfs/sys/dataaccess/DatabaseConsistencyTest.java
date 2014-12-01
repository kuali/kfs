/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.dataaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

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
    private ArrayList<HashMap> tableData;

    public void setUp() throws Exception {
        super.setUp();
        DataSource mySource = SpringContext.getBean(DataSource.class);
        String userName = mySource.getConnection().getMetaData().getUserName();
        
        try {            
            dbCon = mySource.getConnection();
            dbType =  dbCon.getMetaData().getDatabaseProductName().toLowerCase();
        }
        catch (Exception e) {
            LOG.error( "Unable to establish connection to database.", e );
            throw e;
        }
        dbAsk = dbCon.createStatement();
        queryString = new StringBuffer("");
        if ( dbType.contains("oracle") ) {
            System.err.println( "Running Oracle Test" );
            queryString.append("select table_name, column_name,\n");
            queryString.append(" data_type ,data_precision, data_scale, data_length\n");
            queryString.append(" from user_tab_columns\n");
            queryString.append(" where 1=1\n");
            
        }else{
            System.err.println("Running mySQL Test ");
            queryString.append("select table_name, column_name,\n");
            queryString.append(" data_type, numeric_precision, numeric_scale,\n");
            queryString.append(" character_maximum_length from information_schema.columns\n");
            queryString.append(" where table_schema='"+userName+"'\n");
        }
        queryString.append(" and table_name NOT LIKE 'KCB%'\n");
        queryString.append(" and table_name NOT LIKE 'NOTIFICATION%'\n");
        dbAnswer = dbAsk.executeQuery(queryString.toString());
        tableData = new ArrayList();
        while (dbAnswer.next()){
            HashMap<String, String> tempList = new HashMap();
            tempList.put("Table", dbAnswer.getString(1));
            tempList.put("Column", dbAnswer.getString(2));
            tempList.put("Type", dbAnswer.getString(3));
            tempList.put("Precision", (null == dbAnswer.getString(4))?"null":dbAnswer.getString(4));
            tempList.put("Scale", (null == dbAnswer.getString(5))?"null":dbAnswer.getString(5));
            tempList.put("Length", (null == dbAnswer.getString(6))?"null":dbAnswer.getString(6));
            tableData.add(tempList);
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
        boolean testFailed=false;
        String tempString="";
        for (HashMap resultList:tableData){
            if (resultList.get("Type").equals("NUMBER")&&resultList.get("Column").equals("VER_NBR")&&
                    !(resultList.get("Precision").equals("8")||resultList.get("Scale").equals("0"))){
                tempString=tempString+"Bad VER_NBR field in "+resultList.get("Table")+"\n";
                testFailed=true;
            }    
        }
        assertFalse(tempString,testFailed);
    }
    public void testOBJ_ID() throws Exception {
        System.err.println( "dbType: " + dbType );
        boolean testFailed=false;
        String tempString="";
        for (HashMap resultList:tableData){
            if (resultList.get("Column").equals("OBJ_ID")&&!resultList.get("Type").equals("VARCHAR2")&&!resultList.get("Length").equals("36")){
                tempString=tempString+"Bad OBJ_ID field in "+resultList.get("Table")+"\n";
                testFailed=true;
            }
     
        }
    }

}
