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

package org.kuali.module.effort.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;

/**
 * Effort Certification Document Class.
 */
public class EffortCertificationDocument extends PersistableBusinessObjectBase {
    private String documentNumber;
    private String chartOfAccountsCode;
    private String organizationCode;
    private String a21LaborReportNumber;
    private Date a21LaborReportPrintedDate;
    private Date a21LaborReportApprovedDate;
    private String a21LaborDocumentCode;
    private Integer a21LaborFiscalYear;
    private String emplid;

    private DocumentHeader financialDocument;
    private Chart chartOfAccounts;
    private EffortCertificationReportDefinition a21LaborReport;
    private Org org;

    private List a21DetailLines;
    
    /**
     * Default constructor.
     */
    public EffortCertificationDocument() {
        a21DetailLines = new ArrayList(); 
        
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     * 
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     * 
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     * 
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the a21LaborReportNumber attribute.
     * 
     * @return Returns the a21LaborReportNumber
     * 
     */
    public String getA21LaborReportNumber() {
        return a21LaborReportNumber;
    }

    /**
     * Sets the a21LaborReportNumber attribute.
     * 
     * @param a21LaborReportNumber The a21LaborReportNumber to set.
     * 
     */
    public void setA21LaborReportNumber(String a21LaborReportNumber) {
        this.a21LaborReportNumber = a21LaborReportNumber;
    }


    /**
     * Gets the a21LaborReportPrintedDate attribute.
     * 
     * @return Returns the a21LaborReportPrintedDate
     * 
     */
    public Date getA21LaborReportPrintedDate() {
        return a21LaborReportPrintedDate;
    }

    /**
     * Sets the a21LaborReportPrintedDate attribute.
     * 
     * @param a21LaborReportPrintedDate The a21LaborReportPrintedDate to set.
     * 
     */
    public void setA21LaborReportPrintedDate(Date a21LaborReportPrintedDate) {
        this.a21LaborReportPrintedDate = a21LaborReportPrintedDate;
    }


    /**
     * Gets the a21LaborReportApprovedDate attribute.
     * 
     * @return Returns the a21LaborReportApprovedDate
     * 
     */
    public Date getA21LaborReportApprovedDate() {
        return a21LaborReportApprovedDate;
    }

    /**
     * Sets the a21LaborReportApprovedDate attribute.
     * 
     * @param a21LaborReportApprovedDate The a21LaborReportApprovedDate to set.
     * 
     */
    public void setA21LaborReportApprovedDate(Date a21LaborReportApprovedDate) {
        this.a21LaborReportApprovedDate = a21LaborReportApprovedDate;
    }


    /**
     * Gets the a21LaborDocumentCode attribute.
     * 
     * @return Returns the a21LaborDocumentCode
     * 
     */
    public String getA21LaborDocumentCode() {
        return a21LaborDocumentCode;
    }

    /**
     * Sets the a21LaborDocumentCode attribute.
     * 
     * @param a21LaborDocumentCode The a21LaborDocumentCode to set.
     * 
     */
    public void setA21LaborDocumentCode(String a21LaborDocumentCode) {
        this.a21LaborDocumentCode = a21LaborDocumentCode;
    }


    /**
     * Gets the a21LaborFiscalYear attribute.
     * 
     * @return Returns the a21LaborFiscalYear
     * 
     */
    public Integer getA21LaborFiscalYear() {
        return a21LaborFiscalYear;
    }

    /**
     * Sets the a21LaborFiscalYear attribute.
     * 
     * @param a21LaborFiscalYear The a21LaborFiscalYear to set.
     * 
     */
    public void setA21LaborFiscalYear(Integer a21LaborFiscalYear) {
        this.a21LaborFiscalYear = a21LaborFiscalYear;
    }


    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid
     * 
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute.
     * 
     * @param emplid The emplid to set.
     * 
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }


    /**
     * Gets the financialDocument attribute.
     * 
     * @return Returns the financialDocument
     * 
     */
    public DocumentHeader getFinancialDocument() {
        return financialDocument;
    }

    /**
     * Sets the financialDocument attribute.
     * 
     * @param financialDocument The financialDocument to set.
     * @deprecated
     */
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the a21LaborReport attribute.
     * 
     * @return Returns the a21LaborReport
     * 
     */
    public EffortCertificationReportDefinition getA21LaborReport() {
        return a21LaborReport;
    }

    /**
     * Sets the a21LaborReport attribute.
     * 
     * @param a21LaborReport The a21LaborReport to set.
     * @deprecated
     */
    public void setA21LaborReport(EffortCertificationReportDefinition a21LaborReport) {
        this.a21LaborReport = a21LaborReport;
    }

    /**
     * Gets the org attribute.
     * 
     * @return Returns the org.
     */
    public Org getOrg() {
        return org;
    }

    /**
     * Sets the org attribute value.
     * 
     * @param org The org to set.
     * @deprecated
     */
    public void setOrg(Org org) {
        this.org = org;
    }

    /**
     * Gets the a21DetailLines attribute. 
     * @return Returns the a21DetailLines.
     */
    public List getA21DetailLines() {
        return a21DetailLines;
    }

    /**
     * Sets the a21DetailLines attribute value.
     * @param detailLines The a21DetailLines to set.
     */
    public void setA21DetailLines(List detailLines) {
        a21DetailLines = detailLines;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }


}
