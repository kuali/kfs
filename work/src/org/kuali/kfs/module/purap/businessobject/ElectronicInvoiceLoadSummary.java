/*
 * Copyright 2006-2009 The Kuali Foundation
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
/*
 * Created on Feb 28, 2006
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ElectronicInvoiceLoadSummary extends PersistableBusinessObjectBase {
  
  private Integer invoiceLoadSummaryIdentifier;
  private String vendorDunsNumber; // this is string constant if DUNS not found
  private Integer vendorHeaderGeneratedIdentifier;
  private Integer vendorDetailAssignedIdentifier;
  private String vendorName;
  private Integer invoiceLoadSuccessCount = new Integer(0);
  private KualiDecimal invoiceLoadSuccessAmount = new KualiDecimal(0.00);
  private Integer invoiceLoadFailCount = new Integer(0);
  private KualiDecimal invoiceLoadFailAmount = new KualiDecimal(0.00);
  private Boolean isEmpty = Boolean.TRUE;
  private Timestamp fileProcessTimestamp;
  
  public ElectronicInvoiceLoadSummary() {
    super();
  }
  
  public ElectronicInvoiceLoadSummary(String vendorDunsNumber) {
    super();
    this.vendorDunsNumber = vendorDunsNumber;
  }
  
  public void addSuccessfulInvoiceOrder(KualiDecimal amount, 
                                        ElectronicInvoice eInvoice) {
    isEmpty = Boolean.FALSE;
    invoiceLoadSuccessCount = new Integer(invoiceLoadSuccessCount.intValue() + 1);
    fileProcessTimestamp = SpringContext.getBean(DateTimeService.class).getCurrentTimestamp();
    
    if (amount != null) {
      invoiceLoadSuccessAmount = invoiceLoadSuccessAmount.add(amount);
    }
    
    setupVendorInformation(eInvoice);
  }
  
  public void addFailedInvoiceOrder(KualiDecimal amount, 
                                    ElectronicInvoice eInvoice) {
    isEmpty = Boolean.FALSE;
    invoiceLoadFailCount = new Integer(invoiceLoadFailCount.intValue() + 1);
    fileProcessTimestamp = SpringContext.getBean(DateTimeService.class).getCurrentTimestamp();
    
    if (amount != null) {
      invoiceLoadFailAmount = invoiceLoadFailAmount.add(amount);
    }
    
    setupVendorInformation(eInvoice);
  }
  
  public void addFailedInvoiceOrder(ElectronicInvoice ei) {
    this.addFailedInvoiceOrder(new KualiDecimal(0),ei);
  }
  
  public void addFailedInvoiceOrder() {
    this.addFailedInvoiceOrder(new KualiDecimal(0),null);
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
  public KualiDecimal getInvoiceLoadFailAmount() {
    return invoiceLoadFailAmount;
  }

  /**
   * @param invoiceLoadFailAmount the invoiceLoadFailAmount to set
   */
  public void setInvoiceLoadFailAmount(KualiDecimal failAmount) {
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
  public Boolean isEmpty() {
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
  public KualiDecimal getInvoiceLoadSuccessAmount() {
    return invoiceLoadSuccessAmount;
  }

  /**
   * @param invoiceLoadSuccessAmount the invoiceLoadSuccessAmount to set
   */
  public void setInvoiceLoadSuccessAmount(KualiDecimal successAmount) {
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

  public Timestamp getFileProcessTimestamp() {
      return fileProcessTimestamp;
  }

  public void setFileProcessTimestamp(Timestamp fileProcessTimestamp) {
      this.fileProcessTimestamp = fileProcessTimestamp;
  }

/**
   * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
   */
  protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
      LinkedHashMap m = new LinkedHashMap();
      m.put("invoiceLoadSummaryIdentifier", this.invoiceLoadSummaryIdentifier);
      return m;
  }
}
