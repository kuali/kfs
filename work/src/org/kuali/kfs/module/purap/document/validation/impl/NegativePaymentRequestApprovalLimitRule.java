/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.NegativePaymentRequestApprovalLimit;
import org.kuali.kfs.module.purap.document.service.NegativePaymentRequestApprovalLimitService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * Business rules for the NegativePaymentRequestApprovalLimit maintenance document
 */
public class NegativePaymentRequestApprovalLimitRule extends MaintenanceDocumentRuleBase {

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomApproveDocumentBusinessRules(document);
        final NegativePaymentRequestApprovalLimit limit = (NegativePaymentRequestApprovalLimit)getNewBo();
        result &= checkExclusiveOrganizationCodeAndAccountNumber(limit);
        if (document.isNew() || document.isNewWithExisting()) {
            result &= checkUniqueConstraint(limit);
        }
        return result;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);
        final NegativePaymentRequestApprovalLimit limit = (NegativePaymentRequestApprovalLimit)getNewBo();
        result &= checkExclusiveOrganizationCodeAndAccountNumber(limit);
        if (document.isNew() || document.isNewWithExisting()) {
            result &= checkUniqueConstraint(limit);
        }
        return result;
    }
    
    
    private boolean checkUniqueConstraint(NegativePaymentRequestApprovalLimit limit) {
        boolean result = true;
        String chartCode = limit.getChartOfAccountsCode();
        String acctNumber = limit.getAccountNumber();
        String organizationCode = limit.getOrganizationCode();
         if (!StringUtils.isBlank(organizationCode)) {
            Collection<NegativePaymentRequestApprovalLimit> negativePaymentRequestApprovalLimits = SpringContext.getBean(NegativePaymentRequestApprovalLimitService.class).findByChartAndOrganization(chartCode, organizationCode); 
            if (negativePaymentRequestApprovalLimits.size() > 0) {
                putFieldError(KFSPropertyConstants.ORGANIZATION_CODE, PurapKeyConstants.ERROR_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT_ORG_AND_ACCOUNT_UNIQUE, new String[] {});
                result = false;
            }
        } else if (!StringUtils.isBlank(acctNumber)) {
            Collection<NegativePaymentRequestApprovalLimit> negativePaymentRequestApprovalLimits = SpringContext.getBean(NegativePaymentRequestApprovalLimitService.class).findByChartAndAccount(chartCode, acctNumber);
            if (negativePaymentRequestApprovalLimits.size() > 0) {
                putFieldError(KFSPropertyConstants.ACCOUNT_NUMBER, PurapKeyConstants.ERROR_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT_ORG_AND_ACCOUNT_UNIQUE, new String[] {});
                result = false;
            }
        }
        return result;
    }


    /**
     * Checks that organization code and account number aren't both specified on a new NegativePaymentRequestApprovalLimit
     * @param limit the NegativePaymentRequestApprovalLimit to check
     * @return true if the rule passed, false otherwise (with error message added)
     */
    protected boolean checkExclusiveOrganizationCodeAndAccountNumber(NegativePaymentRequestApprovalLimit limit) {
        if (!StringUtils.isBlank(limit.getOrganizationCode()) && !StringUtils.isBlank(limit.getAccountNumber())) {
            putFieldError(KFSPropertyConstants.ORGANIZATION_CODE, PurapKeyConstants.ERROR_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT_ORG_AND_ACCOUNT_EXCLUSIVE, new String[] {});
            putFieldError(KFSPropertyConstants.ACCOUNT_NUMBER, PurapKeyConstants.ERROR_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT_ORG_AND_ACCOUNT_EXCLUSIVE, new String[] {});
            return false;
        }
        return true;
    }

}
