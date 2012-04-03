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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.gl.batch.service.FileEnterpriseFeederHelperService;
import org.kuali.kfs.gl.batch.service.ReconciliationParserService;
import org.kuali.kfs.gl.batch.service.ReconciliationService;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.gl.service.impl.EnterpriseFeederStatusAndErrorMessagesWrapper;
import org.kuali.kfs.sys.Message;

/**
 * This class reads origin entries in a flat file format, reconciles them, and loads them into the origin entry table.
 * Note: the feeding algorithm of this service will read the data file twice to minimize memory usage.
 */
public class FileEnterpriseFeederHelperServiceImpl implements FileEnterpriseFeederHelperService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FileEnterpriseFeederHelperServiceImpl.class);

    protected ReconciliationParserService reconciliationParserService;
    protected ReconciliationService reconciliationService;
    protected OriginEntryService originEntryService;

    /**
     * This method does the reading and the loading of reconciliation. Read class description. This method DOES NOT handle the
     * deletion of the done file
     *
     * @param doneFile a URL that must be present. The contents may be empty
     * @param dataFile a URL to a flat file of origin entry rows.
     * @param reconFile a URL to the reconciliation file. See the implementation of {@link ReconciliationParserService} for file
     *        format.
     * @param originEntryGroup the group into which the origin entries will be loaded
     * @param feederProcessName the name of the feeder process
     * @param reconciliationTableId the name of the block to use for reconciliation within the reconciliation file
     * @param statusAndErrors any status information should be stored within this object
     * @see org.kuali.module.gl.service.impl.FileEnterpriseFeederHelperService#feedOnFile(java.io.File, java.io.File, java.io.File,
     *      org.kuali.kfs.gl.businessobject.OriginEntryGroup)
     */
    @Override
    public void feedOnFile(File doneFile, File dataFile, File reconFile, PrintStream enterpriseFeedPs, String feederProcessName, String reconciliationTableId, EnterpriseFeederStatusAndErrorMessagesWrapper statusAndErrors, LedgerSummaryReport ledgerSummaryReport) {
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Processing done file: " + doneFile.getAbsolutePath());
        }

        List<Message> errorMessages = statusAndErrors.getErrorMessages();
        BufferedReader dataFileReader = null;

        ReconciliationBlock reconciliationBlock = null;
        Reader reconReader = null;
        try {
            reconReader = new FileReader(reconFile);
            reconciliationBlock = reconciliationParserService.parseReconciliationBlock(reconReader, reconciliationTableId);
        }
        catch (IOException e) {
            LOG.error("IO Error occured trying to read the recon file.", e);
            errorMessages.add(new Message("IO Error occured trying to read the recon file.", Message.TYPE_FATAL));
            reconciliationBlock = null;
            statusAndErrors.setStatus(new FileReconBadLoadAbortedStatus());
            throw new RuntimeException(e);
        }
        catch (RuntimeException e) {
            LOG.error("Error occured trying to parse the recon file.", e);
            errorMessages.add(new Message("Error occured trying to parse the recon file.", Message.TYPE_FATAL));
            reconciliationBlock = null;
            statusAndErrors.setStatus(new FileReconBadLoadAbortedStatus());
            throw e;
        }
        finally {
            if (reconReader != null) {
                try {
                    reconReader.close();
                }
                catch (IOException e) {
                    LOG.error("Error occured trying to close recon file: " + reconFile.getAbsolutePath(), e);
                }
            }
        }

        try {
            if (reconciliationBlock == null) {
                errorMessages.add(new Message("Unable to parse reconciliation file.", Message.TYPE_FATAL));
            }
            else {
                dataFileReader = new BufferedReader(new FileReader(dataFile));
                Iterator<OriginEntryFull> fileIterator = new OriginEntryFileIterator(dataFileReader, false);
                reconciliationService.reconcile(fileIterator, reconciliationBlock, errorMessages);

                fileIterator = null;
                dataFileReader.close();
                dataFileReader = null;
            }

            if (reconciliationProcessSucceeded(errorMessages)) {
                dataFileReader = new BufferedReader(new FileReader(dataFile));
                String line;
                int count = 0;

                // create an entry to temporarily parse each line as it comes in
                OriginEntryFull tempEntry = new OriginEntryFull();
                while ((line = dataFileReader.readLine()) != null) {
                    try {
                        enterpriseFeedPs.printf("%s\n", line);
                        tempEntry.setFromTextFileForBatch(line, count);
                        ledgerSummaryReport.summarizeEntry(tempEntry);
                    } catch (Exception e) {
                        throw new IOException(e.toString());
                    }

                    count++;
                }
                dataFileReader.close();
                dataFileReader = null;

                statusAndErrors.setStatus(new FileReconOkLoadOkStatus());
            }
            else {
                statusAndErrors.setStatus(new FileReconBadLoadAbortedStatus());
            }
        }
        catch (Exception e) {
            LOG.error("Caught exception when reconciling/loading done file: " + doneFile, e);
            statusAndErrors.setStatus(new ExceptionCaughtStatus());
            errorMessages.add(new Message("Caught exception attempting to reconcile/load done file: " + doneFile + ".  File contents are NOT loaded", Message.TYPE_FATAL));
            // re-throw the exception rather than returning a value so that Spring will auto-rollback
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            else {
                // Spring only rolls back when throwing a runtime exception (by default), so we throw a new exception
                throw new RuntimeException(e);
            }
        }
        finally {
            if (dataFileReader != null) {
                try {
                    dataFileReader.close();
                }
                catch (IOException e) {
                    LOG.error("IO Exception occured trying to close connection to the data file", e);
                    errorMessages.add(new Message("IO Exception occured trying to close connection to the data file", Message.TYPE_FATAL));
                }
            }
        }
    }

    /**
     * Returns whether the reconciliation process succeeded by looking at the reconciliation error messages For this implementation,
     * the reconciliation does not succeed if at least one of the error messages in the list has a type of
     * {@link Message#TYPE_FATAL}
     *
     * @param errorMessages a List of errorMessages
     * @return true if any of those error messages were fatal
     */
    protected boolean reconciliationProcessSucceeded(List<Message> errorMessages) {
        for (Message message : errorMessages) {
            if (message.getType() == Message.TYPE_FATAL) {
                return false;
            }
        }
        return true;
    }

    public void setReconciliationParserService(ReconciliationParserService reconciliationParserService) {
        this.reconciliationParserService = reconciliationParserService;
    }
    public void setReconciliationService(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }
}
