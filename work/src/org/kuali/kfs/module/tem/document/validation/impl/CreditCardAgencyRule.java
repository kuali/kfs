/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 *
 */
public class CreditCardAgencyRule extends MaintenanceDocumentRuleBase {
    protected static volatile DataDictionaryService dataDictionaryService;

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        super.processCustomSaveDocumentBusinessRules(document);

        final CreditCardAgency creditCardAgency = (CreditCardAgency)document.getNewMaintainableObject().getBusinessObject();
        checkCorporateCardAgencyGotBank(creditCardAgency);

        return true;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);

        final CreditCardAgency creditCardAgency = (CreditCardAgency)document.getNewMaintainableObject().getBusinessObject();
        result &= checkCorporateCardAgencyGotBank(creditCardAgency);

        return result;
    }

    /**
     * Determines if the given credit card agency represents the vendor of corporate cards
     * @param creditCardAgency the credit card agency to check
     * @return true if the agency represents a corporate card vendor, false otherwise
     */
    protected boolean isCorporateCardAgency(CreditCardAgency creditCardAgency) {
        return creditCardAgency.getTravelCardTypeCode().equals(TemConstants.TRAVEL_TYPE_CORP);
    }

    /**
     * Checks that if the credit card agency represents a corporate card vendor, that it has a bank code
     * @param creditCardAgency the credit card agency to check
     * @return true if the rule passed, false otherwise
     */
    protected boolean checkCorporateCardAgencyGotBank(CreditCardAgency creditCardAgency) {
        if (isCorporateCardAgency(creditCardAgency)) {
            final String bankCodeLabel = getDataDictionaryService().getAttributeErrorLabel(CreditCardAgency.class, KFSPropertyConstants.BANK_CODE);
            if (StringUtils.isBlank(creditCardAgency.getBankCode())) {
                putFieldError(KFSPropertyConstants.BANK_CODE, TemKeyConstants.ERROR_CREDIT_CARD_AGENCY_CORPORATE_CARD_AGENCY_BANK_REQUIRED, new String[] { bankCodeLabel });
                return false;
            }
            creditCardAgency.refreshReferenceObject(KFSPropertyConstants.BANK);
            if (ObjectUtils.isNull(creditCardAgency.getBank()) || !creditCardAgency.getBank().isActive()) {
                putFieldError(KFSPropertyConstants.BANK_CODE, RiceKeyConstants.ERROR_EXISTENCE, new String[] { bankCodeLabel });
                return false;
            }
        }
        return true;
    }

    @Override
    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }
}
