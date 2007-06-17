/*
 * Created on Oct 4, 2004
 *
 */
package org.kuali.module.pdp.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.kuali.module.pdp.xml.XmlHeader;


/**
 * @author jsissom
 *
 */
public class LoadPaymentStatus implements Serializable {
  private List warnings;
  private int detailCount;
  private BigDecimal detailTotal;
  private XmlHeader header;
  private Integer batchId;

  public LoadPaymentStatus() {
    super();
  }

  public LoadPaymentStatus(List w,int d,BigDecimal dt) {
    warnings = w;
    detailCount = d;
    detailTotal = dt;
  }


  public Integer getBatchId() {
    return batchId;
  }

  public void setBatchId(Integer batchId) {
    this.batchId = batchId;
  }

  public XmlHeader getHeader() {
    return header;
  }

  public void setHeader(XmlHeader header) {
    this.header = header;
  }

  public int getDetailCount() {
    return detailCount;
  }

  public void setDetailCount(int detailCount) {
    this.detailCount = detailCount;
  }

  public BigDecimal getDetailTotal() {
    return detailTotal;
  }

  public void setDetailTotal(BigDecimal detailTotal) {
    this.detailTotal = detailTotal;
  }

  public List getWarnings() {
    return warnings;
  }

  public void setWarnings(List warnings) {
    this.warnings = warnings;
  }
}
