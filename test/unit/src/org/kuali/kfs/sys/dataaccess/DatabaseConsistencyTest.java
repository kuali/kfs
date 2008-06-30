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

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class DatabaseConsistencyTest extends KualiTestBase {
    Connection dbCon = null;
    Statement dbAsk;
    StringBuffer queryString;
    ResultSet dbAnswer;

    public void setUp() throws Exception {
        super.setUp();
        DataSource mySource = SpringContext.getBean(DataSource.class);
        
        try {

            dbCon = mySource.getConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public void tearDown() throws Exception {
        
            try {
                dbCon.close();
            }
            catch (SQLException sqle2) {
                sqle2.printStackTrace();
            }
       
    }
    public void testNumber(){
        oracleNumberTest();
    }
        
    public void oracleNumberTest(){
        try{
            dbAsk = dbCon.createStatement();
            queryString = new StringBuffer("select table_name, column_name");
            queryString.append(" from all_tab_columns");
            queryString.append(" where data_type = 'NUMBER'");
            queryString.append(" and data_precision = 22");
            queryString.append(" and (data_scale is null or data_scale = 0)");
            queryString.append(" and owner = (select user from dual)");
            queryString.append(" and table_name not like '%$%'");
            queryString.append(" and (table_name != 'EN_DOC_HDR_EXT_LONG_T'");
            queryString.append(" or column_name != 'DOC_HDR_EXT_VAL' )");
            dbAnswer = dbAsk.executeQuery(queryString.toString());
            String tempString="";
            boolean testFailed=false;
            while (dbAnswer.next()){
                tempString = tempString+"Bad number field ";
                tempString = tempString+dbAnswer.getString(1)+"."+dbAnswer.getString(2)+"\n";
                testFailed=true;
            }
            assertFalse(tempString,testFailed);
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }

}
