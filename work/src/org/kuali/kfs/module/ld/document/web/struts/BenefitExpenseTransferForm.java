/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.struts.form;

import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.labor.document.BenefitExpenseTransferDocument;

/**
 * This class is the form class for the Benefit Expense Transfer document. This method extends the parent
 * KualiTransactionalDocumentFormBase class which contains all of the common form methods and form attributes needed by the
 * Benefit Expense Transfer document. It adds a new method which is a convenience method for getting at the Benefit Expense Transfer document easier.
 * 
 * 
 */
public class BenefitExpenseTransferForm extends LaborDocumentFormBase {
    private UniversalUser user;
    private String userId;
    private String emplid;

    /**
     * Constructs a BenefitExpenseTransferForm instance and sets up the appropriately casted document.
     */
    public BenefitExpenseTransferForm() {
        super();
        setDocument(new BenefitExpenseTransferDocument());
    }

    /**
     * @return Returns the BenefitExpenseTransferDocument.
     */
    public BenefitExpenseTransferDocument getBenefitExpenseTransferDocument() {
        return (BenefitExpenseTransferDocument) getDocument();
    }    
    
    /**
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#getForcedReadOnlyFields()
     */
    @Override
    public Map getForcedReadOnlyFields() {
        Map map = super.getForcedReadOnlyFields(); 
     //  map.put("financialObjectCode", Boolean.TRUE);
    //   map.put("financialSubObjectCode", Boolean.TRUE);
    //  map.put("positionNumber", Boolean.TRUE);
       
        return map;
    }
}
