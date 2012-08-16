/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.gl.document.web.struts;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.CorrectionChange;
import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.businessobject.CorrectionCriteria;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryStatistics;
import org.kuali.kfs.gl.businessobject.options.CorrectionGroupEntriesFinder;
import org.kuali.kfs.gl.businessobject.options.OriginEntryFieldFinder;
import org.kuali.kfs.gl.document.CorrectionDocumentUtils;
import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;
import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.kfs.gl.service.GlCorrectionProcessOriginEntryService;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase;
import org.kuali.rice.kns.web.struts.action.KualiTableRenderAction;
import org.kuali.rice.kns.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.krad.comparator.NumericValueComparator;
import org.kuali.rice.krad.comparator.TemporalValueComparator;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

public class CorrectionAction extends KualiDocumentActionBase implements KualiTableRenderAction {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionAction.class);

    protected static OriginEntryGroupService originEntryGroupService;
    protected static OriginEntryService originEntryService;
    protected static DateTimeService dateTimeService;
    protected static ConfigurationService kualiConfigurationService;

    public static final String SYSTEM_AND_EDIT_METHOD_ERROR_KEY = "systemAndEditMethod";

    /**
     * KRAD Conversion: Uses the metadata of different columns created and selects the  
     * column name and column comparator and uses these properties to srt the list. 
     * There is no use of data dictionary.
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("execute() started");

        CorrectionForm correctionForm = (CorrectionForm) form;

        // Init our services once
        if (originEntryGroupService == null) {
            CorrectionAction.originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);
            CorrectionAction.originEntryService = SpringContext.getBean(OriginEntryService.class);
            CorrectionAction.dateTimeService = SpringContext.getBean(DateTimeService.class);
            CorrectionAction.kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        }

        CorrectionForm rForm = (CorrectionForm) form;
        if (LOG.isDebugEnabled()) {
            LOG.debug("execute() methodToCall: " + rForm.getMethodToCall());
        }
        
        Collection<OriginEntryFull> persistedOriginEntries = null;

        // If we are called from the docHandler or reload, ignore the persisted origin entries because we are either creating a new
        // document or loading an old one
        if (!(KFSConstants.DOC_HANDLER_METHOD.equals(rForm.getMethodToCall()) || KFSConstants.RELOAD_METHOD_TO_CALL.equals(rForm.getMethodToCall()))) {
            restoreSystemAndEditMethod(rForm);
            restoreInputGroupSelectionForDatabaseEdits(rForm);
            if (!rForm.isRestrictedFunctionalityMode()) {
                if (StringUtils.isNotBlank(rForm.getGlcpSearchResultsSequenceNumber())) {
                    rForm.setAllEntries(SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).retrieveAllEntries(rForm.getGlcpSearchResultsSequenceNumber()));
                    if (rForm.getAllEntries() == null) {
                        rForm.setDisplayEntries(null);
                    }
                    else {
                        rForm.setDisplayEntries(new ArrayList<OriginEntryFull>(rForm.getAllEntries()));
                    }

                    if ((!"showOutputGroup".equals(rForm.getMethodToCall())) && rForm.getShowOutputFlag()) {
                        // reapply the any criteria to pare down the list if the match criteria only flag is checked
                        GeneralLedgerCorrectionProcessDocument document = rForm.getCorrectionDocument();
                        List<CorrectionChangeGroup> groups = document.getCorrectionChangeGroup();
                        updateEntriesFromCriteria(rForm, rForm.isRestrictedFunctionalityMode());
                    }

                    if (!KFSConstants.TableRenderConstants.SORT_METHOD.equals(rForm.getMethodToCall())) {
                        // if sorting, we'll let the action take care of the sorting
                        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = rForm.getOriginEntrySearchResultTableMetadata();
                        if (originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() != -1) {
                            List<Column> columns = SpringContext.getBean(CorrectionDocumentService.class).getTableRenderColumnMetadata(rForm.getDocument().getDocumentNumber());

                            String propertyToSortName = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getPropertyName();
                            Comparator valueComparator = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getValueComparator();
                            sortList(rForm.getDisplayEntries(), propertyToSortName, valueComparator, originEntrySearchResultTableMetadata.isSortDescending());
                        }
                        if (rForm.getAllEntries() != null) {
                            int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
                            originEntrySearchResultTableMetadata.jumpToPage(originEntrySearchResultTableMetadata.getViewedPageNumber(), rForm.getDisplayEntries().size(), maxRowsPerPage);
                            originEntrySearchResultTableMetadata.setColumnToSortIndex(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex());
                        }
                    }
                }
            }
        }

        ActionForward af = super.execute(mapping, form, request, response);
        return af;
    }
    
    /**
     * Save the document when they click the save button
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("save() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        // Did they pick the edit method and system?
        if (!checkMainDropdown(correctionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!checkRestrictedFunctionalityModeForManualEdit(correctionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!validGroupsItemsForDocumentSave(correctionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!validChangeGroups(correctionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!checkInputGroupPersistedForDocumentSave(correctionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // Populate document
        document.setCorrectionTypeCode(correctionForm.getEditMethod());
        document.setCorrectionSelection(correctionForm.getMatchCriteriaOnly());
        document.setCorrectionFileDelete(!correctionForm.getProcessInBatch());
        document.setCorrectionInputFileName(correctionForm.getInputGroupId());
        document.setCorrectionOutputFileName(null); 
        if (correctionForm.getDataLoadedFlag() || correctionForm.isRestrictedFunctionalityMode()) {
            document.setCorrectionInputFileName(correctionForm.getInputGroupId());
        }
        else {
            document.setCorrectionInputFileName(null);
        }
        document.setCorrectionOutputFileName(null);

        SpringContext.getBean(CorrectionDocumentService.class).persistOriginEntryGroupsForDocumentSave(document, correctionForm);
        if (LOG.isDebugEnabled()) {
            LOG.debug("save() doc type name: " + correctionForm.getDocTypeName());
        }
        return super.save(mapping, form, request, response);
    }

    /**
     * This needs to be done just in case they decide to save it when closing.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#close(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("close() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        // Populate document
        document.setCorrectionTypeCode(correctionForm.getEditMethod());
        document.setCorrectionSelection(correctionForm.getMatchCriteriaOnly());
        document.setCorrectionFileDelete(!correctionForm.getProcessInBatch());
        document.setCorrectionInputFileName(correctionForm.getInputFileName());
        document.setCorrectionOutputFileName(null); // this field is never used
        document.setCorrectionInputFileName(correctionForm.getInputGroupId());
        document.setCorrectionOutputFileName(null);

        return super.close(mapping, form, request, response);
    }

    /**
     * Prepare for routing. Return true if we're good to route, false if not
     * 
     * @param correctionForm
     * @return
     */
    protected boolean prepareForRoute(CorrectionForm correctionForm) throws Exception {
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        // Is there a description?
        if (StringUtils.isEmpty(document.getDocumentHeader().getDocumentDescription())) {
            GlobalVariables.getMessageMap().putError("document.documentHeader.documentDescription", KFSKeyConstants.ERROR_DOCUMENT_NO_DESCRIPTION);
            return false;
        }

        if (correctionForm.isPersistedOriginEntriesMissing()) {
            GlobalVariables.getMessageMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING);
            return false;
        }

        // Did they pick the edit method and system?
        if (!checkMainDropdown(correctionForm)) {
            return false;
        }

        if (correctionForm.getDataLoadedFlag() || correctionForm.isRestrictedFunctionalityMode()) {
            document.setCorrectionInputFileName(correctionForm.getInputGroupId());
        }
        else {
            document.setCorrectionInputFileName(null);
        }
        if (!checkOriginEntryGroupSelectionBeforeRouting(document)) {
            return false;
        }

        // were the system and edit methods inappropriately changed?
        if (GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_SYSTEM_OR_EDIT_METHOD_CHANGE)) {
            return false;
        }

        // was the input group inappropriately changed?
        if (GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_INPUT_GROUP_CHANGE)) {
            return false;
        }

        if (!validGroupsItemsForDocumentSave(correctionForm)) {
            return false;
        }

        // If it is criteria, are all the criteria valid?
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionForm.getEditMethod())) {
            if (!validChangeGroups(correctionForm)) {
                return false;
            }
        }

        if (!checkRestrictedFunctionalityModeForManualEdit(correctionForm)) {
            return false;
        }

        if (!checkInputGroupPersistedForDocumentSave(correctionForm)) {
            return false;
        }
        // Get the output group if necessary
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionForm.getEditMethod())) {
            if (!correctionForm.isRestrictedFunctionalityMode() && correctionForm.getDataLoadedFlag() && !correctionForm.getShowOutputFlag()) {
                // we're going to force the user to view the output group upon routing, so apply the criteria
                // if the user wasn't in show output mode.
                updateEntriesFromCriteria(correctionForm, false);
            }
            correctionForm.setShowOutputFlag(true);
        }
        else {
            // If it is manual edit, we don't need to save any correction groups
            document.getCorrectionChangeGroup().clear();
        }

        // Populate document
        document.setCorrectionTypeCode(correctionForm.getEditMethod());
        document.setCorrectionSelection(correctionForm.getMatchCriteriaOnly());
        document.setCorrectionFileDelete(!correctionForm.getProcessInBatch());
        document.setCorrectionInputFileName(correctionForm.getInputGroupId());
        document.setCorrectionOutputFileName(null); // this field is never used

        // we'll populate the output group id when the doc has a route level change
        document.setCorrectionOutputFileName(null);

        SpringContext.getBean(CorrectionDocumentService.class).persistOriginEntryGroupsForDocumentSave(document, correctionForm);

        return true;
    }

    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("blanketApprove() started");

        CorrectionForm correctionForm = (CorrectionForm) form;

        if (prepareForRoute(correctionForm)) {
            if (correctionForm.getDataLoadedFlag() && !correctionForm.isRestrictedFunctionalityMode()) {
                int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
                // display the entire list after routing
                correctionForm.getDisplayEntries().clear();
                correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());
                correctionForm.getOriginEntrySearchResultTableMetadata().jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
            }
            return super.blanketApprove(mapping, form, request, response);
        }
        else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    /**
     * Route the document
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("route() started");

        CorrectionForm correctionForm = (CorrectionForm) form;

        if (prepareForRoute(correctionForm)) {
            if (correctionForm.getDataLoadedFlag() && !correctionForm.isRestrictedFunctionalityMode()) {
                int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
                // display the entire list after routing
                correctionForm.getDisplayEntries().clear();
                correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());
                correctionForm.getOriginEntrySearchResultTableMetadata().jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
            }
            return super.route(mapping, form, request, response);
        }
        else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    public ActionForward manualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LOG.debug("manualEdit() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        correctionForm.clearEntryForManualEdit();
        correctionForm.setEditableFlag(true);
        correctionForm.setManualEditFlag(false);

        //document.addCorrectionChangeGroup(new CorrectionChangeGroup());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Called when the document is loaded from action list or doc search or a new document is created.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("docHandler() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        String command = correctionForm.getCommand();

        if (KewApiConstants.INITIATE_COMMAND.equals(command)) {
            correctionForm.clearForm();
            createDocument(correctionForm);
        }
        else {
            loadDocument(correctionForm);

            GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
            correctionForm.setInputGroupIdFromLastDocumentLoad(document.getCorrectionInputFileName());
            populateAuthorizationFields(correctionForm);
            Map<String, String> documentActions = correctionForm.getDocumentActions();
            if (documentActions.containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT)) {
                // They have saved the document and they are retreiving it to be completed
                correctionForm.setProcessInBatch(!document.getCorrectionFileDelete());
                correctionForm.setMatchCriteriaOnly(document.getCorrectionSelection());
                correctionForm.setEditMethod(document.getCorrectionTypeCode());
                
                if (document.getCorrectionInputFileName() != null) {
                    if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(document.getCorrectionTypeCode())) {
                        loadPersistedInputGroup(correctionForm);
                        correctionForm.setDeleteFileFlag(false);
                    }
                    else if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(document.getCorrectionTypeCode())) {
                        // for the "true" param below, when the origin entries are persisted in the CorrectionDocumentService, they
                        // // are likely
                        // // not to have origin entry IDs assigned to them. So, we create pseudo entry IDs that are
                        // // unique within the allEntries list, but not necessarily within the DB. The persistence layer
                        // // is responsible for auto-incrementing entry IDs in the DB.
                        loadPersistedOutputGroup(correctionForm, true);

                        correctionForm.setManualEditFlag(true);
                        correctionForm.setEditableFlag(false);
                        correctionForm.setDeleteFileFlag(false);
                    }
                    else if (CorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(document.getCorrectionTypeCode())) {
                        loadPersistedInputGroup(correctionForm);
                        correctionForm.setDeleteFileFlag(true);
                    }
                    else {
                        throw new RuntimeException("Unknown edit method " + document.getCorrectionTypeCode());
                    }
                    correctionForm.setDataLoadedFlag(true);
                }
                else {
                    correctionForm.setDataLoadedFlag(false);
                }
                correctionForm.setShowOutputFlag(documentActions.containsKey(KRADConstants.KUALI_ACTION_CAN_APPROVE));
                if (correctionForm.getShowOutputFlag() && !correctionForm.isRestrictedFunctionalityMode()) {
                    updateEntriesFromCriteria(correctionForm, false);
                }
                correctionForm.setInputFileName(document.getCorrectionInputFileName());
                if (document.getCorrectionInputFileName() != null) {
                    correctionForm.setChooseSystem(CorrectionDocumentService.SYSTEM_UPLOAD);
                }
                else {
                    correctionForm.setChooseSystem(CorrectionDocumentService.SYSTEM_DATABASE);
                }

                correctionForm.setPreviousChooseSystem(correctionForm.getChooseSystem());
                correctionForm.setPreviousEditMethod(correctionForm.getEditMethod());
                correctionForm.setPreviousInputGroupId(correctionForm.getInputGroupId());
            }
            else {
                // They are calling this from their action list to look at it or approve it
                correctionForm.setProcessInBatch(!document.getCorrectionFileDelete());
                correctionForm.setMatchCriteriaOnly(document.getCorrectionSelection());

                // we don't care about setting entry IDs for the records below, so the param is false below
                loadPersistedOutputGroup(correctionForm, false);
                correctionForm.setShowOutputFlag(true);
            }
            correctionForm.setInputGroupIdFromLastDocumentLoadIsMissing(!originEntryGroupService.getGroupExists(document.getCorrectionInputFileName()));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Called when selecting the system and method. If this button is pressed, the document should be reset as if it is the first
     * time it was pressed.
     */
    public ActionForward selectSystemEditMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("selectSystemEditMethod() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        if (checkMainDropdown(correctionForm)) {
            // Clear out any entries that were already loaded
            document.setCorrectionInputFileName(null);
            document.setCorrectionOutputFileName(null);
            document.setCorrectionCreditTotalAmount(null);
            document.setCorrectionDebitTotalAmount(null);
            document.setCorrectionBudgetTotalAmount(null);
            document.setCorrectionRowCount(null);
            document.getCorrectionChangeGroup().clear();

            correctionForm.setDataLoadedFlag(false);
            correctionForm.setDeleteFileFlag(false);
            correctionForm.setEditableFlag(false);
            correctionForm.setManualEditFlag(false);
            correctionForm.setShowOutputFlag(false);
            correctionForm.setAllEntries(new ArrayList<OriginEntryFull>());
            correctionForm.setRestrictedFunctionalityMode(false);
            correctionForm.setProcessInBatch(true);

            if (CorrectionDocumentService.SYSTEM_DATABASE.equals(correctionForm.getChooseSystem())) {
                // if users choose database, then get the list of origin entry groups and set the default
                CorrectionGroupEntriesFinder f = new CorrectionGroupEntriesFinder();
                List values = f.getKeyValues();
                if (values.size() > 0) {
                    
                    String newestScrubberErrorFileName = CorrectionAction.originEntryGroupService.getNewestScrubberErrorFileName();
                    if (newestScrubberErrorFileName != null) {
                        document.setCorrectionInputFileName(newestScrubberErrorFileName);
                    }
                    else {
                        KeyValue klp = (KeyValue) values.get(0);
                        document.setCorrectionInputFileName((String) klp.getKey());
                    }
                }
                else {
                    GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_NO_ORIGIN_ENTRY_GROUPS);
                    correctionForm.setChooseSystem("");
                }
            }
        }
        else {
            correctionForm.setEditMethod("");
            correctionForm.setChooseSystem("");
        }
        correctionForm.setPreviousChooseSystem(correctionForm.getChooseSystem());
        correctionForm.setPreviousEditMethod(correctionForm.getEditMethod());
        correctionForm.setPreviousInputGroupId(null);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Called when copy group to desktop is selected
     */
    public ActionForward saveToDesktop(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.debug("saveToDesktop() started");

        CorrectionForm correctionForm = (CorrectionForm) form;

        if (checkOriginEntryGroupSelection(correctionForm)) {
            if (correctionForm.isInputGroupIdFromLastDocumentLoadIsMissing() && correctionForm.getInputGroupIdFromLastDocumentLoad() != null && correctionForm.getInputGroupIdFromLastDocumentLoad().equals(correctionForm.getInputGroupId())) {
                if (correctionForm.isPersistedOriginEntriesMissing()) {
                    GlobalVariables.getMessageMap().putError("documentsInSystem", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING);
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
                else {
                    String fileName = "glcp_archived_group_" + correctionForm.getInputGroupIdFromLastDocumentLoad().toString() + ".txt";
                    // set response
                    response.setContentType("application/txt");
                    response.setHeader("Content-disposition", "attachment; filename=" + fileName);
                    response.setHeader("Expires", "0");
                    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                    response.setHeader("Pragma", "public");

                    BufferedOutputStream bw = new BufferedOutputStream(response.getOutputStream());

                    SpringContext.getBean(CorrectionDocumentService.class).writePersistedInputOriginEntriesToStream((GeneralLedgerCorrectionProcessDocument) correctionForm.getDocument(), bw);

                    bw.flush();
                    bw.close();

                    return null;
                }
            }
            else {

                //String fileName = oeg.getSource().getCode() + oeg.getId().toString() + "_" + oeg.getDate().toString() + ".txt";
                String batchDirectory = SpringContext.getBean(CorrectionDocumentService.class).getBatchFileDirectoryName();
                String fileNameWithPath; 
                if (!correctionForm.getInputGroupId().contains(batchDirectory)){
                    fileNameWithPath = batchDirectory + File.separator + correctionForm.getInputGroupId();
                } else {
                    fileNameWithPath = correctionForm.getInputGroupId(); 
                }
                
                FileReader fileReader = new FileReader(fileNameWithPath);
                BufferedReader br = new BufferedReader(fileReader);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // write to output
                buildTextOutputfile(baos, br);
                WebUtils.saveMimeOutputStreamAsFile(response, "application/txt", baos, correctionForm.getInputGroupId());
                
                br.close();
                
                return null;
            }
        }
        else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
    }
    
    
    
    public void buildTextOutputfile(ByteArrayOutputStream bw, BufferedReader br){
        String line;
        try {
            while ((line = br.readLine()) != null) {
                bw.write((line + "\n").getBytes());
            }    
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when Load Group button is pressed
     */
    public ActionForward loadGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("loadGroup() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        String batchDirectory = SpringContext.getBean(CorrectionDocumentService.class).getBatchFileDirectoryName();
        
        if (checkOriginEntryGroupSelection(correctionForm)) {
            GeneralLedgerCorrectionProcessDocument doc = (GeneralLedgerCorrectionProcessDocument) correctionForm.getDocument();
            doc.setCorrectionInputFileName(batchDirectory + File.separator + correctionForm.getInputGroupId());

            int inputGroupSize = originEntryService.getGroupCount(correctionForm.getInputGroupId());
            int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
            correctionForm.setPersistedOriginEntriesMissing(false);

            if (CorrectionDocumentUtils.isRestrictedFunctionalityMode(inputGroupSize, recordCountFunctionalityLimit)) {
                correctionForm.setRestrictedFunctionalityMode(true);
                correctionForm.setDataLoadedFlag(false);
                updateDocumentSummary(correctionForm.getCorrectionDocument(), null, true);

                if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
                    // the group size is not suitable for manual editing because it is too large
                    if (recordCountFunctionalityLimit == CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE) {
                        GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_ANY_GROUP);
                    }
                    else {
                        GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_LARGE_GROUP, String.valueOf(recordCountFunctionalityLimit));
                    }
                }
            }
            else {
                correctionForm.setRestrictedFunctionalityMode(false);
                
                loadAllEntries(correctionForm.getInputGroupId(), correctionForm);

                if (correctionForm.getAllEntries().size() > 0) {
                    if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
                        correctionForm.setManualEditFlag(true);
                        correctionForm.setEditableFlag(false);
                        correctionForm.setDeleteFileFlag(false);
                    }
                    correctionForm.setDataLoadedFlag(true);
                }
                else {
                    GlobalVariables.getMessageMap().putError("documentsInSystem", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_NO_RECORDS);
                }
            }

            GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
            if (document.getCorrectionChangeGroup().isEmpty()) {
                document.addCorrectionChangeGroup(new CorrectionChangeGroup());
            }

            correctionForm.setPreviousInputGroupId(correctionForm.getInputGroupId());
        }

        correctionForm.setShowOutputFlag(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Called when a group is selected to be deleted
     */
    public ActionForward confirmDeleteDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("confirmDeleteDocument() started");

        CorrectionForm correctionForm = (CorrectionForm) form;

        if (checkOriginEntryGroupSelection(correctionForm)) {
            //OriginEntryGroup oeg = originEntryGroupService.getExactMatchingEntryGroup(correctionForm.getInputGroupId());
            String batchDirectory = SpringContext.getBean(CorrectionDocumentService.class).getBatchFileDirectoryName();
            String doneFileName = batchDirectory + File.separator + correctionForm.getInputGroupId();
            String dataFileName = doneFileName.replace(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION, GeneralLedgerConstants.BatchFileSystem.EXTENSION);
            
            int groupCount = originEntryService.getGroupCount(dataFileName);
            int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();

            if (!CorrectionDocumentUtils.isRestrictedFunctionalityMode(groupCount, recordCountFunctionalityLimit)) {
                loadAllEntries(dataFileName, correctionForm);
                correctionForm.setDeleteFileFlag(true);
                correctionForm.setDataLoadedFlag(true);
                correctionForm.setRestrictedFunctionalityMode(false);
            }
            else {
                correctionForm.setRestrictedFunctionalityMode(true);
            }
            
            GeneralLedgerCorrectionProcessDocument document = (GeneralLedgerCorrectionProcessDocument) correctionForm.getDocument();
            document.setCorrectionInputFileName(dataFileName);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /*
     * Upload a file
     */
    public ActionForward uploadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException, Exception {
        LOG.debug("uploadFile() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = (GeneralLedgerCorrectionProcessDocument) correctionForm.getDocument();

        Date now = CorrectionAction.dateTimeService.getCurrentDate();
        //creat file after all enries loaded well
        //OriginEntryGroup newOriginEntryGroup = CorrectionAction.originEntryGroupService.createGroup(today, OriginEntrySource.GL_CORRECTION_PROCESS_EDOC, false, false, false);

        FormFile sourceFile = correctionForm.getSourceFile();
        String glcpDirectory = SpringContext.getBean(CorrectionDocumentService.class).getGlcpDirectoryName();
        String fullFileName = glcpDirectory + File.separator + sourceFile.getFileName() + "-" + CorrectionAction.dateTimeService.toDateTimeStringForFilename(now);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(sourceFile.getInputStream()));
        //create a file
        File uploadedFile = new File(fullFileName);
        PrintStream uploadedFilePrintStream;
        try {
            uploadedFilePrintStream = new PrintStream(uploadedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        //write entries to file
        int loadedCount = 0;
        String stringLine;
        while ((stringLine= br.readLine()) != null){
            try {
                uploadedFilePrintStream.printf("%s\n", stringLine);
                loadedCount++;
            } catch (Exception e) {
                throw new IOException(e.toString());
            }
        }
        uploadedFilePrintStream.close();
        
        
        int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
        if (CorrectionDocumentUtils.isRestrictedFunctionalityMode(loadedCount, recordCountFunctionalityLimit)) {
            correctionForm.setRestrictedFunctionalityMode(true);
            correctionForm.setDataLoadedFlag(false);
            document.setCorrectionInputFileName(fullFileName);
            correctionForm.setInputFileName(fullFileName);

            if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
                // the group size is not suitable for manual editing because it is too large
                if (recordCountFunctionalityLimit == CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE) {
                    GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_ANY_GROUP);
                }
                else {
                    GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_LARGE_GROUP, String.valueOf(recordCountFunctionalityLimit));
                }
            }
        }
        else {
            correctionForm.setRestrictedFunctionalityMode(false);
            if (loadedCount > 0) {
                //now we can load all data from file
                List<OriginEntryFull> originEntryList = new ArrayList();
                Map loadMessageMap = originEntryService.getEntriesByGroupIdWithPath(uploadedFile.getAbsolutePath(), originEntryList);
                //put errors on GlobalVariables
                if (loadMessageMap.size() > 0){
                    Iterator iter = loadMessageMap.keySet().iterator();
                    while(iter.hasNext()){
                        Integer lineNumber = (Integer) iter.next();
                        List<Message> messageList = (List<Message>) loadMessageMap.get(lineNumber);
                        if (messageList.size() > 0){
                            for (Message errorMmessage : messageList){
                                GlobalVariables.getMessageMap().putError("fileUpload", KFSKeyConstants.ERROR_INVALID_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE, new String[] {lineNumber.toString(), errorMmessage.toString()});
                            }    
                        }
                    }
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
                
                // Set all the data that we know
                correctionForm.setDataLoadedFlag(true);
                correctionForm.setInputFileName(fullFileName);
                document.setCorrectionInputFileName(fullFileName);
                loadAllEntries(originEntryList, correctionForm);

                if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
                    correctionForm.setEditableFlag(false);
                    correctionForm.setManualEditFlag(true);
                }
            }
            else {
                GlobalVariables.getMessageMap().putError("fileUpload", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_NO_RECORDS);
            }
        }

        if (document.getCorrectionChangeGroup().isEmpty()) {
            document.addCorrectionChangeGroup(new CorrectionChangeGroup());
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Add a correction group
     */
    public ActionForward addCorrectionGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addCorrectionGroup() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        document.addCorrectionChangeGroup(new CorrectionChangeGroup());
        correctionForm.syncGroups();

        if (!correctionForm.isRestrictedFunctionalityMode()) {
            correctionForm.getDisplayEntries().clear();
            correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());

            if (correctionForm.getShowOutputFlag()) {
                updateEntriesFromCriteria(correctionForm, correctionForm.isRestrictedFunctionalityMode());
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Remove a correction group
     */
    public ActionForward removeCorrectionGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("removeCorrectionGroup() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        int groupId = Integer.parseInt(getImageContext(request, "group"));

        document.removeCorrectionChangeGroup(groupId);
        correctionForm.syncGroups();

        validChangeGroups(correctionForm);

        correctionForm.getDisplayEntries().clear();
        correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());

        if (correctionForm.getShowOutputFlag()) {
            updateEntriesFromCriteria(correctionForm, correctionForm.isRestrictedFunctionalityMode());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Add a new correction criteria to the specified group
     */
    public ActionForward addCorrectionCriteria(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addCorrectionCriteria() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        // Find out which group they are on
        int changeGroupId = Integer.parseInt(getImageContext(request, "criteria"));

        CorrectionChangeGroup ccg = document.getCorrectionChangeGroupItem(changeGroupId);
        CorrectionCriteria cc = correctionForm.getGroupsItem(changeGroupId).getCorrectionCriteria();
        if (!CorrectionDocumentUtils.validCorrectionCriteriaForAdding(cc)) {
            if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionForm.getEditMethod())) {
                GlobalVariables.getMessageMap().putError("editCriteria", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_MUST_CHOOSE_FIELD_NAME_WHEN_ADDING_CRITERION);
            }
            else {
                GlobalVariables.getMessageMap().putError("manualEditCriteria", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_MUST_CHOOSE_FIELD_NAME_WHEN_ADDING_CRITERION);
            }
        }
        else {
            ccg.addCorrectionCriteria(cc);

            // clear it for the next new line
            correctionForm.getGroupsItem(changeGroupId).setCorrectionCriteria(new CorrectionCriteria());

            validChangeGroups(correctionForm);

            if (!correctionForm.isRestrictedFunctionalityMode()) {
                correctionForm.getDisplayEntries().clear();
                correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());

                if (correctionForm.getShowOutputFlag()) {
                    updateEntriesFromCriteria(correctionForm, correctionForm.isRestrictedFunctionalityMode());
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Remove a correction criteria from a group
     */
    public ActionForward removeCorrectionCriteria(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("removeCorrectionCriteria() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        String ids[] = getImageContext(request, "criteria").split("-");
        int group = Integer.parseInt(ids[0]);
        int item = Integer.parseInt(ids[1]);

        CorrectionChangeGroup ccg = document.getCorrectionChangeGroupItem(group);
        ccg.removeCorrectionCriteriaItem(item);

        validChangeGroups(correctionForm);

        if (!correctionForm.isRestrictedFunctionalityMode()) {
            correctionForm.getDisplayEntries().clear();
            correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());

            if (correctionForm.getShowOutputFlag()) {
                updateEntriesFromCriteria(correctionForm, correctionForm.isRestrictedFunctionalityMode());
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Remove a correction change from a group
     */
    public ActionForward removeCorrectionChange(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("removeCorrectionChange() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        String ids[] = getImageContext(request, "change").split("-");
        int group = Integer.parseInt(ids[0]);
        int item = Integer.parseInt(ids[1]);

        CorrectionChangeGroup ccg = document.getCorrectionChangeGroupItem(group);
        ccg.removeCorrectionChangeItem(item);

        validChangeGroups(correctionForm);

        if (!correctionForm.isRestrictedFunctionalityMode()) {
            correctionForm.getDisplayEntries().clear();
            correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());

            if (correctionForm.getShowOutputFlag()) {
                updateEntriesFromCriteria(correctionForm, correctionForm.isRestrictedFunctionalityMode());
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Add a new correction change to the specified group
     */
    public ActionForward addCorrectionChange(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addCorrectionChangeReplacementSpecification() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = (GeneralLedgerCorrectionProcessDocument) correctionForm.getDocument();

        // Find out which group they are on
        int changeGroupId = Integer.parseInt(getImageContext(request, "change"));

        CorrectionChangeGroup ccg = document.getCorrectionChangeGroupItem(changeGroupId);
        CorrectionChange cc = correctionForm.getGroupsItem(changeGroupId).getCorrectionChange();
        if (!CorrectionDocumentUtils.validCorrectionChangeForAdding(cc)) {
            GlobalVariables.getMessageMap().putError("editCriteria", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_MUST_CHOOSE_FIELD_NAME_WHEN_ADDING_CRITERION);
        }
        else {
            ccg.addCorrectionChange(cc);

            // clear it for the next new line
            correctionForm.getGroupsItem(changeGroupId).setCorrectionChange(new CorrectionChange());

            validChangeGroups(correctionForm);

            if (!correctionForm.isRestrictedFunctionalityMode()) {
                correctionForm.getDisplayEntries().clear();
                correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());

                if (correctionForm.getShowOutputFlag()) {
                    updateEntriesFromCriteria(correctionForm, correctionForm.isRestrictedFunctionalityMode());
                }
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Add a new row to the group
     */
    public ActionForward addManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addManualEdit() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        if (validOriginEntry(correctionForm)) {
            correctionForm.updateEntryForManualEdit();

            // new entryId is always 0, so give it a unique Id, SequenceAccessorService is used.
            //Long newEntryId = SpringContext.getBean(SequenceAccessorService.class).getNextAvailableSequenceNumber("GL_ORIGIN_ENTRY_T_SEQ");
            int newEntryId = getMaxEntryId(correctionForm.getAllEntries()) + 1;
            correctionForm.getEntryForManualEdit().setEntryId(new Integer(newEntryId));

            correctionForm.getAllEntries().add(correctionForm.getEntryForManualEdit());

            // Clear out the additional row
            correctionForm.clearEntryForManualEdit();
        }


        // Calculate the debit/credit/row count
        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());

        correctionForm.setShowSummaryOutputFlag(true);

        // we've modified the list of all entries, so repersist it
        SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(correctionForm.getGlcpSearchResultsSequenceNumber(), correctionForm.getAllEntries());
        correctionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(correctionForm.getAllEntries()));
        if (correctionForm.getShowOutputFlag()) {
            removeNonMatchingEntries(correctionForm.getDisplayEntries(), document.getCorrectionChangeGroup());
        }

        // list has changed, we'll need to repage and resort
        applyPagingAndSortingFromPreviousPageView(correctionForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Delete a row from the group
     */
    public ActionForward deleteManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("deleteManualEdit() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
        int entryId = Integer.parseInt(getImageContext(request, "entryId"));

        // Find it and remove it
        for (Iterator iter = correctionForm.getAllEntries().iterator(); iter.hasNext();) {
            OriginEntryFull element = (OriginEntryFull) iter.next();
            if (element.getEntryId() == entryId) {
                iter.remove();
                break;
            }
        }

        // Calculate the debit/credit/row count
        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());

        correctionForm.setShowSummaryOutputFlag(true);

        // we've modified the list of all entries, so repersist it
        SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(correctionForm.getGlcpSearchResultsSequenceNumber(), correctionForm.getAllEntries());
        correctionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(correctionForm.getAllEntries()));
        if (correctionForm.getShowOutputFlag()) {
            removeNonMatchingEntries(correctionForm.getDisplayEntries(), document.getCorrectionChangeGroup());
        }

        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();

        // list has changed, we'll need to repage and resort
        applyPagingAndSortingFromPreviousPageView(correctionForm);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Edit a row in the group
     */
    public ActionForward editManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("editManualEdit() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        int entryId = Integer.parseInt(getImageContext(request, "entryId"));

        // Find it and put it in the editing spot
        for (Iterator iter = correctionForm.getAllEntries().iterator(); iter.hasNext();) {
            OriginEntryFull element = (OriginEntryFull) iter.next();
            if (element.getEntryId() == entryId) {
                correctionForm.setEntryForManualEdit(element);
                correctionForm.setEntryFinancialDocumentReversalDate(CorrectionDocumentUtils.convertToString(element.getFinancialDocumentReversalDate(), "Date"));
                correctionForm.setEntryTransactionDate(CorrectionDocumentUtils.convertToString(element.getTransactionDate(), "Date"));
                correctionForm.setEntryTransactionLedgerEntryAmount(CorrectionDocumentUtils.convertToString(element.getTransactionLedgerEntryAmount(), "KualiDecimal"));
                correctionForm.setEntryTransactionLedgerEntrySequenceNumber(CorrectionDocumentUtils.convertToString(element.getTransactionLedgerEntrySequenceNumber(), "Integer"));
                correctionForm.setEntryUniversityFiscalYear(CorrectionDocumentUtils.convertToString(element.getUniversityFiscalYear(), "Integer"));
                break;
            }
        }

        correctionForm.setShowSummaryOutputFlag(true);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Save a changed row in the group
     */
    public ActionForward saveManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("saveManualEdit() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        if (validOriginEntry(correctionForm)) {
            int entryId = correctionForm.getEntryForManualEdit().getEntryId();

            // Find it and replace it with the one from the edit spot
            for (Iterator<OriginEntryFull> iter = correctionForm.getAllEntries().iterator(); iter.hasNext();) {
                OriginEntryFull element = iter.next();
                if (element.getEntryId() == entryId) {
                    iter.remove();
                }
            }

            correctionForm.updateEntryForManualEdit();
            correctionForm.getAllEntries().add(correctionForm.getEntryForManualEdit());

            // we've modified the list of all entries, so repersist it
            SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(correctionForm.getGlcpSearchResultsSequenceNumber(), correctionForm.getAllEntries());
            correctionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(correctionForm.getAllEntries()));

            if (correctionForm.getShowOutputFlag()) {
                removeNonMatchingEntries(correctionForm.getDisplayEntries(), document.getCorrectionChangeGroup());
            }

            // Clear out the additional row
            correctionForm.clearEntryForManualEdit();
        }

        // Calculate the debit/credit/row count
        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());

        // list has changed, we'll need to repage and resort
        applyPagingAndSortingFromPreviousPageView(correctionForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Toggle the show output/input flag and dataset
     */
    public ActionForward showOutputGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("showOutputGroup() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        if (validChangeGroups(correctionForm)) {
            correctionForm.getDisplayEntries().clear();
            correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());

            if (!correctionForm.getShowOutputFlag()) {
                LOG.debug("showOutputGroup() Changing to output group");
                updateEntriesFromCriteria(correctionForm, correctionForm.isRestrictedFunctionalityMode());
            }
            correctionForm.setShowOutputFlag(!correctionForm.getShowOutputFlag());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward searchForManualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LOG.debug("searchForManualEdit() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        correctionForm.setShowOutputFlag(true);
        correctionForm.getDisplayEntries().clear();
        correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());

        removeNonMatchingEntries(correctionForm.getDisplayEntries(), document.getCorrectionChangeGroup());

        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
        correctionForm.getOriginEntrySearchResultTableMetadata().jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward searchCancelForManualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LOG.debug("searchCancelForManualEdit() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        correctionForm.getDisplayEntries().clear();
        correctionForm.getDisplayEntries().addAll(correctionForm.getAllEntries());

        correctionForm.setShowOutputFlag(false);

        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
        correctionForm.getOriginEntrySearchResultTableMetadata().jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected boolean validOriginEntry(CorrectionForm correctionForm) {
        LOG.debug("validOriginEntry() started");

        OriginEntryFull oe = correctionForm.getEntryForManualEdit();

        boolean valid = true;
        OriginEntryFieldFinder oeff = new OriginEntryFieldFinder();
        List fields = oeff.getKeyValues();
        for (Iterator iter = fields.iterator(); iter.hasNext();) {
            KeyValue lkp = (KeyValue) iter.next();

            // Get field name, type, length & value on the form
            String fieldName = (String) lkp.getKey();
            String fieldDisplayName = lkp.getValue();
            String fieldType = oeff.getFieldType(fieldName);
            int fieldLength = oeff.getFieldLength(fieldName);
            String fieldValue = null;
            if ("String".equals(fieldType)) {
                fieldValue = (String) oe.getFieldValue(fieldName);
            }
            else if (KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE.equals(fieldName)) {
                fieldValue = correctionForm.getEntryFinancialDocumentReversalDate();
            }
            else if (KFSPropertyConstants.TRANSACTION_DATE.equals(fieldName)) {
                fieldValue = correctionForm.getEntryTransactionDate();
            }
            else if (KFSPropertyConstants.TRN_ENTRY_LEDGER_SEQUENCE_NUMBER.equals(fieldName)) {
                fieldValue = correctionForm.getEntryTransactionLedgerEntrySequenceNumber();
            }
            else if (KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT.equals(fieldName)) {
                fieldValue = correctionForm.getEntryTransactionLedgerEntryAmount();
            }
            else if (KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR.equals(fieldName)) {
                fieldValue = correctionForm.getEntryUniversityFiscalYear();
            }

            // Now check that the data is valid
            if (!StringUtils.isEmpty(fieldValue)) {
                if (!oeff.isValidValue(fieldName, fieldValue)) {
                    GlobalVariables.getMessageMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { fieldDisplayName, fieldValue });
                    valid = false;
                }
            }
            else if (!oeff.allowNull(fieldName)) {
                GlobalVariables.getMessageMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { fieldDisplayName, fieldValue });
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Show all entries for Manual edit with groupId and persist these entries to the DB The restricted functionality mode flag MUST
     * BE SET PRIOR TO CALLING this method.
     * 
     * @param groupId
     * @param correctionForm
     * @throws Exception
     */
    //protected void loadAllEntries(Integer groupId, CorrectionForm correctionForm) throws Exception {
    protected void loadAllEntries(String fileNameWithPath, CorrectionForm correctionForm) {
        LOG.debug("loadAllEntries() started");
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
        
        if (!correctionForm.isRestrictedFunctionalityMode()) {
            List<OriginEntryFull> searchResults = new ArrayList(); 
            Map loadMessageMap = originEntryService.getEntriesByGroupIdWithPath(fileNameWithPath, searchResults);

            //put errors on GlobalVariables
            if (loadMessageMap.size() > 0){
                Iterator iter = loadMessageMap.keySet().iterator();
                while(iter.hasNext()){
                    Integer lineNumber = (Integer) iter.next();
                    List<Message> messageList = (List<Message>) loadMessageMap.get(lineNumber);
                    for (Message errorMmessage : messageList){
                        GlobalVariables.getMessageMap().putError("fileUpload", KFSKeyConstants.ERROR_INVALID_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE, new String[] {lineNumber.toString(), errorMmessage.toString()});
                    }
                }
            } else {
                try {
                    loadAllEntries(searchResults, correctionForm);
                    
                } catch (Exception e){
                    throw new RuntimeException(e);
                } 
            }
        }
    }
    
    protected void loadAllEntries(List<OriginEntryFull> searchResults, CorrectionForm correctionForm) throws Exception {
        
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
        correctionForm.setAllEntries(searchResults);
        correctionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(searchResults));

        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());

        // if not in restricted functionality mode, then we can store these results temporarily in the GLCP origin entry service
        SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
        String glcpSearchResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE));

        SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(glcpSearchResultsSequenceNumber, searchResults);
        correctionForm.setGlcpSearchResultsSequenceNumber(glcpSearchResultsSequenceNumber);

        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();
        originEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
        originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
    }

    /**
     * Show all entries for Manual edit with groupId and persist these entries to the DB
     */
    
    //TODO: need to check - this method is using for docHandler, but commented out 
    protected void loadPersistedInputGroup(CorrectionForm correctionForm) throws Exception {

        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
        CorrectionDocumentService correctionDocumentService = SpringContext.getBean(CorrectionDocumentService.class);

        if (!correctionDocumentService.areInputOriginEntriesPersisted(document)) {
            // the input origin entry group has been purged from the system
            correctionForm.setPersistedOriginEntriesMissing(true);
            correctionForm.setRestrictedFunctionalityMode(true);
            return;
        }

        correctionForm.setPersistedOriginEntriesMissing(false);
        List<OriginEntryFull> searchResults = correctionDocumentService.retrievePersistedInputOriginEntries(document, recordCountFunctionalityLimit);

        if (searchResults == null) {
            // null when the origin entry list is too large (i.e. in restricted functionality mode)
            correctionForm.setRestrictedFunctionalityMode(true);
            updateDocumentSummary(document, null, true);
        }
        else {
            correctionForm.setAllEntries(searchResults);
            correctionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(searchResults));

            updateDocumentSummary(document, correctionForm.getAllEntries(), false);

            // if not in restricted functionality mode, then we can store these results temporarily in the GLCP origin entry service
            SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
            String glcpSearchResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE));

            SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(glcpSearchResultsSequenceNumber, searchResults);
            correctionForm.setGlcpSearchResultsSequenceNumber(glcpSearchResultsSequenceNumber);

            int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
            KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();
            originEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
            originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
        }
    }

    /**
     * Retrieves the output origin entries that were saved by the {@link #persistOriginEntryGroupsForDocumentSave(CorrectionForm)}
     * method
     * 
     * @param correctionForm
     * @param setSequentialIds if true and not in restricted functionality mode, a pseudo-entry id will be assigned to each of the
     *        elements in the form's allEntries attribute.
     * @throws Exception
     */
    protected void loadPersistedOutputGroup(CorrectionForm correctionForm, boolean setSequentialIds) throws Exception {

        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();

        CorrectionDocumentService correctionDocumentService = SpringContext.getBean(CorrectionDocumentService.class);
        if (!correctionDocumentService.areOutputOriginEntriesPersisted(document)) {
            // the input origin entry group has been purged from the system
            correctionForm.setPersistedOriginEntriesMissing(true);
            correctionForm.setRestrictedFunctionalityMode(true);
            return;
        }


        correctionForm.setPersistedOriginEntriesMissing(false);

        int recordCountFunctionalityLimit;
        if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
            // with manual edits, rows may have been added so that the list goes would go into restricted func mode
            // so for manual edits, we ignore this limit
            recordCountFunctionalityLimit = CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_UNLIMITED;
        }
        else {
            recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
        }

        List<OriginEntryFull> searchResults = correctionDocumentService.retrievePersistedOutputOriginEntries(document, recordCountFunctionalityLimit);

        if (searchResults == null) {
            // null when the origin entry list is too large (i.e. in restricted functionality mode)
            correctionForm.setRestrictedFunctionalityMode(true);
            WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

            Map<String, String> documentActions = correctionForm.getDocumentActions();
            if (documentActions.containsKey(AuthorizationConstants.EditMode.FULL_ENTRY) || workflowDocument.isCanceled()) {
                // doc in read/write mode or is cancelled, so the doc summary fields of the doc are unreliable, so clear them out
                updateDocumentSummary(document, null, true);
            }
            // else we defer to the values already in the doc, and just don't touch the values
        }
        else {
            correctionForm.setAllEntries(searchResults);
            correctionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(searchResults));

            if (setSequentialIds) {
                CorrectionDocumentUtils.setSequentialEntryIds(correctionForm.getAllEntries());
            }

            // if we can display the entries (i.e. not restricted functionality mode), then recompute the summary
            updateDocumentSummary(document, correctionForm.getAllEntries(), false);

            // if not in restricted functionality mode, then we can store these results temporarily in the GLCP origin entry service
            SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
            String glcpSearchResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE));

            SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(glcpSearchResultsSequenceNumber, searchResults);
            correctionForm.setGlcpSearchResultsSequenceNumber(glcpSearchResultsSequenceNumber);

            int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
            KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();
            originEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
            originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
        }
    }


    /**
     * Validate that choose system and edit method are selected
     */
    protected boolean checkMainDropdown(CorrectionForm errorCorrectionForm) {
        LOG.debug("checkMainDropdown() started");

        boolean ret = true;
        if (StringUtils.isEmpty(errorCorrectionForm.getChooseSystem())) {
            GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_SYSTEMFIELD_REQUIRED);
            ret = false;
        }
        if (StringUtils.isEmpty(errorCorrectionForm.getEditMethod())) {
            GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_EDITMETHODFIELD_REQUIRED);
            ret = false;
        }
        if (ret && CorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(errorCorrectionForm.getEditMethod()) && !CorrectionDocumentService.SYSTEM_DATABASE.equals(errorCorrectionForm.getChooseSystem())) {
            GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_REMOVE_GROUP_REQUIRES_DATABASE);
            ret = false;
        }
        return ret;
    }

    /**
     * This method checks that an origin entry group has been selected; and this method is intended to be used for selecting an
     * origin entry group when using the database method If a group has not been loaded, then an error will be added to the screen
     * 
     * @param correctionForm
     * @return
     */
    protected boolean checkOriginEntryGroupSelection(CorrectionForm correctionForm) {
        LOG.debug("checkOriginEntryGroupSelection() started");

        if (correctionForm.getInputGroupId() == null) {
            GlobalVariables.getMessageMap().putError("documentLoadError", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_ORIGINGROUP_REQUIRED);
            return false;
        }
        return true;
    }

    /**
     * This method checks that an origin entry group has been selected or uploaded, depending on the system of the document If a
     * group has not been loaded, then an error will be added to the screen
     * 
     * @param document
     * @return
     */
    protected boolean checkOriginEntryGroupSelectionBeforeRouting(GeneralLedgerCorrectionProcessDocument document) {
        if (document.getCorrectionInputFileName() == null) {
            GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_ORIGINGROUP_REQUIRED_FOR_ROUTING);
            return false;
        }
        return true;
    }

    /**
     * Validate all the correction groups
     * 
     * @param doc
     * @return if valid, return true, false if not
     */
    protected boolean validChangeGroups(CorrectionForm form) {
        LOG.debug("validChangeGroups() started");

        GeneralLedgerCorrectionProcessDocument doc = form.getCorrectionDocument();
        String tab = "";
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(form.getEditMethod())) {
            tab = "editCriteria";
        }
        else {
            tab = "manualEditCriteria";
        }

        boolean allValid = true;

        OriginEntryFieldFinder oeff = new OriginEntryFieldFinder();
        List fields = oeff.getKeyValues();

        List l = doc.getCorrectionChangeGroup();
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            CorrectionChangeGroup ccg = (CorrectionChangeGroup) iter.next();
            for (Iterator iterator = ccg.getCorrectionCriteria().iterator(); iterator.hasNext();) {
                CorrectionCriteria cc = (CorrectionCriteria) iterator.next();
                if (!oeff.isValidValue(cc.getCorrectionFieldName(), cc.getCorrectionFieldValue())) {
                    GlobalVariables.getMessageMap().putError(tab, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { oeff.getFieldDisplayName(cc.getCorrectionFieldName()), cc.getCorrectionFieldValue() });
                    allValid = false;
                }
            }
            for (Iterator iterator = ccg.getCorrectionChange().iterator(); iterator.hasNext();) {
                CorrectionChange cc = (CorrectionChange) iterator.next();
                if (!oeff.isValidValue(cc.getCorrectionFieldName(), cc.getCorrectionFieldValue())) {
                    GlobalVariables.getMessageMap().putError(tab, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { oeff.getFieldDisplayName(cc.getCorrectionFieldName()), cc.getCorrectionFieldValue() });
                    allValid = false;
                }
            }
        }
        return allValid;
    }

    /**
     * Sort CorrectionGroups and their search criteria and replacement specifications so they display properly on the page.
     */
    protected void sortForDisplay(List groups) {
        for (Iterator i = groups.iterator(); i.hasNext();) {
            CorrectionChangeGroup group = (CorrectionChangeGroup) i.next();
            Collections.sort(group.getCorrectionCriteria());
            Collections.sort(group.getCorrectionChange());
        }

        Collections.sort(groups);
    }

    protected void printChangeGroups(GeneralLedgerCorrectionProcessDocument doc) {
        List l = doc.getCorrectionChangeGroup();
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            CorrectionChangeGroup ccg = (CorrectionChangeGroup) iter.next();
            LOG.error("printChangeGroups() doc nbr: " + ccg.getDocumentNumber());
            LOG.error("printChangeGroups() ccg: " + ccg.getCorrectionChangeGroupLineNumber());
            for (Iterator iterator = ccg.getCorrectionCriteria().iterator(); iterator.hasNext();) {
                CorrectionCriteria cc = (CorrectionCriteria) iterator.next();
                LOG.error("printChangeGroups()      doc nbr: " + cc.getDocumentNumber());
                LOG.error("printChangeGroups()      group nbr: " + cc.getCorrectionChangeGroupLineNumber());
                LOG.error("printChangeGroups()      nbr:  " + cc.getCorrectionCriteriaLineNumber());
                LOG.error("printChangeGroups()      criteria " + cc.getCorrectionCriteriaLineNumber() + " " + cc.getCorrectionFieldName() + " " + cc.getCorrectionOperatorCode() + " " + cc.getCorrectionFieldValue());
            }
            for (Iterator iterator = ccg.getCorrectionChange().iterator(); iterator.hasNext();) {
                CorrectionChange cc = (CorrectionChange) iterator.next();
                LOG.error("printChangeGroups()      doc nbr: " + cc.getDocumentNumber());
                LOG.error("printChangeGroups()      group nbr: " + cc.getCorrectionChangeGroupLineNumber());
                LOG.error("printChangeGroups()      nbr:  " + cc.getCorrectionChangeLineNumber());
                LOG.error("printChangeGroups()      change " + cc.getCorrectionChangeLineNumber() + " " + cc.getCorrectionFieldName() + " " + cc.getCorrectionFieldValue());
            }
        }
    }

    protected void updateEntriesFromCriteria(CorrectionForm correctionForm, boolean clearOutSummary) {
        LOG.debug("updateEntriesFromCriteria() started");

        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
        List<CorrectionChangeGroup> changeCriteriaGroups = document.getCorrectionChangeGroup();

        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionForm.getEditMethod())) {
            applyCriteriaOnEntries(correctionForm.getDisplayEntries(), correctionForm.getMatchCriteriaOnly(), changeCriteriaGroups);
        }
        else if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
            applyCriteriaOnEntries(correctionForm.getDisplayEntries(), correctionForm.getShowOutputFlag(), changeCriteriaGroups);
        }
        else if (CorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(correctionForm.getEditMethod())) {
            // do nothing
        }

        // Calculate the debit/credit/row count
        updateDocumentSummary(document, correctionForm.getDisplayEntries(), clearOutSummary);

        // update the table rendering info
        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();
        originEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
    }

    /**
     * For criteria based edits, this method will generate the output group
     * 
     * @param entries a Collection of OriginEntryFull BOs, this collection and its elements may be directly modified as a result of
     *        this method
     * @param matchCriteriaOnly if true, only those entries that matched the criteria before changes were applied will remain in the
     *        collection
     * @param changeCriteriaGroups a list of criteria and change groups.
     */
    protected void applyCriteriaOnEntries(Collection<OriginEntryFull> entries, boolean matchCriteriaOnly, List<CorrectionChangeGroup> changeCriteriaGroups) {
        // Now, if they only want matches in the output group, go through them again and delete items that don't match any of the
        // groups
        // This means that matches within a group are ANDed and each group is ORed
        if (matchCriteriaOnly) {
            removeNonMatchingEntries(entries, changeCriteriaGroups);
        }

        for (OriginEntryFull oe : entries) {
            for (CorrectionChangeGroup ccg : changeCriteriaGroups) {
                int matches = 0;
                for (CorrectionCriteria cc : ccg.getCorrectionCriteria()) {
                    if (CorrectionDocumentUtils.entryMatchesCriteria(cc, oe)) {
                        matches++;
                    }
                }

                // If they all match, change it
                if (matches == ccg.getCorrectionCriteria().size()) {
                    for (CorrectionChange change : ccg.getCorrectionChange()) {
                        // Change the row
                        oe.setFieldValue(change.getCorrectionFieldName(), change.getCorrectionFieldValue());
                    }
                }
            }
        }
    }

    protected void removeNonMatchingEntries(Collection<OriginEntryFull> entries, Collection<CorrectionChangeGroup> groups) {
        Iterator<OriginEntryFull> oei = entries.iterator();
        while (oei.hasNext()) {
            OriginEntryFull oe = oei.next();
            if (!CorrectionDocumentUtils.doesEntryMatchAnyCriteriaGroups(oe, groups)) {
                oei.remove();
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiTableAction#switchToPage(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward switchToPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CorrectionForm correctionForm = (CorrectionForm) form;
        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();
        originEntrySearchResultTableMetadata.jumpToPage(originEntrySearchResultTableMetadata.getSwitchToPageNumber(), correctionForm.getDisplayEntries().size(), maxRowsPerPage);
        originEntrySearchResultTableMetadata.setColumnToSortIndex(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiTableAction#sort(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     *
     * KRAD Conversion: Uses the metadata of different columns created and selects the  
     * column name and column comparator and uses these properties to srt the list. 
     * There is no use of data dictionary.
     */
    public ActionForward sort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CorrectionForm correctionForm = (CorrectionForm) form;
        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();

        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();

        List<Column> columns = SpringContext.getBean(CorrectionDocumentService.class).getTableRenderColumnMetadata(correctionForm.getDocument().getDocumentNumber());

        String propertyToSortName = columns.get(originEntrySearchResultTableMetadata.getColumnToSortIndex()).getPropertyName();
        Comparator valueComparator = columns.get(originEntrySearchResultTableMetadata.getColumnToSortIndex()).getValueComparator();

        boolean sortDescending = false;
        if (originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() == originEntrySearchResultTableMetadata.getColumnToSortIndex()) {
            // clicked sort on the same column that was previously sorted, so we will reverse the sort order
            sortDescending = !originEntrySearchResultTableMetadata.isSortDescending();
            originEntrySearchResultTableMetadata.setSortDescending(sortDescending);
        }

        originEntrySearchResultTableMetadata.setSortDescending(sortDescending);
        // sort the list now so that it will be rendered correctly
        sortList(correctionForm.getDisplayEntries(), propertyToSortName, valueComparator, sortDescending);

        // sorting, so go back to the first page
        originEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected void sortList(List<OriginEntryFull> list, String propertyToSortName, Comparator valueComparator, boolean sortDescending) {
        if (list != null) {
            if (valueComparator instanceof NumericValueComparator || valueComparator instanceof TemporalValueComparator) {
                // hack alert: NumericValueComparator can only compare strings, so we use the KualiDecimal and Date's built in
                // mechanism compare values using
                // the comparable comparator
                valueComparator = new Comparator() {
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
     * KRAD Conversion: Uses the metadata of different columns created and selects the  
     * column name and column comparator and uses these properties to srt the list. 
     * There is no use of data dictionary.
     */
    protected void applyPagingAndSortingFromPreviousPageView(CorrectionForm correctionForm) {
        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();
        if (originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() != -1) {

            List<Column> columns = SpringContext.getBean(CorrectionDocumentService.class).getTableRenderColumnMetadata(correctionForm.getDocument().getDocumentNumber());

            String propertyToSortName = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getPropertyName();
            Comparator valueComparator = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getValueComparator();
            sortList(correctionForm.getDisplayEntries(), propertyToSortName, valueComparator, originEntrySearchResultTableMetadata.isSortDescending());
        }

        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
        originEntrySearchResultTableMetadata.jumpToPage(originEntrySearchResultTableMetadata.getViewedPageNumber(), correctionForm.getDisplayEntries().size(), maxRowsPerPage);
    }

    /**
     * This method restores the system and edit method to the selected values when the last call to the selectSystemAndMethod action
     * was made
     * 
     * @param correctionForm
     * @return if the system and edit method were changed while not in read only mode and the selectSystemEditMethod method was not
     *         being called if true, this is ususally not a good condition
     */
    protected boolean restoreSystemAndEditMethod(CorrectionForm correctionForm) {
        boolean readOnly = correctionForm.getEditingMode().get(AuthorizationConstants.EditMode.FULL_ENTRY) != null;
        if (!"selectSystemEditMethod".equals(correctionForm.getMethodToCall()) && !readOnly) {
            if (!StringUtils.equals(correctionForm.getPreviousEditMethod(), correctionForm.getEditMethod()) || !StringUtils.equals(correctionForm.getPreviousChooseSystem(), correctionForm.getChooseSystem())) {
                correctionForm.setChooseSystem(correctionForm.getPreviousChooseSystem());
                correctionForm.setEditMethod(correctionForm.getPreviousEditMethod());
                GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_SYSTEM_OR_EDIT_METHOD_CHANGE);
                return true;
            }
        }
        return false;
    }

    /**
     * For database-based edits, this method will determine if the input group has been changed since the last time the page was
     * rendered.
     * 
     * @param correctionForm
     * @return false if the input group was inappropriately changed without using the selectSystemEditMethod method
     */
    protected boolean restoreInputGroupSelectionForDatabaseEdits(CorrectionForm correctionForm) {
        if (!CorrectionDocumentService.SYSTEM_DATABASE.equals(correctionForm.getChooseSystem())) {
            return true;
        }
        if ("loadGroup".equals(correctionForm.getMethodToCall())) {
            return true;
        }
        GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
        if (correctionForm.isInputGroupIdFromLastDocumentLoadIsMissing()) {
            if (correctionForm.getInputGroupId() == null) {
                document.setCorrectionInputFileName(correctionForm.getInputGroupIdFromLastDocumentLoad());
            }
        }
        String currentInputGroupId = correctionForm.getInputGroupId();
        String previousInputGroupId = correctionForm.getPreviousInputGroupId();

        if (previousInputGroupId != null && (currentInputGroupId == null || !previousInputGroupId.equals(currentInputGroupId))) {
            document.setCorrectionInputFileName(previousInputGroupId);
            GlobalVariables.getMessageMap().putError("documentsInSystem", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_INPUT_GROUP_CHANGE);
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
    protected void updateDocumentSummary(GeneralLedgerCorrectionProcessDocument document, List<OriginEntryFull> entries, boolean clearOutSummary) {
        if (clearOutSummary) {
            document.setCorrectionCreditTotalAmount(null);
            document.setCorrectionDebitTotalAmount(null);
            document.setCorrectionBudgetTotalAmount(null);
            document.setCorrectionRowCount(null);
        }
        else {
            OriginEntryStatistics oes = CorrectionDocumentUtils.getStatistics(entries);
            CorrectionDocumentUtils.copyStatisticsToDocument(oes, document);
        }
    }

    /**
     * This method puts an error in the error map indicating that manual edit-ing GLCP docs may not be in
     * restrictedFunctionalityMode, if appropriate (Since we can't view results in this mode, we can't manually edit them)
     * 
     * @param correctionForm
     * @return true if we are not in restricted functionality mode or we are not manual editing
     */
    protected boolean checkRestrictedFunctionalityModeForManualEdit(CorrectionForm correctionForm) {
        if (correctionForm.isRestrictedFunctionalityMode() && CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
            int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();

            if (recordCountFunctionalityLimit == CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE) {
                GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_ANY_GROUP);
            }
            else {
                GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_LARGE_GROUP, String.valueOf(recordCountFunctionalityLimit));
            }
            return false;
        }
        return true;
    }

    protected boolean validGroupsItemsForDocumentSave(CorrectionForm correctionForm) {
        // validate the criteria in the "add" groups to ensure that the field name and value are blank
        for (int i = 0; i < correctionForm.getGroupsSize(); i++) {
            GroupHolder groupHolder = correctionForm.getGroupsItem(i);
            if (!CorrectionDocumentUtils.validCorrectionChangeForSaving(groupHolder.getCorrectionChange()) || !CorrectionDocumentUtils.validCorrectionCriteriaForSaving(groupHolder.getCorrectionCriteria())) {
                if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionForm.getEditMethod())) {
                    GlobalVariables.getMessageMap().putError("editCriteria", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_CRITERIA_TO_ADD_MUST_BE_BLANK_FOR_SAVE);
                }
                else {
                    GlobalVariables.getMessageMap().putError("manualEditCriteria", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_CRITERIA_TO_ADD_MUST_BE_BLANK_FOR_SAVE);
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether this document has the input group still persisted in the database. If the document is in the initiated state,
     * then it'll check whether the group exists in the originEntryGroupService. Otherwise, it'll check whether the group is
     * persisted in the correctionDocumentService.
     * 
     * @param correctionForm
     * @return
     */
    protected boolean checkInputGroupPersistedForDocumentSave(CorrectionForm correctionForm) {
        boolean present;
        WorkflowDocument workflowDocument = correctionForm.getDocument().getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || (workflowDocument.isSaved() && (correctionForm.getInputGroupIdFromLastDocumentLoad() == null || !correctionForm.getInputGroupIdFromLastDocumentLoad().equals(correctionForm.getInputGroupId())))) {
            present = originEntryGroupService.getGroupExists(((GeneralLedgerCorrectionProcessDocument) correctionForm.getDocument()).getCorrectionInputFileName());
        }
        else {
            present = SpringContext.getBean(CorrectionDocumentService.class).areInputOriginEntriesPersisted((GeneralLedgerCorrectionProcessDocument) correctionForm.getDocument());
        }
        if (!present) {
            GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING);
        }
        return present;
    }

    protected int getMaxEntryId(List<OriginEntryFull> entryList){
        int maxEntryId = 0;
        List<OriginEntryFull> sortList = entryList;
        Comparator<OriginEntryFull> comparator = new Comparator<OriginEntryFull>() {
            public int compare(OriginEntryFull o1, OriginEntryFull o2) {
                int compareValue = o2.getEntryId().compareTo(o1.getEntryId());

                return compareValue;
            
            }
        };
        Collections.sort(sortList, comparator); 
        maxEntryId = sortList.get(0).getEntryId();
        return maxEntryId;
    }
    
    public ActionForward superExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return super.execute(mapping, form, request, response);
    }
    
    public ActionForward superSave(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return super.save(mapping, form, request, response);
    }
    
}
