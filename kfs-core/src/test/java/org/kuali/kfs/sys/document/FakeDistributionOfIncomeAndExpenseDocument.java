package org.kuali.kfs.sys.document;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.sys.businessobject.*;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.*;
import org.kuali.rice.krad.document.authorization.PessimisticLock;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.util.NoteType;
import org.kuali.rice.krad.util.documentserializer.PropertySerializabilityEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by kkronenb on 5/8/15.
 */
public class FakeDistributionOfIncomeAndExpenseDocument implements FinancialSystemTransactionalDocument, AccountingDocument {

    protected List<SourceAccountingLine> sourceAccountingLines;
    protected List<TargetAccountingLine> targetAccountingLines;

    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return null;
    }

    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return null;
    }

    @Override
    public KualiDecimal getTargetTotal() {
        return null;
    }

    @Override
    public KualiDecimal getSourceTotal() {
        return null;
    }

    @Override
    public AccountingLineParser getAccountingLineParser() {
        return null;
    }

    @Override
    public Class getSourceAccountingLineClass() {
        return null;
    }

    @Override
    public Class getTargetAccountingLineClass() {
        return null;
    }

    @Override
    public String getSourceAccountingLineEntryName() {
        return null;
    }

    @Override
    public String getTargetAccountingLineEntryName() {
        return null;
    }

    @Override
    public Integer getNextSourceLineNumber() {
        return null;
    }

    @Override
    public void setNextSourceLineNumber(Integer nextLineNumber) {

    }

    @Override
    public Integer getNextTargetLineNumber() {
        return null;
    }

    @Override
    public void setNextTargetLineNumber(Integer nextLineNumber) {

    }

    @Override
    public void addSourceAccountingLine(SourceAccountingLine line) {
        if (sourceAccountingLines == null)
            sourceAccountingLines = new ArrayList();
        sourceAccountingLines.add(line);
    }

    @Override
    public List getSourceAccountingLines() {
        return sourceAccountingLines;
    }

    @Override
    public SourceAccountingLine getSourceAccountingLine(int index) {
        return sourceAccountingLines.get(index);
    }

    @Override
    public void setSourceAccountingLines(List sourceLines) {
        sourceAccountingLines = sourceLines;

    }

    @Override
    public void addTargetAccountingLine(TargetAccountingLine line) {
        if (targetAccountingLines == null) {
            targetAccountingLines = new ArrayList();
        }
        targetAccountingLines.add(line);
    }

    @Override
    public List getTargetAccountingLines() {
        return targetAccountingLines;
    }

    @Override
    public TargetAccountingLine getTargetAccountingLine(int index) {
        return targetAccountingLines.get(index);
    }

    @Override
    public void setTargetAccountingLines(List targetLines) {
        targetAccountingLines = targetLines;
    }

    @Override
    public Class<? extends AccountingDocument> getDocumentClassForAccountingLineValueAllowedValidation() {
        return null;
    }

    @Override
    public boolean isDocumentFinalOrProcessed() {
        return false;
    }

    @Override
    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return new ArrayList<>();
    }

    @Override
    public GeneralLedgerPendingEntry getGeneralLedgerPendingEntry(int index) {
        return null;
    }

    @Override
    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> generalLedgerPendingEntries) {

    }

    @Override
    public List<SufficientFundsItem> checkSufficientFunds() {
        return null;
    }

    @Override
    public List<GeneralLedgerPendingEntry> getPendingLedgerEntriesForSufficientFundsChecking() {
        return null;
    }

    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return false;
    }

    @Override
    public Integer getPostingYear() {
        return null;
    }

    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return false;
    }

    @Override
    public void setPostingYear(Integer postingYear) {

    }

    @Override
    public String getPostingPeriodCode() {
        return null;
    }

    @Override
    public void setPostingPeriodCode(String postingPeriodCode) {

    }

    @Override
    public AccountingPeriod getAccountingPeriod() {
        return null;
    }

    @Override
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {

    }

    @Override
    public String getApplicationDocumentStatus() {
        return null;
    }

    @Override
    public void setApplicationDocumentStatus(String applicationDocumentStatus) {

    }

    @Override
    public void updateAndSaveAppDocStatus(String applicationDocumentStatus) throws WorkflowException {

    }

    @Override
    public FinancialSystemDocumentHeader getFinancialSystemDocumentHeader() {
        return null;
    }

    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        return false;
    }

    @Override
    public DocumentHeader getDocumentHeader() {
        return null;
    }

    @Override
    public void clearAnyGeneralLedgerPendingEntries() {

    }

    @Override
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        return null;
    }

    @Override
    public void addPendingEntry(GeneralLedgerPendingEntry entry) {

    }

    @Override
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail) {
        return null;
    }

    @Override
    public String getFinancialDocumentTypeCode() {
        return null;
    }

    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return false;
    }

    @Override
    public void setDocumentHeader(DocumentHeader documentHeader) {

    }

    @Override
    public String getDocumentNumber() {
        return null;
    }

    @Override
    public void setDocumentNumber(String documentHeaderId) {

    }

    @Override
    public void populateDocumentForRouting() {

    }

    @Override
    public String serializeDocumentToXml() {
        return null;
    }

    @Override
    public String getXmlForRouteReport() {
        return null;
    }

    @Override
    public void doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) {

    }

    @Override
    public void doActionTaken(ActionTakenEvent event) {

    }

    @Override
    public void afterActionTaken(ActionType performed, ActionTakenEvent event) {

    }

    @Override
    public void afterWorkflowEngineProcess(boolean successfullyProcessed) {

    }

    @Override
    public void beforeWorkflowEngineProcess() {

    }

    @Override
    public List<String> getWorkflowEngineDocumentIdsToLock() {
        return null;
    }

    @Override
    public String getDocumentTitle() {
        return null;
    }

    @Override
    public List<AdHocRoutePerson> getAdHocRoutePersons() {
        return null;
    }

    @Override
    public List<AdHocRouteWorkgroup> getAdHocRouteWorkgroups() {
        return null;
    }

    @Override
    public void setAdHocRoutePersons(List<AdHocRoutePerson> adHocRoutePersons) {

    }

    @Override
    public void setAdHocRouteWorkgroups(List<AdHocRouteWorkgroup> adHocRouteWorkgroups) {

    }

    @Override
    public void prepareForSave() {

    }

    @Override
    public void validateBusinessRules(KualiDocumentEvent event) {

    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {

    }

    @Override
    public void postProcessSave(KualiDocumentEvent event) {

    }

    @Override
    public void processAfterRetrieve() {

    }

    @Override
    public boolean getAllowsCopy() {
        return false;
    }

    @Override
    public List<KualiDocumentEvent> generateSaveEvents() {
        return null;
    }

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {

    }

    @Override
    public NoteType getNoteType() {
        return null;
    }

    @Override
    public PersistableBusinessObject getNoteTarget() {
        return null;
    }

    @Override
    public void addNote(Note note) {

    }

    @Override
    public List<Note> getNotes() {
        return null;
    }

    @Override
    public void setNotes(List<Note> notes) {

    }

    @Override
    public Note getNote(int index) {
        return null;
    }

    @Override
    public boolean removeNote(Note note) {
        return false;
    }

    @Override
    public List<PessimisticLock> getPessimisticLocks() {
        return null;
    }

    @Override
    public void refreshPessimisticLocks() {

    }

    @Override
    public void addPessimisticLock(PessimisticLock lock) {

    }

    @Override
    public List<String> getLockClearningMethodNames() {
        return null;
    }

    @Override
    public String getBasePathToDocumentDuringSerialization() {
        return null;
    }

    @Override
    public PropertySerializabilityEvaluator getDocumentPropertySerizabilityEvaluator() {
        return null;
    }

    @Override
    public Object wrapDocumentWithMetadataForXmlSerialization() {
        return null;
    }

    @Override
    public boolean useCustomLockDescriptors() {
        return false;
    }

    @Override
    public String getCustomLockDescriptor(Person user) {
        return null;
    }

    @Override
    public void setVersionNumber(Long aLong) {

    }

    @Override
    public void setObjectId(String s) {

    }

    @Override
    public PersistableBusinessObjectExtension getExtension() {
        return null;
    }

    @Override
    public void setExtension(PersistableBusinessObjectExtension persistableBusinessObjectExtension) {

    }

    @Override
    public void refreshNonUpdateableReferences() {

    }

    @Override
    public void refreshReferenceObject(String s) {

    }

    @Override
    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        return null;
    }

    @Override
    public boolean isNewCollectionRecord() {
        return false;
    }

    @Override
    public void setNewCollectionRecord(boolean b) {

    }

    @Override
    public void linkEditableUserFields() {

    }

    @Override
    public void refresh() {

    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public Long getVersionNumber() {
        return null;
    }
}
