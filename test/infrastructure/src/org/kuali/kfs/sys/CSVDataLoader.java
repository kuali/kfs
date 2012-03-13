/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class CSVDataLoader {
    private Map<String, String[]> dataMap = new HashMap<String, String[]>();

    private Map<String, Integer> headerMap = new HashMap<String, Integer>();

    public boolean getBoolean(int pos, String column) {
        String columnData = getColumnData(pos, column);
        if (columnData == null || columnData.trim().length() == 0) {
            return false;
        }
        return Boolean.valueOf(columnData);
    }

    public int getColumnCount() {
        return this.headerMap.size();
    }

    private String getColumnData(int recordPos, String columnName) {
        String[] dataCols = dataMap.get(String.valueOf(recordPos));
        Integer headerPos = headerMap.get(columnName.trim().toUpperCase());
        if (dataCols != null && headerPos != null) {
            return dataCols[headerPos];
        }
        return null;
    }


    public Set<String> getColumns() {
        return this.headerMap.keySet();
    }

    public Date getDate(int pos, String column) throws ParseException {
        String columnData = getColumnData(pos, column);
        if (columnData == null || columnData.trim().length() == 0) {
            return null;
        }
        return new Date(DateUtils.parseDate(columnData, new String[] { "MM/dd/yyyy hh:mm:ss a", "MM/dd/yyyy" }).getTime());
    }

    public Integer getInteger(int pos, String column) {
        String columnData = getColumnData(pos, column);
        if (columnData == null || columnData.trim().length() == 0) {
            return null;
        }
        return Integer.valueOf(columnData);
    }

    public KualiDecimal getKualiDecimal(int pos, String column) {
        String columnData = getColumnData(pos, column);
        if (columnData == null || columnData.trim().length() == 0) {
            columnData = "0";
        }
        return new KualiDecimal(columnData);
    }

    public Long getLong(int pos, String column) {
        String columnData = getColumnData(pos, column);
        if (columnData == null || columnData.trim().length() == 0) {
            return null;
        }
        return Long.valueOf(columnData);
    }

    public int getRowCount() {
        return this.dataMap.size();
    }

    public String getString(int recordPos, String columnName) {
        return getColumnData(recordPos, columnName);
    }

    public Timestamp getTimestamp(int pos, String column) throws ParseException {
        String columnData = getColumnData(pos, column);
        if (columnData == null || columnData.trim().length() == 0) {
            return null;
        }
        return new Timestamp(DateUtils.parseDate(columnData, new String[] { "MM/dd/yyyy hh:mm:ss a", "MM/dd/yyyy" }).getTime());
    }

    public void loadData(String resourceName, boolean quoted) {
        BufferedReader reader = null;
        try {
            InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream(resourceName);
            reader = new BufferedReader(new InputStreamReader(systemResourceAsStream));
            String line = null;
            String[] dataCols = null;
            int headerPos = 0;
            int pos = -1;
            while ((line = reader.readLine()) != null) {
                if (quoted) {
                    dataCols = parseQuoted(line, ",");
                }
                else {
                    dataCols = line.split(",", -1);
                }
                if (pos == -1) {
                    for (String hdr : dataCols) {
                        headerMap.put(hdr.trim().toUpperCase(), headerPos++);
                    }
                }
                else {
                    dataMap.put(String.valueOf(pos), dataCols);
                }
                pos++;
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String[] parseQuoted(String record, String delimiter) {
        if (record == null) {
            return null;
        }
        String value = record.trim();
        // adjust first quote
        value = value.replaceAll("^\"", "");
        // adjust first delimiter
        value = value.replaceAll("^" + delimiter, " \"" + delimiter);
        // adjust last quote
        value = value.replaceAll("\"$", "");
        // adjust last delimiter
        value = value.replaceAll(delimiter + "$", delimiter + "\"");
        // adjust adjacent empty delimiters
        String regEx = "(.*)\"( *)" + delimiter + "( *)" + delimiter + "(.*)";
        while (Pattern.matches(regEx, value)) {
            value = value.replaceAll("\" *" + delimiter + " *" + delimiter, "\"" + delimiter + "\"\"" + delimiter);
        }
        return value.split("\" *" + delimiter + " *\"", -1);
    }

    public void reset() {
        this.dataMap.clear();
        this.dataMap = new HashMap<String, String[]>();
        this.headerMap.clear();
        this.headerMap = new HashMap<String, Integer>();
    }


}
