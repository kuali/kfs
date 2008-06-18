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
package org.kuali.kfs.document;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.kfs.bo.FinancialSystemDocumentHeader;

public interface FinancialSystemTransactionalDocument extends TransactionalDocument {

    /**
     * @see org.kuali.core.document.Document#getDocumentHeader()
     */
    public FinancialSystemDocumentHeader getDocumentHeader();

    /**
     * This method returns whether or not this document is allowed to be corrected.
     * 
     * @return True if it can be corrected, false otherwise.
     */
    public boolean getAllowsErrorCorrection();

}
