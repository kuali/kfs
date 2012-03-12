/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;


/**
 * form for <code>YearEndDistributionOfIncomeAndExpenseDocument</code> the only functional difference between the YearEnd version
 * and the non-yearEnd version of a document is the glpe's generation.
 * 
 * @see org.kuali.kfs.fp.document.web.struts.DistributionOfIncomeAndExpenseForm
 */

public class YearEndDistributionOfIncomeAndExpenseForm extends DistributionOfIncomeAndExpenseForm {


    /**
     * Constructs a YearEndDistributionOfIncomeAndExpenseForm.java.
     */
    public YearEndDistributionOfIncomeAndExpenseForm() {
        super();
    }
    
    @Override
    protected String getDefaultDocumentTypeName() {
        return "YEDI";
    }
    // empty do not change. see above.
}
