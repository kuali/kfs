/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

public class SecurityMaintainableImpl extends KualiMaintainableImpl {

    private Security oldSecurity;
    private Security newSecurity;

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);

        WorkflowDocument workflowDoc = documentHeader.getWorkflowDocument();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        FinancialSystemMaintenanceDocument maintDoc = null;

        try {
            maintDoc = (FinancialSystemMaintenanceDocument) documentService.getByDocumentHeaderId(getDocumentNumber());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        initializeAttributes(maintDoc);

        // This code is only executed when the final approval occurs
        if (workflowDoc.isProcessed()) {

            // if user changed unit price, copy old unit price in previous unit value
            if (!ObjectUtils.equals(newSecurity.getUnitValue(), oldSecurity.getUnitValue())) {

                newSecurity.setPreviousUnitValue(oldSecurity.getUnitValue());

                // the unit value source is populated with the name of the individual that initiated the change
                Person currentUser = GlobalVariables.getUserSession().getPerson();
                newSecurity.setUnitValueSource(currentUser.getName());
            }

            // if unit value date is changed copy old unit value in previous unit value
            if (!ObjectUtils.equals(newSecurity.getValuationDate(), oldSecurity.getValuationDate())) {

                newSecurity.setPreviousUnitValueDate(oldSecurity.getValuationDate());
            }

            // when dividend amount is changed take the new value times 4 and apply to income rate
            if (!ObjectUtils.equals(newSecurity.getDividendAmount(), oldSecurity.getDividendAmount())) {
                if (newSecurity.getDividendAmount() == null) {
                    newSecurity.setIncomeRate(null);
                }
                else {
                    // ensure calculation rounding rules
                    BigDecimal newIncomeRate = KEMCalculationRoundingHelper.multiply(newSecurity.getDividendAmount(), new BigDecimal(4), EndowConstants.Scale.SECURITY_INCOME_RATE);
                    newSecurity.setIncomeRate(newIncomeRate);
                }
                // set the last income change date to the current process date
                KEMService kemService = SpringContext.getBean(KEMService.class);
                Date currentDate = kemService.getCurrentDate();
                newSecurity.setIncomeChangeDate(currentDate);

            }
            
            //KFSMI-6674
            //If SEC_INC_PAY_FREQ entered then the SEC_INC_NEXT_PAY_DT is 
            //automatically calculated.
            //if class code type is stocks and SEC_DIV_PAY_DT is entered then 
            //copy the date value to SEC_INC_NEXT_PAY_DT. 
            //We do not want to overwrite the date if it already exists.
            if (EndowConstants.ClassCodeTypes.STOCKS.equalsIgnoreCase(newSecurity.getClassCode().getClassCodeType())) {
                if (newSecurity.getDividendPayDate() != null) {
                    newSecurity.setIncomeNextPayDate(newSecurity.getDividendPayDate());
                }
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument arg0, Map<String, String[]> arg1) {
        super.processAfterCopy(arg0, arg1);

        initializeAttributes(arg0);

        // set old userEnteredSecurityIDprefix to the whole security ID so that all 9 digits are displayed on the copy screen
        oldSecurity.setUserEnteredSecurityIDprefix(oldSecurity.getId());

        newSecurity.refreshNonUpdateableReferences();

        // clear fields for copy as we want only certain fields to be copied over
        newSecurity.setDescription(KFSConstants.EMPTY_STRING);
        newSecurity.setTickerSymbol(KFSConstants.EMPTY_STRING);
        newSecurity.setUnitValue(null);
        newSecurity.setUnitsHeld(null);
        newSecurity.setValuationDate(null);
        newSecurity.setUnitValueSource(KFSConstants.EMPTY_STRING);
        newSecurity.setPreviousUnitValue(null);
        newSecurity.setPreviousUnitValueDate(null);
        newSecurity.setCarryValue(null);
        newSecurity.setMarketValue(null);
        newSecurity.setSecurityValueByMarket(null);
        newSecurity.setLastTransactionDate(null);
        newSecurity.setIncomeNextPayDate(null);
        // newSecurity.setIncomeRate(null);
        newSecurity.setIncomeChangeDate(null);
        newSecurity.setDividendRecordDate(null);
        newSecurity.setExDividendDate(null);
        newSecurity.setDividendPayDate(null);
        newSecurity.setDividendAmount(null);
        newSecurity.setNextFiscalYearDistributionAmount(null);
    }

    /**
     * Initializes newSecurity and oldSecurity.
     * 
     * @param document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newSecurity == null) {
            newSecurity = (Security) document.getNewMaintainableObject().getBusinessObject();
        }
        if (oldSecurity == null) {
            oldSecurity = (Security) document.getOldMaintainableObject().getBusinessObject();
        }
    }

}
