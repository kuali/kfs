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
package org.kuali.module.ar.rules;

import org.apache.log4j.Logger;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;

public class OrganizationAccountingDefaultRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationAccountingDefaultRule.class);

    @Override
    public void setupConvenienceObjects() {
        // TODO Auto-generated method stub
        super.setupConvenienceObjects();
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        // TODO Auto-generated method stub
        return super.processCustomRouteDocumentBusinessRules(document);
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // TODO Auto-generated method stub
        return super.processCustomSaveDocumentBusinessRules(document);
    }
    
    /**
     * 
     * This method checks to see if the Org specified in this document has an Org Options record
     * for it
     * @return false if it does not have an OrgOptions record
     */
    protected boolean checkOrgOptionsExists() {
        return true;
    }
    
    /**
     * 
     * This method checks that the Late Charge Object Code is of type Income
     * Using the ParameterService to find this valid value?
     * <ul>
     * <li>IN</li>
     * <li>IC</li>
     * <li>CH</li>
     * </ul>
     * @return true if it is an income object
     */
    protected boolean checkLateChargeObjectValidIncome() {
        return true;
    }
    
    /**
     * 
     * This method checks that the Writeoff Object Code is of type Expense
     * <ul>
     * <li>EX</li>
     * <li>EE</li>
     * <li>ES</li>
     * </ul>
     * @return true if it is an expense object
     */
    protected boolean checkWriteOffObjectValidExpense() {
        return true;
    }
    
    /**
     * 
     * This method checks to see if the invoice object code is of type Income
     * <ul>
     * <li>IN</li>
     * <li>IC</li>
     * <li>CH</li>
     * </ul>
     * @return true if it is an income object
     */
    protected boolean checkInvoiceObjectValidIncome() {
        return true;
    }
}
