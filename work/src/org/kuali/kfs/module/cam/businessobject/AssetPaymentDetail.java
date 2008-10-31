package org.kuali.kfs.module.cam.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.kns.bo.DocumentType;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * 
 * This class...
 */
public class AssetPaymentDetail extends SourceAccountingLine {
    private static Logger LOG = Logger.getLogger(AssetPaymentDetail.class);

    private String expenditureFinancialSystemOriginationCode;
    private Date expenditureFinancialDocumentPostedDate;
    private boolean transferPaymentIndicator;

    private String expenditureFinancialDocumentNumber;
    private String expenditureFinancialDocumentTypeCode;
    private String postingPeriodCode;
    private String purchaseOrderNumber;
    private String requisitionNumber;
    private KualiDecimal amount;

    // bo references    
    private AccountingPeriod financialDocumentPostingPeriod;
    private DocumentType expenditureFinancialDocumentType;
    private OriginationCode expenditureFinancialSystemOrigination;



    /**
     * Default constructor.
     */
    public AssetPaymentDetail() {
        super();
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String,String> toStringMapper() {
        LinkedHashMap<String,String> m = new LinkedHashMap<String,String>();
        m.put("documentNumber", this.getDocumentNumber());
        m.put("sequenceNumber", this.getSequenceNumber() == null ? "" : this.getSequenceNumber().toString());
        return m;
    }

    public String getExpenditureFinancialSystemOriginationCode() {
        return expenditureFinancialSystemOriginationCode;
    }


    public void setExpenditureFinancialSystemOriginationCode(String expenditureFinancialSystemOriginationCode) {
        this.expenditureFinancialSystemOriginationCode = expenditureFinancialSystemOriginationCode;
    }


    public Date getExpenditureFinancialDocumentPostedDate() {
        return expenditureFinancialDocumentPostedDate;
    }


    public void setExpenditureFinancialDocumentPostedDate(Date expenditureFinancialDocumentPostedDate) {
        this.expenditureFinancialDocumentPostedDate = expenditureFinancialDocumentPostedDate;
    }


    public boolean isTransferPaymentIndicator() {
        return transferPaymentIndicator;
    }


    public void setTransferPaymentIndicator(boolean transferPaymentIndicator) {
        this.transferPaymentIndicator = transferPaymentIndicator;
    }


    public String getExpenditureFinancialDocumentNumber() {
        return expenditureFinancialDocumentNumber;
    }


    public void setExpenditureFinancialDocumentNumber(String expenditureFinancialDocumentNumber) {
        this.expenditureFinancialDocumentNumber = expenditureFinancialDocumentNumber;
    }


    public String getExpenditureFinancialDocumentTypeCode() {
        return expenditureFinancialDocumentTypeCode;
    }


    public void setExpenditureFinancialDocumentTypeCode(String expenditureFinancialDocumentTypeCode) {
        this.expenditureFinancialDocumentTypeCode = expenditureFinancialDocumentTypeCode;
    }
    

    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }


    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
    }


    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }


    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }


    public String getRequisitionNumber() {
        return requisitionNumber;
    }


    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }


    public AccountingPeriod getFinancialDocumentPostingPeriod() {
        return financialDocumentPostingPeriod;
    }


    public void setFinancialDocumentPostingPeriod(AccountingPeriod financialDocumentPostingPeriod) {
        this.financialDocumentPostingPeriod = financialDocumentPostingPeriod;
    }

    public DocumentType getExpenditureFinancialDocumentType() {
        return expenditureFinancialDocumentType;
    }

    
    public void setExpenditureFinancialDocumentType(DocumentType expenditureFinancialDocumentType) {
        this.expenditureFinancialDocumentType = expenditureFinancialDocumentType;
    }


    public OriginationCode getExpenditureFinancialSystemOrigination() {
        return expenditureFinancialSystemOrigination;
    }


    public void setExpenditureFinancialSystemOrigination(OriginationCode expenditureFinancialSystemOrigination) {
        this.expenditureFinancialSystemOrigination = expenditureFinancialSystemOrigination;
    }
    
    public KualiDecimal getAmount() {
        return amount;
    }


    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#getValuesMap()
     */
    @Override
    public Map getValuesMap() {
        Map simpleValues = super.getValuesMap();
        simpleValues.put(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, getExpenditureFinancialSystemOriginationCode());
        simpleValues.put(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE,getExpenditureFinancialDocumentPostedDate());

        return simpleValues;
    }    
}
