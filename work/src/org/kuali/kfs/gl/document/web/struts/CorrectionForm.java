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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;
import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.kfs.gl.document.web.CorrectionDocumentEntryMetadata;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.rice.kns.web.ui.Column;


/**
 * This class represents the action form for the Correction Document
 */
public class CorrectionForm extends KualiDocumentFormBase implements CorrectionDocumentEntryMetadata {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionForm.class);

    protected String docTitle;
    protected String htmlFormAction;
    protected String documentType;


    protected String chooseSystem;
    /**
     * Used to store the previously selected system, in case the user changed the selection when it's not appropriate, so that it
     * can be restored
     */
    protected String previousChooseSystem;

    protected String editMethod;
    /**
     * Used to store the previously selected edit method, in case the user changed the selection when it's not appropriate, so that
     * it can be restored
     */
    protected String previousEditMethod;

    /**
     * This is the input group ID selected when the last page was rendered
     */
    protected String previousInputGroupId;

    /**
     * This is the input group ID of the document when it was retrieved from the DB
     */
    protected String inputGroupIdFromLastDocumentLoad;

    /**
     * True only when the selected input group ID does not correspond to an input group in the system. True means that querying the
     * {@link org.kuali.kfs.gl.service.OriginEntryGroupService} for the group id last saved in the doc would turn up no results.
     */
    protected boolean inputGroupIdFromLastDocumentLoadIsMissing = false;

    /**
     * Whether the origin entries we should be displaying on the form are not currently persisted by the
     * {@link CorrectionDocumentService}.
     */
    protected boolean persistedOriginEntriesMissing = false;

    protected String outputGroupId;
    protected String inputFileName;
    protected FormFile sourceFile;
    protected boolean processInBatch = true;
    protected boolean matchCriteriaOnly = false;
    protected boolean dataLoadedFlag = false;
    protected boolean editableFlag = false;
    protected boolean manualEditFlag = false;
    protected boolean deleteFileFlag = false;
    protected boolean showOutputFlag = false;
    protected boolean showSummaryOutputFlag = false;
    protected boolean restrictedFunctionalityMode = false;
    protected List<OriginEntryFull> allEntries;
    protected List<OriginEntryFull> displayEntries;
    protected String entryUniversityFiscalYear;
    protected String entryFinancialDocumentReversalDate;
    protected String entryTransactionDate;
    protected String entryTransactionLedgerEntrySequenceNumber;
    protected String entryTransactionLedgerEntryAmount;


    /**
     * Used to identify the search results on the form
     */
    protected String glcpSearchResultsSequenceNumber;

    protected OriginEntryFull entryForManualEdit;

    protected List<GroupHolder> groups;

    protected transient KualiTableRenderFormMetadata originEntrySearchResultTableMetadata;

    public CorrectionForm() {
        super();

        // These are for the blank rows that are used to add criteria/changes
        groups = new ArrayList<GroupHolder>();

        entryForManualEdit = new OriginEntryFull();
        entryForManualEdit.setEntryId(0);
        
        setDocType();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "GLCP";
    }
    
    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        // Sync up the groups
        syncGroups();

        originEntrySearchResultTableMetadata = new KualiTableRenderFormMetadata();

        if (KFSConstants.TableRenderConstants.SWITCH_TO_PAGE_METHOD.equals(getMethodToCall())) {
            // look for the page number to switch to
            originEntrySearchResultTableMetadata.setSwitchToPageNumber(-1);

            // the param we're looking for looks like: methodToCall.switchToPage.1.x , where 1 is the page nbr
            String paramPrefix = KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + KFSConstants.TableRenderConstants.SWITCH_TO_PAGE_METHOD + ".";
            for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
                String parameterName = (String) i.nextElement();
                if (parameterName.startsWith(paramPrefix)) {
                    String switchToPageNumberStr = StringUtils.substringBetween(parameterName, paramPrefix, ".");
                    originEntrySearchResultTableMetadata.setSwitchToPageNumber(Integer.parseInt(switchToPageNumberStr));
                }
            }
            if (originEntrySearchResultTableMetadata.getSwitchToPageNumber() == -1) {
                throw new RuntimeException("Couldn't find page number");
            }
        }

        if (KFSConstants.TableRenderConstants.SORT_METHOD.equals(getMethodToCall())) {
            originEntrySearchResultTableMetadata.setColumnToSortIndex(-1);

            // the param we're looking for looks like: methodToCall.sort.1.x , where 1 is the column to sort on
            String paramPrefix = KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + KFSConstants.TableRenderConstants.SORT_METHOD + ".";
            for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
                String parameterName = (String) i.nextElement();
                if (parameterName.startsWith(paramPrefix) && parameterName.endsWith(".x")) {
                    String columnToSortStr = StringUtils.substringBetween(parameterName, paramPrefix, ".");
                    originEntrySearchResultTableMetadata.setColumnToSortIndex(Integer.parseInt(columnToSortStr));
                }
            }
            if (originEntrySearchResultTableMetadata.getColumnToSortIndex() == -1) {
                throw new RuntimeException("Couldn't find column to sort");
            }
        }
        
        // since the processInBatch option defaults to true, there's no built in POJO way to detect whether it's been unchecked
        // this code takes care of that
        if (StringUtils.isNotBlank(request.getParameter("processInBatch" + KFSConstants.CHECKBOX_PRESENT_ON_FORM_ANNOTATION)) && StringUtils.isBlank(request.getParameter("processInBatch"))) {
            setProcessInBatch(false);
        }
        
        if (StringUtils.isNotBlank(request.getParameter("matchCriteriaOnly" + KFSConstants.CHECKBOX_PRESENT_ON_FORM_ANNOTATION)) && StringUtils.isBlank(request.getParameter("matchCriteriaOnly"))) {
            setMatchCriteriaOnly(false);
        }
    }

    /**
     * This method synchronizes number of group holders added with the group count
     * 
     */
    public void syncGroups() {
        int groupCount = getCorrectionDocument().getCorrectionChangeGroup().size();
        getGroupsItem(groupCount);
    }

    /**
     * Return group sizes
     * 
     * @return
     */
    public int getGroupsSize() {
        return groups.size();
    }

    /**
     * Returns group item with given ID
     * 
     * @param i index of group
     * @return
     */
    public GroupHolder getGroupsItem(int i) {
        while (i >= groups.size()) {
            groups.add(new GroupHolder());
        }
        return groups.get(i);
    }

    /**
     * Clears correction document form
     */
    public void clearForm() {
        chooseSystem = "";
        editMethod = "";
        inputFileName = "";
        outputGroupId = null;
        processInBatch = true;
        matchCriteriaOnly = false;
        dataLoadedFlag = false;
        editableFlag = false;
        manualEditFlag = false;
        deleteFileFlag = false;
        showOutputFlag = false;
        allEntries = new ArrayList<OriginEntryFull>();
        displayEntries = new ArrayList<OriginEntryFull>();

        restrictedFunctionalityMode = false;

        setDocument(null);
        instantiateDocument();
        
        // These are for the blank rows that are used to add criteria/changes
        groups = new ArrayList<GroupHolder>();

        inputGroupIdFromLastDocumentLoad = null;
        inputGroupIdFromLastDocumentLoadIsMissing = false;
        persistedOriginEntriesMissing = false;

        // Sync up the groups
        syncGroups();

        entryForManualEdit = new OriginEntryFull();
        entryForManualEdit.setEntryId(0);
    }

    /**
     * Update origin entry for manual edit with correction document attributes (i.e. university fiscal year, entry transaction ledger 
     * sequence number, entry transaction ledger entry amount, entry transaction date, entry financial document reversal date)
     */
    public void updateEntryForManualEdit() {
        entryForManualEdit.setFieldValue("universityFiscalYear", getEntryUniversityFiscalYear());
        entryForManualEdit.setFieldValue("transactionLedgerEntrySequenceNumber", getEntryTransactionLedgerEntrySequenceNumber());
        entryForManualEdit.setFieldValue("transactionLedgerEntryAmount", getEntryTransactionLedgerEntryAmount());
        entryForManualEdit.setFieldValue("transactionDate", getEntryTransactionDate());
        entryForManualEdit.setFieldValue("financialDocumentReversalDate", getEntryFinancialDocumentReversalDate());
    }

    /**
     * Clears origin entry for manual edit
     * 
     */
    public void clearEntryForManualEdit() {
        OriginEntryFull oe = new OriginEntryFull();
        oe.setEntryId(0);
        oe.setSubAccountNumber("");
        oe.setFinancialSubObjectCode("");
        oe.setProjectCode("");
        setEntryFinancialDocumentReversalDate("");
        setEntryTransactionDate("");
        setEntryTransactionLedgerEntryAmount("");
        setEntryTransactionLedgerEntrySequenceNumber("");
        setEntryUniversityFiscalYear("");
        setEntryForManualEdit(oe);
    }

    /**
     * Return size of list of all OriginEntryInformation objects
     * @return size of entries size
     */
    public Integer getAllEntriesSize() {
        return (allEntries == null) ? null : allEntries.size();
    }

    public GeneralLedgerCorrectionProcessDocument getCorrectionDocument() {
        return (GeneralLedgerCorrectionProcessDocument) getDocument();
    }

    public String getEntryFinancialDocumentReversalDate() {
        return entryFinancialDocumentReversalDate;
    }

    public List<OriginEntryFull> getDisplayEntries() {
        return displayEntries;
    }

    public void setDisplayEntries(List<OriginEntryFull> displayEntries) {
        this.displayEntries = displayEntries;
    }

    public void setEntryFinancialDocumentReversalDate(String entryFinancialDocumentReversalDate) {
        this.entryFinancialDocumentReversalDate = entryFinancialDocumentReversalDate;
    }

    public String getEntryTransactionDate() {
        return entryTransactionDate;
    }

    public void setEntryTransactionDate(String entryTransactionDate) {
        this.entryTransactionDate = entryTransactionDate;
    }

    public String getEntryTransactionLedgerEntryAmount() {
        return entryTransactionLedgerEntryAmount;
    }

    public void setEntryTransactionLedgerEntryAmount(String entryTransactionLedgerEntryAmount) {
        this.entryTransactionLedgerEntryAmount = entryTransactionLedgerEntryAmount;
    }

    public String getEntryTransactionLedgerEntrySequenceNumber() {
        return entryTransactionLedgerEntrySequenceNumber;
    }

    public void setEntryTransactionLedgerEntrySequenceNumber(String entryTransactionLedgerEntrySequenceNumber) {
        this.entryTransactionLedgerEntrySequenceNumber = entryTransactionLedgerEntrySequenceNumber;
    }

    public String getEntryUniversityFiscalYear() {
        return entryUniversityFiscalYear;
    }

    public void setEntryUniversityFiscalYear(String entryUniversityFiscalYear) {
        this.entryUniversityFiscalYear = entryUniversityFiscalYear;
    }

    public boolean getShowOutputFlag() {
        return showOutputFlag;
    }

    public void setShowOutputFlag(boolean showOutputFlag) {
        this.showOutputFlag = showOutputFlag;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public String getOutputGroupId() {
        return outputGroupId;
    }

    public void setOutputGroupId(String outputGroupId) {
        this.outputGroupId = outputGroupId;
    }

    public String getChooseSystem() {
        return chooseSystem;
    }

    public void setChooseSystem(String chooseSystem) {
        this.chooseSystem = chooseSystem;
    }

    public String getEditMethod() {
        return editMethod;
    }

    public void setEditMethod(String editMethod) {
        this.editMethod = editMethod;
    }
    
    public String getInputGroupId() {
        return ((GeneralLedgerCorrectionProcessDocument) getDocument()).getCorrectionInputFileName();
    }

    public boolean getProcessInBatch() {
        return processInBatch;
    }

    public void setProcessInBatch(boolean processInBatch) {
        this.processInBatch = processInBatch;
    }

    public List<OriginEntryFull> getAllEntries() {
        return allEntries;
    }

    public void setAllEntries(List<OriginEntryFull> allEntriesForManualEdit) {
        this.allEntries = allEntriesForManualEdit;
    }

    public OriginEntryFull getEntryForManualEdit() {
        return entryForManualEdit;
    }

    public void setEntryForManualEdit(OriginEntryFull entryForManualEdit) {
        this.entryForManualEdit = entryForManualEdit;
    }

    public FormFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(FormFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public boolean getMatchCriteriaOnly() {
        return matchCriteriaOnly;
    }

    public void setMatchCriteriaOnly(boolean matchCriteriaOnly) {
        this.matchCriteriaOnly = matchCriteriaOnly;
    }

    public boolean getDataLoadedFlag() {
        return dataLoadedFlag;
    }

    public void setDataLoadedFlag(boolean dataLoadedFlag) {
        this.dataLoadedFlag = dataLoadedFlag;
    }

    public boolean getDeleteFileFlag() {
        return deleteFileFlag;
    }

    public void setDeleteFileFlag(boolean deleteFileFlag) {
        this.deleteFileFlag = deleteFileFlag;
    }

    public boolean getEditableFlag() {
        return editableFlag;
    }

    public void setEditableFlag(boolean editableFlag) {
        this.editableFlag = editableFlag;
    }

    public boolean getManualEditFlag() {
        return manualEditFlag;
    }

    public void setManualEditFlag(boolean manualEditFlag) {
        this.manualEditFlag = manualEditFlag;
    }

    public boolean getShowSummaryOutputFlag() {
        return showSummaryOutputFlag;
    }

    public void setShowSummaryOutputFlag(boolean showSummaryOutputFlag) {
        this.showSummaryOutputFlag = showSummaryOutputFlag;
    }

    /**
     * Gets the originEntrySearchResultTableMetadata attribute.
     * 
     * @return Returns the originEntrySearchResultTableMetadata.
     */
    public KualiTableRenderFormMetadata getOriginEntrySearchResultTableMetadata() {
        return originEntrySearchResultTableMetadata;
    }

    
    /**
     * Returns list of Column objects for table render column meta data
     * 
     * @return list of column objects
     */
    public List<Column> getTableRenderColumnMetadata() {
        return SpringContext.getBean(CorrectionDocumentService.class).getTableRenderColumnMetadata(getDocument().getDocumentNumber());
    }

    /**
     * Gets the restrictedFunctionalityMode attribute.
     * 
     * @return Returns the restrictedFunctionalityMode.
     */
    public boolean isRestrictedFunctionalityMode() {
        return restrictedFunctionalityMode;
    }

    /**
     * Sets the restrictedFunctionalityMode attribute value.
     * 
     * @param restrictedFunctionalityMode The restrictedFunctionalityMode to set.
     */
    public void setRestrictedFunctionalityMode(boolean restrictedFunctionalityMode) {
        this.restrictedFunctionalityMode = restrictedFunctionalityMode;
    }

    /**
     * Gets the glcpSearchResuiltsSequenceNumber attribute.
     * 
     * @return Returns the glcpSearchResuiltsSequenceNumber.
     */
    public String getGlcpSearchResultsSequenceNumber() {
        return glcpSearchResultsSequenceNumber;
    }

    /**
     * Sets the glcpSearchResuiltsSequenceNumber attribute value.
     * 
     * @param glcpSearchResuiltsSequenceNumber The glcpSearchResuiltsSequenceNumber to set.
     */
    public void setGlcpSearchResultsSequenceNumber(String glcpSearchResuiltsSequenceNumber) {
        this.glcpSearchResultsSequenceNumber = glcpSearchResuiltsSequenceNumber;
    }

    /**
     * Gets the previousChooseSystem attribute.
     * 
     * @return Returns the previousChooseSystem.
     */
    public String getPreviousChooseSystem() {
        return previousChooseSystem;
    }

    /**
     * Sets the previousChooseSystem attribute value.
     * 
     * @param previousChooseSystem The previousChooseSystem to set.
     */
    public void setPreviousChooseSystem(String previousChooseSystem) {
        this.previousChooseSystem = previousChooseSystem;
    }

    /**
     * Gets the previousEditMethod attribute.
     * 
     * @return Returns the previousEditMethod.
     */
    public String getPreviousEditMethod() {
        return previousEditMethod;
    }

    /**
     * Sets the previousEditMethod attribute value.
     * 
     * @param previousEditMethod The previousEditMethod to set.
     */
    public void setPreviousEditMethod(String previousEditMethod) {
        this.previousEditMethod = previousEditMethod;
    }

    /**
     * Gets the previousInputGroupId attribute.
     * 
     * @return Returns the previousInputGroupId.
     */
    public String getPreviousInputGroupId() {
        return previousInputGroupId;
    }

    /**
     * Sets the previousInputGroupId attribute value.
     * 
     * @param previousInputGroupId The previousInputGroupId to set.
     */
    public void setPreviousInputGroupId(String previousInputGroupId) {
        this.previousInputGroupId = previousInputGroupId;
    }

    /**
     * Gets the input group ID of the document when it was persisted in the DB
     * 
     * @return the input group ID of the document when it was persisted in the DB
     */
    public String getInputGroupIdFromLastDocumentLoad() {
        return inputGroupIdFromLastDocumentLoad;
    }

    /**
     * Sets the input group ID of the document when it was persisted in the DB
     * 
     * @param inputGroupIdFromLastDocumentLoad the input group ID of the document when it was persisted in the DB
     */
    public void setInputGroupIdFromLastDocumentLoad(String inputGroupIdFromLastDocumentLoad) {
        this.inputGroupIdFromLastDocumentLoad = inputGroupIdFromLastDocumentLoad;
    }

    /**
     * Gets whether the selected input group ID does not correspond to an input group in the system.
     * 
     * @return Returns the inputGroupIdFromLastDocumentLoadIsMissing.
     */
    public boolean isInputGroupIdFromLastDocumentLoadIsMissing() {
        return inputGroupIdFromLastDocumentLoadIsMissing;
    }

    /**
     * Sets whether the selected input group ID does not correspond to an input group in the system
     * 
     * @param inputGroupIdFromLastDocumentLoadIsMissing The inputGroupIdFromLastDocumentLoadIsMissing to set.
     */
    public void setInputGroupIdFromLastDocumentLoadIsMissing(boolean inputGroupIdFromLastDocumentLoadIsMissing) {
        this.inputGroupIdFromLastDocumentLoadIsMissing = inputGroupIdFromLastDocumentLoadIsMissing;
    }

    /**
     * Gets whether the origin entries we should be displaying on the form are not currently persisted by the
     * {@link CorrectionDocumentService}.
     * 
     * @return Returns the persistedOriginEntriesMissing.
     */
    public boolean isPersistedOriginEntriesMissing() {
        return persistedOriginEntriesMissing;
    }

    /**
     * Sets whether the origin entries we should be displaying on the form are not currently persisted by the
     * {@link CorrectionDocumentService}.
     * 
     * @param persistedOriginEntriesMissing The persistedOriginEntriesMissing to set.
     */
    public void setPersistedOriginEntriesMissing(boolean persistedOriginEntriesMissing) {
        this.persistedOriginEntriesMissing = persistedOriginEntriesMissing;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getHtmlFormAction() {
        return htmlFormAction;
    }

    public void setHtmlFormAction(String htmlFormAction) {
        this.htmlFormAction = htmlFormAction;
    }

    /**
     * Set document type = "GLCP", document title = "General Ledger Correction Process", html form action = "generalLedgerCorrect"
     */
    public void setDocType() {
        setDocumentType("GLCP");
        //setDocTitle("General Ledger Correction Process");
        setDocTitle("GeneralLedgerCorrectionProcessDocument");
        setHtmlFormAction("generalLedgerCorrection");
    }

    /**
     * Adds the origin entry max file size to the list of max file sizes.
     * 
     * @see org.kuali.rice.kns.web.struts.form.pojo.PojoFormBase#customInitMaxUploadSizes()
     */
    @Override
    protected void customInitMaxUploadSizes() {
        super.customInitMaxUploadSizes();
        addMaxUploadSize(SpringContext.getBean(ParameterService.class).getParameterValueAsString(GeneralLedgerCorrectionProcessDocument.class, KFSConstants.ORIGIN_ENTRY_IMPORT_MAX_FILE_SIZE_PARM_NM));
    }

}
