package edu.arizona.kfs.fp.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.fp.businessobject.ProcurementCardDefault;
import edu.arizona.kfs.sys.KFSPropertyConstants;

@SuppressWarnings("deprecation")
public class ProcurementCardDefaultRule extends org.kuali.kfs.fp.document.validation.impl.ProcurementCardDefaultRule {

    protected static final String WARNING_CARDHOLDER_LAST_ACTIVE_MEMBER = "warning.document.procurementcardholderdetail.cardholder.last.active";

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean continueRouting = super.processCustomRouteDocumentBusinessRules(document);
        ProcurementCardDefault newProcurementCardDefault = (ProcurementCardDefault)document.getNewMaintainableObject().getBusinessObject();
        
        // check membership of reconciler group against cardholder ID
        continueRouting &= checkGroupMembership(newProcurementCardDefault);

        return continueRouting;
    }
    
    /**
     * @return true if cardholder is not only member of reconciler group
     */
	protected boolean checkGroupMembership(ProcurementCardDefault newProcurementCardDefault) {
        boolean result = true;
      
        // check that a reconciler group id and cardholder id have been entered
        if (StringUtils.isNotBlank(newProcurementCardDefault.getReconcilerGroupId()) && StringUtils.isNotBlank(newProcurementCardDefault.getCardHolderSystemId())) {           
           
            List<String> groupMembers = new ArrayList<String>();
            groupMembers = getGroupService().getMemberPrincipalIds(newProcurementCardDefault.getReconcilerGroupId());
            for (String groupMember : groupMembers) {
                if (groupMembers.size() < 2 && groupMember.equals(newProcurementCardDefault.getCardHolderSystemId())) {
                    //card holder is only remaining member of reconciler group 
                    result = false;
                }                
            }
            if (!result) { 
                putGlobalError(WARNING_CARDHOLDER_LAST_ACTIVE_MEMBER);                                        
            }
        }
        
        return result;      
    }
	
	@Override
    protected boolean validateCardHolderDefault(org.kuali.kfs.fp.businessobject.ProcurementCardDefault procurementCardDefault) {
        ProcurementCardDefault newProcurementCardDefault = (ProcurementCardDefault)procurementCardDefault;
        boolean valid = true;
        if (isCardHolderDefaultTurnedOn()) {
            if (StringUtils.isBlank(newProcurementCardDefault.getCardHolderLine1Address())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_HOLDER_LINE1_ADDRESS, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardHolderCityName())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_HOLDER_CITY_NAME, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardHolderStateCode())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_HOLDER_STATE, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardHolderZipCode())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_HOLDER_ZIP_CODE, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardHolderWorkPhoneNumber())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_HOLDER_WORK_PHONE_NUMBER, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (ObjectUtils.isNull(newProcurementCardDefault.getCardLimit())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_LIMIT, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (ObjectUtils.isNull(newProcurementCardDefault.getCardCycleAmountLimit())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_CYCLE_AMOUNT_LIMIT, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (ObjectUtils.isNull(newProcurementCardDefault.getCardCycleVolLimit())) {
                putFieldErrorWithLabel(KFSPropertyConstants.CARD_CYCLE_VOL_LIMIT, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardStatusCode())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_STATUS_CODE, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
            if (StringUtils.isBlank(newProcurementCardDefault.getCardNoteText())) {
                putFieldErrorWithLabel(KFSPropertyConstants.PROCUREMENT_CARD_NOTE_TEXT, KFSKeyConstants.ERROR_REQUIRED);
                valid = false;
            }
        }
        return valid;
    }

}
