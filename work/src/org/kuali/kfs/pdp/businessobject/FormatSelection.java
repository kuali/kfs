/*
 * Created on Sep 24, 2004
 *
 */
package org.kuali.module.pdp.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.kuali.module.pdp.bo.PhysicalCampus;


/**
 * @author jsissom
 *
 */
public class FormatSelection implements Serializable {
  PhysicalCampus campus;
  Date startDate;
  List customerList;
  List rangeList;

  public FormatSelection() {
    super();
  }

  public PhysicalCampus getCampus() {
    return campus;
  }
  public void setCampus(PhysicalCampus campus) {
    this.campus = campus;
  }
  public List getCustomerList() {
    return customerList;
  }
  public void setCustomerList(List customerList) {
    this.customerList = customerList;
  }
  public List getRangeList() {
    return rangeList;
  }
  public void setRangeList(List rangeList) {
    this.rangeList = rangeList;
  }
  public Date getStartDate() {
    return startDate;
  }
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }
}
