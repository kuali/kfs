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
package org.kuali.kfs.module.ld.document.web.struts;

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
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.kuali.kfs.gl.document.CorrectionDocumentUtils;
import org.kuali.kfs.gl.document.authorization.CorrectionDocumentAuthorizer;
import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.kfs.gl.document.web.struts.CorrectionAction;
import org.kuali.kfs.gl.document.web.struts.CorrectionForm;
import org.kuali.kfs.gl.service.GlCorrectionProcessOriginEntryService;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.options.CorrectionLaborGroupEntriesFinder;
import org.kuali.kfs.module.ld.businessobject.options.LaborOriginEntryFieldFinder;
import org.kuali.kfs.module.ld.document.LaborCorrectionDocument;
import org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryGroupService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
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
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Struts Action Class for the Labor Ledger Correction Process.
 */
public class LaborCorrectionAction extends CorrectionAction {

    LaborOriginEntryService laborOriginEntryService = SpringContext.getBean(LaborOriginEntryService.class);

    /**
     * This needs to be done just in case they decide to execute.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#excute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     *
     * KRAD Conversion: Lookupable performs customized sort on search results.
     * 
     * Uses data dictionary to get the metadata to render the columns.
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("execute() started");

        CorrectionForm correctionForm = (CorrectionForm) form;

        // Init our services once
        if (originEntryGroupService == null) {
            CorrectionAction.originEntryGroupService = (OriginEntryGroupService)SpringContext.getBean(LaborOriginEntryGroupService.class);;
            CorrectionAction.originEntryService = SpringContext.getBean(OriginEntryService.class);
            CorrectionAction.dateTimeService = SpringContext.getBean(DateTimeService.class);
            CorrectionAction.kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        }

        LaborCorrectionForm rForm = (LaborCorrectionForm) form;
        if (LOG.isDebugEnabled()) {
            LOG.debug("execute() methodToCall: " + rForm.getMethodToCall());    
        }
        Collection<OriginEntryFull> persistedOriginEntries = null;

        // If we are called from the docHandler or reload, ignore the persisted origin entries because we are either creating a new
        // document
        // or loading an old one
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
                        LaborCorrectionDocument document = rForm.getLaborCorrectionDocument();
                        List<CorrectionChangeGroup> groups = document.getCorrectionChangeGroup();
                        //TODO:- need to change for LLCP                      
                        updateEntriesFromCriteria(rForm, rForm.isRestrictedFunctionalityMode());
                    }

                    if (!KFSConstants.TableRenderConstants.SORT_METHOD.equals(rForm.getMethodToCall())) {
                        // if sorting, we'll let the action take care of the sorting
                        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = rForm.getOriginEntrySearchResultTableMetadata();
                        if (originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() != -1) {
                            List<Column> columns = SpringContext.getBean(LaborCorrectionDocumentService.class).getTableRenderColumnMetadata(rForm.getDocument().getDocumentNumber());

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

        ActionForward af = super.superExecute(mapping, form, request, response);
        return af;
    }

    /**
     * This needs to be done just in case they decide to save.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("save() started");
        
        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) form;
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();

        // Did they pick the edit method and system?
        if (!checkMainDropdown(laborCorrectionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!checkRestrictedFunctionalityModeForManualEdit(laborCorrectionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!validGroupsItemsForDocumentSave(laborCorrectionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!validChangeGroups(laborCorrectionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (!checkInputGroupPersistedForDocumentSave(laborCorrectionForm)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // Populate document
        document.setCorrectionTypeCode(laborCorrectionForm.getEditMethod());
        document.setCorrectionSelection(laborCorrectionForm.getMatchCriteriaOnly());
        document.setCorrectionFileDelete(!laborCorrectionForm.getProcessInBatch());
        document.setCorrectionInputFileName(laborCorrectionForm.getInputGroupId());
        document.setCorrectionOutputFileName(null); 
        if (laborCorrectionForm.getDataLoadedFlag() || laborCorrectionForm.isRestrictedFunctionalityMode()) {
            document.setCorrectionInputFileName(laborCorrectionForm.getInputGroupId());
        }
        else {
            document.setCorrectionInputFileName(null);
        }
        document.setCorrectionOutputFileName(null);

        SpringContext.getBean(LaborCorrectionDocumentService.class).persistOriginEntryGroupsForDocumentSave(document, laborCorrectionForm);
        if (LOG.isDebugEnabled()) {
            LOG.debug("save() doc type name: " + laborCorrectionForm.getDocTypeName());
        }
        ActionForward af = super.superSave(mapping, form, request, response);
        return af;
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

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) form;
        String command = laborCorrectionForm.getCommand();

        if (KewApiConstants.INITIATE_COMMAND.equals(command)) {
            laborCorrectionForm.clearForm();
            createDocument(laborCorrectionForm);
        }
        else {
            loadDocument(laborCorrectionForm);

            LaborCorrectionDocument laborDocument = laborCorrectionForm.getLaborCorrectionDocument();
            laborCorrectionForm.setInputGroupIdFromLastDocumentLoad(laborDocument.getCorrectionInputFileName());
            populateAuthorizationFields(laborCorrectionForm);
            Map<String, String> documentActions = laborCorrectionForm.getDocumentActions();
            if (documentActions.containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT)) {
                // They have saved the document and they are retreiving it to be completed
                laborCorrectionForm.setProcessInBatch(!laborDocument.getCorrectionFileDelete());
                laborCorrectionForm.setMatchCriteriaOnly(laborDocument.getCorrectionSelection());
                laborCorrectionForm.setEditMethod(laborDocument.getCorrectionTypeCode());
                
                if (laborDocument.getCorrectionInputFileName() != null) {
                    if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(laborDocument.getCorrectionTypeCode())) {
                        loadPersistedInputGroup(laborCorrectionForm);
                        laborCorrectionForm.setDeleteFileFlag(false);
                    }
                    else if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(laborDocument.getCorrectionTypeCode())) {
                        // for the "true" param below, when the origin entries are persisted in the CorrectionDocumentService, they
                        // // are likely
                        // // not to have origin entry IDs assigned to them. So, we create pseudo entry IDs that are
                        // // unique within the allEntries list, but not necessarily within the DB. The persistence layer
                        // // is responsible for auto-incrementing entry IDs in the DB.
                        loadPersistedOutputGroup(laborCorrectionForm, true);

                        laborCorrectionForm.setManualEditFlag(true);
                        laborCorrectionForm.setEditableFlag(false);
                        laborCorrectionForm.setDeleteFileFlag(false);
                    }
                    else if (CorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(laborDocument.getCorrectionTypeCode())) {
                        loadPersistedInputGroup(laborCorrectionForm);
                        laborCorrectionForm.setDeleteFileFlag(true);
                    }
                    else {
                        throw new RuntimeException("Unknown edit method " + laborDocument.getCorrectionTypeCode());
                    }
                    laborCorrectionForm.setDataLoadedFlag(true);
                }
                else {
                    laborCorrectionForm.setDataLoadedFlag(false);
                }
                laborCorrectionForm.setShowOutputFlag(documentActions.containsKey(KRADConstants.KUALI_ACTION_CAN_APPROVE));
                if (laborCorrectionForm.getShowOutputFlag() && !laborCorrectionForm.isRestrictedFunctionalityMode()) {
                    updateEntriesFromCriteria(laborCorrectionForm, false);
                }
                laborCorrectionForm.setInputFileName(laborDocument.getCorrectionInputFileName());
                if (laborDocument.getCorrectionInputFileName() != null) {
                    laborCorrectionForm.setChooseSystem(CorrectionDocumentService.SYSTEM_UPLOAD);
                }
                else {
                    laborCorrectionForm.setChooseSystem(CorrectionDocumentService.SYSTEM_DATABASE);
                }

                laborCorrectionForm.setPreviousChooseSystem(laborCorrectionForm.getChooseSystem());
                laborCorrectionForm.setPreviousEditMethod(laborCorrectionForm.getEditMethod());
                laborCorrectionForm.setPreviousInputGroupId(laborCorrectionForm.getInputGroupId());
            }
            else {
                // They are calling this from their action list to look at it or approve it
                laborCorrectionForm.setProcessInBatch(!laborDocument.getCorrectionFileDelete());
                laborCorrectionForm.setMatchCriteriaOnly(laborDocument.getCorrectionSelection());

                // we don't care about setting entry IDs for the records below, so the param is false below
                loadPersistedOutputGroup(laborCorrectionForm, false);
                laborCorrectionForm.setShowOutputFlag(true);
            }
            laborCorrectionForm.setInputGroupIdFromLastDocumentLoadIsMissing(!originEntryGroupService.getGroupExists(laborDocument.getCorrectionInputFileName()));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }



    /**
     * This handles the action for uploading a file
     * 
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#uploadFile(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward uploadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException, Exception {
        LOG.debug("uploadFile() started");

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) form;
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();

        Date now = CorrectionAction.dateTimeService.getCurrentDate();
        //creat file after all enries loaded well
        //OriginEntryGroup newOriginEntryGroup = CorrectionAction.originEntryGroupService.createGroup(today, OriginEntrySource.LABOR_CORRECTION_PROCESS_EDOC, false, false, false);

        FormFile sourceFile = laborCorrectionForm.getSourceFile();
        String llcpDirectory = SpringContext.getBean(LaborCorrectionDocumentService.class).getLlcpDirectoryName();
        String fullFileName = llcpDirectory + File.separator + sourceFile.getFileName() + "-" + CorrectionAction.dateTimeService.toDateTimeStringForFilename(now);
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
            laborCorrectionForm.setRestrictedFunctionalityMode(true);
            laborCorrectionForm.setDataLoadedFlag(false);
            document.setCorrectionInputFileName(fullFileName);
            laborCorrectionForm.setInputFileName(fullFileName);

            if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(laborCorrectionForm.getEditMethod())) {
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
            laborCorrectionForm.setRestrictedFunctionalityMode(false);
            if (loadedCount > 0) {
                //now we can load all data from file
                List<LaborOriginEntry> originEntryList = new ArrayList();
                Map loadMessageMap = laborOriginEntryService.getEntriesByGroupIdWithPath(uploadedFile.getAbsolutePath(), originEntryList);
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
                laborCorrectionForm.setDataLoadedFlag(true);
                laborCorrectionForm.setInputFileName(fullFileName);
                document.setCorrectionInputFileName(fullFileName);
                List<OriginEntryFull> originEntryFullList = new ArrayList();
                originEntryFullList.addAll(originEntryList);
                loadAllEntries(originEntryFullList, laborCorrectionForm);
                
                if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(laborCorrectionForm.getEditMethod())) {
                    laborCorrectionForm.setEditableFlag(false);
                    laborCorrectionForm.setManualEditFlag(true);
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
     * Show all entries for Manual edit with groupId and persist these entries to the DB The restricted functionality mode flag MUST
     * BE SET PRIOR TO CALLING this method.
     * 
     * @param groupId group ID
     * @param correctionForm correction form
     * @throws Exception
     */
//    protected void loadAllEntries(Integer groupId, CorrectionForm correctionForm) throws Exception {
//        LOG.debug("loadAllEntries() started");
//
//        if (!correctionForm.isRestrictedFunctionalityMode()) {
//            GeneralLedgerCorrectionProcessDocument document = correctionForm.getCorrectionDocument();
//            List<LaborOriginEntry> laborSearchResults = laborOriginEntryService.getEntriesByGroupId(groupId);
//            List<OriginEntryFull> searchResults = new ArrayList();
//            searchResults.addAll(laborSearchResults);
//
//            correctionForm.setAllEntries(searchResults);
//            correctionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(searchResults));
//
//            updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());
//
//            // if not in restricted functionality mode, then we can store these results temporarily in the GLCP origin entry service
//            SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
//            String glcpSearchResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE));
//
//            SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(glcpSearchResultsSequenceNumber, searchResults);
//            correctionForm.setGlcpSearchResultsSequenceNumber(glcpSearchResultsSequenceNumber);
//
//            int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
//            KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();
//            originEntrySearchResultTableMetadata.jumpToFirstPage(correctionForm.getDisplayEntries().size(), maxRowsPerPage);
//            originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
//        }
//    }

    protected void loadAllEntries(String fileNameWithPath, LaborCorrectionForm laborCorrectionForm) {
        LOG.debug("loadAllEntries() started");
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();
        
        if (!laborCorrectionForm.isRestrictedFunctionalityMode()) {
            List<LaborOriginEntry> laborSearchResults = new ArrayList();
            Map loadMessageMap = laborOriginEntryService.getEntriesByGroupIdWithPath(fileNameWithPath, laborSearchResults);
            List<OriginEntryFull> searchResults = new ArrayList();
            searchResults.addAll(laborSearchResults);
            
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
                    loadAllEntries(searchResults, laborCorrectionForm);
                    
                } catch (Exception e){
                    throw new RuntimeException(e);
                } 
            }
        }
    }
    
    
    
    /**
     * Save a changed row in the group
     *
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#saveManualEntry(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward saveManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("saveManualEdit() started");

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) form;
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();

        if (validLaborOriginEntry(laborCorrectionForm)) {
            int entryId = laborCorrectionForm.getLaborEntryForManualEdit().getEntryId();

            // Find it and replace it with the one from the edit spot
            for (Iterator<OriginEntryFull> iter = laborCorrectionForm.getAllEntries().iterator(); iter.hasNext();) {
                OriginEntryFull element = iter.next();
                if (element.getEntryId() == entryId) {
                    iter.remove();
                }
            }

            laborCorrectionForm.updateLaborEntryForManualEdit();
            laborCorrectionForm.getAllEntries().add(laborCorrectionForm.getLaborEntryForManualEdit());

            // we've modified the list of all entries, so repersist it
            SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(laborCorrectionForm.getGlcpSearchResultsSequenceNumber(), laborCorrectionForm.getAllEntries());
            // laborCorrectionForm.setDisplayEntries(null);
            laborCorrectionForm.setDisplayEntries(laborCorrectionForm.getAllEntries());

            if (laborCorrectionForm.getShowOutputFlag()) {
                removeNonMatchingEntries(laborCorrectionForm.getDisplayEntries(), document.getCorrectionChangeGroup());
            }

            // Clear out the additional row
            laborCorrectionForm.clearLaborEntryForManualEdit();
        }

        // Calculate the debit/credit/row count
        updateDocumentSummary(document, laborCorrectionForm.getAllEntries(), laborCorrectionForm.isRestrictedFunctionalityMode());

        // list has changed, we'll need to repage and resort
        applyPagingAndSortingFromPreviousPageView(laborCorrectionForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Add a new row to the group
     *
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#addManualEntry(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward addManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addManualEdit() started");

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) form;
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();

        if (validLaborOriginEntry(laborCorrectionForm)) {
            laborCorrectionForm.updateLaborEntryForManualEdit();

            // new entryId is always 0, so give it a unique Id, SequenceAccessorService is used.
            //Long newEntryId = SpringContext.getBean(SequenceAccessorService.class).getNextAvailableSequenceNumber("GL_ORIGIN_ENTRY_T_SEQ");
            int newEntryId = getMaxEntryId(laborCorrectionForm.getAllEntries()) + 1;
            laborCorrectionForm.getEntryForManualEdit().setEntryId(new Integer(newEntryId));
            
            laborCorrectionForm.getAllEntries().add(laborCorrectionForm.getLaborEntryForManualEdit());

            // Clear out the additional row
            laborCorrectionForm.clearLaborEntryForManualEdit();
        }


        // Calculate the debit/credit/row count
        updateDocumentSummary(document, laborCorrectionForm.getAllEntries(), laborCorrectionForm.isRestrictedFunctionalityMode());

        laborCorrectionForm.setShowSummaryOutputFlag(true);

        // we've modified the list of all entries, so repersist it
        SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(laborCorrectionForm.getGlcpSearchResultsSequenceNumber(), laborCorrectionForm.getAllEntries());
        laborCorrectionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(laborCorrectionForm.getAllEntries()));
        if (laborCorrectionForm.getShowOutputFlag()) {
            removeNonMatchingEntries(laborCorrectionForm.getDisplayEntries(), document.getCorrectionChangeGroup());
        }

        // list has changed, we'll need to repage and resort
        applyPagingAndSortingFromPreviousPageView(laborCorrectionForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Handles manual edit of labor correction form
     * 
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#manualEdit(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward manualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) form;
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();
        laborCorrectionForm.clearLaborEntryForManualEdit();
        
        laborCorrectionForm.clearEntryForManualEdit();
        laborCorrectionForm.setEditableFlag(true);
        laborCorrectionForm.setManualEditFlag(false);

        if ( document.getCorrectionChangeGroup().isEmpty() ) {
            document.addCorrectionChangeGroup(new CorrectionChangeGroup());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Handles edit of manual entry
     * 
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#editManualEntry(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward editManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("editManualEdit() started");

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) form;
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();

        int entryId = Integer.parseInt(getImageContext(request, "entryId"));

        // Find it and put it in the editing spot
        for (Iterator iter = laborCorrectionForm.getAllEntries().iterator(); iter.hasNext();) {
            LaborOriginEntry element = (LaborOriginEntry) iter.next();
            if (element.getEntryId() == entryId) {
                laborCorrectionForm.setLaborEntryForManualEdit(element);
                laborCorrectionForm.setLaborEntryFinancialDocumentReversalDate(CorrectionDocumentUtils.convertToString(element.getFinancialDocumentReversalDate(), "Date"));
                laborCorrectionForm.setLaborEntryTransactionDate(CorrectionDocumentUtils.convertToString(element.getTransactionDate(), "Date"));
                laborCorrectionForm.setLaborEntryTransactionLedgerEntryAmount(CorrectionDocumentUtils.convertToString(element.getTransactionLedgerEntryAmount(), "KualiDecimal"));
                laborCorrectionForm.setLaborEntryTransactionLedgerEntrySequenceNumber(CorrectionDocumentUtils.convertToString(element.getTransactionLedgerEntrySequenceNumber(), "Integer"));
                laborCorrectionForm.setLaborEntryUniversityFiscalYear(CorrectionDocumentUtils.convertToString(element.getUniversityFiscalYear(), "Integer"));
                
                laborCorrectionForm.setLaborEntryTransactionPostingDate(CorrectionDocumentUtils.convertToString(element.getTransactionPostingDate(), "Date"));
                laborCorrectionForm.setLaborEntryPayPeriodEndDate(CorrectionDocumentUtils.convertToString(element.getPayPeriodEndDate(), "Date"));
                laborCorrectionForm.setLaborEntryTransactionTotalHours(CorrectionDocumentUtils.convertToString(element.getTransactionTotalHours(), "BigDecimal"));
                laborCorrectionForm.setLaborEntryPayrollEndDateFiscalYear(CorrectionDocumentUtils.convertToString(element.getPayrollEndDateFiscalYear(), "Integer"));
                laborCorrectionForm.setLaborEntryEmployeeRecord(CorrectionDocumentUtils.convertToString(element.getEmployeeRecord(), "Integer"));
                
                break;
            }
        }

        laborCorrectionForm.setShowSummaryOutputFlag(true);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is for validation of Labor Origin Entry
     * 
     * @param laborCorrectionForm
     * @return boolean
     */
    protected boolean validLaborOriginEntry(LaborCorrectionForm laborCorrectionForm) {
        LOG.debug("validOriginEntry() started");

        LaborOriginEntry oe = laborCorrectionForm.getLaborEntryForManualEdit();

        boolean valid = true;
        LaborOriginEntryFieldFinder loeff = new LaborOriginEntryFieldFinder();
        List fields = loeff.getKeyValues();
        for (Iterator iter = fields.iterator(); iter.hasNext();) {
            KeyValue lkp = (KeyValue) iter.next();

            // Get field name, type, length & value on the form
            String fieldName = (String) lkp.getKey();
            String fieldDisplayName = lkp.getValue();
            String fieldType = loeff.getFieldType(fieldName);
            int fieldLength = loeff.getFieldLength(fieldName);
            String fieldValue = null;
            if ("String".equals(fieldType)) {
                fieldValue = (String) oe.getFieldValue(fieldName);
            }
            else if (KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE.equals(fieldName)) {
                fieldValue = laborCorrectionForm.getLaborEntryFinancialDocumentReversalDate();
            }
            else if (KFSPropertyConstants.TRANSACTION_DATE.equals(fieldName)) {
                fieldValue = laborCorrectionForm.getLaborEntryTransactionDate();
            }
            else if (KFSPropertyConstants.TRN_ENTRY_LEDGER_SEQUENCE_NUMBER.equals(fieldName)) {
                fieldValue = laborCorrectionForm.getLaborEntryTransactionLedgerEntrySequenceNumber();
            }
            else if (KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT.equals(fieldName)) {
                fieldValue = laborCorrectionForm.getLaborEntryTransactionLedgerEntryAmount();
            }
            else if (KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR.equals(fieldName)) {
                fieldValue = laborCorrectionForm.getLaborEntryUniversityFiscalYear();
            }
            
            // for Labor Specified fields
            else if (KFSPropertyConstants.TRANSACTION_POSTING_DATE.equals(fieldName)) {
                fieldValue = laborCorrectionForm.getLaborEntryTransactionPostingDate();
            }
            else if (KFSPropertyConstants.PAY_PERIOD_END_DATE.equals(fieldName)) {
                fieldValue = laborCorrectionForm.getLaborEntryPayPeriodEndDate();
            }
            else if (KFSPropertyConstants.TRANSACTION_TOTAL_HOURS.equals(fieldName)) {
                fieldValue = laborCorrectionForm.getLaborEntryTransactionTotalHours();
            }
            else if (KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR.equals(fieldName)) {
                fieldValue = laborCorrectionForm.getLaborEntryPayrollEndDateFiscalYear();
            }
            
            else if (KFSPropertyConstants.EMPLOYEE_RECORD.equals(fieldName)) {
                fieldValue = laborCorrectionForm.getLaborEntryEmployeeRecord();
            }
            
            
            // Now check that the data is valid
            if (!StringUtils.isEmpty(fieldValue)) {
                if (!loeff.isValidValue(fieldName, fieldValue)) {
                    GlobalVariables.getMessageMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { fieldDisplayName, fieldValue });
                    valid = false;
                }
            }
            else if (!loeff.allowNull(fieldName)) {
                GlobalVariables.getMessageMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { fieldDisplayName, fieldValue });
                valid = false;
            }
        }

        return valid;
    }

    /**
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#removeNonMatchingEntries(java.util.Collection,
     *      java.util.Collection)
     */
    protected void removeNonMatchingEntries(Collection<OriginEntryFull> entries, Collection<CorrectionChangeGroup> groups) {
        Iterator<OriginEntryFull> loei = entries.iterator();
        while (loei.hasNext()) {
            OriginEntryFull oe = loei.next();
            if (!org.kuali.kfs.module.ld.util.CorrectionDocumentUtils.doesLaborEntryMatchAnyCriteriaGroups(oe, groups)) {
                loei.remove();
            }
        }
    }

    /**
     * Validate all the correction groups
     * 
     * @param doc
     * @return if valid, return true, false if not
     */
    @Override
    protected boolean validChangeGroups(CorrectionForm correctionForm) {
        LOG.debug("validChangeGroups() started");

        LaborCorrectionForm form = (LaborCorrectionForm) correctionForm;
        LaborCorrectionDocument doc = form.getLaborCorrectionDocument();
        String tab = "";
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(form.getEditMethod())) {
            tab = "editCriteria";
        }
        else {
            tab = "manualEditCriteria";
        }

        boolean allValid = true;

        LaborOriginEntryFieldFinder loeff = new LaborOriginEntryFieldFinder();
        List fields = loeff.getKeyValues();

        List l = doc.getCorrectionChangeGroup();
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            CorrectionChangeGroup ccg = (CorrectionChangeGroup) iter.next();
            for (Iterator iterator = ccg.getCorrectionCriteria().iterator(); iterator.hasNext();) {
                CorrectionCriteria cc = (CorrectionCriteria) iterator.next();
                if (!loeff.isValidValue(cc.getCorrectionFieldName(), cc.getCorrectionFieldValue())) {
                    GlobalVariables.getMessageMap().putError(tab, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { loeff.getFieldDisplayName(cc.getCorrectionFieldName()), cc.getCorrectionFieldValue() });
                    allValid = false;
                }
            }
            for (Iterator iterator = ccg.getCorrectionChange().iterator(); iterator.hasNext();) {
                CorrectionChange cc = (CorrectionChange) iterator.next();
                if (!loeff.isValidValue(cc.getCorrectionFieldName(), cc.getCorrectionFieldValue())) {
                    GlobalVariables.getMessageMap().putError(tab, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_INVALID_VALUE, new String[] { loeff.getFieldDisplayName(cc.getCorrectionFieldName()), cc.getCorrectionFieldValue() });
                    allValid = false;
                }
            }
        }
        return allValid;
    }

    /**
     * This method is for loading loadPersistedInputGroup
     * 
     * @param correctionForm
     * @throws Exception
     */
    protected void loadPersistedInputGroup(CorrectionForm correctionForm) throws Exception {

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) correctionForm;
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();

        int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
        LaborCorrectionDocumentService laborCorrectionDocumentService = SpringContext.getBean(LaborCorrectionDocumentService.class);

        if (!laborCorrectionDocumentService.areInputOriginEntriesPersisted(document)) {
            // the input origin entry group has been purged from the system
            laborCorrectionForm.setPersistedOriginEntriesMissing(true);
            laborCorrectionForm.setRestrictedFunctionalityMode(true);
            return;
        }

        laborCorrectionForm.setPersistedOriginEntriesMissing(false);
        List<LaborOriginEntry> laborSearchResults = laborCorrectionDocumentService.retrievePersistedInputOriginEntries(document, recordCountFunctionalityLimit);
        if (laborSearchResults == null) {
            // null when the origin entry list is too large (i.e. in restricted functionality mode)
            laborCorrectionForm.setRestrictedFunctionalityMode(true);
            updateDocumentSummary(document, null, true);
        }
        else {
            List<OriginEntryFull> searchResults = new ArrayList();
            searchResults.addAll(laborSearchResults);
            laborCorrectionForm.setAllEntries(searchResults);
            laborCorrectionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(searchResults));

            updateDocumentSummary(document, laborCorrectionForm.getAllEntries(), false);

            // if not in restricted functionality mode, then we can store these results temporarily in the GLCP origin entry service
            SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
            String glcpSearchResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE));

            SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(glcpSearchResultsSequenceNumber, searchResults);
            laborCorrectionForm.setGlcpSearchResultsSequenceNumber(glcpSearchResultsSequenceNumber);

            int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
            KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = laborCorrectionForm.getOriginEntrySearchResultTableMetadata();
            originEntrySearchResultTableMetadata.jumpToFirstPage(laborCorrectionForm.getDisplayEntries().size(), maxRowsPerPage);
            originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
        }
    }

    /**
     * Loads persisted output group 
     * 
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#loadPersistedOutputGroup(org.kuali.kfs.gl.document.web.struts.CorrectionForm,
     *      boolean)
     */
    protected void loadPersistedOutputGroup(CorrectionForm correctionForm, boolean setSequentialIds) throws Exception {
        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) correctionForm;
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();

        LaborCorrectionDocumentService laborCorrectionDocumentService = SpringContext.getBean(LaborCorrectionDocumentService.class);
        if (!laborCorrectionDocumentService.areOutputOriginEntriesPersisted(document)) {
            // the input origin entry group has been purged from the system
            laborCorrectionForm.setPersistedOriginEntriesMissing(true);
            laborCorrectionForm.setRestrictedFunctionalityMode(true);
            return;
        }

        laborCorrectionForm.setPersistedOriginEntriesMissing(false);

        int recordCountFunctionalityLimit;
        if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(laborCorrectionForm.getEditMethod())) {
            // with manual edits, rows may have been added so that the list goes would go into restricted func mode
            // so for manual edits, we ignore this limit
            recordCountFunctionalityLimit = CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_UNLIMITED;
        }
        else {
            recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
        }

        List<LaborOriginEntry> laborSearchResults = laborCorrectionDocumentService.retrievePersistedOutputOriginEntries(document, recordCountFunctionalityLimit);

        if (laborSearchResults == null) {
            // null when the origin entry list is too large (i.e. in restricted functionality mode)
            laborCorrectionForm.setRestrictedFunctionalityMode(true);

            WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

            CorrectionDocumentAuthorizer cda = new CorrectionDocumentAuthorizer();
            // TODO fix for KIM
//            Map editingMode = cda.getEditMode(document, GlobalVariables.getUserSession().getPerson());
//            if (editingMode.containsKey(KfsAuthorizationConstants.TransactionalEditMode.FULL_ENTRY) || workflowDocument.isCanceled()) {
//                // doc in read/write mode or is cancelled, so the doc summary fields of the doc are unreliable, so clear them out
//                updateDocumentSummary(document, null, true);
//            }
            // else we defer to the values already in the doc, and just don't touch the values
        }
        else {
            List<OriginEntryFull> searchResults = new ArrayList();
            searchResults.addAll(laborSearchResults);
            laborCorrectionForm.setAllEntries(searchResults);
            laborCorrectionForm.setDisplayEntries(new ArrayList<OriginEntryFull>(searchResults));

            if (setSequentialIds) {
                CorrectionDocumentUtils.setSequentialEntryIds(laborCorrectionForm.getAllEntries());
            }

            // if we can display the entries (i.e. not restricted functionality mode), then recompute the summary
            updateDocumentSummary(document, laborCorrectionForm.getAllEntries(), false);

            // if not in restricted functionality mode, then we can store these results temporarily in the GLCP origin entry service
            SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
            String glcpSearchResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE));

            SpringContext.getBean(GlCorrectionProcessOriginEntryService.class).persistAllEntries(glcpSearchResultsSequenceNumber, searchResults);
            laborCorrectionForm.setGlcpSearchResultsSequenceNumber(glcpSearchResultsSequenceNumber);

            int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
            KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = laborCorrectionForm.getOriginEntrySearchResultTableMetadata();
            originEntrySearchResultTableMetadata.jumpToFirstPage(laborCorrectionForm.getDisplayEntries().size(), maxRowsPerPage);
            originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);
        }
    }

    /**
     * Prepare labor correction document for routing
     * 
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#prepareForRoute(org.kuali.kfs.gl.document.web.struts.CorrectionForm)
     */
    protected boolean prepareForRoute(CorrectionForm correctionForm) throws Exception {

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) correctionForm;
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();

        // Is there a description?
        if (StringUtils.isEmpty(document.getDocumentHeader().getDocumentDescription())) {
            GlobalVariables.getMessageMap().putError("document.documentHeader.documentDescription", KFSKeyConstants.ERROR_DOCUMENT_NO_DESCRIPTION);
            return false;
        }

        if (laborCorrectionForm.isPersistedOriginEntriesMissing()) {
            GlobalVariables.getMessageMap().putError("searchResults", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING);
            return false;
        }

        // Did they pick the edit method and system?
        if (!checkMainDropdown(laborCorrectionForm)) {
            return false;
        }

        if (laborCorrectionForm.getDataLoadedFlag() || laborCorrectionForm.isRestrictedFunctionalityMode()) {
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

        if (!validGroupsItemsForDocumentSave(laborCorrectionForm)) {
            return false;
        }

        // If it is criteria, are all the criteria valid?
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(laborCorrectionForm.getEditMethod())) {
            if (!validChangeGroups(laborCorrectionForm)) {
                return false;
            }
        }

        if (!checkRestrictedFunctionalityModeForManualEdit(laborCorrectionForm)) {
            return false;
        }

        if (!checkInputGroupPersistedForDocumentSave(laborCorrectionForm)) {
            return false;
        }
        // Get the output group if necessary
        if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(laborCorrectionForm.getEditMethod())) {
            if (!laborCorrectionForm.isRestrictedFunctionalityMode() && laborCorrectionForm.getDataLoadedFlag() && !laborCorrectionForm.getShowOutputFlag()) {
                // we're going to force the user to view the output group upon routing, so apply the criteria
                // if the user wasn't in show output mode.
                updateEntriesFromCriteria(laborCorrectionForm, false);
            }
            laborCorrectionForm.setShowOutputFlag(true);
        }
        else {
            // If it is manual edit, we don't need to save any correction groups
            document.getCorrectionChangeGroup().clear();
        }

        // Populate document
        document.setCorrectionTypeCode(laborCorrectionForm.getEditMethod());
        document.setCorrectionSelection(laborCorrectionForm.getMatchCriteriaOnly());
        document.setCorrectionFileDelete(!laborCorrectionForm.getProcessInBatch());
        document.setCorrectionInputFileName(laborCorrectionForm.getInputGroupId());
        document.setCorrectionOutputFileName(null); // this field is never used

        // we'll populate the output group id when the doc has a route level change
        document.setCorrectionOutputFileName(null);

        SpringContext.getBean(LaborCorrectionDocumentService.class).persistOriginEntryGroupsForDocumentSave(document, laborCorrectionForm);

        return true;
    }

    /**
     * Save labor correction form as a text document (.txt)
     * 
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#saveToDesktop(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward saveToDesktop(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.debug("saveToDesktop() started");

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) form;

        if (checkOriginEntryGroupSelection(laborCorrectionForm)) {
            if (laborCorrectionForm.isInputGroupIdFromLastDocumentLoadIsMissing() && laborCorrectionForm.getInputGroupIdFromLastDocumentLoad() != null && laborCorrectionForm.getInputGroupIdFromLastDocumentLoad().equals(laborCorrectionForm.getInputGroupId())) {
                if (laborCorrectionForm.isPersistedOriginEntriesMissing()) {
                    GlobalVariables.getMessageMap().putError("documentsInSystem", LaborKeyConstants.ERROR_LABOR_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING);
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
                else {
                    String fileName = "llcp_archived_group_" + laborCorrectionForm.getInputGroupIdFromLastDocumentLoad().toString() + ".txt";
                    // set response
                    response.setContentType("application/txt");
                    response.setHeader("Content-disposition", "attachment; filename=" + fileName);
                    response.setHeader("Expires", "0");
                    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                    response.setHeader("Pragma", "public");

                    BufferedOutputStream bw = new BufferedOutputStream(response.getOutputStream());

                    SpringContext.getBean(CorrectionDocumentService.class).writePersistedInputOriginEntriesToStream((LaborCorrectionDocument) laborCorrectionForm.getDocument(), bw);

                    bw.flush();
                    bw.close();

                    return null;
                }
            }
            else {
                String batchDirectory = SpringContext.getBean(LaborCorrectionDocumentService.class).getBatchFileDirectoryName();
                String fileNameWithPath; 
                if (!laborCorrectionForm.getInputGroupId().contains(batchDirectory)){
                    fileNameWithPath = batchDirectory + File.separator + laborCorrectionForm.getInputGroupId();
                } else {
                    fileNameWithPath = laborCorrectionForm.getInputGroupId(); 
                }
                
                FileReader fileReader = new FileReader(fileNameWithPath);
                BufferedReader br = new BufferedReader(fileReader);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // write to output
                buildTextOutputfile(baos, br);
                WebUtils.saveMimeOutputStreamAsFile(response, "application/txt", baos, laborCorrectionForm.getInputGroupId());
                
                br.close();
                
                return null;
            }
        }
        else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    /**
     * Sort labor correction document by selected column
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiTableRenderAction#sort(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     *      
     * KRAD Conversion: Performs sorting of the results based on column to sort.
     * 
     * Uses data dictionary for originEntrySearchResultTableMetadata
     */
    public ActionForward sort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LaborCorrectionForm correctionForm = (LaborCorrectionForm) form;
        
        // when we return from the lookup, our next request's method to call is going to be refresh
        correctionForm.registerEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);
        
        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();

        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = correctionForm.getOriginEntrySearchResultTableMetadata();

        List<Column> columns = SpringContext.getBean(LaborCorrectionDocumentService.class).getTableRenderColumnMetadata(correctionForm.getDocument().getDocumentNumber());

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

    /**
     * Apply paging and sorting from previous page view
     * 
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#applyPagingAndSortingFromPreviousPageView(org.kuali.kfs.gl.document.web.struts.CorrectionForm)
     *      
     * KRAD Conversion: Performs sorting of the results based on column to sort.
     * 
     * Uses data dictionary for originEntrySearchResultTableMetadata
     */
    protected void applyPagingAndSortingFromPreviousPageView(LaborCorrectionForm laborCorrectionForm) {
        KualiTableRenderFormMetadata originEntrySearchResultTableMetadata = laborCorrectionForm.getOriginEntrySearchResultTableMetadata();
        if (originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex() != -1) {

            List<Column> columns = SpringContext.getBean(LaborCorrectionDocumentService.class).getTableRenderColumnMetadata(laborCorrectionForm.getDocument().getDocumentNumber());

            String propertyToSortName = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getPropertyName();
            Comparator valueComparator = columns.get(originEntrySearchResultTableMetadata.getPreviouslySortedColumnIndex()).getValueComparator();
            sortList(laborCorrectionForm.getDisplayEntries(), propertyToSortName, valueComparator, originEntrySearchResultTableMetadata.isSortDescending());
        }

        int maxRowsPerPage = CorrectionDocumentUtils.getRecordsPerPage();
        originEntrySearchResultTableMetadata.jumpToPage(originEntrySearchResultTableMetadata.getViewedPageNumber(), laborCorrectionForm.getDisplayEntries().size(), maxRowsPerPage);
    }

    /**
     * Returns true if input group exists from labor correction document  
     * 
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#checkInputGroupPersistedForDocumentSave(org.kuali.kfs.gl.document.web.struts.CorrectionForm)
     */
    protected boolean checkInputGroupPersistedForDocumentSave(CorrectionForm correctionForm) {
        boolean present;
        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) correctionForm;
        WorkflowDocument workflowDocument = laborCorrectionForm.getDocument().getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || (workflowDocument.isSaved() && (laborCorrectionForm.getInputGroupIdFromLastDocumentLoad() == null || !laborCorrectionForm.getInputGroupIdFromLastDocumentLoad().equals(laborCorrectionForm.getInputGroupId())))) {
            present = originEntryGroupService.getGroupExists(((LaborCorrectionDocument) laborCorrectionForm.getDocument()).getCorrectionInputFileName());
        }
        else {
            present = SpringContext.getBean(LaborCorrectionDocumentService.class).areInputOriginEntriesPersisted((LaborCorrectionDocument) laborCorrectionForm.getDocument());
        }
        if (!present) {
            GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING);
        }
        return present;
    }

    /**
     * Called when selecting the system and method. If this button is pressed, the document should be reset as if it is the first
     * time it was pressed.
     *
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionAction#selectSystemEditMethod(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward selectSystemEditMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("selectSystemEditMethod() started");

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) form;
        LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();

        if (checkMainDropdown(laborCorrectionForm)) {
            // Clear out any entries that were already loaded
            document.setCorrectionInputFileName(null);
            document.setCorrectionOutputFileName(null);
            document.setCorrectionCreditTotalAmount(null);
            document.setCorrectionDebitTotalAmount(null);
            document.setCorrectionBudgetTotalAmount(null);
            document.setCorrectionRowCount(null);
            document.getCorrectionChangeGroup().clear();

            laborCorrectionForm.setDataLoadedFlag(false);
            laborCorrectionForm.setDeleteFileFlag(false);
            laborCorrectionForm.setEditableFlag(false);
            laborCorrectionForm.setManualEditFlag(false);
            laborCorrectionForm.setShowOutputFlag(false);
            laborCorrectionForm.setAllEntries(new ArrayList<OriginEntryFull>());
            laborCorrectionForm.setRestrictedFunctionalityMode(false);
            laborCorrectionForm.setProcessInBatch(true);

            if (CorrectionDocumentService.SYSTEM_DATABASE.equals(laborCorrectionForm.getChooseSystem())) {
                // if users choose database, then get the list of origin entry groups and set the default

                // I shouldn't have to do this query twice, but with the current architecture, I can't find anyway not to do it.
                CorrectionLaborGroupEntriesFinder f = new CorrectionLaborGroupEntriesFinder();
                List values = f.getKeyValues();
                if (values.size() > 0) {
                    //TODO:- need to change using file
                    //OriginEntryGroup g = CorrectionAction.originEntryGroupService.getNewestScrubberErrorGroup();
                    String newestScrubberErrorFileName = CorrectionAction.originEntryGroupService.getNewestScrubberErrorFileName();
                    //if (g != null) {
                    if (newestScrubberErrorFileName != null) {
                        document.setCorrectionInputFileName(newestScrubberErrorFileName);
                    }
                    else {
                        KeyValue klp = (KeyValue) values.get(0);
                        //document.setCorrectionInputGroupId(Integer.parseInt((String) klp.getKey()));
                        document.setCorrectionInputFileName((String) klp.getKey());
                    }
                }
                else {
                    GlobalVariables.getMessageMap().putError(SYSTEM_AND_EDIT_METHOD_ERROR_KEY, KFSKeyConstants.ERROR_NO_ORIGIN_ENTRY_GROUPS);
                    laborCorrectionForm.setChooseSystem("");
                }
            }
        }
        else {
            laborCorrectionForm.setEditMethod("");
            laborCorrectionForm.setChooseSystem("");
        }
        laborCorrectionForm.setPreviousChooseSystem(laborCorrectionForm.getChooseSystem());
        laborCorrectionForm.setPreviousEditMethod(laborCorrectionForm.getEditMethod());
        laborCorrectionForm.setPreviousInputGroupId(null);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Called when Load Group button is pressed
     */
    public ActionForward loadGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("loadGroup() started");

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) form;
        String batchDirectory = SpringContext.getBean(LaborCorrectionDocumentService.class).getBatchFileDirectoryName();

        if (checkOriginEntryGroupSelection(laborCorrectionForm)) {
            LaborCorrectionDocument doc = laborCorrectionForm.getLaborCorrectionDocument();
            doc.setCorrectionInputFileName(batchDirectory + File.separator + laborCorrectionForm.getInputGroupId());

            // TODO:- need to change using file - just size info will be enough
            // TODO:- int will be enough? should I change it long??
            int inputGroupSize = laborOriginEntryService.getGroupCount(laborCorrectionForm.getInputGroupId());
            int recordCountFunctionalityLimit = CorrectionDocumentUtils.getRecordCountFunctionalityLimit();
            laborCorrectionForm.setPersistedOriginEntriesMissing(false);

            if (CorrectionDocumentUtils.isRestrictedFunctionalityMode(inputGroupSize, recordCountFunctionalityLimit)) {
                laborCorrectionForm.setRestrictedFunctionalityMode(true);
                laborCorrectionForm.setDataLoadedFlag(false);
                updateDocumentSummary(laborCorrectionForm.getCorrectionDocument(), null, true);

                if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(laborCorrectionForm.getEditMethod())) {
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
                laborCorrectionForm.setRestrictedFunctionalityMode(false);
                
                //TODO:- need to change using file
                loadAllEntries(laborCorrectionForm.getInputGroupId(), laborCorrectionForm);

                if (laborCorrectionForm.getAllEntries().size() > 0) {
                    if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(laborCorrectionForm.getEditMethod())) {
                        laborCorrectionForm.setManualEditFlag(true);
                        laborCorrectionForm.setEditableFlag(false);
                        laborCorrectionForm.setDeleteFileFlag(false);
                    }
                    laborCorrectionForm.setDataLoadedFlag(true);
                }
                else {
                    GlobalVariables.getMessageMap().putError("documentsInSystem", KFSKeyConstants.ERROR_GL_ERROR_CORRECTION_NO_RECORDS);
                }
            }

            LaborCorrectionDocument document = laborCorrectionForm.getLaborCorrectionDocument();
            if (document.getCorrectionChangeGroup().isEmpty()) {
                document.addCorrectionChangeGroup(new CorrectionChangeGroup());
            }

            laborCorrectionForm.setPreviousInputGroupId(laborCorrectionForm.getInputGroupId());
        }

        laborCorrectionForm.setShowOutputFlag(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward confirmDeleteDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("confirmDeleteDocument() started");

        LaborCorrectionForm correctionForm = (LaborCorrectionForm) form;

        if (checkOriginEntryGroupSelection(correctionForm)) {
            String batchDirectory = SpringContext.getBean(LaborCorrectionDocumentService.class).getBatchFileDirectoryName();
            String doneFileName = batchDirectory + File.separator + correctionForm.getInputGroupId();
            String dataFileName = doneFileName.replace(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION, GeneralLedgerConstants.BatchFileSystem.EXTENSION);
            
            int groupCount = laborOriginEntryService.getGroupCount(dataFileName);
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
            
            LaborCorrectionDocument document = (LaborCorrectionDocument) correctionForm.getDocument();
            document.setCorrectionInputFileName(dataFileName);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

}

