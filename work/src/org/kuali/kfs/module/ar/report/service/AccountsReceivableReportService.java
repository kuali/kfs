/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
