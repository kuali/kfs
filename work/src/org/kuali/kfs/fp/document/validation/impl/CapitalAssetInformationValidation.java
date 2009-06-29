/*
 * Copyright 2008 The Kuali Foundation.
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

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * validate the capital asset information associated with the accounting document for validation
 */
public class CapitalAssetInformationValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CapitalAssetInformationValidation.class);

    private CapitalAssetBuilderModuleService capitalAssetBuilderModuleService = SpringContext.getBean(CapitalAssetBuilderModuleService.class);
    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        return this.hasValidCapitalAssetInformation(accountingDocumentForValidation);
    }

    // determine whehter the given document has valid capital asset information if any
    private boolean hasValidCapitalAssetInformation(AccountingDocument accountingDocument) {
        LOG.debug("hasValidCapitalAssetInformation(Document) - start");

        if (accountingDocument instanceof CapitalAssetEditable == false) {
            return true;
        }

        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) accountingDocument;
        CapitalAssetInformation capitalAssetInformation = capitalAssetEditable.getCapitalAssetInformation();

        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            MessageMap errors = GlobalVariables.getMessageMap();
            errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
            errors.addToErrorPath(KFSPropertyConstants.CAPITAL_ASSET_INFORMATION);
            
            boolean isValid = capitalAssetBuilderModuleService.validateFinancialProcessingData(accountingDocument, capitalAssetInformation);
            
            errors.removeFromErrorPath(KFSPropertyConstants.CAPITAL_ASSET_INFORMATION);
            errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);
            return isValid;
        }

        return true;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

}
