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
 * The interface defines the methods used to generate Customer Invoice PDFs, Customer Credit Memo PDFs
 * and Customer Statements.
 */
public interface AccountsReceivableReportService {

    /**
     * This method generates a Customer Invoice PDF.
     *
     * @param invoice document used to generate the PDF.
     * @return File invoice PDF
     */
    public File generateInvoice(CustomerInvoiceDocument invoice);

    /**
     * This method generates Customer Invoice PDFs by billing chart and org
     * and optionally by the date the invoice doc was created.
     *
     * @param chartCode
     * @param orgCode
     * @param date
     * @return List<File> invoice PDFs
     */
    public List<File> generateInvoicesByBillingOrg(String chartCode, String orgCode, Date date);

    /**
     * This method generates Customer Invoice PDFs by processing chart and org
     * and optionally by the date the invoice doc was created.
     *
     * @param chartCode
     * @param orgCode
     * @param date
     * @return List<File> invoice PDFs
     */
    public List<File> generateInvoicesByProcessingOrg(String chartCode, String orgCode, Date date);

    /**
     * This method generates Customer Invoice PDFs by initiator
     * and optionally by the date the invoice doc was created.
     *
     * @param initiator
     * @param date
     * @return List<File> invoice PDFs
     */
    public List<File> generateInvoicesByInitiator(String initiator, java.sql.Date date);

    /**
     * This method generates a Customer Credit Memo PDF for a particular credit memo document.
     *
     * @param creditMemo
     * @return File credit memo PDF
     * @throws WorkflowException
     */
    public File generateCreditMemo(CustomerCreditMemoDocument creditMemo) throws WorkflowException;

    /**
     * This method generates detailed or summary (depending on format parameter) statement reports for the billing chart/org.
     *
     * @param chartCode
     * @param orgCode
     * @param statementFormat either detailed or summary
     * @param incldueZeroBalanceCustomers
     * @return List<CustomerStatementResultHolder> statements
     */
    public List<CustomerStatementResultHolder> generateStatementByBillingOrg(String chartCode, String orgCode, String statementFormat, String incldueZeroBalanceCustomers);

    /**
     * This method generates detailed or summary (depending on format parameter) statement reports for the account.
     *
     * @param accountNumber
     * @param statementFormat either detailed or summary
     * @param incldueZeroBalanceCustomers
     * @return List<CustomerStatementResultHolder> statements
     */
    public List<CustomerStatementResultHolder> generateStatementByAccount(String accountNumber, String statementFormat, String incldueZeroBalanceCustomers);

    /**
     * This method generates detailed or summary (depending on format parameter) statement reports for the customer.
     *
     * @param customerNumber
     * @param statementFormat either detailed or summary
     * @param incldueZeroBalanceCustomers
     * @return List<CustomerStatementResultHolder> statements
     */
    public List<CustomerStatementResultHolder> generateStatementByCustomer(String customerNumber, String statementFormat, String incldueZeroBalanceCustomers);

}
