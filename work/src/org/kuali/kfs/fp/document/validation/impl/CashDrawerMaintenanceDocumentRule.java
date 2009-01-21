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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.KualiDecimal;

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
        checkCurrencyAmountsPositive(cashDrawer);
        checkCoinAmountsPositive(cashDrawer);
        return documentValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean documentValid = super.processCustomSaveDocumentBusinessRules(document);
        final CashDrawer cashDrawer = (CashDrawer)document.getNewMaintainableObject().getBusinessObject();
        checkCurrencyAmountsPositive(cashDrawer);
        checkCoinAmountsPositive(cashDrawer);
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

    protected boolean checkCurrencyAmountsPositive(CashDrawer cashDrawer) {
        boolean valid = true;
        if (cashDrawer.getFinancialDocumentHundredDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentHundredDollarAmount", KFSKeyConstants.CashDrawerMaintenance.HUNDRED_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentHundredDollarAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentFiftyDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentFiftyDollarAmount", KFSKeyConstants.CashDrawerMaintenance.FIFTY_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentFiftyDollarAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentTwentyDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentTwentyDollarAmount", KFSKeyConstants.CashDrawerMaintenance.TWENTY_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentTwentyDollarAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentTenDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentTenDollarAmount", KFSKeyConstants.CashDrawerMaintenance.TEN_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentTenDollarAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentFiveDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentFiveDollarAmount", KFSKeyConstants.CashDrawerMaintenance.FIVE_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentFiveDollarAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentTwoDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentTwoDollarAmount", KFSKeyConstants.CashDrawerMaintenance.TWO_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentTwoDollarAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentOneDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentOneDollarAmount", KFSKeyConstants.CashDrawerMaintenance.ONE_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentOneDollarAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentOtherDollarAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentOtherDollarAmount", KFSKeyConstants.CashDrawerMaintenance.OTHER_DOLLAR_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentOtherDollarAmount().toString() });
            valid = false;
        }
        return valid;
    }
    
    protected boolean checkCoinAmountsPositive(CashDrawer cashDrawer) {
        boolean valid = true;
        if (cashDrawer.getFinancialDocumentHundredCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentHundredCentAmount", KFSKeyConstants.CashDrawerMaintenance.HUNDRED_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentHundredCentAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentFiftyCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentFiftyCentAmount", KFSKeyConstants.CashDrawerMaintenance.FIFTY_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentFiftyCentAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentTwentyFiveCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentTwentyFiveCentAmount", KFSKeyConstants.CashDrawerMaintenance.TWENTY_FIVE_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentTwentyFiveCentAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentTenCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentTenCentAmount", KFSKeyConstants.CashDrawerMaintenance.TEN_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentTenCentAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentFiveCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentFiveCentAmount", KFSKeyConstants.CashDrawerMaintenance.FIVE_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentFiveCentAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentOneCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentOneCentAmount", KFSKeyConstants.CashDrawerMaintenance.ONE_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentOneCentAmount().toString() });
            valid = false;
        }
        if (cashDrawer.getFinancialDocumentOtherCentAmount().compareTo(KualiDecimal.ZERO) < 0) {
            putFieldError("financialDocumentOtherCentAmount", KFSKeyConstants.CashDrawerMaintenance.OTHER_CENT_AMOUNT_NEGATIVE, new String[] { cashDrawer.getFinancialDocumentOtherCentAmount().toString() });
            valid = false;
        }
        return valid;
    }
}
