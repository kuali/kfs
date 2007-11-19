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
package org.kuali.module.gl.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.service.ReconciliationParserService;
import org.kuali.module.gl.util.ColumnReconciliation;
import org.kuali.module.gl.util.ReconciliationBlock;

/**
 * Format of the reconciliation file:
 * 
 * <pre>
 *  C tableid rowcount ; 
 *  S field1 dollaramount ; 
 *  S field2 dollaramount ; 
 *  E checksum ;
 * </pre>
 * 
 * The character '#' and everything following it on that line is ignored. Whitespace characters are tab and space.<br>
 * <br>
 * A 'C' 'S' or 'E' must be the first character on a line unless the line is entirely whitespace or a comment. The case of these
 * three codes is not significant.<br>
 * <br>
 * Semi-colons are required before any possible comments on C S or E lines. Any amount of whitespace delimits the elements of C, S
 * and E lines. (If an S line contains field1+field2 for the field element, take care NOT to put any whitespace between the
 * 'field1', the '+' and the 'field2'.) <br>
 * <br>
 * Tableid is an arbitrary identifier for the record<br>
 * <br>
 * Rowcount must be a non-negative integer. Fieldn is the technical fieldname(s) in the target database. Case *is* significant,
 * since this must match the database name(s) exactly.<br>
 * <br>
 * Dollaramount may be negative; the check is significant to 4 decimal places.<br>
 * <br>
 * The checksum on line E is the number of C and S lines. A C line and a terminating E line are mandatory; S lines are optional.<br>
 * <br>
 * There may be more than one C-E block per metadata file.<br>
 * <br>
 * In general, this implementation of the parser attempts to be error tolerant. It primarily looks at the C-E block that is being
 * looked for, by ignoring all other C-E blocks. A C-E block is "looked for" when the table ID of the C line is passed in as a
 * parameter of {@link #parseReconciliationData(Reader, String)}. However, if the C lines of any blocks before the looked for block
 * are incorrect, then it is likely to cause undesired behavior.
 */
public class ReconciliationParserServiceImpl implements ReconciliationParserService {
    private enum ParseState {
        INIT, TABLE_DEF, COLUMN_DEF, CHECKSUM_DEF;
    };

    // the case of these strings is not important
    private static final String TABLE_DEF_STRING = "c";
    private static final String COLUMN_DEF_STRING = "s";
    private static final String CHECKSUM_DEF_STRING = "e";

    private static final String COMMENT_STRING = "#";

    /**
     * Parses a reconciliation file
     * 
     * @param reader a source of data from which to build a reconciliation
     * @param tableId defined within the reconciliation file; defines which block to parse
     * @return parsed reconciliation data
     * @throws IOException thrown if the file cannot be written for any reason
     * @see org.kuali.module.gl.service.ReconciliationParserService#parseReconciliatioData(java.io.Reader)
     */
    public ReconciliationBlock parseReconciliationBlock(Reader reader, String tableId) throws IOException {
        BufferedReader bufReader;
        if (reader instanceof BufferedReader) {
            bufReader = (BufferedReader) reader;
        }
        else {
            bufReader = new BufferedReader(reader);
        }

        // this variable is not null when we find the C line corresponding to the param table ID
        ReconciliationBlock reconciliationBlock = null;

        int linesInBlock = 0;

        // find the first "C" line of the C-E block by matching the table Id
        String line = bufReader.readLine();
        while (line != null && reconciliationBlock == null) {
            line = stripCommentsAndTrim(line);
            if (StringUtils.isBlank(line)) {
                continue;
            }

            StringTokenizer strTok = new StringTokenizer(line);
            if (!strTok.hasMoreTokens()) {
                throw new RuntimeException();
            }
            String command = strTok.nextToken();
            if (command.equalsIgnoreCase(TABLE_DEF_STRING)) {
                if (!strTok.hasMoreTokens()) {
                    throw new RuntimeException();
                }
                String parsedTableId = strTok.nextToken();
                if (parsedTableId.equals(tableId)) {
                    if (!strTok.hasMoreTokens()) {
                        throw new RuntimeException();
                    }
                    String parsedRowCountStr = StringUtils.removeEnd(strTok.nextToken(), ";");
                    int parsedRowCount = Integer.parseInt(parsedRowCountStr);

                    reconciliationBlock = new ReconciliationBlock();
                    reconciliationBlock.setTableId(parsedTableId);
                    reconciliationBlock.setRowCount(parsedRowCount);

                    linesInBlock++;

                    break;
                }
            }
            line = bufReader.readLine();
        }

        if (reconciliationBlock == null) {
            return null;
        }

        boolean endBlockLineEncountered = false;
        line = bufReader.readLine();
        while (line != null && !endBlockLineEncountered) {
            line = stripCommentsAndTrim(line);
            if (StringUtils.isBlank(line)) {
                continue;
            }

            StringTokenizer strTok = new StringTokenizer(line);
            if (!strTok.hasMoreTokens()) {
                throw new RuntimeException();
            }

            String command = strTok.nextToken();
            if (command.equalsIgnoreCase(COLUMN_DEF_STRING)) {
                if (!strTok.hasMoreTokens()) {
                    throw new RuntimeException();
                }
                String fieldName = strTok.nextToken();
                if (!strTok.hasMoreTokens()) {
                    throw new RuntimeException();
                }
                String columnAmountStr = strTok.nextToken();
                columnAmountStr = StringUtils.removeEnd(columnAmountStr, ";");

                KualiDecimal columnAmount = new KualiDecimal(columnAmountStr);
                ColumnReconciliation columnReconciliation = new ColumnReconciliation();
                columnReconciliation.setFieldName(fieldName);
                columnReconciliation.setDollarAmount(columnAmount);
                reconciliationBlock.addColumn(columnReconciliation);
                linesInBlock++;
            }
            else if (command.equalsIgnoreCase(CHECKSUM_DEF_STRING)) {
                if (!strTok.hasMoreTokens()) {
                    throw new RuntimeException();
                }
                String checksumStr = strTok.nextToken();
                checksumStr = StringUtils.removeEnd(checksumStr, ";");

                int checksum = Integer.parseInt(checksumStr);

                if (checksum != linesInBlock) {
                    throw new RuntimeException();
                }
                break;
            }
            else {
                throw new RuntimeException();
            }

            line = bufReader.readLine();
        }
        return reconciliationBlock;
    }

    /**
     * Removes comments and trims whitespace
     * 
     * @param line the line
     * @return stripped and trimmed line
     */
    private String stripCommentsAndTrim(String line) {
        int commentIndex = line.indexOf(COMMENT_STRING);
        if (commentIndex > -1) {
            // chop off comments
            line = line.substring(0, commentIndex);
        }

        line = line.trim();
        return line;
    }
}
