package edu.arizona.kfs.fp.document.web.struts;

import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.sys.KFSConstants;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.DataDictionaryService;


public class DisbursementVoucherForm extends org.kuali.kfs.fp.document.web.struts.DisbursementVoucherForm {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherForm.class);

    /**
     * Constructs a DisbursementVoucherForm
     */
    public DisbursementVoucherForm() {
        super();
        try {
            setDocument( (DisbursementVoucherDocument)SpringContext.getBean(DataDictionaryService.class).getDocumentClassByTypeName(getDefaultDocumentTypeName()).newInstance() );
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the docuwareTableParam attribute.
     *   
     * @return Returns the docuwareTableParam.
     */
    public String getDocuwareTableParam() {
        return SpringContext.getBean(ParameterService.class).getParameterValueAsString(DisbursementVoucherDocument.class, KFSConstants.DOCUWARE_TABLE_PARAMETER);
    }
}
