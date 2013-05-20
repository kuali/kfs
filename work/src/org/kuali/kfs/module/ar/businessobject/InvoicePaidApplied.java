/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.NonAppliedDistribution;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class InvoicePaidApplied extends PersistableBusinessObjectBase {

    private String documentNumber; // document the payment is being applied FROM
    private Integer paidAppliedItemNumber;
    private String financialDocumentReferenceInvoiceNumber; // document the payment is being applied TO
    private Integer invoiceItemNumber;
    private Integer universityFiscalYear;
    private String universityFiscalPeriodCode;
    private KualiDecimal invoiceItemAppliedAmount = KualiDecimal.ZERO;

    private CustomerInvoiceDetail invoiceDetail;
    private AccountingPeriod universityFiscalPeriod;
    private FinancialSystemDocumentHeader documentHeader;
    transient private DocumentService documentService;
    private KualiDecimal paidAppiedDistributionAmount = KualiDecimal.ZERO;
    private Collection<NonInvoicedDistribution> nonInvoicedDistributions;
    private Collection<NonAppliedDistribution> nonAppliedDistributions;
    private transient CustomerInvoiceDocument customerInvoiceDocument;

    /**
     * Default constructor.
     */
    public InvoicePaidApplied() {
    }

    public InvoicePaidApplied(String documentNumber, String refInvoiceDocNumber, Integer invoiceSequenceNumber, KualiDecimal appliedAmount, Integer paidAppliedItemNumber, Integer universityFiscalYear, String universityFiscalPeriodCode) {
        this.documentNumber = documentNumber;
        this.financialDocumentReferenceInvoiceNumber = refInvoiceDocNumber;
        this.invoiceItemNumber = invoiceSequenceNumber;
        this.paidAppliedItemNumber = paidAppliedItemNumber;
        this.invoiceItemAppliedAmount = appliedAmount;
        this.universityFiscalYear = universityFiscalYear;
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    /**
     * Constructs a InvoicePaidApplied object, and assumes the current Fiscal Year and FiscalPeriodCode.
     * 
     * @param documentNumber
     * @param refInvoiceDocNumber
     * @param invoiceSequenceNumber
     * @param appliedAmount
     * @param paidAppliedItemNumber
     */
    public InvoicePaidApplied(String documentNumber, String refInvoiceDocNumber, Integer invoiceSequenceNumber, KualiDecimal appliedAmount, Integer paidAppliedItemNumber) {
        this.documentNumber = documentNumber;
        this.financialDocumentReferenceInvoiceNumber = refInvoiceDocNumber;
        this.invoiceItemNumber = invoiceSequenceNumber;
        this.paidAppliedItemNumber = paidAppliedItemNumber;
        this.invoiceItemAppliedAmount = appliedAmount;

        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        this.universityFiscalYear = universityDateService.getCurrentFiscalYear();
        this.universityFiscalPeriodCode = universityDateService.getCurrentUniversityDate().getAccountingPeriod().getUniversityFiscalPeriodCode();
    }

    public DocumentService getDocumentService() {
        if (null == documentService) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        CustomerInvoiceDocument _customerInvoiceDocument = null;
        try {
            _customerInvoiceDocument = (CustomerInvoiceDocument) getDocumentService().getByDocumentHeaderId(getFinancialDocumentReferenceInvoiceNumber());
        }
        catch (WorkflowException e) {
            throw new RuntimeException("WorkflowException thrown when trying to retrieve Invoice document [" + getFinancialDocumentReferenceInvoiceNumber() + "]", e);
        }
        return _customerInvoiceDocument;
    }

    public ObjectCode getAccountsReceivableObjectCode() {

        // make sure its all re-connected with child objects
        getInvoiceDetail().refresh();
        getInvoiceDetail().refreshNonUpdateableReferences();

        String parameterName = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD;
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String parameterValue = parameterService.getParameterValueAsString(CustomerInvoiceDocument.class, parameterName);

        ObjectCode objectCode = null;
        if ("1".equals(parameterValue) || "2".equals(parameterValue)) {
            getInvoiceDetail().refreshReferenceObject("objectCode");
            objectCode = getInvoiceDetail().getObjectCode();
        }
        else if ("3".equals(parameterValue)) {
            CustomerInvoiceDocument customerInvoiceDocument = getInvoiceDetail().getCustomerInvoiceDocument();
            customerInvoiceDocument.refreshReferenceObject("paymentFinancialObject");
            objectCode = getInvoiceDetail().getCustomerInvoiceDocument().getPaymentFinancialObject();
        }
        else {
            throw new RuntimeException("No AR ObjectCode was available for this InvoicePaidApplied, which should never happen.");
        }

        return objectCode;
    }

    public SystemInformation getSystemInformation() {
        String processingOrgCode = getCustomerInvoiceDocument().getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        String processingChartCode = getCustomerInvoiceDocument().getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();

        SystemInformationService sysInfoService = SpringContext.getBean(SystemInformationService.class);
        Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        SystemInformation systemInformation = sysInfoService.getByProcessingChartOrgAndFiscalYear(processingChartCode, processingOrgCode, currentFiscalYear);

        if (systemInformation == null) {
            throw new RuntimeException("The InvoicePaidApplied doesnt have an associated SystemInformation.  This should never happen.");
        }
        return systemInformation;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the paidAppliedItemNumber attribute.
     * 
     * @return Returns the paidAppliedItemNumber
     */
    public Integer getPaidAppliedItemNumber() {
        return paidAppliedItemNumber;
    }

    /**
     * Sets the paidAppliedItemNumber attribute.
     * 
     * @param paidAppliedItemNumber The paidAppliedItemNumber to set.
     */
    public void setPaidAppliedItemNumber(Integer paidAppliedItemNumber) {
        this.paidAppliedItemNumber = paidAppliedItemNumber;
    }


    /**
     * Gets the financialDocumentReferenceInvoiceNumber attribute.
     * 
     * @return Returns the financialDocumentReferenceInvoiceNumber
     */
    public String getFinancialDocumentReferenceInvoiceNumber() {
        return financialDocumentReferenceInvoiceNumber;
    }

    /**
     * Sets the financialDocumentReferenceInvoiceNumber attribute.
     * 
     * @param financialDocumentReferenceInvoiceNumber The financialDocumentReferenceInvoiceNumber to set.
     */
    public void setFinancialDocumentReferenceInvoiceNumber(String financialDocumentReferenceInvoiceNumber) {
        this.financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber;
    }

    /**
     * Gets the invoiceItemNumber attribute.
     * 
     * @return Returns the invoiceItemNumber
     */
    public Integer getInvoiceItemNumber() {
        return invoiceItemNumber;
    }

    /**
     * Sets the invoiceItemNumber attribute.
     * 
     * @param invoiceItemNumber The invoiceItemNumber to set.
     */
    public void setInvoiceItemNumber(Integer invoiceItemNumber) {
        this.invoiceItemNumber = invoiceItemNumber;
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the universityFiscalPeriodCode attribute.
     * 
     * @return Returns the universityFiscalPeriodCode
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode attribute.
     * 
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
     * Gets the invoiceItemAppliedAmount attribute.
     * 
     * @return Returns the invoiceItemAppliedAmount
     */
    public KualiDecimal getInvoiceItemAppliedAmount() {
        return invoiceItemAppliedAmount;
    }

    /**
     * Sets the invoiceItemAppliedAmount attribute.
     * 
     * @param invoiceItemAppliedAmount The invoiceItemAppliedAmount to set.
     */
    public void setInvoiceItemAppliedAmount(KualiDecimal invoiceItemAppliedAmount) {
        this.invoiceItemAppliedAmount = invoiceItemAppliedAmount;
    }

    /**
     * Gets the invoiceItem attribute.
     * 
     * @return Returns the invoiceItem
     */
    public CustomerInvoiceDetail getInvoiceDetail() {
        return invoiceDetail;
    }

    /**
     * Gets the universityFiscalPeriod attribute.
     * 
     * @return Returns the universityFiscalPeriod
     */
    public AccountingPeriod getUniversityFiscalPeriod() {
        return universityFiscalPeriod;
    }

    /**
     * Sets the universityFiscalPeriod attribute.
     * 
     * @param universityFiscalPeriod The universityFiscalPeriod to set.
     * @deprecated
     */
    public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
        this.universityFiscalPeriod = universityFiscalPeriod;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.paidAppliedItemNumber != null) {
            m.put("paidAppliedItemNumber", this.paidAppliedItemNumber.toString());
        }
        return m;
    }

    /**
     * Get the paidAppiedDistributionAmount attribute.
     * 
     * @return Returns the paidAppiedDistributionAmount
     */
    public KualiDecimal getPaidAppiedDistributionAmount() {
        return paidAppiedDistributionAmount;
    }

    /**
     * Set the paidAppiedDistributionAmount attribute.
     * 
     * @param paidAppiedDistributionAmount The paidAppiedDistributionAmount to set.
     */
    public void setPaidAppiedDistributionAmount(KualiDecimal paidAppiedDistributionAmount) {
        this.paidAppiedDistributionAmount = paidAppiedDistributionAmount;
    }

    /**
     * Gets the nonInvoicedDistributions attribute.
     * 
     * @return Returns the nonInvoicedDistributions.
     */
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributions() {
        return nonInvoicedDistributions;
    }

    /**
     * Sets the nonInvoicedDistributions attribute value.
     * 
     * @param nonInvoicedDistributions The nonInvoicedDistributions to set.
     */
    public void setNonInvoicedDistributions(Collection<NonInvoicedDistribution> nonInvoicedDistributions) {
        this.nonInvoicedDistributions = nonInvoicedDistributions;
    }

    /**
     * Gets the nonAppliedDistributions attribute.
     * 
     * @return Returns the nonAppliedDistributions.
     */
    public Collection<NonAppliedDistribution> getNonAppliedDistributions() {
        return nonAppliedDistributions;
    }

    /**
     * Sets the nonAppliedDistributions attribute value.
     * 
     * @param nonAppliedDistributions The nonAppliedDistributions to set.
     */
    public void setNonAppliedDistributions(List<NonAppliedDistribution> nonAppliedDistributions) {
        this.nonAppliedDistributions = nonAppliedDistributions;
    }

    /**
     * Sets the customerInvoiceDocument attribute value.
     * 
     * @param customerInvoiceDocument The customerInvoiceDocument to set.
     */
    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }
}
