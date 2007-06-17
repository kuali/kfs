/*
 * Created on Jul 16, 2004
 *
 */
package org.kuali.module.pdp.xml.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.ProjectCodeService;
import org.kuali.module.chart.service.SubAccountService;
import org.kuali.module.chart.service.SubObjectCodeService;
import org.kuali.module.pdp.bo.AccountingChange;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.Code;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentAccountDetail;
import org.kuali.module.pdp.bo.PaymentAccountHistory;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentNoteText;
import org.kuali.module.pdp.bo.PaymentStatus;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.dao.CustomerProfileDao;
import org.kuali.module.pdp.dao.PaymentFileLoadDao;
import org.kuali.module.pdp.dao.ReferenceDao;
import org.kuali.module.pdp.xml.PdpFileHandler;
import org.kuali.module.pdp.xml.XmlAccounting;
import org.kuali.module.pdp.xml.XmlDetail;
import org.kuali.module.pdp.xml.XmlGroup;
import org.kuali.module.pdp.xml.XmlHeader;
import org.kuali.module.pdp.xml.XmlTrailer;
import org.springframework.beans.factory.BeanFactory;


/**
 * @author jsissom
 *  
 */
public class DataLoadHandler implements PdpFileHandler {
  private static Logger LOG = Logger.getLogger(DataLoadHandler.class);

  private static String HELD_TAX_EMPLOYEE_CD = "HTXE";
  private static String HELD_TAX_NRA_CD = "HTXN";
  private static String HELD_TAX_NRA_EMPL_CD = "HTXB";
  private static String OPEN_CD = "OPEN";
  
  // Injected services
  private AccountService accountService;
  private SubAccountService subAccountService;
  private ObjectCodeService objectCodeService;
  private SubObjectCodeService subObjectCodeService;
  private ProjectCodeService projectCodeService;
  private PaymentFileLoadDao loadDao;
  private CustomerProfileDao customerDao;
  private ReferenceDao referenceDao;

  private Map acctgChngCds;
  private PaymentStatus openStatus;
  private PaymentStatus heldForNRA;
  private PaymentStatus heldForEmployee;
  private PaymentStatus heldForNRAEmployee;

  private CustomerProfile customer = null;
  private Batch batch;

  // Data from the XML
  private XmlHeader header;
  private XmlTrailer trailer;

  private List errors = new ArrayList();
  private boolean abort = false;
  private String filename;
  private PdpUser user;
  private int count = 0;
  
  private boolean taxEmailRequired = false;

  private Timestamp now;
  private Calendar nowPlus30;
  private Calendar nowMinus30;
  
  // ADDED for Threshold E-Mail
  private Boolean detailThreshold;
  private List detailThresholdMessages;
  private Boolean fileThreshold;
  private String fileThresholdMessage;
  //

  public DataLoadHandler(BeanFactory bf,XmlTrailer t) {
      customerDao = (CustomerProfileDao)bf.getBean("pdpCustomerProfileDao");
      loadDao = (PaymentFileLoadDao)bf.getBean("pdpPaymentFileLoadDao");
      referenceDao = (ReferenceDao)bf.getBean("pdpReferenceDao");
      accountService = (AccountService)bf.getBean("accountService");
      subAccountService = (SubAccountService)bf.getBean("subAccountService");
      objectCodeService = (ObjectCodeService)bf.getBean("objectCodeService");
      subObjectCodeService = (SubObjectCodeService)bf.getBean("subObjectCodeService");
      projectCodeService = (ProjectCodeService)bf.getBean("projectCodeService");
      fileThreshold = new Boolean(false);
      detailThreshold = new Boolean(false);
      detailThresholdMessages = new ArrayList();

      trailer = t;
  }

  public void clear() {
    header = null;
    trailer = null;
    errors.clear();
    abort = false;
    batch = null;
    filename = null;
    user = null;
    count = 0;
  }

  public Boolean getDetailThreshold() {
    return detailThreshold;
  }
  public Boolean getFileThreshold() {
    return fileThreshold;
  }
  public void setDetailThreshold(Boolean detailThreshold) {
    this.detailThreshold = detailThreshold;
  }
  public void setFileThreshold(Boolean fileThreshold) {
    this.fileThreshold = fileThreshold;
  }
  public List getDetailThresholdMessages() {
    return detailThresholdMessages;
  }
  public String getFileThresholdMessage() {
    return fileThresholdMessage;
  }
  public void setDetailThresholdMessages(List detailThresholdMessages) {
    this.detailThresholdMessages = detailThresholdMessages;
  }
  public void setFileThresholdMessage(String fileThresholdMessage) {
    this.fileThresholdMessage = fileThresholdMessage;
  }
  public void setMaxNoteLines(int lines) {
    // Not needed
  }

  // Manually set
  public void setFilename(String f) {
    filename = f;
  }

  // Manually set
  public void setUser(PdpUser u) {
    user = u;
  }

  public XmlHeader getHeader() {
    return header;
  }

  public XmlTrailer getTrailer() {
    return trailer;
  }

  public void setHeader(XmlHeader header) {
    this.header = header;

    // Get the customer
    customer = customerDao.get(header.getChart(), header.getOrg(), header.getSubUnit());
    if (customer == null) {
      LOG.error("setCreationDate() File uploaded with invalid customer: " + header.getChart() + "/" + header.getOrg() + "/" + header.getSubUnit());
      setErrorMessage("Invalid Customer: " + header.getChart() + "/" + header.getOrg() + "/" + header.getSubUnit());
      abort = true;
      return;
    }

    // Create a batch
    now = new Timestamp(new Date().getTime()); 

    nowPlus30 = Calendar.getInstance();
    nowPlus30.setTime(now);
    nowPlus30.add(Calendar.DATE,30);

    nowMinus30 = Calendar.getInstance();    
    nowMinus30.setTime(now);
    nowMinus30.add(Calendar.DATE,-30);

    Integer iCount = new Integer(trailer.getPaymentCount());

    batch = new Batch();
    batch.setCustomerProfile(customer);
    batch.setCustomerFileCreateTimestamp(new Timestamp(header.getCreationDate().getTime()));
    batch.setFileProcessTimestamp(now);
    batch.setPaymentCount(iCount);
    if (filename.length() > 30) {
      batch.setPaymentFileName(filename.substring(0, 30));
    } else {
      batch.setPaymentFileName(filename);
    }
    batch.setPaymentTotalAmount(trailer.getPaymentTotalAmount());
    batch.setSubmiterUser(user);
    loadDao.createBatch(batch);

    // Load all the accounting change codes
    acctgChngCds = referenceDao.getAllMap("AccountingChange");
    openStatus = (PaymentStatus)referenceDao.getCode("PaymentStatus",OPEN_CD);
    heldForEmployee = (PaymentStatus)referenceDao.getCode("PaymentStatus",HELD_TAX_EMPLOYEE_CD);
    heldForNRA = (PaymentStatus)referenceDao.getCode("PaymentStatus",HELD_TAX_NRA_CD);
    heldForNRAEmployee = (PaymentStatus)referenceDao.getCode("PaymentStatus",HELD_TAX_NRA_EMPL_CD);
    
  }

  public void setTrailer(XmlTrailer trailer) {
    this.trailer = trailer;
    if ( trailer.getPaymentTotalAmount().compareTo(customer.getFileThresholdAmount()) > 0 ) {
      setErrorMessage("The total amount of this file (" + trailer.getPaymentTotalAmount() + ") is greater than the threshold amount (" + customer.getFileThresholdAmount() + ")");
      fileThreshold = new Boolean(true);
      setFileThresholdMessage("The total amount of this file (" + trailer.getPaymentTotalAmount() + ") is greater than the threshold amount (" + customer.getFileThresholdAmount() + ")");
    }
  }

  public void setGroup(XmlGroup group) {
    if (abort) {
      return; // Don't process detail if the header wasn't right
    }

    PaymentGroup pg = group.getPaymentGroup();

    pg.setBatch(batch);
    pg.setPaymentStatus(openStatus);
    pg.setPayeeName(pg.getPayeeName().toUpperCase());
    
    // Set defaults for missing information
    if ( pg.getCombineGroups() ==  null ) {
      pg.setCombineGroups(Boolean.TRUE);
    }
    if ( pg.getCampusAddress() == null ) {
      pg.setCampusAddress(Boolean.FALSE);
    }
    if ( pg.getPymtAttachment() == null ) {
      pg.setPymtAttachment(Boolean.FALSE);
    }
    if ( pg.getPymtSpecialHandling() == null ) {
      pg.setPymtSpecialHandling(Boolean.FALSE);
    }
    if ( pg.getProcessImmediate() == null ) {
      pg.setProcessImmediate(Boolean.FALSE);
    }
    if ( pg.getIuEmployee() == null ) {
      pg.setIuEmployee(Boolean.FALSE);
    }
    if ( pg.getNraPayment() == null ) {
      pg.setNraPayment(Boolean.FALSE);
    }
    if ( pg.getTaxablePayment() == null ) {
      pg.setTaxablePayment(Boolean.FALSE);
    }
    
    // Tax Group Requirements for automatic Holding
    if (customer.getNraReview().booleanValue() && customer.getEmployeeCheck().booleanValue() && 
        pg.getIuEmployee().booleanValue() && pg.getNraPayment().booleanValue()){
      if (heldForNRAEmployee != null){
        pg.setPaymentStatus(heldForNRAEmployee);
        this.setTaxEmailRequired(true);
      }
    } else if (customer.getEmployeeCheck().booleanValue() && pg.getIuEmployee().booleanValue()){
      if (heldForEmployee != null){
        pg.setPaymentStatus(heldForEmployee);
        this.setTaxEmailRequired(true);
      }
    } else if (customer.getNraReview().booleanValue() && pg.getNraPayment().booleanValue()){
      if (heldForNRA != null){
        pg.setPaymentStatus(heldForNRA);
        this.setTaxEmailRequired(true);
      }
    }

    processGroupSoftEdits(pg);

    for (Iterator iter = group.getDetail().iterator(); iter.hasNext();) {
      XmlDetail item = (XmlDetail) iter.next();

      count++;
      LOG.debug("setGroup() Detail Count " + count);

      // Fill in missing amounts if necessary
      item.updateAmounts();

      PaymentDetail detail = item.getPaymentDetail();
      detail.setPaymentGroup(pg);
      pg.addPaymentDetails(detail);

      processDetailSoftEdits(detail);

      for (Iterator i = item.getAccounting().iterator(); i.hasNext();) {
        XmlAccounting acct = (XmlAccounting) i.next();
        PaymentAccountDetail pad = acct.getPaymentAccountDetail();
        pad.setPaymentDetail(detail);

        processAccountSoftEdits(pad,now);
        detail.addAccountDetail(pad);
      }

      int noteCount = 1;
      for (Iterator i = item.getPayment_text().iterator(); i.hasNext();) {
        String note = (String) i.next();

        PaymentNoteText pnt = new PaymentNoteText();
        pnt.setPaymentDetail(detail);
        pnt.setCustomerNoteLineNbr(new Integer(noteCount));
        pnt.setCustomerNoteText(note);
        detail.addNote(pnt);

        noteCount++;
      }
    }

    loadDao.createGroup(pg);
  }

  private void processGroupSoftEdits(PaymentGroup group) {
    // Check payment date
    if ( group.getPaymentDate() != null ) {
      Calendar payDate = Calendar.getInstance();
      payDate.setTime(group.getPaymentDate());

      if ( payDate.before(nowMinus30) ) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        errors.add("Payment date of " + sdf.format(group.getPaymentDate()) + " is more than 30 days ago");
      }
      if ( payDate.after(nowPlus30) ) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        errors.add("Payment date of " + sdf.format(group.getPaymentDate()) + " is more than 30 days in the future");
      }
    } else {
      group.setPaymentDate(now);
    }
  }

  private void processDetailSoftEdits(PaymentDetail detail) {
//  BELOW LINE REPLACED DUE TO JIRA ISSUE KULPDP-33
//  BigDecimal testAmount = detail.getCalculatedPaymentAmount();
    // Check net payment amount
    BigDecimal testAmount = detail.getNetPaymentAmount();
    if (testAmount.compareTo(customer.getPaymentThresholdAmount()) > 0) {
      errors.add("Detail amount " + testAmount + " is greater than threshold");
      detailThreshold = new Boolean(true);
      detailThresholdMessages.add("Payment Detail to " + detail.getPaymentGroup().getPayeeName() + " with amount " + testAmount + "\n");
    }

    // Set invoice date if it doesn't exist
    if ( detail.getInvoiceDate() == null ) {
      detail.setInvoiceDate(now);
    }
  }

  private PaymentAccountHistory newAccountHistory(String attName, String newValue, String oldValue, Code changeCode) {
    PaymentAccountHistory pah = new PaymentAccountHistory();
    pah.setAcctAttributeName(attName);
    pah.setAcctAttributeNewValue(newValue);
    pah.setAcctAttributeOrigValue(oldValue);
    pah.setAcctChangeDate(now);
    pah.setAccountingChange((AccountingChange) changeCode);
    return pah;
  }

  private void replaceAccountingString(Code objChangeCd, List changeRecords, PaymentAccountDetail pad) {
    changeRecords.add(newAccountHistory("fin_coa_cd", customer.getDefaultChartCode(), pad.getFinChartCode(),
        objChangeCd));
    changeRecords.add(newAccountHistory("account_nbr", customer.getDefaultAccountNumber(), pad.getAccountNbr(),
        objChangeCd));
    changeRecords.add(newAccountHistory("sub_acct_nbr", customer.getDefaultSubAccountNumber(), pad.getSubAccountNbr(), objChangeCd));
    changeRecords.add(newAccountHistory("fin_object_cd", customer.getDefaultObjectCode(), pad.getFinObjectCode(),
        objChangeCd));
    changeRecords.add(newAccountHistory("fin_sub_obj_cd", customer.getDefaultSubObjectCode(), pad.getFinSubObjectCode(),
        objChangeCd));

    pad.setFinChartCode(customer.getDefaultChartCode());
    pad.setAccountNbr(customer.getDefaultAccountNumber());
    pad.setSubAccountNbr(customer.getDefaultSubAccountNumber());
    pad.setFinObjectCode(customer.getDefaultObjectCode());
    pad.setFinSubObjectCode(customer.getDefaultSubObjectCode());
  }

  private void processAccountSoftEdits(PaymentAccountDetail pad,Timestamp now) {
    List changeRecords = new ArrayList();
    boolean replacement = false;

    // Lookup accounting info in the FIS
    if (customer.getAccountingEditRequired().booleanValue()) {
      // Check account number
      Account acct = accountService.getByPrimaryId(pad.getFinChartCode(), pad.getAccountNbr());
      if ( acct ==  null ) {
        // Put in account number from customer profile
        errors.add("Account number " + pad.getFinChartCode() + "-" + pad.getAccountNbr()
            + " is not valid. Replaced with default accounting string");

        Code objChangeCd = (Code) acctgChngCds.get("ACCT");
        replaceAccountingString(objChangeCd, changeRecords, pad);
        replacement = true;
      }

      if ( ! replacement ) {
        // Check sub account code
        if (pad.getSubAccountNbr() != null) {
          SubAccount sa = subAccountService.getByPrimaryId(pad.getFinChartCode(), pad.getAccountNbr(), pad.getSubAccountNbr());
          if ( sa == null ) {
            // Get rid of sub account
            errors.add("Sub Account code " + pad.getFinChartCode() + "-" + pad.getAccountNbr() + "-"
                + pad.getSubAccountNbr() + " is invalid.  Removed");

            changeRecords.add(newAccountHistory("sub_acct_nbr", "-----", pad.getSubAccountNbr(), (Code) acctgChngCds
                .get("SA")));
            pad.setSubAccountNbr("-----");
          }
        }
      }

      if ( ! replacement ) {
        // Check object code
        ObjectCode oc = objectCodeService.getByPrimaryIdForCurrentYear(pad.getFinChartCode(), pad.getFinObjectCode());
        if ( oc == null ) {
          // Put in object from customer profile
          errors.add("Object code " + pad.getFinChartCode() + "-" + pad.getFinObjectCode() + " is invalid. Replaced with default accounting string");

          Code objChangeCd = (Code) acctgChngCds.get("OBJ");
          replaceAccountingString(objChangeCd, changeRecords, pad);
          replacement = true;
        }
      }

      if ( ! replacement ) {
        // Check sub object code
        if (pad.getFinSubObjectCode() != null) {
          SubObjCd soc = subObjectCodeService.getByPrimaryIdForCurrentYear(pad.getFinChartCode(), pad.getAccountNbr(), pad.getFinObjectCode(), pad.getFinSubObjectCode());
          if ( soc == null ) {
            // Get rid of sub object
            errors.add("Sub Object code " + pad.getFinChartCode() + "-" + pad.getAccountNbr() + "-" + pad.getFinObjectCode() + "-" + pad.getFinSubObjectCode() + " is invalid.  Removed");

            changeRecords.add(newAccountHistory("fin_subobj_cd", "---", pad.getFinSubObjectCode(), (Code) acctgChngCds.get("SO")));
            pad.setFinSubObjectCode("---");
          }
        }
      }

      if ( ! replacement ) {
        // Check project code
        if (pad.getProjectCode() != null) {
          ProjectCode pc = projectCodeService.getByName(pad.getProjectCode());
          if (pc == null) {
            // Get rid of project
            errors.add("Project code " + pad.getProjectCode() + " is invalid.  Removed");

            changeRecords.add(newAccountHistory("project_cd", "----------", pad.getProjectCode(), (Code) acctgChngCds.get("PROJ")));

            pad.setProjectCode("----------");
          }
        }
      }
    }

    // Change nulls into ---'s for the fields that need it
    if ( pad.getFinSubObjectCode() == null ) {
      pad.setFinSubObjectCode("---");
    }
    if ( pad.getSubAccountNbr() == null ) {
      pad.setSubAccountNbr("-----");
    }
    if ( pad.getProjectCode() == null ) {
      pad.setProjectCode("----------");
    }

    // Add them all to the payment detail
    for (Iterator i = changeRecords.iterator(); i.hasNext();) {
      PaymentAccountHistory pah = (PaymentAccountHistory) i.next();

      pah.setPaymentAccountDetail(pad);
      pad.addAccountHistory(pah);
    }
  }

  public void setErrorMessage(String message) {
    errors.add(message);
  }

  public List getErrorMessages() {
    return errors;
  }
  
  public Batch getBatch() {
    return batch;
  }
  /**
   * @return Returns the taxEmailRequired.
   */
  public boolean isTaxEmailRequired() {
    return taxEmailRequired;
  }
  /**
   * @param taxEmailRequired The taxEmailRequired to set.
   */
  public void setTaxEmailRequired(boolean taxEmailRequired) {
    this.taxEmailRequired = taxEmailRequired;
  }
}