package edu.arizona.kfs.fp.document.web.struts;

import org.kuali.kfs.sys.KFSConstants;

public class YearEndGeneralErrorCorrectionForm extends GeneralErrorCorrectionForm {

	public YearEndGeneralErrorCorrectionForm() {
		super();
	}
	
	@Override
	protected String getDefaultDocumentTypeName() {
		return KFSConstants.FinancialDocumentTypeCodes.YEAR_END_GENERAL_ERROR_CORRECTION;
	}
	
}
