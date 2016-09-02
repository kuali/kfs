package edu.arizona.kfs.gl.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import edu.arizona.kfs.gl.GeneralLedgerConstants;
import edu.arizona.kfs.gl.businessobject.GecEntry;
import edu.arizona.kfs.gl.businessobject.lookup.GecEntryHelperServiceImpl;
import edu.arizona.kfs.sys.KFSPropertyConstants;

/**
 * This class serves as the struts action for implementing GEC Entry lookups
 *
 * @author Adam Kost <kosta@email.arizona.edu> with some code adapted from UCI
 */

@SuppressWarnings("deprecation")
public class GecEntryLookupAction extends KualiMultipleValueLookupAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GecEntryLookupAction.class);

    private static transient volatile ObjectCodeService objectCodeService;
    private static transient volatile ParameterEvaluatorService parameterEvaluatorService;
    private static transient volatile LookupService lookupService;
    private static transient volatile SystemOptions systemOptions;
    private static transient volatile BusinessObjectService businessObjectService;

    protected static ParameterEvaluatorService getParameterEvaluatorService() {
        if (parameterEvaluatorService == null) {
            parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        }
        return parameterEvaluatorService;
    }

    protected static ObjectCodeService getObjectCodeService() {
        if (objectCodeService == null) {
            objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        }
        return objectCodeService;
    }

    protected static LookupService getLookupService() {
        if (lookupService == null) {
            lookupService = SpringContext.getBean(LookupService.class);
        }
        return lookupService;
    }

    protected static SystemOptions getSystemOptions() {
        if (systemOptions == null) {
            systemOptions = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();
        }
        return systemOptions;
    }

    protected static BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    @Override
    protected Collection<GecEntry> performMultipleValueLookup(MultipleValueLookupForm multipleValueLookupForm, List<ResultRow> resultTable, int maxRowsPerPage, boolean bounded) {
        super.performMultipleValueLookup(multipleValueLookupForm, resultTable, maxRowsPerPage, bounded);
        Collection<GecEntry> list = filterResults(multipleValueLookupForm, resultTable, maxRowsPerPage);
        return list;
    }

    protected List<ResultRow> selectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = super.selectAll(multipleValueLookupForm, maxRowsPerPage);
        filterResults(multipleValueLookupForm, resultTable, maxRowsPerPage);
        return resultTable;
    }

    @Override
    protected List<ResultRow> unselectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = super.unselectAll(multipleValueLookupForm, maxRowsPerPage);
        filterResults(multipleValueLookupForm, resultTable, maxRowsPerPage);
        return resultTable;
    }

    @Override
    protected List<ResultRow> switchToPage(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = super.switchToPage(multipleValueLookupForm, maxRowsPerPage);
        filterResults(multipleValueLookupForm, resultTable, maxRowsPerPage);
        return resultTable;
    }

    @Override
    protected List<ResultRow> sort(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = super.sort(multipleValueLookupForm, maxRowsPerPage);
        filterResults(multipleValueLookupForm, resultTable, maxRowsPerPage);
        return resultTable;
    }

    @SuppressWarnings("unchecked")
    protected Collection<GecEntry> filterResults(MultipleValueLookupForm multipleValueLookupForm, List<ResultRow> resultTable, int maxRowsPerPage) {
        Collection<GecEntry> c = (Collection<GecEntry>) multipleValueLookupForm.getLookupable().performLookup(multipleValueLookupForm, new ArrayList<ResultRow>(), true);
        if (c == null || c.isEmpty()) {
            LOG.debug("No results found.");
            return new ArrayList<GecEntry>();
        }

        List<GecEntry> entries = new ArrayList<GecEntry>(c);
        List<GecEntry> entriesToRemove = new ArrayList<GecEntry>();
        List<GecEntry> entriesToDisable = new ArrayList<GecEntry>();

        for (GecEntry e : entries) {
            boolean removeEntry = removeEntry(e);
            if (removeEntry) {
                entriesToRemove.add(e);
            }
        }
        removeRecords(entriesToRemove, entries, resultTable);

        for (GecEntry e : entries) {
            if (e.getGecDocumentNumber() == null) {
                String gecDocumentNumber = GecEntryHelperServiceImpl.findGecDocumentNumber(e.getDocumentNumber());
                e.setGecDocumentNumber(gecDocumentNumber);
            }
            boolean disableEntry = disableEntry(e);
            if (disableEntry) {
                entriesToDisable.add(e);
            }
        }
        disableRecords(entriesToDisable, resultTable, multipleValueLookupForm);

        GecEntryHelperServiceImpl.addInquiryLinksToRecords(resultTable);

        multipleValueLookupForm.jumpToFirstPage(resultTable.size(), maxRowsPerPage);

        Map<String, String> compositeObjectIdMap = generateCompositeObjectIdMap(resultTable);
        multipleValueLookupForm.setCompositeObjectIdMap(compositeObjectIdMap);

        return entries;
    }

    private Map<String, String> generateCompositeObjectIdMap(List<ResultRow> resultTable) {
        Map<String, String> compositeObjectIdMap = new HashMap<String, String>();
        for (ResultRow row : resultTable) {
            String objId = row.getObjectId();
            compositeObjectIdMap.put(objId, objId);
        }
        return compositeObjectIdMap;
    }

    @Override
    public ActionForward prepareToReturnSelectedResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        if (StringUtils.isBlank(multipleValueLookupForm.getLookupResultsSequenceNumber())) {
            // no search was executed
            return prepareToReturnNone(mapping, form, request, response);
        }

        prepareToReturnSelectedResultBOs(multipleValueLookupForm);

        // build the parameters for the refresh url
        Properties parameters = new Properties();
        parameters.put(KRADConstants.LOOKUP_RESULTS_BO_CLASS_NAME, multipleValueLookupForm.getBusinessObjectClassName());
        parameters.put(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, multipleValueLookupForm.getLookupResultsSequenceNumber());
        parameters.put(KRADConstants.DOC_FORM_KEY, multipleValueLookupForm.getFormKey());
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KRADConstants.REFRESH_CALLER, KRADConstants.MULTIPLE_VALUE);
        parameters.put(KRADConstants.ANCHOR, multipleValueLookupForm.getLookupAnchor());
        if (multipleValueLookupForm.getDocNum() != null) {
            parameters.put(KRADConstants.DOC_NUM, multipleValueLookupForm.getDocNum());
        }

        String backUrl = UrlFactory.parameterizeUrl(multipleValueLookupForm.getBackLocation(), parameters);
        return new ActionForward(backUrl, true);
    }

    /**
     * Exclude the entry selection base on custom logic.
     *
     * @param entry
     * @return
     */
    protected boolean removeEntry(GecEntry entry) {
        LOG.debug("Determining if entry should be removed: " + entry.toString());

        // Valid Entry lookup criteria
        boolean checkLookupFields = checkLookupFields(entry);
        if (checkLookupFields == true) {
            LOG.debug("Entry not valid by the lookup field values specifications.");
            return true;
        }

        // Parameter Based Validation
        boolean checkParameters = checkParameters(entry);
        if (checkParameters == true) {
            LOG.debug("Entry not valid by the parameters");
            return true;
        }
        // Exclude offset entries.
        boolean checkOffset = checkOffset(entry);
        if (checkOffset == true) {
            LOG.debug("Entry not valid because it is an offset.");
            return true;
        }

        return false;
    }

    protected boolean checkLookupFields(GecEntry entry) {
        boolean isFiscalYearValid = isFiscalYearValid(entry);
        if (isFiscalYearValid == false) {
            LOG.debug("Fiscal Year not valid per Specifications.");
            return true;
        }

        boolean isbalanceTypeValid = getSystemOptions().getActualFinancialBalanceTypeCd().equals(entry.getFinancialBalanceTypeCode());
        if (isbalanceTypeValid == false) {
            LOG.debug("Balance Type Code not valid per Specifications.");
            return true;
        }

        boolean isEntryAmoutIsZero = entry.getTransactionLedgerEntryAmount().isZero();
        if (isEntryAmoutIsZero == true) {
            LOG.debug("Entry Amount not valid per Specifications.");
            return true;
        }

        return false;
    }

    protected boolean isFiscalYearValid(GecEntry entry) {
        boolean isFiscalYearValid = getSystemOptions().getUniversityFiscalYear().toString().equals(entry.getUniversityFiscalYear().toString());
        return isFiscalYearValid;
    }

    protected boolean checkParameters(GecEntry entry) {
        boolean isObjectTypeValid = isObjectTypeValid(entry);
        if (!isObjectTypeValid) {
            LOG.debug("isObjectTypeValid=" + isObjectTypeValid);
            return true;
        }

        boolean isObjectSubTypeValid = isObjectSubTypeValid(entry);
        if (!isObjectSubTypeValid) {
            LOG.debug("isObjectSubTypeValid=" + isObjectSubTypeValid);
            return true;
        }

        boolean isObjectTypeValidBySubType = isObjectTypeValidBySubType(entry);
        if (!isObjectTypeValidBySubType) {
            LOG.debug("isObjectTypeValidBySubType=" + isObjectTypeValidBySubType);
            return true;
        }

        boolean isDocumentTypeValid = isDocumentTypeValid(entry);
        if (!isDocumentTypeValid) {
            LOG.debug("isDocumentTypeValid=" + isDocumentTypeValid);
            return true;
        }

        boolean isOriginationCodeValid = isOriginationCodeValid(entry);
        if (!isOriginationCodeValid) {
            LOG.debug("isOriginationCodeValid=" + isOriginationCodeValid);
            return true;
        }

        return false;
    }

    protected boolean isObjectTypeValid(GecEntry entry) {
        boolean retval = getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.RESTRICTED_OBJECT_TYPE_CODES, entry.getFinancialObjectTypeCode()).evaluationSucceeds();
        return retval;
    }

    protected boolean isObjectSubTypeValid(GecEntry entry) {
        ObjectCode code = getObjectCodeService().getByPrimaryId(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialObjectCode());
        boolean retval = getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.RESTRICTED_OBJECT_SUB_TYPE_CODES, code.getFinancialObjectSubTypeCode()).evaluationSucceeds();
        return retval;
    }

    protected boolean isObjectTypeValidBySubType(GecEntry entry) {
        ObjectCode code = getObjectCodeService().getByPrimaryId(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialObjectCode());
        boolean retval = getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE, code.getFinancialObjectTypeCode(), code.getFinancialObjectSubTypeCode()).evaluationSucceeds();
        return retval;
    }

    protected boolean isDocumentTypeValid(GecEntry entry) {
        boolean retval = getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.DOCUMENT_TYPES, entry.getFinancialDocumentTypeCode()).evaluationSucceeds();
        return retval;
    }

    protected boolean isOriginationCodeValid(GecEntry entry) {
        boolean retval = getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.ORIGIN_CODES, entry.getFinancialSystemOriginationCode()).evaluationSucceeds();
        return retval;
    }

    /**
     * This method checks the entry to see if it is an Offset entry, and therefore should not be listed in the search results.
     * 
     * @param entry
     * @return true if the entry should be removed from the search results.
     */
    protected boolean checkOffset(GecEntry entry) {
        boolean isOffsetDefinition = isOffsetDefinition(entry);
        if (isOffsetDefinition) {
            LOG.debug("isOffsetDefinition=" + isOffsetDefinition);
            return true;
        }

        boolean isOffsetAccountEntry = isOffsetAccountEntry(entry);
        if (isOffsetAccountEntry) {
            LOG.debug("isOffsetAccountEntry=" + isOffsetAccountEntry);
            return true;
        }

        return false;
    }

    protected boolean isOffsetDefinition(GecEntry entry) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, entry.getUniversityFiscalYear().toString());
        primaryKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, entry.getChartOfAccountsCode());
        primaryKeys.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, entry.getFinancialDocumentTypeCode());
        primaryKeys.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, entry.getFinancialBalanceTypeCode());
        OffsetDefinition offsetDefinition = getBusinessObjectService().findByPrimaryKey(OffsetDefinition.class, primaryKeys);
        if (offsetDefinition == null || offsetDefinition.getFinancialObjectCode() == null || entry.getFinancialObjectCode() == null) {
            return false;
        }
        if (offsetDefinition.getFinancialObjectCode().equals(entry.getFinancialObjectCode())) {
            return true;
        }
        return false;
    }

    protected boolean isOffsetAccountEntry(GecEntry entry) {
        List<Bank> bankAccountList = (List<Bank>) getLookupService().findCollectionBySearchUnbounded(Bank.class, new HashMap<String, String>());
        for (Bank bankAccount : bankAccountList) {
            if (bankAccount.getBankAccountNumber().equals(entry.getAccountNumber())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Disable the entry selection based on custom logic as defined in the specifications.
     *
     * @param entry
     * @return
     */
    private boolean disableEntry(GecEntry entry) {
        LOG.debug("Determining if entry should be disabled: " + entry.toString());

        String gecDocumentNumber = entry.getGecDocumentNumber();
        if (StringUtils.isBlank(gecDocumentNumber)) {
            return false;
        }

        String docStatusCode = getDocumentStatusCode(gecDocumentNumber);
        if (docStatusCode.equals(KewApiConstants.ROUTE_HEADER_FINAL_CD)) {
            return true;
        }
        if (docStatusCode.equals(KewApiConstants.ROUTE_HEADER_ENROUTE_CD)) {
            return true;
        }
        if (docStatusCode.equals(KewApiConstants.ROUTE_HEADER_INITIATED_CD)) {
            return true;
        }
        if (docStatusCode.equals(KewApiConstants.ROUTE_HEADER_PROCESSED_CD)) {
            return true;
        }
        if (docStatusCode.equals(KewApiConstants.ROUTE_HEADER_SAVED_CD)) {
            return true;
        }
        if (docStatusCode.equals(KewApiConstants.ROUTE_HEADER_INITIATED_CD)) {
            return true;
        }
        return false;
    }

    private String getDocumentStatusCode(String documentNumber) {
        DocumentRouteHeaderValue docHeader = getBusinessObjectService().findBySinglePrimaryKey(DocumentRouteHeaderValue.class, documentNumber);
        if (docHeader == null) {
            return KFSConstants.EMPTY_STRING;
        }
        return docHeader.getDocRouteStatus();
    }

    /**
     * Removes the selected records (listed in entriesToRemove) from the results.
     * 
     * @param entriesToRemove
     * @param entries
     * @param resultTable
     */
    private void removeRecords(List<GecEntry> entriesToRemove, List<GecEntry> entries, List<ResultRow> resultTable) {
        for (GecEntry entryToRemove : entriesToRemove) {
            entries.remove(entryToRemove);
            Iterator<ResultRow> iter = resultTable.iterator();
            while (iter.hasNext()) {
                ResultRow currentRow = iter.next();
                boolean isSameEntry = GecEntryHelperServiceImpl.compareGecEntryToRow(entryToRemove, currentRow);
                if (isSameEntry) {
                    iter.remove();
                }
            }
        }
    }

    /**
     * Disables the checkbox of the selected records (listed in entriesToDisable) in the results.
     * 
     * @param entriesToRemove
     * @param entries
     * @param resultTable
     */
    private void disableRecords(List<GecEntry> entriesToDisable, List<ResultRow> resultTable, MultipleValueLookupForm multipleValueLookupForm) {
        if (entriesToDisable.isEmpty()) {
            LOG.debug("entriesToRemove is Empty");
            return;
        }
        for (GecEntry entryToDisable : entriesToDisable) {
            for (ResultRow row : resultTable) {
                boolean isSameEntry = GecEntryHelperServiceImpl.compareGecEntryToRow(entryToDisable, row);
                if (isSameEntry) {
                    row.setReturnUrl(StringUtils.EMPTY);
                    row.setRowReturnable(false);
                    GecEntryHelperServiceImpl.setFieldValue(row, KFSPropertyConstants.GEC_DOCUMENT_NUMBER, entryToDisable.getGecDocumentNumber());
                    LOG.debug("gecDocumentNumber=" + entryToDisable.getGecDocumentNumber());
                }
            }
        }
    }

}
