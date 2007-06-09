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
package org.kuali.module.gl.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.core.dao.DocumentDao;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.web.comparator.NumericValueComparator;
import org.kuali.core.web.comparator.StringValueComparator;
import org.kuali.core.web.comparator.TemporalValueComparator;
import org.kuali.core.web.ui.Column;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.CorrectionChangeDao;
import org.kuali.module.gl.dao.CorrectionChangeGroupDao;
import org.kuali.module.gl.dao.CorrectionCriteriaDao;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.exception.LoadException;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.CorrectionDocumentEntryMetadata;
import org.kuali.module.gl.util.CorrectionDocumentUtils;
import org.kuali.module.gl.util.OriginEntryFileIterator;
import org.kuali.module.gl.util.OriginEntryStatistics;
import org.kuali.module.gl.web.struts.form.CorrectionForm;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class CorrectionDocumentServiceImpl implements CorrectionDocumentService {
    private static Logger LOG = Logger.getLogger(CorrectionDocumentServiceImpl.class);
    
    private CorrectionChangeGroupDao correctionChangeGroupDao;
    private CorrectionChangeDao correctionChangeDao;
    private CorrectionCriteriaDao correctionCriteriaDao;
    private DocumentDao documentDao;
    private KualiConfigurationService kualiConfigurationService;
    private OriginEntryService originEntryService;
    private String glcpDirectoryName;
    private OriginEntryGroupService originEntryGroupService;
    
    protected static final String INPUT_ORIGIN_ENTRIES_FILE_SUFFIX = "-input.txt";
    protected static final String OUTPUT_ORIGIN_ENTRIES_FILE_SUFFIX = "-output.txt";

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentNumberAndCorrectionChangeGroupNumber(java.lang.String, int)
     */
    public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String docId, int i) {

        return correctionChangeGroupDao.findByDocumentNumberAndCorrectionChangeGroupNumber(docId, i);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentHeaderIdAndCorrectionGroupNumber(java.lang.String, int)
     */
    public List findByDocumentHeaderIdAndCorrectionGroupNumber(String docId, int i) {

        return correctionChangeDao.findByDocumentHeaderIdAndCorrectionGroupNumber(docId, i);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentNumberAndCorrectionGroupNumber(java.lang.String, int)
     */
    public List findByDocumentNumberAndCorrectionGroupNumber(String docId, int i) {

        return correctionCriteriaDao.findByDocumentNumberAndCorrectionGroupNumber(docId, i);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByCorrectionDocumentHeaderId(java.lang.String)
     */
    public CorrectionDocument findByCorrectionDocumentHeaderId(String docId) {

        return (CorrectionDocument) documentDao.findByDocumentHeaderId(CorrectionDocument.class, docId);
    }

    public void setCorrectionChangeDao(CorrectionChangeDao correctionChangeDao) {
        this.correctionChangeDao = correctionChangeDao;
    }

    public void setCorrectionChangeGroupDao(CorrectionChangeGroupDao correctionChangeGroupDao) {
        this.correctionChangeGroupDao = correctionChangeGroupDao;
    }

    public void setCorrectionCriteriaDao(CorrectionCriteriaDao correctionCriteriaDao) {
        this.correctionCriteriaDao = correctionCriteriaDao;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    private List<Column> cachedColumns = null;
    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#getTableRenderColumnMetadata(java.lang.String)
     */
    public List<Column> getTableRenderColumnMetadata(String docId) {
        synchronized (this) {
            if (cachedColumns == null) {
                cachedColumns = new ArrayList<Column>();
                Column columnToAdd;
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Fiscal Year");
                columnToAdd.setPropertyName(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Chart Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Account Number");
                columnToAdd.setPropertyName(KFSPropertyConstants.ACCOUNT_NUMBER);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sub Account Number");
                columnToAdd.setPropertyName(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Object Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sub Object Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Balance Type");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Object Type");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Fiscal Period");
                columnToAdd.setPropertyName(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Document Type");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Origin Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Document Number");
                columnToAdd.setPropertyName(KFSPropertyConstants.DOCUMENT_NUMBER);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sequence Number");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Description");
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Amount");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Debit Credit Indicator");
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Transaction Date");
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_DATE);
                columnToAdd.setValueComparator(TemporalValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Org Doc Number");
                columnToAdd.setPropertyName(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Project Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.PROJECT_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Org Ref ID");
                columnToAdd.setPropertyName(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Doc Type");
                columnToAdd.setPropertyName(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Origin Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Doc Number");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Reversal Date");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Enc Update Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
                
                cachedColumns = Collections.unmodifiableList(cachedColumns);
            }
        }
        return cachedColumns;
    }

    protected String generateInputOriginEntryFileName(CorrectionDocument document) {
        String docId = document.getDocumentHeader().getDocumentNumber();
        return generateInputOriginEntryFileName(docId);
    }
    
    protected String generateOutputOriginEntryFileName(CorrectionDocument document) {
        String docId = document.getDocumentHeader().getDocumentNumber();
        return generateOutputOriginEntryFileName(docId);
    }
    
    protected String generateInputOriginEntryFileName(String docId) {
        return getOriginEntryStagingDirectoryPath() + File.separator + docId + INPUT_ORIGIN_ENTRIES_FILE_SUFFIX;
    }
    
    protected String generateOutputOriginEntryFileName(String docId) {
        return getOriginEntryStagingDirectoryPath() + File.separator + docId + OUTPUT_ORIGIN_ENTRIES_FILE_SUFFIX;
    }
    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#persistOriginEntriesToFile(java.lang.String, java.util.Iterator)
     */
    public void persistInputOriginEntriesForInitiatedOrSavedDocument(CorrectionDocument document, Iterator<OriginEntry> entries) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.stateIsInitiated() && !workflowDocument.stateIsSaved()) {
            LOG.error("This method may only be called when the document is in the initiated or saved state.");
        }
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        persistOriginEntries(fullPathUniqueFileName, entries);
    }

    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#persistOutputOriginEntriesForInitiatedOrSavedDocument(org.kuali.module.gl.document.CorrectionDocument, java.util.Iterator)
     */
    public void persistOutputOriginEntriesForInitiatedOrSavedDocument(CorrectionDocument document, Iterator<OriginEntry> entries) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.stateIsInitiated() && !workflowDocument.stateIsSaved()) {
            LOG.error("This method may only be called when the document is in the initiated or saved state.");
        }
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        persistOriginEntries(fullPathUniqueFileName, entries);
    }

    protected void persistOriginEntries(String fullPathUniqueFileName, Iterator<OriginEntry> entries) {
        File fileOut = new File(fullPathUniqueFileName);
        FileOutputStream streamOut = null;
        BufferedOutputStream bufferedStreamOut = null;
        try {
            streamOut = new FileOutputStream(fileOut);
            bufferedStreamOut = new BufferedOutputStream(streamOut);
            
            byte[] newLine = "\n".getBytes();
            while (entries.hasNext()) {
                OriginEntry entry = entries.next();
                bufferedStreamOut.write(entry.getLine().getBytes());
                bufferedStreamOut.write(newLine);
            }
        }
        catch (IOException e) {
            LOG.error("unable to persist origin entries to file: " + fullPathUniqueFileName, e);
            throw new RuntimeException("unable to persist origin entries to file.");
        }
        finally {
            try {
                bufferedStreamOut.close();
                streamOut.close();
            }
            catch (IOException e) {
                LOG.error("unable to close output streams for file: " + fullPathUniqueFileName, e);
                throw new RuntimeException("unable to close output streams");
            }
        }
    }
    
    protected BufferedOutputStream openEntryOutputStreamForOutputGroup(CorrectionDocument document) throws IOException {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        return new BufferedOutputStream(new FileOutputStream(fullPathUniqueFileName));
    }
    
    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#removePersistedInputOriginEntriesForInitiatedOrSavedDocument(org.kuali.module.gl.document.CorrectionDocument)
     */
    public void removePersistedInputOriginEntries(CorrectionDocument document) {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        removePersistedOriginEntries(fullPathUniqueFileName);
    }

    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#removePersistedOutputOriginEntriesForInitiatedOrSavedDocument(org.kuali.module.gl.document.CorrectionDocument)
     */
    public void removePersistedOutputOriginEntries(CorrectionDocument document) {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        removePersistedOriginEntries(fullPathUniqueFileName);
    }
    
    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#removePersistedInputOriginEntries(java.lang.String)
     */
    public void removePersistedInputOriginEntries(String docId) {
        removePersistedOriginEntries(generateInputOriginEntryFileName(docId));
    }

    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#removePersistedOutputOriginEntries(java.lang.String)
     */
    public void removePersistedOutputOriginEntries(String docId) {
        removePersistedOriginEntries(generateOutputOriginEntryFileName(docId));
    }

    protected void removePersistedOriginEntries(String fullPathUniqueFileName) {
        File fileOut = new File(fullPathUniqueFileName);
        if (fileOut.exists() && fileOut.isFile()) {
            fileOut.delete();
        }
    }

    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#retrievePersistedInputOriginEntries(org.kuali.module.gl.document.CorrectionDocument, int)
     */
    public List<OriginEntry> retrievePersistedInputOriginEntries(CorrectionDocument document, int abortThreshold) {
        return retrievePersistedOriginEntries(generateInputOriginEntryFileName(document), abortThreshold);
    }

    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#retrievePersistedOutputOriginEntries(org.kuali.module.gl.document.CorrectionDocument, int)
     */
    public List<OriginEntry> retrievePersistedOutputOriginEntries(CorrectionDocument document, int abortThreshold) {
        return retrievePersistedOriginEntries(generateOutputOriginEntryFileName(document), abortThreshold);
    }
    
    protected List<OriginEntry> retrievePersistedOriginEntries(String fullPathUniqueFileName, int abortThreshold) {
        File fileIn = new File(fullPathUniqueFileName);
        if (!fileIn.exists()) {
            LOG.error("File " + fullPathUniqueFileName + " does not exist.");
            throw new RuntimeException("File does not exist");
        }
        BufferedReader reader = null;
        FileReader fReader = null;
        
        List<OriginEntry> entries = new ArrayList<OriginEntry>();
        int lineNumber = 0;
        try {
            fReader = new FileReader(fileIn);
            reader = new BufferedReader(fReader);
            String line;
            while ((line = reader.readLine()) != null) {
                OriginEntry entry = new OriginEntry();
                entry.setFromTextFile(line, lineNumber);
                if (abortThreshold != UNLIMITED_ABORT_THRESHOLD && lineNumber >= abortThreshold) {
                    return null;
                }
                lineNumber++;
                entries.add(entry);
            }
        }
        catch (IOException e) {
            LOG.error("retrievePersistedOriginEntries() Error reading file " + fileIn.getAbsolutePath(), e);
            throw new RuntimeException("Error reading file");
        }
        finally {
            try {
                if (fReader != null) {
                    fReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException e) {
                LOG.error("Unable to close file " + fileIn.getAbsolutePath(), e);
                throw new RuntimeException("Error closing file");
            }
        }
        return entries;
    }
    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#retrievePersistedInputOriginEntriesAsIterator(org.kuali.module.gl.document.CorrectionDocument)
     */
    public Iterator<OriginEntry> retrievePersistedInputOriginEntriesAsIterator(CorrectionDocument document) {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        return retrievePersistedOriginEntriesAsIterator(fullPathUniqueFileName);
    }
    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#retrievePersistedOutputOriginEntriesAsIterator(org.kuali.module.gl.document.CorrectionDocument)
     */
    public Iterator<OriginEntry> retrievePersistedOutputOriginEntriesAsIterator(CorrectionDocument document) {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        return retrievePersistedOriginEntriesAsIterator(fullPathUniqueFileName);
    }
    
    protected Iterator<OriginEntry> retrievePersistedOriginEntriesAsIterator(String fullPathUniqueFileName) {
        File fileIn = new File(fullPathUniqueFileName);
        if (!fileIn.exists()) {
            LOG.error("File " + fullPathUniqueFileName + " does not exist.");
            throw new RuntimeException("File does not exist");
        }
        BufferedReader reader = null;
        FileReader fReader = null;
        
        try {
            fReader = new FileReader(fileIn);
            reader = new BufferedReader(fReader);
            
            return new OriginEntryFileIterator(reader);
        }
        catch (IOException e) {
            LOG.error("retrievePersistedOriginEntries() Error opening file " + fileIn.getAbsolutePath(), e);
            throw new RuntimeException("Error opening file");
        }
        // don't close the reader, the iterator will take care of that
    }

    /**
     * Returns true if and only if the file corresponding to this document's input origin entries are on the file system.
     * @see org.kuali.module.gl.service.CorrectionDocumentService#areInputOriginEntriesPersisted(org.kuali.module.gl.document.CorrectionDocument)
     */
    public boolean areInputOriginEntriesPersisted(CorrectionDocument document) {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        File file = new File(fullPathUniqueFileName);
        return file.exists();
    }

    /**
     * Returns true if and only if the file corresponding to this document's output origin entries are on the file system.
     * @see org.kuali.module.gl.service.CorrectionDocumentService#areOutputOriginEntriesPersisted(org.kuali.module.gl.document.CorrectionDocument)
     */
    public boolean areOutputOriginEntriesPersisted(CorrectionDocument document) {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        File file = new File(fullPathUniqueFileName);
        return file.exists();
    }

    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#writePersistedInputOriginEntriesToStream(java.io.OutputStream)
     */
    public void writePersistedInputOriginEntriesToStream(CorrectionDocument document, OutputStream out) throws IOException {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        writePersistedOriginEntriesToStream(fullPathUniqueFileName, out);
    }

    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#writePersistedOutputOriginEntriesToStream(java.io.OutputStream)
     */
    public void writePersistedOutputOriginEntriesToStream(CorrectionDocument document, OutputStream out) throws IOException {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        writePersistedOriginEntriesToStream(fullPathUniqueFileName, out);
    }
    
    protected void writePersistedOriginEntriesToStream(String fullPathUniqueFileName, OutputStream out) throws IOException {
        FileInputStream fileIn = new FileInputStream(fullPathUniqueFileName);
        
        try {
            byte[] buf = new byte[1000];
            int bytesRead;
            
            while ((bytesRead = fileIn.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
        }
        finally {
            fileIn.close();
        }
    }

    public void persistOriginEntryGroupsForDocumentSave(CorrectionDocument document, CorrectionDocumentEntryMetadata correctionDocumentEntryMetadata) {
        if (correctionDocumentEntryMetadata.getAllEntries() == null && !correctionDocumentEntryMetadata.isRestrictedFunctionalityMode()) {
            // if we don't have origin entries loaded and not in restricted functionality mode, then there's nothing worth persisting
            removePersistedInputOriginEntries(document);
            removePersistedOutputOriginEntries(document);
            return;
        }
        
        if (!correctionDocumentEntryMetadata.getDataLoadedFlag() && !correctionDocumentEntryMetadata.isRestrictedFunctionalityMode()) {
            // data is not loaded (maybe user selected a new group with no rows)
            // clear out existing data
            removePersistedInputOriginEntries(document);
            removePersistedOutputOriginEntries(document);
            return;
        }
        
        // reload the group from the origin entry service
        Iterator<OriginEntry> inputGroupEntries;
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if ((workflowDocument.stateIsSaved() && !(correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad() != null && correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad().equals(document.getCorrectionInputGroupId()))) 
                || workflowDocument.stateIsInitiated()) {
            // we haven't saved the origin entry group yet, so let's load the entries from the DB and persist them for the document
            // this could be because we've previously saved the doc, but now we are now using a new input group, so we have to repersist the input group
            OriginEntryGroup group = originEntryGroupService.getExactMatchingEntryGroup(document.getCorrectionInputGroupId());
            inputGroupEntries = originEntryService.getEntriesByGroup(group);
            persistInputOriginEntriesForInitiatedOrSavedDocument(document, inputGroupEntries);
            
            // we've exhausted the iterator for the origin entries group
            // reload the iterator from the file
            inputGroupEntries = retrievePersistedInputOriginEntriesAsIterator(document);
        }
        else if (workflowDocument.stateIsSaved() && correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad().equals(document.getCorrectionInputGroupId())) {
            // we've saved the origin entries before, so just retrieve them
            inputGroupEntries = retrievePersistedInputOriginEntriesAsIterator(document);
        }
        else {
            LOG.error("Unexpected state while trying to persist/retrieve GLCP origin entries during document save: document status is " 
                    + workflowDocument.getStatusDisplayValue() + " selected input group: " + document.getCorrectionInputGroupId()
                    + " last saved input group: " + correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad());
            throw new RuntimeException("Error persisting GLCP document origin entries.");
        }
        
        OriginEntryStatistics statistics;
        if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionDocumentEntryMetadata.getEditMethod())) {
            // persist the allEntries element as the output group, since it has all of the modifications made by during the manual edits 
            persistOutputOriginEntriesForInitiatedOrSavedDocument(document, correctionDocumentEntryMetadata.getAllEntries().iterator());
            
            // even though the struts action handler may have computed the doc totals, let's recompute them
            statistics = CorrectionDocumentUtils.getStatistics(correctionDocumentEntryMetadata.getAllEntries());
        }
        else if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionDocumentEntryMetadata.getEditMethod())) {
            // we want to persist the values of the output group.  So reapply all of the criteria on each entry, one at a time
            
            BufferedOutputStream bufferedOutputStream = null;
            try {
                bufferedOutputStream = openEntryOutputStreamForOutputGroup(document);
                statistics = new OriginEntryStatistics();
                byte[] newLine = "\n".getBytes();
                
                while (inputGroupEntries.hasNext()) {
                    OriginEntry entry = inputGroupEntries.next();
                    
                    entry = CorrectionDocumentUtils.applyCriteriaToEntry(entry, correctionDocumentEntryMetadata.getMatchCriteriaOnly(), document.getCorrectionChangeGroup());
                    if (entry != null) {
                        CorrectionDocumentUtils.updateStatisticsWithEntry(entry, statistics);
                        bufferedOutputStream.write(entry.getLine().getBytes());
                        bufferedOutputStream.write(newLine);
                    }
                    // else it was null, which means that the match criteria only flag was set, and the entry didn't match the criteria
                }
            }
            catch (IOException e) {
                LOG.error("Unable to persist persisted output entry", e);
                throw new RuntimeException("Unable to persist output entry");
            }
            finally {
                if (bufferedOutputStream != null) {
                    try {
                        bufferedOutputStream.close();
                    }
                    catch (IOException e) {
                        LOG.error("Unable to close output stream for persisted output entries", e);
                        throw new RuntimeException("Unable to close output entry file");
                    }
                }
            }
        }
        else if (CorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(correctionDocumentEntryMetadata.getEditMethod())) {
            // just wipe out the previous output entries
            removePersistedOutputOriginEntries(document);
            statistics = new OriginEntryStatistics();
        }
        else {
            throw new RuntimeException("Unrecognized edit method: " + correctionDocumentEntryMetadata.getEditMethod());
        }
        
        CorrectionDocumentUtils.copyStatisticsToDocument(statistics, document);
    }
    
    protected String getOriginEntryStagingDirectoryPath() {
        return getGlcpDirectoryName();
    }
    /**
     * Gets the kualiConfigurationService attribute. 
     * @return Returns the kualiConfigurationService.
     */
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Gets the originEntryService attribute. 
     * @return Returns the originEntryService.
     */
    public OriginEntryService getOriginEntryService() {
        return originEntryService;
    }

    /**
     * Sets the originEntryService attribute value.
     * @param originEntryService The originEntryService to set.
     */
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    /**
     * Gets the glcpDirectoryName attribute. 
     * @return Returns the glcpDirectoryName.
     */
    public String getGlcpDirectoryName() {
        return glcpDirectoryName;
    }

    /**
     * Sets the glcpDirectoryName attribute value.
     * @param glcpDirectoryName The glcpDirectoryName to set.
     */
    public void setGlcpDirectoryName(String glcpDirectoryName) {
        this.glcpDirectoryName = glcpDirectoryName;
    }

    /**
     * Gets the originEntryGroupService attribute. 
     * @return Returns the originEntryGroupService.
     */
    public OriginEntryGroupService getOriginEntryGroupService() {
        return originEntryGroupService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }
    
    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#getTableRenderColumnMetadata(java.lang.String)
     */
    public List<Column> getLaborTableRenderColumnMetadata(String docId) {
        synchronized (this) {
            if (cachedColumns == null) {
                cachedColumns = new ArrayList<Column>();
                Column columnToAdd;
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Fiscal Year");
                columnToAdd.setPropertyName(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Chart Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Account Number");
                columnToAdd.setPropertyName(KFSPropertyConstants.ACCOUNT_NUMBER);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sub Account Number");
                columnToAdd.setPropertyName(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Object Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sub Object Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Balance Type");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Object Type");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Fiscal Period");
                columnToAdd.setPropertyName(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Document Type");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Origin Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Document Number");
                columnToAdd.setPropertyName(KFSPropertyConstants.DOCUMENT_NUMBER);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sequence Number");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Position Number");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.POSITION_NUMBER);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Project Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.PROJECT_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Description");
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Amount");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Debit Credit Indicator");
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Transaction Date");
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_DATE);
                columnToAdd.setValueComparator(TemporalValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Org Doc Number");
                columnToAdd.setPropertyName(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Org Ref ID");
                columnToAdd.setPropertyName(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Doc Type");
                columnToAdd.setPropertyName(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Origin Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Doc Number");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Reversal Date");
                columnToAdd.setPropertyName(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Enc Update Code");
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD);
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
              
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Transaction Posting Date");
                columnToAdd.setValueComparator(TemporalValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_POSTING_DATE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Pay Period End Date");
                columnToAdd.setValueComparator(TemporalValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.PAY_PERIOD_END_DATE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Trn Total Hours");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.TRANSACTION_TOTAL_HOURS);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Payroll EndDate Fiscal Year");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Payroll EndDate Fiscal Period Code");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Empl Id");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.EMPL_ID);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Empl Record");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.EMPLOYEE_RECORD);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Earn Code");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.EARN_CODE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Pay Group");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.PAY_GROUP);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Salary Admin Plan");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.SALARY_ADMINISTRATION_PLAN);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Grade");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.GRADE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Run Id");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.RUN_IDENTIFIER);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("LD Original Accounts Code");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE);
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("LD Original Account Number");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER);
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("LD Original Sub-Account Number");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER);
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("LD Original Object Code");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE);
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("LD Original Sub-Object Code");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE);
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Company");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.HRMS_COMPANY);
                cachedColumns.add(columnToAdd);
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("SetId");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.SET_ID);
                cachedColumns.add(columnToAdd);
                
                cachedColumns = Collections.unmodifiableList(cachedColumns);
            }
        }
        return cachedColumns;
    }
    
}
