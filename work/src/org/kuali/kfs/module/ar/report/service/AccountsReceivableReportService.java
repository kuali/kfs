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
package org.kuali.kfs.module.ar.report.service;


import java.io.File;
import java.sql.Date;
import java.util.List;

import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.rice.kew.exception.WorkflowException;

/**
 * The interface defines the methods that extract Labor Ledger records of the employees who were paid on a grant or cost shared
 * during the selected reporting period.
 */
public interface AccountsReceivableReportService {

   public File generateInvoice(CustomerInvoiceDocument invoice);
   
   public List<File> generateInvoicesByBillingOrg(String chartCode, String orgCode, Date date);
   
   public List<File> generateInvoicesByProcessingOrg(String chartCode, String orgCode, Date date);
   
   public List<File> generateInvoicesByInitiator(String initiator, java.sql.Date date);
   
   
   public File generateCreditMemo(CustomerCreditMemoDocument creditMemo) throws WorkflowException;
   
   
   public List<File> generateStatementByBillingOrg(String chartCode, String orgCode);

   public List<File> generateStatementByAccount(String accountNumber);

   public List<File> generateStatementByCustomer(String customerNumber);
   
}
