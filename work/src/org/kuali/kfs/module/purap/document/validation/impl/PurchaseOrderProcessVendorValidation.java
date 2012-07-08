/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.VendorConstants.VendorTypes;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Document;
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
        if (ObjectUtils.isNull(vendorDetail))
            return valid;

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

        Document document = (Document) poDocument;
        
        //make sure that the vendor contract expiration date
        if (super.getVendorService().isVendorContractExpired(document, vendorDetail)) {
            errorMap.putError(VendorPropertyConstants.VENDOR_CONTRACT_END_DATE, PurapKeyConstants.ERROR_EXPIRED_CONTRACT_END_DATE);
            valid &= false;
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
