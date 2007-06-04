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
package org.kuali.module.labor.web.struts.action;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.core.service.SequenceAccessorService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.exception.LoadException;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.module.gl.util.CorrectionDocumentUtils;
import org.kuali.module.gl.web.struts.action.CorrectionAction;
import org.kuali.module.gl.web.struts.form.CorrectionForm;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.service.LaborOriginEntryService;
import org.kuali.module.labor.web.struts.form.LaborCorrectionForm;
import org.kuali.rice.KNSServiceLocator;

public class LaborCorrectionAction extends CorrectionAction{

    LaborOriginEntryService laborOriginEntryService = SpringServiceLocator.getLaborOriginEntryService();
  
    /*
     * Upload a file
     */
    public ActionForward uploadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException, Exception {
        LOG.debug("uploadFile() started");

        CorrectionForm correctionForm = (CorrectionForm) form;
        CorrectionDocument document = (CorrectionDocument) correctionForm.getDocument();

        java.sql.Date today = CorrectionAction.dateTimeService.getCurrentSqlDate();
        OriginEntryGroup newOriginEntryGroup = CorrectionAction.originEntryGroupService.createGroup(today, OriginEntrySource.LABOR_CORRECTION_PROCESS_EDOC, false, false, false);

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
                        LaborOriginEntry entryFromFile = new LaborOriginEntry();
                        entryFromFile.setFromTextFile(currentLine, lineNumber);
                        entryFromFile.setEntryGroupId(newOriginEntryGroup.getId());
                        laborOriginEntryService.createEntry(entryFromFile, newOriginEntryGroup);
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
            document.setCorrectionInputGroupId(newOriginEntryGroup.getId());
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
                document.setCorrectionInputGroupId(newOriginEntryGroup.getId());
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
     * Show all entries for Manual edit with groupId and persist these entries to the DB
     * The restricted functionality mode flag MUST BE SET PRIOR TO CALLING this method.
     * 
     * @param groupId
     * @param correctionForm
     * @throws Exception
     */
    protected void loadAllEntries(Integer groupId, CorrectionForm correctionForm) throws Exception {
        LOG.debug("loadAllEntries() started");

        if (!correctionForm.isRestrictedFunctionalityMode()) {
            CorrectionDocument document = correctionForm.getCorrectionDocument();
            List<LaborOriginEntry> laborSearchResults = laborOriginEntryService.getEntriesByGroupId(groupId);
            List<OriginEntry> searchResults = new ArrayList();
            searchResults.addAll(laborSearchResults);
            
            correctionForm.setAllEntries(searchResults);
            correctionForm.setDisplayEntries(new ArrayList<OriginEntry> (searchResults));

            updateDocumentSummary(document, correctionForm.getAllEntries(), correctionForm.isRestrictedFunctionalityMode());
            
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
     * Add a new row to the group
     */
    public ActionForward addManualEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addManualEdit() started");

        LaborCorrectionForm laborCorrectionForm = (LaborCorrectionForm) form;
        CorrectionDocument document = laborCorrectionForm.getCorrectionDocument();

        if (validOriginEntry(laborCorrectionForm)) {
            laborCorrectionForm.updateEntryForManualEdit();

            // new entryId is always 0, so give it a unique Id, SequenceAccessorService is used.
            Long newEntryId = SpringServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("GL_ORIGIN_ENTRY_T_SEQ");
            laborCorrectionForm.getLaborEntryForManualEdit().setEntryId(new Integer(newEntryId.intValue()));

            laborCorrectionForm.getAllEntries().add(laborCorrectionForm.getLaborEntryForManualEdit());

            // Clear out the additional row
            laborCorrectionForm.clearEntryForManualEdit();
        }


        // Calculate the debit/credit/row count
        updateDocumentSummary(document, laborCorrectionForm.getAllEntries(), laborCorrectionForm.isRestrictedFunctionalityMode());

        laborCorrectionForm.setShowSummaryOutputFlag(true);

        // we've modified the list of all entries, so repersist it
        SpringServiceLocator.getGlCorrectionProcessOriginEntryService().persistAllEntries(laborCorrectionForm.getGlcpSearchResultsSequenceNumber(), laborCorrectionForm.getAllEntries());
        laborCorrectionForm.setDisplayEntries(new ArrayList<OriginEntry>(laborCorrectionForm.getAllEntries()));
        if (laborCorrectionForm.getShowOutputFlag()) {
            removeNonMatchingEntries(laborCorrectionForm.getDisplayEntries(), document.getCorrectionChangeGroup());
        }
        
        // list has changed, we'll need to repage and resort
        applyPagingAndSortingFromPreviousPageView(laborCorrectionForm);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    
   /* @Override
    public ActionForward selectSystemEditMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        
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
                    OriginEntryGroup g = SpringServiceLocator.getOriginEntryGroupService().getNewestScrubberErrorGroup();
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

    
*/    
    
}
