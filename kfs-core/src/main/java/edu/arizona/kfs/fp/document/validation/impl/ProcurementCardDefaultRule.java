package edu.arizona.kfs.fp.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kns.document.MaintenanceDocument;

import edu.arizona.kfs.fp.businessobject.ProcurementCardDefault;

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
            groupMembers = SpringContext.getBean(GroupService.class).getMemberPrincipalIds(newProcurementCardDefault.getReconcilerGroupId());
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

}
