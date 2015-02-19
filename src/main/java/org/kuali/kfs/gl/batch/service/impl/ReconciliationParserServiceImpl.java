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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.ReconciliationParserService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

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
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FileEnterpriseFeederHelperServiceImpl.class);
    private enum ParseState {
        INIT, TABLE_DEF, COLUMN_DEF, CHECKSUM_DEF;
    };



    /**
     * Parses a reconciliation file
     * 
     * @param reader a source of data from which to build a reconciliation
     * @param tableId defined within the reconciliation file; defines which block to parse
     * @return parsed reconciliation data
     * @throws IOException thrown if the file cannot be written for any reason
     * @see org.kuali.kfs.gl.batch.service.ReconciliationParserService#parseReconciliatioData(java.io.Reader)
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
                line = bufReader.readLine();
                continue;
            }

            StringTokenizer strTok = new StringTokenizer(line);
            if (!strTok.hasMoreTokens()) {
                LOG.error("Cannot find TABLE_DEF_STRING");
                throw new RuntimeException();
            }
            String command = strTok.nextToken();
            if (command.equalsIgnoreCase(GeneralLedgerConstants.Reconciliation.TABLE_DEF_STRING)) {
                if (!strTok.hasMoreTokens()) {
                    LOG.error("Cannot find TABLE_DEF_STRING");
                    throw new RuntimeException();
                }
                String parsedTableId = strTok.nextToken();
                if (parsedTableId.equalsIgnoreCase(tableId)) {
                    if (!strTok.hasMoreTokens()) {
                        LOG.error("Cannot find Parsed Table Id");
                        throw new RuntimeException();
                    }
                    String parsedRowCountStr = StringUtils.removeEnd(strTok.nextToken(), ";");
                    parsedRowCountStr = StringUtils.removeEnd(parsedRowCountStr, ".00");
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
                LOG.error("Cannot find COLUMN_DEF_STRING");
                throw new RuntimeException();
            }

            String command = strTok.nextToken();
            if (command.equalsIgnoreCase(GeneralLedgerConstants.Reconciliation.COLUMN_DEF_STRING)) {
                if (!strTok.hasMoreTokens()) {
                    LOG.error("Cannot find COLUMN_DEF_STRING");
                    throw new RuntimeException();
                }
                String fieldName = strTok.nextToken();
                if (!strTok.hasMoreTokens()) {
                    LOG.error("Cannot find COLUMN_DEF_STRING");
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
            else if (command.equalsIgnoreCase(GeneralLedgerConstants.Reconciliation.CHECKSUM_DEF_STRING)) {
                if (!strTok.hasMoreTokens()) {
                    LOG.error("Cannot find CHECKSUM_DEF_STRING");
                    throw new RuntimeException();
                }
                String checksumStr = strTok.nextToken();
                checksumStr = StringUtils.removeEnd(checksumStr, ";");

                int checksum = Integer.parseInt(checksumStr);

                if (checksum != linesInBlock) {
                    LOG.error("Check Sum String is not same as Lines in Block");
                    throw new RuntimeException();
                }
                break;
            }
            else {
                LOG.error("Cannot find any fields");
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
    protected String stripCommentsAndTrim(String line) {
        int commentIndex = line.indexOf(GeneralLedgerConstants.Reconciliation.COMMENT_STRING);
        if (commentIndex > -1) {
            // chop off comments
            line = line.substring(0, commentIndex);
        }

        line = line.trim();
        return line;
    }
}
