package edu.arizona.kfs.fp.document;

import org.kuali.rice.kew.api.exception.WorkflowException;

import edu.arizona.kfs.fp.businessobject.ErrorCertification;

/**
 * This is the business object that represents the UA modifications for the GeneralErrorCorrectionDocument. This is a transactional document that
 * will eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines:
 * from and to. From lines are the source lines, to lines are the target lines.
 *
 * @author Adam Kost <kosta@email.arizona.edu> with some code adapted from UCI
 */

public class GeneralErrorCorrectionDocument extends org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument {

    private static final long serialVersionUID = 3559591546723165167L;
    
    private ErrorCertification errorCertification;
    private Integer errorCertID;
    
    public GeneralErrorCorrectionDocument() {
    	super();
    }
    
    public ErrorCertification getErrorCertification() {
    	return errorCertification;
    }
    
    public void setErrorCertification(ErrorCertification errorCertification) {
    	this.errorCertification = errorCertification;
    }
    
    public Integer getErrorCertID() {
    	return errorCertID;
    }
    
    public void setErrorCertID(Integer errorCertID) {
    	this.errorCertID = errorCertID;
    	this.errorCertification.setErrorCertID(errorCertID);
    }

    public void toCopy() throws WorkflowException {
    	super.toCopy();
    	
    	errorCertID = null;
    	ErrorCertification oldErrorCertification = errorCertification;
    	errorCertification = new ErrorCertification();
    	errorCertification.setExpenditureDescription(oldErrorCertification.getExpenditureDescription());
    	errorCertification.setExpenditureProjectBenefit(oldErrorCertification.getExpenditureProjectBenefit());
    	errorCertification.setErrorDescription(oldErrorCertification.getErrorDescription());
    	errorCertification.setErrorCorrectionReason(oldErrorCertification.getErrorCorrectionReason());
    }
    
}
