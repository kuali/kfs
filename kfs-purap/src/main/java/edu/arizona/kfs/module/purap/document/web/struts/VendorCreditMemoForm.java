package edu.arizona.kfs.module.purap.document.web.struts;

import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;
import edu.arizona.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * Struts Action Form for Credit Memo document.
 */
public class VendorCreditMemoForm extends org.kuali.kfs.module.purap.document.web.struts.VendorCreditMemoForm {

    /**
     * Constructs a VendorCreditMemoForm instance and sets up the appropriately casted document.
     */
    public VendorCreditMemoForm() {
        super();
        setDocument(new VendorCreditMemoDocument());
    }

    /**
     * Gets the docuwareTableParam attribute.
     *   
     * @return Returns the docuwareTableParam.
     */
    public String getDocuwareTableParam() {
        return SpringContext.getBean(ParameterService.class).getParameterValueAsString(VendorCreditMemoDocument.class, KFSConstants.DOCUWARE_TABLE_PARAMETER);
    }
}
