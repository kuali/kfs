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
package org.kuali.kfs.fp.document.authorization;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.batch.service.impl.DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Authorizer which deals with financial processing document issues, specifically sales tax lines on documents This class utilizes
 * the new accountingLine model.
 */
public class DistributionOfIncomeAndExpenseAccountingLineAuthorizer extends CapitalAccountingLinesAuthorizerBase {

    private static volatile ParameterService parameterService; 

    /**
     * This method determines if the field is editable.
     *
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineFieldModifyability(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.document.web.AccountingLineViewField, java.util.Map)
     */
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        boolean hasEditPermOnField = super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);
        boolean isSourceAccountingLine = accountingLine.isSourceAccountingLine();
        if (!isSourceAccountingLine) {
            return hasEditPermOnField;
        }
        boolean isEpcSourceLine = isEpcSourceLine(accountingDocument, accountingLine);
        if (hasEditPermOnField && isSourceAccountingLine && !isEpcSourceLine) {
            return true;
        }
        return false;
    }

    /**
     * Don't render a new line if this is the source group and there's electronic payment claims
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String)
     */
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        boolean shouldRender = super.renderNewLine(accountingDocument, accountingGroupProperty);
        boolean isSourceLines = accountingGroupProperty.contains(KFSConstants.SOURCE_ACCOUNTING_LINES_GROUP_NAME);
        if (shouldRender && isSourceLines) {
            boolean hasElectronicPaymentClaims = hasElectronicPaymentClaims(accountingDocument);
            if (hasElectronicPaymentClaims) {
                String namespaceCode = KFSConstants.CoreModuleNamespaces.FINANCIAL;
                String componentCode = DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl.URL_DOC_TYPE;
                String parameterName = KFSParameterKeyConstants.FpParameterConstants.ALLOW_ADDITIONAL_FROM_LINE_IND;
                String parameterValue = getParameterService().getParameterValueAsString(namespaceCode, componentCode, parameterName);
                boolean isParameterTrue = StringUtils.isNotBlank(parameterValue) && parameterValue.equals(KFSConstants.ParameterValues.YES);
                return isParameterTrue;
            }
        }
        return shouldRender;
    }

    /**
     * There's no edit permission on lines in the source group on documents claiming electronic payments unless it is a new source line.
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionOnLine(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     */
    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, boolean currentUserIsDocumentInitiator, boolean pageIsEditable) {
        boolean hasEditPermOnLine = super.determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUserIsDocumentInitiator, pageIsEditable);
        boolean isSourceAccountingLine = accountingLine.isSourceAccountingLine();
        if (!isSourceAccountingLine) {
            return hasEditPermOnLine;
        }
        boolean isEpcSourceLine = isEpcSourceLine(accountingDocument, accountingLine);
        if (hasEditPermOnLine && isSourceAccountingLine && !isEpcSourceLine) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the DI document has electronic payment claims associated with it
     * @param accountingDocument a DI document
     * @return true if there are electronic payment claims, false otherwise
     */
    protected boolean hasElectronicPaymentClaims(AccountingDocument accountingDocument) {
        DistributionOfIncomeAndExpenseDocument diDoc = (DistributionOfIncomeAndExpenseDocument) accountingDocument;

        List<ElectronicPaymentClaim> epcs = diDoc.getElectronicPaymentClaims();

        if (epcs == null) {
            diDoc.refreshReferenceObject(KFSPropertyConstants.ELECTRONIC_PAYMENT_CLAIMS);
            epcs = diDoc.getElectronicPaymentClaims();
        }

        return (!ObjectUtils.isNull(epcs) && epcs.size() > 0);
    }

    /**
     * determine whether the given source accounting line is auto generated by
     * ElectronicPaymentClaims
     * 
     * @param accountingDocument
     *            the given document
     * @param accountingLine
     *            the given accounting line
     * @return true if the given accounting line is auto generated by
     *         ElectronicPaymentClaims
     */
    protected boolean isEpcSourceLine(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean hasElectronicPaymentClaims = hasElectronicPaymentClaims(accountingDocument);
        if (hasElectronicPaymentClaims) {
            DistributionOfIncomeAndExpenseDocument diDoc = (DistributionOfIncomeAndExpenseDocument) accountingDocument;
            List<ElectronicPaymentClaim> electronicPaymentClaims = diDoc.getElectronicPaymentClaims();
            for (ElectronicPaymentClaim claim : electronicPaymentClaims) {
                SourceAccountingLine generatingAccountingLine = claim.getGeneratingAccountingLine();
                boolean equals = generatingAccountingLine.equals(accountingLine); //?
                if (equals){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * retrieves the Parameter Service
     * 
     * @return ParameterService
     */
    public ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }
}
