package edu.arizona.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.fp.businessobject.ErrorCertification;
import edu.arizona.kfs.fp.document.GeneralErrorCorrectionDocument;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;

public class ErrorCertificationValidation extends GenericValidation {
	
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ErrorCertificationValidation.class);
	
	public boolean validate(AttributedDocumentEvent event) {
		GeneralErrorCorrectionDocument currDocument = (GeneralErrorCorrectionDocument)event.getDocument();
		int numBlankFields = errorCertificationBlankFieldsCheck(currDocument);
		LOG.debug("numBlankFields: " + numBlankFields);
		
		//if Error Certification tab is partially filled out
		if((numBlankFields > 1) && (numBlankFields < KFSConstants.ErrorCertificationConstants.NUM_ERROR_CERT_FIELDS)) {
			GlobalVariables.getMessageMap().putErrorForSectionId("document.errorCertification", KFSKeyConstants.ERROR_ERROR_CERT_FIELDS_REQ, "error.errorCert.fieldsReq");
			return false;
		}
		
		return true;
	}

	/**
	 * This method goes through all the Error Certification fields looking for blank fields.
	 * @param document
	 * @return blankFieldCount the number of blank fields in the tab
	 */
	public int errorCertificationBlankFieldsCheck(GeneralErrorCorrectionDocument document) {
		int blankFieldCount = 0;
		ErrorCertification ecTab = document.getErrorCertification();
		
		if(StringUtils.isBlank(ecTab.getExpenditureDescription())) {
			blankFieldCount++;
		}
		
		if(StringUtils.isBlank(ecTab.getExpenditureProjectBenefit())) {
			blankFieldCount++;
		}
		
		if(StringUtils.isBlank(ecTab.getErrorDescription())) {
			blankFieldCount++;
		}
		
		if(StringUtils.isBlank(ecTab.getErrorCorrectionReason())) {
			blankFieldCount++;
		}
		
		return blankFieldCount;		
	}
	
}
