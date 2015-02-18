/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.tem.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 *
 */
public class CreditCardAgencyRule extends MaintenanceDocumentRuleBase {
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        super.processCustomSaveDocumentBusinessRules(document);

        final CreditCardAgency creditCardAgency = (CreditCardAgency)document.getNewMaintainableObject().getBusinessObject();

        checkOnlyCorpCardsCanMakePayments(creditCardAgency);
        checkCorporateCardAgencyGotBank(creditCardAgency);
        checkCorporateCardAgencyHasVendor(creditCardAgency);

        return true;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);

        final CreditCardAgency creditCardAgency = (CreditCardAgency)document.getNewMaintainableObject().getBusinessObject();
        result &= checkOnlyCorpCardsCanMakePayments(creditCardAgency);
        result &= checkCorporateCardAgencyGotBank(creditCardAgency);
        result &= checkCorporateCardAgencyHasVendor(creditCardAgency);

        return result;
    }

    /**
     * Determines if the given credit card agency represents the vendor of corporate cards
     * @param creditCardAgency the credit card agency to check
     * @return true if the agency represents a corporate card vendor, false otherwise
     */
    protected boolean isCorporateCardAgency(CreditCardAgency creditCardAgency) {
        return !StringUtils.isBlank(creditCardAgency.getTravelCardTypeCode()) && creditCardAgency.getTravelCardTypeCode().equals(TemConstants.TRAVEL_TYPE_CORP);
    }

    /**
     * Determines if the credit card agency can have payment indicator checked - which is only the case if the agency is a CORP card agency
     * @param creditCardAgency the credit card agency to check
     * @return true if payment indicator is either not chosen or is allowed for this agency, false and an error otherwise
     */
    protected boolean checkOnlyCorpCardsCanMakePayments(CreditCardAgency creditCardAgency) {
        if (!isCorporateCardAgency(creditCardAgency) && creditCardAgency.isPaymentIndicator().booleanValue()) {
            putFieldError(TemPropertyConstants.PAYMENT_INDICATOR, TemKeyConstants.ERROR_CREDIT_CARD_AGENCY_PAYMENT_INDICATOR_NOT_ALLOWED);
            return false;
        }
        return true;
    }

    /**
     * Checks that if the credit card agency represents a corporate card vendor, that it has a bank code
     * @param creditCardAgency the credit card agency to check
     * @return true if the rule passed, false otherwise
     */
    protected boolean checkCorporateCardAgencyGotBank(CreditCardAgency creditCardAgency) {
        final String bankCodeLabel = getDataDictionaryService().getAttributeErrorLabel(CreditCardAgency.class, KFSPropertyConstants.BANK_CODE);

        if (isCorporateCardAgency(creditCardAgency) && creditCardAgency.isPaymentIndicator()) {
            if (StringUtils.isBlank(creditCardAgency.getBankCode())) {
                putFieldError(KFSPropertyConstants.BANK_CODE, TemKeyConstants.ERROR_CREDIT_CARD_AGENCY_CORPORATE_CARD_AGENCY_BANK_REQUIRED, new String[] { bankCodeLabel });
                return false;
            }
        }
        if (!StringUtils.isBlank(creditCardAgency.getBankCode())) {
            if (ObjectUtils.isNull(creditCardAgency.getBank()) || !StringUtils.equals(creditCardAgency.getBankCode(), creditCardAgency.getBank().getBankCode())) {
                creditCardAgency.refreshReferenceObject(KFSPropertyConstants.BANK);
            }
            if (ObjectUtils.isNull(creditCardAgency.getBank()) || !creditCardAgency.getBank().isActive()) {
                putFieldError(KFSPropertyConstants.BANK_CODE, RiceKeyConstants.ERROR_EXISTENCE, new String[] { bankCodeLabel });
                return false;
            }
        }
        return true;
    }

    /**
     * Verifies that if the given credit card agency represents a corporate card vendor responsible for payments, that a vendor is associated
     * @param creditCardAgency the credit card agency to check
     * @return true if rule passes, false otherwise
     */
    protected boolean checkCorporateCardAgencyHasVendor(CreditCardAgency creditCardAgency) {
        final String vendorNumberLabel = getDataDictionaryService().getAttributeErrorLabel(CreditCardAgency.class, KFSPropertyConstants.VENDOR_NUMBER);

        if (isCorporateCardAgency(creditCardAgency) && creditCardAgency.isPaymentIndicator()) {
            if (creditCardAgency.getVendorHeaderGeneratedIdentifier() == null || creditCardAgency.getVendorDetailAssignedIdentifier() == null) {
                putFieldError(KFSPropertyConstants.VENDOR_NUMBER, TemKeyConstants.ERROR_CREDIT_CARD_AGENCY_CORPORATE_CARD_VENDOR_NUMBER_REQUIRED, new String[] { vendorNumberLabel });
                return false;
            }
        }
        if (creditCardAgency.getVendorHeaderGeneratedIdentifier() != null && creditCardAgency.getVendorDetailAssignedIdentifier() != null) {
            if (ObjectUtils.isNull(creditCardAgency.getVendorDetail()) || creditCardAgency.getVendorDetail().getVendorHeaderGeneratedIdentifier() != creditCardAgency.getVendorHeaderGeneratedIdentifier() || creditCardAgency.getVendorDetail().getVendorDetailAssignedIdentifier() != creditCardAgency.getVendorDetailAssignedIdentifier()) {
                creditCardAgency.refreshReferenceObject(VendorPropertyConstants.VENDOR_DETAIL);
            }

            if (ObjectUtils.isNull(creditCardAgency.getVendorDetail()) || !creditCardAgency.getVendorDetail().isActiveIndicator()) {
                putFieldError(KFSPropertyConstants.VENDOR_NUMBER, RiceKeyConstants.ERROR_EXISTENCE, new String[] { vendorNumberLabel });
                return false;
            }
        }
        return true;
    }
}
