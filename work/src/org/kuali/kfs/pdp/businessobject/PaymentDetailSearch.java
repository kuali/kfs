/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.module.pdp.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author delyea
 *
 */
public class PaymentDetailSearch implements Serializable {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailSearch.class);
  
  // INDIVIDUAL SEARCH PARMS
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
  private String iuIdForCustomer;
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
  /**
   * @param beginDisbursementDate The beginDisbursementDate to set.
   */
  public void setBeginDisbursementDate(Date beginDisbursementDate) {
    this.beginDisbursementDate = beginDisbursementDate;
  }
  /**
   * @param endDisbursementDate The endDisbursementDate to set.
   */
  public void setEndDisbursementDate(Date endDisbursementDate) {
    this.endDisbursementDate = endDisbursementDate;
  }
  /**
   * @param paymentDate The paymentDate to set.
   */
  public void setBeginPaymentDate(Date beginPaymentDate) {
    this.beginPaymentDate = beginPaymentDate;
  }
  /**
   * @param paymentDate The paymentDate to set.
   */
  public void setEndPaymentDate(Date endPaymentDate) {
    this.endPaymentDate = endPaymentDate;
  }
/**
   * @return Returns the custPaymentDocNbr.
   */
  public String getCustPaymentDocNbr() {
    return custPaymentDocNbr;
  }
  /**
   * @return Returns the disbursementDate.
   */
  public Date getBeginDisbursementDate() {
    return beginDisbursementDate;
  }
  /**
   * @return Returns the disbursementDate.
   */
  public Date getEndDisbursementDate() {
    return endDisbursementDate;
  }
  /**
   * @return Returns the disbursementNbr.
   */
  public Integer getDisbursementNbr() {
    return disbursementNbr;
  }
  /**
   * @return Returns the disbursementType.
   */
  public String getDisbursementTypeCode() {
    return disbursementTypeCode;
  }
  /**
   * @return Returns the invoiceNbr.
   */
  public String getInvoiceNbr() {
    return invoiceNbr;
  }
  /**
   * @return Returns the netPaymentAmount.
   */
  public BigDecimal getNetPaymentAmount() {
    return netPaymentAmount;
  }
  /**
   * @return Returns the payeeId.
   */
  public String getPayeeId() {
    return payeeId;
  }
  /**
   * @return Returns the payeeIdTypeCd.
   */
  public String getPayeeIdTypeCd() {
    return payeeIdTypeCd;
  }
  /**
   * @return Returns the payeeName.
   */
  public String getPayeeName() {
    return payeeName;
  }
  /**
   * @return Returns the paymentDate.
   */
  public Date getBeginPaymentDate() {
    return beginPaymentDate;
  }
  /**
   * @return Returns the paymentDate.
   */
  public Date getEndPaymentDate() {
    return endPaymentDate;
  }
/**
   * @return Returns the paymentStatus.
   */
  public String getPaymentStatusCode() {
    return paymentStatusCode;
  }
  /**
   * @return Returns the purchaseOrderNbr.
   */
  public String getPurchaseOrderNbr() {
    return purchaseOrderNbr;
  }
  /**
   * @return Returns the pymtAttachment.
   */
  public String getPymtAttachment() {
    return pymtAttachment;
  }
  /**
   * @param custPaymentDocNbr The custPaymentDocNbr to set.
   */
  public void setCustPaymentDocNbr(String custPaymentDocNbr) {
    this.custPaymentDocNbr = custPaymentDocNbr;
  }
  /**
   * @param disbursementDate The disbursementDate to set.
   */
  public void setBeginDisbursementDate(Timestamp beginDisbursementDate) {
    this.beginDisbursementDate = beginDisbursementDate;
  }
  /**
   * @param disbursementDate The disbursementDate to set.
   */
  public void setEndDisbursementDate(Timestamp endDisbursementDate) {
    this.endDisbursementDate = endDisbursementDate;
  }
/**
   * @param disbursementNbr The disbursementNbr to set.
   */
  public void setDisbursementNbr(Integer disbursementNbr) {
    this.disbursementNbr = disbursementNbr;
  }
  /**
   * @param disbursementType The disbursementType to set.
   */
  public void setDisbursementTypeCode(String disbursementTypeCode) {
    this.disbursementTypeCode = disbursementTypeCode;
  }
  /**
   * @param invoiceNbr The invoiceNbr to set.
   */
  public void setInvoiceNbr(String invoiceNbr) {
    this.invoiceNbr = invoiceNbr;
  }
  /**
   * @param netPaymentAmount The netPaymentAmount to set.
   */
  public void setNetPaymentAmount(BigDecimal netPaymentAmount) {
    this.netPaymentAmount = netPaymentAmount;
  }
  /**
   * @param payeeId The payeeId to set.
   */
  public void setPayeeId(String payeeId) {
    this.payeeId = payeeId;
  }
  /**
   * @param payeeIdTypeCd The payeeIdTypeCd to set.
   */
  public void setPayeeIdTypeCd(String payeeIdTypeCd) {
    this.payeeIdTypeCd = payeeIdTypeCd;
  }
  /**
   * @param payeeName The payeeName to set.
   */
  public void setPayeeName(String payeeName) {
    this.payeeName = payeeName;
  }
  /**
   * @param paymentDate The paymentDate to set.
   */
  public void setBeginPaymentDate(Timestamp beginPaymentDate) {
    this.beginPaymentDate = beginPaymentDate;
  }
  /**
   * @param paymentDate The paymentDate to set.
   */
  public void setEndPaymentDate(Timestamp endPaymentDate) {
    this.endPaymentDate = endPaymentDate;
  }
  /**
   * @param paymentStatus The paymentStatus to set.
   */
  public void setPaymentStatusCode(String paymentStatusCode) {
    this.paymentStatusCode = paymentStatusCode;
  }
  /**
   * @param purchaseOrderNbr The purchaseOrderNbr to set.
   */
  public void setPurchaseOrderNbr(String purchaseOrderNbr) {
    this.purchaseOrderNbr = purchaseOrderNbr;
  }
  /**
   * @param pymtAttachment The pymtAttachment to set.
   */
  public void setPymtAttachment(String pymtAttachment) {
    this.pymtAttachment = pymtAttachment;
  }
  
  /**
   * @return Returns the chartCode.
   */
  public String getChartCode() {
    return chartCode;
  }
  /**
   * @return Returns the orgCode.
   */
  public String getOrgCode() {
    return orgCode;
  }
  /**
   * @return Returns the subUnitCode.
   */
  public String getSubUnitCode() {
    return subUnitCode;
  }
  /**
   * @param chartCode The chartCode to set.
   */
  public void setChartCode(String chartCode) {
    this.chartCode = chartCode;
  }
  /**
   * @param orgCode The orgCode to set.
   */
  public void setOrgCode(String orgCode) {
    this.orgCode = orgCode;
  }
  /**
   * @param subUnitCode The subUnitCode to set.
   */
  public void setSubUnitCode(String subUnitCode) {
    this.subUnitCode = subUnitCode;
  }
  /**
   * @return Returns the iuIdForCustomer.
   */
  public String getIuIdForCustomer() {
    return iuIdForCustomer;
  }
  /**
   * @return Returns the requisitionNbr.
   */
  public String getRequisitionNbr() {
    return requisitionNbr;
  }
  /**
   * @param iuIdForCustomer The iuIdForCustomer to set.
   */
  public void setIuIdForCustomer(String iuIdForCustomer) {
    this.iuIdForCustomer = iuIdForCustomer;
  }
  /**
   * @param requisitionNbr The requisitionNbr to set.
   */
  public void setRequisitionNbr(String requisitionNbr) {
    this.requisitionNbr = requisitionNbr;
  }
  /**
   * @return Returns the processId.
   */
  public Integer getProcessId() {
    return processId;
  }
  /**
   * @param procId The processId to set.
   */
  public void setProcessId(Integer processId) {
    this.processId = processId;
  }
  /**
   * @return Returns the pymtSpecialHandling.
   */
  public String getPymtSpecialHandling() {
    return pymtSpecialHandling;
  }
  /**
   * @param pymtSpecialHandling The pymtSpecialHandling to set.
   */
  public void setPymtSpecialHandling(String pymtSpecialHandling) {
    this.pymtSpecialHandling = pymtSpecialHandling;
  }
  /**
   * @return Returns the paymentId.
   */
  public Integer getPaymentId() {
    return paymentId;
  }
  /**
   * @param paymentId The paymentId to set.
   */
  public void setPaymentId(Integer paymentId) {
    this.paymentId = paymentId;
  }
}
