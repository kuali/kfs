/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.batch.service;

import java.io.PrintStream;

import org.kuali.rice.core.api.util.type.KualiDecimal;


/**
 * Service interface for implementing methods to retrieve and validate awards to create contracts and grants invoice documents.
 */
public interface LetterOfCreditCreateService {

    /**
     * This method created cashcontrol documents and payment application based on the loc creation type and loc value passed.
     * 
     * @param customerNumber
     * @param locCreationType
     * @param locValue
     * @param totalAmount
     * @param outputFileStream
     * @return documentNumber
     */
    public String createCashControlDocuments(String customerNumber, String locCreationType, String locValue, KualiDecimal totalAmount, PrintStream outputFileStream);

    /**
     * The method validates if there are any existing cash control documents for the same locValue and customer number combination.
     * 
     * @param customerNumber
     * @param locCreationType
     * @param locValue
     * @param outputFileStream
     * @return
     */
    public boolean validatecashControlDocument(String customerNumber, String locCreationType, String locValue, PrintStream outputFileStream);
    
    /**
     * This method retrieves all the cash control and payment application docs with a status of 'I' and routes them to the next step in the
     * routing path.
     * 
     * @return True if the routing was performed successfully. A runtime exception will be thrown if any errors occur while routing.
     * 
     */
    public boolean routeLOCDocuments() ;

}
