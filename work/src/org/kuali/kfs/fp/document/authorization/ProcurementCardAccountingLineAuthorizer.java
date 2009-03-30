/*
 * Copyright 2009 The Kuali Foundation.
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

import org.apache.commons.lang.StringUtils;

public class ProcurementCardAccountingLineAuthorizer extends FinancialProcessingAccountingLineAuthorizer {

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getKimHappyPropertyNameForField(java.lang.String)
     */
    @Override
    protected String getKimHappyPropertyNameForField(String convertedName) {
        String name = super.getKimHappyPropertyNameForField(convertedName);
        name =  name.replaceFirst("(.)*transactionEntriess\\.", StringUtils.EMPTY);
        
        return name;
    }

}
