package edu.arizona.kfs.fp.document.web.struts;

public class YearEndGeneralErrorCorrectionForm extends GeneralErrorCorrectionForm {

	public YearEndGeneralErrorCorrectionForm() {
		super();
	}
	
	@Override
	protected String getDefaultDocumentTypeName() {
		return "YEGE";
	}
	
}
