/*
 * Created on Feb 26, 2004
 *
 */
package org.kuali.module.pdp.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.kuali.kfs.context.SpringContext;
import org.kuali.module.pdp.dao.ReferenceDao;


/**
 * @author jsissom
 */
public class GetReferenceTag extends TagSupport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GetReferenceTag.class);

    private String name = "";

    /**
     * Return the bean name.
     */
    public String getName() {
        return (this.name);
    }

    /**
     * Set the bean name.
     * 
     * @param name The new bean name
     */
    public void setName(String name) {
        this.name = name;
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

        ReferenceDao rd = SpringContext.getBean(ReferenceDao.class);

        pageContext.setAttribute(name + "List", rd.getAll(name));

        return (EVAL_PAGE);
    }

    /**
     * Release any acquired resources.
     */
    public void release() {
        super.release();
    }
}
