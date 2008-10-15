/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.service;

import java.util.List;

import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.rice.kns.util.ErrorMap;

/**
 * Defines methods for sending payment status emails.
 */
public interface PaymentFileEmailService {

    /**
     * Sends email for a payment load has failed. Errors encountered will be printed out in message
     * 
     * @param paymentFile parsed payment file object (might not be populated completely due to errors)
     * @param errors <code>ErrorMap</code> containing <code>ErrorMessage</code> entries
     */
    public void sendErrorEmail(PaymentFileLoad paymentFile, ErrorMap errors);

    /**
     * Sends email for a successful payment load. Warnings encountered will be printed out in message
     * 
     * @param paymentFile parsed payment file object
     * @param warnings <code>List</code> of <code>String</code> messages
     */
    public void sendLoadEmail(PaymentFileLoad paymentFile, List<String> warnings);

    /**
     * Sends email for a payment load that was held due to tax reasons
     * 
     * @param paymentFile parsed payment file object
     */
    public void sendTaxEmail(PaymentFileLoad paymentFile);

    /**
     * Sends email for a load done internally
     * 
     * @param batch <code>Batch</code> created by load
     */
    public void sendLoadEmail(Batch batch);

    /**
     * Sends email for a purap bundle that exceeds the maximum number of notes allowed
     * 
     * @param creditMemos list of credit memo documents in bundle
     * @param paymentRequests list of payment request documents in bundle
     * @param lineTotal total number of lines for bundle
     * @param maxNoteLines maximum number of lines allowed
     */
    public void sendExceedsMaxNotesWarningEmail(List<String> creditMemos, List<String> paymentRequests, int lineTotal, int maxNoteLines);
}
