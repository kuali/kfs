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
package org.kuali.module.budget.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;
import org.kuali.module.budget.service.BudgetRequestImportService;

import com.lowagie.text.DocumentException;

public class BudgetRequestImportServiceImpl implements BudgetRequestImportService {
    BusinessObjectService businessObjectService;
    List<String> errorList = new ArrayList<String>();
    Integer currentLine;
    
    public void generatePdf(List errorMessages, ByteArrayOutputStream baos) throws DocumentException {
        // TODO Auto-generated method stub
        
    }

    public List processImportFile(InputStream fileImportStream, String fieldSeperator, String textDelimiter, String fileType) throws IOException {
        this.currentLine = 1;
        List errors = new ArrayList();
        
        businessObjectService.deleteMatching(BudgetConstructionRequestMove.class, new HashMap());

        BudgetConstructionRequestMove budgetConstructionRequestMove = new BudgetConstructionRequestMove();

        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileImportStream));
        int rowCount = 1;
        while (fileReader.ready()) {
            String line = StringUtils.strip(fileReader.readLine());
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(textDelimiter)) {
                    budgetConstructionRequestMove.setChartOfAccountsCode(StringUtils.substringBetween(line, textDelimiter, textDelimiter));
                }
                else {

                }
            }
            
            // validate required fields, field length, and set default dash values
            if (StringUtils.isBlank(budgetConstructionRequestMove.getAccountNumber())) {
                // errors.add(new ErrorMessage(BCKeyConstants.ERROR_BUDGET_OBJECT_CODE_NOT_SELECTED));
                return errors;
            }
            else {
                // check account length
            }
            
            
            if (StringUtils.isBlank(budgetConstructionRequestMove.getSubAccountNumber())) {
                budgetConstructionRequestMove.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
            
            
            // store budgetConstructionRequestMove using businessObjectService
            businessObjectService.save(budgetConstructionRequestMove);
            this.currentLine ++;
        }

        return errors;
    }
    
    public void clearExistingRequest() {
        
    }
    
    public boolean validateLine(BudgetConstructionRequestMove budgetConstructionRequestMove) {
        boolean isValid = true;
        
        //if line is not valid, update errorList and return false
        
        return isValid;
    }
 
}
