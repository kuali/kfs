package edu.arizona.kfs.fp.document.validation.impl;

import java.util.Set;

import org.kuali.kfs.fp.document.validation.impl.ExpiredAccountOverridePreRules;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;

import edu.arizona.kfs.fp.document.GeneralErrorCorrectionDocument;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;

public class GeneralErrorCorrectionDocumentPreRules extends ExpiredAccountOverridePreRules {

	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralErrorCorrectionDocumentPreRules.class);
	private GeneralErrorCorrectionErrorCertificationValidation gecErrorCertificationValidationService;
	
	/**
	 * Will call private methods to examine a GEC document.
	 * Includes Error Certification Statement for approval by a fiscal officer if appropriate.
	 * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doPrompts(org.kuali.rice.krad.document.Document)
	 */
	@Override
	public boolean doPrompts(Document document) {
		boolean preRulesOK = true;
		
		GeneralErrorCorrectionDocument gecDocument = (GeneralErrorCorrectionDocument) document;
		preRulesOK &= errorCertStmtApproved(gecDocument);
		
		return preRulesOK;
	}
	
	/**
	 * Calls private methods to determine whether to show Error Certification Statement.
	 * @param gecDocument
	 * @return false if Error Certification Statement isn't approved; true if Error Certification Statement
	 *  is approved, and by default
	 */
	private boolean errorCertStmtApproved(GeneralErrorCorrectionDocument gecDocument) {
		boolean fiscalOfficerNode = checkRouteLevel(gecDocument);
		boolean parameterTriggered = false;
		
		try {
			parameterTriggered = checkTargetLines(gecDocument);
		} catch (EntryNotFoundException e) {
			GlobalVariables.getMessageMap().putError(KFSPropertyConstants.REFERENCE_NUMBER, KFSKeyConstants.ERROR_GEC_REF_NUMBER_INVALID, e.getCriteria().get(KRADPropertyConstants.DOCUMENT_NUMBER));
			LOG.debug("checkTargetLines failed", e);
		}
		
		if(fiscalOfficerNode && parameterTriggered) {
			return showErrorCertStmt();
		}
		return true;
	}
	
	/**
	 * This method checks the current route level.  If it is at the "Account" route node, then the fiscal officer
	 * is looking at the GEC.
	 * @return true if it's at the "Account" route node, false otherwise	
	 */
	private boolean checkRouteLevel(GeneralErrorCorrectionDocument gecDocument) {
		Set<String> currNode = gecDocument.getDocumentHeader().getWorkflowDocument().getCurrentNodeNames();
		if(LOG.isDebugEnabled()) {
			LOG.debug("checkRouteLevel currNode: " + currNode);
		}
		
		return currNode.contains(KFSConstants.RouteLevelNames.ACCOUNT);
	}
	
	/**
	 * This method iterates through the target accounting lines to examine the original transaction date,
	 * the sub funds, and the ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter.
	 * @param gecDocument
	 * @return true if there is a transaction that contains the sub fund in the parameter and the original
	 * transaction is older than the number of days in the parameter; false otherwise
	 */
	@SuppressWarnings("unchecked")
	private boolean checkTargetLines(GeneralErrorCorrectionDocument gecDocument) throws EntryNotFoundException {
		Integer initialPeriodsFromParameter = null;
		
		return !(getGecErrorCertificationValidationService().defaultNumberOfDaysCheck(gecDocument, gecDocument.getTargetAccountingLines(), initialPeriodsFromParameter, true));
	}
	
	/**
	 * Shows the Error Certification Statement and returns the result.
	 * @return true if the Error Certification Statement returns a "Yes"; false otherwise
	 */
	@SuppressWarnings("deprecation")
	private boolean showErrorCertStmt() {
		String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.QUESTION_ERROR_CERTIFICATION_STMT);
		boolean approved = super.askOrAnalyzeYesNoQuestion(KFSConstants.GeneralErrorCorrectionDocumentConstants.GENERATE_ERROR_CERTIFICATION_STMT_ID, questionText);
		
		if(!approved) {
			super.event.setActionForwardName(RiceConstants.MAPPING_BASIC);
		}
		return approved;
	}
	
	public ParameterService getParameterService() {
		return SpringContext.getBean(ParameterService.class);
	}
	
	public BusinessObjectService getBusinessObjectService() {
		return SpringContext.getBean(BusinessObjectService.class);
	}
	
	public GeneralErrorCorrectionErrorCertificationValidation getGecErrorCertificationValidationService() {
		if (gecErrorCertificationValidationService == null) {
			gecErrorCertificationValidationService = SpringContext.getBean(GeneralErrorCorrectionErrorCertificationValidation.class);
		}
		
		return gecErrorCertificationValidationService;
	}
	
	public void setGecErrorCertificationValidationService(GeneralErrorCorrectionErrorCertificationValidation gecErrorCertificationValidationService) {
		this.gecErrorCertificationValidationService = gecErrorCertificationValidationService;
	}
}
