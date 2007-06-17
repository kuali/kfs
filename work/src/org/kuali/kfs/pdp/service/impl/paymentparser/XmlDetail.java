package org.kuali.module.pdp.xml;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.kuali.module.pdp.bo.PaymentDetail;


public class XmlDetail implements Serializable {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(XmlDetail.class);

  private String source_doc_nbr = null;
  private String invoice_nbr = null;
  private String po_nbr = null;
  private String req_nbr = null;
  private String org_doc_nbr = null;
  private String fdoc_typ_cd = null;
  private Date invoice_date = null;
  private BigDecimal orig_invoice_amt = null;
  private BigDecimal net_payment_amt = null;
  private BigDecimal invoice_tot_discount_amt = null;
  private BigDecimal invoice_tot_ship_amt = null;
  private BigDecimal invoice_tot_other_debits = null;
  private BigDecimal invoice_tot_other_credits = null;
  private List accounting;
  private List payment_text;

  public XmlDetail() {
    accounting = new ArrayList();
    payment_text = new ArrayList();
  }

  public void setField(String name,String value) {
    // Don't need to set an empty value
    if ( (value == null) || (value.length() == 0) ) {
      return;
    }

    if ( "source_doc_nbr".equals(name) ) {
      setSource_doc_nbr(value);
    } else if ( "invoice_nbr".equals(name) ) {
      setInvoice_nbr(value);
    } else if ( "po_nbr".equals(name) ) {
      setPo_nbr(value);
    } else if ( "req_nbr".equals(name) ) {
      setReq_nbr(value);
    } else if ( "org_doc_nbr".equals(name) ) {
      setOrg_doc_nbr(value);
    } else if ( "fdoc_typ_cd".equals(name) ) {
      setFdoc_typ_cd(value);
    } else if ( "invoice_date".equals(name) ) {
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        setInvoice_date(sdf.parse(value));
      } catch (ParseException e) {
        // Don't know what to do
      }      
    } else if ("orig_invoice_amt".equals(name) ) {
      setOrig_invoice_amt(new BigDecimal(value.trim()));
    } else if ("net_payment_amt".equals(name) ) {
      setNet_payment_amt(new BigDecimal(value.trim()));
    } else if ("invoice_tot_discount_amt".equals(name) ) {
      setInvoice_tot_discount_amt(new BigDecimal(value.trim()));
    } else if ("invoice_tot_ship_amt".equals(name) ) {
      setInvoice_tot_ship_amt(new BigDecimal(value.trim()));
    } else if ("invoice_tot_other_debits".equals(name) ) {
      setInvoice_tot_other_debits(new BigDecimal(value.trim()));
    } else if ("invoice_tot_other_credits".equals(name) ) {
      setInvoice_tot_other_credits(new BigDecimal(value.trim()));
    }
  }

  public void updateAmounts() {
    BigDecimal zero = new BigDecimal(0.00);

    if ( invoice_tot_discount_amt == null ) {
      setInvoice_tot_discount_amt(zero);
    }
    if ( invoice_tot_ship_amt == null ) {
      setInvoice_tot_ship_amt(zero);
    }
    if ( invoice_tot_other_debits == null ) {
      setInvoice_tot_other_debits(zero);
    }
    if ( invoice_tot_other_credits == null ) {
      setInvoice_tot_other_credits(zero);
    }

    // Update the total payment amount with the amount
    // from the accounts if it's not there
    if ( getNet_payment_amt() == null ) {
      setNet_payment_amt(this.getAccountTotal());
    }
    
    if ( orig_invoice_amt == null ) {
      BigDecimal amt = net_payment_amt;
      amt = amt.add(invoice_tot_discount_amt);
      amt = amt.subtract(invoice_tot_ship_amt);
      amt = amt.subtract(invoice_tot_other_debits);
      amt = amt.add(invoice_tot_other_credits);
      setOrig_invoice_amt(amt);
    }
  }

  public boolean isDetailAmountProvided() {
    return ! ( (orig_invoice_amt == null) && (invoice_tot_discount_amt == null) && (invoice_tot_ship_amt == null) &&
    (invoice_tot_other_debits == null) && (invoice_tot_other_credits == null) );
  }

  public BigDecimal getCalculatedPaymentAmount() {
    BigDecimal zero = new BigDecimal(0.00);
    BigDecimal orig_invoice_amt = getOrig_invoice_amt() == null ? zero : getOrig_invoice_amt();
    BigDecimal invoice_tot_discount_amt = getInvoice_tot_discount_amt() == null ? zero : getInvoice_tot_discount_amt();
    BigDecimal invoice_tot_ship_amt = getInvoice_tot_ship_amt() == null ? zero : getInvoice_tot_ship_amt();
    BigDecimal invoice_tot_other_debits = getInvoice_tot_other_debits() == null ? zero : getInvoice_tot_other_debits();
    BigDecimal invoice_tot_other_credits = getInvoice_tot_other_credits() == null ? zero : getInvoice_tot_other_credits();

    BigDecimal t = orig_invoice_amt.subtract(invoice_tot_discount_amt);
    t = t.add(invoice_tot_ship_amt);
    t = t.add(invoice_tot_other_debits);
    t = t.subtract(invoice_tot_other_credits);
    
    return t;
  }

  public BigDecimal getAccountTotal() {
    BigDecimal acctTotal = new BigDecimal(0.00);

    Iterator i = accounting.iterator();
    while ( i.hasNext() ) {
      XmlAccounting acct = (XmlAccounting)i.next();
      if ( acct.getAmount() != null ) {
        acctTotal = acctTotal.add(acct.getAmount());
      }
    }
    
    return acctTotal;
  }

  public PaymentDetail getPaymentDetail() {
    PaymentDetail d = new PaymentDetail();

    d.setCustPaymentDocNbr(source_doc_nbr);
    d.setInvoiceNbr(invoice_nbr);
    d.setPurchaseOrderNbr(po_nbr);
    d.setRequisitionNbr(req_nbr);
    d.setOrganizationDocNbr(org_doc_nbr);
    d.setFinancialDocumentTypeCode(fdoc_typ_cd);
    if ( invoice_date != null ) {
      d.setInvoiceDate(new Timestamp(invoice_date.getTime()));
    } else {
      d.setInvoiceDate(null);
    }
    d.setOrigInvoiceAmount(orig_invoice_amt);
    d.setNetPaymentAmount(net_payment_amt);
    d.setInvTotDiscountAmount(invoice_tot_discount_amt);
    d.setInvTotShipAmount(invoice_tot_ship_amt);
    d.setInvTotOtherCreditAmount(invoice_tot_other_credits);
    d.setInvTotOtherDebitAmount(invoice_tot_other_debits);
    return d;
  }

  public Date getInvoice_date() {
    return invoice_date;
  }
  public void setInvoice_date(Date invoice_date) {
    this.invoice_date = invoice_date;
  }
  /**
   * @return Returns the accounting.
   */
  public List getAccounting() {
    return accounting;
  }
  
  public void addAccounting(XmlAccounting a) {
    accounting.add(a);
  }

  /**
   * @param accounting The accounting to set.
   */
  public void setAccounting(List accounting) {
    this.accounting = accounting;
  }

  /**
   * @return Returns the invoice_nbr.
   */
  public String getInvoice_nbr() {
    return invoice_nbr;
  }
  /**
   * @param invoice_nbr The invoice_nbr to set.
   */
  public void setInvoice_nbr(String invoice_nbr) {
    this.invoice_nbr = invoice_nbr;
  }
  /**
   * @return Returns the invoice_tot_discount_amt.
   */
  public BigDecimal getInvoice_tot_discount_amt() {
    return invoice_tot_discount_amt;
  }
  /**
   * @param invoice_tot_discount_amt The invoice_tot_discount_amt to set.
   */
  public void setInvoice_tot_discount_amt(BigDecimal invoice_tot_discount_amt) {
    this.invoice_tot_discount_amt = invoice_tot_discount_amt;
  }
  /**
   * @return Returns the invoice_tot_other_credits.
   */
  public BigDecimal getInvoice_tot_other_credits() {
    return invoice_tot_other_credits;
  }
  /**
   * @param invoice_tot_other_credits The invoice_tot_other_credits to set.
   */
  public void setInvoice_tot_other_credits(BigDecimal invoice_tot_other_credits) {
    this.invoice_tot_other_credits = invoice_tot_other_credits;
  }
  /**
   * @return Returns the invoice_tot_other_debits.
   */
  public BigDecimal getInvoice_tot_other_debits() {
    return invoice_tot_other_debits;
  }
  /**
   * @param invoice_tot_other_debits The invoice_tot_other_debits to set.
   */
  public void setInvoice_tot_other_debits(BigDecimal invoice_tot_other_debits) {
    this.invoice_tot_other_debits = invoice_tot_other_debits;
  }
  /**
   * @return Returns the invoice_tot_ship_amt.
   */
  public BigDecimal getInvoice_tot_ship_amt() {
    return invoice_tot_ship_amt;
  }
  /**
   * @param invoice_tot_ship_amt The invoice_tot_ship_amt to set.
   */
  public void setInvoice_tot_ship_amt(BigDecimal invoice_tot_ship_amt) {
    this.invoice_tot_ship_amt = invoice_tot_ship_amt;
  }
  /**
   * @return Returns the net_payment_amt.
   */
  public BigDecimal getNet_payment_amt() {
    return net_payment_amt;
  }
  /**
   * @param net_payment_amt The net_payment_amt to set.
   */
  public void setNet_payment_amt(BigDecimal net_payment_amt) {
    this.net_payment_amt = net_payment_amt;
  }
  /**
   * @return Returns the org_doc_nbr.
   */
  public String getOrg_doc_nbr() {
    return org_doc_nbr;
  }
  /**
   * @param org_doc_nbr The org_doc_nbr to set.
   */
  public void setOrg_doc_nbr(String org_doc_nbr) {
    this.org_doc_nbr = org_doc_nbr;
  }
  /**
   * @return Returns the fdoc_typ_cd.
   */
  public String getFdoc_typ_cd() {
    return fdoc_typ_cd;
  }
  /**
   * @param fdoc_typ_cd The fdoc_typ_cd to set.
   */
  public void setFdoc_typ_cd(String fdoc_typ_cd) {
    this.fdoc_typ_cd = fdoc_typ_cd;
  }
  /**
   * @return Returns the orig_invoice_amt.
   */
  public BigDecimal getOrig_invoice_amt() {
    return orig_invoice_amt;
  }
  /**
   * @param orig_invoice_amt The orig_invoice_amt to set.
   */
  public void setOrig_invoice_amt(BigDecimal orig_invoice_amt) {
    this.orig_invoice_amt = orig_invoice_amt;
  }
  /**
   * @return Returns the payment_text.
   */
  public List getPayment_text() {
    return payment_text;
  }
  
  public void addPayment_text(String text) {
    payment_text.add(text);
  }

  /**
   * @param payment_text The payment_text to set.
   */
  public void setPayment_text(List payment_text) {
    this.payment_text = payment_text;
  }
  /**
   * @return Returns the po_nbr.
   */
  public String getPo_nbr() {
    return po_nbr;
  }
  /**
   * @param po_nbr The po_nbr to set.
   */
  public void setPo_nbr(String po_nbr) {
    this.po_nbr = po_nbr;
  }
  /**
   * @return Returns the req_nbr.
   */
  public String getReq_nbr() {
    return req_nbr;
  }
  /**
   * @param req_nbr The req_nbr to set.
   */
  public void setReq_nbr(String req_nbr) {
    this.req_nbr = req_nbr;
  }
  /**
   * @return Returns the source_doc_nbr.
   */
  public String getSource_doc_nbr() {
    return source_doc_nbr;
  }
  /**
   * @param source_doc_nbr The source_doc_nbr to set.
   */
  public void setSource_doc_nbr(String source_doc_nbr) {
    this.source_doc_nbr = source_doc_nbr;
  }
}
