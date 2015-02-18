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
