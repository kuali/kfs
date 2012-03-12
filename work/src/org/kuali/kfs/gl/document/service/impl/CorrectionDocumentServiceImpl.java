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
package org.kuali.kfs.gl.document.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryFileIterator;
import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryStatistics;
import org.kuali.kfs.gl.dataaccess.CorrectionChangeDao;
import org.kuali.kfs.gl.dataaccess.CorrectionChangeGroupDao;
import org.kuali.kfs.gl.dataaccess.CorrectionCriteriaDao;
import org.kuali.kfs.gl.document.CorrectionDocumentUtils;
import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;
import org.kuali.kfs.gl.document.dataaccess.CorrectionDocumentDao;
import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.kfs.gl.document.web.CorrectionDocumentEntryMetadata;
import org.kuali.kfs.gl.report.CorrectionDocumentReport;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.InitiateDirectoryBase;
import org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService;
import org.kuali.kfs.sys.service.ReportAggregatorService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.krad.comparator.NumericValueComparator;
import org.kuali.rice.krad.comparator.StringValueComparator;
import org.kuali.rice.krad.comparator.TemporalValueComparator;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementaiton of CorrectionDocumentService
 */
@Transactional
public class CorrectionDocumentServiceImpl extends InitiateDirectoryBase implements CorrectionDocumentService {
    protected static Logger LOG = Logger.getLogger(CorrectionDocumentServiceImpl.class);

    protected CorrectionChangeGroupDao correctionChangeGroupDao;
    protected CorrectionChangeDao correctionChangeDao;
    protected CorrectionCriteriaDao correctionCriteriaDao;
    
    protected DocumentService documentService;
    protected ConfigurationService kualiConfigurationService;
    protected OriginEntryService originEntryService;
    protected String glcpDirectoryName;
    protected OriginEntryGroupService originEntryGroupService;
    protected DocumentNumberAwareReportWriterService glCorrectionDocumentReportWriterService;
    protected DateTimeService dateTimeService;
    protected ReportAggregatorService reportAggregatorService;
    
    protected String temporaryReportsDirectory;
    protected String temporaryReportFilenameComponent;
    protected String temporaryReportFilenameSuffix;
    protected String reportsDirectory;
    protected String reportFilenamePrefix;
    protected String reportFilenameSuffix;
    
    protected static final String INPUT_ORIGIN_ENTRIES_FILE_SUFFIX = "-input.txt";
    protected static final String OUTPUT_ORIGIN_ENTRIES_FILE_SUFFIX = "-output.txt";
    protected static final String GLCP_OUTPUT_PREFIX = "glcp_output";
    protected static final String CORRECTION_FILE_FILTER = "put.txt";

    protected CorrectionDocumentDao correctionDocumentDao;
    protected String batchFileDirectoryName;

    /**
     * Returns a specific correction change group for a GLCP document. Defers to DAO.
     * 
     * @param docId the document id of a GLCP document
     * @param i the number of the correction group within the document
     * @return a CorrectionChangeGroup
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#findByDocumentNumberAndCorrectionChangeGroupNumber(java.lang.String,
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
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#findByDocumentHeaderIdAndCorrectionGroupNumber(java.lang.String,
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
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#findByDocumentNumberAndCorrectionGroupNumber(java.lang.String,
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
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#findByCorrectionDocumentHeaderId(java.lang.String)
     */
    public GeneralLedgerCorrectionProcessDocument findByCorrectionDocumentHeaderId(String docId) {
        try {
            return (GeneralLedgerCorrectionProcessDocument) documentService.getByDocumentHeaderIdSessionless(docId);
        } catch (WorkflowException ex) {
            throw new RuntimeException( "Unable to retrieve document for GLCP process", ex );
        }
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

    private List<Column> cachedColumns = null;

    /**
     * Returns metadata to help render columns in the GLCP. Do not modify this list or the contents in this list.
     * 
     * @param docId the document id of a GLCP document
     * @return a List of Columns to render
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#getTableRenderColumnMetadata(java.lang.String)
     *
     * KRAD Conversion: Service method creates metadata of different columns.
     * No use of data dictionary.
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
    protected String generateInputOriginEntryFileName(GeneralLedgerCorrectionProcessDocument document) {
        String docId = document.getDocumentHeader().getDocumentNumber();
        return generateInputOriginEntryFileName(docId);
    }

    /**
     * Generates the file name that output origin entries should be written to
     * 
     * @param document a GLCP document
     * @return the name of the file to write to
     */
    protected String generateOutputOriginEntryFileName(GeneralLedgerCorrectionProcessDocument document) {
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
    public String generateOutputOriginEntryFileName(String docId) {
        return getOriginEntryStagingDirectoryPath() + File.separator + docId + OUTPUT_ORIGIN_ENTRIES_FILE_SUFFIX;
    }

    /**
     * This method persists an Iterator of input origin entries for a document that is in the initiated or saved state
     * 
     * @param document an initiated or saved document
     * @param entries an Iterator of origin entries
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#persistOriginEntriesToFile(java.lang.String,
     *      java.util.Iterator)
     */
    public void persistInputOriginEntriesForInitiatedOrSavedDocument(GeneralLedgerCorrectionProcessDocument document, Iterator<OriginEntryFull> entries) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.isInitiated() && !workflowDocument.isSaved()) {
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
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#persistOutputOriginEntriesForInitiatedOrSavedDocument(org.kuali.kfs.gl.document.CorrectionDocument,
     *      java.util.Iterator)
     */
    public void persistOutputOriginEntriesForInitiatedOrSavedDocument(GeneralLedgerCorrectionProcessDocument document, Iterator<OriginEntryFull> entries) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.isInitiated() && !workflowDocument.isSaved()) {
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
    protected BufferedOutputStream openEntryOutputStreamForOutputGroup(GeneralLedgerCorrectionProcessDocument document) throws IOException {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        return new BufferedOutputStream(new FileOutputStream(fullPathUniqueFileName));
    }


    /**
     * Removes input origin entries that were saved to the database associated with the given document
     * 
     * @param document a GLCP document
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#removePersistedInputOriginEntriesForInitiatedOrSavedDocument(org.kuali.kfs.gl.document.CorrectionDocument)
     */
    public void removePersistedInputOriginEntries(GeneralLedgerCorrectionProcessDocument document) {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        removePersistedOriginEntries(fullPathUniqueFileName);
    }

    /**
     * Removes all output origin entries persisted in the database created by the given document
     * 
     * @param document a GLCP document
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#removePersistedOutputOriginEntriesForInitiatedOrSavedDocument(org.kuali.kfs.gl.document.CorrectionDocument)
     */
    public void removePersistedOutputOriginEntries(GeneralLedgerCorrectionProcessDocument document) {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        removePersistedOriginEntries(fullPathUniqueFileName);
    }


    /**
     * Removes input origin entries that were saved to the database associated with the given document
     * 
     * @param docId the document id of a GLCP document
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#removePersistedInputOriginEntries(java.lang.String)
     */
    public void removePersistedInputOriginEntries(String docId) {
        removePersistedOriginEntries(generateInputOriginEntryFileName(docId));
    }

    /**
     * Removes all output origin entries persisted in the database created by the given document
     * 
     * @param docId the document id of a GLCP document
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#removePersistedOutputOriginEntries(java.lang.String)
     */
    public void removePersistedOutputOriginEntries(String docId) {
        removePersistedOriginEntries(generateOutputOriginEntryFileName(docId));
    }

    /**
     * Removes a file of origin entries. Just deletes the whole thing!
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
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#retrievePersistedInputOriginEntries(org.kuali.kfs.gl.document.CorrectionDocument,
     *      int)
     */
    public List<OriginEntryFull> retrievePersistedInputOriginEntries(GeneralLedgerCorrectionProcessDocument document, int abortThreshold) {
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
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#retrievePersistedOutputOriginEntries(org.kuali.kfs.gl.document.CorrectionDocument,
     *      int)
     */
    public List<OriginEntryFull> retrievePersistedOutputOriginEntries(GeneralLedgerCorrectionProcessDocument document, int abortThreshold) {
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
                entry.setFromTextFileForBatch(line, lineNumber);
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
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#retrievePersistedInputOriginEntriesAsIterator(org.kuali.kfs.gl.document.CorrectionDocument)
     */
    public Iterator<OriginEntryFull> retrievePersistedInputOriginEntriesAsIterator(GeneralLedgerCorrectionProcessDocument document) {
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
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#retrievePersistedOutputOriginEntriesAsIterator(org.kuali.kfs.gl.document.CorrectionDocument)
     */
    public Iterator<OriginEntryFull> retrievePersistedOutputOriginEntriesAsIterator(GeneralLedgerCorrectionProcessDocument document) {
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
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#areInputOriginEntriesPersisted(org.kuali.kfs.gl.document.CorrectionDocument)
     */
    public boolean areInputOriginEntriesPersisted(GeneralLedgerCorrectionProcessDocument document) {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        File file = new File(fullPathUniqueFileName);
        return file.exists();
    }

    /**
     * Returns true if and only if the file corresponding to this document's output origin entries are on the file system.
     * 
     * @param document a GLCP document to query
     * @return true if origin entries are stored to the system, false otherwise
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#areOutputOriginEntriesPersisted(org.kuali.kfs.gl.document.CorrectionDocument)
     */
    public boolean areOutputOriginEntriesPersisted(GeneralLedgerCorrectionProcessDocument document) {
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
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#writePersistedInputOriginEntriesToStream(java.io.OutputStream)
     */
    public void writePersistedInputOriginEntriesToStream(GeneralLedgerCorrectionProcessDocument document, OutputStream out) throws IOException {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        writePersistedOriginEntriesToStream(fullPathUniqueFileName, out);
    }

    /**
     * Writes out the persisted output origin entries in an {@link OutputStream} in a flat file format\
     * 
     * @param document a GLCP document
     * @param out axn open and ready output stream
     * @throws IOException thrown if IOExceptions occurred in writing the persisted origin entries
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#writePersistedOutputOriginEntriesToStream(java.io.OutputStream)
     */
    public void writePersistedOutputOriginEntriesToStream(GeneralLedgerCorrectionProcessDocument document, OutputStream out) throws IOException {
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

    public String createOutputFileForProcessing(String docId, java.util.Date today) {
        File outputFile = new File(glcpDirectoryName + File.separator + docId + OUTPUT_ORIGIN_ENTRIES_FILE_SUFFIX);
        String newFileName = batchFileDirectoryName + File.separator + GLCP_OUTPUT_PREFIX  + "." + docId + buildFileExtensionWithDate(today);
        File newFile = new File(newFileName);
        FileReader inputFileReader;
        FileWriter newFileWriter;

        try {
            // copy output file and put in OriginEntryInformation directory
            inputFileReader = new FileReader(outputFile);
            newFileWriter = new FileWriter(newFile);
            int c;
            while ((c = inputFileReader.read()) != -1) {
                newFileWriter.write(c);
            }

            inputFileReader.close();
            newFileWriter.close();

            // create done file, after successfully copying output file
            String doneFileName = newFileName.replace(GeneralLedgerConstants.BatchFileSystem.EXTENSION, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
            File doneFile = new File(doneFileName);
            if (!doneFile.exists()) {
                doneFile.createNewFile();
            }

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return newFileName;
    }

    protected String buildFileExtensionWithDate(java.util.Date date) {
        String formattedDateTime = dateTimeService.toDateTimeStringForFilename(date);
        return "." + formattedDateTime + GeneralLedgerConstants.BatchFileSystem.EXTENSION;


    }

    /**
     * Saves the input and output origin entry groups for a document prior to saving the document
     * 
     * @param document a GLCP document
     * @param correctionDocumentEntryMetadata metadata about this GLCP document
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#persistOriginEntryGroupsForDocumentSave(org.kuali.kfs.gl.document.CorrectionDocument,
     *      org.kuali.kfs.gl.document.web.CorrectionDocumentEntryMetadata)
     */
    public void persistOriginEntryGroupsForDocumentSave(GeneralLedgerCorrectionProcessDocument document, CorrectionDocumentEntryMetadata correctionDocumentEntryMetadata) {
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
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if ((workflowDocument.isSaved() && !(correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad() != null && correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad().equals(document.getCorrectionInputFileName()))) || workflowDocument.isInitiated()) {
            // we haven't saved the origin entry group yet, so let's load the entries from the DB and persist them for the document
            // this could be because we've previously saved the doc, but now we are now using a new input group, so we have to
            // repersist the input group


            // OriginEntryGroup group = originEntryGroupService.getExactMatchingEntryGroup(document.getCorrectionInputGroupId());
            
            File file = new File(document.getCorrectionInputFileName());
            
            inputGroupEntries = new OriginEntryFileIterator(file);
            persistInputOriginEntriesForInitiatedOrSavedDocument(document, inputGroupEntries);

            // we've exhausted the iterator for the origin entries group
            // reload the iterator from the file
            inputGroupEntries = retrievePersistedInputOriginEntriesAsIterator(document);
        }
        else if (workflowDocument.isSaved() && correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad().equals(document.getCorrectionInputFileName())) {
            // we've saved the origin entries before, so just retrieve them
            inputGroupEntries = retrievePersistedInputOriginEntriesAsIterator(document);
        }
        else {
            LOG.error("Unexpected state while trying to persist/retrieve GLCP origin entries during document save: document status is " + workflowDocument.getStatus() + " selected input group: " + document.getCorrectionInputFileName() + " last saved input group: " + correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad());
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
    
    protected class CorrectionFileFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            return name.contains(CORRECTION_FILE_FILTER);
        }
    }
    
    /**
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#generateCorrectionReport(org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument)
     */
    public void generateCorrectionReport(GeneralLedgerCorrectionProcessDocument document) {
        CorrectionDocumentReport correctionReport = new CorrectionDocumentReport();
        correctionReport.generateReport(glCorrectionDocumentReportWriterService, document);
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
    public ConfigurationService getConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
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
        
        FileUtil.createDirectory(glcpDirectoryName);
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
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#getCorrectionDocumentsFinalizedOn(java.sql.Date)
     */
    public Collection<GeneralLedgerCorrectionProcessDocument> getCorrectionDocumentsFinalizedOn(Date date) {
        return correctionDocumentDao.getCorrectionDocumentsFinalizedOn(date);
    }

    public void setCorrectionDocumentDao(CorrectionDocumentDao correctionDocumentDao) {
        this.correctionDocumentDao = correctionDocumentDao;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    public String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }

    /**
     * @return Returns the glCorrectionDocumentReportWriterService.
     */
    protected DocumentNumberAwareReportWriterService getGlCorrectionDocumentReportWriterService() {
        return glCorrectionDocumentReportWriterService;
    }

    /**
     * @param glCorrectionDocumentReportWriterService The glCorrectionDocumentReportWriterService to set.
     */
    public void setGlCorrectionDocumentReportWriterService(DocumentNumberAwareReportWriterService glCorrectionDocumentReportWriterService) {
        this.glCorrectionDocumentReportWriterService = glCorrectionDocumentReportWriterService;
    }

    /**
     * @see org.kuali.kfs.gl.document.service.CorrectionDocumentService#aggregateCorrectionDocumentReports()
     */
    public void aggregateCorrectionDocumentReports(GeneralLedgerCorrectionProcessDocument document) {
        File outputFile = getAggregatedReportFile(document.getDocumentNumber());
        List<File> inputFiles = getReportsToAggregateIntoReport(document.getDocumentNumber());
        reportAggregatorService.aggregateReports(outputFile, inputFiles);
    }
    
    protected File getAggregatedReportFile(String documentNumber) {
        String dateTimeStamp = dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());
        String outputFilename = reportsDirectory + File.separator + reportFilenamePrefix + documentNumber + "_" + dateTimeStamp + reportFilenameSuffix;
        return new File(outputFilename);
    }
    
    protected List<File> getReportsToAggregateIntoReport(String documentNumber) {
        File inputDirectory = new File(temporaryReportsDirectory);
        if (!inputDirectory.exists() || !inputDirectory.isDirectory()) {
            LOG.error(temporaryReportsDirectory + " does not exist or is not a directory.");
            throw new RuntimeException("Unable to locate temporary reports directory");
        }
        String filePrefix = documentNumber + "_" + temporaryReportFilenameComponent;
        FileFilter filter = FileFilterUtils.andFileFilter(
                new PrefixFileFilter(filePrefix), new SuffixFileFilter(temporaryReportFilenameSuffix));
        
        // FSKD-244, KFSMI-5424 sort with filename, just in case 
        List<File> fileList = Arrays.asList(inputDirectory.listFiles(filter));
        
        Comparator fileNameComparator = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                if (obj1 == null) {
                    return -1;
                }
                if (obj2 == null) {
                    return 1;
                }
                File file1 = (File) obj1;
                File file2 = (File) obj2;
                
                return ((Comparable) file1.getName()).compareTo(file2.getName());
            }
        };
        
        Collections.sort(fileList, fileNameComparator);
        return fileList ;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the reportAggregatorService attribute value.
     * @param reportAggregatorService The reportAggregatorService to set.
     */
    public void setReportAggregatorService(ReportAggregatorService reportAggregatorService) {
        this.reportAggregatorService = reportAggregatorService;
    }

    /**
     * Sets the temporaryReportsDirectory attribute value.
     * @param temporaryReportsDirectory The temporaryReportsDirectory to set.
     */
    public void setTemporaryReportsDirectory(String temporaryReportsDirectory) {
        this.temporaryReportsDirectory = temporaryReportsDirectory;
    }

    /**
     * Sets the temporaryReportFilenameComponent attribute value.
     * @param temporaryReportFilenameComponent The temporaryReportFilenameComponent to set.
     */
    public void setTemporaryReportFilenameComponent(String temporaryReportFilenameComponent) {
        this.temporaryReportFilenameComponent = temporaryReportFilenameComponent;
    }

    /**
     * Sets the temporaryReportFilenameSuffix attribute value.
     * @param temporaryReportFilenameSuffix The temporaryReportFilenameSuffix to set.
     */
    public void setTemporaryReportFilenameSuffix(String temporaryReportFilenameSuffix) {
        this.temporaryReportFilenameSuffix = temporaryReportFilenameSuffix;
    }

    /**
     * Sets the reportsDirectory attribute value.
     * @param reportsDirectory The reportsDirectory to set.
     */
    public void setReportsDirectory(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    /**
     * Sets the reportFilenamePrefix attribute value.
     * @param reportFilenamePrefix The reportFilenamePrefix to set.
     */
    public void setReportFilenamePrefix(String reportFilenamePrefix) {
        this.reportFilenamePrefix = reportFilenamePrefix;
    }

    /**
     * Sets the reportFilenameSuffix attribute value.
     * @param reportFilenameSuffix The reportFilenameSuffix to set.
     */
    public void setReportFilenameSuffix(String reportFilenameSuffix) {
        this.reportFilenameSuffix = reportFilenameSuffix;
    }

    protected static class GlcpFilenameFilter implements FilenameFilter {
        String documentNumber;
        public GlcpFilenameFilter( String documentNumber ) {
            this.documentNumber = documentNumber;
        }
        public boolean accept(File dir, String name) {
            return name.startsWith(GLCP_OUTPUT_PREFIX + "." + documentNumber);
        }
    }

    public String[] findExistingCorrectionOutputFilesForDocument( String documentNumber ) {
        return new File(batchFileDirectoryName).list( new GlcpFilenameFilter(documentNumber));
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.impl.InitiateDirectoryImpl#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return new ArrayList<String>() {{add(getOriginEntryStagingDirectoryPath()); }};
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
