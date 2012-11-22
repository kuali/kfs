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
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.tem.businessobject.AgencyEntryFull;
import org.kuali.kfs.module.tem.document.TemCorrectionProcessDocument;
import org.kuali.kfs.module.tem.document.service.TemCorrectionDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.rice.kns.web.ui.Column;

public class TemCorrectionForm extends KualiDocumentFormBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TemCorrectionForm.class);

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
    protected boolean persistedAgencyEntriesMissing = false;

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
    protected List<AgencyEntryFull> allEntries;
    protected List<AgencyEntryFull> displayEntries;
    protected String entryUniversityFiscalYear;
    protected String entryFinancialDocumentReversalDate;
    protected String entryTransactionDate;
    protected String entryTransactionLedgerEntrySequenceNumber;
    protected String entryTransactionLedgerEntryAmount;
    protected AgencyEntryFull entryForManualEdit;
   

    protected transient KualiTableRenderFormMetadata agencyEntrySearchResultTableMetadata;
    
    public TemCorrectionForm() {
        super();

        entryForManualEdit = new AgencyEntryFull();
        entryForManualEdit.setEntryId(0);
        setChooseSystem(TemCorrectionDocumentService.SYSTEM_DATABASE);
        setPreviousChooseSystem(TemCorrectionDocumentService.SYSTEM_DATABASE);
        setDocType();
    }
    
    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        // Sync up the groups
        syncGroups();

        agencyEntrySearchResultTableMetadata = new KualiTableRenderFormMetadata();

        if (KFSConstants.TableRenderConstants.SWITCH_TO_PAGE_METHOD.equals(getMethodToCall())) {
            // look for the page number to switch to
            agencyEntrySearchResultTableMetadata.setSwitchToPageNumber(-1);

            // the param we're looking for looks like: methodToCall.switchToPage.1.x , where 1 is the page nbr
            String paramPrefix = KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + KFSConstants.TableRenderConstants.SWITCH_TO_PAGE_METHOD + ".";
            for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
                String parameterName = (String) i.nextElement();
                if (parameterName.startsWith(paramPrefix)) {
                    String switchToPageNumberStr = StringUtils.substringBetween(parameterName, paramPrefix, ".");
                    agencyEntrySearchResultTableMetadata.setSwitchToPageNumber(Integer.parseInt(switchToPageNumberStr));
                }
            }
            if (agencyEntrySearchResultTableMetadata.getSwitchToPageNumber() == -1) {
                throw new RuntimeException("Couldn't find page number");
            }
        }

        if (KFSConstants.TableRenderConstants.SORT_METHOD.equals(getMethodToCall())) {
            agencyEntrySearchResultTableMetadata.setColumnToSortIndex(-1);

            // the param we're looking for looks like: methodToCall.sort.1.x , where 1 is the column to sort on
            String paramPrefix = KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + KFSConstants.TableRenderConstants.SORT_METHOD + ".";
            for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
                String parameterName = (String) i.nextElement();
                if (parameterName.startsWith(paramPrefix) && parameterName.endsWith(".x")) {
                    String columnToSortStr = StringUtils.substringBetween(parameterName, paramPrefix, ".");
                    agencyEntrySearchResultTableMetadata.setColumnToSortIndex(Integer.parseInt(columnToSortStr));
                }
            }
            if (agencyEntrySearchResultTableMetadata.getColumnToSortIndex() == -1) {
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
        //getGroupsItem(groupCount);
    }
    
    /**
     * Return group sizes
     * 
     * @return
     */
    public int getGroupsSize() {
        //return groups.size();
        return 0;
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
        allEntries = new ArrayList<AgencyEntryFull>();
        displayEntries = new ArrayList<AgencyEntryFull>();

        restrictedFunctionalityMode = false;

        setDocument(null);
        instantiateDocument();

        inputGroupIdFromLastDocumentLoad = null;
        inputGroupIdFromLastDocumentLoadIsMissing = false;
        persistedAgencyEntriesMissing = false;

        // Sync up the groups
        syncGroups();

        entryForManualEdit = new AgencyEntryFull();
        entryForManualEdit.setEntryId(0);
    }
    
    /**
     * Clears agency entry for manual edit
     * 
     */
    public void clearEntryForManualEdit() {
        AgencyEntryFull ae = new AgencyEntryFull();
        ae.setEntryId(0);
        setEntryFinancialDocumentReversalDate("");
        setEntryTransactionDate("");
        setEntryTransactionLedgerEntryAmount("");
        setEntryTransactionLedgerEntrySequenceNumber("");
        setEntryUniversityFiscalYear("");
        setEntryForManualEdit(ae);
    }
    
    @Override
    protected String getDefaultDocumentTypeName() {
        return "TMCP";
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
     * 
     */
    public void setDocType() {
        setDocumentType("TMCP");
        setDocTitle("TemCorrectionProcessDocument");
        setHtmlFormAction("temCorrectionDocument");
    }

    /**
     * Gets the chooseSystem attribute. 
     * @return Returns the chooseSystem.
     */
    public String getChooseSystem() {
        return chooseSystem;
    }

    /**
     * Sets the chooseSystem attribute value.
     * @param chooseSystem The chooseSystem to set.
     */
    public void setChooseSystem(String chooseSystem) {
        this.chooseSystem = chooseSystem;
    }

    /**
     * Gets the previousChooseSystem attribute. 
     * @return Returns the previousChooseSystem.
     */
    public String getPreviousChooseSystem() {
        return previousChooseSystem;
    }

    /**
     * Sets the previousChooseSystem attribute value.
     * @param previousChooseSystem The previousChooseSystem to set.
     */
    public void setPreviousChooseSystem(String previousChooseSystem) {
        this.previousChooseSystem = previousChooseSystem;
    }

    /**
     * Gets the editMethod attribute. 
     * @return Returns the editMethod.
     */
    public String getEditMethod() {
        return editMethod;
    }

    /**
     * Sets the editMethod attribute value.
     * @param editMethod The editMethod to set.
     */
    public void setEditMethod(String editMethod) {
        this.editMethod = editMethod;
    }
    
    public String getInputGroupId() {
        return ((TemCorrectionProcessDocument) getDocument()).getCorrectionInputFileName();
    }

    /**
     * Gets the previousEditMethod attribute. 
     * @return Returns the previousEditMethod.
     */
    public String getPreviousEditMethod() {
        return previousEditMethod;
    }

    /**
     * Sets the previousEditMethod attribute value.
     * @param previousEditMethod The previousEditMethod to set.
     */
    public void setPreviousEditMethod(String previousEditMethod) {
        this.previousEditMethod = previousEditMethod;
    }

    /**
     * Gets the previousInputGroupId attribute. 
     * @return Returns the previousInputGroupId.
     */
    public String getPreviousInputGroupId() {
        return previousInputGroupId;
    }

    /**
     * Sets the previousInputGroupId attribute value.
     * @param previousInputGroupId The previousInputGroupId to set.
     */
    public void setPreviousInputGroupId(String previousInputGroupId) {
        this.previousInputGroupId = previousInputGroupId;
    }

    /**
     * Gets the inputGroupIdFromLastDocumentLoad attribute. 
     * @return Returns the inputGroupIdFromLastDocumentLoad.
     */
    public String getInputGroupIdFromLastDocumentLoad() {
        return inputGroupIdFromLastDocumentLoad;
    }

    /**
     * Sets the inputGroupIdFromLastDocumentLoad attribute value.
     * @param inputGroupIdFromLastDocumentLoad The inputGroupIdFromLastDocumentLoad to set.
     */
    public void setInputGroupIdFromLastDocumentLoad(String inputGroupIdFromLastDocumentLoad) {
        this.inputGroupIdFromLastDocumentLoad = inputGroupIdFromLastDocumentLoad;
    }

    /**
     * Gets the inputGroupIdFromLastDocumentLoadIsMissing attribute. 
     * @return Returns the inputGroupIdFromLastDocumentLoadIsMissing.
     */
    public boolean isInputGroupIdFromLastDocumentLoadIsMissing() {
        return inputGroupIdFromLastDocumentLoadIsMissing;
    }

    /**
     * Sets the inputGroupIdFromLastDocumentLoadIsMissing attribute value.
     * @param inputGroupIdFromLastDocumentLoadIsMissing The inputGroupIdFromLastDocumentLoadIsMissing to set.
     */
    public void setInputGroupIdFromLastDocumentLoadIsMissing(boolean inputGroupIdFromLastDocumentLoadIsMissing) {
        this.inputGroupIdFromLastDocumentLoadIsMissing = inputGroupIdFromLastDocumentLoadIsMissing;
    }

    /**
     * Gets the persistedOriginEntriesMissing attribute. 
     * @return Returns the persistedOriginEntriesMissing.
     */
    public boolean isPersistedOriginEntriesMissing() {
        return persistedAgencyEntriesMissing;
    }

    /**
     * Sets the persistedOriginEntriesMissing attribute value.
     * @param persistedOriginEntriesMissing The persistedOriginEntriesMissing to set.
     */
    public void setPersistedOriginEntriesMissing(boolean persistedOriginEntriesMissing) {
        this.persistedAgencyEntriesMissing = persistedOriginEntriesMissing;
    }

    /**
     * Gets the outputGroupId attribute. 
     * @return Returns the outputGroupId.
     */
    public String getOutputGroupId() {
        return outputGroupId;
    }

    /**
     * Sets the outputGroupId attribute value.
     * @param outputGroupId The outputGroupId to set.
     */
    public void setOutputGroupId(String outputGroupId) {
        this.outputGroupId = outputGroupId;
    }

    /**
     * Gets the inputFileName attribute. 
     * @return Returns the inputFileName.
     */
    public String getInputFileName() {
        return inputFileName;
    }

    /**
     * Sets the inputFileName attribute value.
     * @param inputFileName The inputFileName to set.
     */
    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    /**
     * Gets the sourceFile attribute. 
     * @return Returns the sourceFile.
     */
    public FormFile getSourceFile() {
        return sourceFile;
    }

    /**
     * Sets the sourceFile attribute value.
     * @param sourceFile The sourceFile to set.
     */
    public void setSourceFile(FormFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * Gets the processInBatch attribute. 
     * @return Returns the processInBatch.
     */
    public boolean isProcessInBatch() {
        return processInBatch;
    }

    /**
     * Sets the processInBatch attribute value.
     * @param processInBatch The processInBatch to set.
     */
    public void setProcessInBatch(boolean processInBatch) {
        this.processInBatch = processInBatch;
    }

    /**
     * Gets the matchCriteriaOnly attribute. 
     * @return Returns the matchCriteriaOnly.
     */
    public boolean isMatchCriteriaOnly() {
        return matchCriteriaOnly;
    }

    /**
     * Sets the matchCriteriaOnly attribute value.
     * @param matchCriteriaOnly The matchCriteriaOnly to set.
     */
    public void setMatchCriteriaOnly(boolean matchCriteriaOnly) {
        this.matchCriteriaOnly = matchCriteriaOnly;
    }

    /**
     * Gets the dataLoadedFlag attribute. 
     * @return Returns the dataLoadedFlag.
     */
    public boolean isDataLoadedFlag() {
        return dataLoadedFlag;
    }

    /**
     * Sets the dataLoadedFlag attribute value.
     * @param dataLoadedFlag The dataLoadedFlag to set.
     */
    public void setDataLoadedFlag(boolean dataLoadedFlag) {
        this.dataLoadedFlag = dataLoadedFlag;
    }

    /**
     * Gets the editableFlag attribute. 
     * @return Returns the editableFlag.
     */
    public boolean isEditableFlag() {
        return editableFlag;
    }

    /**
     * Sets the editableFlag attribute value.
     * @param editableFlag The editableFlag to set.
     */
    public void setEditableFlag(boolean editableFlag) {
        this.editableFlag = editableFlag;
    }

    /**
     * Gets the manualEditFlag attribute. 
     * @return Returns the manualEditFlag.
     */
    public boolean isManualEditFlag() {
        return manualEditFlag;
    }

    /**
     * Sets the manualEditFlag attribute value.
     * @param manualEditFlag The manualEditFlag to set.
     */
    public void setManualEditFlag(boolean manualEditFlag) {
        this.manualEditFlag = manualEditFlag;
    }

    /**
     * Gets the deleteFileFlag attribute. 
     * @return Returns the deleteFileFlag.
     */
    public boolean isDeleteFileFlag() {
        return deleteFileFlag;
    }

    /**
     * Sets the deleteFileFlag attribute value.
     * @param deleteFileFlag The deleteFileFlag to set.
     */
    public void setDeleteFileFlag(boolean deleteFileFlag) {
        this.deleteFileFlag = deleteFileFlag;
    }

    /**
     * Gets the showOutputFlag attribute. 
     * @return Returns the showOutputFlag.
     */
    public boolean isShowOutputFlag() {
        return showOutputFlag;
    }

    /**
     * Sets the showOutputFlag attribute value.
     * @param showOutputFlag The showOutputFlag to set.
     */
    public void setShowOutputFlag(boolean showOutputFlag) {
        this.showOutputFlag = showOutputFlag;
    }

    /**
     * Gets the showSummaryOutputFlag attribute. 
     * @return Returns the showSummaryOutputFlag.
     */
    public boolean isShowSummaryOutputFlag() {
        return showSummaryOutputFlag;
    }

    /**
     * Sets the showSummaryOutputFlag attribute value.
     * @param showSummaryOutputFlag The showSummaryOutputFlag to set.
     */
    public void setShowSummaryOutputFlag(boolean showSummaryOutputFlag) {
        this.showSummaryOutputFlag = showSummaryOutputFlag;
    }

    /**
     * Gets the restrictedFunctionalityMode attribute. 
     * @return Returns the restrictedFunctionalityMode.
     */
    public boolean isRestrictedFunctionalityMode() {
        return restrictedFunctionalityMode;
    }

    /**
     * Sets the restrictedFunctionalityMode attribute value.
     * @param restrictedFunctionalityMode The restrictedFunctionalityMode to set.
     */
    public void setRestrictedFunctionalityMode(boolean restrictedFunctionalityMode) {
        this.restrictedFunctionalityMode = restrictedFunctionalityMode;
    }

    /**
     * Gets the displayEntries attribute. 
     * @return Returns the displayEntries.
     */
    public List<AgencyEntryFull> getDisplayEntries() {
        return displayEntries;
    }

    /**
     * Sets the displayEntries attribute value.
     * @param displayEntries The displayEntries to set.
     */
    public void setDisplayEntries(List<AgencyEntryFull> displayEntries) {
        this.displayEntries = displayEntries;
    }

    /**
     * Gets the entryUniversityFiscalYear attribute. 
     * @return Returns the entryUniversityFiscalYear.
     */
    public String getEntryUniversityFiscalYear() {
        return entryUniversityFiscalYear;
    }

    /**
     * Sets the entryUniversityFiscalYear attribute value.
     * @param entryUniversityFiscalYear The entryUniversityFiscalYear to set.
     */
    public void setEntryUniversityFiscalYear(String entryUniversityFiscalYear) {
        this.entryUniversityFiscalYear = entryUniversityFiscalYear;
    }

    /**
     * Gets the entryFinancialDocumentReversalDate attribute. 
     * @return Returns the entryFinancialDocumentReversalDate.
     */
    public String getEntryFinancialDocumentReversalDate() {
        return entryFinancialDocumentReversalDate;
    }

    /**
     * Sets the entryFinancialDocumentReversalDate attribute value.
     * @param entryFinancialDocumentReversalDate The entryFinancialDocumentReversalDate to set.
     */
    public void setEntryFinancialDocumentReversalDate(String entryFinancialDocumentReversalDate) {
        this.entryFinancialDocumentReversalDate = entryFinancialDocumentReversalDate;
    }

    /**
     * Gets the entryTransactionDate attribute. 
     * @return Returns the entryTransactionDate.
     */
    public String getEntryTransactionDate() {
        return entryTransactionDate;
    }

    /**
     * Sets the entryTransactionDate attribute value.
     * @param entryTransactionDate The entryTransactionDate to set.
     */
    public void setEntryTransactionDate(String entryTransactionDate) {
        this.entryTransactionDate = entryTransactionDate;
    }

    /**
     * Gets the entryTransactionLedgerEntrySequenceNumber attribute. 
     * @return Returns the entryTransactionLedgerEntrySequenceNumber.
     */
    public String getEntryTransactionLedgerEntrySequenceNumber() {
        return entryTransactionLedgerEntrySequenceNumber;
    }

    /**
     * Sets the entryTransactionLedgerEntrySequenceNumber attribute value.
     * @param entryTransactionLedgerEntrySequenceNumber The entryTransactionLedgerEntrySequenceNumber to set.
     */
    public void setEntryTransactionLedgerEntrySequenceNumber(String entryTransactionLedgerEntrySequenceNumber) {
        this.entryTransactionLedgerEntrySequenceNumber = entryTransactionLedgerEntrySequenceNumber;
    }

    /**
     * Gets the entryTransactionLedgerEntryAmount attribute. 
     * @return Returns the entryTransactionLedgerEntryAmount.
     */
    public String getEntryTransactionLedgerEntryAmount() {
        return entryTransactionLedgerEntryAmount;
    }

    /**
     * Sets the entryTransactionLedgerEntryAmount attribute value.
     * @param entryTransactionLedgerEntryAmount The entryTransactionLedgerEntryAmount to set.
     */
    public void setEntryTransactionLedgerEntryAmount(String entryTransactionLedgerEntryAmount) {
        this.entryTransactionLedgerEntryAmount = entryTransactionLedgerEntryAmount;
    }

    public TemCorrectionProcessDocument getCorrectionDocument() {
        if(ObjectUtils.isNotNull(this.getDocument())) {
            return (TemCorrectionProcessDocument)this.getDocument();
        } else {
            return null;
        }
    }

    /**
     * Gets the allEntries attribute. 
     * @return Returns the allEntries.
     */
    public List<AgencyEntryFull> getAllEntries() {
        return allEntries;
    }

    /**
     * Sets the allEntries attribute value.
     * @param allEntries The allEntries to set.
     */
    public void setAllEntries(List<AgencyEntryFull> allEntries) {
        this.allEntries = allEntries;
    }
    
    /**
     * Return size of list of all OriginEntryInformation objects
     * @return size of entries size
     */
    public Integer getAllEntriesSize() {
        return (allEntries == null) ? null : allEntries.size();
    }
    
    
    
    /**
     * Gets the agencyEntrySearchResultTableMetadata attribute.
     * 
     * @return Returns the agencyEntrySearchResultTableMetadata.
     */
    public KualiTableRenderFormMetadata getAgencyEntrySearchResultTableMetadata() {
        return agencyEntrySearchResultTableMetadata;
    }

    
    /**
     * Returns list of Column objects for table render column meta data
     * 
     * @return list of column objects
     */
    public List<Column> getTableRenderColumnMetadata() {
        return SpringContext.getBean(TemCorrectionDocumentService.class).getTableRenderColumnMetadata(getDocument().getDocumentNumber());
    }

    /**
     * Gets the persistedAgencyEntriesMissing attribute. 
     * @return Returns the persistedAgencyEntriesMissing.
     */
    public boolean isPersistedAgencyEntriesMissing() {
        return persistedAgencyEntriesMissing;
    }

    /**
     * Sets the persistedAgencyEntriesMissing attribute value.
     * @param persistedAgencyEntriesMissing The persistedAgencyEntriesMissing to set.
     */
    public void setPersistedAgencyEntriesMissing(boolean persistedAgencyEntriesMissing) {
        this.persistedAgencyEntriesMissing = persistedAgencyEntriesMissing;
    }

    /**
     * Gets the entryForManualEdit attribute. 
     * @return Returns the entryForManualEdit.
     */
    public AgencyEntryFull getEntryForManualEdit() {
        return entryForManualEdit;
    }

    /**
     * Sets the entryForManualEdit attribute value.
     * @param entryForManualEdit The entryForManualEdit to set.
     */
    public void setEntryForManualEdit(AgencyEntryFull entryForManualEdit) {
        this.entryForManualEdit = entryForManualEdit;
    }

    /**
     * Sets the agencyEntrySearchResultTableMetadata attribute value.
     * @param agencyEntrySearchResultTableMetadata The agencyEntrySearchResultTableMetadata to set.
     */
    public void setAgencyEntrySearchResultTableMetadata(KualiTableRenderFormMetadata agencyEntrySearchResultTableMetadata) {
        this.agencyEntrySearchResultTableMetadata = agencyEntrySearchResultTableMetadata;
    }
    

}
