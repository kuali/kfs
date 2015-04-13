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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.VendorConstants.VendorTypes;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchaseOrderProcessVendorValidation extends PurchasingProcessVendorValidation {

    private DataDictionaryService dataDictionaryService;

    /**
     * Validation for the Stipulation tab.
     *
     * @param poDocument the purchase order document to be validated
     * @return boolean false if the vendor stipulation description is blank.
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = super.validate(event);
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)event.getDocument();
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) purapDocument;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(PurapConstants.VENDOR_ERRORS);

        // check to see if the vendor exists in the database, i.e. its ID is not null
        Integer vendorHeaderID = poDocument.getVendorHeaderGeneratedIdentifier();
        if (ObjectUtils.isNull(vendorHeaderID)) {
            valid = false;
            errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_NONEXIST_VENDOR);
        }

        // vendor active validation...
        VendorDetail vendorDetail = super.getVendorService().getVendorDetail(poDocument.getVendorHeaderGeneratedIdentifier(), poDocument.getVendorDetailAssignedIdentifier());
        if (ObjectUtils.isNull(vendorDetail)) {
            return valid;
        }

        // make sure that the vendor is active
        if (!vendorDetail.isActiveIndicator()) {
            valid &= false;
            errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_INACTIVE_VENDOR);
        }

        // validate vendor address
        super.getPostalCodeValidationService().validateAddress(poDocument.getVendorCountryCode(), poDocument.getVendorStateCode(), poDocument.getVendorPostalCode(), PurapPropertyConstants.VENDOR_STATE_CODE, PurapPropertyConstants.VENDOR_POSTAL_CODE);

        // Do checks for alternate payee vendor.
        Integer alternateVendorHdrGeneratedId = poDocument.getAlternateVendorHeaderGeneratedIdentifier();
        Integer alternateVendorHdrDetailAssignedId = poDocument.getAlternateVendorDetailAssignedIdentifier();

        VendorDetail alternateVendor = super.getVendorService().getVendorDetail(alternateVendorHdrGeneratedId,alternateVendorHdrDetailAssignedId);

        if (alternateVendor != null) {
            if (alternateVendor.isVendorDebarred()) {
                errorMap.putError(PurapPropertyConstants.ALTERNATE_VENDOR_NAME,PurapKeyConstants.ERROR_PURCHASE_ORDER_ALTERNATE_VENDOR_DEBARRED);
                valid &= false;
            }
            if (StringUtils.equals(alternateVendor.getVendorHeader().getVendorTypeCode(), VendorTypes.DISBURSEMENT_VOUCHER)) {
                errorMap.putError(PurapPropertyConstants.ALTERNATE_VENDOR_NAME,PurapKeyConstants.ERROR_PURCHASE_ORDER_ALTERNATE_VENDOR_DV_TYPE);
                valid &= false;
            }
            if (!alternateVendor.isActiveIndicator()) {
                errorMap.putError(PurapPropertyConstants.ALTERNATE_VENDOR_NAME,PurapKeyConstants.ERROR_PURCHASE_ORDER_ALTERNATE_VENDOR_INACTIVE,PODocumentsStrings.ALTERNATE_PAYEE_VENDOR);
                valid &= false;
            }
        }


        //make sure that the vendor contract expiration date and not marked inactive.
        if (StringUtils.isNotBlank(poDocument.getVendorContractName())) {
            if (super.getVendorService().isVendorContractExpired(poDocument, poDocument.getVendorContractGeneratedIdentifier(), vendorDetail)) {
                errorMap.putError(VendorPropertyConstants.VENDOR_CONTRACT_END_DATE, PurapKeyConstants.ERROR_EXPIRED_CONTRACT_END_DATE);
                valid &= false;
            }
        }

        errorMap.clearErrorPath();
        return valid;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }


}
