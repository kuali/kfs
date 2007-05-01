/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.struts.action.KualiLookupAction;
import org.kuali.core.web.struts.action.KualiTableRenderAction;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.web.lookupable.AccountBalanceByConsolidationLookupableHelperServiceImpl;
import org.kuali.module.gl.web.struts.form.BalanceInquiryLookupForm;

/**
 * Balance inquiries are pretty much just lookups already, but are not used in the traditional sense. In most
 * cases, balance inquiries only show the end-user data, and allow the end-user to drill-down into inquiries. A
 * traditional lookup allows the user to return data to a form. This class is for balance inquiries implemented
 * in the sense of a traditional lookup for forms that pull data out of inquiries.<br/>
 * <br/>
 * One example of this is the <code>{@link org.kuali.module.labor.document.SalaryExpenseTransferDocument}</code>
 * which creates source lines from a labor ledger balance inquiry screen.<br/>
 * <br/>
 * This is a <code>{@link KualiMultipleValueLookupAction}</code> which required some customization because requirements
 * were not possible with displaytag. 
 *
 * @see org.kuali.module.labor.document.SalaryExpenseTransferDocument
 * @see org.kuali.module.labor.web.struts.action.SalaryExpenseTransferAction;
 * @see org.kuali.module.labor.web.struts.form.SalaryExpenseTransferForm;
 */
public class BalanceInquiryLookupAction extends KualiLookupAction implements KualiTableRenderAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquiryAction.class);

    private static final String TOTALS_TABLE_KEY = "totalsTable";

    /**
     * If there is no app param defined for the # rows/page, then this value
     * will be used for the default
     * 
     * @see KualiMultipleValueLookupAction#getMaxRowsPerPage(MultipleValueLookupForm)
     */
    public static final int DEFAULT_MAX_ROWS_PER_PAGE = 50;

    private LookupDisplayTagSurrogate displayTagSurrogate;
    private KualiConfigurationService kualiConfigurationService;
    private String[] totalTitles;
    
    public BalanceInquiryLookupAction() {
        super();
        kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
        displayTagSurrogate = new BalanceInquiryLookupDisplayTagSurrogate();
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
     * Entry point to lookups, forwards to jsp for search render.
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * search - sets the values of the data entered on the form on the jsp into a map and then searches for the results.
     */
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BalanceInquiryLookupForm lookupForm = (BalanceInquiryLookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();

        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Collection displayList = new ArrayList();
        CollectionIncomplete incompleteDisplayList;
        List<ResultRow> resultTable = new ArrayList<ResultRow>();
        Long totalSize; 
        boolean bounded = true;

        kualiLookupable.validateSearchParameters(lookupForm.getFields());

        displayList = displayTagSurrogate.performMultipleValueLookup(lookupForm.getLookupResultsSelectable(), lookupForm, resultTable, bounded);
        incompleteDisplayList = (CollectionIncomplete) displayList;
        totalSize = incompleteDisplayList.getActualSizeIfTruncated();

        if (kualiLookupable.isSearchUsingOnlyPrimaryKeyValues()) {
            lookupForm.setSearchUsingOnlyPrimaryKeyValues(true);
            lookupForm.setPrimaryKeyFieldLabels(kualiLookupable.getPrimaryKeyFieldLabels());
        }
        else {
            lookupForm.setSearchUsingOnlyPrimaryKeyValues(false);
            lookupForm.setPrimaryKeyFieldLabels(KFSConstants.EMPTY_STRING);
        }

        request.setAttribute("reqSearchResultsActualSize", totalSize);
        request.setAttribute("reqSearchResults", resultTable);
        
        lookupForm.getLookupResultsSelectable().setResultsActualSize((int) totalSize.longValue());
        lookupForm.getLookupResultsSelectable().setResultsLimitedSize(resultTable.size());

        // TODO: use inheritance instead of this if statement
        if (kualiLookupable.getLookupableHelperService() instanceof AccountBalanceByConsolidationLookupableHelperServiceImpl) {
            Object[] resultTableAsArray = resultTable.toArray();
            Collection totalsTable = new ArrayList();
            
            int arrayIndex = 0;

            try {
                for (int listIndex = 0; listIndex < incompleteDisplayList.size();listIndex++) {
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
        
        if (request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY) != null) {
            GlobalVariables.getUserSession().removeObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
            request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, 
                                     GlobalVariables.getUserSession().addObject(resultTable));
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * refresh - is called when one quickFinder returns to the previous one. Sets all the values and performs the new search.
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Map fieldValues = new HashMap();
        Map values = lookupForm.getFields();

        for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();

            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();

                if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                    if (request.getParameter(field.getPropertyName()) != null) {
                        field.setPropertyValue(request.getParameter(field.getPropertyName()));
                    }
                    else if (values.get(field.getPropertyName()) != null) {
                        field.setPropertyValue(values.get(field.getPropertyName()));
                    }
                }
                fieldValues.put(field.getPropertyName(), field.getPropertyValue());
            }
        }
        fieldValues.put(KFSConstants.DOC_FORM_KEY, lookupForm.getFormKey());
        fieldValues.put(KFSConstants.BACK_LOCATION, lookupForm.getBackLocation());

        if (kualiLookupable.checkForAdditionalFields(fieldValues)) {
            for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
                Row row = (Row) iter.next();
                for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();
                    if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                        if (request.getParameter(field.getPropertyName()) != null) {
                            field.setPropertyValue(request.getParameter(field.getPropertyName()));
                            fieldValues.put(field.getPropertyName(), request.getParameter(field.getPropertyName()));
                        }
                        else if (values.get(field.getPropertyName()) != null) {
                            field.setPropertyValue(values.get(field.getPropertyName()));
                        }
                    }
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * clearValues - clears the values of all the fields on the jsp.
     */
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!field.getFieldType().equals(Field.RADIO)) {
                    field.setPropertyValue(field.getDefaultValue());
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, GlobalVariables.getUserSession().retrieveObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY)));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, request.getParameter(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE));

        // TODO: use inheritance instead of this if statement
        if (((BalanceInquiryLookupForm) form).getLookupable().getLookupableHelperService() instanceof AccountBalanceByConsolidationLookupableHelperServiceImpl) {
            Object totalsTable = GlobalVariables.getUserSession().retrieveObject(TOTALS_TABLE_KEY);
            request.setAttribute(TOTALS_TABLE_KEY, totalsTable);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }        

    
    /**
     * This method switches to another page on a multi-value lookup
     * 
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward switchToPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BalanceInquiryLookupForm balanceInquiryLookup = (BalanceInquiryLookupForm) form;
        List<ResultRow> resultTable = displayTagSurrogate.switchToPage(balanceInquiryLookup.getLookupResultsSelectable(), 
                                                                       displayTagSurrogate.getMaxRowsPerPage(balanceInquiryLookup.getLookupResultsSelectable()));
        request.setAttribute("reqSearchResults", resultTable);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * This method sorts a column.  If the page is currently sorted on a certain column,
     * and the same column is selected to be sorted again, then the results will be 
     * reversed.  After the search method is called, it is difficult to determine the sort
     * order of the result table, so no column is considered sorted.  So, after a search, we were
     * to click sort on an already sorted column, it would appear to have no effect.  Subsequent clicks
     * would tell you  
     * 
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward sort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BalanceInquiryLookupForm balanceInquiryLookup = (BalanceInquiryLookupForm) form;
        List<ResultRow> resultTable = displayTagSurrogate.sort(balanceInquiryLookup.getLookupResultsSelectable(), 
                                                               displayTagSurrogate.getMaxRowsPerPage(balanceInquiryLookup.getLookupResultsSelectable()));
        request.setAttribute("reqSearchResults", resultTable);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
        BalanceInquiryLookupForm balanceInquiryLookup = (BalanceInquiryLookupForm) form;
        if (StringUtils.isBlank(balanceInquiryLookup.getLookupResultsSelectable().getLookupResultsSequenceNumber())) {
            // no search was executed
            return prepareToReturnNone(mapping, form, request, response);
        }
        
        displayTagSurrogate.prepareToReturnSelectedResultBOs(balanceInquiryLookup.getLookupResultsSelectable());
        
        // build the parameters for the refresh url
        Properties parameters = new Properties();
        parameters.put(KFSConstants.LOOKUP_RESULTS_BO_CLASS_NAME, balanceInquiryLookup.getBusinessObjectClassName());
        parameters.put(KFSConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, balanceInquiryLookup.getLookupResultsSelectable().getLookupResultsSequenceNumber());
        parameters.put(KFSConstants.DOC_FORM_KEY, balanceInquiryLookup.getFormKey());
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KFSConstants.REFRESH_CALLER, KFSConstants.MULTIPLE_VALUE);
        parameters.put(KFSConstants.ANCHOR, balanceInquiryLookup.getLookupAnchor());
        parameters.put(KFSConstants.LOOKED_UP_COLLECTION_NAME, balanceInquiryLookup.getLookupResultsSelectable().getLookedUpCollectionName());
        String backUrl = UrlFactory.parameterizeUrl(balanceInquiryLookup.getBackLocation(), parameters);
        return new ActionForward(backUrl, true);
    }
    
    /**
     * This method selects all results across all pages
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BalanceInquiryLookupForm balanceInquiryLookup = (BalanceInquiryLookupForm) form;
        List<ResultRow> resultTable = displayTagSurrogate.selectAll(balanceInquiryLookup.getLookupResultsSelectable(), 
                                                                    displayTagSurrogate.getMaxRowsPerPage(balanceInquiryLookup.getLookupResultsSelectable()));
        request.setAttribute("reqSearchResults", resultTable);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * This method unselects all results across all pages
     * 
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unselectAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BalanceInquiryLookupForm balanceInquiryLookup = (BalanceInquiryLookupForm) form;
        List<ResultRow> resultTable = displayTagSurrogate.unselectAll(balanceInquiryLookup.getLookupResultsSelectable(), 
                                                                      displayTagSurrogate.getMaxRowsPerPage(balanceInquiryLookup.getLookupResultsSelectable()));
        request.setAttribute("reqSearchResults", resultTable);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * This method overrides the super class calcel method because it is basically equivalent to clicking prepare to return none 
     * 
     * @see org.kuali.core.web.struts.action.KualiLookupAction#cancel(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return prepareToReturnNone(mapping, form, request, response);
    }
    

    /**
     * This method returns none of the selected results and redirects back to the lookup caller.
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward prepareToReturnNone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BalanceInquiryLookupForm balanceInquiryLookup = (BalanceInquiryLookupForm) form;
        displayTagSurrogate.prepareToReturnNone(balanceInquiryLookup.getLookupResultsSelectable());
        
        // build the parameters for the refresh url
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DOC_FORM_KEY, balanceInquiryLookup.getFormKey());
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KFSConstants.REFRESH_CALLER, KFSConstants.MULTIPLE_VALUE);
        parameters.put(KFSConstants.ANCHOR, balanceInquiryLookup.getLookupAnchor());
        
        String backUrl = UrlFactory.parameterizeUrl(balanceInquiryLookup.getBackLocation(), parameters);
        return new ActionForward(backUrl, true);
    }
    
    /**
     * This method prepares to export results.  Note: this method will not look for any rows selected since the last page view, so it is best
     * that exporting opens in a new browser window.
     * 
     * @param mapping
     * @param form an <code>{@link ActionForm}</code>
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BalanceInquiryLookupForm balanceInquiryLookup = (BalanceInquiryLookupForm) form;
        List<ResultRow> resultTable = displayTagSurrogate.prepareToExport(balanceInquiryLookup.getLookupResultsSelectable());
        request.setAttribute("reqSearchResults", resultTable);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
