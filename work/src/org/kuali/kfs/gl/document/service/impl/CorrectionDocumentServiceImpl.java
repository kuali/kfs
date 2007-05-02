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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.core.dao.DocumentDao;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.web.comparator.NumericValueComparator;
import org.kuali.core.web.comparator.StringValueComparator;
import org.kuali.core.web.comparator.TemporalValueComparator;
import org.kuali.core.web.ui.Column;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.dao.CorrectionChangeDao;
import org.kuali.module.gl.dao.CorrectionChangeGroupDao;
import org.kuali.module.gl.dao.CorrectionCriteriaDao;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.module.gl.service.OriginEntryService;
import org.springframework.transaction.annotation.Transactional;

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
                columnToAdd.setPropertyName("universityFiscalYear");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Chart Code");
                columnToAdd.setPropertyName("chartOfAccountsCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Account Number");
                columnToAdd.setPropertyName("accountNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sub Account Number");
                columnToAdd.setPropertyName("subAccountNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Object Code");
                columnToAdd.setPropertyName("financialObjectCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sub Object Code");
                columnToAdd.setPropertyName("financialSubObjectCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Balance Type");
                columnToAdd.setPropertyName("financialBalanceTypeCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Object Type");
                columnToAdd.setPropertyName("financialObjectTypeCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Fiscal Period");
                columnToAdd.setPropertyName("universityFiscalPeriodCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Document Type");
                columnToAdd.setPropertyName("financialDocumentTypeCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Origin Code");
                columnToAdd.setPropertyName("financialSystemOriginationCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Document Number");
                columnToAdd.setPropertyName("documentNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sequence Number");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName("transactionLedgerEntrySequenceNumber");
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Description");
                columnToAdd.setPropertyName("transactionLedgerEntryDescription");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Amount");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName("transactionLedgerEntryAmount");
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Debit Credit Indicator");
                columnToAdd.setPropertyName("transactionDebitCreditCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Transaction Date");
                columnToAdd.setPropertyName("transactionDate");
                columnToAdd.setValueComparator(TemporalValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Org Doc Number");
                columnToAdd.setPropertyName("organizationDocumentNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Project Code");
                columnToAdd.setPropertyName("projectCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Org Ref ID");
                columnToAdd.setPropertyName("organizationReferenceId");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Doc Type");
                columnToAdd.setPropertyName("referenceFinancialDocumentTypeCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Origin Code");
                columnToAdd.setPropertyName("referenceFinancialSystemOriginationCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Doc Number");
                columnToAdd.setPropertyName("referenceFinancialDocumentNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Reversal Date");
                columnToAdd.setPropertyName("financialDocumentReversalDate");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Enc Update Code");
                columnToAdd.setPropertyName("transactionEncumbranceUpdateCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
                
                cachedColumns = Collections.unmodifiableList(cachedColumns);
            }
        }
        return cachedColumns;
    }

    protected String generateInputOriginEntryFileName(CorrectionDocument document) {
        String docId = document.getDocumentHeader().getDocumentNumber();
        return getOriginEntryStagingDirectoryPath() + File.separator + docId + INPUT_ORIGIN_ENTRIES_FILE_SUFFIX;
    }
    
    protected String generateOutputOriginEntryFileName(CorrectionDocument document) {
        String docId = document.getDocumentHeader().getDocumentNumber();
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
                bufferedStreamOut.write(entry.getLineWithOriginEntryId().getBytes());
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
     * @see org.kuali.module.gl.service.CorrectionDocumentService#removePersistedInputOriginEntriesForInitiatedOrSavedDocument(org.kuali.module.gl.document.CorrectionDocument)
     */
    public void removePersistedInputOriginEntries(CorrectionDocument document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.stateIsInitiated() && !workflowDocument.stateIsSaved()) {
            LOG.error("This method may only be called when the document is in the initiated or saved state.");
        }
        String fullPathUniqueFileName = generateInputOriginEntryFileName(document);
        removePersistedOriginEntries(fullPathUniqueFileName);
    }

    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#removePersistedOutputOriginEntriesForInitiatedOrSavedDocument(org.kuali.module.gl.document.CorrectionDocument)
     */
    public void removePersistedOutputOriginEntries(CorrectionDocument document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.stateIsInitiated() && !workflowDocument.stateIsSaved()) {
            LOG.error("This method may only be called when the document is in the initiated or saved state.");
        }
        String fullPathUniqueFileName = generateOutputOriginEntryFileName(document);
        removePersistedOriginEntries(fullPathUniqueFileName);
    }
    
    protected void removePersistedOriginEntries(String fullPathUniqueFileName) {
        File fileOut = new File(fullPathUniqueFileName);
        if (fileOut.exists() && fileOut.isFile()) {
            fileOut.delete();
        }
    }

    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#retrievePersistedInputOriginEntries(org.kuali.module.gl.document.CorrectionDocument)
     */
    public List<OriginEntry> retrievePersistedInputOriginEntries(CorrectionDocument document) {
        return retrievePersistedOriginEntries(generateInputOriginEntryFileName(document));
    }

    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#retrievePersistedOutputOriginEntries(org.kuali.module.gl.document.CorrectionDocument)
     */
    public List<OriginEntry> retrievePersistedOutputOriginEntries(CorrectionDocument document) {
        return retrievePersistedOriginEntries(generateOutputOriginEntryFileName(document));
    }
    
    protected List<OriginEntry> retrievePersistedOriginEntries(String fullPathUniqueFileName) {
        File fileIn = new File(fullPathUniqueFileName);
        if (!fileIn.exists()) {
            return new ArrayList<OriginEntry>();
        }
        BufferedReader reader = null;
        
        List<OriginEntry> entries = new ArrayList<OriginEntry>();
        int lineNumber = 0;
        try {
            reader = new BufferedReader(new FileReader(fileIn));
            String line;
            while ((line = reader.readLine()) != null) {
                OriginEntry entry = new OriginEntry(line, lineNumber, true);
                lineNumber++;
                entries.add(entry);
            }
        }
        catch (IOException e) {
            LOG.error("retrievePersistedOriginEntries() Error reading file", e);
            throw new IllegalArgumentException("Error reading file");
        }
        return entries;
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
}
