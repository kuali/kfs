/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.module.gl.batch.poster.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.EncumbranceDao;

/**
 * @author jsissom
 *
 */
public class PostEncumbrance implements PostTransaction,VerifyTransaction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostEncumbrance.class);

  private EncumbranceDao encumbranceDao;

  public void setEncumbranceDao(EncumbranceDao ed) {
    encumbranceDao = ed;
  }

  /**
   * 
   */
  public PostEncumbrance() {
    super();
  }

  /**
   * Make sure the transaction is correct for posting.  If there is an error,
   * this will stop the transaction from posting in all files.
   */
  public List verifyTransaction(Transaction t) {
    LOG.debug("verifyTransaction() started");

    List errors = new ArrayList();

    // The encumbrance update code can only be space, N, R or D.  Nothing else    
    if ( (! " ".equals(t.getEncumbranceUpdateCode())) && ( ! "N".equals(t.getEncumbranceUpdateCode())) && 
        (! "R".equals(t.getEncumbranceUpdateCode())) && (! "D".equals(t.getEncumbranceUpdateCode())) ) {
      errors.add("Invalid Encumbrance Update Code (" + t.getEncumbranceUpdateCode() + ")");
    }

    return errors;
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction)
   */
  public String post(Transaction t,int mode,Date postDate) {
    LOG.debug("post() started");

    String returnCode = "U";

    // If the encumbrance update code is space or N, or the object type code is FB 
    // we don't need to post an encumbrance
    if ( " ".equals(t.getEncumbranceUpdateCode()) || "N".equals(t.getEncumbranceUpdateCode()) || "FB".equals(t.getObjectTypeCode()) ) {
      LOG.debug("post() not posting encumbrance transaction");
      return "";
    }

    // Get the current encumbrance record if there is one
    Entry e = new Entry(t,null);
    if ( "R".equals(t.getEncumbranceUpdateCode()) ) {
      e.setDocumentNumber(t.getReferenceDocumentNumber());
      e.setOriginCode(t.getReferenceOriginCode());
      e.setDocumentTypeCode(t.getReferenceDocumentTypeCode());
    }

    Encumbrance enc = encumbranceDao.getEncumbranceByTransaction(e);
    if ( enc == null ) {
      // Build a new encumbrance record
      enc = new Encumbrance(e);

      returnCode = "I";
    } else {
      // Use the one retrieved
      if ( enc.getTransactionEncumbranceDate() == null ) {
        enc.setTransactionEncumbranceDate(t.getTransactionDate());
      }

      returnCode = "U";
    }

    if ( "R".equals(t.getEncumbranceUpdateCode()) ) {
      // If using referring doc number, add or subtract transaction amount from encumbrance closed amount
      if ( "D".equals(t.getDebitOrCreditCode()) ) {
        enc.setAccountLineEncumbranceClosedAmount(enc.getAccountLineEncumbranceClosedAmount().subtract(t.getTransactionLedgerEntryAmount()));
      } else {
        enc.setAccountLineEncumbranceClosedAmount(enc.getAccountLineEncumbranceClosedAmount().add(t.getTransactionLedgerEntryAmount()));        
      }
    } else {
      // If not using referring doc number, add or subtract transaction amount from encumbrance amount
      if ( "D".equals(t.getDebitOrCreditCode()) || " ".equals(t.getDebitOrCreditCode()) ) {
        enc.setAccountLineEncumbranceAmount(enc.getAccountLineEncumbranceAmount().add(t.getTransactionLedgerEntryAmount()));
      } else {
        enc.setAccountLineEncumbranceAmount(enc.getAccountLineEncumbranceAmount().subtract(t.getTransactionLedgerEntryAmount()));        
      }      
    }

    enc.setTimestamp(new Timestamp(postDate.getTime()));

    encumbranceDao.save(enc);

    return returnCode;
  }

  public String getDestinationName() {
    return "GL_ENCUMBRANCE_T";
  }
}
