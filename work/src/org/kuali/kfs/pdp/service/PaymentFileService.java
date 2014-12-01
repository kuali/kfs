/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

