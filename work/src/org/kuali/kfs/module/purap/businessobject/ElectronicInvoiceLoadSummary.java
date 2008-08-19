/*
 * Created on Feb 28, 2006
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author delyea
 *
 */
public class ElectronicInvoiceLoadSummary extends PersistableBusinessObjectBase {
  
  // NOT NULL FIELDS
  private Integer invoiceLoadSummaryIdentifier;
  private String vendorDunsNumber; // this is string constant if DUNS not found
  private Integer vendorHeaderGeneratedIdentifier;
  private Integer vendorDetailAssignedIdentifier;
  private String vendorName;
  private Integer invoiceLoadSuccessCount = new Integer(0);
  private BigDecimal invoiceLoadSuccessAmount = new BigDecimal(0.00);
  private Integer invoiceLoadFailCount = new Integer(0);
  private BigDecimal invoiceLoadFailAmount = new BigDecimal(0.00);
  private Boolean isEmpty = Boolean.TRUE;
  private Timestamp fileProcessDate;
  
  /**
   * 
   */
  public ElectronicInvoiceLoadSummary() {
    super();
  }
  
  public ElectronicInvoiceLoadSummary(String vendorDunsNumber) {
    super();
    this.vendorDunsNumber = vendorDunsNumber;
  }
  
  public ElectronicInvoiceLoadSummary(Integer id, String vendorDunsNumber) {
    super();
    this.invoiceLoadSummaryIdentifier = id;
    this.vendorDunsNumber = vendorDunsNumber;
  }
  
  public void addSuccessfulInvoiceOrder(BigDecimal amount, 
                                        ElectronicInvoice eInvoice) {
    isEmpty = Boolean.FALSE;
    invoiceLoadSuccessCount = new Integer(invoiceLoadSuccessCount.intValue() + 1);
    fileProcessDate = new Timestamp((new Date()).getTime());
    
    if (amount != null) {
      invoiceLoadSuccessAmount = invoiceLoadSuccessAmount.add(amount);
    }
    
    setupVendorInformation(eInvoice);
  }
  
  public void addFailedInvoiceOrder(BigDecimal amount, 
                                    ElectronicInvoice eInvoice) {
    isEmpty = Boolean.FALSE;
    invoiceLoadFailCount = new Integer(invoiceLoadFailCount.intValue() + 1);
    fileProcessDate = new Timestamp((new Date()).getTime());
    
    if (amount != null) {
      invoiceLoadFailAmount = invoiceLoadFailAmount.add(amount);
    }
    
    setupVendorInformation(eInvoice);
  }
  
  public void addFailedInvoiceOrder(ElectronicInvoice ei) {
    this.addFailedInvoiceOrder(new BigDecimal(0),ei);
  }
  
  public void addFailedInvoiceOrder() {
    this.addFailedInvoiceOrder(new BigDecimal(0),null);
  }

  private void setupVendorInformation(ElectronicInvoice eInvoice) {
      
    if (eInvoice != null && 
        getVendorHeaderGeneratedIdentifier() == null && 
        getVendorDetailAssignedIdentifier() == null) {
        
        setVendorHeaderGeneratedIdentifier(eInvoice.getVendorHeaderID());
        setVendorDetailAssignedIdentifier(eInvoice.getVendorDetailID());
        setVendorName(eInvoice.getVendorName());
        
    }
  }
  
  public String getVendorDescriptor() {
    String kualiDescriptor = null;
    if ( (this.vendorName != null) && (this.vendorHeaderGeneratedIdentifier != null) && (this.vendorDetailAssignedIdentifier != null) ) {
      kualiDescriptor = "  (Kuali Match:  " + this.vendorName + "  ~  " + vendorHeaderGeneratedIdentifier + "-" + vendorDetailAssignedIdentifier + ")";
    } else if ( (this.vendorHeaderGeneratedIdentifier != null) && (this.vendorDetailAssignedIdentifier != null) ) {
      kualiDescriptor = "  (Kuali Match:  " + vendorHeaderGeneratedIdentifier + "-" + vendorDetailAssignedIdentifier + ")";
    } else if (this.vendorName != null) {
      kualiDescriptor = "  (Kuali Match:  " + this.vendorName + ")";
    }
    return this.getVendorDunsNumber() + ((kualiDescriptor != null) ? kualiDescriptor : "");
  }

  /**
   * @return the vendorDetailAssignedIdentifier
   */
  public Integer getVendorDetailAssignedIdentifier() {
    return vendorDetailAssignedIdentifier;
  }

  /**
   * @param vendorDetailAssignedIdentifier the vendorDetailAssignedIdentifier to set
   */
  public void setVendorDetailAssignedIdentifier(Integer kualiVendorDetailId) {
    this.vendorDetailAssignedIdentifier = kualiVendorDetailId;
  }

  /**
   * @return the vendorHeaderGeneratedIdentifier
   */
  public Integer getVendorHeaderGeneratedIdentifier() {
    return vendorHeaderGeneratedIdentifier;
  }

  /**
   * @param vendorHeaderGeneratedIdentifier the vendorHeaderGeneratedIdentifier to set
   */
  public void setVendorHeaderGeneratedIdentifier(Integer kualiVendorHeaderId) {
    this.vendorHeaderGeneratedIdentifier = kualiVendorHeaderId;
  }

  /**
   * @return the vendorName
   */
  public String getVendorName() {
    return vendorName;
  }

  /**
   * @param vendorName the vendorName to set
   */
  public void setVendorName(String kualiVendorName) {
    this.vendorName = kualiVendorName;
  }
  
  /**
   * @return the invoiceLoadFailAmount
   */
  public BigDecimal getInvoiceLoadFailAmount() {
    return invoiceLoadFailAmount;
  }

  /**
   * @param invoiceLoadFailAmount the invoiceLoadFailAmount to set
   */
  public void setInvoiceLoadFailAmount(BigDecimal failAmount) {
    this.invoiceLoadFailAmount = failAmount;
  }
  
  /**
   * @return the invoiceLoadFailCount
   */
  public Integer getInvoiceLoadFailCount() {
    return invoiceLoadFailCount;
  }
  
  /**
   * @param invoiceLoadFailCount the invoiceLoadFailCount to set
   */
  public void setInvoiceLoadFailCount(Integer failCount) {
    this.invoiceLoadFailCount = failCount;
  }

  /**
   * @return the invoiceLoadSummaryIdentifier
   */
  public Integer getInvoiceLoadSummaryIdentifier() {
    return invoiceLoadSummaryIdentifier;
  }

  /**
   * @param invoiceLoadSummaryIdentifier the invoiceLoadSummaryIdentifier to set
   */
  public void setInvoiceLoadSummaryIdentifier(Integer id) {
    this.invoiceLoadSummaryIdentifier = id;
  }

  /**
   * @return the isEmpty
   */
  public Boolean getIsEmpty() {
    return isEmpty;
  }

  /**
   * @param isEmpty the isEmpty to set
   */
  public void setIsEmpty(Boolean isEmpty) {
    this.isEmpty = isEmpty;
  }

  /**
   * @return the invoiceLoadSuccessAmount
   */
  public BigDecimal getInvoiceLoadSuccessAmount() {
    return invoiceLoadSuccessAmount;
  }

  /**
   * @param invoiceLoadSuccessAmount the invoiceLoadSuccessAmount to set
   */
  public void setInvoiceLoadSuccessAmount(BigDecimal successAmount) {
    this.invoiceLoadSuccessAmount = successAmount;
  }

  /**
   * @return the invoiceLoadSuccessCount
   */
  public Integer getInvoiceLoadSuccessCount() {
    return invoiceLoadSuccessCount;
  }

  /**
   * @param invoiceLoadSuccessCount the invoiceLoadSuccessCount to set
   */
  public void setInvoiceLoadSuccessCount(Integer successCount) {
    this.invoiceLoadSuccessCount = successCount;
  }

  /**
   * @return the vendorDunsNumber
   */
  public String getVendorDunsNumber() {
    return vendorDunsNumber;
  }

  /**
   * @param vendorDunsNumber the vendorDunsNumber to set
   */
  public void setVendorDunsNumber(String vendorDunsNumber) {
    this.vendorDunsNumber = vendorDunsNumber;
  }

  public Timestamp getFileProcessDate() {
      return fileProcessDate;
  }

  public void setFileProcessDate(Timestamp fileProcessDate) {
      this.fileProcessDate = fileProcessDate;
  }

/**
   * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
   */
  protected LinkedHashMap toStringMapper() {
      LinkedHashMap m = new LinkedHashMap();
      m.put("invoiceLoadSummaryIdentifier", this.invoiceLoadSummaryIdentifier);
      return m;
  }
}
