package org.kuali.kfs.module.cab.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that reads contents of a SQL file and builds executable SQL statements
 */
public class SqlFileParser {

    public static List<SqlDigester> parseSqls(String resourceName, String delimiter) {

        List<SqlDigester> sqls = new ArrayList<SqlDigester>();
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            URL resource = contextClassLoader.getResource(resourceName);
            BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()));
            String line = null;
            SqlDigester sql = new SqlDigester(delimiter);
            while ((line = reader.readLine()) != null) {
                sql.addLine(line);
                if (sql.isComplete()) {
                    sqls.add(sql);
                    sql = new SqlDigester(delimiter);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error while parsing the SQL file. ", e);
        }
        return sqls;
    }
}
