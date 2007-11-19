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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.core.dao.DocumentDao;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.web.comparator.NumericValueComparator;
import org.kuali.core.web.comparator.StringValueComparator;
import org.kuali.core.web.comparator.TemporalValueComparator;
import org.kuali.core.web.ui.Column;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.CorrectionChangeDao;
import org.kuali.module.gl.dao.CorrectionChangeGroupDao;
import org.kuali.module.gl.dao.CorrectionCriteriaDao;
import org.kuali.module.gl.dao.CorrectionDocumentDao;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.CorrectionDocumentEntryMetadata;
import org.kuali.module.gl.util.CorrectionDocumentUtils;
import org.kuali.module.gl.util.OriginEntryFileIterator;
import org.kuali.module.gl.util.OriginEntryStatistics;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementaiton of CorrectionDocumentService
 */
@Transactional
public class CorrectionDocumentServiceImpl implements CorrectionDocumentService {
    protected static Logger LOG = Logger.getLogger(CorrectionDocumentServiceImpl.class);

    protected CorrectionChangeGroupDao correctionChangeGroupDao;
    protected CorrectionChangeDao correctionChangeDao;
    protected CorrectionCriteriaDao correctionCriteriaDao;
    protected DocumentDao documentDao;
    protected KualiConfigurationService kualiConfigurationService;
    private OriginEntryService originEntryService;
    private String glcpDirectoryName;
    protected OriginEntryGroupService originEntryGroupService;

    protected static final String INPUT_ORIGIN_ENTRIES_FILE_SUFFIX = "-input.txt";
    protected static final String OUTPUT_ORIGIN_ENTRIES_FILE_SUFFIX = "-output.txt";

    protected CorrectionDocumentDao correctionDocumentDao;

    /**
     * Returns a specific correction change group for a GLCP document.  Defers to DAO.
     * 
     * @param docId the document id of a GLCP document
     * @param i the number of the correction group within the document
     * @return a CorrectionChangeGroup
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentNumberAndCorrectionChangeGroupNumber(java.lang.String,
     *      int)
     */
    public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String docId, int i) {

        return correctionChangeGroupDao.findByDocumentNumberAndCorrectionChangeGroupNumber(docId, i);
    }

    /**
     * Finds CollectionChange records associated with a given document id and correction change group. Defers to DAO
     * 
     * @param docId the document id of a GLCP document
     * @param i the number of the correction group within the document
     * @return a List of qualifying CorrectionChange records
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentHeaderIdAndCorrectionGroupNumber(java.lang.String,
     *      int)
     */
    public List findByDocumentHeaderIdAndCorrectionGroupNumber(String docId, int i) {

        return correctionChangeDao.findByDocumentHeaderIdAndCorrectionGroupNumber(docId, i);
    }

    /**
     * Finds Collection Criteria associated with the given GLCP document and group. Defers to DAO.
     * 
     * @param docId the document id of a GLCP document
     * @param i the number of the correction group within the document
     * @return a List of qualifying CorrectionCriteria
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentNumberAndCorrectionGroupNumber(java.lang.String,
     *      int)
     */
    public List findByDocumentNumberAndCorrectionGroupNumber(String docId, int i) {

        return correctionCriteriaDao.findByDocumentNumberAndCorrectionGroupNumber(docId, i);
    }

    /**
     * Retrieves a correction document by the document id
     * 
     * @param docId the document id of the GLCP to find
     * @return a CorrectionDocument if found
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
     * Returns metadata to help render columns in the GLCP. Do not modify this list or the contents in this list.
     * 
     * @param docId the document id of a GLCP document
     * @return a List of Columns to render
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
                columnToAdd.setValueComparator(TemporalValueComparator.getInstance());
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

    /**
     * Generates the file name that input origin entries should be retrieved from
     * 
     * @param document a GLCP document
     * @return the name of the file to read
     */
    protected String generateInputOriginEntryFileName(CorrectionDocument document) {
        String docId = document.getDocumentHeader().getDocumentNumber();
        return generateInputOriginEntryFileName(docId);
    }

    /**
     * Generates the file name that output origin entries should be written to
     * 
     * @param document a GLCP document
     * @return the name of the file to write to
     */
    protected String generateOutputOriginEntryFileName(CorrectionDocument document) {
        String docId = document.getDocumentHeader().getDocumentNumber();
        return generateOutputOriginEntryFileName(docId);
    }

    /**
     * Generates the file name that input origin entries should be retrieved from
     * 
     * @param docId the document id of a GLCP document
     * @return the name of the file to read input origin entries in from
     */
    protected String generateInputOriginEntryFileName(String docId) {
        return getOriginEntryStagingDirectoryPath() + File.separator + docId + INPUT_ORIGIN_ENTRIES_FILE_SUFFIX;
    }

    /**
     * Generates the file name that output origin entries should be written to
     * 
     * @param docId the document id of a GLCP document
     * @return the name of the file to write output origin entries to
     */
    protected String generateOutputOriginEntryFileName(String docId) {
        return getOriginEntryStagingDirectoryPath() + File.separator + docId + OUTPUT_ORIGIN_ENTRIES_FILE_SUFFIX;
    }

    /**
     * This method persists an Iterator of input origin entries for a document that is in the initiated or saved state
     * 
     * @param document an initiated or saved document
     * @param entries an Iterator of origin entries
     * @see org.kuali.module.gl.service.CorrectionDocumentService#persistOriginEntriesToFile(java.lang.String, java.util.Iterator)
     */
    public void persistInputOriginEntriesForInitiatedOrSavedDocument(CorrectionDocument document, Iterator<OriginEntryFull> entries) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.stateIsInitiated() && !workflowDocument.stateIsSaved()) {
            LOG.error("This method may only be called when the document is in the initiated or saved state.");
        }
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        persistOriginEntries(fullPathUniqueFileName, entries);
    }


    /**
     * This method persists an Iterator of output origin entries for a document that is in the initiated or saved state
     * 
     * @param document an initiated or saved document
     * @param entries an Iterator of origin entries
     * @see org.kuali.module.gl.service.CorrectionDocumentService#persistOutputOriginEntriesForInitiatedOrSavedDocument(org.kuali.module.gl.document.CorrectionDocument,
     *      java.util.Iterator)
     */
    public void persistOutputOriginEntriesForInitiatedOrSavedDocument(CorrectionDocument document, Iterator<OriginEntryFull> entries) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.stateIsInitiated() && !workflowDocument.stateIsSaved()) {
            LOG.error("This method may only be called when the document is in the initiated or saved state.");
        }
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        persistOriginEntries(fullPathUniqueFileName, entries);
    }

    /**
     * Saves an interator of Origin Entry records to the given file name
     * 
     * @param fullPathUniqueFileName the name of the file to write entries to
     * @param entries entries to write
     */
    protected void persistOriginEntries(String fullPathUniqueFileName, Iterator<OriginEntryFull> entries) {
        File fileOut = new File(fullPathUniqueFileName);
        FileOutputStream streamOut = null;
        BufferedOutputStream bufferedStreamOut = null;
        try {
            streamOut = new FileOutputStream(fileOut);
            bufferedStreamOut = new BufferedOutputStream(streamOut);

            byte[] newLine = "\n".getBytes();
            while (entries.hasNext()) {
                OriginEntryFull entry = entries.next();
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

    /**
     * Opens an Output Stream to write Origin Entries to
     * 
     * @param document the GLCP document which has the origin entries to write
     * @return an OutputStream to write to
     * @throws IOException if the file cannot be successfully opened
     */
    protected BufferedOutputStream openEntryOutputStreamForOutputGroup(CorrectionDocument document) throws IOException {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        return new BufferedOutputStream(new FileOutputStream(fullPathUniqueFileName));
    }


    /**
     * Removes input origin entries that were saved to the database associated with the given document
     * 
     * @param document a GLCP document
     * @see org.kuali.module.gl.service.CorrectionDocumentService#removePersistedInputOriginEntriesForInitiatedOrSavedDocument(org.kuali.module.gl.document.CorrectionDocument)
     */
    public void removePersistedInputOriginEntries(CorrectionDocument document) {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        removePersistedOriginEntries(fullPathUniqueFileName);
    }

    /**
     * Removes all output origin entries persisted in the database created by the given document 
     * 
     * @param document a GLCP document
     * @see org.kuali.module.gl.service.CorrectionDocumentService#removePersistedOutputOriginEntriesForInitiatedOrSavedDocument(org.kuali.module.gl.document.CorrectionDocument)
     */
    public void removePersistedOutputOriginEntries(CorrectionDocument document) {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        removePersistedOriginEntries(fullPathUniqueFileName);
    }


    /**
     * Removes input origin entries that were saved to the database associated with the given document
     * 
     * @param docId the document id of a GLCP document
     * @see org.kuali.module.gl.service.CorrectionDocumentService#removePersistedInputOriginEntries(java.lang.String)
     */
    public void removePersistedInputOriginEntries(String docId) {
        removePersistedOriginEntries(generateInputOriginEntryFileName(docId));
    }

    /**
     * Removes all output origin entries persisted in the database created by the given document 
     *
     * @param docId the document id of a GLCP document
     * @see org.kuali.module.gl.service.CorrectionDocumentService#removePersistedOutputOriginEntries(java.lang.String)
     */
    public void removePersistedOutputOriginEntries(String docId) {
        removePersistedOriginEntries(generateOutputOriginEntryFileName(docId));
    }

    /**
     * Removes a file of origin entries.  Just deletes the whole thing!
     * 
     * @param fullPathUniqueFileName the file name of the file holding origin entries
     */
    protected void removePersistedOriginEntries(String fullPathUniqueFileName) {
        File fileOut = new File(fullPathUniqueFileName);
        if (fileOut.exists() && fileOut.isFile()) {
            fileOut.delete();
        }
    }

    /**
     * retrieves input origin entries that have been persisted for this document
     * 
     * @param document the document
     * @param abortThreshold if the file exceeds this number of rows, then null is returned. {@link UNLIMITED_ABORT_THRESHOLD}
     *        signifies that there is no limit
     * @return the list, or null if there are too many origin entries
     * @throws RuntimeException several reasons, primarily relating to underlying persistence layer problems
     * @see org.kuali.module.gl.service.CorrectionDocumentService#retrievePersistedInputOriginEntries(org.kuali.module.gl.document.CorrectionDocument,
     *      int)
     */
    public List<OriginEntryFull> retrievePersistedInputOriginEntries(CorrectionDocument document, int abortThreshold) {
        return retrievePersistedOriginEntries(generateInputOriginEntryFileName(document), abortThreshold);
    }

    /**
     * retrieves output origin entries that have been persisted for this document
     * 
     * @param document the document
     * @param abortThreshold if the file exceeds this number of rows, then null is returned. {@link UNLIMITED_ABORT_THRESHOLD}
     *        signifies that there is no limit
     * @return the list, or null if there are too many origin entries
     * @throws RuntimeException several reasons, primarily relating to underlying persistence layer problems
     * @see org.kuali.module.gl.service.CorrectionDocumentService#retrievePersistedOutputOriginEntries(org.kuali.module.gl.document.CorrectionDocument,
     *      int)
     */
    public List<OriginEntryFull> retrievePersistedOutputOriginEntries(CorrectionDocument document, int abortThreshold) {
        return retrievePersistedOriginEntries(generateOutputOriginEntryFileName(document), abortThreshold);
    }

    /**
     * Reads a file of origin entries and returns a List of those entry records
     * 
     * @param fullPathUniqueFileName the file name of the file to read
     * @param abortThreshold if more entries than this need to be read...well, they just won't get read
     * @return a List of OriginEntryFulls
     */
    protected List<OriginEntryFull> retrievePersistedOriginEntries(String fullPathUniqueFileName, int abortThreshold) {
        File fileIn = new File(fullPathUniqueFileName);
        if (!fileIn.exists()) {
            LOG.error("File " + fullPathUniqueFileName + " does not exist.");
            throw new RuntimeException("File does not exist");
        }
        BufferedReader reader = null;
        FileReader fReader = null;

        List<OriginEntryFull> entries = new ArrayList<OriginEntryFull>();
        int lineNumber = 0;
        try {
            fReader = new FileReader(fileIn);
            reader = new BufferedReader(fReader);
            String line;
            while ((line = reader.readLine()) != null) {
                OriginEntryFull entry = new OriginEntryFull();
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
     * Retrieves input origin entries that have been persisted for this document in an iterator. Implementations of this method may
     * choose to implement this method in a way that consumes very little memory.
     * 
     * @param document the document
     * @return the iterator
     * @see org.kuali.module.gl.service.CorrectionDocumentService#retrievePersistedInputOriginEntriesAsIterator(org.kuali.module.gl.document.CorrectionDocument)
     */
    public Iterator<OriginEntryFull> retrievePersistedInputOriginEntriesAsIterator(CorrectionDocument document) {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        return retrievePersistedOriginEntriesAsIterator(fullPathUniqueFileName);
    }

    /**
     * Retrieves output origin entries that have been persisted for this document in an iterator. Implementations of this method may
     * choose to implement this method in a way that consumes very little memory.
     * 
     * @param document the document
     * @return the iterator
     * @throws RuntimeException several reasons, primarily relating to underlying persistence layer problems
     * @see org.kuali.module.gl.service.CorrectionDocumentService#retrievePersistedOutputOriginEntriesAsIterator(org.kuali.module.gl.document.CorrectionDocument)
     */
    public Iterator<OriginEntryFull> retrievePersistedOutputOriginEntriesAsIterator(CorrectionDocument document) {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        return retrievePersistedOriginEntriesAsIterator(fullPathUniqueFileName);
    }

    /**
     * Reads origin entries from a file to an iterator
     * 
     * @param fullPathUniqueFileName the file name to read from
     * @return an Iterator of OriginEntries
     */
    protected Iterator<OriginEntryFull> retrievePersistedOriginEntriesAsIterator(String fullPathUniqueFileName) {
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
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#areInputOriginEntriesPersisted(org.kuali.module.gl.document.CorrectionDocument)
     */
    public boolean areInputOriginEntriesPersisted(CorrectionDocument document) {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        File file = new File(fullPathUniqueFileName);
        return file.exists();
    }

    /**
     * Returns true if and only if the file corresponding to this document's output origin entries are on the file system.
     * @param document a GLCP document to query
     * @return true if origin entries are stored to the system, false otherwise
     * @see org.kuali.module.gl.service.CorrectionDocumentService#areOutputOriginEntriesPersisted(org.kuali.module.gl.document.CorrectionDocument)
     */
    public boolean areOutputOriginEntriesPersisted(CorrectionDocument document) {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        File file = new File(fullPathUniqueFileName);
        return file.exists();
    }


    /**
     * Writes out the persisted input origin entries in an {@link OutputStream} in a flat file format\
     * 
     * @param document a GLCP document
     * @param out axn open and ready output stream
     * @throws IOException thrown if IOExceptions occurred in writing the persisted origin entries
     * @see org.kuali.module.gl.service.CorrectionDocumentService#writePersistedInputOriginEntriesToStream(java.io.OutputStream)
     */
    public void writePersistedInputOriginEntriesToStream(CorrectionDocument document, OutputStream out) throws IOException {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        writePersistedOriginEntriesToStream(fullPathUniqueFileName, out);
    }

    /**
     * Writes out the persisted output origin entries in an {@link OutputStream} in a flat file format\
     * 
     * @param document a GLCP document
     * @param out axn open and ready output stream
     * @throws IOException thrown if IOExceptions occurred in writing the persisted origin entries
     * @see org.kuali.module.gl.service.CorrectionDocumentService#writePersistedOutputOriginEntriesToStream(java.io.OutputStream)
     */
    public void writePersistedOutputOriginEntriesToStream(CorrectionDocument document, OutputStream out) throws IOException {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        writePersistedOriginEntriesToStream(fullPathUniqueFileName, out);
    }

    /**
     * Writes origin entries to an output stream
     * 
     * @param fullPathUniqueFileName the name of the file to write to
     * @param out an output stream to write to
     * @throws IOException thrown if problems occur during writing
     */
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

    /**
     * Saves the input and output origin entry groups for a document prior to saving the document
     * 
     * @param document a GLCP document
     * @param correctionDocumentEntryMetadata metadata about this GLCP document
     * @see org.kuali.module.gl.service.CorrectionDocumentService#persistOriginEntryGroupsForDocumentSave(org.kuali.module.gl.document.CorrectionDocument, org.kuali.module.gl.util.CorrectionDocumentEntryMetadata)
     */
    public void persistOriginEntryGroupsForDocumentSave(CorrectionDocument document, CorrectionDocumentEntryMetadata correctionDocumentEntryMetadata) {
        if (correctionDocumentEntryMetadata.getAllEntries() == null && !correctionDocumentEntryMetadata.isRestrictedFunctionalityMode()) {
            // if we don't have origin entries loaded and not in restricted functionality mode, then there's nothing worth
            // persisting
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
        Iterator<OriginEntryFull> inputGroupEntries;
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if ((workflowDocument.stateIsSaved() && !(correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad() != null && correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad().equals(document.getCorrectionInputGroupId()))) || workflowDocument.stateIsInitiated()) {
            // we haven't saved the origin entry group yet, so let's load the entries from the DB and persist them for the document
            // this could be because we've previously saved the doc, but now we are now using a new input group, so we have to
            // repersist the input group
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
            LOG.error("Unexpected state while trying to persist/retrieve GLCP origin entries during document save: document status is " + workflowDocument.getStatusDisplayValue() + " selected input group: " + document.getCorrectionInputGroupId() + " last saved input group: " + correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad());
            throw new RuntimeException("Error persisting GLCP document origin entries.");
        }

        OriginEntryStatistics statistics;
        if (CorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionDocumentEntryMetadata.getEditMethod())) {
            // persist the allEntries element as the output group, since it has all of the modifications made by during the manual
            // edits
            persistOutputOriginEntriesForInitiatedOrSavedDocument(document, correctionDocumentEntryMetadata.getAllEntries().iterator());

            // even though the struts action handler may have computed the doc totals, let's recompute them
            statistics = CorrectionDocumentUtils.getStatistics(correctionDocumentEntryMetadata.getAllEntries());
        }
        else if (CorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionDocumentEntryMetadata.getEditMethod())) {
            // we want to persist the values of the output group. So reapply all of the criteria on each entry, one at a time

            BufferedOutputStream bufferedOutputStream = null;
            try {
                bufferedOutputStream = openEntryOutputStreamForOutputGroup(document);
                statistics = new OriginEntryStatistics();
                byte[] newLine = "\n".getBytes();

                while (inputGroupEntries.hasNext()) {
                    OriginEntryFull entry = inputGroupEntries.next();

                    entry = CorrectionDocumentUtils.applyCriteriaToEntry(entry, correctionDocumentEntryMetadata.getMatchCriteriaOnly(), document.getCorrectionChangeGroup());
                    if (entry != null) {
                        CorrectionDocumentUtils.updateStatisticsWithEntry(entry, statistics);
                        bufferedOutputStream.write(entry.getLine().getBytes());
                        bufferedOutputStream.write(newLine);
                    }
                    // else it was null, which means that the match criteria only flag was set, and the entry didn't match the
                    // criteria
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

    /**
     * Gets the name of the directory to save all these temporary files in
     * 
     * @return the name of a directory path
     */
    protected String getOriginEntryStagingDirectoryPath() {
        return getGlcpDirectoryName();
    }

    /**
     * Gets the kualiConfigurationService attribute.
     * 
     * @return Returns the kualiConfigurationService.
     */
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Gets the originEntryService attribute.
     * 
     * @return Returns the originEntryService.
     */
    public OriginEntryService getOriginEntryService() {
        return originEntryService;
    }

    /**
     * Sets the originEntryService attribute value.
     * 
     * @param originEntryService The originEntryService to set.
     */
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    /**
     * Gets the glcpDirectoryName attribute.
     * 
     * @return Returns the glcpDirectoryName.
     */
    public String getGlcpDirectoryName() {
        return glcpDirectoryName;
    }

    /**
     * Sets the glcpDirectoryName attribute value.
     * 
     * @param glcpDirectoryName The glcpDirectoryName to set.
     */
    public void setGlcpDirectoryName(String glcpDirectoryName) {
        this.glcpDirectoryName = glcpDirectoryName;
    }

    /**
     * Gets the originEntryGroupService attribute.
     * 
     * @return Returns the originEntryGroupService.
     */
    public OriginEntryGroupService getOriginEntryGroupService() {
        return originEntryGroupService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#getCorrectionDocumentsFinalizedOn(java.sql.Date)
     */
    public Collection<CorrectionDocument> getCorrectionDocumentsFinalizedOn(Date date) {
        return correctionDocumentDao.getCorrectionDocumentsFinalizedOn(date);
    }

    public void setCorrectionDocumentDao(CorrectionDocumentDao correctionDocumentDao) {
        this.correctionDocumentDao = correctionDocumentDao;
    }
}
