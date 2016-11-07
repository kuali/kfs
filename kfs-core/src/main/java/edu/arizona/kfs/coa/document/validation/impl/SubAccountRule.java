package edu.arizona.kfs.coa.document.validation.impl;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class implements the business rules specific to the {@link SubAccount} Maintenance Document.
 */
public class SubAccountRule extends org.kuali.kfs.coa.document.validation.impl.SubAccountRule {
    /**
     * This checks that the reporting fields are entered altogether or none at all
     * 
     * @return false if only one reporting field filled out and not all of them, true otherwise
     */
    @Override
    protected boolean checkForPartiallyEnteredReportingFields() {

        LOG.debug("Entering checkExistenceAndActive()");

        boolean success = true;
        boolean allReportingFieldsEntered = false;
        boolean anyReportingFieldsEntered = false;

        // set a flag if all three reporting fields are filled (this is separated just for readability)
        if (StringUtils.isNotEmpty(newSubAccount.getFinancialReportChartCode()) && StringUtils.isNotEmpty(newSubAccount.getFinReportOrganizationCode()) && StringUtils.isNotEmpty(newSubAccount.getFinancialReportingCode())) {
            allReportingFieldsEntered = true;
        }

        // set a flag if any of the three reporting fields are filled (this is separated just for readability)
        if (StringUtils.isNotEmpty(newSubAccount.getFinancialReportChartCode()) || StringUtils.isNotEmpty(newSubAccount.getFinReportOrganizationCode()) || StringUtils.isNotEmpty(newSubAccount.getFinancialReportingCode())) {
            anyReportingFieldsEntered = true;
        }

        // if any of the three reporting code fields are filled out, all three must be, or none
        // if any of the three are entered
        if (anyReportingFieldsEntered && !allReportingFieldsEntered) {
            // UAF-1203 This message applies to "Edit Financial Reporting Code" tab instead of entire document
            putFieldError("financialReportChartCode", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_RPTCODE_ALL_FIELDS_IF_ANY_FIELDS);
            success &= false;
        }

        return success;
    }

    /**
     * This checks to make sure that if cgAuthorized is false it succeeds immediately, otherwise it checks that all the information
     * for CG is correctly entered and identified including:
     * <ul>
     * <li>If the {@link SubFundGroup} isn't for Contracts and Grants then check to make sure that the cost share and ICR fields are
     * not empty</li>
     * <li>If it isn't a child of CG, then the SubAccount must be of type ICR</li>
     * </ul>
     *
     * @param document
     * @return true if the user is not authorized to change CG fields, otherwise it checks the above conditions
     */
    @Override
    protected boolean checkCgRules(MaintenanceDocument document) {

        boolean success = true;

        // short circuit if the parent account is NOT part of a CG fund group
        boolean a21SubAccountRefreshed = false;
        if (ObjectUtils.isNotNull(newSubAccount.getAccount())) {
            if (ObjectUtils.isNotNull(newSubAccount.getAccount().getSubFundGroup())) {

                // compare them, exit if the account isn't for contracts and grants
                if (!SpringContext.getBean(SubFundGroupService.class).isForContractsAndGrants(newSubAccount.getAccount().getSubFundGroup())) {

                    // KULCOA-1116 - Check if CG CS and CG ICR are empty, if not throw an error
                    if (checkCgCostSharingIsEmpty() == false) {
                        putFieldError("a21SubAccount.costShareChartOfAccountCode", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_CS_INVALID, new String[] { SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingAttributeLabel(), SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingValueForMessage() });
                        success = false;
                    }

                    if (checkCgIcrIsEmpty() == false) {
                    	putFieldError("a21SubAccount.a21IndirectCostRecoveryAccounts", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_ICR_INVALID, new String[] { SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingAttributeLabel(), SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingValueForMessage() });
                        success = false;
                    }

                    // KULRNE-4660 - this isn't the child of a CG account; sub account must be ICR type
                    if (!ObjectUtils.isNull(newSubAccount.getA21SubAccount())) {
                        // KFSMI-798 - refresh() changed to refreshNonUpdateableReferences()
                        // All references for A21SubAccount are non-updatable
                        newSubAccount.getA21SubAccount().refreshNonUpdateableReferences();
                        a21SubAccountRefreshed = true;
                        if (StringUtils.isEmpty(newSubAccount.getA21SubAccount().getSubAccountTypeCode()) || !newSubAccount.getA21SubAccount().getSubAccountTypeCode().equals(KFSConstants.SubAccountType.EXPENSE)) {
                            putFieldError("a21SubAccount.subAccountTypeCode", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_SUB_ACCT_TYPE_CODE_INVALID, new String[] { SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingAttributeLabel(), SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingValueForMessage() });
                            success = false;
                        }
                    }

                    return success;
                }
            }
        }

        A21SubAccount a21 = newSubAccount.getA21SubAccount();

        // short circuit if there is no A21SubAccount object at all (ie, null)
        if (ObjectUtils.isNull(a21)) {
            return success;
        }

        // FROM HERE ON IN WE CAN ASSUME THERE IS A VALID A21 SUBACCOUNT OBJECT

        // KFSMI-6848 since there is a ICR Collection Account object, change refresh to perform
        // manually refresh the a21SubAccount object, as it wont have been
        // refreshed by the parent, as its updateable
        // though only refresh if we didn't refresh in the checks above

        if (!a21SubAccountRefreshed) {
            //preserve the ICRAccounts before refresh to prevent the list from dropping
            List<A21IndirectCostRecoveryAccount>icrAccounts =a21.getA21IndirectCostRecoveryAccounts();
            a21.refresh();
            a21.setA21IndirectCostRecoveryAccounts(icrAccounts);

        }

        // C&G A21 Type field must be in the allowed values
        if (!KFSConstants.SubAccountType.ELIGIBLE_SUB_ACCOUNT_TYPE_CODES.contains(a21.getSubAccountTypeCode())) {
            putFieldError("a21SubAccount.subAccountTypeCode", KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_INVALI_SUBACCOUNT_TYPE_CODES, KFSConstants.SubAccountType.ELIGIBLE_SUB_ACCOUNT_TYPE_CODES.toString());
            success &= false;
        }

        // get a convenience reference to this code
        String cgA21TypeCode = a21.getSubAccountTypeCode();

        // if this is a Cost Sharing SubAccount, run the Cost Sharing rules
        if (KFSConstants.SubAccountType.COST_SHARE.trim().equalsIgnoreCase(StringUtils.trim(cgA21TypeCode))) {
            success &= checkCgCostSharingRules();
        }

        // if this is an ICR subaccount, run the ICR rules
        if (KFSConstants.SubAccountType.EXPENSE.trim().equals(StringUtils.trim(cgA21TypeCode))) {
            success &= checkCgIcrRules();
        }

        return success;
    }
}
