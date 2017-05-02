package edu.arizona.kfs.fp.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;

import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.sys.KFSParameterKeyConstants;

public class GeneralErrorCorrectionRefNumberValidation extends GenericValidation {

	private ParameterService parameterService;
	private AccountingLineBase accountingLineForValidation;
	private BusinessObjectService businessObjectService;
	
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralErrorCorrectionRefNumberValidation.class);
	
	public boolean validate(AttributedDocumentEvent event) {
		boolean refNumValid;
		GeneralErrorCorrectionDocument currDocument = (GeneralErrorCorrectionDocument)event.getDocument();
		
		LOG.debug("validate ref number called");		
		LOG.debug("parm: " + parameterService.getParameterValueAsBoolean(currDocument.getClass(), KFSParameterKeyConstants.FpParameterConstants.REFERENCE_NUMBER_VALIDATION_IND_PARM));
		
		//if REFERENCE_NUMBER_VALIDATION_IND_PARM parameter is false, don't do validation
		if(parameterService.getParameterValueAsBoolean(currDocument.getClass(), KFSParameterKeyConstants.FpParameterConstants.REFERENCE_NUMBER_VALIDATION_IND_PARM) == false) {
			refNumValid = true;
		} else {
			refNumValid = isRefNumValid();
		}
		
		return refNumValid;		
	}
	
	protected boolean isRefNumValid() {
		Collection matchingEntry = getRefNumberMatch();
		
		if(matchingEntry != null && matchingEntry.size() > 0) {
			return true;
		} else {
			GlobalVariables.getMessageMap().putError(KFSPropertyConstants.REFERENCE_NUMBER, KFSKeyConstants.ERROR_GEC_REF_NUMBER_INVALID, accountingLineForValidation.getReferenceNumber());
			return false;
		}
	}
	
	protected Collection getRefNumberMatch() {
		LOG.debug("ref num: " + accountingLineForValidation.getReferenceNumber());
		LOG.debug("origin code: " + accountingLineForValidation.getReferenceOriginCode());
		
		Map criteria = new HashMap();
		criteria.put(KRADPropertyConstants.DOCUMENT_NUMBER, accountingLineForValidation.getReferenceNumber());
		criteria.put(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, accountingLineForValidation.getReferenceOriginCode());
		
		Collection results = getBusinessObjectService().findMatching(Entry.class, criteria);
		LOG.debug("results: " + results);
		
		return results;
	}
	
	public ParameterService getParameterService() {
		return parameterService;
	}
	
	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}
	
	public AccountingLineBase getAccountingLineForValidation() {
		return accountingLineForValidation;
	}
	
	public void setAccountingLineForValidation(AccountingLineBase accountingLineForValidation) {
		this.accountingLineForValidation = accountingLineForValidation;
	}
	
	public BusinessObjectService getBusinessObjectService() {
		return this.businessObjectService;
	}
	
	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}
}
