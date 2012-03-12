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
package org.kuali.kfs.pdp.service;

import org.kuali.kfs.pdp.businessobject.LoadPaymentStatus;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.InitiateDirectory;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Handles processing (validation, loading, and reporting) of incoming payment files.
 */
public interface PaymentFileService extends InitiateDirectory{

    /**
     * Process all incoming payment files
     * 
     * @param paymentInputFileType <code>BatchInputFileType</code> for payment files
     */
    public void processPaymentFiles(BatchInputFileType paymentInputFileType);

    /**
     * Performs hard edits on payment file
     * 
     * @param paymentFile <code>PaymentFileLoad</code> containing parsed file contents
     * @param errorMap <code>Map</code> that will hold errors encountered
     */
    public void doPaymentFileValidation(PaymentFileLoad paymentFile, MessageMap errorMap);

    /**
     * Performs soft edits of payment file data and loads records into database
     * 
     * @param paymentFile <code>PaymentFileLoad</code> containing parsed file contents
     * @param status <code>LoadPaymentStatus</code> containing status information for load
     * @param incomingFileName string file name
     */
    public void loadPayments(PaymentFileLoad paymentFile, LoadPaymentStatus status, String incomingFileName);

    /**
     * Creates the PDP XML output which can be parsed to obtain load status information
     * 
     * @param status <code>LoadPaymentStatus</code> containing status information for load
     * @param inputFileName incomingFileName string file name
     * @return true if output file was successfully created, false otherwise
     */
    public boolean createOutputFile(LoadPaymentStatus status, String inputFileName);

}

