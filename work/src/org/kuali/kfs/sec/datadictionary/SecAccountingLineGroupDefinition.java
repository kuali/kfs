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
