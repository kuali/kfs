/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.List;

import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Defines validation methods on a payment file.
 */
public interface PaymentFileValidationService {

    /**
     * Performs validations that stop the loading of a payment file.
     * 
     * @param paymentFile parsed payment file object
     * @param errorMap map of errors encountered
     */
    public void doHardEdits(PaymentFileLoad paymentFile, MessageMap errorMap);

    /**
     * Performs warning checks and setting of default values.
     * 
     * @param paymentFile parsed payment file object
     * @return <code>List</code> of warning messages
     */
    public List<String> doSoftEdits(PaymentFileLoad paymentFile);
}
