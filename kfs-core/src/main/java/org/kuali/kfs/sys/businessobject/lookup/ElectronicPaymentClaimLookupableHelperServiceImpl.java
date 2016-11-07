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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.AdvanceDepositDetail;
import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.dao.LookupDao;
import org.kuali.rice.krad.util.KRADConstants;
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
        String claimingStatus = fieldValues.remove(KFSPropertyConstants.PAYMENT_CLAIM_STATUS_CODE);
        if (claimingStatus != null) {
            if (StringUtils.equals(claimingStatus, ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED)) {
                fieldValues.put(KFSPropertyConstants.PAYMENT_CLAIM_STATUS_CODE, ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED);
            }
            if (StringUtils.equals(claimingStatus, ElectronicPaymentClaim.ClaimStatusCodes.UNCLAIMED)) {
                fieldValues.put(KFSPropertyConstants.PAYMENT_CLAIM_STATUS_CODE, ElectronicPaymentClaim.ClaimStatusCodes.UNCLAIMED);
            }
        }
        Map<String, String> advanceDepositFieldValues = getAdvanceDepositFieldValues(fieldValues);
        List<ElectronicPaymentClaim> epcList = (List<ElectronicPaymentClaim>) lookupDao.findCollectionBySearchHelper(ElectronicPaymentClaim.class, fieldValues, false, false);
        if (advanceDepositFieldValues.size() > 0) {
            epcList = pruneResults(epcList, advanceDepositFieldValues);
        }
        List<PersistableBusinessObject> resultsList = new ArrayList<PersistableBusinessObject>(epcList);
        return resultsList;
    }

    private List<ElectronicPaymentClaim> pruneResults(List<ElectronicPaymentClaim> epcList, Map<String, String> fieldValues) {
        List<AdvanceDepositDocument> addList = getAdvanceDepositsWithMatchingFields(fieldValues);
        ArrayList<ElectronicPaymentClaim> prunedResults = new ArrayList<ElectronicPaymentClaim>();
        for (ElectronicPaymentClaim epc : epcList) {
            for (AdvanceDepositDocument add : addList) {
                boolean isElectronicPaymentClaimMatchAdvanceDepositDocument = isElectronicPaymentClaimMatchAdvanceDepositDocument(epc, add);
                if (isElectronicPaymentClaimMatchAdvanceDepositDocument)
                    prunedResults.add(epc);
            }
        }

        return prunedResults;
    }

    private List<AdvanceDepositDocument> getAdvanceDepositsWithMatchingFields(Map<String, String> advanceDepositFieldValues) {
        List<AdvanceDepositDocument> advanceDepositList = (List<AdvanceDepositDocument>) getLookupService().findCollectionBySearchUnbounded(AdvanceDepositDocument.class, advanceDepositFieldValues);
        return advanceDepositList;
    }

    private Map<String, String> getAdvanceDepositFieldValues(Map<String, String> fieldValues) {
        Map<String, String> returnMap = new HashMap<String, String>();

        String orgRefId = fieldValues.remove(KFSPropertyConstants.GENERATING_ACCOUNTING_LINE + KFSConstants.DELIMITER + KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);
        String dateFrom = fieldValues.remove(KFSPropertyConstants.RANGE_LOWER_BOUND_KEY_PREFIX + KFSPropertyConstants.GENERATING_ADVANCE_DEPOSIT_DETAIL + KFSConstants.DELIMITER + KFSPropertyConstants.FINANCIAL_DOCUMENT_ADVANCE_DEPOSIT_DATE);
        String dateTo = fieldValues.remove(KFSPropertyConstants.GENERATING_ADVANCE_DEPOSIT_DETAIL + KFSConstants.DELIMITER + KFSPropertyConstants.FINANCIAL_DOCUMENT_ADVANCE_DEPOSIT_DATE);
        String description = fieldValues.remove(KFSPropertyConstants.GENERATING_ACCOUNTING_LINE + KFSConstants.DELIMITER + KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_DESCRIPTION);
        String amountFrom = fieldValues.remove(KFSPropertyConstants.PAYMENT_CLAIM_AMOUNT_FROM);
        String amountTo = fieldValues.remove(KFSPropertyConstants.PAYMENT_CLAIM_AMOUNT_TO);
        if (StringUtils.isNotBlank(orgRefId)) {
            returnMap.put(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES + KFSConstants.DELIMITER + KFSPropertyConstants.ORGANIZATION_REFERENCE_ID, orgRefId);
        }
        if (StringUtils.isNotBlank(description)) {
            returnMap.put(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES + KFSConstants.DELIMITER + KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_DESCRIPTION, description);
        }
        if (StringUtils.isNotBlank(dateTo)) {
            returnMap.put(KFSPropertyConstants.ADVANCE_DEPOSITS + KFSConstants.DELIMITER + KFSPropertyConstants.FINANCIAL_DOCUMENT_ADVANCE_DEPOSIT_DATE, dateTo);
        } else {
            if (StringUtils.isNotBlank(dateFrom)) {
                returnMap.put(KFSPropertyConstants.ADVANCE_DEPOSITS + KFSConstants.DELIMITER + KFSPropertyConstants.FINANCIAL_DOCUMENT_ADVANCE_DEPOSIT_DATE, dateFrom);
            }
        }
        String amount = getAmountCriteria(amountFrom, amountTo);
        if (StringUtils.isNotBlank(amount)) {
            returnMap.put(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES + KFSConstants.DELIMITER + KFSPropertyConstants.AMOUNT, amount);
        }
        return returnMap;
    }

    /**
     * Turns a from amount and to amount into a lookupable criteria
     * 
     * @param fromAmount
     *            the lower bound amount
     * @param toAmount
     *            the upper bound amount
     * @return a lookupable criteria
     */
    private String getAmountCriteria(String fromAmount, String toAmount) {
        if (StringUtils.isNotBlank(fromAmount) && StringUtils.isNotBlank(toAmount)) {
            return fromAmount + SearchOperator.BETWEEN.op() + toAmount;
        }
        if (StringUtils.isNotBlank(fromAmount) && StringUtils.isBlank(toAmount)) {
            return SearchOperator.GREATER_THAN_EQUAL.op() + fromAmount;
        }
        if (StringUtils.isBlank(fromAmount) && StringUtils.isNotBlank(toAmount)) {
            return SearchOperator.LESS_THAN_EQUAL.op() + toAmount;
        }
        return null;
    }

    private boolean isElectronicPaymentClaimMatchAdvanceDepositDocument(ElectronicPaymentClaim epc, AdvanceDepositDocument add) {
        for (AdvanceDepositDetail detail : add.getAdvanceDeposits()) {
            if (detail.getDocumentNumber().equals(epc.getDocumentNumber())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        // grab the backLocation and the docFormKey
        this.setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        this.setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        super.validateSearchParameters(fieldValues);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#isResultReturnable(org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    public boolean isResultReturnable(BusinessObject claimAsBO) {
        boolean result = super.isResultReturnable(claimAsBO);
        ElectronicPaymentClaim claim = (ElectronicPaymentClaim) claimAsBO;
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
        for (ResultRow row : (Collection<ResultRow>) resultTable) {
            for (Column col : row.getColumns()) {
                if (StringUtils.equals(KFSPropertyConstants.REFERENCE_FINANCIAL_DOCUMENT_NUMBER, col.getPropertyName()) && StringUtils.isNotBlank(col.getPropertyValue())) {
                    String propertyURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.WORKFLOW_URL_KEY) + KRADConstants.DOCHANDLER_DO_URL + col.getPropertyValue() + KRADConstants.DOCHANDLER_URL_CHUNK;
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

}
