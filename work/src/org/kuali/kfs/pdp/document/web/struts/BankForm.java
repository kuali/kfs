/*
 * Created on Sep 3, 2004
 *
 */
package org.kuali.module.pdp.form.bank;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.module.pdp.bo.Bank;


/**
 * @author jsissom
 *
 */
public class BankForm extends ActionForm {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankForm.class);

  private Integer id;
  private Integer version;
  private String description;
  private String name;
  private String routingNumber;
  private String accountNumber;
  private Boolean active;
  private String disbursementTypeCode;

  public BankForm() {
    super();
  }

  public BankForm(Bank b) {
    super();
    id = b.getId();
    version = b.getVersion();
    description = b.getDescription();
    name = b.getName();
    routingNumber = b.getRoutingNumber();
    accountNumber = b.getAccountNumber();
    active = b.getActive();
    if ( b.getDisbursementType() != null ) {
      disbursementTypeCode = b.getDisbursementType().getCode();
    }
  }

  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    ActionErrors ae = new ActionErrors();

    String b = "none";
    Enumeration e = request.getParameterNames();
    while ( e.hasMoreElements() ) {
      String name = (String)e.nextElement();
      if ( name.startsWith("btn") ) {
        b = name;
        break;
      }
    }
    
    if ( b.startsWith("btnSave") ) {
      if ((description == null) || (description.length() == 0)) {
        ae.add("description", new ActionMessage("bankform.missing.description"));
      }
      if ((name == null) || (name.length() == 0)) {
        ae.add("name", new ActionMessage("bankform.missing.name"));
      }
      if ((accountNumber == null) || (accountNumber.length() == 0)) {
        ae.add("accountNumber", new ActionMessage("bankform.missing.accountNumber"));
      }
      if ((routingNumber == null) || (routingNumber.length() == 0)) {
        ae.add("routingNumber", new ActionMessage("bankform.missing.routingNumber"));
      }
    }

    return ae;
  }

  public String getAccountNumber() {
    return accountNumber;
  }
  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }
  public Boolean getActive() {
    return active;
  }
  public void setActive(Boolean active) {
    this.active = active;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getDisbursementTypeCode() {
    return disbursementTypeCode;
  }
  public void setDisbursementTypeCode(String disbursementTypeCode) {
    this.disbursementTypeCode = disbursementTypeCode;
  }
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getRoutingNumber() {
    return routingNumber;
  }
  public void setRoutingNumber(String routingNumber) {
    this.routingNumber = routingNumber;
  }
  public Integer getVersion() {
    return version;
  }
  public void setVersion(Integer version) {
    this.version = version;
  }
}
