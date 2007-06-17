/*
 * Created on Sep 2, 2004
 *
 */
package org.kuali.module.pdp.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.pdp.dao.BankDao;


/**
 * @author jsissom
 *
 */
public class GetBankTag extends TagSupport {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GetBankTag.class);

  private String active = "";

  /**
   * Return the active indicator.
   */
  public String getActive() {
    return active;
  }

  /**
   * Set the active indicator.
   *
   * @param name The new active indicator
   */
  public void setActive(String a) {
    active = a;
  }

  /**
   * Defer our checking until the end of this tag is encountered.
   *
   * @exception JspException if a JSP exception has occurred
   */
  public int doStartTag() throws JspException {
    return (SKIP_BODY);
  }

  /**
   * Get the requested lookup table and save it in the page scope.
   *
   * @exception JspException if a JSP exception has occurred
   */
  public int doEndTag() throws JspException {
    LOG.info("doEndTag() starting");

    BankDao cpd = (BankDao)SpringServiceLocator.getService("pdpBankDao");

    if ( "".equals(active) ) {
      pageContext.setAttribute("BankList",cpd.getAll());
    } else {
      pageContext.setAttribute("BankList",cpd.getAll("Y".equals(active)));
    }
    return (EVAL_PAGE);
  }

  /**
   * Release any acquired resources.
   */
  public void release() {
    super.release();
  }
}
