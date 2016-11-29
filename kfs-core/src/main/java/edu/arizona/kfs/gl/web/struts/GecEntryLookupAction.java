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
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import edu.arizona.kfs.gl.GeneralLedgerConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;


public class GecEntryLookupAction extends KualiMultipleValueLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GecEntryLookupAction.class);

    private ObjectCodeService objectCodeService;
    private ParameterEvaluatorService parameterEvaluatorService;
    private LookupService lookupService;
    private SystemOptions systemOptions;
    private BusinessObjectService businessObjectService;
    private OptionsService optionsService;
    private List<Bank> bankAccountList;


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


    @SuppressWarnings("deprecation") //ResultRow
    @Override
    protected Collection<Entry> performMultipleValueLookup(MultipleValueLookupForm multipleValueLookupForm, List<ResultRow> resultTable, int maxRowsPerPage, boolean bounded) {
        super.performMultipleValueLookup(multipleValueLookupForm, resultTable, maxRowsPerPage, bounded);
        Collection<Entry> list = filterResults(multipleValueLookupForm, resultTable, maxRowsPerPage);
        return list;
    }


    @SuppressWarnings("deprecation") //ResultRow
    @Override
    protected List<ResultRow> selectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = super.selectAll(multipleValueLookupForm, maxRowsPerPage);
        filterResults(multipleValueLookupForm, resultTable, maxRowsPerPage);
        return resultTable;
    }


    @SuppressWarnings("deprecation") //ResultRow
    @Override
    protected List<ResultRow> unselectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = super.unselectAll(multipleValueLookupForm, maxRowsPerPage);
        filterResults(multipleValueLookupForm, resultTable, maxRowsPerPage);
        return resultTable;
    }


    @SuppressWarnings("deprecation") //ResultRow
    @Override
    protected List<ResultRow> switchToPage(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = super.switchToPage(multipleValueLookupForm, maxRowsPerPage);
        filterResults(multipleValueLookupForm, resultTable, maxRowsPerPage);
        return resultTable;
    }


    @SuppressWarnings("deprecation") //ResultRow
    @Override
    protected List<ResultRow> sort(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = super.sort(multipleValueLookupForm, maxRowsPerPage);
        filterResults(multipleValueLookupForm, resultTable, maxRowsPerPage);
        return resultTable;
    }


    @SuppressWarnings("deprecation") //ResultRow
    private Collection<Entry> filterResults(MultipleValueLookupForm multipleValueLookupForm, List<ResultRow> resultTable, int maxRowsPerPage) {
        Collection<? extends BusinessObject> c = multipleValueLookupForm.getLookupable().performLookup(multipleValueLookupForm, new ArrayList<ResultRow>(), true);
        if (c == null || c.isEmpty()) {
            LOG.debug("No results found.");
            return new ArrayList<Entry>();
        }

        @SuppressWarnings("unchecked") // Lookupable's  Collection<BusinessObject>c
        List<Entry> entries = new ArrayList<Entry>((Collection<Entry>)c);
        List<Entry> entriesToRemove = new ArrayList<Entry>();
        List<String> entriesToDisable = new ArrayList<String>();

        // Separate entries to those that should be removed or disaabled
        for(Entry e : entries){
            if (shouldRemoveEntry(e)) {
                entriesToRemove.add(e);
            } else if (shoulDisableEntry(e)) {
                entriesToDisable.add(e.getObjectId());
            }
        }

        // Do actual removals
        LOG.debug("Removing " + entriesToRemove.size() + "entries.");
        for (Entry entry : entriesToRemove) {
            removeRecord(entry, resultTable, c);
            multipleValueLookupForm.getCompositeObjectIdMap().remove(entry.getObjectId());
        }

        // Do actual disables
        LOG.debug("Disabling " + entriesToDisable.size() + "entries.");
        for (ResultRow resultRow : resultTable) {
            if (entriesToDisable.contains(resultRow.getObjectId())) {
                resultRow.setReturnUrl(StringUtils.EMPTY);
                resultRow.setRowReturnable(false);
                multipleValueLookupForm.getCompositeObjectIdMap().remove(resultRow.getObjectId());
            }
        }

        // Set back to jump user was on, with the column sort they had last(if any)
        multipleValueLookupForm.jumpToPage(multipleValueLookupForm.getViewedPageNumber(), resultTable.size(), maxRowsPerPage);
        if (multipleValueLookupForm.getPreviouslySortedColumnIndex() != null) {
            multipleValueLookupForm.setColumnToSortIndex(Integer.parseInt(multipleValueLookupForm.getPreviouslySortedColumnIndex()));
        }

        return entries;
    }


    // Short circuit on first disqualification to save time
    private boolean shouldRemoveEntry(Entry entry) {
        LOG.debug("Determining if entry should be removed: " + entry.toString());

        if (!areLookupFieldsValid(entry)) {
            // Valid Entry lookup criteria
            LOG.debug("Entry not valid by the lookup field values specifications.");
            return true;
        } else if (!areParametersValid(entry)) {
            // Parameter Based Validation
            LOG.debug("Entry not valid by the parameters");
            return true;
        } else if (isOffset(entry)) {
            // Exclude offset entries.
            LOG.debug("Entry not valid because it is an offset.");
            return true;
        }

        return false;
    }


    // Short circuit on first disqualification to save time
    private boolean areLookupFieldsValid(Entry entry) {
        if (!isFiscalYearValid(entry)) {
            LOG.debug("Fiscal Year not valid per specifications.");
            return false;
        } else if (!getSystemOptions().getActualFinancialBalanceTypeCd().equals(entry.getFinancialBalanceTypeCode())) {
            LOG.debug("Balance Type Code not valid per specifications.");
            return false;
        } else if (entry.getTransactionLedgerEntryAmount().isZero()) {
            LOG.debug("Entry Amount not valid per specifications.");
            return false;
        }

        return true;
    }


    // Short circuit on first disqualification to save time
    private boolean areParametersValid(Entry entry) {
        if (!isObjectTypeValid(entry)) {
            LOG.debug("Object Type is invalid");
            return false;
        } else if (!isObjectSubTypeValid(entry)) {
            LOG.debug("Object Sub-Type is invalid");
            return false;
        } else if (!isObjectTypeValidBySubType(entry)) {
            LOG.debug("Object Type by Sub Type is invalid");
            return false;
        } else if (!isDocumentTypeValid(entry)) {
            LOG.debug("Document Type is invalid");
            return false;
        } else if (!isOriginationCodeValid(entry)) {
            LOG.debug("Origination Code is invalid");
            return false;
        }

        return true;
    }


    protected boolean isFiscalYearValid(Entry entry) {
        return getSystemOptions().getUniversityFiscalYear().toString().equals(entry.getUniversityFiscalYear().toString());
    }


    private boolean isObjectTypeValid(Entry entry) {
        return getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.RESTRICTED_OBJECT_TYPE_CODES, entry.getFinancialObjectTypeCode()).evaluationSucceeds();
    }


    private boolean isObjectSubTypeValid(Entry entry) {
        @SuppressWarnings("deprecation")//ObjectCode
        ObjectCode code = getObjectCodeService().getByPrimaryId(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialObjectCode());
        return getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.RESTRICTED_OBJECT_SUB_TYPE_CODES, code.getFinancialObjectSubTypeCode()).evaluationSucceeds();
    }


    private boolean isObjectTypeValidBySubType(Entry entry) {
        @SuppressWarnings("deprecation")//ObjectCode
        ObjectCode code = getObjectCodeService().getByPrimaryId(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialObjectCode());
        return getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE, code.getFinancialObjectTypeCode(), code.getFinancialObjectSubTypeCode()).evaluationSucceeds();
    }


    private boolean isDocumentTypeValid(Entry entry) {
        return getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.DOCUMENT_TYPES, entry.getFinancialDocumentTypeCode()).evaluationSucceeds();
    }


    private boolean isOriginationCodeValid(Entry entry) {
        return getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.ORIGIN_CODES, entry.getFinancialSystemOriginationCode()).evaluationSucceeds();
    }


    private boolean isOffset(Entry entry) {
        if (isOffsetDefinition(entry)) {
            LOG.debug("Entry is Offset Definition");
            return true;
        } else if (isOffsetAccountEntry(entry)) {
            LOG.debug("Entry has Offset Account");
            return true;
        }

        return false;
    }


    private boolean isOffsetDefinition(Entry entry) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, entry.getUniversityFiscalYear().toString());
        primaryKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, entry.getChartOfAccountsCode());
        primaryKeys.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, entry.getFinancialDocumentTypeCode());
        primaryKeys.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, entry.getFinancialBalanceTypeCode());

        OffsetDefinition offsetDefinition = getBusinessObjectService().findByPrimaryKey(OffsetDefinition.class, primaryKeys);
        if (offsetDefinition == null || offsetDefinition.getFinancialObjectCode() == null || entry.getFinancialObjectCode() == null) {
            return false;
        } else if (offsetDefinition.getFinancialObjectCode().equals(entry.getFinancialObjectCode())) {
            return true;
        }

        return false;
    }


    private boolean isOffsetAccountEntry(Entry entry) {
        for (Bank bankAccount : getBankAccountList()) {
            if (bankAccount.getBankAccountNumber().equals(entry.getAccountNumber())) {
                return true;
            }
        }
        return false;
    }


    /*
     * Currently, the sole reason to disable, is if the entry is already associated
     * with another active GEC doc. When a GEC doc goes through route status change,
     * GeneralErrorCorrectionDocument.doRouteStatusChange() handles setting all associated
     * entry.gecDocumentNumber accordingly. So here, if we see a GEC number, it means
     * the association is still active, and we should disable selection for this GEC.
     */
    private boolean shoulDisableEntry(Entry entry) {
        LOG.debug("Determining if entry should be disabled: " + entry.toString());

        String gecDocumentNumber = entry.getGecDocumentNumber();
        if (StringUtils.isNotBlank(gecDocumentNumber)) {
            // This entry is associtated with a GEC document, disable it
            LOG.debug("Disabling Entry selection, already associated with active GEC: (entryId, gecDocNumber): (" + entry.getEntryId().toString() + ", " + entry.getGecDocumentNumber() + ")");
            return true;
        }

        LOG.debug("Entry not associated with GEC, not be disabling.");
        return false;
    }


    @SuppressWarnings("deprecation") //ResultRow
    private void removeRecord(Entry entry, List<ResultRow> resultTable, Collection c) {
        if (c != null) {
            c.remove(entry);
        }
        Iterator<ResultRow> iter = resultTable.iterator();
        while (iter.hasNext()) {
            String objectId = iter.next().getObjectId();
            if (objectId != null && objectId.equals(entry.getObjectId())) {
                iter.remove();
            }
        }
    }


    private List<Bank> getBankAccountList() {
        if (bankAccountList == null) {
            bankAccountList = (List<Bank>) getLookupService().findCollectionBySearchUnbounded(Bank.class, new HashMap<String, String>());
        }
        return bankAccountList;
    }


    private ParameterEvaluatorService getParameterEvaluatorService() {
        if (parameterEvaluatorService == null) {
            parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        }
        return parameterEvaluatorService;
    }


    private ObjectCodeService getObjectCodeService() {
        if (objectCodeService == null) {
            objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        }
        return objectCodeService;
    }


    private LookupService getLookupService() {
        if (lookupService == null) {
            lookupService = SpringContext.getBean(LookupService.class);
        }
        return lookupService;
    }


    protected SystemOptions getSystemOptions() {
        if (systemOptions == null) {
            systemOptions = getOptionsService().getCurrentYearOptions();
        }
        return systemOptions;
    }


    private OptionsService getOptionsService() {
        if (optionsService == null) {
            optionsService = SpringContext.getBean(OptionsService.class);
        }
        return optionsService;
    }


    private BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

}
