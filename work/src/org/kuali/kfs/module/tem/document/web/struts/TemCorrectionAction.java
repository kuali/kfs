/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService;
import org.kuali.kfs.module.tem.businessobject.AgencyCorrectionChangeGroup;
import org.kuali.kfs.module.tem.businessobject.AgencyEntryFull;
import org.kuali.kfs.module.tem.document.TemCorrectionProcessDocument;
import org.kuali.kfs.module.tem.document.service.TemCorrectionDocumentService;
import org.kuali.kfs.module.tem.service.AgencyEntryGroupService;
import org.kuali.kfs.module.tem.service.AgencyEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase;
import org.kuali.rice.kns.web.struts.action.KualiTableRenderAction;
import org.kuali.rice.kns.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.krad.comparator.NumericValueComparator;
import org.kuali.rice.krad.comparator.TemporalValueComparator;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;

public class TemCorrectionAction extends KualiDocumentActionBase implements KualiTableRenderAction {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TemCorrectionAction.class);
    public static final int DEFAULT_RECORD_COUNT_FUNCTIONALITY_LIMIT = 1000;

    public static final int DEFAULT_RECORDS_PER_PAGE = 10;

    protected static AgencyEntryGroupService agencyEntryGroupService;
    protected static AgencyEntryService agencyEntryService;
    protected static DateTimeService dateTimeService;
    protected static ConfigurationService ConfigurationService;
    protected static ExpenseImportByTripService expenseImportByTripService;

    public static final String SYSTEM_AND_EDIT_METHOD_ERROR_KEY = "systemAndEditMethod";
    private static final int MAX_ROWS = 25;


    @Override
    public ActionForward switchToPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemCorrectionForm correctionForm = (TemCorrectionForm) form;
        int maxRowsPerPage = MAX_ROWS;
        KualiTableRenderFormMetadata agencyEntrySearchResultTableMetadata = correctionForm.getAgencyEntrySearchResultTableMetadata();
        agencyEntrySearchResultTableMetadata.jumpToPage(agencyEntrySearchResultTableMetadata.getSwitchToPageNumber(), correctionForm.getDisplayEntries().size(), maxRowsPerPage);
        agencyEntrySearchResultTableMetadata.setColumnToSortIndex(agencyEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward sort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemCorrectionForm correctionForm = (TemCorrectionForm) form;
        int maxRowsPerPage = MAX_ROWS;

        KualiTableRenderFormMetadata agencyEntrySearchResultTableMetadata = correctionForm.getAgencyEntrySearchResultTableMetadata();

        List<Column> columns = SpringContext.getBean(TemCorrectionDocumentService.class).getTableRenderColumnMetadata(correctionForm.getDocument().getDocumentNumber());

        String propertyToSortName = columns.get(agencyEntrySearchResultTableMetadata.getColumnToSortIndex()).getPropertyName();
        Comparator valueComparator = columns.get(agencyEntrySearchResultTableMetadata.getColumnToSortIndex()).getValueComparator();

        boolean sortDescending = false;
        if (agencyEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() == agencyEntrySearchResultTableMetadata.getColumnToSortIndex()) {
            // clicked sort on the same column that was previously sorted, so we will reverse the sort order
            sortDescending = !agencyEntrySearchResultTableMetadata.isSortDescending();
            agencyEntrySearchResultTableMetadata.setSortDescending(sortDescending);
        }

        agencyEntrySearchResultTableMetadata.setSortDescending(sortDescending);
        // sort the list now so that it will be rendered correctly
        sortList(correctionForm.getDisplayEntries(), propertyToSortName, valueComparator, sortDescending);

        // sorting, so go back to the first page
        agencyEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Called when selecting the system and method. If this button is pressed, the document should be reset as if it is the first
     * time it was pressed.
     */
    public ActionForward selectSystemEditMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("selectSystemEditMethod() started");

        TemCorrectionForm correctionForm = (TemCorrectionForm) form;
        TemCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        if (checkMainDropdown(correctionForm)) {
            // Clear out any entries that were already loaded
            document.setCorrectionInputFileName(null);
            document.setCorrectionOutputFileName(null);
            document.setCorrectionTripTotalAmount(null);
            document.setCorrectionRowCount(null);
            document.getCorrectionChangeGroup().clear();

            correctionForm.setDataLoadedFlag(false);
            correctionForm.setDeleteFileFlag(false);
            correctionForm.setEditableFlag(false);
            correctionForm.setManualEditFlag(false);
            correctionForm.setShowOutputFlag(false);
            correctionForm.setAllEntries(new ArrayList<AgencyEntryFull>());
            correctionForm.setRestrictedFunctionalityMode(false);
            correctionForm.setProcessInBatch(true);

            // if users choose database, then get the list of origin entry groups and set the default
            // CorrectionGroupEntriesFinder f = new CorrectionGroupEntriesFinder();
            // List values = f.getKeyValues();
            // if (values.size() > 0) {

            String newestAgencyMatchingErrorFileName = agencyEntryGroupService.getNewestAgencyMatchingErrorFileName();
            if (newestAgencyMatchingErrorFileName != null) {
                document.setCorrectionInputFileName(newestAgencyMatchingErrorFileName);
            }
            /*
             * else { ConcreteKeyValue klp = (ConcreteKeyValue) values.get(0); document.setCorrectionInputFileName((String) klp.getKey()); }
             * } else { GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY,
             * KFSKeyConstants.ERROR_NO_ORIGIN_ENTRY_GROUPS); correctionForm.setChooseSystem(""); }
             */
        }
        else {
            correctionForm.setEditMethod("");
        }
        correctionForm.setPreviousEditMethod(correctionForm.getEditMethod());
        correctionForm.setPreviousInputGroupId(null);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Validate that choose system and edit method are selected
     */
    protected boolean checkMainDropdown(TemCorrectionForm errorCorrectionForm) {
        LOG.debug("checkMainDropdown() started");
        boolean ret = true;
        if (StringUtils.isEmpty(errorCorrectionForm.getEditMethod())) {
            GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, TemKeyConstants.ERROR_TMCP_EDITMETHODFIELD_REQUIRED);
            ret = false;
        }
        if (ret && TemCorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(errorCorrectionForm.getEditMethod()) && !TemCorrectionDocumentService.SYSTEM_DATABASE.equals(errorCorrectionForm.getChooseSystem())) {
            GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, TemKeyConstants.ERROR_TMCP_REMOVE_GROUP_REQUIRES_DATABASE);
            ret = false;
        }
        return ret;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("execute() started");

        TemCorrectionForm correctionForm = (TemCorrectionForm) form;

        // Init our services once
        if (agencyEntryGroupService == null) {
            TemCorrectionAction.agencyEntryGroupService = SpringContext.getBean(AgencyEntryGroupService.class);
            TemCorrectionAction.agencyEntryService = SpringContext.getBean(AgencyEntryService.class);
            TemCorrectionAction.dateTimeService = SpringContext.getBean(DateTimeService.class);
            TemCorrectionAction.ConfigurationService = SpringContext.getBean(ConfigurationService.class);
            TemCorrectionAction.expenseImportByTripService = SpringContext.getBean(ExpenseImportByTripService.class);
        }
        TemCorrectionForm rForm = (TemCorrectionForm) form;
        LOG.debug("execute() methodToCall: " + rForm.getMethodToCall());

        Collection<AgencyEntryFull> persistedAgencyEntries = null;

        if (!(KFSConstants.DOC_HANDLER_METHOD.equals(rForm.getMethodToCall()) || KFSConstants.RELOAD_METHOD_TO_CALL.equals(rForm.getMethodToCall()))) {
            restoreSystemAndEditMethod(rForm);
            // restoreInputGroupSelectionForDatabaseEdits(rForm);
            if (!rForm.isRestrictedFunctionalityMode()) {
                loadAllEntries(correctionForm.getInputGroupId(), rForm);
                rForm.setDisplayEntries(new ArrayList<AgencyEntryFull>(rForm.getAllEntries()));



                if (!KFSConstants.TableRenderConstants.SORT_METHOD.equals(rForm.getMethodToCall())) {
                    // if sorting, we'll let the action take care of the sorting
                    KualiTableRenderFormMetadata agencyEntrySearchResultTableMetadata = rForm.getAgencyEntrySearchResultTableMetadata();
                    if (agencyEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() != -1) {
                        List<Column> columns = SpringContext.getBean(TemCorrectionDocumentService.class).getTableRenderColumnMetadata(rForm.getDocument().getDocumentNumber());

                        String propertyToSortName = columns.get(agencyEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getPropertyName();
                        Comparator valueComparator = columns.get(agencyEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getValueComparator();
                        sortList(rForm.getDisplayEntries(), propertyToSortName, valueComparator, agencyEntrySearchResultTableMetadata.isSortDescending());
                    }
                    if (rForm.getAllEntries() != null) {
                        int maxRowsPerPage = 25;
                        agencyEntrySearchResultTableMetadata.jumpToPage(agencyEntrySearchResultTableMetadata.getViewedPageNumber(), rForm.getDisplayEntries().size(), maxRowsPerPage);
                        agencyEntrySearchResultTableMetadata.setColumnToSortIndex(agencyEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex());
                    }
                }
            }
        }

        ActionForward af = super.execute(mapping, form, request, response);
        return af;
    }

    /**
     * Edit a row in the group
     */
    public ActionForward editManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("editManualEdit() started");

        TemCorrectionForm correctionForm = (TemCorrectionForm) form;
        TemCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        int entryId = Integer.parseInt(getImageContext(request, "entryId"));

        // Find it and put it in the editing spot

        correctionForm.setEntryForManualEdit(correctionForm.getAllEntries().get(entryId - 1));

        correctionForm.setShowSummaryOutputFlag(true);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Add a new row to the group
     */
    public ActionForward addManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addManualEdit() started");

        TemCorrectionForm correctionForm = (TemCorrectionForm) form;
        TemCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        if (validAgencyEntry(correctionForm.getEntryForManualEdit())) {
            //correctionForm.updateEntryForManualEdit();

            // new entryId is always 0, so give it a unique Id, SequenceAccessorService is used.
            int newEntryId = correctionForm.getAllEntries().size() + 1;
            correctionForm.getEntryForManualEdit().setEntryId(new Integer(newEntryId));

            correctionForm.getAllEntries().add(correctionForm.getEntryForManualEdit());

            // Clear out the additional row
            correctionForm.clearEntryForManualEdit();
        }


        // Calculate the debit/credit/row count
        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());

        correctionForm.setShowSummaryOutputFlag(true);

        // we've modified the list of all entries, so repersist it
        correctionForm.setDisplayEntries(new ArrayList<AgencyEntryFull>(correctionForm.getAllEntries()));


        // list has changed, we'll need to repage and resort
        applyPagingAndSortingFromPreviousPageView(correctionForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Delete a row from the group
     */
    public ActionForward deleteManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("deleteManualEdit() started");

        TemCorrectionForm correctionForm = (TemCorrectionForm) form;
        TemCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
        int entryId = Integer.parseInt(getImageContext(request, "entryId"));

        // Find it and remove it
        for (Iterator iter = correctionForm.getAllEntries().iterator(); iter.hasNext();) {
            AgencyEntryFull element = (AgencyEntryFull) iter.next();
            if (element.getEntryId() == entryId) {
                iter.remove();
                break;
            }
        }

        // Calculate the debit/credit/row count
        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());

        correctionForm.setShowSummaryOutputFlag(true);

        // we've modified the list of all entries, so repersist it
        correctionForm.setDisplayEntries(new ArrayList<AgencyEntryFull>(correctionForm.getAllEntries()));

        // list has changed, we'll need to repage and resort
        applyPagingAndSortingFromPreviousPageView(correctionForm);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Save a changed row in the group
     */
    public ActionForward saveManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("saveManualEdit() started");

        TemCorrectionForm correctionForm = (TemCorrectionForm) form;
        TemCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        if (validAgencyEntry(correctionForm.getEntryForManualEdit())) {
            int entryId = correctionForm.getEntryForManualEdit().getEntryId();

            // Find it and replace it with the one from the edit spot
            for (Iterator<AgencyEntryFull> iter = correctionForm.getAllEntries().iterator(); iter.hasNext();) {
                AgencyEntryFull element = iter.next();
                if (element.getEntryId() == entryId) {
                    iter.remove();
                }
            }

            correctionForm.getAllEntries().add(correctionForm.getEntryForManualEdit());

            // we've modified the list of all entries, so repersist it
            // SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(correctionForm.getGlcpSearchResultsSequenceNumber(),
            // correctionForm.getAllEntries());
            correctionForm.setDisplayEntries(new ArrayList<AgencyEntryFull>(correctionForm.getAllEntries()));


            // Clear out the additional row
            correctionForm.clearEntryForManualEdit();
        }

        // Calculate the debit/credit/row count
        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());

        // list has changed, we'll need to repage and resort
        applyPagingAndSortingFromPreviousPageView(correctionForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    private boolean validAgencyEntry(AgencyEntryFull entryForManualEdit) {
        List<ErrorMessage> errors = expenseImportByTripService.validateMandatoryFieldsPresent(entryForManualEdit);

        for(ErrorMessage error: errors) {
            GlobalVariables.getMessageMap().putError("searchResults", error.toString());
        }

        return errors.isEmpty();
    }

    /**
     * Called when Load Group button is pressed
     */
    public ActionForward loadGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("loadGroup() started");

        TemCorrectionForm correctionForm = (TemCorrectionForm) form;
        String batchDirectory = SpringContext.getBean(TemCorrectionDocumentService.class).getBatchFileDirectoryName();

        if (checkAgencyEntryGroupSelection(correctionForm)) {
            TemCorrectionProcessDocument doc = (TemCorrectionProcessDocument) correctionForm.getDocument();
            doc.setCorrectionInputFileName(correctionForm.getInputGroupId());

            int inputGroupSize = agencyEntryService.getGroupCount(correctionForm.getInputGroupId());
            int recordCountFunctionalityLimit = DEFAULT_RECORD_COUNT_FUNCTIONALITY_LIMIT;
            correctionForm.setPersistedOriginEntriesMissing(false);

            correctionForm.setRestrictedFunctionalityMode(false);

            loadAllEntries(correctionForm.getInputGroupId(), correctionForm);

            if (correctionForm.getAllEntries().size() > 0) {
                if (TemCorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
                    correctionForm.setManualEditFlag(true);
                    correctionForm.setEditableFlag(false);
                    correctionForm.setDeleteFileFlag(false);
                }
                correctionForm.setDataLoadedFlag(true);
            }
            else {
                GlobalVariables.getMessageMap().putError("documentsInSystem", TemKeyConstants.ERROR_TMCP_NO_RECORDS);
            }

            TemCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
            if (document.getCorrectionChangeGroup().isEmpty()) {
                document.addCorrectionChangeGroup(new AgencyCorrectionChangeGroup());
            }

            correctionForm.setPreviousInputGroupId(correctionForm.getInputGroupId());
        }

        correctionForm.setShowOutputFlag(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method checks that an origin entry group has been selected; and this method is intended to be used for selecting an
     * origin entry group when using the database method If a group has not been loaded, then an error will be added to the screen
     *
     * @param correctionForm
     * @return
     */
    protected boolean checkAgencyEntryGroupSelection(TemCorrectionForm correctionForm) {
        LOG.debug("checkOriginEntryGroupSelection() started");

        if (correctionForm.getInputGroupId() == null) {
            GlobalVariables.getMessageMap().putError("documentLoadError", TemKeyConstants.ERROR_TMCP_AGENCYGROUP_REQUIRED_FOR_ROUTING);
            return false;
        }
        return true;
    }

    /**
     * This method updates the summary information contained within each document depending on the document status, editing method,
     * whether only the rows matching criteria are shown, and whether the output is being shown If the form is in restricted
     * functionality mode (and the override param is not set to true), then the summaries will be cleared out
     *
     * @param document the document
     * @param entries the entries to summarize
     * @param clearOutSummary whether to set the doc summary to 0s
     */
    protected void updateDocumentSummary(TemCorrectionProcessDocument document, List<AgencyEntryFull> entries, boolean clearOutSummary) {
        if (clearOutSummary) {
            document.setCorrectionTripTotalAmount(null);
            document.setCorrectionRowCount(null);
        }
        else {
            // update the summary section
            document.setCorrectionRowCount(entries.size());
            KualiDecimal tripTotal = KualiDecimal.ZERO;
            for(AgencyEntryFull agency: entries) {
                if(null != agency.getTripExpenseAmount()) {
                    tripTotal = tripTotal.add(agency.getTripExpenseAmount());
                }

            }
            document.setCorrectionTripTotalAmount(tripTotal);
        }
    }

    /**
     * Show all entries for Manual edit with groupId and persist these entries to the DB The restricted functionality mode flag MUST
     * BE SET PRIOR TO CALLING this method.
     *
     * @param groupId
     * @param correctionForm
     * @throws Exception
     */
    protected void loadAllEntries(String fileNameWithPath, TemCorrectionForm correctionForm) {
        LOG.debug("loadAllEntries() started");
        TemCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        if (!correctionForm.isRestrictedFunctionalityMode()) {
            List<AgencyEntryFull> searchResults = new ArrayList();
            Map loadErrorMap = agencyEntryService.getEntriesByGroupIdWithPath(fileNameWithPath, searchResults);
            correctionForm.setAllEntries(searchResults);
            int maxRowsPerPage = MAX_ROWS;
            KualiTableRenderFormMetadata agencyEntrySearchResultTableMetadata = correctionForm.getAgencyEntrySearchResultTableMetadata();
            agencyEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getAllEntries().size(), maxRowsPerPage);

            // put errors on GlobalVariables
            if (loadErrorMap != null && loadErrorMap.size() > 0) {
                Iterator iter = loadErrorMap.keySet().iterator();
                while (iter.hasNext()) {
                    Integer lineNumber = (Integer) iter.next();
                    List<Message> messageList = (List<Message>) loadErrorMap.get(lineNumber);
                    for (Message errorMmessage : messageList) {
                        GlobalVariables.getMessageMap().putError("fileUpload", KFSKeyConstants.ERROR_INVALID_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE, new String[] { lineNumber.toString(), errorMmessage.toString() });
                    }
                }
            }
            else {
                try {
                    loadAllEntries(searchResults, correctionForm);

                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected void loadAllEntries(List<AgencyEntryFull> searchResults, TemCorrectionForm correctionForm) throws Exception {

        TemCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
        correctionForm.setAllEntries(searchResults);
        correctionForm.setDisplayEntries(new ArrayList<AgencyEntryFull>(searchResults));

        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());

        int maxRowsPerPage = 25;
        KualiTableRenderFormMetadata agencyEntrySearchResultTableMetadata = correctionForm.getAgencyEntrySearchResultTableMetadata();
        agencyEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
        agencyEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
    }

    public ActionForward manualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LOG.debug("manualEdit() started");

        TemCorrectionForm correctionForm = (TemCorrectionForm) form;
        TemCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        correctionForm.clearEntryForManualEdit();
        correctionForm.setEditableFlag(true);
        correctionForm.setManualEditFlag(false);

        int maxRowsPerPage = 25;
        KualiTableRenderFormMetadata agencyEntrySearchResultTableMetadata = correctionForm.getAgencyEntrySearchResultTableMetadata();
        agencyEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getAllEntries().size(), maxRowsPerPage);
        agencyEntrySearchResultTableMetadata.setColumnToSortIndex(-1);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    protected void applyPagingAndSortingFromPreviousPageView(TemCorrectionForm correctionForm) {
        KualiTableRenderFormMetadata agencyEntrySearchResultTableMetadata = correctionForm.getAgencyEntrySearchResultTableMetadata();
        if (agencyEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() != -1) {

            List<Column> columns = SpringContext.getBean(TemCorrectionDocumentService.class).getTableRenderColumnMetadata(correctionForm.getDocument().getDocumentNumber());

            String propertyToSortName = columns.get(agencyEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getPropertyName();
            Comparator valueComparator = columns.get(agencyEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getValueComparator();
            sortList(correctionForm.getDisplayEntries(), propertyToSortName, valueComparator, agencyEntrySearchResultTableMetadata.isSortDescending());
        }

        int maxRowsPerPage = 25;
        agencyEntrySearchResultTableMetadata.jumpToPage(agencyEntrySearchResultTableMetadata.getViewedPageNumber(), correctionForm.getDisplayEntries().size(), maxRowsPerPage);
    }

    protected void sortList(List<AgencyEntryFull> list, String propertyToSortName, Comparator valueComparator, boolean sortDescending) {
        if (list != null) {
            if (valueComparator instanceof NumericValueComparator || valueComparator instanceof TemporalValueComparator) {
                // hack alert: NumericValueComparator can only compare strings, so we use the KualiDecimal and Date's built in
                // mechanism compare values using
                // the comparable comparator
                valueComparator = new Comparator() {
                    @Override
                    public int compare(Object obj1, Object obj2) {
                        if (obj1 == null) {
                            return -1;
                        }
                        if (obj2 == null) {
                            return 1;
                        }
                        return ((Comparable) obj1).compareTo(obj2);
                    }
                };
            }
            if (sortDescending) {
                valueComparator = new ReverseComparator(valueComparator);
            }
            Collections.sort(list, new BeanComparator(propertyToSortName, valueComparator));
        }
    }

    /**
     * This method restores the system and edit method to the selected values when the last call to the selectSystemAndMethod action
     * was made
     *
     * @param correctionForm
     * @return if the system and edit method were changed while not in read only mode and the selectSystemEditMethod method was not
     *         being called if true, this is ususally not a good condition
     */
    protected boolean restoreSystemAndEditMethod(TemCorrectionForm correctionForm) {
        boolean readOnly = correctionForm.getEditingMode().get(TemConstants.TravelEditMode.FULL_ENTRY) != null;
        if (!"selectSystemEditMethod".equals(correctionForm.getMethodToCall()) && !readOnly) {
            if (!StringUtils.equals(correctionForm.getPreviousEditMethod(), correctionForm.getEditMethod()) || !StringUtils.equals(correctionForm.getPreviousChooseSystem(), correctionForm.getChooseSystem())) {
                correctionForm.setChooseSystem(correctionForm.getPreviousChooseSystem());
                correctionForm.setEditMethod(correctionForm.getPreviousEditMethod());
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        return super.save(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("blanketApprove() started");

        TemCorrectionForm correctionForm = (TemCorrectionForm) form;

        if (prepareForRoute(correctionForm)) {
            if (correctionForm.isDataLoadedFlag() && !correctionForm.isRestrictedFunctionalityMode()) {
                int maxRowsPerPage = MAX_ROWS;
                // display the entire list after routing
                correctionForm.getDisplayEntries().clear();
                correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());
                correctionForm.getAgencyEntrySearchResultTableMetadata().jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
            }
            return super.blanketApprove(mapping, form, request, response);
        }
        else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#blanketApprove(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("blanketApprove() started");

        TemCorrectionForm correctionForm = (TemCorrectionForm) form;

        if (prepareForRoute(correctionForm)) {
            if (correctionForm.isDataLoadedFlag() && !correctionForm.isRestrictedFunctionalityMode()) {
                int maxRowsPerPage = MAX_ROWS;
                // display the entire list after routing
                correctionForm.getDisplayEntries().clear();
                correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());
                correctionForm.getAgencyEntrySearchResultTableMetadata().jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
            }
            return super.blanketApprove(mapping, form, request, response);
        }
        else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#cancel(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        return super.cancel(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#close(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        return super.close(mapping, form, request, response);
    }

    private boolean prepareForRoute(TemCorrectionForm correctionForm) {
        TemCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        // Is there a description?
        if (StringUtils.isEmpty(document.getDocumentHeader().getDocumentDescription())) {
            GlobalVariables.getMessageMap().putError("document.documentHeader.documentDescription", KFSKeyConstants.ERROR_DOCUMENT_NO_DESCRIPTION);
            return false;
        }

        // Did they pick the edit method and system?
        if (!checkMainDropdown(correctionForm)) {
            return false;
        }

        document.setCorrectionInputFileName(correctionForm.getInputGroupId());

        if (!checkAgencyEntryGroupSelectionBeforeRouting(document)) {
            return false;
        }

        if (!validGroupsItemsForDocumentSave(correctionForm)) {
            return false;
        }

        // Populate document
        document.setCorrectionTypeCode(correctionForm.getEditMethod());
        document.setCorrectionInputFileName(correctionForm.getInputGroupId());

        // we'll populate the output group id when the doc has a route level change
        document.setCorrectionOutputFileName(null);

        SpringContext.getBean(TemCorrectionDocumentService.class).persistAgencyEntryGroupsForDocumentSave(document, correctionForm);

        return true;

    }

    private boolean validChangeGroups(TemCorrectionForm correctionForm) {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean validGroupsItemsForDocumentSave(TemCorrectionForm correctionForm) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * This method checks that an origin entry group has been selected or uploaded, depending on the system of the document If a
     * group has not been loaded, then an error will be added to the screen
     *
     * @param document
     * @return
     */
    protected boolean checkAgencyEntryGroupSelectionBeforeRouting(TemCorrectionProcessDocument document) {
        if (document.getCorrectionInputFileName() == null) {
            GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, TemKeyConstants.ERROR_TMCP_AGENCYGROUP_REQUIRED_FOR_ROUTING);
            return false;
        }
        return true;
    }



}
