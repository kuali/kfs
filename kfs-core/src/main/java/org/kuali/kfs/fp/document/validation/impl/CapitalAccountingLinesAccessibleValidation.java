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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.CapitalAccountingLinesDocumentBase;
import org.kuali.kfs.fp.document.authorization.CapitalAccountingLinesAuthorizer;
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

                // we can safely cast the lookup result to CapitalAccountingLinesAuthorizer, because even if the security module is turned on so that
                // the returned result is CapitalAccountingLinesAuthorizer, it's still fine since the latter implements CapitalAccountingLinesAuthorizer
                final CapitalAccountingLinesAuthorizer capitalAccountingLineAuthorizer = (CapitalAccountingLinesAuthorizer) lookupAccountingLineAuthorizer();
                return capitalAccountingLineAuthorizer.determineEditPermissionOnFieldBypassCapitalCheck(accountingDocumentForValidation, accountingLineForValidation, getAccountingLineCollectionProperty(), KFSPropertyConstants.ACCOUNT_NUMBER, true);
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
