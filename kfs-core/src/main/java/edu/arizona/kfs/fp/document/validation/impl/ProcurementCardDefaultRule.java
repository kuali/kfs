package edu.arizona.kfs.fp.document.validation.impl;

import edu.arizona.kfs.sys.KFSKeyConstants;
import org.apache.commons.lang.StringUtils;
import edu.arizona.kfs.fp.businessobject.ProcurementCardDefault;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.List;

public class ProcurementCardDefaultRule extends MaintenanceDocumentRuleBase {

    private ProcurementCardDefault newProcurementCardDefault;

    /**
     * Returns value from processCustomRouteDocumentBusinessRules(document)
     *
     * @param document maintenance document
     * @return value from processCustomRouteDocumentBusinessRules(document)
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        return processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * Returns true procurement cardholder maintenance document is routed successfully
     *
     * @param document submitted procurement cardholder maintenance document
     * @return true if procurement cardholder maintenance document is routed successfully
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean continueRouting = super.processCustomRouteDocumentBusinessRules(document);
        newProcurementCardDefault = (ProcurementCardDefault)document.getNewMaintainableObject().getBusinessObject();

        // check chart/account/organization is valid
        continueRouting &= checkAccountValidity();

        // check membership of reconciler group against cardholder ID
        continueRouting &= validateReconcilerGroup();

        return continueRouting;
    }

    /**
     * Returns true if chart/account/organization is valid
     *
     * @return true if chart/account/organization is valid
     */

    protected boolean checkAccountValidity() {
        boolean result = false;

        // check that an org has been entered
        if (StringUtils.isNotBlank(newProcurementCardDefault.getOrganizationCode())) {

            if ( StringUtils.isNotBlank(newProcurementCardDefault.getChartOfAccountsCode()) ) {
                Account defaultAccount = getAccountService().getByPrimaryId(newProcurementCardDefault.getChartOfAccountsCode(), newProcurementCardDefault.getAccountNumber());
                // if the object doesn't exist, then we can't continue, so exit
                if (ObjectUtils.isNull(defaultAccount)) {
                    putFieldError("accountNumber", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ACCOUNT, new String[] {newProcurementCardDefault.getAccountNumber(), newProcurementCardDefault.getOrganizationCode()});
                    return result;
                }
                if (newProcurementCardDefault.getOrganizationCode().equals(defaultAccount.getOrganizationCode())) {
                    result = true;
                }
                if (!result) {
                    putFieldError("organizationCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ORG, new String[] {newProcurementCardDefault.getAccountNumber(), newProcurementCardDefault.getOrganizationCode()});
                }
            }
        }

        return result;
    }

    /**
     * Checks if the card holder is the only remaining member of reconciler group
     *
     * @return true if the reconciler group has more members than the card holder
     */

    protected boolean validateReconcilerGroup() {

        // check that a reconciler group id and cardholder id are not empty
        if (StringUtils.isNotBlank(newProcurementCardDefault.getReconcilerGroupId()) && StringUtils.isNotBlank(newProcurementCardDefault.getCardHolderSystemId())) {
            List<String> groupMembers = getGroupService().getMemberPrincipalIds(newProcurementCardDefault.getReconcilerGroupId());
            //if there is only one member in the reconcilers group
            if (groupMembers.size() == 1 ){
                String groupMember  = groupMembers.get(0);
                if ( groupMember.equals(newProcurementCardDefault.getCardHolderSystemId())) {
                    //card holder is the only remaining member of reconciler group
                    putFieldError("reconcilerGroupId", KFSKeyConstants.WARNING_CARDHOLDER_LAST_ACTIVE_MEMBER, (String[])null);
                    return false;
                }
            }
        }

        return true;
    }


    public AccountService getAccountService() {
        return SpringContext.getBean(AccountService.class);
    }


}

