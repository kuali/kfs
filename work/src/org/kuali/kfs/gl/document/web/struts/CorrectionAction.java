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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.Constants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.SequenceAccessorService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.comparator.NumericValueComparator;
import org.kuali.core.web.comparator.TemporalValueComparator;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.core.web.struts.action.KualiTableRenderAction;
import org.kuali.core.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.core.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.core.web.ui.Column;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.CorrectionChange;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.CorrectionCriteria;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.document.CorrectionDocumentAuthorizer;
import org.kuali.module.gl.exception.LoadException;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.impl.CorrectionDocumentServiceImpl;
import org.kuali.module.gl.util.CorrectionDocumentUtils;
import org.kuali.module.gl.util.OriginEntryStatistics;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.gl.web.optionfinder.CorrectionGroupEntriesFinder;
import org.kuali.module.gl.web.optionfinder.OriginEntryFieldFinder;
import org.kuali.module.gl.web.struts.form.CorrectionForm;
import org.kuali.module.gl.web.struts.form.GroupHolder;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.clientapp.IDocHandler;

public class CorrectionAction extends KualiDocumentActionBase implements KualiTableRenderAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionAction.class);

    private static OriginEntryGroupService originEntryGroupService;
    private static OriginEntryService originEntryService;
    private static DateTimeService dateTimeService;
    private static KualiConfigurationService kualiConfigurationService;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("execute() started");

        // Init our services once
        if (originEntryGroupService == null) {
            CorrectionAction.originEntryGroupService = SpringServiceLocator.getOriginEntryGroupService();
            CorrectionAction.originEntryService = SpringServiceLocator.getOriginEntryService();
            CorrectionAction.dateTimeService = SpringServiceLocator.getDateTimeService();
            CorrectionAction.kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
        }

        request.setAttribute("debug", Boolean.valueOf(kualiConfigurationService.getApplicationParameterIndicator("GL.GLCP", "GL.DEBUG")));

        CorrectionForm rForm = (CorrectionForm) form;
        LOG.debug("execute() methodToCall: " + rForm.getMethodToCall());

        Collection<OriginEntry> persistedOriginEntries = null;
        
        // If we are called from the docHandler or reload, ignore the persisted origin entries because we are either creating a new document
        // or loading an old one
        if (!(Constants.DOC_HANDLER_METHOD.equals(rForm.getMethodToCall()) && Constants.RELOAD_METHOD_TO_CALL.equals(rForm.getMethodToCall()))) {
            restoreSystemAndEditMethod(rForm);
            restoreInputGroupSelectionForDatabaseEdits(rForm);
            if (!rForm.isRestrictedFunctionalityMode()) {
                if (StringUtils.isNotBlank(rForm.getGlcpSearchResultsSequenceNumber())) {
                    rForm.setAllEntries(SpringServiceLocator.getGlCorrectionProcessOriginEntryService().retrieveAllEntries(rForm.getGlcpSearchResultsSequenceNumber()));
                    if (rForm.getAllEntries() == null) { 
                        rForm.setDisplayEntries(null);
                    }
                    else {
                        rForm.setDisplayEntries(new ArrayList<OriginEntry> (rForm.getAllEntries()));
                    }
                
                    if ((!"showOutputGroup".equals(rForm.getMethodToCall())) && rForm.getShowOutputFlag()) {
                        // reapply the any criteria to pare down the list if the match criteria only flag is checked
                        CorrectionDocument document = rForm.getCorrectionDocument();
                        List<CorrectionChangeGroup> groups = document.getCorrectionChangeGroup();
                        updateEntriesFromCriteria(rForm, rForm.isRestrictedFunctionalityMode());
                    }
                    
                    if (!KFSConstants.TableRenderConstants.SORT_METHOD.equals(rForm.getMethodToCall())) {
                        // if sorting, we'll let the action take care of the sorting
                        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = rForm.getOriginEntrySearchResultTableMetadata();
                        if (originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() != -1) {
                            List<Column> columns = SpringServiceLocator.getCorrectionDocumentService().getTableRenderColumnMetadata(rForm.getDocument().getDocumentNumber());
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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

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
        
        // Populate document
        document.setCorrectionTypeCode(correctionForm.getEditMethod());
        document.setCorrectionSelection(correctionForm.getMatchCriteriaOnly());
        document.setCorrectionFileDelete(!correctionForm.getProcessInBatch());
        document.setCorrectionInputFileName(correctionForm.getInputFileName());
        document.setCorrectionOutputFileName(null); // this field is never used
        if (correctionForm.getDataLoadedFlag() || correctionForm.isRestrictedFunctionalityMode()) {
            document.setCorrectionInputGroupId(correctionForm.getInputGroupId());
        }
        else {
            document.setCorrectionInputGroupId(null);
        }
        document.setCorrectionOutputGroupId(null);

        persistOriginEntryGroupsForDocumentSave(correctionForm);
        
        LOG.debug("save() doc type name: " + correctionForm.getDocTypeName());
        return super.save(mapping, form, request, response);
    }

    /**
     * This needs to be done just in case they decide to save it when closing.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#close(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("close() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        // Populate document
        document.setCorrectionTypeCode(correctionForm.getEditMethod());
        document.setCorrectionSelection(correctionForm.getMatchCriteriaOnly());
        document.setCorrectionFileDelete(!correctionForm.getProcessInBatch());
        document.setCorrectionInputFileName(correctionForm.getInputFileName());
        document.setCorrectionOutputFileName(null); // this field is never used
        document.setCorrectionInputGroupId(correctionForm.getInputGroupId());
        document.setCorrectionOutputGroupId(null);

        return super.close(mapping, form, request, response);
    }

    /**
     * Prepare for routing. Return true if we're good to route, false if not
     * 
     * @param correctionForm
     * @return
     */
    private boolean prepareForRoute(CorrectionForm correctionForm) throws Exception {
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        // Is there a description?
        if (StringUtils.isEmpty(document.getDocumentHeader().getFinancialDocumentDescription())) {
            GlobalVariables.getErrorMap().putError("document.documentHeader.financialDocumentDescription", KFSKeyConstants.ERROR_DOCUMENT_NO_DESCRIPTION);
            return false;
        }

        // Did they pick the edit method and system?
        if (!checkMainDropdown(correctionForm)) {
            return false;
        }

        if (correctionForm.getDataLoadedFlag() || correctionForm.isRestrictedFunctionalityMode()) {
            document.setCorrectionInputGroupId(correctionForm.getInputGroupId());
        }
        else {
            document.setCorrectionInputGroupId(null);
        }
        if (!checkOriginEntryGroupSelectionBeforeRouting(document)) {
            return false;
        }
        
        // were the system and edit methods inappropriately changed?
        if (GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_SYSTEM_OR_EDIT_METHOD_CHANGE)) {
            return false;
        }
        
        // was the input group inappropriately changed?
        if (GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_INPUT_GROUP_CHANGE)) {
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
        
        // Get the output group if necessary
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionForm.getEditMethod())) {
            loadAllEntries(correctionForm.getInputGroupId(), correctionForm);
            updateEntriesFromCriteria(correctionForm, false);
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
        document.setCorrectionInputFileName(correctionForm.getInputFileName());
        document.setCorrectionOutputFileName(null); // this field is never used
        
        // we'll populate the output group id when the doc has a route level change
        document.setCorrectionOutputGroupId(null);

        persistOriginEntryGroupsForDocumentSave(correctionForm);
        
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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        correctionForm.clearEntryForManualEdit();
        correctionForm.setEditableFlag(true);
        correctionForm.setManualEditFlag(false);

        document.addCorrectionChangeGroup(new CorrectionChangeGroup());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Called when the document is loaded from action list or doc search or a new document is created.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("docHandler() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        String command = correctionForm.getCommand();

        if (IDocHandler.INITIATE_COMMAND.equals(command)) {
            correctionForm.clearForm();
            createDocument(correctionForm);
        }
        else {
            loadDocument(correctionForm);
            CorrectionDocument document = correctionForm.getCorrectionDocument();

            CorrectionDocumentAuthorizer cda = new CorrectionDocumentAuthorizer();
            Map editingMode = cda.getEditMode(document, GlobalVariables.getUserSession().getUniversalUser());

            if (editingMode.get(AuthorizationConstants.TransactionalEditMode.FULL_ENTRY) != null) {
                // They have saved the document and they are retreiving it to be completed
                correctionForm.setProcessInBatch(!document.getCorrectionFileDelete());
                correctionForm.setMatchCriteriaOnly(document.getCorrectionSelection());
                if (document.getCorrectionInputGroupId() != null) {
                    if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(document.getCorrectionTypeCode())) {
                        loadAllEntries(document.getCorrectionInputGroupId(), correctionForm);
                    }
                    else if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(document.getCorrectionTypeCode())) {
                        loadSavedManuallyEditedEntries(correctionForm);
                        correctionForm.setManualEditFlag(true);
                        correctionForm.setEditableFlag(false);
                        correctionForm.setDeleteFileFlag(false);
                    }
                    else {
                        throw new RuntimeException("Unknown edit method " + document.getCorrectionTypeCode());
                    }
                    correctionForm.setDataLoadedFlag(true);
                }
                else {
                    correctionForm.setDataLoadedFlag(false);
                }
                correctionForm.setShowOutputFlag(false);
                correctionForm.setInputFileName(document.getCorrectionInputFileName());
                correctionForm.setInputGroupId(document.getCorrectionInputGroupId());
                if (document.getCorrectionInputFileName() != null) {
                    correctionForm.setChooseSystem(CorrectionDocumentService.SYSTEM_UPLOAD);
                }
                else {
                    correctionForm.setChooseSystem(CorrectionDocumentService.SYSTEM_DATABASE);
                }
                correctionForm.setEditMethod(document.getCorrectionTypeCode());
                
                correctionForm.setPreviousChooseSystem(correctionForm.getChooseSystem());
                correctionForm.setPreviousEditMethod(correctionForm.getEditMethod());
                correctionForm.setPreviousInputGroupId(correctionForm.getInputGroupId());
            }
            else {
                // They are calling this from their action list to look at it or approve it
                correctionForm.setProcessInBatch(!document.getCorrectionFileDelete());
                correctionForm.setMatchCriteriaOnly(document.getCorrectionSelection());
                loadAllEntries(document.getCorrectionOutputGroupId(), correctionForm);
                correctionForm.setShowOutputFlag(true);
            }
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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        if (checkMainDropdown(correctionForm)) {
            // Clear out any entries that were already loaded
            document.setCorrectionInputFileName(null);
            document.setCorrectionInputGroupId(null);
            document.setCorrectionOutputFileName(null);
            document.setCorrectionOutputGroupId(null);
            document.setCorrectionCreditTotalAmount(null);
            document.setCorrectionDebitTotalAmount(null);
            document.setCorrectionRowCount(null);
            document.getCorrectionChangeGroup().clear();
            
            correctionForm.setDataLoadedFlag(false);
            correctionForm.setDeleteFileFlag(false);
            correctionForm.setEditableFlag(false);
            correctionForm.setManualEditFlag(false);
            correctionForm.setShowOutputFlag(false);
            correctionForm.setAllEntries(new ArrayList<OriginEntry>());
            correctionForm.setRestrictedFunctionalityMode(false);
            correctionForm.setProcessInBatch(true);
            
            if (CorrectionDocumentService.SYSTEM_DATABASE.equals(correctionForm.getChooseSystem())) {
                // if users choose database, then get the list of origin entry groups and set the default

                // I shouldn't have to do this query twice, but with the current architecture, I can't find anyway not to do it.
                CorrectionGroupEntriesFinder f = new CorrectionGroupEntriesFinder();
                List values = f.getKeyValues();
                if (values.size() > 0) {
                    OriginEntryGroup g = CorrectionAction.originEntryGroupService.getNewestScrubberErrorGroup();
                    if (g != null) {
                        correctionForm.setInputGroupId(g.getId());
                    }
                    else {
                        KeyLabelPair klp = (KeyLabelPair) values.get(0);
                        correctionForm.setInputGroupId(Integer.parseInt((String) klp.getKey()));
                    }
                }
                else {
                    GlobalVariables.getErrorMap().putError("systemAndEditMethod", KFSKeyConstants.ERROR_NO_ORIGIN_ENTRY_GROUPS);
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
            OriginEntryGroup oeg = CorrectionAction.originEntryGroupService.getExactMatchingEntryGroup(correctionForm.getInputGroupId());

            String fileName = oeg.getSource().getCode() + oeg.getId().toString() + "_" + oeg.getDate().toString() + ".txt";

            // set response
            response.setContentType("application/txt");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");

            BufferedOutputStream bw = new BufferedOutputStream(response.getOutputStream());

            // write to output
            CorrectionAction.originEntryService.flatFile(correctionForm.getInputGroupId(), bw);

            bw.flush();
            bw.close();

            return null;
        }
        else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    /**
     * Called when Load Group button is pressed
     */
    public ActionForward loadGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)  throws Exception {
        LOG.debug("loadGroup() started");

        CorrectionForm correctionForm = (CorrectionForm) form;

        if (checkOriginEntryGroupSelection(correctionForm)) {
            CorrectionDocument doc = (CorrectionDocument) correctionForm.getDocument();
            doc.setCorrectionInputGroupId(correctionForm.getInputGroupId());

            int inputGroupSize = originEntryService.getGroupCount(correctionForm.getInputGroupId());
            int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
            
            if (CorrectionDocumentUtils.isRestrictedFunctionalityMode(inputGroupSize, recordCountFunctionalityLimit)) {
                correctionForm.setRestrictedFunctionalityMode(true);
                correctionForm.setDataLoadedFlag(false);
                updateDocumentSummary(correctionForm.getCorrectionDocument(), null, true);
                
                if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
                    // the group size is not suitable for manual editing because it is too large
                    if (recordCountFunctionalityLimit == CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE) {
                        GlobalVariables.getErrorMap().putError("systemAndEditMethod",
                                KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_ANY_GROUP);
                    }
                    else {
                        GlobalVariables.getErrorMap().putError("systemAndEditMethod",
                                KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_LARGE_GROUP, String.valueOf(recordCountFunctionalityLimit));
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
                    GlobalVariables.getErrorMap().putError("documentsInSystem", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_NO_RECORDS);
                }
            }
            
            CorrectionDocument document = correctionForm.getCorrectionDocument();
            if (document.getCorrectionChangeGroup().isEmpty()) {
                document.addCorrectionChangeGroup(new CorrectionChangeGroup());
            }
            
            correctionForm.setPreviousInputGroupId(correctionForm.getInputGroupId());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Called when a group is selected to be deleted
     */
    public ActionForward confirmDeleteDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)  throws Exception {
        LOG.debug("confirmDeleteDocument() started");

        CorrectionForm correctionForm = (CorrectionForm) form;

        if (checkOriginEntryGroupSelection(correctionForm)) {
            OriginEntryGroup oeg = CorrectionAction.originEntryGroupService.getExactMatchingEntryGroup(correctionForm.getInputGroupId());
            if (oeg.getProcess()) {
                loadAllEntries(correctionForm.getInputGroupId(), correctionForm);
                correctionForm.setDeleteFileFlag(true);
                correctionForm.setDataLoadedFlag(true);
            }
            else {
                GlobalVariables.getErrorMap().putError("documentsInSystem", KFSKeyConstants.ERROR_GL_ERROR_GROUP_ALREADY_MARKED_NO_PROCESS);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    public ActionForward deleteGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LOG.debug("deleteGroup() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        CorrectionAction.originEntryGroupService.dontProcessGroup(correctionForm.getInputGroupId());
        correctionForm.setEditMethod("");
        correctionForm.setChooseSystem("");
        document.setCorrectionInputFileName(null);
        document.setCorrectionInputGroupId(null);
        document.setCorrectionOutputFileName(null);
        document.setCorrectionOutputGroupId(null);
        document.setCorrectionCreditTotalAmount(null);
        document.setCorrectionDebitTotalAmount(null);
        document.setCorrectionRowCount(null);
        correctionForm.setDataLoadedFlag(false);
        correctionForm.setDeleteFileFlag(false);
        correctionForm.setEditableFlag(false);
        correctionForm.setManualEditFlag(false);
        correctionForm.setShowOutputFlag(false);
        correctionForm.setAllEntries(new ArrayList());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /*
     * Upload a file
     */
    public ActionForward uploadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException, Exception {
        LOG.debug("uploadFile() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        CorrectionDocument document = (CorrectionDocument) correctionForm.getDocument();

        java.sql.Date today = CorrectionAction.dateTimeService.getCurrentSqlDate();
        OriginEntryGroup newOriginEntryGroup = CorrectionAction.originEntryGroupService.createGroup(today, OriginEntrySource.GL_CORRECTION_PROCESS_EDOC, false, false, false);

        FormFile sourceFile = correctionForm.getSourceFile();

        String fullFileName = sourceFile.getFileName();

        sourceFile.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(sourceFile.getInputStream()));
        int lineNumber = 0;
        int loadedCount = 0;
        boolean errorsLoading = false;
        try {
            String currentLine = br.readLine();
            while (currentLine != null) {
                lineNumber++;
                if (!StringUtils.isEmpty(currentLine)) {
                    try {
                        OriginEntry entryFromFile = new OriginEntry();
                        entryFromFile.setFromTextFile(currentLine, lineNumber);
                        entryFromFile.setEntryGroupId(newOriginEntryGroup.getId());
                        originEntryService.createEntry(entryFromFile, newOriginEntryGroup);
                        loadedCount++;
                    }
                    catch (LoadException e) {
                        errorsLoading = true;
                    }
                }
                currentLine = br.readLine();
            }
        }
        finally {
            br.close();
        }

        int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
        if (CorrectionDocumentUtils.isRestrictedFunctionalityMode(loadedCount, recordCountFunctionalityLimit)) {
            correctionForm.setRestrictedFunctionalityMode(true);
            correctionForm.setDataLoadedFlag(false);
            correctionForm.setInputGroupId(newOriginEntryGroup.getId());
            correctionForm.setInputFileName(fullFileName);
            
            if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
                // the group size is not suitable for manual editing because it is too large
                if (recordCountFunctionalityLimit == CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE) {
                    GlobalVariables.getErrorMap().putError("systemAndEditMethod",
                            KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_ANY_GROUP);
                }
                else {
                    GlobalVariables.getErrorMap().putError("systemAndEditMethod",
                            KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_LARGE_GROUP, String.valueOf(recordCountFunctionalityLimit));
                }
            }
        }
        else {
            correctionForm.setRestrictedFunctionalityMode(false);
            if (loadedCount > 0) {
                // Set all the data that we know
                correctionForm.setDataLoadedFlag(true);
                correctionForm.setInputFileName(fullFileName);
                correctionForm.setInputGroupId(newOriginEntryGroup.getId());
                loadAllEntries(newOriginEntryGroup.getId(), correctionForm);
    
                if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
                    correctionForm.setEditableFlag(false);
                    correctionForm.setManualEditFlag(true);
                }
            }
            else {
                GlobalVariables.getErrorMap().putError("fileUpload", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_NO_RECORDS);
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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        // Find out which group they are on
        int changeGroupId = Integer.parseInt(getImageContext(request, "criteria"));

        CorrectionChangeGroup ccg = document.getCorrectionChangeGroupItem(changeGroupId);
        CorrectionCriteria cc = correctionForm.getGroupsItem(changeGroupId).getCorrectionCriteria();
        if (!CorrectionDocumentUtils.validCorrectionCriteriaForAdding(cc)) {
            if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionForm.getEditMethod())) {
                GlobalVariables.getErrorMap().putError("editCriteria", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_MUST_CHOOSE_FIELD_NAME_WHEN_ADDING_CRITERION);
            }
            else {
                GlobalVariables.getErrorMap().putError("manualEditCriteria", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_MUST_CHOOSE_FIELD_NAME_WHEN_ADDING_CRITERION);
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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

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
        CorrectionDocument document = (CorrectionDocument) correctionForm.getDocument();

        // Find out which group they are on
        int changeGroupId = Integer.parseInt(getImageContext(request, "change"));

        CorrectionChangeGroup ccg = document.getCorrectionChangeGroupItem(changeGroupId);
        CorrectionChange cc = correctionForm.getGroupsItem(changeGroupId).getCorrectionChange();
        if (!CorrectionDocumentUtils.validCorrectionChangeForAdding(cc)) {
            GlobalVariables.getErrorMap().putError("editCriteria", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_MUST_CHOOSE_FIELD_NAME_WHEN_ADDING_CRITERION);
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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        if (validOriginEntry(correctionForm)) {
            correctionForm.updateEntryForManualEdit();

            // new entryId is always 0, so give it a unique Id, SequenceAccessorService is used.
            Long newEntryId = SpringServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("GL_ORIGIN_ENTRY_T_SEQ");
            correctionForm.getEntryForManualEdit().setEntryId(new Integer(newEntryId.intValue()));

            correctionForm.getAllEntries().add(correctionForm.getEntryForManualEdit());

            // Clear out the additional row
            correctionForm.clearEntryForManualEdit();
        }


        // Calculate the debit/credit/row count
        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());

        correctionForm.setShowSummaryOutputFlag(true);

        // we've modified the list of all entries, so repersist it
        SpringServiceLocator.getGlCorrectionProcessOriginEntryService().persistAllEntries(correctionForm.getGlcpSearchResultsSequenceNumber(), correctionForm.getAllEntries());
        correctionForm.setDisplayEntries(new ArrayList<OriginEntry>(correctionForm.getAllEntries()));
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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        int entryId = Integer.parseInt(getImageContext(request, "entryId"));

        // Find it and remove it
        for (Iterator iter = correctionForm.getAllEntries().iterator(); iter.hasNext();) {
            OriginEntry element = (OriginEntry) iter.next();
            if (element.getEntryId() == entryId) {
                iter.remove();
                break;
            }
        }

        // Calculate the debit/credit/row count
        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());

        correctionForm.setShowSummaryOutputFlag(true);

        // we've modified the list of all entries, so repersist it
        SpringServiceLocator.getGlCorrectionProcessOriginEntryService().persistAllEntries(correctionForm.getGlcpSearchResultsSequenceNumber(), correctionForm.getAllEntries());
        correctionForm.setDisplayEntries(new ArrayList<OriginEntry>(correctionForm.getAllEntries()));
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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        int entryId = Integer.parseInt(getImageContext(request, "entryId"));

        // Find it and put it in the editing spot
        for (Iterator iter = correctionForm.getAllEntries().iterator(); iter.hasNext();) {
            OriginEntry element = (OriginEntry) iter.next();
            if (element.getEntryId() == entryId) {
                correctionForm.setEntryForManualEdit(element);
                correctionForm.setEntryFinancialDocumentReversalDate(convertToString(element.getFinancialDocumentReversalDate(), "Date"));
                correctionForm.setEntryTransactionDate(convertToString(element.getTransactionDate(), "Date"));
                correctionForm.setEntryTransactionLedgerEntryAmount(convertToString(element.getTransactionLedgerEntryAmount(), "KualiDecimal"));
                correctionForm.setEntryTransactionLedgerEntrySequenceNumber(convertToString(element.getTransactionLedgerEntrySequenceNumber(), "Integer"));
                correctionForm.setEntryUniversityFiscalYear(convertToString(element.getUniversityFiscalYear(), "Integer"));
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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        if (validOriginEntry(correctionForm)) {
            int entryId = correctionForm.getEntryForManualEdit().getEntryId();

            // Find it and replace it with the one from the edit spot
            for (Iterator<OriginEntry> iter = correctionForm.getAllEntries().iterator(); iter.hasNext();) {
                OriginEntry element = iter.next();
                if (element.getEntryId() == entryId) {
                    iter.remove();
                }
            }

            correctionForm.updateEntryForManualEdit();
            correctionForm.getAllEntries().add(correctionForm.getEntryForManualEdit());

            // we've modified the list of all entries, so repersist it
            SpringServiceLocator.getGlCorrectionProcessOriginEntryService().persistAllEntries(correctionForm.getGlcpSearchResultsSequenceNumber(), correctionForm.getAllEntries());
            correctionForm.setDisplayEntries(new ArrayList<OriginEntry>(correctionForm.getAllEntries()));
            
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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

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
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        correctionForm.setShowOutputFlag(false);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    private boolean validOriginEntry(CorrectionForm correctionForm) {
        LOG.debug("validOriginEntry() started");

        OriginEntry oe = correctionForm.getEntryForManualEdit();

        boolean valid = true;
        OriginEntryFieldFinder oeff = new OriginEntryFieldFinder();
        List fields = oeff.getKeyValues();
        for (Iterator iter = fields.iterator(); iter.hasNext();) {
            KeyLabelPair lkp = (KeyLabelPair) iter.next();

            // Get field name, type, length & value on the form
            String fieldName = (String) lkp.getKey();
            String fieldDisplayName = lkp.getLabel();
            String fieldType = oeff.getFieldType(fieldName);
            int fieldLength = oeff.getFieldLength(fieldName);
            String fieldValue = null;
            if ("String".equals(fieldType)) {
                fieldValue = (String) oe.getFieldValue(fieldName);
            }
            else if ("financialDocumentReversalDate".equals(fieldName)) {
                fieldValue = correctionForm.getEntryFinancialDocumentReversalDate();
            }
            else if ("transactionDate".equals(fieldName)) {
                fieldValue = correctionForm.getEntryTransactionDate();
            }
            else if ("transactionLedgerEntrySequenceNumber".equals(fieldName)) {
                fieldValue = correctionForm.getEntryTransactionLedgerEntrySequenceNumber();
            }
            else if ("transactionLedgerEntryAmount".equals(fieldName)) {
                fieldValue = correctionForm.getEntryTransactionLedgerEntryAmount();
            }
            else if ("universityFiscalYear".equals(fieldName)) {
                fieldValue = correctionForm.getEntryUniversityFiscalYear();
            }

            // Now check that the data is valid
            if (!StringUtils.isEmpty(fieldValue)) {
                if (!oeff.isValidValue(fieldName, fieldValue)) {
                    GlobalVariables.getErrorMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { fieldDisplayName, fieldValue });
                    valid = false;
                }
            }
            else if (!oeff.allowNull(fieldName)) {
                GlobalVariables.getErrorMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { fieldDisplayName, fieldValue });
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Show all entries for Manual edit with groupId and persist these entries to the DB
     * 
     */
    private void loadAllEntries(Integer groupId, CorrectionForm correctionForm) throws Exception {
        LOG.debug("loadAllEntries() started");

        CorrectionDocument document = correctionForm.getCorrectionDocument();

        // TODO: move the group retrieval down to the if statement if possible to conserve more space
        List<OriginEntry> searchResults = retrieveGroup(groupId);
        
        correctionForm.setAllEntries(searchResults);
        correctionForm.setDisplayEntries(new ArrayList<OriginEntry> (searchResults));

        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());
        
        if (!correctionForm.isRestrictedFunctionalityMode()) {
            // if not in restricted functionality mode, then we can store these results temporarily in the GLCP origin entry service
            SequenceAccessorService sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
            String glcpSearchResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KFSConstants.LOOKUP_RESULTS_SEQUENCE));
            
            SpringServiceLocator.getGlCorrectionProcessOriginEntryService().persistAllEntries(glcpSearchResultsSequenceNumber, searchResults);
            correctionForm.setGlcpSearchResultsSequenceNumber(glcpSearchResultsSequenceNumber);
        
            int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
            KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();
            originEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
            originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
        }
    }
    
    /**
     * Show all entries for Manual edit with groupId and persist these entries to the DB
     * 
     * BE CAREFUL about calling this method for manual edit documents.  
     */
    private void loadSavedManuallyEditedEntries(CorrectionForm correctionForm) throws Exception {
        LOG.debug("loadAllEntries() started");

        CorrectionDocument document = correctionForm.getCorrectionDocument();

        List<OriginEntry> searchResults = SpringServiceLocator.getCorrectionDocumentService().retrievePersistedOutputOriginEntries(document);
        
        correctionForm.setAllEntries(searchResults);
        correctionForm.setDisplayEntries(new ArrayList<OriginEntry> (searchResults));
        
        // Calculate the debit/credit/row count
        updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());
        
        SequenceAccessorService sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
        String glcpSearchResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KFSConstants.LOOKUP_RESULTS_SEQUENCE));
        
        SpringServiceLocator.getGlCorrectionProcessOriginEntryService().persistAllEntries(glcpSearchResultsSequenceNumber, searchResults);
        correctionForm.setGlcpSearchResultsSequenceNumber(glcpSearchResultsSequenceNumber);
        
        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();
        originEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
        originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
    }
    
    private List<OriginEntry> retrieveGroup(Integer groupId) {
        // Get the entries from the group
        Map searchMap = new HashMap();
        searchMap.put("entryGroupId", groupId);
        Collection<OriginEntry> searchResultAsCollection = originEntryService.getMatchingEntriesByCollection(searchMap);

        List<OriginEntry> searchResults;
        if (searchResultAsCollection instanceof List) {
            searchResults = (List<OriginEntry>) searchResultAsCollection;
        }
        else {
            searchResults = new ArrayList<OriginEntry>(searchResultAsCollection);
        }
        return searchResults;
    }
    
    private OriginEntryStatistics getStatistics(Collection<OriginEntry> entries) {
        OriginEntryStatistics oes = new OriginEntryStatistics();

        for (OriginEntry oe : entries) {
            oes.incrementCount();
            if (isDebitBudget(oe)) {
                oes.addDebit(oe.getTransactionLedgerEntryAmount());
            }
            else {
                oes.addCredit(oe.getTransactionLedgerEntryAmount());
            }
        }
        return oes;
    }

    private boolean isDebitBudget(OriginEntry oe) {
        return (oe.getTransactionDebitCreditCode() == null || KFSConstants.GL_BUDGET_CODE.equals(oe.getTransactionDebitCreditCode()) || KFSConstants.GL_DEBIT_CODE.equals(oe.getTransactionDebitCreditCode()));
    }

    /**
     * Validate that choose system and edit method are selected
     */
    private boolean checkMainDropdown(CorrectionForm errorCorrectionForm) {
        LOG.debug("checkMainDropdown() started");

        boolean ret = true;
        if (StringUtils.isEmpty(errorCorrectionForm.getChooseSystem())) {
            GlobalVariables.getErrorMap().putError("systemAndEditMethod", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_SYSTEMFIELD_REQUIRED);
            ret = false;
        }
        if (StringUtils.isEmpty(errorCorrectionForm.getEditMethod())) {
            GlobalVariables.getErrorMap().putError("systemAndEditMethod", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_EDITMETHODFIELD_REQUIRED);
            ret = false;
        }
        return ret;
    }

    /**
     * This method checks that an origin entry group has been selected; and this method is intended to be used for selecting an origin entry group when
     * using the database method
     * 
     * If a group has not been loaded, then an error will be added to the screen
     * @param correctionForm
     * @return
     */
    private boolean checkOriginEntryGroupSelection(CorrectionForm correctionForm) {
        LOG.debug("checkOriginEntryGroupSelection() started");

        if (correctionForm.getInputGroupId() == null) {
            GlobalVariables.getErrorMap().putError("documentLoadError", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_ORIGINGROUP_REQUIRED);
            return false;
        }
        return true;
    }
    
    /**
     * This method checks that an origin entry group has been selected or uploaded, depending on the system of the document
     * If a group has not been loaded, then an error will be added to the screen
     * 
     * @param document
     * @return
     */
    private boolean checkOriginEntryGroupSelectionBeforeRouting(CorrectionDocument document) {
        if (document.getCorrectionInputGroupId() == null) {
            GlobalVariables.getErrorMap().putError("systemAndEditMethod", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_ORIGINGROUP_REQUIRED_FOR_ROUTING);
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
    private boolean validChangeGroups(CorrectionForm form) {
        LOG.debug("validChangeGroups() started");

        CorrectionDocument doc = form.getCorrectionDocument();
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
                    GlobalVariables.getErrorMap().putError(tab, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { oeff.getFieldDisplayName(cc.getCorrectionFieldName()), cc.getCorrectionFieldValue() });
                    allValid = false;
                }
            }
            for (Iterator iterator = ccg.getCorrectionChange().iterator(); iterator.hasNext();) {
                CorrectionChange cc = (CorrectionChange) iterator.next();
                if (!oeff.isValidValue(cc.getCorrectionFieldName(), cc.getCorrectionFieldValue())) {
                    GlobalVariables.getErrorMap().putError(tab, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { oeff.getFieldDisplayName(cc.getCorrectionFieldName()), cc.getCorrectionFieldValue() });
                    allValid = false;
                }
            }
        }
        return allValid;
    }

    /**
     * Sort CorrectionGroups and their search criteria and replacement specifications so they display properly on the page.
     */
    private void sortForDisplay(List groups) {
        for (Iterator i = groups.iterator(); i.hasNext();) {
            CorrectionChangeGroup group = (CorrectionChangeGroup) i.next();
            Collections.sort(group.getCorrectionCriteria());
            Collections.sort(group.getCorrectionChange());
        }

        Collections.sort(groups);
    }

    private void printChangeGroups(CorrectionDocument doc) {
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

    private void updateEntriesFromCriteria(CorrectionForm correctionForm, boolean clearOutSummary) {
        LOG.debug("updateEntriesFromCriteria() started");

        CorrectionDocument document = correctionForm.getCorrectionDocument();
        List<CorrectionChangeGroup> changeCriteriaGroups = document.getCorrectionChangeGroup();

        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionForm.getEditMethod())) {
            applyCriteriaOnEntries(correctionForm.getDisplayEntries(), correctionForm.getMatchCriteriaOnly(), changeCriteriaGroups);
        }
        else if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
            applyCriteriaOnEntries(correctionForm.getDisplayEntries(), correctionForm.getShowOutputFlag(), changeCriteriaGroups);
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
     * @param entries a Collection of OriginEntry BOs, this collection and its elements may be directly modified as a result of this method
     * @param matchCriteriaOnly if true, only those entries that matched the criteria before changes were applied will remain in the collection
     * @param changeCriteriaGroups a list of criteria and change groups.
     */
    private void applyCriteriaOnEntries(Collection<OriginEntry> entries, boolean matchCriteriaOnly, List<CorrectionChangeGroup> changeCriteriaGroups) {
        // Now, if they only want matches in the output group, go through them again and delete items that don't match any of the
        // groups
        // This means that matches within a group are ANDed and each group is ORed
        if (matchCriteriaOnly) {
            removeNonMatchingEntries(entries, changeCriteriaGroups);
        }

        for (OriginEntry oe : entries) {
            for (CorrectionChangeGroup ccg : changeCriteriaGroups) {
                int matches = 0;
                for (CorrectionCriteria cc : ccg.getCorrectionCriteria()) {
                    if (entryMatchesCriteria(cc, oe)) {
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
    
    private void removeNonMatchingEntries(Collection<OriginEntry> entries, Collection<CorrectionChangeGroup> groups) {
        Iterator<OriginEntry> oei = entries.iterator();
        while (oei.hasNext()) {
            OriginEntry oe = oei.next();
            boolean anyGroupMatch = false;
            for (CorrectionChangeGroup ccg : groups) {
                int matches = 0;
                for (CorrectionCriteria cc : ccg.getCorrectionCriteria()) {
                    if (entryMatchesCriteria(cc, oe)) {
                        matches++;
                    }
                }

                // If they all match, change it
                if (matches == ccg.getCorrectionCriteria().size()) {
                    anyGroupMatch = true;
                }
            }

            // If none of the groups match, delete it
            if (!anyGroupMatch) {
                oei.remove();
            }
        }
    }

    private boolean entryMatchesCriteria(CorrectionCriteria cc, OriginEntry oe) {
        OriginEntryFieldFinder oeff = new OriginEntryFieldFinder();
        Object fieldActualValue = oe.getFieldValue(cc.getCorrectionFieldName());
        String fieldTestValue = cc.getCorrectionFieldValue() == null ? "" : cc.getCorrectionFieldValue();
        String fieldType = oeff.getFieldType(cc.getCorrectionFieldName());
        String fieldActualValueString = convertToString(fieldActualValue, fieldType);

        if ("eq".equals(cc.getCorrectionOperatorCode())) {
            return fieldActualValueString.equals(fieldTestValue);
        }
        else if ("ne".equals(cc.getCorrectionOperatorCode())) {
            return (!fieldActualValueString.equals(fieldTestValue));
        }
        else if ("sw".equals(cc.getCorrectionOperatorCode())) {
            return fieldActualValueString.startsWith(fieldTestValue);
        }
        else if ("ew".equals(cc.getCorrectionOperatorCode())) {
            return fieldActualValueString.endsWith(fieldTestValue);
        }
        else if ("ct".equals(cc.getCorrectionOperatorCode())) {
            return (fieldActualValueString.indexOf(fieldTestValue) > -1);
        }
        throw new IllegalArgumentException("Unknown operator: " + cc.getCorrectionOperatorCode());
    }

    private String convertToString(Object fieldActualValue, String fieldType) {
        if (fieldActualValue == null) {
            return "";
        }
        if ("String".equals(fieldType)) {
            return (String) fieldActualValue;
        }
        else if ("Integer".equals(fieldType)) {
            Integer i = (Integer) fieldActualValue;
            return i.toString();
        }
        else if ("KualiDecimal".equals(fieldType)) {
            KualiDecimal kd = (KualiDecimal) fieldActualValue;
            return kd.toString();
        }
        else if ("Date".equals(fieldType)) {
            Date d = (Date) fieldActualValue;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.format(d);
        }
        return "";
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiTableAction#switchToPage(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
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
     * @see org.kuali.core.web.struts.action.KualiTableAction#sort(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward sort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CorrectionForm correctionForm = (CorrectionForm) form;
        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();

        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();

        List<Column> columns = SpringServiceLocator.getCorrectionDocumentService().getTableRenderColumnMetadata(correctionForm.getDocument().getDocumentNumber());
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
    
    private void sortList(List<OriginEntry> list, String propertyToSortName, Comparator valueComparator, boolean sortDescending) {
        if (list != null) {
            if (valueComparator instanceof NumericValueComparator || valueComparator instanceof TemporalValueComparator) {
                // hack alert: NumericValueComparator can only compare strings, so we use the KualiDecimal and Date's built in mechanism compare values using
                // the comparable comparator
                valueComparator = new ComparableComparator();
            }
            if (sortDescending) {
                valueComparator = new ReverseComparator(valueComparator);
            }
            Collections.sort(list, new BeanComparator(propertyToSortName, valueComparator));
        }
    }
    
    private void applyPagingAndSortingFromPreviousPageView(CorrectionForm correctionForm) {
        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();
        if (originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() != -1) {
            List<Column> columns = SpringServiceLocator.getCorrectionDocumentService().getTableRenderColumnMetadata(correctionForm.getDocument().getDocumentNumber());
            String propertyToSortName = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getPropertyName();
            Comparator valueComparator = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getValueComparator();
            sortList(correctionForm.getDisplayEntries(), propertyToSortName, valueComparator, originEntrySearchResultTableMetadata.isSortDescending());
        }
        
        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
        originEntrySearchResultTableMetadata.jumpToPage(originEntrySearchResultTableMetadata.getViewedPageNumber(), correctionForm.getDisplayEntries().size(), maxRowsPerPage);
    }
    
    /**
     * This method restores the system and edit method to the selected values when the last call to the selectSystemAndMethod action was made
     * 
     * @param correctionForm
     * @return if the system and edit method were changed while not in read only mode and the selectSystemEditMethod method was not being called
     * if true, this is ususally not a good condition 
     */
    private boolean restoreSystemAndEditMethod(CorrectionForm correctionForm) {
        boolean readOnly = correctionForm.getEditingMode().get(AuthorizationConstants.TransactionalEditMode.FULL_ENTRY) != null;
        if (!"selectSystemEditMethod".equals(correctionForm.getMethodToCall()) && !readOnly) {
            if (!StringUtils.equals(correctionForm.getPreviousEditMethod(), correctionForm.getEditMethod()) || 
                    !StringUtils.equals(correctionForm.getPreviousChooseSystem(), correctionForm.getChooseSystem())) {
                correctionForm.setChooseSystem(correctionForm.getPreviousChooseSystem());
                correctionForm.setEditMethod(correctionForm.getPreviousEditMethod());
                GlobalVariables.getErrorMap().putError("systemAndEditMethod", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_SYSTEM_OR_EDIT_METHOD_CHANGE);
                return true;
            }
        }
        return false;
    }
    
    /**
     * For database-based edits, this method will determine if the input group has been changed since the last time
     * the page was rendered. 
     *    
     * @param correctionForm
     * @return false if the input group was inappropriately changed without using the selectSystemEditMethod method
     */
    private boolean restoreInputGroupSelectionForDatabaseEdits(CorrectionForm correctionForm) {
        if (!CorrectionDocumentService.SYSTEM_DATABASE.equals(correctionForm.getChooseSystem())) {
            return true;
        }
        if ("loadGroup".equals(correctionForm.getMethodToCall())) {
            return true;
        }
        Integer currentInputGroupId = correctionForm.getInputGroupId();
        Integer previousInputGroupId = correctionForm.getPreviousInputGroupId();
        
        if (previousInputGroupId != null && (currentInputGroupId == null || previousInputGroupId.intValue() != currentInputGroupId.intValue())) {
            correctionForm.setInputGroupId(previousInputGroupId);
            GlobalVariables.getErrorMap().putError("documentsInSystem", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_INPUT_GROUP_CHANGE);
            return false;
        }
        return true;
    }
    
    private void persistOriginEntryGroupsForDocumentSave(CorrectionForm correctionForm) {
        CorrectionDocumentService correctionDocumentService = SpringServiceLocator.getCorrectionDocumentService();
        CorrectionDocument document = correctionForm.getCorrectionDocument();

        if (correctionForm.getAllEntries() == null && !correctionForm.isRestrictedFunctionalityMode()) {
            // if we don't have origin entries loaded and not in restricted functionality mode, then there's nothing worth persisting
            return;
        }
        
        if (!correctionForm.getDataLoadedFlag() && !correctionForm.isRestrictedFunctionalityMode()) {
            // data is not loaded (maybe user selected a new group)
            // clear out existing data
            correctionDocumentService.removePersistedInputOriginEntries(document);
            correctionDocumentService.removePersistedOutputOriginEntries(document);
            return;
            
        }
        // reload the group from the origin entry service
        List<OriginEntry> inputGroupEntries = retrieveGroup(correctionForm.getInputGroupId());
        correctionDocumentService.persistInputOriginEntriesForInitiatedOrSavedDocument(document, inputGroupEntries.iterator());
        
        if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
            // persist the allEntries element as the output group, since it has all of the modifications made by during the manual edits 
            correctionDocumentService.persistOutputOriginEntriesForInitiatedOrSavedDocument(document, correctionForm.getAllEntries().iterator());
        }
        else if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionForm.getEditMethod())) {
            // we want to persist the values of the output group.  We have the allEntries list in the form, and depending on whether the showOutputFlag is checked, this list
            // may represent the entries in the input or output group
            List<OriginEntry> outputGroupEntries;
            if (correctionForm.getShowOutputFlag()) {
                outputGroupEntries = correctionForm.getDisplayEntries();
            }
            else {
                // since this flag is false, it means that the all entries list represents the input group (see the execute method),
                // so we're going to call updateEntriesFromCriteria to apply criteria to the entries
                outputGroupEntries = new ArrayList<OriginEntry>(inputGroupEntries);
                applyCriteriaOnEntries(outputGroupEntries, correctionForm.getMatchCriteriaOnly(), document.getCorrectionChangeGroup());
            }
            correctionDocumentService.persistOutputOriginEntriesForInitiatedOrSavedDocument(document, outputGroupEntries.iterator());
        }
        else {
            throw new RuntimeException("Unrecognized edit method: " + correctionForm.getEditMethod());
        }
    }
    
    /**
     * This method updates the summary information contained within each document depending on the
     * document status, editing method, whether only the rows matching criteria are shown, and whether the output is being shown
     * 
     * If the form is in restricted functionality mode (and the override param is not set to true), then the summaries will be cleared out
     * 
     * @param document the document
     * @param entries the entries to summarize 
     * @param clearOutSummary whether to set the doc summary to 0s
     */
    private void updateDocumentSummary(CorrectionDocument document, List<OriginEntry> entries,
            boolean clearOutSummary) {
        if (clearOutSummary) {
            document.setCorrectionCreditTotalAmount(null);
            document.setCorrectionDebitTotalAmount(null);
            document.setCorrectionRowCount(null);
        }
        else {
            OriginEntryStatistics oes = getStatistics(entries);
            document.setCorrectionCreditTotalAmount(oes.getCreditTotalAmount());
            document.setCorrectionDebitTotalAmount(oes.getDebitTotalAmount());
            document.setCorrectionRowCount(oes.getRowCount());
        }
    }
    
    /**
     * This method puts an error in the error map indicating that manual edit-ing GLCP docs may not be in restrictedFunctionalityMode, if appropriate
     * (Since we can't view results in this mode, we can't manually edit them)
     * 
     * @param correctionForm
     * @return true if we are not in restricted functionality mode or we are not manual editing
     */
    private boolean checkRestrictedFunctionalityModeForManualEdit(CorrectionForm correctionForm) {
        if (correctionForm.isRestrictedFunctionalityMode() && CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionForm.getEditMethod())) {
            int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
            
            if (recordCountFunctionalityLimit == CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE) {
                GlobalVariables.getErrorMap().putError("systemAndEditMethod",
                        KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_ANY_GROUP);
            }
            else {
                GlobalVariables.getErrorMap().putError("systemAndEditMethod",
                        KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_LARGE_GROUP, String.valueOf(recordCountFunctionalityLimit));
            }
            return false;
        }
        return true;
    }
    
    private boolean validGroupsItemsForDocumentSave(CorrectionForm correctionForm) {
        // validate the criteria in the "add" groups to ensure that the field name and value are blank
        for (int i = 0; i < correctionForm.getGroupsSize(); i++) {
            GroupHolder groupHolder = correctionForm.getGroupsItem(i);
            if (!CorrectionDocumentUtils.validCorrectionChangeForSaving(groupHolder.getCorrectionChange()) ||
                    !CorrectionDocumentUtils.validCorrectionCriteriaForSaving(groupHolder.getCorrectionCriteria())) {
                if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionForm.getEditMethod())) {
                    GlobalVariables.getErrorMap().putError("editCriteria", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_CRITERIA_TO_ADD_MUST_BE_BLANK_FOR_SAVE);
                }
                else {
                    GlobalVariables.getErrorMap().putError("manualEditCriteria", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_CRITERIA_TO_ADD_MUST_BE_BLANK_FOR_SAVE);
                }
                return false;
            }
        }
        return true;
    }
}
