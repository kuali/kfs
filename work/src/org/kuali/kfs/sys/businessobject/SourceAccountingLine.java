/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject;


/**
 * This class represents the "source" grouping of accounting lines in a given document. Its counterpart is the TargetAccountingLine
 * class. Both objects' data is stored in the DB in a single table named "FP_ACCT_LINES_T." Most documents follow the "source" and
 * "target" patterns. Source Accounting Line Business Object
 */
public class SourceAccountingLine extends AccountingLineBase {

    private static final long serialVersionUID = -2699347311790831686L;

    /**
     * This constructor needs to initialize the financialDocumentLineTypeCode attribute to the value for this class.
     */
    public SourceAccountingLine() {
        super();
        super.financialDocumentLineTypeCode = "F";
    }
}
