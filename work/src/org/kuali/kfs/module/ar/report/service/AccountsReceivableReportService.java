/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.report.service;


import java.io.File;
import java.sql.Date;
import java.util.List;

import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.report.util.CustomerStatementResultHolder;
import org.kuali.rice.kew.api.exception.WorkflowException;

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
   
   
   public List<CustomerStatementResultHolder> generateStatementByBillingOrg(String chartCode, String orgCode, String statementFormat, String incldueZeroBalanceCustomers);

   public List<CustomerStatementResultHolder> generateStatementByAccount(String accountNumber, String statementFormat, String incldueZeroBalanceCustomers);

   public List<CustomerStatementResultHolder> generateStatementByCustomer(String customerNumber, String statementFormat, String incldueZeroBalanceCustomers);
   
}
