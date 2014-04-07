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
package org.kuali.kfs.module.bc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionRequestMove;
import org.kuali.rice.core.api.util.type.KualiInteger;

/**
 * This class contains methods to help parse budget construction import request files
 *
 */
public class ImportRequestFileParsingHelper {


    /**
     * Parses line and creates BudgetConstructionRequestMove object.
     *
     * @param lineToParse
     * @param fieldSeperator
     * @param textDelimiter
     * @return the BudgetConstructionRequestMove or null if there was an error parsing the line
     */
    public static BudgetConstructionRequestMove parseLine(String lineToParse, String fieldSeperator, String textDelimiter, boolean isAnnual) {
        List<String> attributes = new ArrayList<String>();
        BudgetConstructionRequestMove budgetConstructionRequestMove = new BudgetConstructionRequestMove();

        int expectedNumberOfSeparators = isAnnual ? 5 : 16;

        lineToParse = lineToParse.trim();

        //check if line is in correct format
        if (!isLineCorrectlyFormatted(lineToParse, fieldSeperator, textDelimiter, isAnnual)) {
            return null;
        }

        if (textDelimiter.equalsIgnoreCase(BCConstants.RequestImportTextFieldDelimiter.NOTHING.getDelimiter())) {
            attributes.addAll(Arrays.asList(lineToParse.split(isFieldSeparatorSpecialCharacter(fieldSeperator) ? "\\" + fieldSeperator : fieldSeperator)));
        } else if ( getEscapedFieldSeparatorCount(lineToParse, fieldSeperator, textDelimiter, isAnnual) == 0) {
            lineToParse = StringUtils.remove(lineToParse, textDelimiter);
            attributes.addAll(Arrays.asList(lineToParse.split(isFieldSeparatorSpecialCharacter(fieldSeperator) ? "\\" + fieldSeperator : fieldSeperator)));
        } else {
            int firstIndexOfTextDelimiter = 0;
            int nextIndexOfTextDelimiter = lineToParse.indexOf(textDelimiter, firstIndexOfTextDelimiter + 1);
            int expectedNumberOfTextDelimiters = 10;

            for (int i = 0; i < expectedNumberOfTextDelimiters/2; i++) {
                attributes.add(lineToParse.substring(firstIndexOfTextDelimiter, nextIndexOfTextDelimiter).replaceAll(textDelimiter, ""));
                firstIndexOfTextDelimiter = lineToParse.indexOf(textDelimiter, nextIndexOfTextDelimiter + 1);
                nextIndexOfTextDelimiter = lineToParse.indexOf(textDelimiter, firstIndexOfTextDelimiter + 1);
            }

            String remainingNonStringValuesToParse = lineToParse.substring(lineToParse.lastIndexOf(textDelimiter + 1));
            attributes.addAll(Arrays.asList(remainingNonStringValuesToParse.split(isFieldSeparatorSpecialCharacter(fieldSeperator) ? "\\" + fieldSeperator : fieldSeperator)));
        }

        // catch the short list case
        if (attributes.size() < (expectedNumberOfSeparators + 1)) {
            return null;
        }

        budgetConstructionRequestMove.setChartOfAccountsCode(attributes.get(0));
        budgetConstructionRequestMove.setAccountNumber(attributes.get(1));
        budgetConstructionRequestMove.setSubAccountNumber(attributes.get(2));
        budgetConstructionRequestMove.setFinancialObjectCode(attributes.get(3));
        budgetConstructionRequestMove.setFinancialSubObjectCode(attributes.get(4));

        try {
            if (isAnnual) {
                budgetConstructionRequestMove.setAccountLineAnnualBalanceAmount(new KualiInteger(Integer.parseInt(attributes.get(5))));

            } else {
                budgetConstructionRequestMove.setFinancialDocumentMonth1LineAmount(new KualiInteger(Integer.parseInt(attributes.get(5))));
                budgetConstructionRequestMove.setFinancialDocumentMonth2LineAmount(new KualiInteger(Integer.parseInt(attributes.get(6))));
                budgetConstructionRequestMove.setFinancialDocumentMonth3LineAmount(new KualiInteger(Integer.parseInt(attributes.get(7))));
                budgetConstructionRequestMove.setFinancialDocumentMonth4LineAmount(new KualiInteger(Integer.parseInt(attributes.get(8))));
                budgetConstructionRequestMove.setFinancialDocumentMonth5LineAmount(new KualiInteger(Integer.parseInt(attributes.get(9))));
                budgetConstructionRequestMove.setFinancialDocumentMonth6LineAmount(new KualiInteger(Integer.parseInt(attributes.get(10))));
                budgetConstructionRequestMove.setFinancialDocumentMonth7LineAmount(new KualiInteger(Integer.parseInt(attributes.get(11))));
                budgetConstructionRequestMove.setFinancialDocumentMonth8LineAmount(new KualiInteger(Integer.parseInt(attributes.get(12))));
                budgetConstructionRequestMove.setFinancialDocumentMonth9LineAmount(new KualiInteger(Integer.parseInt(attributes.get(13))));
                budgetConstructionRequestMove.setFinancialDocumentMonth10LineAmount(new KualiInteger(Integer.parseInt(attributes.get(14))));
                budgetConstructionRequestMove.setFinancialDocumentMonth11LineAmount(new KualiInteger(Integer.parseInt(attributes.get(15))));
                budgetConstructionRequestMove.setFinancialDocumentMonth12LineAmount(new KualiInteger(Integer.parseInt(attributes.get(16))));

            }
        }
        catch (NumberFormatException e) {
            return null;
        }

        return budgetConstructionRequestMove;
    }

    /**
     * Checks for the correct number of field separators and text delimiters on line (either annual or monthly file). Does not check if the correct field separator and text delimiter are being used (form level validation should do this)
     *
     * @param lineToParse
     * @param fieldSeperator
     * @param textDelimiter
     * @param isAnnual
     * @return
     */
    public static boolean isLineCorrectlyFormatted(String lineToParse, String fieldSeperator, String textDelimiter, boolean isAnnual) {
        int fieldSeparatorCount = StringUtils.countMatches(lineToParse, fieldSeperator);
        int expectedNumberOfSeparators = isAnnual ? 5 : 16;

        if (textDelimiter.equalsIgnoreCase(BCConstants.RequestImportTextFieldDelimiter.NOTHING.getDelimiter())) {

            if (isAnnual) {
                if (fieldSeparatorCount != 5) {
                    return false;
                }
            } else {
                if (fieldSeparatorCount != 16) {
                    return false;
                }
            }
        } else if (StringUtils.countMatches(lineToParse, textDelimiter) != 10) {
            return false;
        } else if ( getEscapedFieldSeparatorCount(lineToParse, fieldSeperator, textDelimiter, isAnnual) == -1 || ( fieldSeparatorCount - getEscapedFieldSeparatorCount(lineToParse, fieldSeperator, textDelimiter, isAnnual) != expectedNumberOfSeparators ) ) {
            return false;
        }

        return true;
    }

    /**
     * Checks if a line of an annual file contains an escaped field separator (convience method to aid in file parsing)
     * Will not work correctly if text delimiters are not correctly placed in lineToParse (method does not check file formatting)
     *
     * @param lineToParse
     * @param fieldSeperator
     * @param textDelimiter
     * @return number of escaped separators or -1 if file is incorrectly formatted
     */
    private static int getEscapedFieldSeparatorCount(String lineToParse, String fieldSeperator, String textDelimiter, boolean isAnnual) {
        int firstIndexOfTextDelimiter = 0;
        int nextIndexOfTextDelimiter = lineToParse.indexOf(textDelimiter, firstIndexOfTextDelimiter + 1);
        int expectedNumberOfSeparators = isAnnual ? 5 : 16;
        int expectedTextDelimitersCount = 10;
        int actualNumberOfTextDelimiters = StringUtils.countMatches(lineToParse, textDelimiter);
        int totalSeparatorsInLineToParse = StringUtils.countMatches(lineToParse, fieldSeperator);
        int escapedSeparatorsCount = 0;

        //line does not use text delimiters
        if (textDelimiter.equalsIgnoreCase(BCConstants.RequestImportTextFieldDelimiter.NOTHING.getDelimiter())) {
            return 0;
        }

        //line does not contain escaped field separators
        if (totalSeparatorsInLineToParse == expectedNumberOfSeparators) {
            return 0;
        }

        //line is incorrectly formatted
        if ( actualNumberOfTextDelimiters != expectedTextDelimitersCount) {
            return -1;
        }

        for (int i = 0; i < expectedTextDelimitersCount/2; i++) {
            String escapedString = lineToParse.substring(firstIndexOfTextDelimiter, nextIndexOfTextDelimiter);
            escapedSeparatorsCount += StringUtils.countMatches(escapedString, fieldSeperator);
            firstIndexOfTextDelimiter = lineToParse.indexOf(textDelimiter, nextIndexOfTextDelimiter + 1);
            nextIndexOfTextDelimiter = lineToParse.indexOf(textDelimiter, firstIndexOfTextDelimiter + 1);
        }

        return escapedSeparatorsCount;
    }

    /**
     * Determines if the field delimiter is a regular expression special character
     *
     * @param delimiter
     * @return true if special character
     */
    private static boolean isFieldSeparatorSpecialCharacter(String delimiter) {
        if (delimiter.equals(".")) {
            return true;
        }
        if (delimiter.equals("[")) {
            return true;
        }
        if (delimiter.equals("\\")) {
            return true;
        }
        if (delimiter.equals("*")) {
            return true;
        }
        if (delimiter.equals("^")) {
            return true;
        }
        if (delimiter.equals("$")) {
            return true;
        }

        return false;
    }

}
