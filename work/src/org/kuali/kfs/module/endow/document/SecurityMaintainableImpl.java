/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class SecurityMaintainableImpl extends KualiMaintainableImpl {

    private Security oldSecurity;
    private Security newSecurity;

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);

        KualiWorkflowDocument workflowDoc = documentHeader.getWorkflowDocument();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        FinancialSystemMaintenanceDocument maintDoc = null;

        try {
            maintDoc = (FinancialSystemMaintenanceDocument) documentService.getByDocumentHeaderId(this.documentNumber);
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        initializeAttributes(maintDoc);

        // This code is only executed when the final approval occurs
        if (workflowDoc.stateIsProcessed()) {

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

            // when dividend amount is changed take the new value times 4 and pply to income rate
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
        newSecurity.refreshNonUpdateableReferences();

        // clear fields for copy as we want only certain fields to be copied over
        newSecurity.setDescription(KFSConstants.EMPTY_STRING);
        newSecurity.setTickerSymbol(KFSConstants.EMPTY_STRING);
        newSecurity.setUnitValue(BigDecimal.ZERO);
        newSecurity.setUnitsHeld(null);
        newSecurity.setValuationDate(null);
        newSecurity.setUnitValueSource(KFSConstants.EMPTY_STRING);
        newSecurity.setPreviousUnitValue(null);
        newSecurity.setPreviousUnitValueDate(null);
        newSecurity.setCarryValue(null);
        newSecurity.setMarketValue(BigDecimal.ZERO);
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
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterPost(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterPost(MaintenanceDocument maintDoc, Map<String, String[]> parameters) {
        super.processAfterPost(maintDoc, parameters);

        initializeAttributes(maintDoc);

        // compute and set market value or unit value depending on valuation method
        if (KNSConstants.MAINTENANCE_EDIT_ACTION.equals(getMaintenanceAction())) {

            SecurityService securityService = SpringContext.getBean(SecurityService.class);
            securityService.computeValueBasedOnValuationMethod(newSecurity);
        }
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
