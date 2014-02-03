/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.authorization;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;

/**
 * The sole purpose of this interface is to allow authorizers that needs to bypass capital check to be able
 * to do so without having class casting issue when the security module is on, in which case all accounting lines
 * will go through SecAccountingLineAuthorizer, which also implements the bypass method, so it can be casted to
 * one of the desired accounting line authorizer, as a result.
 */
public interface CapitalAccountingLinesAuthorizer {
    /**
     * This method bypasses the capital accounting lines readOnly check in this class and
     * calls the super determineEditPermissionOnField
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionOnField(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.String, boolean)
     */
    public boolean determineEditPermissionOnFieldBypassCapitalCheck(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage);

}
