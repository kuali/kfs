package edu.arizona.kfs.fp.document.web.struts;

import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import edu.arizona.kfs.sys.KFSConstants;

public class ProcurementCardForm extends org.kuali.kfs.fp.document.web.struts.ProcurementCardForm {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardForm.class);

    /**
     * Constructs a ProcurementCardForm
     */
    public ProcurementCardForm() {
        super();
        setDocument(new ProcurementCardDocument());
    }

    /**
     * Gets the docuwareTableParam attribute.
     *   
     * @return Returns the docuwareTableParam.
     */
    public String getDocuwareTableParam() {
        return SpringContext.getBean(ParameterService.class).getParameterValueAsString(ProcurementCardDocument.class, KFSConstants.DOCUWARE_TABLE_PARAMETER);
    }
}
