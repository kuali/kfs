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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCAmountsNotDrawnReport;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * Defines a custom lookupable for LOC amounts not drawn report.
 */
public class ContractsGrantsLOCAmountsNotDrawnReportLookupableHelperServiceImpl extends ContractsGrantsLOCDrawDetailsReportLookupableHelperServiceImpl {
    private static final Log LOG = LogFactory.getLog(ContractsGrantsLOCDrawDetailsReportLookupableHelperServiceImpl.class);

    /**
     * This method performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        Collection<ContractsGrantsLOCAmountsNotDrawnReport> displayList = new ArrayList<ContractsGrantsLOCAmountsNotDrawnReport>();
        Collection<ContractsGrantsLetterOfCreditReviewDocument> cgLOCReviewDocs = findFinalDocuments(ContractsGrantsLetterOfCreditReviewDocument.class);

        for (ContractsGrantsLetterOfCreditReviewDocument cgLOCReviewDoc : cgLOCReviewDocs) {
            List<ContractsGrantsLetterOfCreditReviewDetail> headerReviewDetails = cgLOCReviewDoc.getHeaderReviewDetails();
            List<ContractsGrantsLetterOfCreditReviewDetail> accountReviewDetails = cgLOCReviewDoc.getAccountReviewDetails();
            if (accountReviewDetails.size() > 0) {

                KualiDecimal totalAmountAvailableToDraw = KualiDecimal.ZERO;
                KualiDecimal totalClaimOnCashBalance = KualiDecimal.ZERO;
                KualiDecimal totalAmountToDraw = KualiDecimal.ZERO;
                KualiDecimal totalFundsNotDrawn = KualiDecimal.ZERO;

                ContractsGrantsLOCAmountsNotDrawnReport cgLOCAmountNotDrawnReport = new ContractsGrantsLOCAmountsNotDrawnReport();

                for (ContractsGrantsLetterOfCreditReviewDetail accountReviewDetailEntry : accountReviewDetails) {
                    KualiDecimal claimOnCashBalance = ObjectUtils.isNull(accountReviewDetailEntry.getClaimOnCashBalance()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getClaimOnCashBalance();
                    // PreviousDraw should be sum of amountToDraw?
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

                cgLOCAmountNotDrawnReport.setDocumentNumber(cgLOCReviewDoc.getDocumentNumber());
                cgLOCAmountNotDrawnReport.setLetterOfCreditFundCode(cgLOCReviewDoc.getLetterOfCreditFundCode());
                cgLOCAmountNotDrawnReport.setLetterOfCreditFundGroupCode(cgLOCReviewDoc.getLetterOfCreditFundGroupCode());

                final DateTime dateCreated = getDocumentDateCreated(cgLOCReviewDoc.getDocumentNumber());
                if (ObjectUtils.isNotNull(dateCreated)) {
                    cgLOCAmountNotDrawnReport.setLetterOfCreditReviewCreateDate(new java.sql.Date(dateCreated.getMillis()));
                }
                cgLOCAmountNotDrawnReport.setAmountAvailableToDraw(totalAmountAvailableToDraw);
                cgLOCAmountNotDrawnReport.setClaimOnCashBalance(totalClaimOnCashBalance);
                cgLOCAmountNotDrawnReport.setAmountToDraw(totalAmountToDraw);
                cgLOCAmountNotDrawnReport.setFundsNotDrawn(totalFundsNotDrawn);

                // Only adding entries with funds not drawn amount greater than zero
                if (totalFundsNotDrawn.isGreaterThan(KualiDecimal.ZERO)) {
                    if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgLOCAmountNotDrawnReport, "ContractsGrantsLOCAmountsNotDrawnReport")) {
                        displayList.add(cgLOCAmountNotDrawnReport);
                    }
                }
            }
        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }
}
