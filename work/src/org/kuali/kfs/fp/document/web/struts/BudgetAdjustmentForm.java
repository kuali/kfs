/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.web.struts.form;

import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;

/**
 * This class is the form class for the ProcurementCard document. This method extends the parent KualiTransactionalDocumentFormBase
 * class which contains all of the common form methods and form attributes needed by the Procurment Card document.
 */
public class BudgetAdjustmentForm extends KualiAccountingDocumentFormBase {

    /**
     * Constructs a BudgetAdjustmentForm instance and sets up the appropriately casted document. Also, the
     * newSourceLine/newTargetLine need to be the extended
     * BudgetAdjustmentSourceAccountingLine/BudgetAdjustmentTargetAccountingLine.
     */
    public BudgetAdjustmentForm() {
        super();
        setDocument(new BudgetAdjustmentDocument());
    }
}