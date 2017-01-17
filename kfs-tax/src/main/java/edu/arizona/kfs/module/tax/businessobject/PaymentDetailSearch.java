package edu.arizona.kfs.module.tax.businessobject;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class PaymentDetailSearch extends TransientBusinessObjectBase {
    private static final long serialVersionUID = 1374286713969285382L;
    private String custPaymentDocNbr;
    private String invoiceNbr;
    private String purchaseOrderNbr;
    private String payeeName;
    private String payeeId;
    private String payeeIdTypeCd;
    private String pymtAttachment;
    private String pymtSpecialHandling;
    private String processImmediate;
    private Integer disbursementNbr;
    private BigDecimal netPaymentAmount;
    private Date beginDisbursementDate;
    private Date endDisbursementDate;
    private Date beginPaymentDate;
    private Date endPaymentDate;
    private String paymentStatusCode;
    private String disbursementTypeCode;
    private String requisitionNbr;
    private String customerInstitutionNumber;
    private Integer processId;
    private Integer paymentId;
    private String chartCode;
    private String orgCode;
    private String subUnitCode;

    public String getProcessImmediate() {
        return processImmediate;
    }

    public void setProcessImmediate(String processImmediate) {
        this.processImmediate = processImmediate;
    }

    public void setBeginDisbursementDate(Date beginDisbursementDate) {
        this.beginDisbursementDate = beginDisbursementDate;
    }

    public void setEndDisbursementDate(Date endDisbursementDate) {
        this.endDisbursementDate = endDisbursementDate;
    }

    public void setBeginPaymentDate(Date beginPaymentDate) {
        this.beginPaymentDate = beginPaymentDate;
    }

    public void setEndPaymentDate(Date endPaymentDate) {
        this.endPaymentDate = endPaymentDate;
    }

    public String getCustPaymentDocNbr() {
        return custPaymentDocNbr;
    }

    public Date getBeginDisbursementDate() {
        return beginDisbursementDate;
    }

    public Date getEndDisbursementDate() {
        return endDisbursementDate;
    }

    public Integer getDisbursementNbr() {
        return disbursementNbr;
    }

    public String getDisbursementTypeCode() {
        return disbursementTypeCode;
    }

    public String getInvoiceNbr() {
        return invoiceNbr;
    }

    public BigDecimal getNetPaymentAmount() {
        return netPaymentAmount;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public String getPayeeIdTypeCd() {
        return payeeIdTypeCd;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public Date getBeginPaymentDate() {
        return beginPaymentDate;
    }

    public Date getEndPaymentDate() {
        return endPaymentDate;
    }

    public String getPaymentStatusCode() {
        return paymentStatusCode;
    }

    public String getPurchaseOrderNbr() {
        return purchaseOrderNbr;
    }

    public String getPymtAttachment() {
        return pymtAttachment;
    }

    public void setCustPaymentDocNbr(String custPaymentDocNbr) {
        this.custPaymentDocNbr = custPaymentDocNbr;
    }

    public void setBeginDisbursementDate(Timestamp beginDisbursementDate) {
        this.beginDisbursementDate = beginDisbursementDate;
    }

    public void setEndDisbursementDate(Timestamp endDisbursementDate) {
        this.endDisbursementDate = endDisbursementDate;
    }

    public void setDisbursementNbr(Integer disbursementNbr) {
        this.disbursementNbr = disbursementNbr;
    }

    public void setDisbursementTypeCode(String disbursementTypeCode) {
        this.disbursementTypeCode = disbursementTypeCode;
    }

    public void setInvoiceNbr(String invoiceNbr) {
        this.invoiceNbr = invoiceNbr;
    }

    public void setNetPaymentAmount(BigDecimal netPaymentAmount) {
        this.netPaymentAmount = netPaymentAmount;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public void setPayeeIdTypeCd(String payeeIdTypeCd) {
        this.payeeIdTypeCd = payeeIdTypeCd;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public void setBeginPaymentDate(Timestamp beginPaymentDate) {
        this.beginPaymentDate = beginPaymentDate;
    }

    public void setEndPaymentDate(Timestamp endPaymentDate) {
        this.endPaymentDate = endPaymentDate;
    }

    public void setPaymentStatusCode(String paymentStatusCode) {
        this.paymentStatusCode = paymentStatusCode;
    }

    public void setPurchaseOrderNbr(String purchaseOrderNbr) {
        this.purchaseOrderNbr = purchaseOrderNbr;
    }

    public void setPymtAttachment(String pymtAttachment) {
        this.pymtAttachment = pymtAttachment;
    }

    public String getChartCode() {
        return chartCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public String getSubUnitCode() {
        return subUnitCode;
    }

    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public void setSubUnitCode(String subUnitCode) {
        this.subUnitCode = subUnitCode;
    }

    public String getCustomerInstitutionNumber() {
        return customerInstitutionNumber;
    }

    public String getRequisitionNbr() {
        return requisitionNbr;
    }

    public void setCustomerInstitutionNumber(String iuIdForCustomer) {
        this.customerInstitutionNumber = iuIdForCustomer;
    }

    public void setRequisitionNbr(String requisitionNbr) {
        this.requisitionNbr = requisitionNbr;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getPymtSpecialHandling() {
        return pymtSpecialHandling;
    }

    public void setPymtSpecialHandling(String pymtSpecialHandling) {
        this.pymtSpecialHandling = pymtSpecialHandling;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_DOC_NUMBER, this.custPaymentDocNbr);
        m.put(PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_NUMBER, this.invoiceNbr);
        m.put(PdpPropertyConstants.PaymentDetail.PAYMENT_PURCHASE_ORDER_NUMBER, this.purchaseOrderNbr);
        m.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYEE_NAME, this.payeeName);
        m.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYEE_ID, this.payeeId);
        m.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYEE_ID_TYPE_CODE, this.payeeIdTypeCd);
        m.put(PdpPropertyConstants.PaymentGroup.PAYMENT_ATTACHMENT, this.pymtAttachment);
        m.put(PdpPropertyConstants.PaymentGroup.PAYMENT_SPECIAL_HANDLING, this.pymtSpecialHandling);
        m.put(PdpPropertyConstants.PaymentGroup.PROCESS_IMMEDIATE, this.processImmediate);
        m.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_NBR, this.disbursementNbr.toString());
        m.put(PdpPropertyConstants.PaymentDetail.PAYMENT_NET_AMOUNT, this.netPaymentAmount.toString());
        m.put(PdpPropertyConstants.PaymentDetail.BEGIN_DISBURSEMENT_DATE, this.beginDisbursementDate.toString());
        m.put(PdpPropertyConstants.PaymentDetail.END_DISBURSEMENT_DATE, this.endDisbursementDate.toString());
        m.put(PdpPropertyConstants.PaymentDetail.BEGIN_PAYMENT_DATE, this.beginPaymentDate.toString());
        m.put(PdpPropertyConstants.PaymentDetail.END_PAYMENT_DATE, this.endPaymentDate.toString());
        m.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE, this.paymentStatusCode);
        m.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_TYPE_CODE, this.disbursementTypeCode);
        m.put(PdpPropertyConstants.PaymentDetail.PAYMENT_REQUISITION_NUMBER, this.requisitionNbr);
        m.put(PdpPropertyConstants.CUSTOMER_INSTITUTION_NUMBER, this.customerInstitutionNumber);
        m.put(PdpPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_PAYMENT_PROCESS_ID, this.processId.toString());
        m.put(PdpPropertyConstants.PAYMENT_ID, this.paymentId.toString());
        m.put(PdpPropertyConstants.CHART_CODE, this.chartCode);
        return m;
    }
}
