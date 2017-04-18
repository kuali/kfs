package edu.arizona.kfs.module.cr.document.web.struts;

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Check Reconciliation Report Form
 * 
 * Deprecation: Eclipse shows org.kuali.rice.kns.web.struts.form.KualiForm as deprecated.
 */
@SuppressWarnings("deprecation")
public class CheckReconciliationReportForm extends KualiForm {
    private static final long serialVersionUID = -8697351241486395726L;

    private Date endDate;
    private String format;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors ae = new ActionErrors();
        return ae;
    }
}
