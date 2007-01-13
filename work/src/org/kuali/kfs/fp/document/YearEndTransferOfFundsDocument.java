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


/**
 * Year End version of the <code>TransferOfFundsDocument</code> functionally the only difference is the accounting period code
 * used on the glpe entries
 * 
 * @see TransferOfFundsDocument
 * 
 */
public class YearEndTransferOfFundsDocument extends TransferOfFundsDocument implements YearEndDocument {

    /**
     * Constructs a YearEndTransferOfFundsDocument.java.
     */
    public YearEndTransferOfFundsDocument() {
        super();
    }
    // empty do not change. see above.
}
