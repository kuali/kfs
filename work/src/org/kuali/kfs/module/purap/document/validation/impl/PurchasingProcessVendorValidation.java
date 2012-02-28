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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocumentBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchasingProcessVendorValidation extends PurchasingAccountsPayableProcessVendorValidation {

    private VendorService vendorService;
    private ParameterService parameterService;
    private PostalCodeValidationService postalCodeValidationService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurchasingDocument purDocument = (PurchasingDocument) event.getDocument();
        MessageMap errorMap = GlobalVariables.getMessageMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(PurapConstants.VENDOR_ERRORS);

        valid &= super.validate(event);

        if (!purDocument.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.B2B)) {

            //If there is a vendor and the transmission method is FAX and the fax number is blank, display
            //error that the fax number is required.
            if (purDocument.getVendorHeaderGeneratedIdentifier() != null && purDocument.getPurchaseOrderTransmissionMethodCode().equals(PurapConstants.POTransmissionMethods.FAX) && StringUtils.isBlank(purDocument.getVendorFaxNumber())) {
                valid &= false;
                String attributeLabel = SpringContext.getBean(DataDictionaryService.class).
                getDataDictionary().getBusinessObjectEntry(VendorAddress.class.getName()).
                getAttributeDefinition(VendorPropertyConstants.VENDOR_FAX_NUMBER).getLabel();
                errorMap.putError(VendorPropertyConstants.VENDOR_FAX_NUMBER, KFSKeyConstants.ERROR_REQUIRED, attributeLabel);
            }
            if (StringUtils.isNotBlank(purDocument.getVendorFaxNumber())) {
                PhoneNumberValidationPattern phonePattern = new PhoneNumberValidationPattern();
                if (!phonePattern.matches(purDocument.getVendorFaxNumber())) {
                    valid &= false;
                    errorMap.putError(VendorPropertyConstants.VENDOR_FAX_NUMBER, PurapKeyConstants.ERROR_FAX_NUMBER_INVALID);
                }
            }
        }

        VendorDetail vendorDetail = vendorService.getVendorDetail(purDocument.getVendorHeaderGeneratedIdentifier(), purDocument.getVendorDetailAssignedIdentifier());
        if (ObjectUtils.isNull(vendorDetail))
            return valid;
        VendorHeader vendorHeader = vendorDetail.getVendorHeader();

        // make sure that the vendor is not debarred
        if (vendorDetail.isVendorDebarred()) {
            if (parameterService.getParameterValueAsBoolean(KFSConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE, "Requisition", PurapParameterConstants.SHOW_DEBARRED_VENDOR_WARNING_IND)) {
                if (StringUtils.isEmpty(((PurchasingDocumentBase)purDocument).getJustification())) {
                    errorMap.putWarning(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.WARNING_DEBARRED_VENDOR, vendorDetail.getVendorName());
                    valid &= false;
                }
            } else {
                errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_DEBARRED_VENDOR);
                valid &= false;
            }
        }

        // make sure that the vendor is of allowed type
        List<String> allowedVendorTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapRuleConstants.PURAP_VENDOR_TYPE_ALLOWED_ON_REQ_AND_PO) );
        if (allowedVendorTypes != null && !allowedVendorTypes.isEmpty()){
           if (ObjectUtils.isNotNull(vendorHeader) && ObjectUtils.isNotNull(vendorHeader.getVendorTypeCode()) && ! allowedVendorTypes.contains(vendorHeader.getVendorTypeCode())) {
                    valid &= false;
                    errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_INVALID_VENDOR_TYPE);
            }
        }

        // make sure that the vendor is active
        if (!vendorDetail.isActiveIndicator()) {
            valid &= false;
            errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_INACTIVE_VENDOR);
        }

        // validate vendor address
        postalCodeValidationService.validateAddress(purDocument.getVendorCountryCode(), purDocument.getVendorStateCode(), purDocument.getVendorPostalCode(), PurapPropertyConstants.VENDOR_STATE_CODE, PurapPropertyConstants.VENDOR_POSTAL_CODE);

        errorMap.clearErrorPath();
        return valid;

    }

    public VendorService getVendorService() {
        return vendorService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the postalCodeValidationService attribute.
     *
     * @return Returns the postalCodeValidationService
     */

    public PostalCodeValidationService getPostalCodeValidationService() {
        return postalCodeValidationService;
    }

    /**
     * Sets the postalCodeValidationService attribute.
     *
     * @param postalCodeValidationService The postalCodeValidationService to set.
     */
    public void setPostalCodeValidationService(PostalCodeValidationService postalCodeValidationService) {
        this.postalCodeValidationService = postalCodeValidationService;
    }
}
