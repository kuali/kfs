/*
 * Created on Jun 22, 2004
 *
 */
package org.kuali.module.pdp.xml;

import java.io.Serializable;
import java.math.BigDecimal;

import org.kuali.module.pdp.bo.PaymentAccountDetail;


/**
 * @author jsissom
 *
 */
public class XmlAccounting implements Serializable {
  private String coa_cd;
  private String account_nbr;
  private String sub_account_nbr;
  private String object_cd;
  private String sub_object_cd;
  private String org_ref_id;
  private String project_cd;
  private BigDecimal amount;

  public XmlAccounting() {
  }

  public void setField(String name,String value) {
    // Don't need to set an empty value
    if ( (value == null) || (value.length() == 0) ) {
      return;
    }

    if ( "coa_cd".equals(name) ) {
      setCoa_cd(value.toUpperCase());
    } else if ("account_nbr".equals(name) ) {
      setAccount_nbr(value);
    } else if ("sub_account_nbr".equals(name) ) {
      setSub_account_nbr(value);
    } else if ("object_cd".equals(name) ) {
      setObject_cd(value);
    } else if ("sub_object_cd".equals(name) ) {
      setSub_object_cd(value);
    } else if ("org_ref_id".equals(name) ) {
      setOrg_ref_id(value);
    } else if ("project_cd".equals(name) ) {
      setProject_cd(value);
    } else if ("amount".equals(name) ) {
      setAmount(new BigDecimal(value.trim()));
    }
  }

  public PaymentAccountDetail getPaymentAccountDetail() {
    PaymentAccountDetail pad = new PaymentAccountDetail();
    pad.setFinChartCode(coa_cd);
    pad.setAccountNbr(account_nbr);
    pad.setSubAccountNbr(sub_account_nbr);
    pad.setFinObjectCode(object_cd);
    pad.setFinSubObjectCode(sub_object_cd);
    pad.setOrgReferenceId(org_ref_id);
    pad.setProjectCode(project_cd);
    pad.setAccountNetAmount(amount);
    return pad;
  }

  /**
   * @return Returns the account_nbr.
   */
  public String getAccount_nbr() {
    return account_nbr;
  }

  /**
   * @param account_nbr The account_nbr to set.
   */
  public void setAccount_nbr(String account_nbr) {
    this.account_nbr = account_nbr;
  }
  
  /**
   * @return Returns the amount.
   */
  public BigDecimal getAmount() {
    return amount;
  }

  /**
   * @param amount The amount to set.
   */
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
  
  /**
   * @return Returns the coa_cd.
   */
  public String getCoa_cd() {
    return coa_cd;
  }
  
  /**
   * @param coa_cd The coa_cd to set.
   */
  public void setCoa_cd(String coa_cd) {
    this.coa_cd = coa_cd;
  }
  
  /**
   * @return Returns the object_cd.
   */
  public String getObject_cd() {
    return object_cd;
  }
  
  /**
   * @param object_cd The object_cd to set.
   */
  public void setObject_cd(String object_cd) {
    this.object_cd = object_cd;
  }
  
  /**
   * @return Returns the org_ref_id.
   */
  public String getOrg_ref_id() {
    return org_ref_id;
  }
  
  /**
   * @param org_ref_id The org_ref_id to set.
   */
  public void setOrg_ref_id(String org_ref_id) {
    this.org_ref_id = org_ref_id;
  }
  
  /**
   * @return Returns the project_cd.
   */
  public String getProject_cd() {
    return project_cd;
  }
  
  /**
   * @param project_cd The project_cd to set.
   */
  public void setProject_cd(String project_cd) {
    this.project_cd = project_cd;
  }
  
  /**
   * @return Returns the sub_account_nbr.
   */
  public String getSub_account_nbr() {
    return sub_account_nbr;
  }
  
  /**
   * @param sub_account_nbr The sub_account_nbr to set.
   */
  public void setSub_account_nbr(String sub_account_nbr) {
    this.sub_account_nbr = sub_account_nbr;
  }
  
  /**
   * @return Returns the sub_object_cd.
   */
  public String getSub_object_cd() {
    return sub_object_cd;
  }
  
  /**
   * @param sub_object_cd The sub_object_cd to set.
   */
  public void setSub_object_cd(String sub_object_cd) {
    this.sub_object_cd = sub_object_cd;
  }
}
