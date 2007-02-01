/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.struts.action;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.AccountingLineOverride;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.labor.bo.SalaryExpenseTransferAccountingLine;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;

/**
 * This class extends the parent KualiTransactionalDocumentActionBase class, which contains all common action methods. Since the SEP
 * follows the basic transactional document pattern, there are no specific actions that it has to implement; however, this empty
 * class is necessary for integrating into the framework.
 * 
 * 
 */
public class SalaryExpenseTransferAction extends LaborDocumentActionBase {
        
    protected void processAccountingLineOverrides(KualiTransactionalDocumentFormBase transForm) {

        processAccountingLineOverrides(transForm.getNewSourceLine());
        processAccountingLineOverrides(transForm.getNewTargetLine());
        if (transForm.hasDocumentId()) {
            TransactionalDocument transactionalDocument = (TransactionalDocument) transForm.getDocument();
            SalaryExpenseTransferDocument salaryExpenseTransferDocument = (SalaryExpenseTransferDocument)transactionalDocument;            
            
            // Save the employee ID on all source and target accounting lines
            for (Iterator i = transactionalDocument.getSourceAccountingLines().iterator(); i.hasNext();) {
                SalaryExpenseTransferAccountingLine salaryExpenseTransferAccountingLine = (SalaryExpenseTransferAccountingLine) i.next();
                salaryExpenseTransferAccountingLine.setEmplid(salaryExpenseTransferDocument.getEmplid());
            }
            for (Iterator i = transactionalDocument.getTargetAccountingLines().iterator(); i.hasNext();) {
                SalaryExpenseTransferAccountingLine salaryExpenseTransferAccountingLine = (SalaryExpenseTransferAccountingLine) i.next();
                salaryExpenseTransferAccountingLine.setEmplid(salaryExpenseTransferDocument.getEmplid());
            }

            processAccountingLineOverrides(transactionalDocument.getSourceAccountingLines());
            processAccountingLineOverrides(transactionalDocument.getTargetAccountingLines());
        }
    }
}
