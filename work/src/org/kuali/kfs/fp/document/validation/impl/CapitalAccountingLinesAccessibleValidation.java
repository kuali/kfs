/*
 * Copyright 2013 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.CapitalAccountingLinesDocumentBase;
import org.kuali.kfs.fp.document.authorization.CapitalAccountingLinesAuthorizerBase;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation;

/**
 * FP documents that collect CAMs data need special Accessible logic
 */
public class CapitalAccountingLinesAccessibleValidation extends AccountingLineAccessibleValidation {

    protected CapitalAssetBuilderModuleService capitalAssetBuilderModuleService;

    /**
     * Due to code in CapitalAccountingLinesAuthorizerBase we need alter the accessible logic a bit. Otherwise the user gets stopped for reasons they shouldn't be
     * @see org.kuali.kfs.fp.document.authorization.CapitalAccountingLinesAuthorizerBase#determineEditPermissionOnField
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionOnField
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        if (accountingDocumentForValidation instanceof CapitalAccountingLinesDocumentBase) {
            CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) accountingDocumentForValidation;

            if(caldb.getCapitalAccountingLines().size() > 0 && capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(accountingLineForValidation)) {
                // In this scenario the line is readOnly because of the logic in CapitalAccountingLinesAuthorizerBase. We only stop the user from updating
                // if the document shouldn't be editable. That means call AccountingLineAuthorizerBase#determineEditPermissionOnField and skip
                // CapitalAccountingLinesAuthorizerBase#determineEditPermissionOnField. Furthermore error correction documents should not be stopped
                if (accountingDocumentForValidation instanceof Correctable) {
                    final String errorDocumentNumber = ((FinancialSystemDocumentHeader)accountingDocumentForValidation.getDocumentHeader()).getFinancialDocumentInErrorNumber();
                    if (StringUtils.isNotBlank(errorDocumentNumber)) {
                        return true;
                    }
                }

                final CapitalAccountingLinesAuthorizerBase accountingLineAuthorizer = (CapitalAccountingLinesAuthorizerBase) lookupAccountingLineAuthorizer();
                return accountingLineAuthorizer.determineEditPermissionOnFieldBypassCapitalCheck(accountingDocumentForValidation, accountingLineForValidation, getAccountingLineCollectionProperty(), KFSPropertyConstants.ACCOUNT_NUMBER, true);
            }
        }

        return super.validate(event);
    }

    /**
     * Set the capitalAssetBuilderModuleService
     *
     * @param capitalAssetBuilderModuleService
     */
    public void setCapitalAssetBuilderModuleService(CapitalAssetBuilderModuleService capitalAssetBuilderModuleService) {
        this.capitalAssetBuilderModuleService = capitalAssetBuilderModuleService;
    }
}
