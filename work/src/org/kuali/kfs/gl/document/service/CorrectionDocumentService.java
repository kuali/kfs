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
package org.kuali.kfs.gl.document.service;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.document.CorrectionDocumentUtils;
import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;
import org.kuali.kfs.gl.document.web.CorrectionDocumentEntryMetadata;
import org.kuali.kfs.sys.batch.InitiateDirectory;
import org.kuali.rice.kns.web.ui.Column;

/**
 * An interface declaring methods needed by the GLCP to function
 */
public interface CorrectionDocumentService extends InitiateDirectory{
    public final static String CORRECTION_TYPE_MANUAL = "M";
    public final static String CORRECTION_TYPE_CRITERIA = "C";
    public final static String CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING = "R";

    public final static String SYSTEM_DATABASE = "D";
    public final static String SYSTEM_UPLOAD = "U";

    /**
     * When passed into {@link #retrievePersistedInputOriginEntries(CorrectionDocument, int)} and
     * {@link #retrievePersistedOutputOriginEntries(CorrectionDocument, int)} as the int parameter, this will signify that there is
     * no abort threshold (i.e. the methods should return all of the persisted rows, regardless of number of rows.
     */
    public final static int UNLIMITED_ABORT_THRESHOLD = CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_UNLIMITED;

    /**
     * Returns a specific correction change group for a GLCP document
     * 
     * @param docId the document id of a GLCP document
     * @param i the number of the correction group within the document
     * @return a CorrectionChangeGroup
     */
    public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String docId, int i);

    /**
     * Finds CollectionChange records associated with a given document id and correction change group
     * 
     * @param docId the document id of a GLCP document
     * @param i the number of the correction group within the document
     * @return a List of qualifying CorrectionChange records
     */
    public List findByDocumentHeaderIdAndCorrectionGroupNumber(String docId, int i);

    /**
     * Finds Collection Criteria associated with the given GLCP document and group
     * 
     * @param docId the document id of a GLCP document
     * @param i the number of the correction group within the document
     * @return a List of qualifying CorrectionCriteria
     */
    public List findByDocumentNumberAndCorrectionGroupNumber(String docId, int i);

    /**
     * Retrieves a correction document by the document id
     * 
     * @param docId the document id of the GLCP to find
     * @return a CorrectionDocument if found
     */
    public GeneralLedgerCorrectionProcessDocument findByCorrectionDocumentHeaderId(String docId);

    /**
     * Returns metadata to help render columns in the GLCP. Do not modify this list or the contents in this list.
     * 
     * @param docId the document id of a GLCP document
     * @return a List of Columns to render
     */
    public List<Column> getTableRenderColumnMetadata(String docId);

    /**
     * This method persists an Iterator of input origin entries for a document that is in the initiated or saved state
     * 
     * @param document an initiated or saved document
     * @param entries an Iterator of origin entries
     */
    public void persistInputOriginEntriesForInitiatedOrSavedDocument(GeneralLedgerCorrectionProcessDocument document, Iterator<OriginEntryFull> entries);

    /**
     * Removes input origin entries that were saved to the database associated with the given document
     * 
     * @param document a GLCP document
     */
    public void removePersistedInputOriginEntries(GeneralLedgerCorrectionProcessDocument document);

    /**
     * Removes input origin entries that were saved to the database associated with the given document
     * 
     * @param docId the document id of a GLCP document
     */
    public void removePersistedInputOriginEntries(String docId);

    /**
     * Retrieves input origin entries that have been persisted for this document
     * 
     * @param document the document
     * @param abortThreshold if the file exceeds this number of rows, then null is returned. {@link UNLIMITED_ABORT_THRESHOLD}
     *        signifies that there is no limit
     * @return the list, or null if there are too many origin entries
     * @throws RuntimeException several reasons, primarily relating to underlying persistence layer problems
     */
    public List<OriginEntryFull> retrievePersistedInputOriginEntries(GeneralLedgerCorrectionProcessDocument document, int abortThreshold);

    /**
     * Returns true if the system is storing input origin entries for this class. Note that this does not mean that there's at least
     * one input origin entry record persisted for this document, but merely returns true if and only if the underlying persistence
     * mechanism has a record of this document's origin entries. See the docs for the implementations of this method for more
     * implementation specific details.
     * 
     * @param document a GLCP document
     * @return Returns true if system should store origin entries, false otherwise
     */
    public boolean areInputOriginEntriesPersisted(GeneralLedgerCorrectionProcessDocument document);

    /**
     * Writes out the persisted input origin entries in an {@link OutputStream} in a flat file format
     * 
     * @param document a GLCP document
     * @param out an open and ready output stream
     * @throws IOException thrown if errors were encountered writing to the Stream
     * @throws RuntimeException several reasons, including if the entries are not persisted
     */
    public void writePersistedInputOriginEntriesToStream(GeneralLedgerCorrectionProcessDocument document, OutputStream out) throws IOException;

    /**
     * This method persists an Iterator of input origin entries for a document that is in the initiated or saved state
     * 
     * @param document an initiated or saved document
     * @param entries an Iterator of OriginEntries to persist
     */
    public void persistOutputOriginEntriesForInitiatedOrSavedDocument(GeneralLedgerCorrectionProcessDocument document, Iterator<OriginEntryFull> entries);

    /**
     * Removes all output origin entries persisted in the database created by the given document 
     * 
     * @param document a GLCP document
     */
    public void removePersistedOutputOriginEntries(GeneralLedgerCorrectionProcessDocument document);

    /**
     * Removes all output origin entries persisted in the database created by the given document 
     *
     * @param docId the document id of a GLCP document
     */
    public void removePersistedOutputOriginEntries(String docId);

    /**
     * Retrieves output origin entries that have been persisted for this document
     * 
     * @param document the document
     * @param abortThreshold if the file exceeds this number of rows, then null is returned. {@link UNLIMITED_ABORT_THRESHOLD}
     *        signifies that there is no limit
     * @return the list, or null if there are too many origin entries
     * @throws RuntimeException several reasons, primarily relating to underlying persistence layer problems
     */
    public List<OriginEntryFull> retrievePersistedOutputOriginEntries(GeneralLedgerCorrectionProcessDocument document, int abortThreshold);

    /**
     * Retrieves input origin entries that have been persisted for this document in an iterator. Implementations of this method may
     * choose to implement this method in a way that consumes very little memory.
     * 
     * @param document the document
     * @return the iterator
     * @throws RuntimeException several reasons, primarily relating to underlying persistence layer problems
     */
    public Iterator<OriginEntryFull> retrievePersistedInputOriginEntriesAsIterator(GeneralLedgerCorrectionProcessDocument document);

    /**
     * Retrieves output origin entries that have been persisted for this document in an iterator. Implementations of this method may
     * choose to implement this method in a way that consumes very little memory.
     * 
     * @param document the document
     * @return the iterator
     * @throws RuntimeException several reasons, primarily relating to underlying persistence layer problems
     */
    public Iterator<OriginEntryFull> retrievePersistedOutputOriginEntriesAsIterator(GeneralLedgerCorrectionProcessDocument document);

    /**
     * Returns true if the system is storing output origin entries for this class. Note that this does not mean that there's at
     * least one output origin entry record persisted for this document, but merely returns true if and only if the underlying
     * persistence mechanism has a record of this document's origin entries. See the docs for the implementations of this method for
     * more implementation specific details.
     * 
     * @param document a GLCP document to query
     * @return true if origin entries are stored to the system, false otherwise
     */
    public boolean areOutputOriginEntriesPersisted(GeneralLedgerCorrectionProcessDocument document);

    /**
     * Writes out the persisted output origin entries in an {@link OutputStream} in a flat file format\
     * 
     * @param document a GLCP document
     * @param out axn open and ready output stream
     * @throws IOException thrown if IOExceptions occurred in writing the persisted origin entries
     * @throws RuntimeException several reasons, including if the entries are not persisted
     */
    public void writePersistedOutputOriginEntriesToStream(GeneralLedgerCorrectionProcessDocument document, OutputStream out) throws IOException;

    /**
     * Saves the input and output origin entry groups for a document prior to saving the document
     * 
     * @param document a GLCP document
     * @param correctionDocumentEntryMetadata metadata about this GLCP document
     */
    public void persistOriginEntryGroupsForDocumentSave(GeneralLedgerCorrectionProcessDocument document, CorrectionDocumentEntryMetadata correctionDocumentEntryMetadata);

    /**
     * Retrieves all of the documents that were finalized on a certain date
     * 
     * @param date the date to find GLCP documents finalized on
     * @return a collection of documents
     */
    public Collection<GeneralLedgerCorrectionProcessDocument> getCorrectionDocumentsFinalizedOn(Date date);
    
    public String generateOutputOriginEntryFileName(String docId);
    
    public String createOutputFileForProcessing(String docId, java.util.Date today);
    
    public String getBatchFileDirectoryName();
    
    public String getGlcpDirectoryName();
    
    /**
     * Generate a text report for the given correction document
     * 
     * @param document GLCP document to report on
     */
    public void generateCorrectionReport(GeneralLedgerCorrectionProcessDocument document);
    
    public void aggregateCorrectionDocumentReports(GeneralLedgerCorrectionProcessDocument document);

    /**
     * Finds any existing output files for the given document.  Used to prevent double-processing.
     * 
     * @param documentNumber
     * @return
     */
    public String[] findExistingCorrectionOutputFilesForDocument( String documentNumber );
}
