/*
 * Created on Jul 27, 2004
 *
 */
package org.kuali.module.pdp.xml;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author jsissom
 *
 */
public class XmlHeader implements Serializable {
  private String chart;
  private String org;
  private String subUnit;
  private Date creationDate;
  
  public XmlHeader() {
    super();
  }

  public void setField(String name,String value) {
    if ( "chart".equals(name) ) {
      setChart(value.toUpperCase());
    } else if ( "organization".equals(name) ) {
      setOrg(value);
    } else if ( "sub_unit".equals(name) ) {
      setSubUnit(value);
    } else if ( "creation_date".equals(name) ) {
      try {
        StringBuffer chars = new StringBuffer(value);

        // Get rid of the T
        chars.setCharAt(10,' ');
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date d = sdf.parse(chars.toString());
        setCreationDate(d);
      } catch (ParseException e) {
        // Not sure what to do
      }
    }
  }

  /**
   * @return Returns the chart.
   */
  public String getChart() {
    return chart;
  }
  /**
   * @param chart The chart to set.
   */
  public void setChart(String chart) {
    this.chart = chart;
  }
  /**
   * @return Returns the creationDate.
   */
  public Date getCreationDate() {
    return creationDate;
  }
  /**
   * @param creationDate The creationDate to set.
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }
  /**
   * @return Returns the org.
   */
  public String getOrg() {
    return org;
  }
  /**
   * @param org The org to set.
   */
  public void setOrg(String org) {
    this.org = org;
  }
  /**
   * @return Returns the subUnit.
   */
  public String getSubUnit() {
    return subUnit;
  }
  /**
   * @param subUnit The subUnit to set.
   */
  public void setSubUnit(String subUnit) {
    this.subUnit = subUnit;
  }
}
