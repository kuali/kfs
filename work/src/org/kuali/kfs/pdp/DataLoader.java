/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.pdp;


// This is a utility to help load data from a spreadsheet.  It is not needed for production or unit tests.
// It should be deleted before the release.

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DataLoader {
    public static int count = 0;

    public static void main(String[] args) {
        DataLoader ld = new DataLoader();
        List dater = ld.readFile("/Users/jsissom/fields.csv");

        List sqlData = ld.convertLines(dater);

        Collections.reverse(sqlData);
        ld.processDeletes(sqlData);

        Collections.reverse(sqlData);
        ld.processInserts(sqlData);
    }

    class SqlData {
        public String tableFields[];
        public String fieldTypes[];
        public String pks[];
        public String fields[];
    }

    public List convertLines(List lines) {
        List output = new ArrayList();
        String tableFields[] = null;
        String fieldTypes[] = null;
        String pks[] = null;

        for (Iterator iter = lines.iterator(); iter.hasNext();) {
            String line = (String)iter.next();

            String fields[] = line.split(",");
            if ( fields.length > 1 ) {
                if ( ! StringUtils.isEmpty(fields[0]) ) {
                    if ( "Types".equals(fields[0]) ) {
                        fieldTypes = fields;
                    } else if ( "PK".equals(fields[0]) ) {
                        pks = fields;
                    } else if ( "DATA".equals(fields[0]) ){
                        SqlData sd = new SqlData();
                        sd.tableFields = tableFields;
                        sd.fieldTypes = fieldTypes;
                        sd.pks = pks;
                        sd.fields = fields;
                        output.add(sd);
                    } else {
                        tableFields = fields;
                    }
                }
            }
        }
        return output;
    }

    public List readFile(String fileName) {
        List data = new ArrayList();
        File file = new File(fileName);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        try {
            fis = new FileInputStream(file);

            // Here BufferedInputStream is added for fast reading.
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            while (dis.available() != 0) {
                String line = dis.readLine();
                data.add(line);
            }

            fis.close();
            bis.close();
            dis.close();
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processDeletes(List dater) {
        for (Iterator iter = dater.iterator(); iter.hasNext();) {
            SqlData sd = (SqlData)iter.next();

            generateDelete(sd.tableFields,sd.fieldTypes,sd.pks,sd.fields);
        }
    }

    private void print(String[] fields,String title) {
        System.out.print(title + " ");
        for (int i = 0; i < fields.length; i++) {
            System.out.print(fields[i] + ",");
        }
        System.out.println("");
    }

    private void processInserts(List dater) {
        for (Iterator iter = dater.iterator(); iter.hasNext();) {
            SqlData sd = (SqlData)iter.next();

            generateInsert(sd.tableFields,sd.fieldTypes,sd.pks,sd.fields);
        }
    }
    
    private void generateDelete(String[] tableFields,String[] fieldTypes,String[] pks,String[] values) {
        StringBuffer sb = new StringBuffer("SELECT ");
        sb.append(DataLoader.count++);
        sb.append(" FROM DUAL\n/\nDELETE FROM ");
        sb.append(tableFields[0]);
        sb.append(" WHERE ");
        boolean stuff = false;
        for (int i = 1; i < tableFields.length; i++) {
            if ( "X".equals(pks[i]) ) {
                if ( stuff ) {
                    sb.append(" AND ");
                }
                sb.append(tableFields[i]);
                sb.append(" = ");
                sb.append(value(values[i],fieldTypes[i]));
                stuff = true;
            }
        }
        sb.append("\n/\n");
        System.out.println(sb);
    }

    private void generateInsert(String[] tableFields,String[] fieldTypes,String[] pks,String[] values) {
        StringBuffer sb = new StringBuffer("SELECT ");
        sb.append(DataLoader.count++);
        sb.append(" FROM DUAL\n/\nINSERT INTO ");
        sb.append(tableFields[0]);
        sb.append(" (");
        boolean stuff = false;
        for (int i = 1; i < tableFields.length; i++) {
            if ( ! StringUtils.isEmpty(values[i]) ) {
                if ( stuff ) {
                    sb.append(",");
                }
                sb.append(tableFields[i]);
                stuff = true;
            }
        }
        sb.append(") VALUES (");
        stuff = false;
        for (int i = 1; i < values.length; i++) {
            if ( ! StringUtils.isEmpty(values[i]) ) {
                if ( stuff ) {
                    sb.append(",");
                }
                sb.append(value(values[i],fieldTypes[i]));
                stuff = true;
            }
        }
        sb.append(")\n/\n");
        System.out.println(sb);
    }

    private String value(String value,String type) {
        StringBuffer sb = new StringBuffer();
        if ( "S".equals(type) ) {
            sb.append("'");
            sb.append(value);
            sb.append("'");
        } else if ( "D".equals(type) ) {
            sb.append("to_date('");
            sb.append(value);
            sb.append("','MM/DD/YYYY')");
        } else if ( "N".equals(type) ) {
            sb.append(value);
        } else if ( "F".equals(type) ) {
            sb.append("sys_guid()");
        }
        return sb.toString();
    }
}
