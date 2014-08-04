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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCReport;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a custom lookup for LOC Draw Details Report.
 */
public class ContractsGrantsLOCReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected DateTimeService dateTimeService;

    @Override
    public void validateSearchParameters(Map<String, String> fieldValues) {
        super.validateSearchParameters(fieldValues);
        validateSearchParametersForOperatorAndValue(fieldValues, ArPropertyConstants.AMOUNT_AVAILABLE_TO_DRAW);
        validateSearchParametersForOperatorAndValue(fieldValues, ArPropertyConstants.CLAIM_ON_CASH_BALANCE);
        validateSearchParametersForOperatorAndValue(fieldValues, ArPropertyConstants.AMOUNT_TO_DRAW);
        validateSearchParametersForOperatorAndValue(fieldValues, ArPropertyConstants.FUNDS_NOT_DRAWN);

        final String lowerBoundLetterOfCreditReviewCreateDate = fieldValues.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+ArPropertyConstants.LETTER_OF_CREDIT_REVIEW_CREATE_DATE);
        validateTimestampField(lowerBoundLetterOfCreditReviewCreateDate, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+ArPropertyConstants.LETTER_OF_CREDIT_REVIEW_CREATE_DATE, getDateTimeService());
        final String upperBoundLetterOfCreditReviewCreateDate = fieldValues.get(ArPropertyConstants.LETTER_OF_CREDIT_REVIEW_CREATE_DATE);
        validateTimestampField(upperBoundLetterOfCreditReviewCreateDate, ArPropertyConstants.LETTER_OF_CREDIT_REVIEW_CREATE_DATE, getDateTimeService());
    }

    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        final String reportType = (String)lookupFormFields.get("reportType");

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        OperatorAndValue amountAvailableToDrawOperator = buildOperatorAndValueFromField(lookupFormFields, ArPropertyConstants.AMOUNT_AVAILABLE_TO_DRAW);
        OperatorAndValue claimOnCashBalanceOperator = buildOperatorAndValueFromField(lookupFormFields, ArPropertyConstants.CLAIM_ON_CASH_BALANCE);
        OperatorAndValue amountToDrawOperator = buildOperatorAndValueFromField(lookupFormFields, ArPropertyConstants.AMOUNT_TO_DRAW);
        OperatorAndValue fundsNotDrawnOperator = buildOperatorAndValueFromField(lookupFormFields, ArPropertyConstants.FUNDS_NOT_DRAWN);

        Collection<ContractsGrantsLOCReport> displayList = new ArrayList<ContractsGrantsLOCReport>();

        Map<String, String> locLookupFields = buildCriteriaForLetterOfCreditLookup(lookupFormFields);
        locLookupFields.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, StringUtils.join(getFinancialSystemDocumentService().getSuccessfulDocumentStatuses(),SearchOperator.OR.op()));
        Collection<ContractsGrantsLetterOfCreditReviewDocument> cgLOCReviewDocs = getLookupService().findCollectionBySearchHelper(ContractsGrantsLetterOfCreditReviewDocument.class, locLookupFields, true);

        for (ContractsGrantsLetterOfCreditReviewDocument cgLOCReviewDoc : cgLOCReviewDocs) {
            List<ContractsGrantsLetterOfCreditReviewDetail> headerReviewDetails = cgLOCReviewDoc.getHeaderReviewDetails();
            List<ContractsGrantsLetterOfCreditReviewDetail> accountReviewDetails = cgLOCReviewDoc.getAccountReviewDetails();
            if (accountReviewDetails.size() > 0) {

                KualiDecimal totalAmountAvailableToDraw = KualiDecimal.ZERO;
                KualiDecimal totalClaimOnCashBalance = KualiDecimal.ZERO;
                KualiDecimal totalAmountToDraw = KualiDecimal.ZERO;
                KualiDecimal totalFundsNotDrawn = KualiDecimal.ZERO;

                ContractsGrantsLOCReport cgLOCReport = new ContractsGrantsLOCReport();

                for (ContractsGrantsLetterOfCreditReviewDetail accountReviewDetailEntry : accountReviewDetails) {
                    KualiDecimal claimOnCashBalance = ObjectUtils.isNull(accountReviewDetailEntry.getClaimOnCashBalance()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getClaimOnCashBalance();
                    // previousDraw should be sum of amountToDraw?
                    KualiDecimal previousDraw = ObjectUtils.isNull(accountReviewDetailEntry.getAmountToDraw()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getAmountToDraw();
                    KualiDecimal fundsNotDrawn = ObjectUtils.isNull(accountReviewDetailEntry.getFundsNotDrawn()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getFundsNotDrawn();

                    totalClaimOnCashBalance = totalClaimOnCashBalance.add(claimOnCashBalance);
                    totalAmountToDraw = totalAmountToDraw.add(previousDraw);
                    totalFundsNotDrawn = totalFundsNotDrawn.add(fundsNotDrawn);
                }

                for (ContractsGrantsLetterOfCreditReviewDetail accountReviewDetailEntry : headerReviewDetails) {
                    KualiDecimal amountAvailableToDraw = ObjectUtils.isNull(accountReviewDetailEntry.getAmountAvailableToDraw()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getAmountAvailableToDraw();
                    totalAmountAvailableToDraw = totalAmountAvailableToDraw.add(amountAvailableToDraw);
                }

                // skip the calculated fields values if necessary
                if (amountAvailableToDrawOperator != null && !amountAvailableToDrawOperator.applyComparison(totalAmountAvailableToDraw)) {
                    continue;
                }
                if (claimOnCashBalanceOperator != null && !claimOnCashBalanceOperator.applyComparison(totalClaimOnCashBalance)) {
                    continue;
                }
                if (amountToDrawOperator != null && !amountToDrawOperator.applyComparison(totalAmountToDraw)) {
                    continue;
                }
                if (fundsNotDrawnOperator != null && !fundsNotDrawnOperator.applyComparison(totalFundsNotDrawn)) {
                    continue;
                }

                cgLOCReport.setDocumentNumber(cgLOCReviewDoc.getDocumentNumber());
                cgLOCReport.setLetterOfCreditFundCode(cgLOCReviewDoc.getLetterOfCreditFundCode());
                cgLOCReport.setLetterOfCreditFundGroupCode(cgLOCReviewDoc.getLetterOfCreditFundGroupCode());

                final Timestamp dateCreated = cgLOCReviewDoc.getFinancialSystemDocumentHeader().getWorkflowCreateDate();
                if (ObjectUtils.isNotNull(dateCreated)) {
                    cgLOCReport.setLetterOfCreditReviewCreateDate(new java.sql.Date(dateCreated.getTime()));
                }
                cgLOCReport.setAmountAvailableToDraw(totalAmountAvailableToDraw);
                cgLOCReport.setClaimOnCashBalance(totalClaimOnCashBalance);
                cgLOCReport.setAmountToDraw(totalAmountToDraw);
                cgLOCReport.setFundsNotDrawn(totalFundsNotDrawn);

                if (reportType.equals(ArConstants.LOCReportTypeFieldValues.AMOUNTS_NOT_DRAWN)) {
                    if (totalFundsNotDrawn.isGreaterThan(KualiDecimal.ZERO)) {
                        displayList.add(cgLOCReport);
                    }
                } else {
                    displayList.add(cgLOCReport);
                }

            }
        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }

    /**
     * Pulls criteria from lookup form criteria which can be directly applied to finding letter of credit documents
     * @param lookupFormFields the lookup form's fields
     * @return the smaller set of fields to perform the document lookup
     */
    protected Map<String, String> buildCriteriaForLetterOfCreditLookup(Map lookupFormFields) {
        Map<String, String> lookupFields = new HashMap<String, String>();

        final String documentNumber = (String)lookupFormFields.get(KFSPropertyConstants.DOCUMENT_NUMBER);
        if (!StringUtils.isBlank(documentNumber)) {
            lookupFields.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        }

        final String letterOfCreditFundCode = (String)lookupFormFields.get(ArPropertyConstants.LETTER_OF_CREDIT_FUND_CODE);
        if (!StringUtils.isBlank(letterOfCreditFundCode)) {
            lookupFields.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND_CODE, letterOfCreditFundCode);
        }

        final String letterOfCreditFundGroupCode = (String)lookupFormFields.get(ArPropertyConstants.LETTER_OF_CREDIT_FUND_GROUP_CODE);
        if (!StringUtils.isBlank(letterOfCreditFundGroupCode)) {
            lookupFields.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND_GROUP_CODE, letterOfCreditFundGroupCode);
        }

        final String lowerBoundLetterOfCreditReviewCreateDate = (String)lookupFormFields.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+ArPropertyConstants.LETTER_OF_CREDIT_REVIEW_CREATE_DATE);
        final String upperBoundLetterOfCreditReviewCreateDate = (String)lookupFormFields.get(ArPropertyConstants.LETTER_OF_CREDIT_REVIEW_CREATE_DATE);
        final String letterOfCreditReviewCreateDateCriteria = fixDateCriteria(lowerBoundLetterOfCreditReviewCreateDate, upperBoundLetterOfCreditReviewCreateDate, true);
        if (!StringUtils.isBlank(letterOfCreditReviewCreateDateCriteria)) {
            lookupFields.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_CREATE_DATE, letterOfCreditReviewCreateDateCriteria);
        }

        return lookupFields;
    }

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
