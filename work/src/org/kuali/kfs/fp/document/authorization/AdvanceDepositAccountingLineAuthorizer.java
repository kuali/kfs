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
package org.kuali.kfs.fp.document.authorization;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.kim.bo.Person;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;

/**
 * The default implementation of AccountingLineAuthorizer
 */
public class AdvanceDepositAccountingLineAuthorizer extends AccountingLineAuthorizerBase {

    /**
     * Determines if the accounting line can be edited at account review level. The AD document can never be, so return false.
     * 
     * @param document the accounting document the accounting line to check lives on
     * @param accountingLine the accounting line to check
     * @param currentUser the user who is viewing this accounting line
     * @return true if the line is editable, false otherwise
     */
    @Override
    protected boolean isAccountingLineEditableOnAccountReview(AccountingDocument document, AccountingLine accountingLine, Person currentUser) {
        return false;
    }
}

