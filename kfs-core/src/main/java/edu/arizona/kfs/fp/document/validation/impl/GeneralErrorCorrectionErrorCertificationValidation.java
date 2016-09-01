package edu.arizona.kfs.fp.document.validation.impl;

import java.util.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.SubFundGroup;
import edu.arizona.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.sys.KFSParameterKeyConstants;

public class GeneralErrorCorrectionErrorCertificationValidation extends GenericValidation {
	
	private ParameterService parameterService;
	private BusinessObjectService businessObjectService;
	private AccountingLineBase accountingLineForValidation;
	
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralErrorCorrectionErrorCertificationValidation.class);
	
	public boolean validate(AttributedDocumentEvent event) {
		LOG.debug("validate error certification tab called");
		GeneralErrorCorrectionDocument currDocument = (GeneralErrorCorrectionDocument)event.getDocument();
		String defaultDaysFromParam = parameterService.getParameterValueAsString(currDocument.getClass(), KFSParameterKeyConstants.FpParameterConstants.DEFAULT_NUMBER_OF_DAYS_ERROR_CERTIFICATION_TAB_REQUIRED);
		Integer daysFromParameter = new Integer(defaultDaysFromParam);
		
		boolean success = true;
		boolean olderThanOriginal = true;
		boolean checkSubFund = false; // indicates whether sub fund should be checked for ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND rule
		int numBlankFields = 0;
		List<AccountingLineBase> acctLines;
		
		// check original transaction dates and the system parameters involving age of transaction
		try {
			acctLines = currDocument.getSourceAccountingLines();
			olderThanOriginal = !defaultNumberOfDaysCheck(currDocument, acctLines, daysFromParameter, checkSubFund);
			LOG.debug("olderThanOriginal for source accounting lines: " + olderThanOriginal);
			
			// if all source accounting lines are younger than the days specified in the parameter, check target accounting lines
			if(!olderThanOriginal) {
				acctLines = currDocument.getTargetAccountingLines();
				checkSubFund = true;
				olderThanOriginal = !defaultNumberOfDaysCheck(currDocument, acctLines, daysFromParameter, checkSubFund);
				LOG.debug("olderThanOriginal for target accounting lines: " + olderThanOriginal);
			}
		} catch (EntryNotFoundException e) {
			GlobalVariables.getMessageMap().putError(KFSPropertyConstants.REFERENCE_NUMBER, KFSKeyConstants.ERROR_GEC_REF_NUMBER_INVALID, e.getCriteria().get(KRADPropertyConstants.DOCUMENT_NUMBER));
			LOG.debug("defaultNumberOfDaysCheck failed", e);
			success = false;
		}
		
		// check for blank fields in the Error Certification Tab
		ErrorCertificationValidation errorCertificationTabValidation = new ErrorCertificationValidation();
		numBlankFields = errorCertificationTabValidation.errorCertificationBlankFieldsCheck(currDocument);
		LOG.debug("numBlankFields: " + numBlankFields);
		
		// if there is a transaction older than DEFAULT_NUMBER_OF_DAYS_ERROR_CERTIFICATION_TAB_REQUIRED and Error Certification tab is partially filled
		if (olderThanOriginal && ((numBlankFields > 0) && (numBlankFields <= KFSConstants.ErrorCertificationConstants.NUM_ERROR_CERT_FIELDS))) {
			GlobalVariables.getMessageMap().putErrorForSectionId("document.errorCertification", KFSKeyConstants.ERROR_ERROR_CERT_DATE_PARAM_TRIGGERED, KFSKeyConstants.ERROR_ERROR_CERT_DATE_PARAM_TRIGGERED);
		}
		
		return success;
	}
	
	/**
	 * Determines if the age of the referenced transactions are older than the value in the parameter.
	 * If checking source accounting lines, use the day in the
	 *  DEFAULT_NUMBER_OF_DAYS_ERROR_CERTIFICATION_TAB_REQUIRED parameter.
	 * If checking target accounting lines, check account's sub fund and maybe use
	 *  ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter.
	 *  
	 * <ol>
	 * 	<li>Loop through {@link AccountingLineBase} instances in the {@link AccountingDocument}.</li>
	 * 	<li>Get Original Transaction Date from {@link Entry} associated by ...</li>
	 * </ol>
	 * @param accountingLines
	 * @param checkSubFund indicates whether account's sub fund needs to be checked
	 * @return true if all of the original transaction dates are younger by days than specified in the appropriate
	 *  parameter; false otherwise
	 * @throws EntryNotFoundException if an Entry with the reference number cannot be found. 
	 */
	public boolean defaultNumberOfDaysCheck(GeneralErrorCorrectionDocument currDocument, List<AccountingLineBase> accountingLines, Integer daysFromParameter, boolean checkSubFund) throws EntryNotFoundException {
		Date originalDate;
		
		for(AccountingLineBase currentLine : accountingLines) {
			originalDate = getOriginalTransactionDate(currentLine);
			LOG.debug("transaction date: " + originalDate);
			
			// if target line, check sub fund associated with the account on the original doc
			if(currentLine instanceof TargetAccountingLine) {
				daysFromParameter = checkCurrentSubFund(currDocument, daysFromParameter, currentLine);
			}
			LOG.debug("daysFromParameter: " + daysFromParameter);
			
			if(daysFromParameter != null) {
				Calendar cal = Calendar.getInstance();
				cal.add(cal.DATE, -daysFromParameter);
				Date dateForComparison = cal.getTime();
				LOG.debug("dateForComparison: " + dateForComparison);
				
				if(dateForComparison.after(originalDate)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Gets original transaction date of given accounting line. Uses reference number and reference origin code.
	 * The original transaction's FDOC_NBR = accounting line's reference number.
	 * The original transaction's FS_ORIGIN_CD = accounting line's reference origin code.
	 * @return original transaction's date
	 * @throws EntryNotFoundException if an Entry with the reference number cannot be found.
	 */
	protected Date getOriginalTransactionDate(AccountingLineBase currentLine) throws EntryNotFoundException {
		Map criteria = new HashMap();
		criteria.put(KRADPropertyConstants.DOCUMENT_NUMBER, currentLine.getReferenceNumber());
		criteria.put(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, currentLine.getReferenceOriginCode());
		
		Collection<Entry> results = getBusinessObjectService().findMatching(Entry.class, criteria);
		if((results == null) || (results.size() == 0)) {
			throw new EntryNotFoundException(criteria);
		}
		
		for(Entry entry : results) {
			return entry.getTransactionDate();
		}
		
		return new Date();  // dummy return to pass compiler
	}

	/**
	 * This method checks the sub fund associated with the account in an accounting line
	 * If sub fund is in ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter, use different # of days
	 * @param daysFromParameter The days in DEFAULT_NUMBER_OF_DAYS_ERROR_CERTIFICATION_TAB_REQUIRED
	 * @param currLine
	 * @return the daysFromParameter, which may have value in ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND
	 * @throws EntryNotFoundException
	 */
	protected Integer checkCurrentSubFund(GeneralErrorCorrectionDocument currDocument, Integer daysFromParameter, AccountingLineBase currLine) throws EntryNotFoundException {
		LOG.debug("in checkCurrentSubFund");
		SubFundGroup subFundGroup = currLine.getAccount().getSubFundGroup();
		String subFundGroupCode = subFundGroup.getSubFundGroupCode();
		String newCompareDate = parameterService.getSubParameterValueAsString(currDocument.getClass(), KFSParameterKeyConstants.FpParameterConstants.ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND, subFundGroupCode);
		
		if(ObjectUtils.isNotNull(newCompareDate)) {
			daysFromParameter = new Integer(newCompareDate);
		}
		
		return daysFromParameter;
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
