package edu.arizona.kfs.module.purap.document.web.struts;

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import edu.arizona.kfs.sys.KFSConstants;

/**
 * Struts Action Form for Payment Request document.
 */
public class PaymentRequestForm extends org.kuali.kfs.module.purap.document.web.struts.PaymentRequestForm {

    private String docuwareTableParam;

    
    /**
     * Constructs a PaymentRequestForm instance and sets up the appropriately casted document.
     */
    public PaymentRequestForm() {
        super();
        setDocument(new PaymentRequestDocument());
    }

    /**
     * Gets the docuwareTableParam attribute.
     *   
     * @return Returns the docuwareTableParam.
     */
    public String getDocuwareTableParam() {
        return SpringContext.getBean(ParameterService.class).getParameterValueAsString(PaymentRequestDocument.class, KFSConstants.DOCUWARE_TABLE_PARAMETER);
    }
}
