package edu.arizona.kfs.fp.document.web.struts;

import org.kuali.kfs.sys.KFSConstants;

public class YearEndGeneralErrorCorrectionForm extends GeneralErrorCorrectionForm {

    private static final long serialVersionUID = 7268818232511243734L;

	public YearEndGeneralErrorCorrectionForm() {
		super();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	protected String getDefaultDocumentTypeName() {
		return KFSConstants.FinancialDocumentTypeCodes.YEAR_END_GENERAL_ERROR_CORRECTION;
	}
	
}
