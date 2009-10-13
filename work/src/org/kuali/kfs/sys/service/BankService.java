/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.businessobject.Bank;

/**
 * This service interface defines methods that a BankService implementation must provide.
 */
public interface BankService {

    /**
     * Retrieves a bank object who's primary id matches the values provided.
     * 
     * @param bankCode The bank code to be looked up by.
     * @return A Bank object with a matching primary id.
     */
    public Bank getByPrimaryId(String bankCode);
    
    /**
     * Retrieves the default bank code for the given document type from system parameter. 
     * .
     * @param documentClass <code>Class</code> for the document type
     * @return <code>Bank</code> object retrieved by default bank code
     */
    public Bank getDefaultBankByDocType(Class documentClass);
    
    /**
     * Determines if the bank specification is enabled in the system by system parameter.
     * 
     * @return true if specification is enabled
     */
    public boolean isBankSpecificationEnabled();
}
