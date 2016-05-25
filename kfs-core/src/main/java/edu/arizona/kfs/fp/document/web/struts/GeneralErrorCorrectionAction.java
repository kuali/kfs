package edu.arizona.kfs.fp.document.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.service.SegmentedLookupResultsService;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import edu.arizona.kfs.gl.businessobject.Entry;
import edu.arizona.kfs.sys.KFSConstants;

/**
 * This class is the UA modification of the GeneralErrorCorrectionAction.
 *
 * @author Adam Kost <kosta@email.arizona.edu> with some code adapted from UCI
 */
@SuppressWarnings("deprecation")
public class GeneralErrorCorrectionAction extends org.kuali.kfs.fp.document.web.struts.GeneralErrorCorrectionAction {

    private static transient volatile SegmentedLookupResultsService segmentedLookupResultsService;
    private static transient volatile DebitDeterminerService debitDeterminerService;
    private static transient volatile BusinessObjectService businessObjectService;

    private static SegmentedLookupResultsService getSegmentedLookupResultsService() {
        if (segmentedLookupResultsService == null) {
            segmentedLookupResultsService = SpringContext.getBean(SegmentedLookupResultsService.class);
        }
        return segmentedLookupResultsService;
    }

    protected static DebitDeterminerService getDebitDeterminerService() {
        if (debitDeterminerService == null) {
            debitDeterminerService = SpringContext.getBean(DebitDeterminerService.class);
        }
        return debitDeterminerService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);

        // determine what the action path is
        String actionPath = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM4_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM4_RIGHT_DEL);
        if (StringUtils.isBlank(actionPath)) {
            return super.performLookup(mapping, form, request, response);
        }

        GeneralErrorCorrectionForm financialDocumentForm = (GeneralErrorCorrectionForm) form;

        // when we return from the lookup, our next request's method to call is going to be refresh
        financialDocumentForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);

        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform lookup, no business object class name specified.");
        }

        // build the parameters for the lookup url
        Properties parameters = new Properties();
        String conversionFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        if (StringUtils.isNotBlank(conversionFields)) {
            parameters.put(KFSConstants.CONVERSION_FIELDS_PARAMETER, conversionFields);
        }

        // pass values from form that should be pre-populated on lookup search
        String parameterFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if (StringUtils.isNotBlank(parameterFields)) {
            String[] lookupParams = parameterFields.split(KFSConstants.FIELD_CONVERSIONS_SEPERATOR);

            for (int i = 0; i < lookupParams.length; i++) {
                String[] keyValue = lookupParams[i].split(KFSConstants.FIELD_CONVERSION_PAIR_SEPERATOR);

                // hard-coded passed value
                if (StringUtils.contains(keyValue[0], KRADConstants.SINGLE_QUOTE)) {
                    parameters.put(keyValue[1], StringUtils.replace(keyValue[0], KRADConstants.SINGLE_QUOTE, KRADConstants.EMPTY_STRING));
                }
                // passed value should come from property
                else if (StringUtils.isNotBlank(request.getParameter(keyValue[0]))) {
                    parameters.put(keyValue[1], request.getParameter(keyValue[0]));
                }
            }
        }

        // grab whether or not the "return value" link should be hidden or not
        String hideReturnLink = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM3_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL);
        if (StringUtils.isNotBlank(hideReturnLink)) {
            parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, hideReturnLink);
        }

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(KFSConstants.LOOKUP_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // now add required parameters
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form));
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);
        parameters.put(KFSConstants.RETURN_LOCATION_PARAMETER, basePath + mapping.getPath() + KFSConstants.ACTION_EXTENSION_DOT_DO);

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + KFSConstants.PATH_SEPERATOR + actionPath, parameters);

        return new ActionForward(lookupUrl, true);
    }

    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        GeneralErrorCorrectionForm gecForm = (GeneralErrorCorrectionForm) form;
        Collection<Entry> rawValues = null;

        // If multiple asset lookup was used to select the assets, then....
        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, gecForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = gecForm.getLookupResultsSequenceNumber();

            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                // actually returning from a multiple value lookup
                Set<String> entryIds = getSegmentedLookupResultsService().retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());

                // Retrieving selected data from table.
                rawValues = retrieveSelectedResultBOs(entryIds);

                if (rawValues == null || rawValues.isEmpty()) {
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }

                for (Entry entry : rawValues) {
                    SourceAccountingLine line = copyEntryToAccountingLine(entry);
                    insertAccountingLine(true, (KualiAccountingDocumentFormBase) form, line);
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    private SourceAccountingLine copyEntryToAccountingLine(Entry entry) {
        SourceAccountingLine retval = new SourceAccountingLine();
        retval.setChartOfAccountsCode(entry.getChartOfAccountsCode());
        retval.setAccountNumber(entry.getAccountNumber());
        retval.setSubAccountNumber(entry.getSubAccountNumber());
        retval.setFinancialObjectCode(entry.getFinancialObjectCode());
        retval.setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        retval.setProjectCode(entry.getProjectCode());
        retval.setOrganizationReferenceId(entry.getOrganizationReferenceId());
        retval.setAmount(entry.getTransactionLedgerEntryAmount());
        retval.setReferenceOriginCode(entry.getFinancialSystemOriginationCode());
        retval.setReferenceNumber(entry.getDocumentNumber());
        retval.setFinancialDocumentLineDescription(entry.getTransactionLedgerEntryDescription());
        retval.setDebitCreditCode(entry.getTransactionDebitCreditCode());
        retval.setBalanceTypeCode(entry.getFinancialBalanceTypeCode());
        return retval;
    }

    /**
     * Custom retrieve for Entry - using entryId instead of objectId since Entry does not have an objectId
     *
     * @param setOfSelectedEntryIds
     * @return
     */
    private Collection<Entry> retrieveSelectedResultBOs(Set<String> setOfSelectedEntryIds) {
        ArrayList<Entry> retvals = new ArrayList<Entry>();

        for (String selectedEntryId : setOfSelectedEntryIds) {
            if (selectedEntryId == null || selectedEntryId.equals(KFSConstants.NULL_STRING)) {
                continue;
            }

            Entry result = Entry.getEntry(selectedEntryId);
            if (result != null) {
                retvals.add(result);
            }
        }

        return retvals;
    }

}
