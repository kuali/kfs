package org.kuali.kfs.module.cab.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper classes which constructs SQL from multiple lines of a SQL file.
 */
public class SqlDigester {
    private static final String LINE_COMMENT2 = "//";
    private static final String LINE_COMMENT1 = "--";
    private static final String BEGIN_COMMENT_BLOCK = "/*";
    private static final String END_COMMENT_BLOCK = "*/";
    private String delimiter;
    private List<String> lines = new ArrayList<String>();
    private boolean complete;
    private boolean blockComment;
    private boolean lineComment;

    public SqlDigester(String delimiter) {
        this.delimiter = delimiter;
    }

    public void addLine(String line) {
        String sqlLine = line.trim();
        if (this.blockComment) {
            if (sqlLine.endsWith(END_COMMENT_BLOCK) || sqlLine.indexOf(END_COMMENT_BLOCK) > -1) {
                this.blockComment = false;
                if (sqlLine.indexOf(END_COMMENT_BLOCK) < sqlLine.length() - 1) {
                    addLine(sqlLine.substring(sqlLine.indexOf(END_COMMENT_BLOCK) + 2));
                }
            }
        }
        else {
            if (!sqlLine.startsWith(LINE_COMMENT1) && !sqlLine.startsWith(LINE_COMMENT2)) {
                if (!sqlLine.startsWith(BEGIN_COMMENT_BLOCK)) {
                    this.lines.add(sqlLine);
                    if (sqlLine.endsWith(this.delimiter)) {
                        this.complete = true;
                    }
                }
                if (sqlLine.startsWith(BEGIN_COMMENT_BLOCK)) {
                    this.blockComment = true;
                }
                if (sqlLine.endsWith(END_COMMENT_BLOCK) || sqlLine.indexOf(END_COMMENT_BLOCK) > -1) {
                    this.blockComment = false;
                    if (sqlLine.indexOf(END_COMMENT_BLOCK) < sqlLine.length() - 1) {
                        addLine(sqlLine.substring(sqlLine.indexOf(END_COMMENT_BLOCK) + 2));
                    }
                }

            }
        }
    }


    public String toSql() {
        if (!this.complete) {
            return null;
        }
        StringBuilder sql = new StringBuilder();
        for (String line : this.lines) {
            sql.append(line);
            sql.append(" ");
        }
        sql.replace(sql.lastIndexOf(this.delimiter), sql.length(), "");
        return sql.toString();
    }


    /**
     * @return the complete
     */
    public boolean isComplete() {
        return this.complete;
    }
}
