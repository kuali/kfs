package edu.arizona.kfs.module.cab.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableProcessingReport;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.cab.CabPropertyConstants;

/**
 * This class overrides org for CAB AP lookupable service
 */
public class PurApReportLookupableHelperServiceImpl extends org.kuali.kfs.module.cab.businessobject.lookup.PurApReportLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApReportLookupableHelperServiceImpl.class);
    
    /**
     * Overriding org CAB AP lookable service to return search result for new lookup field invoiceStatus
     * 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // purapDocumentIdentifier should query PurchasingAccountsPayableDocument
        String purapDocumentIdentifier = getSelectedField(fieldValues, CabPropertyConstants.PurchasingAccountsPayableProcessingReport.PURAP_DOCUMENT_IDENTIFIER);

        // Get the user selects 'Y' for "processed by CAMs". We will search for all status GL lines. This is because of the partial
        // submitted GL lines when GL is 'N'(new) or 'M'(modified), partial GL lines could submit to CAMs. we should include these
        // lines into the search result.
        String active = getSelectedField(fieldValues, CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE);
        if (KFSConstants.ACTIVE_INDICATOR.equalsIgnoreCase(active)) {
            fieldValues.remove(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE);
        }
        // search for GeneralLedgerEntry BO.
        Iterator searchResultIterator = getPurApReportService().findGeneralLedgers(fieldValues);
        
        // build search result per user input invoice status lookup field
        List<PurchasingAccountsPayableProcessingReport> purApReportList = new ArrayList<PurchasingAccountsPayableProcessingReport>();
        purApReportList = buildGlEntrySearchResultCollection(searchResultIterator, active, fieldValues);

        // purapDocumentIdentifier is the attribute in PurchasingAccountsPayableDocument. We need to generate a new lookup for that
        // BO, then join search results with the generalLedgerCollection to get the correct search result collection.
        if (StringUtils.isNotBlank(purapDocumentIdentifier)) {
            // construct the lookup criteria for PurchasingAccountsPayableDocument from user input
            Map<String, String> purapDocumentLookupFields = getPurApDocumentLookupFields(fieldValues, purapDocumentIdentifier);

            Collection purApDocs = getPurApReportService().findPurchasingAccountsPayableDocuments(purapDocumentLookupFields);

            Map<String, Integer> purApDocNumberMap = buildDocumentNumberMap(purApDocs);

            purApReportList = updatePurApReportListByPurApDocs(purApReportList, purApDocNumberMap);
        }
        else {
            purApReportList = updateResultList(purApReportList);
        }


        return buildSearchResultList(purApReportList);
    }


    /**
     * Override building search result collection in order to include invoice status searching as well as in search result of PurchasingAccountsPayableProcessingReport collection.
     * 
     * @param searchResultIterator
     * @param activeSelection
     * @param fieldValues
     * @return
     */
    protected List<PurchasingAccountsPayableProcessingReport> buildGlEntrySearchResultCollection(Iterator searchResultIterator, 
    		String activeSelection, Map<String, String> fieldValues) {
        List<PurchasingAccountsPayableProcessingReport> purApReportList = new ArrayList();
        String invoiceStatusForSearching = "";
        if (fieldValues.containsKey(CabPropertyConstants.INVOICE_STATUS)) {
        	invoiceStatusForSearching = fieldValues.get(CabPropertyConstants.INVOICE_STATUS);
        }
        
        while (searchResultIterator.hasNext()) {
            Object glEntry = searchResultIterator.next();

            if (glEntry.getClass().isArray()) {
                int i = 0;
                Object[] columnValues = (Object[]) glEntry;

                PurchasingAccountsPayableProcessingReport newReport = new PurchasingAccountsPayableProcessingReport();

                newReport.setUniversityFiscalYear(new Integer(columnValues[i++].toString()));
                newReport.setUniversityFiscalPeriodCode(columnValues[i++].toString());
                newReport.setChartOfAccountsCode(columnValues[i++].toString());
                newReport.setAccountNumber(columnValues[i++].toString());
                newReport.setFinancialObjectCode(columnValues[i++].toString());
                newReport.setFinancialDocumentTypeCode(columnValues[i++].toString());
                newReport.setDocumentNumber(columnValues[i++].toString());
                newReport.setTransactionDebitCreditCode(columnValues[i] == null ? null : columnValues[i].toString());
                i++;
                newReport.setTransactionLedgerEntryAmount(columnValues[i] == null ? null : new KualiDecimal(columnValues[i].toString()));
                i++;
                newReport.setReferenceFinancialDocumentNumber(columnValues[i] == null ? null : columnValues[i].toString());
                i++;
                newReport.setTransactionDate(columnValues[i] == null ? null : getDate(columnValues[i]));
                i++;
                newReport.setTransactionLedgerSubmitAmount(columnValues[i] == null ? null : new KualiDecimal(columnValues[i].toString()));
                i++;
                newReport.setActivityStatusCode(columnValues[i] == null ? null : columnValues[i].toString());

                if (!excludeFromSearchResults(newReport, activeSelection)) {
                    if (newReport.getActivityStatusCode() != null && newReport.isActive()) {
                        // adjust amount if the activity_status_code is 'N' or 'M'
                        if (newReport.getTransactionLedgerEntryAmount() != null) {
                            setReportAmount(activeSelection, newReport);
                        }
                    }
                    else {
                        // set report amount by transactional Amount
                        newReport.setReportAmount(newReport.getAmount());
                    }
                    
					if (isDocumentInvoiceStatusMatching(newReport,invoiceStatusForSearching)) {
						purApReportList.add(newReport);
					}
                }
            }
        }
        return purApReportList;
    }

    /** 
     * Matching user search field invoice status with document app doc status
     * @param documentNumber
     * @param invoiceStatusForSearching
     * @return
     */
	protected boolean isDocumentInvoiceStatusMatching(PurchasingAccountsPayableProcessingReport searchResultLine, String invoiceStatusForSearching) {
		boolean matching = true;
		String documentNumber = searchResultLine.getDocumentNumber();
		FinancialSystemDocumentHeader fsDocHeader = null;
		// populate search result with App doc status(i.e. invoice status) from financial doc header
		if (StringUtils.isNotBlank(documentNumber)) {
			Map<String, String> primaryKeys = new HashMap<String, String>();
			primaryKeys.put(CabPropertyConstants.DOCUMENT_NUMBER, documentNumber);
			fsDocHeader = getBusinessObjectService().findByPrimaryKey(FinancialSystemDocumentHeader.class, primaryKeys);
		}
		
		if (ObjectUtils.isNotNull(fsDocHeader)) {
			searchResultLine.setInvoiceStatus(fsDocHeader.getApplicationDocumentStatus());
			if (invoiceStatusForSearching.contains(KFSConstants.WILDCARD_CHARACTER)) {
				// regular expression matching
				Pattern ptn = Pattern.compile(invoiceStatusForSearching.replace(KFSConstants.WILDCARD_CHARACTER,".*"), Pattern.CASE_INSENSITIVE);
				Matcher matcher = ptn.matcher(fsDocHeader.getApplicationDocumentStatus());
				matching = matcher.matches();
			}
			else if (StringUtils.isNotBlank(invoiceStatusForSearching) && !StringUtils.equalsIgnoreCase(invoiceStatusForSearching, fsDocHeader.getApplicationDocumentStatus())) {
				// exact string matching
				matching = false;
			}
		}
		
		return matching;
	}

}
