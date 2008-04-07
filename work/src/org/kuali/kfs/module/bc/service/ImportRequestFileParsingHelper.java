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
package org.kuali.module.budget.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;

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
     * @return
     */
    public static BudgetConstructionRequestMove parseLine(String lineToParse, String fieldSeperator, String textDelimiter, boolean isAnnual) {
        List<String> attributes = new ArrayList<String>();
        BudgetConstructionRequestMove budgetConstructionRequestMove = new BudgetConstructionRequestMove();
        
        //check if line is in correct format
        if (!isLineCorrectlyFormatted(lineToParse, fieldSeperator, textDelimiter, isAnnual)) return null;
        
        if (textDelimiter.equalsIgnoreCase(BCConstants.RequestImportTextFieldDelimiter.NOTHING.toString())) {
            attributes.addAll(Arrays.asList(lineToParse.split(fieldSeperator)));
        } else if ( !lineContainsEscapedFieldSeparator(lineToParse, fieldSeperator, textDelimiter, true) ) {
            lineToParse = StringUtils.remove(lineToParse, textDelimiter);
            attributes.addAll(Arrays.asList(lineToParse.split(fieldSeperator)));
        } else {
            int firstIndexOfTextDelimiter = 0;
            int nextIndexOfTextDelimiter = lineToParse.indexOf(textDelimiter, firstIndexOfTextDelimiter + 1);
            int numberOfSeparators = isAnnual ? 5 : 16;
            
            for (int i = 0; i < numberOfSeparators; i++) {
                attributes.add(lineToParse.substring(firstIndexOfTextDelimiter + 1, nextIndexOfTextDelimiter - 1));
                firstIndexOfTextDelimiter = lineToParse.indexOf(textDelimiter, nextIndexOfTextDelimiter + 1);
                nextIndexOfTextDelimiter = lineToParse.indexOf(textDelimiter, firstIndexOfTextDelimiter + 1);
            }
            
            String remainingNonStringValuesToParse = lineToParse.substring(lineToParse.lastIndexOf(textDelimiter + 1));
            attributes.addAll(Arrays.asList(remainingNonStringValuesToParse.split(fieldSeperator)));
        }
        
        budgetConstructionRequestMove.setChartOfAccountsCode(attributes.get(0));
        budgetConstructionRequestMove.setAccountNumber(attributes.get(1));
        budgetConstructionRequestMove.setSubAccountNumber(attributes.get(2));
        budgetConstructionRequestMove.setFinancialObjectCode(attributes.get(3));
        budgetConstructionRequestMove.setFinancialSubObjectCode(attributes.get(4));
        //TODO: what value should be set for request amount
        if (isAnnual) {
            //budgetConstructionRequestMove.setFinancialDocumentMonth11LineAmount(attributes.get(0));
        } else {
            
        }

        return budgetConstructionRequestMove;
    }
    
    public static boolean isLineCorrectlyFormatted(String lineToParse, String fieldSeperator, String textDelimiter, boolean isAnnual) {
        
        if (textDelimiter.equalsIgnoreCase(BCConstants.RequestImportTextFieldDelimiter.NOTHING.toString())) {
            if (isAnnual) {
                if (StringUtils.countMatches(lineToParse, fieldSeperator) != 5) {
                    //TODO: use correct error message
                    /*this.errorList.add("line " + this.currentLine + " is incorrectly formatted. Cannot import file");*/
                    return false;
                } 
            } else {
                if (StringUtils.countMatches(lineToParse, fieldSeperator) != 16) {
                    //TODO: use correct error message
                    /*this.errorList.add("line " + this.currentLine + " is incorrectly formatted. Cannot import file");*/
                    return false;
                } 
            }
            
        } else if (StringUtils.countMatches(lineToParse, textDelimiter) != 10) {
            //TODO: use correct error message
            /*this.errorList.add("line " + this.currentLine + " is incorrectly formatted. Cannot import file");*/
            return false;
        }
        
        
        return true;
    }
    
    /**
     * Checks if a line of an annual file contains an escaped field separator (convience method to aid in file parsing)
     * 
     * @param lineToParse
     * @param fieldSeperator
     * @param textDelimiter
     * @return
     */
    public static boolean lineContainsEscapedFieldSeparator(String lineToParse, String fieldSeperator, String textDelimiter, boolean isAnnual) {
        if (isAnnual) {
            if ( isLineCorrectlyFormatted(lineToParse, fieldSeperator, textDelimiter, isAnnual) && StringUtils.countMatches(lineToParse, fieldSeperator) > 5) return true;
        } 
        else if ( isLineCorrectlyFormatted(lineToParse, fieldSeperator, textDelimiter, isAnnual) && StringUtils.countMatches(lineToParse, fieldSeperator) > 16) return true;
        
        return false;
    }    
    
}
