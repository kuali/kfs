/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.financial.document;

import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.module.financial.bo.BasicFormatWithLineDescriptionAccountingLineParser;

/**
 * This is the business object that represents the ServiceBillingDocument in Kuali. See
 * {@link org.kuali.module.financial.rules.ServiceBillingDocumentRule} for details on how it differs from
 * {@link InternalBillingDocument}.
 */
public class ServiceBillingDocument extends InternalBillingDocument {

    /**
     * @see org.kuali.module.financial.document.InternalBillingDocument#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new BasicFormatWithLineDescriptionAccountingLineParser();
    }
}