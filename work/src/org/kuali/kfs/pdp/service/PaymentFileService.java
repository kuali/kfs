/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.pdp.service;

import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.exception.PaymentLoadException;

public interface PaymentFileService {
    /**
     * Process all incoming payment files
     */
    public void processPaymentFiles();

    /**
     * Load a Payment file
     * 
     * @param filename
     * @param user
     * @return
     * @throws PaymentLoadException
     */
    public LoadPaymentStatus loadPayments(String filename, PdpUser user) throws PaymentLoadException;

    /**
     * Send notification email about a loaded batch
     * 
     * @param batch
     */
    public void sendLoadEmail(Batch batch);

    /**
     * Save a batch
     * 
     * @param batch
     */
    public void saveBatch(Batch batch);
}
