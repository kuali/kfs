/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sec.datadictionary;

import org.kuali.kfs.sec.document.authorization.SecAccountingLineAuthorizer;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;


/**
 * Override of group definition to wrap access security around accounting objects
 */
public class SecAccountingLineGroupDefinition extends AccountingLineGroupDefinition {

    /**
     * Creates a new instance of the security accounting line authorizer and sets the configured authorizer property
     * 
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition#createAccountingLineAuthorizer()
     */
    @Override
    protected AccountingLineAuthorizer createAccountingLineAuthorizer() {
        Class<? extends AccountingLineAuthorizer> authorizerClass = getAccountingLineAuthorizerClass();

        SecAccountingLineAuthorizer secAuthorizer = new SecAccountingLineAuthorizer();
        try {
            AccountingLineAuthorizer authorizer = authorizerClass.newInstance();
            secAuthorizer.setLineAuthorizer(authorizer);
        } catch (InstantiationException ie) {
            throw new IllegalArgumentException("InstantiationException while attempting to instantiate AccountingLineAuthorization class", ie);
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("IllegalAccessException while attempting to instantiate AccountingLineAuthorization class", iae);
        }
        
        return secAuthorizer;
    }

}
