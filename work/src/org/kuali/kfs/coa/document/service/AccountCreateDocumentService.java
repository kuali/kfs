/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.coa.document.service;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.KCAward;

/**
 * An interface of services to create account document for CG
 */
public interface AccountCreateDocumentService {
    
    /**
    * This method will use KC award data, merge using default kfs to create
    * an  account for the maintenance document.
    */
    public Account createAccountForCGMaintenanceDocument(KCAward kCAward);
    
    /**
     * This method creates a maintenance document for the cg account
     */
    
    public String createAutomaticCGAccountMaintenanceDocument(Account account);
}
