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

import edu.arizona.kfs.fp.document.validation.impl.GeneralErrorCorrectionDocumentRuleConstants;
import edu.arizona.kfs.gl.businessobject.EntryLiteBo;
import edu.arizona.kfs.gl.businessobject.lookup.EntryLiteBoHelperServiceImpl;
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

    private static ParameterEvaluatorService getParameterEvaluatorService() {
        if (parameterEvaluatorService == null) {
            parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        }
        return parameterEvaluatorService;
    }

    private static ObjectCodeService getObjectCodeService() {
        if (objectCodeService == null) {
            objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        }
        return objectCodeService;
    }

    private static LookupService getLookupService() {
        if (lookupService == null) {
            lookupService = SpringContext.getBean(LookupService.class);
        }
        return lookupService;
    }

    private static SystemOptions getSystemOptions() {
        if (systemOptions == null) {
            systemOptions = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();
        }
        return systemOptions;
    }

    private static BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Collection<EntryLiteBo> performMultipleValueLookup(MultipleValueLookupForm multipleValueLookupForm, List<ResultRow> resultTable, int maxRowsPerPage, boolean bounded) {
        Collection<EntryLiteBo> c = super.performMultipleValueLookup(multipleValueLookupForm, resultTable, maxRowsPerPage, bounded);
        if (c == null || c.isEmpty()) {
            LOG.debug("No results found.");
            return new ArrayList<EntryLiteBo>();
        }

        List<EntryLiteBo> entries = new ArrayList<EntryLiteBo>(c);
        List<EntryLiteBo> entriesToRemove = new ArrayList<EntryLiteBo>();
        List<EntryLiteBo> entriesToDisable = new ArrayList<EntryLiteBo>();

        for (EntryLiteBo e : entries) {
            boolean removeEntry = removeEntry(e);
            boolean disableEntry = disableEntry(e);
            if (removeEntry) {
                entriesToRemove.add(e);
            }
            if (disableEntry) {
                entriesToDisable.add(e);
            }
        }

        removeRecords(entriesToRemove, entries, resultTable);
        disableRecords(entriesToDisable, resultTable, multipleValueLookupForm);
        EntryLiteBoHelperServiceImpl.addInquiryLinksToRecords(resultTable);
        multipleValueLookupForm.jumpToFirstPage(resultTable.size(), maxRowsPerPage);

        Map<String, String> displayedEntryIds = generateEntryIdMap(entries);
        multipleValueLookupForm.setCompositeObjectIdMap(displayedEntryIds);
        return entries;
    }

    private Map<String, String> generateEntryIdMap(List<EntryLiteBo> entries) {
        Map<String, String> retval = new HashMap<String, String>();
        for (EntryLiteBo entry : entries) {
            retval.put(entry.getEntryId(), entry.getEntryId());
        }
        return retval;
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
    private boolean removeEntry(EntryLiteBo entry) {
        LOG.debug("Determining if entry should be removed: " + entry.toString());
        ObjectCode code = getObjectCodeService().getByPrimaryId(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialObjectCode());

        // GEC Validation
        boolean objectTypeValid = getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralErrorCorrectionDocumentRuleConstants.RESTRICTED_OBJECT_TYPE_CODES, entry.getFinancialObjectTypeCode()).evaluationSucceeds();
        boolean objectTypeValidBySubType = getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralErrorCorrectionDocumentRuleConstants.VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE, GeneralErrorCorrectionDocumentRuleConstants.INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE, code.getFinancialObjectTypeCode(), code.getFinancialObjectSubTypeCode()).evaluationSucceeds();
        boolean objectSubTypeValid = getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralErrorCorrectionDocumentRuleConstants.RESTRICTED_OBJECT_SUB_TYPE_CODES, code.getFinancialObjectSubTypeCode()).evaluationSucceeds();
        boolean documentTypeValid = getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralErrorCorrectionDocumentRuleConstants.DOCUMENT_TYPES, entry.getFinancialDocumentTypeCode()).evaluationSucceeds();
        boolean originationCodeValid = getParameterEvaluatorService().getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralErrorCorrectionDocumentRuleConstants.ORIGIN_CODES, entry.getFinancialSystemOriginationCode()).evaluationSucceeds();

        if (!objectTypeValid || !objectTypeValidBySubType || !objectSubTypeValid || !documentTypeValid || !originationCodeValid) {
            LOG.debug("objectTypeValid=" + objectTypeValid + "; objectTypeValidBySubType=" + objectTypeValidBySubType + "; objectSubTypeValid=" + objectSubTypeValid + "; documentTypeValid=" + documentTypeValid + "; originationCodeValid=" + originationCodeValid);
            return true;
        }

        // Exclude offset entries.
        List<Bank> bankAccountList = (List<Bank>) getLookupService().findCollectionBySearchUnbounded(Bank.class, new HashMap<String, String>());
        boolean isOffsetDescription = KFSConstants.GL_PE_OFFSET_STRING.equalsIgnoreCase(entry.getTransactionLedgerEntryDescription());
        boolean isOffsetAccountEntry = isOffsetAccountEntry(bankAccountList, entry);
        if (isOffsetDescription || isOffsetAccountEntry) {
            LOG.debug("offsetDescription=[" + isOffsetDescription + ", " + entry.getTransactionLedgerEntryDescription() + "]; isOffsetAccountEntry=[" + isOffsetAccountEntry + ", " + entry.getAccountNumber() + "]");
            return true;
        }

        // Entry lookup criteria
        boolean balanceTypeValid = getSystemOptions().getActualFinancialBalanceTypeCd().equals(entry.getFinancialBalanceTypeCode());
        boolean entryAmoutIsZero = entry.getTransactionLedgerEntryAmount().isZero();
        if (!balanceTypeValid || entryAmoutIsZero) {
            LOG.debug("balanceTypeValid=" + balanceTypeValid + "; entryAmoutIsZero=" + entryAmoutIsZero);
            return true;
        }

        return false;
    }

    private boolean isOffsetAccountEntry(List<Bank> bankAccountList, EntryLiteBo entry) {
        for (Bank bankAccount : bankAccountList) {
            if (bankAccount.getBankAccountNumber().equals(entry.getAccountNumber())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Disable the entry selection based on custom logic
     *
     * @param entry
     * @return
     */
    private boolean disableEntry(EntryLiteBo entry) {
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

    private void removeRecords(List<EntryLiteBo> entriesToRemove, List<EntryLiteBo> entries, List<ResultRow> resultTable) {
        for (EntryLiteBo entryToRemove : entriesToRemove) {
            entries.remove(entryToRemove);
            Iterator<ResultRow> iter = resultTable.iterator();
            while (iter.hasNext()) {
                ResultRow currentRow = iter.next();
                boolean isSameEntry = EntryLiteBoHelperServiceImpl.compareEntryBoToRow(entryToRemove, currentRow);
                if (isSameEntry) {
                    iter.remove();
                }
            }
        }
    }

    private void disableRecords(List<EntryLiteBo> entriesToDisable, List<ResultRow> resultTable, MultipleValueLookupForm multipleValueLookupForm) {
        if (entriesToDisable.isEmpty()) {
            LOG.debug("entriesToRemove is Empty");
            return;
        }
        for (EntryLiteBo entryToDisable : entriesToDisable) {
            for (ResultRow row : resultTable) {
                boolean isSameEntry = EntryLiteBoHelperServiceImpl.compareEntryBoToRow(entryToDisable, row);
                if (isSameEntry) {
                    row.setReturnUrl(StringUtils.EMPTY);
                    row.setRowReturnable(false);
                    EntryLiteBoHelperServiceImpl.setFieldValue(row, KFSPropertyConstants.GEC_DOCUMENT_NUMBER, entryToDisable.getGecDocumentNumber());
                    LOG.debug("gecDocumentNumber=" + entryToDisable.getGecDocumentNumber());
                }
            }
        }
    }

}
