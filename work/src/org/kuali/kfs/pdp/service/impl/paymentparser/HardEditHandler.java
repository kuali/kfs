/*
 * Created on Jun 28, 2004
 *
 */
package org.kuali.module.pdp.xml.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.dao.CustomerProfileDao;
import org.kuali.module.pdp.dao.PaymentFileLoadDao;
import org.kuali.module.pdp.xml.PdpFileHandler;
import org.kuali.module.pdp.xml.XmlDetail;
import org.kuali.module.pdp.xml.XmlGroup;
import org.kuali.module.pdp.xml.XmlHeader;
import org.kuali.module.pdp.xml.XmlTrailer;
import org.springframework.beans.factory.BeanFactory;


/**
 * @author jsissom
 *
 */
public class HardEditHandler implements PdpFileHandler {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HardEditHandler.class);

  private CustomerProfileDao customerDao;
  private PaymentFileLoadDao loadDao;
  private CustomerProfile customer;

  private XmlHeader header;
  private XmlTrailer trailer;

  private int maxNoteLines;

  private int actualGroupCount = 0;
  private int actualPaymentCount = 0;
  private BigDecimal calculatedPaymentTotalAmount = new BigDecimal(0.00);
  private List errorMessageList = new ArrayList();

  public HardEditHandler() {
    // If you call this constructor, you need to set the dependencies mannually!
  }

  public HardEditHandler(BeanFactory bf) {
    customerDao = (CustomerProfileDao)bf.getBean("pdpCustomerProfileDao");
    loadDao = (PaymentFileLoadDao)bf.getBean("pdpPaymentFileLoadDao");
  }

  public void clear() {
    header = null;
    trailer = null;
    actualPaymentCount = 0;
    calculatedPaymentTotalAmount = new BigDecimal(0.00);
    errorMessageList.clear();
  }

  public void setMaxNoteLines(int lines) {
    maxNoteLines = lines;
  }

  public XmlHeader getHeader() {
    return header;
  }

  public XmlTrailer getTrailer() {
    return trailer;
  }

  public void setHeader(XmlHeader header) {
    this.header = header;

    // See if this customer is valid & active
    customer = customerDao.get(header.getChart(),header.getOrg(),header.getSubUnit());
    if ( customer == null ) {
      setErrorMessage("Invalid Customer: " + header.getChart() + "/" + header.getOrg() + "/" + header.getSubUnit());
    } else {
      if ( ! convert2boolean(customer.getCustomerActive()) ) {
        setErrorMessage("Customer not active: " + header.getChart() + "/" + header.getOrg() + "/" + header.getSubUnit());      
      }
    }
  }

  private boolean convert2boolean(Boolean b) {
    if ( b == null ) {
      return false;
    }
    return b.booleanValue();
  }

  public void setTrailer(XmlTrailer trailer) {
    this.trailer = trailer;

    // Only attempt this stuff if there aren't any errors
    if ( errorMessageList.size() == 0 ) {
      // If this doesn't match the number of detail segments, add
      // an error message
      if ( actualPaymentCount != trailer.getPaymentCount() ) {
        setErrorMessage("Detail Count in trailer (" + trailer.getPaymentCount() + ") does not match number of detail segments (" + actualPaymentCount + ")");
      }

      // If this doesn't match the amount in the detail segments, add
      // an error message
      if ( trailer.getPaymentTotalAmount().compareTo(getCalculatedPaymentTotalAmount()) != 0 )  {
        setErrorMessage("Detail Total Amount in trailer (" + trailer.getPaymentTotalAmount() + ") does not match total of detail segments (" + getCalculatedPaymentTotalAmount() + ")");
      } else {
        // Check to see if this is a duplicate batch
        Timestamp now = new Timestamp(header.getCreationDate().getTime());

        if ( loadDao.isDuplicateBatch(customer,new Integer(trailer.getPaymentCount()),trailer.getPaymentTotalAmount(),now) ) {
          LOG.error("setPaymentTotalAmount() duplicate batch uploaded");
          setErrorMessage("Duplicate Batch");
        }
      }
    }
  }

  public int getActualPaymentCount() {
    return actualPaymentCount;
  }

  public BigDecimal getCalculatedPaymentTotalAmount() {
    return calculatedPaymentTotalAmount;
  }

  public void setGroup(XmlGroup item) {
    BigDecimal groupTotal = new BigDecimal(0);

    int noteLineCount = 0;
    actualGroupCount++;
    for (Iterator iter = item.getDetail().iterator(); iter.hasNext();) {
      XmlDetail detail = (XmlDetail) iter.next();

      noteLineCount++;  // Add a line to print the invoice number
      noteLineCount = noteLineCount + detail.getPayment_text().size();

      // Add up the payment count & total payment amount
      actualPaymentCount++;
      calculatedPaymentTotalAmount = calculatedPaymentTotalAmount.add(detail.getAccountTotal());

      LOG.debug("setGroup() actualPaymentCount: " + actualPaymentCount + " Accounts: " + detail.getAccounting().size());

      if ( (detail.getNet_payment_amt() == null) && ( ! detail.isDetailAmountProvided()) ) {
        // set net amount to accounting segments
        detail.setNet_payment_amt(detail.getAccountTotal());
      } else if ( (detail.getNet_payment_amt() == null) && ( detail.isDetailAmountProvided()) ) {
        // set net amount to calculated detail amount
        detail.setNet_payment_amt(detail.getCalculatedPaymentAmount());

        // compare net to accounting segments
        if ( detail.getAccountTotal().compareTo(detail.getNet_payment_amt()) != 0 ) {
          setErrorMessage("Detail " + actualPaymentCount + " account total " + detail.getAccountTotal() + " is not equal to net payment " + detail.getNet_payment_amt());
        }
      } else if ( (detail.getNet_payment_amt() != null) && ( ! detail.isDetailAmountProvided()) ) {
        // compare net to accounting segments
        if ( detail.getAccountTotal().compareTo(detail.getNet_payment_amt()) != 0 ) {
          setErrorMessage("Detail " + actualPaymentCount + " account total " + detail.getAccountTotal() + " is not equal to net payment " + detail.getNet_payment_amt());
        }
      } else {
        // compare net to accounting segments
        if ( detail.getAccountTotal().compareTo(detail.getNet_payment_amt()) != 0 ) {
          setErrorMessage("Detail " + actualPaymentCount + " account total " + detail.getAccountTotal() + " is not equal to net payment " + detail.getNet_payment_amt());
        }
        
        // THIS WAS REMOVED FOR EPIC
        // The PREQ section was sending original invoice amounts that might or might not work with this validation check
        // compare  calculation to net
//        if ( detail.getCalculatedPaymentAmount().compareTo(detail.getNet_payment_amt()) != 0 ) {
//          setErrorMessage("Detail " + actualPaymentCount + " calculated total " + detail.getCalculatedPaymentAmount() + " is not equal to net payment " + detail.getNet_payment_amt());
//        }
      }
      groupTotal = groupTotal.add(detail.getNet_payment_amt());
    }

    if ( groupTotal.doubleValue() < 0 ) {
      setErrorMessage("Group #" + actualGroupCount + " total is less than 0");
    }

    // Now check that the number of detail items and note lines will fit on a check stub
    if ( noteLineCount > maxNoteLines ) {
      setErrorMessage("Group #" + actualGroupCount + " total note lines required is " + noteLineCount + " which is greater than " + maxNoteLines);
    }
  }

  public void setErrorMessage(String message) {
    errorMessageList.add(message);
  }

  public List getErrorMessages() {
    return errorMessageList;
  }

  public void setFilename(String filename) {
    // Unneeded
  }

  public void setUser(PdpUser u) {
    // Unneeded
  }
  
  public void setPaymentFileLoadDao(PaymentFileLoadDao pfld) {
    this.loadDao = pfld;
  }
  
  public void setCustomerProfileDao(CustomerProfileDao cpd) {
    this.customerDao = cpd;
  }
}
