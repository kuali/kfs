/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.ObjectHelper;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.lookup.AccountBalanceByConsolidationLookupableHelperServiceImpl;
import org.kuali.kfs.integration.ld.SegmentedBusinessObject;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Balance inquiries are pretty much just lookups already, but are not used in the traditional sense. In most cases, balance
 * inquiries only show the end-user data, and allow the end-user to drill-down into inquiries. A traditional lookup allows the user
 * to return data to a form. This class is for balance inquiries implemented in the sense of a traditional lookup for forms that
 * pull data out of inquiries.<br/> <br/> One example of this is the
 * <code>{@link org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument}</code> which creates source lines from a labor
 * ledger balance inquiry screen.<br/> <br/> This is a <code>{@link KualiMultipleValueLookupAction}</code> which required some
 * customization because requirements were not possible with displaytag.
 * 
 * @see org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument
 * @see org.kuali.kfs.module.ld.document.web.struts.SalaryExpenseTransferAction;
 * @see org.kuali.kfs.module.ld.document.web.struts.SalaryExpenseTransferForm;
 */
public class BalanceInquiryLookupAction extends KualiMultipleValueLookupAction {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BalanceInquiryLookupAction.class);

    private static final String TOTALS_TABLE_KEY = "totalsTable";

    /**
     * If there is no app param defined for the # rows/page, then this value will be used for the default
     * 
     * @see KualiMultipleValueLookupAction#getMaxRowsPerPage(MultipleValueLookupForm)
     */
    public static final int DEFAULT_MAX_ROWS_PER_PAGE = 50;

    private ConfigurationService kualiConfigurationService;
    private String[] totalTitles;

    public BalanceInquiryLookupAction() {
        super();
        kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
    }

    private void setTotalTitles() {
        totalTitles = new String[7];

        totalTitles[0] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME);
        totalTitles[1] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME_FROM_TRANSFERS);
        totalTitles[2] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.INCOME_TOTAL);
        totalTitles[3] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE);
        totalTitles[4] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE_FROM_TRANSFERS);
        totalTitles[5] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.EXPENSE_TOTAL);
        totalTitles[6] = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.AccountBalanceService.TOTAL);

    }

    private String[] getTotalTitles() {
        if (null == totalTitles) {
            setTotalTitles();
        }

        return totalTitles;
    }

    /**
     * search - sets the values of the data entered on the form on the jsp into a map and then searches for the results.
     * 
     * KRAD Conversion: Lookupable performs customization of the results if 
     * balance inquiry. The result rows are added to a collection 
     * based on field's actual size if truncated is > 7.
     * 
     * Fields are in data dictionary for bo Balance.
     */
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BalanceInquiryLookupForm lookupForm = (BalanceInquiryLookupForm) form;
        Lookupable lookupable = lookupForm.getLookupable();

        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Collection displayList = new ArrayList();
        CollectionIncomplete incompleteDisplayList;
        List<ResultRow> resultTable = new ArrayList<ResultRow>();
        Long totalSize;
        boolean bounded = true;

        lookupable.validateSearchParameters(lookupForm.getFields());

        displayList = performMultipleValueLookup(lookupForm, resultTable, getMaxRowsPerPage(lookupForm), bounded);
        incompleteDisplayList = (CollectionIncomplete) displayList;
        totalSize = incompleteDisplayList.getActualSizeIfTruncated();

        if (lookupable.isSearchUsingOnlyPrimaryKeyValues()) {
            lookupForm.setSearchUsingOnlyPrimaryKeyValues(true);
            lookupForm.setPrimaryKeyFieldLabels(lookupable.getPrimaryKeyFieldLabels());
        }
        else {
            lookupForm.setSearchUsingOnlyPrimaryKeyValues(false);
            lookupForm.setPrimaryKeyFieldLabels(KFSConstants.EMPTY_STRING);
        }


        // TODO: use inheritance instead of this if statement
        if (lookupable.getLookupableHelperService() instanceof AccountBalanceByConsolidationLookupableHelperServiceImpl) {
            Object[] resultTableAsArray = resultTable.toArray();
            Collection totalsTable = new ArrayList();

            int arrayIndex = 0;

            try {
                for (int listIndex = 0; listIndex < incompleteDisplayList.size(); listIndex++) {
                    AccountBalance balance = (AccountBalance) incompleteDisplayList.get(listIndex);
                    boolean ok = ObjectHelper.isOneOf(balance.getTitle(), getTotalTitles());
                    if (ok) {
                        if (totalSize > 7) {
                            totalsTable.add(resultTableAsArray[arrayIndex]);
                        }
                        resultTable.remove(resultTableAsArray[arrayIndex]);
                        incompleteDisplayList.remove(balance);
                    }
                    arrayIndex++;
                }

                request.setAttribute(TOTALS_TABLE_KEY, totalsTable);
                GlobalVariables.getUserSession().addObject(TOTALS_TABLE_KEY, totalsTable);
            }
            catch (NumberFormatException e) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSKeyConstants.ERROR_CUSTOM, new String[] { "Fiscal Year must be a four-digit number" });
            }
            catch (Exception e) {
                GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_CUSTOM, new String[] { "Please report the server error." });
                LOG.error("Application Errors", e);
            }
        }

        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, totalSize);
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, resultTable);
        lookupForm.setResultsActualSize((int) totalSize.longValue());
        lookupForm.setResultsLimitedSize(resultTable.size());

        if (lookupForm.isSegmented()) {
            LOG.debug("I'm segmented");
            request.setAttribute(GeneralLedgerConstants.LookupableBeanKeys.SEGMENTED_LOOKUP_FLAG_NAME, Boolean.TRUE);
        }

        if (request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY) != null) {
            GlobalVariables.getUserSession().removeObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
            request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(resultTable));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method returns none of the selected results and redirects back to the lookup caller.
     * 
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward prepareToReturnNone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        prepareToReturnNone(multipleValueLookupForm);

        // build the parameters for the refresh url
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DOC_FORM_KEY, multipleValueLookupForm.getFormKey());
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KFSConstants.REFRESH_CALLER, KFSConstants.MULTIPLE_VALUE);
        parameters.put(KFSConstants.ANCHOR, multipleValueLookupForm.getLookupAnchor());

        String backUrl = UrlFactory.parameterizeUrl(multipleValueLookupForm.getBackLocation(), parameters);
        return new ActionForward(backUrl, true);
    }

    /**
     * This method does the processing necessary to return selected results and sends a redirect back to the lookup caller
     * 
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward prepareToReturnSelectedResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        if (StringUtils.isBlank(multipleValueLookupForm.getLookupResultsSequenceNumber())) {
            // no search was executed
            return prepareToReturnNone(mapping, form, request, response);
        }

        prepareToReturnSelectedResultBOs(multipleValueLookupForm);

        // build the parameters for the refresh url
        Properties parameters = new Properties();
        parameters.put(KFSConstants.LOOKUP_RESULTS_BO_CLASS_NAME, multipleValueLookupForm.getBusinessObjectClassName());
        parameters.put(KFSConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, multipleValueLookupForm.getLookupResultsSequenceNumber());
        parameters.put(KFSConstants.DOC_FORM_KEY, multipleValueLookupForm.getFormKey());
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KFSConstants.REFRESH_CALLER, KFSConstants.MULTIPLE_VALUE);
        parameters.put(KFSConstants.ANCHOR, multipleValueLookupForm.getLookupAnchor());
        String backUrl = UrlFactory.parameterizeUrl(multipleValueLookupForm.getBackLocation(), parameters);
        return new ActionForward(backUrl, true);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction#sort(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward sort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(GeneralLedgerConstants.LookupableBeanKeys.SEGMENTED_LOOKUP_FLAG_NAME, Boolean.TRUE);
        return super.sort(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction#selectAll(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward selectAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(GeneralLedgerConstants.LookupableBeanKeys.SEGMENTED_LOOKUP_FLAG_NAME, Boolean.TRUE);
        return super.selectAll(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction#unselectAll(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward unselectAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(GeneralLedgerConstants.LookupableBeanKeys.SEGMENTED_LOOKUP_FLAG_NAME, Boolean.TRUE);
        return super.unselectAll(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction#switchToPage(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward switchToPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(GeneralLedgerConstants.LookupableBeanKeys.SEGMENTED_LOOKUP_FLAG_NAME, Boolean.TRUE);
        return super.switchToPage(mapping, form, request, response);
    }

    /**
     * This method performs the lookup and returns a collection of lookup items. Also initializes values in the form that will allow
     * the multiple value lookup page to render
     * 
     * @param multipleValueLookupForm
     * @param resultTable a list of result rows (used to generate what's shown in the UI). This list will be modified by this method
     * @param maxRowsPerPage
     * @param bounded whether the results will be bounded
     * @return the list of result BOs, possibly bounded by size
     * 
     * KRAD Conversion: Lookupable performs customization of the results if 
     * balance inquiry. The multiple value results are sorted.
     * 
     * Fields are in data dictionary for bo Balance.
     */
    protected Collection performMultipleValueLookup(MultipleValueLookupForm multipleValueLookupForm, List<ResultRow> resultTable, int maxRowsPerPage, boolean bounded) {
        Lookupable lookupable = multipleValueLookupForm.getLookupable();
        Collection displayList = lookupable.performLookup(multipleValueLookupForm, resultTable, bounded);

        List defaultSortColumns = lookupable.getDefaultSortColumns();
        if (defaultSortColumns != null && !defaultSortColumns.isEmpty() && resultTable != null && !resultTable.isEmpty()) {
            // there's a default sort order, just find the first sort column, and we can't go wrong
            String firstSortColumn = (String) defaultSortColumns.get(0);

            // go thru the first result row to find the index of the column (more efficient than calling lookupable.getColumns since
            // we don't have to recreate column list)
            int firstSortColumnIdx = -1;
            List<Column> columnsForFirstResultRow = resultTable.get(0).getColumns();
            for (int i = 0; i < columnsForFirstResultRow.size(); i++) {
                if (StringUtils.equals(firstSortColumn, columnsForFirstResultRow.get(i).getPropertyName())) {
                    firstSortColumnIdx = i;
                    break;
                }
            }
            multipleValueLookupForm.setColumnToSortIndex(firstSortColumnIdx);
        }
        else {
            // don't know how results were sorted, so we just say -1
            multipleValueLookupForm.setColumnToSortIndex(-1);
        }

        // we just performed the lookup, so we're on the first page (indexed from 0)
        multipleValueLookupForm.jumpToFirstPage(resultTable.size(), maxRowsPerPage);

        SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
        String lookupResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE));
        multipleValueLookupForm.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        try {
            LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
            lookupResultsService.persistResultsTable(lookupResultsSequenceNumber, resultTable, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to persist multiple lookup results", e);
            throw new RuntimeException("error occured trying to persist multiple lookup results");
        }

        // since new search, nothing's checked
        multipleValueLookupForm.setCompositeObjectIdMap(new HashMap<String, String>());

        return displayList;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction#selectAll(org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm,
     *      int)
     * 
     * KRAD Conversion: Lookupable performs customization of the results.
     * 
     * Fields are in data dictionary for bo Balance.
     */
    @Override
    protected List<ResultRow> selectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = null;
        try {
            LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
            String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();

            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to export multiple lookup results", e);
            throw new RuntimeException("error occured trying to export multiple lookup results");
        }

        Map<String, String> selectedObjectIds = this.getSelectedObjectIds(multipleValueLookupForm, resultTable);

        multipleValueLookupForm.jumpToPage(multipleValueLookupForm.getViewedPageNumber(), resultTable.size(), maxRowsPerPage);
        multipleValueLookupForm.setColumnToSortIndex(Integer.parseInt(multipleValueLookupForm.getPreviouslySortedColumnIndex()));
        multipleValueLookupForm.setCompositeObjectIdMap(selectedObjectIds);

        return resultTable;
    }

    /**
     * put all enties into select object map. This implmentation only deals with the money amount objects.
     * 
     * @param multipleValueLookupForm the given struts form
     * @param resultTable the given result table that holds all data being presented
     * @return the map containing all entries available for selection
     * 
     * KRAD Conversion: Performs customization of the results. Prepares
     * 
     * There is no use of data dictionary for fields.
     */
    private Map<String, String> getSelectedObjectIds(MultipleValueLookupForm multipleValueLookupForm, List<ResultRow> resultTable) {
        String businessObjectClassName = multipleValueLookupForm.getBusinessObjectClassName();
        SegmentedBusinessObject segmentedBusinessObject;
        try {
            segmentedBusinessObject = (SegmentedBusinessObject) Class.forName(multipleValueLookupForm.getBusinessObjectClassName()).newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Fail to create an object of " + businessObjectClassName + e);
        }

        Map<String, String> selectedObjectIds = new HashMap<String, String>();
        Collection<String> segmentedPropertyNames = segmentedBusinessObject.getSegmentedPropertyNames();
        for (ResultRow row : resultTable) {
            for (Column column : row.getColumns()) {
                String propertyName = column.getPropertyName();
                if (segmentedPropertyNames.contains(propertyName)) {
                    String propertyValue = StringUtils.replace(column.getPropertyValue(), ",", "");
                    KualiDecimal amount = new KualiDecimal(propertyValue);

                    if (amount.isNonZero()) {
                        String objectId = row.getObjectId() + "." + propertyName + "." + KRADUtils.convertDecimalIntoInteger(amount);
                        selectedObjectIds.put(objectId, objectId);
                    }
                }
            }
        }

        return selectedObjectIds;
    }
}

