/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.gl.web.struts.action;

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
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.LookupResultsService;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.SequenceAccessorService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiMultipleValueLookupAction;
import org.kuali.core.web.struts.form.MultipleValueLookupForm;
import org.kuali.core.web.ui.Column;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.util.KFSUtils;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.web.lookupable.AccountBalanceByConsolidationLookupableHelperServiceImpl;
import org.kuali.module.gl.web.struts.form.BalanceInquiryLookupForm;
import org.kuali.module.integration.bo.SegmentedBusinessObject;
import org.kuali.rice.KNSServiceLocator;

/**
 * Balance inquiries are pretty much just lookups already, but are not used in the traditional sense. In most cases, balance
 * inquiries only show the end-user data, and allow the end-user to drill-down into inquiries. A traditional lookup allows the user
 * to return data to a form. This class is for balance inquiries implemented in the sense of a traditional lookup for forms that
 * pull data out of inquiries.<br/> <br/> One example of this is the
 * <code>{@link org.kuali.module.labor.document.SalaryExpenseTransferDocument}</code> which creates source lines from a labor
 * ledger balance inquiry screen.<br/> <br/> This is a <code>{@link KualiMultipleValueLookupAction}</code> which required some
 * customization because requirements were not possible with displaytag.
 * 
 * @see org.kuali.module.labor.document.SalaryExpenseTransferDocument
 * @see org.kuali.module.labor.web.struts.action.SalaryExpenseTransferAction;
 * @see org.kuali.module.labor.web.struts.form.SalaryExpenseTransferForm;
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

    private KualiConfigurationService kualiConfigurationService;
    private String[] totalTitles;

    public BalanceInquiryLookupAction() {
        super();
        kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
    }

    private void setTotalTitles() {
        totalTitles = new String[7];

        totalTitles[0] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.INCOME);
        totalTitles[1] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.INCOME_FROM_TRANSFERS);
        totalTitles[2] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.INCOME_TOTAL);
        totalTitles[3] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.EXPENSE);
        totalTitles[4] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.EXPENSE_FROM_TRANSFERS);
        totalTitles[5] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.EXPENSE_TOTAL);
        totalTitles[6] = kualiConfigurationService.getPropertyString(KFSKeyConstants.AccountBalanceService.TOTAL);

    }

    private String[] getTotalTitles() {
        if (null == totalTitles) {
            setTotalTitles();
        }

        return totalTitles;
    }

    /**
     * search - sets the values of the data entered on the form on the jsp into a map and then searches for the results.
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
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSKeyConstants.ERROR_CUSTOM, new String[] { "Fiscal Year must be a four-digit number" });
            }
            catch (Exception e) {
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_CUSTOM, new String[] { "Please report the server error." });
                LOG.error("Application Errors", e);
            }
        }

        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, totalSize);
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, resultTable);
        lookupForm.setResultsActualSize((int) totalSize.longValue());
        lookupForm.setResultsLimitedSize(resultTable.size());

        if (lookupForm.isSegmented()) {
            LOG.debug("I'm segmented");
            request.setAttribute(GLConstants.LookupableBeanKeys.SEGMENTED_LOOKUP_FLAG_NAME, Boolean.TRUE);
        }

        if (request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY) != null) {
            GlobalVariables.getUserSession().removeObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
            request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObject(resultTable));
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
     * @see org.kuali.core.web.struts.action.KualiMultipleValueLookupAction#sort(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward sort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(GLConstants.LookupableBeanKeys.SEGMENTED_LOOKUP_FLAG_NAME, Boolean.TRUE);
        return super.sort(mapping, form, request, response);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiMultipleValueLookupAction#selectAll(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward selectAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(GLConstants.LookupableBeanKeys.SEGMENTED_LOOKUP_FLAG_NAME, Boolean.TRUE);
        return super.selectAll(mapping, form, request, response);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiMultipleValueLookupAction#unselectAll(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward unselectAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(GLConstants.LookupableBeanKeys.SEGMENTED_LOOKUP_FLAG_NAME, Boolean.TRUE);
        return super.unselectAll(mapping, form, request, response);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiMultipleValueLookupAction#switchToPage(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward switchToPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(GLConstants.LookupableBeanKeys.SEGMENTED_LOOKUP_FLAG_NAME, Boolean.TRUE);
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
        String lookupResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KFSConstants.LOOKUP_RESULTS_SEQUENCE));
        multipleValueLookupForm.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        try {
            LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
            lookupResultsService.persistResultsTable(lookupResultsSequenceNumber, resultTable, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
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
     * @see org.kuali.core.web.struts.action.KualiMultipleValueLookupAction#selectAll(org.kuali.core.web.struts.form.MultipleValueLookupForm,
     *      int)
     */
    @Override
    protected List<ResultRow> selectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = null;
        try {
            LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();
            String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();

            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
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
                        String objectId = row.getObjectId() + "." + propertyName + "." + KFSUtils.convertDecimalIntoInteger(amount);
                        selectedObjectIds.put(objectId, objectId);
                    }
                }
            }
        }

        return selectedObjectIds;
    }
}
