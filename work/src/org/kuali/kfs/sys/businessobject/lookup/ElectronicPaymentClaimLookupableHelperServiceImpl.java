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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.dao.LookupDao;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.springframework.transaction.annotation.Transactional;

/**
 * A helper class that gives us the ability to do special lookups on electronic payment claims.
 */
@Transactional
public class ElectronicPaymentClaimLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicPaymentClaimLookupableHelperServiceImpl.class);
    private LookupDao lookupDao;

    /**
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<PersistableBusinessObject> getSearchResults(Map<String, String> fieldValues) {
        boolean unbounded = false;
        String claimingStatus = fieldValues.remove("paymentClaimStatusCode");
        if (claimingStatus != null && !claimingStatus.equals("A")) {
            if (claimingStatus.equals(ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED)) {
                fieldValues.put("paymentClaimStatusCode", ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED);
            } else {
                fieldValues.put("paymentClaimStatusCode", ElectronicPaymentClaim.ClaimStatusCodes.UNCLAIMED);
            }
        }
        String organizationReferenceId = fieldValues.remove("generatingAccountingLine.organizationReferenceId");
        List<PersistableBusinessObject> resultsList = (List)lookupDao.findCollectionBySearchHelper(ElectronicPaymentClaim.class, fieldValues, unbounded, false);
        if (!StringUtils.isBlank(organizationReferenceId)) {

            List<PersistableBusinessObject> prunedResults = pruneResults(resultsList, organizationReferenceId);
            resultsList = new CollectionIncomplete<PersistableBusinessObject>(prunedResults, ((CollectionIncomplete)resultsList).getActualSizeIfTruncated());

        }
        return resultsList;
    }

    /**
     * If organization reference id was present in lookup fields, only returns electronic payment claims which associate with the given organization reference id
     * @param paymentsToPrune the Collection of electronic payment claims, still unfiltered by organization reference id
     * @param organizationReferenceId the organization reference id to use as a filter
     * @return the filtered results
     */
    protected List<PersistableBusinessObject> pruneResults(List<PersistableBusinessObject> paymentsToPrune, String organizationReferenceId) {
        final String matchingAdvanceDepositDocumentNumbers = getAdvanceDepositsWithOrganizationReferenceId(organizationReferenceId);
        final List<GeneratingLineHolder> generatingLineHolders = getGeneratingLinesForDocuments(matchingAdvanceDepositDocumentNumbers, organizationReferenceId);

        List<PersistableBusinessObject> prunedResults = new ArrayList<PersistableBusinessObject>();
        for (PersistableBusinessObject epcAsPBO : paymentsToPrune) {
            final ElectronicPaymentClaim epc = (ElectronicPaymentClaim)epcAsPBO;

            int count = 0;
            boolean epcMatches = false;
            while (count < generatingLineHolders.size() && !epcMatches) {
                final GeneratingLineHolder generatingLine = generatingLineHolders.get(count);
                if (generatingLine.isMommy(epc)) {
                    prunedResults.add(epc);
                    epcMatches = true;
                }

                count += 1;
            }
        }

        return prunedResults;
    }

    /**
     * Finds the document ids for all AD documents which have an accounting line with the given organizationReferenceId
     * @param organizationReferenceId the organization reference id to find advance deposit documents for
     * @return a lookup String that holds the document numbers of the matching advance deposit documents
     */
    protected String getAdvanceDepositsWithOrganizationReferenceId(String organizationReferenceId) {
        StringBuilder documentNumbers = new StringBuilder();

        Map fields = new HashMap();
        fields.put("sourceAccountingLines.organizationReferenceId", organizationReferenceId);
        Collection ads = getLookupService().findCollectionBySearchUnbounded(AdvanceDepositDocument.class, fields);
        for (Object adAsObject : ads) {
            final AdvanceDepositDocument adDoc = (AdvanceDepositDocument)adAsObject;
            documentNumbers.append("|");
            documentNumbers.append(adDoc.getDocumentNumber());
        }

        return documentNumbers.substring(1);
    }

    /**
     * Looks up all of the generating lines and stores essential information about them on documents given by the matchingAdvanceDepositDocumentNumbers parameter
     * and matching the given organization reference id
     * @param matchingAdvanceDepositDocumentNumbers the document numbers of matching advance deposit documents in lookup form
     * @param organizationReferenceId the organization reference id the accounting line must match
     * @return a List of essential information about each of the matching accounting lines
     */
    protected List<GeneratingLineHolder> getGeneratingLinesForDocuments(String matchingAdvanceDepositDocumentNumbers, String organizationReferenceId) {
        List<GeneratingLineHolder> holders = new ArrayList<GeneratingLineHolder>();

        Map fields = new HashMap();
        fields.put("documentNumber", matchingAdvanceDepositDocumentNumbers);
        fields.put("organizationReferenceId", organizationReferenceId);

        Collection als = getLookupService().findCollectionBySearchUnbounded(SourceAccountingLine.class, fields);
        for (Object alAsObject : als) {
            final SourceAccountingLine accountingLine = (SourceAccountingLine)alAsObject;
            holders.add(new GeneratingLineHolder(accountingLine.getDocumentNumber(), accountingLine.getSequenceNumber()));
        }

        return holders;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        // grab the backLocation and the docFormKey
        this.setDocFormKey((String)fieldValues.get("docFormKey"));
        this.setBackLocation((String)fieldValues.get("backLocation"));
        super.validateSearchParameters(fieldValues);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#isResultReturnable(org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    public boolean isResultReturnable(BusinessObject claimAsBO) {
        boolean result = super.isResultReturnable(claimAsBO);
        ElectronicPaymentClaim claim = (ElectronicPaymentClaim)claimAsBO;
        if (result && ((claim.getPaymentClaimStatusCode() != null && claim.getPaymentClaimStatusCode().equals(ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED)) || (!StringUtils.isBlank(claim.getReferenceFinancialDocumentNumber())))) {
            result = false;
        }
        return result;
    }

    /**
     * Using default results, add columnAnchor link for reference financial document number to open document
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     *
     * KRAD Conversion: Lookupable performing customization of columns of the display list.
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection displayList = super.performLookup(lookupForm, resultTable, bounded);
        for (ResultRow row : (Collection<ResultRow>)resultTable) {
            for (Column col : row.getColumns()) {
                if (StringUtils.equals("referenceFinancialDocumentNumber", col.getPropertyName()) && StringUtils.isNotBlank(col.getPropertyValue())) {
                    String propertyURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + col.getPropertyValue() + "&command=displayDocSearchView";
                    AnchorHtmlData htmlData = new AnchorHtmlData(propertyURL, "", col.getPropertyValue());
                    htmlData.setTitle(col.getPropertyValue());
                    col.setColumnAnchor(htmlData);
                }
            }
        }
        return displayList;
    }

    /**
     * Sets the lookupDao attribute value.
     * @param lookupDao The lookupDao to set.
     */
    public void setLookupDao(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    /**
     * Holds information about an accounting line which created an electronic payment claim
     */
    protected class GeneratingLineHolder {
        private String documentNumber;
        private Integer lineNumber;

        /**
         * Constructs a GeneratingLineHolder
         * @param documentNumber the document the generating line is on
         * @param lineNumber the line number of the generating line
         */
        public GeneratingLineHolder(String documentNumber, Integer lineNumber) {
            this.documentNumber = documentNumber;
            this.lineNumber = lineNumber;
        }

        /**
         * Determines if the given electronic payment claim was generated by the accounting line that this GeneratingLineHolder has information for
         * @param epc the electronic payment claim to check
         * @return true if this accounting line did generate the epc, false otherwise
         */
        public boolean isMommy(ElectronicPaymentClaim epc) {
            return epc.getDocumentNumber().equals(documentNumber) && epc.getFinancialDocumentLineNumber().equals(lineNumber);
        }
    }
}
