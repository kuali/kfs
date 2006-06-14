/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.module.gl.bo;

import java.sql.Date;

import org.kuali.core.bo.user.Options;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers, Cornell University, Trustees of
 * Indiana University, Michigan State University Board of Trustees, Trustees of San Joaquin Delta College, University of Hawai'i,
 * The Arizona Board of Regents on behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining, using and/or copying this Original
 * Work, you agree that you have read, understand, and will comply with the terms and conditions of the Educational Community
 * License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
public interface Transaction {
    public String getAccountNumber();

    public String getFinancialBalanceTypeCode();

    public String getChartOfAccountsCode();

    public String getTransactionDebitCreditCode();

    public String getFinancialDocumentNumber();

    public Date getFinancialDocumentReversalDate();

    public String getFinancialDocumentTypeCode();

    public String getTransactionEncumbranceUpdateCode();

    public String getFinancialObjectCode();

    public String getFinancialObjectTypeCode();

    public String getOrganizationDocumentNumber();

    public String getOrganizationReferenceId();

    public String getFinancialSystemOriginationCode();

    public String getProjectCode();

    public String getReferenceFinancialDocumentNumber();

    public String getReferenceFinancialDocumentTypeCode();

    public String getReferenceFinancialSystemOriginationCode();

    public String getSubAccountNumber();

    public String getFinancialSubObjectCode();

    public Date getTransactionDate();

    public Integer getTransactionLedgerEntrySequenceNumber();

    public KualiDecimal getTransactionLedgerEntryAmount();

    public String getTransactionLedgerEntryDescription();

    public String getUniversityFiscalPeriodCode();

    public Integer getUniversityFiscalYear();

    // bo mappings
    public Chart getChart();

    public Account getAccount();

    public ObjectCode getFinancialObject();

    public BalanceTyp getBalanceType();

    public Options getOption();

    public ObjectType getObjectType();
}
