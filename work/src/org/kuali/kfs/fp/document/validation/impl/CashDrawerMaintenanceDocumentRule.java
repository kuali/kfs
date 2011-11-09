/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * Validations for the Cash Drawer Maintenance Document
 */
public class CashDrawerMaintenanceDocumentRule extends MaintenanceDocumentRuleBase {

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean documentValid = super.processCustomRouteDocumentBusinessRules(document);
        final CashDrawer cashDrawer = (CashDrawer)document.getNewMaintainableObject().getBusinessObject();
        documentValid &= checkCashDrawerStillClosed(cashDrawer);
        documentValid &= checkCurrencyAmountsPositive(cashDrawer);
        documentValid &= checkCoinAmountsPositive(cashDrawer);
        return documentValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean documentValid = super.processCustomSaveDocumentBusinessRules(document);
        final CashDrawer cashDrawer = (CashDrawer)document.getNewMaintainableObject().getBusinessObject();
        documentValid &= checkCashDrawerStillClosed(cashDrawer);
        if (documentValid) {
            checkCurrencyAmountsPositive(cashDrawer);
            checkCoinAmountsPositive(cashDrawer);
        }
        return documentValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean documentValid = super.processCustomApproveDocumentBusinessRules(document);
        final CashDrawer cashDrawer = (CashDrawer)document.getNewMaintainableObject().getBusinessObject();
        checkCurrencyAmountsPositive(cashDrawer);
        checkCoinAmountsPositive(cashDrawer);
        return documentValid;
    }

    /**
     * Validates that all the currency counts are positive
     * @param cashDrawer the cash drawer to check
     * @return true if the cash drawer has valid positive currency amounts, false otherwise
     */
    protected boolean checkCurrencyAmountsPositive(CashDrawer cashDrawer) {
        boolean valid = true;
        if (cashDrawer.getFinancialDocumentHundredDollarAmount() != null && cashDrawer.getFinancialDocumentHundredDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("hundredDollarCount", KFSKeyConstants.CashDrawerMaintenance.HUNDRED_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getHundredDollarCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentFiftyDollarAmount() != null && cashDrawer.getFinancialDocumentFiftyDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("fiftyDollarCount", KFSKeyConstants.CashDrawerMaintenance.FIFTY_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFiftyDollarCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentTwentyDollarAmount() != null && cashDrawer.getFinancialDocumentTwentyDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("twentyDollarCount", KFSKeyConstants.CashDrawerMaintenance.TWENTY_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getTwentyDollarCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentTenDollarAmount() != null && cashDrawer.getFinancialDocumentTenDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("tenDollarCount", KFSKeyConstants.CashDrawerMaintenance.TEN_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getTenDollarCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentFiveDollarAmount() != null && cashDrawer.getFinancialDocumentFiveDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("fiveDollarCount", KFSKeyConstants.CashDrawerMaintenance.FIVE_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFiveDollarCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentTwoDollarAmount() != null && cashDrawer.getFinancialDocumentTwoDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("twoDollarCount", KFSKeyConstants.CashDrawerMaintenance.TWO_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getTwoDollarCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentOneDollarAmount() != null && cashDrawer.getFinancialDocumentOneDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("oneDollarCount", KFSKeyConstants.CashDrawerMaintenance.ONE_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getOneDollarCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentOtherDollarAmount() != null && cashDrawer.getFinancialDocumentOtherDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentOtherDollarAmount", KFSKeyConstants.CashDrawerMaintenance.OTHER_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentOtherDollarAmount().toString() });
            valid = false;
        }
        return valid;
    }
    
    /**
     * Validates that all the coin counts are positive
     * @param cashDrawer the cash drawer to check
     * @return true if the cash drawer has valid positive coin amounts, false otherwise
     */
    protected boolean checkCoinAmountsPositive(CashDrawer cashDrawer) {
        boolean valid = true;
        if (cashDrawer.getFinancialDocumentHundredCentAmount() != null && cashDrawer.getFinancialDocumentHundredCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("hundredCentCount", KFSKeyConstants.CashDrawerMaintenance.HUNDRED_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getHundredCentCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentFiftyCentAmount() != null && cashDrawer.getFinancialDocumentFiftyCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("fiftyCentCount", KFSKeyConstants.CashDrawerMaintenance.FIFTY_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFiftyCentCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentTwentyFiveCentAmount() != null && cashDrawer.getFinancialDocumentTwentyFiveCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("twentyFiveCentCount", KFSKeyConstants.CashDrawerMaintenance.TWENTY_FIVE_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getTwentyFiveCentCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentTenCentAmount() != null && cashDrawer.getFinancialDocumentTenCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("tenCentCount", KFSKeyConstants.CashDrawerMaintenance.TEN_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getTenCentCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentFiveCentAmount() != null && cashDrawer.getFinancialDocumentFiveCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("fiveCentCount", KFSKeyConstants.CashDrawerMaintenance.FIVE_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFiveCentCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentOneCentAmount() != null && cashDrawer.getFinancialDocumentOneCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("oneCentCount", KFSKeyConstants.CashDrawerMaintenance.ONE_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getOneCentCount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentOtherCentAmount() != null && cashDrawer.getFinancialDocumentOtherCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentOtherCentAmount", KFSKeyConstants.CashDrawerMaintenance.OTHER_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentOtherCentAmount().toString() });
            valid = false;
        }
        return valid;
    }
    
    /**
     * Checks that the cash drawer is still closed at the time of the rule invocation.
     * @param cashDrawer the cash drawer to check
     */
    public boolean checkCashDrawerStillClosed(CashDrawer cashDrawer) {
       boolean valid = true;
       final CashDrawerService cashDrawerService = SpringContext.getBean(CashDrawerService.class);
       final CashDrawer cashDrawerFromDB = cashDrawerService.getByCampusCode(cashDrawer.getCampusCode());
       if (cashDrawerFromDB != null && !cashDrawerFromDB.isClosed()) {
           putFieldError("campusCode", KFSKeyConstants.CashDrawerMaintenance.CASH_DRAWER_NOT_CLOSED, new String[] { cashDrawer.getCampusCode() });
           valid = false;
       }
       return valid;
    }
}
