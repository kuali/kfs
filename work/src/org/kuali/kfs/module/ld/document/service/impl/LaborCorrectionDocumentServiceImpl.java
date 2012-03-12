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
package org.kuali.kfs.module.ld.document.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.businessobject.OriginEntryStatistics;
import org.kuali.kfs.gl.dataaccess.CorrectionChangeDao;
import org.kuali.kfs.gl.dataaccess.CorrectionChangeGroupDao;
import org.kuali.kfs.gl.dataaccess.CorrectionCriteriaDao;
import org.kuali.kfs.gl.document.CorrectionDocumentUtils;
import org.kuali.kfs.gl.document.service.impl.CorrectionDocumentServiceImpl;
import org.kuali.kfs.gl.document.web.CorrectionDocumentEntryMetadata;
import org.kuali.kfs.gl.report.CorrectionDocumentReport;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.document.LaborCorrectionDocument;
import org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.module.ld.util.LaborOriginEntryFileIterator;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.krad.comparator.NumericValueComparator;
import org.kuali.rice.krad.comparator.StringValueComparator;
import org.kuali.rice.krad.comparator.TemporalValueComparator;
import org.kuali.rice.krad.dao.DocumentDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of LaborCorrectionDocumentService.
 */
@Transactional
public class LaborCorrectionDocumentServiceImpl extends CorrectionDocumentServiceImpl implements LaborCorrectionDocumentService {
    private static Logger LOG = Logger.getLogger(LaborCorrectionDocumentServiceImpl.class);

    protected OriginEntryGroupService originEntryGroupService;
    private LaborOriginEntryService laborOriginEntryService;
    private String llcpDirectoryName;
    private String batchFileDirectoryName;
    private DocumentDao documentDao;
    private DocumentNumberAwareReportWriterService laborCorrectionDocumentReportWriterService;

    protected static final String INPUT_ORIGIN_ENTRIES_FILE_SUFFIX = "-input.txt";
    protected static final String OUTPUT_ORIGIN_ENTRIES_FILE_SUFFIX = "-output.txt";
    protected static final String LLCP_OUTPUT_PREFIX = "llcp_output";

    public final static int UNLIMITED_ABORT_THRESHOLD = CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_UNLIMITED;

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#findByDocumentNumberAndCorrectionChangeGroupNumber(java.lang.String,
     *      int)
     */
    public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String docId, int i) {

        return correctionChangeGroupDao.findByDocumentNumberAndCorrectionChangeGroupNumber(docId, i);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#findByDocumentHeaderIdAndCorrectionGroupNumber(java.lang.String,
     *      int)
     */
    public List findByDocumentHeaderIdAndCorrectionGroupNumber(String docId, int i) {

        return correctionChangeDao.findByDocumentHeaderIdAndCorrectionGroupNumber(docId, i);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#findByDocumentNumberAndCorrectionGroupNumber(java.lang.String,
     *      int)
     */
    public List findByDocumentNumberAndCorrectionGroupNumber(String docId, int i) {

        return correctionCriteriaDao.findByDocumentNumberAndCorrectionGroupNumber(docId, i);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#findByCorrectionDocumentHeaderId(java.lang.String)
     */
    public LaborCorrectionDocument findByCorrectionDocumentHeaderId(String docId) {

        return (LaborCorrectionDocument) documentDao.findByDocumentHeaderId(LaborCorrectionDocument.class, docId);
    }

    /**
     * Sets the correctionChangeDao attribute value.
     * 
     * @param correctionChangeDao The correctionChangeDao to set.
     */

    public void setCorrectionChangeDao(CorrectionChangeDao correctionChangeDao) {
        this.correctionChangeDao = correctionChangeDao;
    }

    /**
     * Sets the correctionChangeDao attribute value.
     * 
     * @param correctionChangeGroupDao The correctionChangeDao to set.
     */
    public void setCorrectionChangeGroupDao(CorrectionChangeGroupDao correctionChangeGroupDao) {
        this.correctionChangeGroupDao = correctionChangeGroupDao;
    }

    /**
     * Sets the correctionCriteriaDao attribute value.
     * 
     * @param correctionCriteriaDao The correctionCriteriaDao to set.
     */
    public void setCorrectionCriteriaDao(CorrectionCriteriaDao correctionCriteriaDao) {
        this.correctionCriteriaDao = correctionCriteriaDao;
    }

    /**
     * Sets the documentDao attribute value.
     * 
     * @param documentDao The documentDao to set.
     */
    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    private List<Column> cachedColumns = null;

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#generateInputOriginEntryFileName(java.lang.String)
     */
    protected String generateInputOriginEntryFileName(LaborCorrectionDocument document) {
        String docId = document.getDocumentHeader().getDocumentNumber();
        return generateInputOriginEntryFileName(docId);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#generateOutputOriginEntryFileName(java.lang.String)
     */
    protected String generateOutputOriginEntryFileName(LaborCorrectionDocument document) {
        String docId = document.getDocumentHeader().getDocumentNumber();
        return generateOutputOriginEntryFileName(docId);
    }


    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#generateInputOriginEntryFileName(java.lang.String)
     */
    protected String generateInputOriginEntryFileName(String docId) {
        return getOriginEntryStagingDirectoryPath() + File.separator + docId + INPUT_ORIGIN_ENTRIES_FILE_SUFFIX;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#generateOutputOriginEntryFileName(java.lang.String)
     */
    public String generateOutputOriginEntryFileName(String docId) {
        return getOriginEntryStagingDirectoryPath() + File.separator + docId + OUTPUT_ORIGIN_ENTRIES_FILE_SUFFIX;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#persistOriginEntriesToFile(org.kuali.kfs.module.ld.document.LaborCorrectionDocument,
     *      java.util.Iterator)
     */
    public void persistInputOriginEntriesForInitiatedOrSavedDocument(LaborCorrectionDocument document, Iterator<LaborOriginEntry> entries) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.isInitiated() && !workflowDocument.isSaved()) {
            LOG.error("This method may only be called when the document is in the initiated or saved state.");
        }
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        if ( LOG.isInfoEnabled() ) {
            LOG.info( "About to save input labor origin entries for document " + document.getDocumentNumber() + " to file: " + fullPathUniqueFileName);
        }
        persistLaborOriginEntries(fullPathUniqueFileName, entries);
    }


    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#persistOutputLaborOriginEntriesForInitiatedOrSavedDocument(org.kuali.kfs.module.ld.document.LaborCorrectionDocument,
     *      java.util.Iterator)
     */
    public void persistOutputLaborOriginEntriesForInitiatedOrSavedDocument(LaborCorrectionDocument document, Iterator<LaborOriginEntry> entries) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.isInitiated() && !workflowDocument.isSaved()) {
            LOG.error("This method may only be called when the document is in the initiated or saved state.");
        }
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        if ( LOG.isInfoEnabled() ) {
            LOG.info( "About to save output labor origin entries for document " + document.getDocumentNumber() + " to file: " + fullPathUniqueFileName);
        }
        persistLaborOriginEntries(fullPathUniqueFileName, entries);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#persistLaborOriginEntries(java.lang.String,
     *      java.util.Iterator)
     */
    protected void persistLaborOriginEntries(String fullPathUniqueFileName, Iterator<LaborOriginEntry> entries) {
        File fileOut = new File(fullPathUniqueFileName);
        FileOutputStream streamOut = null;
        BufferedOutputStream bufferedStreamOut = null;
        try {
            streamOut = new FileOutputStream(fileOut);
            bufferedStreamOut = new BufferedOutputStream(streamOut);

            byte[] newLine = "\n".getBytes();
            while (entries.hasNext()) {
                LaborOriginEntry entry = entries.next();
                bufferedStreamOut.write(entry.getLine().getBytes());
                bufferedStreamOut.write(newLine);
            }
        }
        catch (IOException e) {
            LOG.error("unable to persist labor origin entries to file: " + fullPathUniqueFileName, e);
            throw new RuntimeException("unable to persist origin entries to file.", e);
        }
        finally {
            try {
                bufferedStreamOut.close();
                streamOut.close();
            }
            catch (IOException e) {
                LOG.error("unable to close output streams for file: " + fullPathUniqueFileName, e);
                throw new RuntimeException("unable to close output streams", e);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#openEntryOutputStreamForOutputGroup(org.kuali.kfs.module.ld.document.LaborCorrectionDocument)
     */
    protected BufferedOutputStream openEntryOutputStreamForOutputGroup(LaborCorrectionDocument document) throws IOException {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        return new BufferedOutputStream(new FileOutputStream(fullPathUniqueFileName));
    }


    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#removePersistedInputOriginEntriesForInitiatedOrSavedDocument(org.kuali.kfs.module.ld.document.LaborCorrectionDocument)
     */
    public void removePersistedInputOriginEntries(LaborCorrectionDocument document) {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        removePersistedOriginEntries(fullPathUniqueFileName);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#removePersistedOutputOriginEntriesForInitiatedOrSavedDocument(org.kuali.kfs.module.ld.document.LaborCorrectionDocument)
     */
    public void removePersistedOutputOriginEntries(LaborCorrectionDocument document) {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        removePersistedOriginEntries(fullPathUniqueFileName);
    }


    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#removePersistedInputOriginEntries(java.lang.String)
     */
    public void removePersistedInputOriginEntries(String docId) {
        removePersistedOriginEntries(generateInputOriginEntryFileName(docId));
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#removePersistedOutputOriginEntries(java.lang.String)
     */
    public void removePersistedOutputOriginEntries(String docId) {
        removePersistedOriginEntries(generateOutputOriginEntryFileName(docId));
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#removePersistedOriginEntries(java.lang.String)
     */
    protected void removePersistedOriginEntries(String fullPathUniqueFileName) {
        File fileOut = new File(fullPathUniqueFileName);
        if (fileOut.exists() && fileOut.isFile()) {
            fileOut.delete();
        }
    }


    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#retrievePersistedInputOriginEntries(org.kuali.kfs.module.ld.document.LaborCorrectionDocument,
     *      int)
     */
    public List<LaborOriginEntry> retrievePersistedInputOriginEntries(LaborCorrectionDocument document, int abortThreshold) {
        return retrievePersistedLaborOriginEntries(generateInputOriginEntryFileName(document), abortThreshold);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#retrievePersistedOutputOriginEntries(org.kuali.kfs.module.ld.document.LaborCorrectionDocument,
     *      int)
     */
    public List<LaborOriginEntry> retrievePersistedOutputOriginEntries(LaborCorrectionDocument document, int abortThreshold) {
        return retrievePersistedLaborOriginEntries(generateOutputOriginEntryFileName(document), abortThreshold);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#retrievePersistedLaborOriginEntries(java.lang.String, int)
     */
    protected List<LaborOriginEntry> retrievePersistedLaborOriginEntries(String fullPathUniqueFileName, int abortThreshold) {
        if ( LOG.isInfoEnabled() ) {
            LOG.info( "Retrieving Entries from file " + fullPathUniqueFileName);
        }
        File fileIn = new File(fullPathUniqueFileName);
        if (!fileIn.exists()) {
            LOG.error("File " + fullPathUniqueFileName + " does not exist.");
            throw new RuntimeException("File does not exist");
        }
        BufferedReader reader = null;
        FileReader fReader = null;

        List<LaborOriginEntry> entries = new ArrayList<LaborOriginEntry>();
        int lineNumber = 0;
        try {
            fReader = new FileReader(fileIn);
            reader = new BufferedReader(fReader);
            String line;
            while ((line = reader.readLine()) != null) {
                LaborOriginEntry entry = new LaborOriginEntry();
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
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#retrievePersistedInputOriginEntriesAsIterator(org.kuali.kfs.module.ld.document.LaborCorrectionDocument)
     */
    public Iterator<LaborOriginEntry> retrievePersistedInputOriginEntriesAsIterator(LaborCorrectionDocument document) {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        return retrievePersistedLaborOriginEntriesAsIterator(fullPathUniqueFileName);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#retrievePersistedOutputOriginEntriesAsIterator(org.kuali.kfs.module.ld.document.LaborCorrectionDocument)
     */
    public Iterator<LaborOriginEntry> retrievePersistedOutputOriginEntriesAsIterator(LaborCorrectionDocument document) {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        return retrievePersistedLaborOriginEntriesAsIterator(fullPathUniqueFileName);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#retrievePersistedLaborOriginEntriesAsIterator(java.lang.String)
     */
    protected Iterator<LaborOriginEntry> retrievePersistedLaborOriginEntriesAsIterator(String fullPathUniqueFileName) {
        if ( LOG.isInfoEnabled() ) {
            LOG.info( "Retrieving Entries from file " + fullPathUniqueFileName);
        }
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

            return new LaborOriginEntryFileIterator(reader);
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
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#areInputOriginEntriesPersisted(org.kuali.kfs.module.ld.document.LaborCorrectionDocument)
     */
    public boolean areInputOriginEntriesPersisted(LaborCorrectionDocument document) {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        File file = new File(fullPathUniqueFileName);
        return file.exists();
    }

    /**
     * Returns true if and only if the file corresponding to this document's output origin entries are on the file system.
     * 
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#areOutputOriginEntriesPersisted(org.kuali.kfs.module.ld.document.LaborCorrectionDocument)
     */
    public boolean areOutputOriginEntriesPersisted(LaborCorrectionDocument document) {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        File file = new File(fullPathUniqueFileName);
        return file.exists();
    }


    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#writePersistedInputOriginEntriesToStream(java.io.OutputStream)
     */
    public void writePersistedInputOriginEntriesToStream(LaborCorrectionDocument document, OutputStream out) throws IOException {
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        writePersistedOriginEntriesToStream(fullPathUniqueFileName, out);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#writePersistedOutputOriginEntriesToStream(java.io.OutputStream)
     */
    public void writePersistedOutputOriginEntriesToStream(LaborCorrectionDocument document, OutputStream out) throws IOException {
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        writePersistedOriginEntriesToStream(fullPathUniqueFileName, out);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#writePersistedOriginEntriesToStream(java.lang.String,
     *      java.io.OutputStream)
     */
    protected void writePersistedOriginEntriesToStream(String fullPathUniqueFileName, OutputStream out) throws IOException {
        FileInputStream fileIn = new FileInputStream(fullPathUniqueFileName);

        try {
            byte[] buf = new byte[1000];
            int bytesRead;

            while ((bytesRead = fileIn.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
        } catch ( IOException ex ) {
            LOG.error("Unable to write origin entries from " + fullPathUniqueFileName + "to output stream.",ex);
            throw ex;
        } catch ( RuntimeException ex ) {
            LOG.error("Unable to write origin entries from " + fullPathUniqueFileName + "to output stream.",ex);
            throw ex;
        } finally {
            fileIn.close();
        }
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#writePersistedOriginEntriesToStream(org.kuali.kfs.module.ld.document.LaborCorrectionDocument,
     *      org.kuali.module.gl.util)
     */
    public void persistOriginEntryGroupsForDocumentSave(LaborCorrectionDocument document, CorrectionDocumentEntryMetadata correctionDocumentEntryMetadata) {
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
        Iterator<LaborOriginEntry> inputGroupEntries;
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if ((workflowDocument.isSaved() && !(correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad() != null && correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad().equals(document.getCorrectionInputFileName()))) || workflowDocument.isInitiated()) {
            // we haven't saved the origin entry group yet, so let's load the entries from the DB and persist them for the document
            // this could be because we've previously saved the doc, but now we are now using a new input group, so we have to
            // repersist the input group
            
            
            //OriginEntryGroup group = originEntryGroupService.getExactMatchingEntryGroup(document.getCorrectionInputGroupId());
            File file = new File(document.getCorrectionInputFileName());
            
            inputGroupEntries = new LaborOriginEntryFileIterator(file);
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
            LOG.error("Unexpected state while trying to persist/retrieve GLCP origin entries during document save: document status is " + workflowDocument.getStatus().values().toString() + " selected input group: " + document.getCorrectionInputFileName() + " last saved input group: " + correctionDocumentEntryMetadata.getInputGroupIdFromLastDocumentLoad());
            throw new RuntimeException("Error persisting GLCP document origin entries.");
        }

        OriginEntryStatistics statistics;
        if (LaborCorrectionDocumentService.CORRECTION_TYPE_MANUAL.equals(correctionDocumentEntryMetadata.getEditMethod())) {
            // persist the allEntries element as the output group, since it has all of the modifications made by during the manual
            // edits
            Collection allEntries = new ArrayList();
            allEntries.addAll(correctionDocumentEntryMetadata.getAllEntries());
            persistOutputLaborOriginEntriesForInitiatedOrSavedDocument(document, allEntries.iterator());

            // even though the struts action handler may have computed the doc totals, let's recompute them
            statistics = CorrectionDocumentUtils.getStatistics(correctionDocumentEntryMetadata.getAllEntries());
        }
        else if (LaborCorrectionDocumentService.CORRECTION_TYPE_CRITERIA.equals(correctionDocumentEntryMetadata.getEditMethod())) {
            // we want to persist the values of the output group. So reapply all of the criteria on each entry, one at a time

            BufferedOutputStream bufferedOutputStream = null;
            try {
                bufferedOutputStream = openEntryOutputStreamForOutputGroup(document);
                statistics = new OriginEntryStatistics();
                byte[] newLine = "\n".getBytes();

                while (inputGroupEntries.hasNext()) {
                    LaborOriginEntry entry = inputGroupEntries.next();

                    entry = (LaborOriginEntry) CorrectionDocumentUtils.applyCriteriaToEntry(entry, correctionDocumentEntryMetadata.getMatchCriteriaOnly(), document.getCorrectionChangeGroup());
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
                throw new RuntimeException("Unable to persist output entry",e);
            }
            finally {
                if (bufferedOutputStream != null) {
                    try {
                        bufferedOutputStream.close();
                    }
                    catch (IOException e) {
                        LOG.error("Unable to close output stream for persisted output entries", e);
                        throw new RuntimeException("Unable to close output entry file",e);
                    }
                }
            }
        }
        else if (LaborCorrectionDocumentService.CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING.equals(correctionDocumentEntryMetadata.getEditMethod())) {
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
     * 
     */
    public String createOutputFileForProcessing(String docId, java.util.Date today) {
        File outputFile = new File(llcpDirectoryName + File.separator + docId + OUTPUT_ORIGIN_ENTRIES_FILE_SUFFIX);
        String newFileName = batchFileDirectoryName + File.separator + LLCP_OUTPUT_PREFIX  + "." + docId + buildFileExtensionWithDate(today);
        File newFile = new File (newFileName);
        FileReader inputFileReader;
        FileWriter newFileWriter;
        
        try{
            // copy output file and put in OriginEntryInformation directory
            inputFileReader = new FileReader(outputFile);
            newFileWriter = new FileWriter(newFile);
            int c;
            while ((c = inputFileReader.read()) != -1){
                newFileWriter.write(c);
            }
            
            inputFileReader.close();
            newFileWriter.close();
            
            // create done file, after successfully copying output file
            String doneFileName = newFileName.replace(GeneralLedgerConstants.BatchFileSystem.EXTENSION, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
            File doneFile = new File(doneFileName);
            if (!doneFile.exists()){
                doneFile.createNewFile();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return newFileName;
    }
    
    
    
    /**
     * Gets the OriginEntryStagingDirectoryPath attribute.
     * 
     * @return Returns the getLlcpDirectoryName.
     */
    protected String getOriginEntryStagingDirectoryPath() {
        return getLlcpDirectoryName();
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
     * Sets the originEntryService attribute value.
     * 
     * @param originEntryService The originEntryService to set.
     */
    public void setLaborOriginEntryService(LaborOriginEntryService laborOriginEntryService) {
        this.laborOriginEntryService = laborOriginEntryService;
    }

    /**
     * Gets the llcpDirectoryName attribute.
     * 
     * @return Returns the llcpDirectoryName.
     */
    public String getLlcpDirectoryName() {
        return llcpDirectoryName;
    }

    /**
     * Sets the llcpDirectoryName attribute value.
     * 
     * @param llcpDirectoryName The llcpDirectoryName to set.
     */
    public void setLlcpDirectoryName(String llcpDirectoryName) {
        this.llcpDirectoryName = llcpDirectoryName;
        //check directory directly when path is set 
        FileUtil.createDirectory(llcpDirectoryName);
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
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#getTableRenderColumnMetadata(java.lang.String)
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
                columnToAdd.setPropertyName(LaborPropertyConstants.PAY_PERIOD_END_DATE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Trn Total Hours");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.TRANSACTION_TOTAL_HOURS);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Payroll EndDate Fiscal Year");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Payroll EndDate Fiscal Period Code");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Empl Id");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.EMPLID);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Empl Record");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName(KFSPropertyConstants.EMPLOYEE_RECORD);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Earn Code");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.EARN_CODE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Pay Group");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.PAY_GROUP);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Salary Admin Plan");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.SALARY_ADMINISTRATION_PLAN);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Grade");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.GRADE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Run Id");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.RUN_IDENTIFIER);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Original Chart Code");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Original Account Number");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Original Sub-Account Number");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Original Object Code");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Original Sub-Object Code");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Company");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.HRMS_COMPANY);
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("SetId");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                columnToAdd.setPropertyName(LaborPropertyConstants.SET_ID);
                cachedColumns.add(columnToAdd);

                cachedColumns = Collections.unmodifiableList(cachedColumns);
            }
        }
        return cachedColumns;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#generateCorrectionReport(org.kuali.kfs.module.ld.document.LaborCorrectionDocument)
     */
    public void generateCorrectionReport(LaborCorrectionDocument document) {
        CorrectionDocumentReport correctionReport = new CorrectionDocumentReport();
        correctionReport.generateReport(laborCorrectionDocumentReportWriterService, document);
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    public String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }

    /**
     * @return Returns the laborCorrectionDocumentReportWriterService.
     */
    protected DocumentNumberAwareReportWriterService getLaborCorrectionDocumentReportWriterService() {
        return laborCorrectionDocumentReportWriterService;
    }

    /**
     * @param laborCorrectionDocumentReportWriterService The laborCorrectionDocumentReportWriterService to set.
     */
    public void setLaborCorrectionDocumentReportWriterService(DocumentNumberAwareReportWriterService laborCorrectionDocumentReportWriterService) {
        this.laborCorrectionDocumentReportWriterService = laborCorrectionDocumentReportWriterService;
    }

    protected static class LlcpFilenameFilter implements FilenameFilter {
        String documentNumber;
        public LlcpFilenameFilter( String documentNumber ) {
            this.documentNumber = documentNumber;
        }
        public boolean accept(File dir, String name) {
            return name.startsWith(LLCP_OUTPUT_PREFIX + "." + documentNumber);
        }
    }

    @Override
    public String[] findExistingCorrectionOutputFilesForDocument( String documentNumber ) {
        return new File(batchFileDirectoryName).list( new LlcpFilenameFilter(documentNumber));
    }
}
