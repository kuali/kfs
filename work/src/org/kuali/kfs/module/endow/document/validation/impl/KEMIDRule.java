/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.document.validation.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CloseCode;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidAgreement;
import org.kuali.kfs.module.endow.businessobject.KemidAuthorizations;
import org.kuali.kfs.module.endow.businessobject.KemidBenefittingOrganization;
import org.kuali.kfs.module.endow.businessobject.KemidCombineDonorStatement;
import org.kuali.kfs.module.endow.businessobject.KemidDonorStatement;
import org.kuali.kfs.module.endow.businessobject.KemidFee;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.KemidPayoutInstruction;
import org.kuali.kfs.module.endow.businessobject.KemidReportGroup;
import org.kuali.kfs.module.endow.businessobject.KemidSourceOfFunds;
import org.kuali.kfs.module.endow.businessobject.KemidSpecialInstruction;
import org.kuali.kfs.module.endow.businessobject.KemidUseCriteria;
import org.kuali.kfs.module.endow.document.service.KemidCurrentCashService;
import org.kuali.kfs.module.endow.document.service.KemidHoldingTaxLotOpenRecordsService;
import org.kuali.kfs.module.endow.document.service.ValidateDateBasedOnFrequencyCodeService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This KEMIDRule class implements the Business rules associated with the KEMID.
 */
public class KEMIDRule extends MaintenanceDocumentRuleBase {

    protected static Logger LOG = org.apache.log4j.Logger.getLogger(KEMIDRule.class);
    private KEMID newKemid;
    private KEMID oldKemid;

    /**
     * This method initializes the old and new kemid.
     * 
     * @param document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newKemid == null) {
            newKemid = (KEMID) document.getNewMaintainableObject().getBusinessObject();
        }
        if (oldKemid == null) {
            oldKemid = (KEMID) document.getOldMaintainableObject().getBusinessObject();
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean isValid = true;
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();

        if (isValid) {

            initializeAttributes(document);
            isValid &= checkCloseCode();
            isValid &= checkIfKemidHasCurrentCashOpenRecordsIfClosed();
            isValid &= checkIfKemidHasHoldingTaxLotOpenRecordsIfClosed();
            isValid &= validateIncomeRestrictionCode(document);
            isValid &= validateAgreements();
            isValid &= validateUseTransactionRestrictionFromAgreement();
            isValid &= validateSourceOfFunds();
            isValid &= validateBenefittingOrgs();
            isValid &= validateGeneralLedgerAccounts();
            isValid &= validateKemidAuthorizations();
            isValid &= validatePayoutInstructions();
            isValid &= validateKemidDonorStatements();
            isValid &= validateFees();
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean success = true;

        success &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, bo);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        success &= errorMap.hasNoErrors();

        if (success) {

            initializeAttributes(document);

            if (collectionName.equalsIgnoreCase(EndowPropertyConstants.KEMID_DONOR_STATEMENTS_TAB)) {
                KemidDonorStatement donorStatement = (KemidDonorStatement) bo;

                if (!validCombineWithDonorId(donorStatement)) {
                    success &= false;
                }
            }

            if (bo instanceof KemidAgreement) {
                KemidAgreement agreement = (KemidAgreement) bo;
                success &= checkAgreement(agreement);

            }

            if (bo instanceof KemidSourceOfFunds) {
                KemidSourceOfFunds sourceOfFunds = (KemidSourceOfFunds) bo;
                success &= checkSourceOfFunds(sourceOfFunds);
            }

            if (bo instanceof KemidBenefittingOrganization) {
                KemidBenefittingOrganization benefittingOrg = (KemidBenefittingOrganization) bo;
                success &= checkBenefittingOrg(benefittingOrg);
            }

            if (bo instanceof KemidGeneralLedgerAccount) {
                KemidGeneralLedgerAccount generalLedgerAccount = (KemidGeneralLedgerAccount) bo;
                success &= checkGeneralLedgerAccount(generalLedgerAccount);

                List<KemidGeneralLedgerAccount> generalLedgerAccounts = new ArrayList<KemidGeneralLedgerAccount>();
                generalLedgerAccounts.addAll(newKemid.getKemidGeneralLedgerAccounts());
                generalLedgerAccounts.add(generalLedgerAccount);

                success &= validateIncomePrincipalGLAccounts(generalLedgerAccounts);
            }

            if (bo instanceof KemidAuthorizations) {
                KemidAuthorizations authorization = (KemidAuthorizations) bo;
                success &= checkAuthorization(authorization, -1);
            }

            if (bo instanceof KemidPayoutInstruction) {
                KemidPayoutInstruction payoutInstruction = (KemidPayoutInstruction) bo;
                success &= checkPayoutInstruction(payoutInstruction, -1);
            }

            if (bo instanceof KemidUseCriteria) {
                KemidUseCriteria useCriteria = (KemidUseCriteria) bo;
                success &= checkUseCriteria(useCriteria);
            }

            if (bo instanceof KemidSpecialInstruction) {
                KemidSpecialInstruction specialInstruction = (KemidSpecialInstruction) bo;
                success &= checkSpecialInstruction(specialInstruction);
            }

            if (bo instanceof KemidReportGroup) {
                KemidReportGroup reportGroup = (KemidReportGroup) bo;
                success &= checkReportGroup(reportGroup);
            }

            if (bo instanceof KemidDonorStatement) {
                KemidDonorStatement donorStatement = (KemidDonorStatement) bo;
                success &= checkDonorStatement(donorStatement);
            }

            if (bo instanceof KemidCombineDonorStatement) {
                KemidCombineDonorStatement combineDonorStatement = (KemidCombineDonorStatement) bo;
                success &= checkCombineDonorStatement(combineDonorStatement);
            }

            if (bo instanceof KemidFee) {
                KemidFee fee = (KemidFee) bo;
                success &= checkFee(fee);
                success &= validateFeePercentageTotal(fee, -1);
                success &= validatePercentageOfFeeChargedToPrincipal(fee, -1);
                success &= validateKemidFeeStartDate(fee, -1);
            }
        }
        return success;
    }

    /**
     * This method will validate if income restriction code is "P" (Permanently Restricted) Rule: Type_inc_restr_cd cannot be P
     * (Permanently Restricted).
     * 
     * @param document
     * @return true if Income Restriction code is not "P" else return false
     */
    private boolean validateIncomeRestrictionCode(Document document) {
        boolean rulesPassed = true;

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        KEMID kemid = (KEMID) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        if (EndowConstants.TypeRestrictionPresetValueCodes.PERMANENT_TYPE_RESTRICTION_CODE.equalsIgnoreCase(kemid.getIncomeRestrictionCode())) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.TYPE_INC_RESTR_CD, EndowKeyConstants.TypeRestrictionCodeConstants.ERROR_PERMANENT_INDICATOR_CANNOT_BE_USED_FOR_TYPE_RESTRICTION_CODE);
            return false;

        }
        return rulesPassed;
    }


    /**
     * Checks that the agreement type and agreement status exist.
     * 
     * @param agreement
     * @return true if they exist, false otherwise
     */
    private boolean checkAgreement(KemidAgreement agreement) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_AGREEMENTS_TAB + ".";

        // check that the agreement type exists
        if (StringUtils.isNotBlank(agreement.getAgreementTypeCode())) {
            agreement.refreshReferenceObject(EndowPropertyConstants.KEMID_AGRMNT_TYPE);

            if (ObjectUtils.isNull(agreement.getAgreementType())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidAgreement.class, EndowPropertyConstants.KEMID_AGRMNT_TYP_CD);
                String message = label + "(" + agreement.getAgreementTypeCode() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_AGRMNT_TYP_CD, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        // check that the agreement status exists
        if (StringUtils.isNotBlank(agreement.getAgreementStatusCode())) {
            agreement.refreshReferenceObject(EndowPropertyConstants.KEMID_AGRMNT_STATUS);

            if (ObjectUtils.isNull(agreement.getAgreementType())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidAgreement.class, EndowPropertyConstants.KEMID_AGRMNT_STAT_CD);
                String message = label + "(" + agreement.getAgreementStatusCode() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_AGRMNT_STAT_CD, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;

    }

    /**
     * Checks that the fund source and opened from kemid exist.
     * 
     * @param sourceOfFunds
     * @return true if they exist, false otherwise
     */
    private boolean checkSourceOfFunds(KemidSourceOfFunds sourceOfFunds) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_SOURCE_OF_FUNDS_TAB + ".";

        // check that the fund source exists
        if (StringUtils.isNotBlank(sourceOfFunds.getFundSourceCode())) {
            sourceOfFunds.refreshReferenceObject(EndowPropertyConstants.KEMID_FND_SRC);

            if (ObjectUtils.isNull(sourceOfFunds.getFundSource())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidAgreement.class, EndowPropertyConstants.KEMID_FND_SRC_CD);
                String message = label + "(" + sourceOfFunds.getFundSourceCode() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_FND_SRC_CD, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        // check that the opened from kemid exists
        if (StringUtils.isNotBlank(sourceOfFunds.getOpenedFromKemid())) {
            sourceOfFunds.refreshReferenceObject(EndowPropertyConstants.KEMID_FND_SRC_OPND_FROM_KEMID_OBJ_REF);

            if (ObjectUtils.isNull(sourceOfFunds.getOpenedFromKemidObjRef())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidSourceOfFunds.class, EndowPropertyConstants.KEMID_FND_SRC_OPND_FROM_KEMID);
                String message = label + "(" + sourceOfFunds.getOpenedFromKemid() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_FND_SRC_OPND_FROM_KEMID, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;

    }

    /**
     * Checks that the pay income to kemid exists.
     * 
     * @param payoutInstruction
     * @return true if it exists, false otherwise
     */
    private boolean checkBenefittingOrg(KemidBenefittingOrganization benefittingOrg) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_BENEFITTING_ORGS_TAB + ".";

        // check that the organization exists
        if (StringUtils.isNotBlank(benefittingOrg.getBenefittingOrgCode())) {
            benefittingOrg.refreshReferenceObject(EndowPropertyConstants.KEMID_BENE_ORG);

            if (ObjectUtils.isNull(benefittingOrg.getOrganization())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidBenefittingOrganization.class, EndowPropertyConstants.KEMID_BENE_ORG_CD);
                String message = label + "(" + benefittingOrg.getBenefittingOrgCode() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_BENE_ORG_CD, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        // check that the chart exists
        if (StringUtils.isNotBlank(benefittingOrg.getBenefittingChartCode())) {
            benefittingOrg.refreshReferenceObject(EndowPropertyConstants.KEMID_BENE_CHRT);

            if (ObjectUtils.isNull(benefittingOrg.getChart())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidBenefittingOrganization.class, EndowPropertyConstants.KEMID_BENE_CHRT_CD);
                String message = label + "(" + benefittingOrg.getBenefittingChartCode() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_BENE_CHRT_CD, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;

    }

    /**
     * Checks that the generalLedgerAccount chart and account status exist.
     * 
     * @param generalLedgerAccount
     * @return true if they exist, false otherwise
     */
    private boolean checkGeneralLedgerAccount(KemidGeneralLedgerAccount generalLedgerAccount) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_GENERAL_LEDGER_ACCOUNTS_TAB + ".";

        // check that the chart exists
        if (StringUtils.isNotBlank(generalLedgerAccount.getChartCode())) {
            generalLedgerAccount.refreshReferenceObject(EndowPropertyConstants.KEMID_GL_ACCOUNT_CHART);

            if (ObjectUtils.isNull(generalLedgerAccount.getChart())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidGeneralLedgerAccount.class, EndowPropertyConstants.KEMID_GL_ACCOUNT_CHART_CD);
                String message = label + "(" + generalLedgerAccount.getChartCode() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_GL_ACCOUNT_CHART_CD, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        // check that the account exists
        if (StringUtils.isNotBlank(generalLedgerAccount.getAccountNumber())) {
            generalLedgerAccount.refreshReferenceObject(EndowPropertyConstants.KEMID_GL_ACCOUNT);

            if (ObjectUtils.isNull(generalLedgerAccount.getAccount())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidGeneralLedgerAccount.class, EndowPropertyConstants.KEMID_GL_ACCOUNT_NBR);
                String message = label + "(" + generalLedgerAccount.getAccountNumber() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_GL_ACCOUNT_NBR, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;

    }

    /**
     * Checks that the given authorization is valid.
     * 
     * @param authorization
     * @param index
     * @return true if valid, false otherwise
     */
    private boolean checkAuthorization(KemidAuthorizations authorization, int index) {
        boolean success = true;

        if (authorization.isActive()) {
            success &= validateRoleInKFSEndowNamespace(authorization, index);
        }

        return success;
    }

    /**
     * Checks that the pay income to kemid exists.
     * 
     * @param payoutInstruction the payout instruction to be validated
     * @param index -1 if cehcking the add payout instruction, the index of the payout instruction in the list of added payout
     *        instruction otherwise
     * @return true if it exists, false otherwise
     */
    private boolean checkPayoutInstruction(KemidPayoutInstruction payoutInstruction, int index) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();

        // check that the pay income to kemid exists
        if (StringUtils.isNotBlank(payoutInstruction.getPayIncomeToKemid()) && !payoutInstruction.getPayIncomeToKemid().equalsIgnoreCase(newKemid.getKemid())) {
            payoutInstruction.refreshReferenceObject(EndowPropertyConstants.KEMID_PAY_INC_TO_KEMID_OBJ_REF);

            if (ObjectUtils.isNull(payoutInstruction.getPayIncomeToKemidObjRef())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidPayoutInstruction.class, EndowPropertyConstants.KEMID_PAY_INC_TO_KEMID);
                String message = label + "(" + payoutInstruction.getPayIncomeToKemid() + ")";

                if (index == -1) {
                    putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_PAY_INSTRUCTIONS_TAB + "." + EndowPropertyConstants.KEMID_PAY_INC_TO_KEMID, KFSKeyConstants.ERROR_EXISTENCE, message);
                }
                else {
                    putFieldError(EndowPropertyConstants.KEMID_PAY_INSTRUCTIONS_TAB + "[" + index + "]" + "." + EndowPropertyConstants.KEMID_PAY_INC_TO_KEMID, KFSKeyConstants.ERROR_EXISTENCE, message);
                }
            }
        }

        // check that start date is prior to end date
        Date startDate = payoutInstruction.getStartDate();
        Date endDate = payoutInstruction.getEndDate();

        if (startDate != null && endDate != null) {
            if (startDate.after(endDate)) {
                if (index == -1) {
                    putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_PAY_INSTRUCTIONS_TAB + "." + EndowPropertyConstants.KEMID_PAY_INC_START_DATE, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_PAYOUT_INSTRUCTION_START_DATE_SHOULD_BE_PRIOR_TO_END_DATE);
                }
                else {
                    putFieldError(EndowPropertyConstants.KEMID_PAY_INSTRUCTIONS_TAB + "[" + index + "]" + "." + EndowPropertyConstants.KEMID_PAY_INC_START_DATE, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_PAYOUT_INSTRUCTION_START_DATE_SHOULD_BE_PRIOR_TO_END_DATE);
                }
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;

    }

    /**
     * Checks that the use criteria exists.
     * 
     * @param useCriteria
     * @return true if it exists, false otherwise
     */
    private boolean checkUseCriteria(KemidUseCriteria useCriteria) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_USE_CRITERIA_TAB + ".";

        // check that the use criteria exists
        if (StringUtils.isNotBlank(useCriteria.getUseCriteriaCode())) {
            useCriteria.refreshReferenceObject(EndowPropertyConstants.KEMID_USE_CRIT);

            if (ObjectUtils.isNull(useCriteria.getUseCriteria())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidUseCriteria.class, EndowPropertyConstants.KEMID_USE_CRIT_CD);
                String message = label + "(" + useCriteria.getUseCriteriaCode() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_USE_CRIT_CD, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;

    }

    /**
     * Checks that the agreement special instruction exists.
     * 
     * @param specialInstruction
     * @return true if it exists, false otherwise
     */
    private boolean checkSpecialInstruction(KemidSpecialInstruction specialInstruction) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_SPECIAL_INSTRUCTIONS_TAB + ".";

        // check that the agreement special instruction exists
        if (StringUtils.isNotBlank(specialInstruction.getAgreementSpecialInstructionCode())) {
            specialInstruction.refreshReferenceObject(EndowPropertyConstants.KEMID_SPEC_INSTR);

            if (ObjectUtils.isNull(specialInstruction.getAgreementSpecialInstruction())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidSpecialInstruction.class, EndowPropertyConstants.KEMID_SPEC_INSTR_CD);
                String message = label + "(" + specialInstruction.getAgreementSpecialInstructionCode() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_SPEC_INSTR_CD, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;

    }

    /**
     * Checks that the fee method, charge fee to kemid exist.
     * 
     * @param fee
     * @return true if it exist, false otherwise
     */
    private boolean checkFee(KemidFee fee) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_FEES_TAB + ".";

        // check that the fee method exists
        if (StringUtils.isNotBlank(fee.getFeeMethodCode())) {
            fee.refreshReferenceObject(EndowPropertyConstants.KEMID_FEE_MTHD);

            if (ObjectUtils.isNull(fee.getFeeMethod())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidFee.class, EndowPropertyConstants.KEMID_FEE_MTHD_CD);
                String message = label + "(" + fee.getFeeMethodCode() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_FEE_MTHD_CD, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        // check that charge fee to kemid exists
        if (StringUtils.isNotBlank(fee.getChargeFeeToKemid())) {
            fee.refreshReferenceObject(EndowPropertyConstants.KEMID_FEE_CHARGE_FEE_TO_KEMID_OBJ_REF);

            if (ObjectUtils.isNull(fee.getChargeFeeToKemidObjRef())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidFee.class, EndowPropertyConstants.KEMID_FEE_CHARGE_FEE_TO_KEMID);
                String message = label + "(" + fee.getChargeFeeToKemid() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_FEE_CHARGE_FEE_TO_KEMID, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;

    }

    /**
     * Checks that the combine Group exists.
     * 
     * @param reportGroup
     * @return true if it exists, false otherwise
     */
    private boolean checkReportGroup(KemidReportGroup reportGroup) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_REPORT_GROUP_TAB + ".";

        // check that the combine Group exists
        if (StringUtils.isNotBlank(reportGroup.getCombineGroupCode())) {
            reportGroup.refreshReferenceObject(EndowPropertyConstants.KEMID_REPORT_GRP);

            if (ObjectUtils.isNull(reportGroup.getCombineGroup())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidReportGroup.class, EndowPropertyConstants.KEMID_REPORT_GRP_CD);
                String message = label + "(" + reportGroup.getCombineGroupCode() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_REPORT_GRP_CD, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;

    }

    /**
     * Checks that the donor, donor statement, combine with donor and donor label exist.
     * 
     * @param donorStatement
     * @return true if they exist, false otherwise
     */
    private boolean checkDonorStatement(KemidDonorStatement donorStatement) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_DONOR_STATEMENTS_TAB + ".";

        // check that the donor exists
        if (StringUtils.isNotBlank(donorStatement.getDonorId())) {
            donorStatement.refreshReferenceObject(EndowPropertyConstants.KEMID_DONOR_STATEMENT_DONOR);

            if (ObjectUtils.isNull(donorStatement.getDonor())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidDonorStatement.class, EndowPropertyConstants.KEMID_DONOR_STATEMENT_ID);
                String message = label + "(" + donorStatement.getDonorId() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_DONOR_STATEMENT_ID, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        // check that the donor statement exists
        if (StringUtils.isNotBlank(donorStatement.getDonorStatementCode())) {
            donorStatement.refreshReferenceObject(EndowPropertyConstants.KEMID_DONOR_STATEMENT);

            if (ObjectUtils.isNull(donorStatement.getDonor())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidDonorStatement.class, EndowPropertyConstants.KEMID_DONOR_STATEMENT_CD);
                String message = label + "(" + donorStatement.getDonorStatementCode() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_DONOR_STATEMENT_CD, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        // check that the combine with donor exists
        if (StringUtils.isNotBlank(donorStatement.getCombineWithDonorId())) {
            donorStatement.refreshReferenceObject(EndowPropertyConstants.KEMID_DONOR_STATEMENT_COMBINE_WITH_DONOR);

            if (ObjectUtils.isNull(donorStatement.getCombineWithDonor())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidDonorStatement.class, EndowPropertyConstants.KEMID_DONOR_STATEMENT_COMBINE_WITH_DONOR_ID);
                String message = label + "(" + donorStatement.getCombineWithDonorId() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_DONOR_STATEMENT_COMBINE_WITH_DONOR_ID, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        // check that the donor label exists
        if (StringUtils.isNotBlank(donorStatement.getDonorLabel())) {
            donorStatement.refreshReferenceObject(EndowPropertyConstants.KEMID_DONOR_STATEMENT_DONOR_LABEL_OBJ_REF);

            if (ObjectUtils.isNull(donorStatement.getDonorLabelObjRef())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidDonorStatement.class, EndowPropertyConstants.KEMID_DONOR_STATEMENT_DONOR_LABEL);
                String message = label + "(" + donorStatement.getDonorLabel() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_DONOR_STATEMENT_DONOR_LABEL, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;

    }

    /**
     * Checks that the combine with kemid exists.
     * 
     * @param combineDonorStatement
     * @return true if it exists, false otherwise
     */
    private boolean checkCombineDonorStatement(KemidCombineDonorStatement combineDonorStatement) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_COMBINE_DONOR_STATEMENTS_TAB + ".";

        // check that the combine with kemid exists
        if (StringUtils.isNotBlank(combineDonorStatement.getCombineWithKemid())) {
            combineDonorStatement.refreshReferenceObject(EndowPropertyConstants.KEMID_COMBINE_DONOR_STATEMENT_WITH_KEMID_OBJ_REF);

            if (ObjectUtils.isNull(combineDonorStatement.getCombineWithKemidObjRef())) {
                String label = this.getDataDictionaryService().getAttributeLabel(KemidCombineDonorStatement.class, EndowPropertyConstants.KEMID_COMBINE_DONOR_STATEMENT_WITH_KEMID);
                String message = label + "(" + combineDonorStatement.getCombineWithKemid() + ")";

                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_COMBINE_DONOR_STATEMENT_WITH_KEMID, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;

    }

    /**
     * Checks that a valid Reason Closed is entered whe the Closed indicator is "Yes".
     * 
     * @return true if valid, false otherwise
     */
    private boolean checkCloseCode() {
        boolean valid = true;

        if (newKemid.isClose()) {
            String closeCode = newKemid.getCloseCode();

            Map pkMap = new HashMap();
            pkMap.put(EndowPropertyConstants.ENDOWCODEBASE_CODE, closeCode);
            CloseCode reasonClosed = (CloseCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CloseCode.class, pkMap);

            if (ObjectUtils.isNull(reasonClosed)) {
                putFieldError(EndowPropertyConstants.KEMID_CLOSE_CODE, EndowKeyConstants.KEMIDConstants.ERROR_INVALID_CLOSED_CODE);
                valid = false;
            }
        }
        return valid;

    }

    /**
     * Checks if the kemid has current cash open records in case the closed indicator is "Yes".
     * 
     * @return true if it does not have open records, false otherwise
     */
    private boolean checkIfKemidHasCurrentCashOpenRecordsIfClosed() {
        boolean valid = true;

        if (newKemid.isClose()) {
            String kemid = newKemid.getKemid();
            boolean hasOpenRecords = SpringContext.getBean(KemidCurrentCashService.class).hasKemidOpenRecordsInCurrentCash(kemid);

            valid = !hasOpenRecords;

            if (!valid) {
                putFieldError(EndowPropertyConstants.KEMID_CLOSED_IND, EndowKeyConstants.KEMIDConstants.ERROR_HAS_OPEN_RECORDS_IN_CURRENT_CASH);
            }
        }
        return valid;

    }

    /**
     * Checks if the kemid has holding tax lot open records in case the closed indicator is "Yes".
     * 
     * @return true if it does not have open records, false otherwise
     */
    private boolean checkIfKemidHasHoldingTaxLotOpenRecordsIfClosed() {
        boolean valid = true;

        if (newKemid.isClose()) {
            String kemid = newKemid.getKemid();
            boolean hasOpenRecords = SpringContext.getBean(KemidHoldingTaxLotOpenRecordsService.class).hasKemidHoldingTaxLotOpenRecords(kemid);

            valid = !hasOpenRecords;

            if (!valid) {
                putFieldError(EndowPropertyConstants.KEMID_CLOSED_IND, EndowKeyConstants.KEMIDConstants.ERROR_HAS_OPEN_RECORDS_IN_HOLDING_TAX_LOT);
            }
        }
        return valid;

    }

    /**
     * Checks that the KEMID has at least one ACTIVE Agreement set up.
     * 
     * @return true if it has at least one Agreement, false otherwise
     */
    private boolean validateAgreements() {
        boolean valid = true;
        boolean hasActiveRecord = false;

        if (newKemid.getKemidAgreements() == null || newKemid.getKemidAgreements().size() == 0) {
            putFieldError(EndowPropertyConstants.KEMID_AGREEMENTS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_AGREEMENT);
            valid = false;
        }
        else {
            // Make sure that the KEMID has at least one ACTIVE Agreement
            for (KemidAgreement kemidAgreement : newKemid.getKemidAgreements()) {
                if (kemidAgreement.isActive()) {
                    hasActiveRecord = true;
                    break;
                }
            }

        }
        if (!hasActiveRecord) {
            putFieldError(EndowPropertyConstants.KEMID_AGREEMENTS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_AGREEMENT);
            valid = false;
        }

        return valid;
    }

    /**
     * Checks that only one Agreement has the Use Transaction Restriction From Agreement checked.
     * 
     * @return true if valid, false otherwise
     */
    private boolean validateUseTransactionRestrictionFromAgreement() {
        boolean valid = true;
        boolean useTransactionRestrictionFromAgreementFound = false;

        for (KemidAgreement kemidAgreement : newKemid.getKemidAgreements()) {
            if (kemidAgreement.isUseTransactionRestrictionFromAgreement()) {
                if (useTransactionRestrictionFromAgreementFound) {
                    putFieldError(EndowPropertyConstants.KEMID_AGREEMENTS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_ONLY_ONE_AGREEMENT_CAN_BR_MARKED_FOR_TRANSACTION_RESTR_USE);
                    valid = false;
                    break;
                }
                useTransactionRestrictionFromAgreementFound = true;
            }

        }
        return valid;
    }

    /**
     * Validates that the KEMID has at least one Source of Funds defined.
     * 
     * @return true if valid, false otherwise
     */
    private boolean validateSourceOfFunds() {
        boolean valid = true;
        boolean hasActiveRecord = false;

        if (newKemid.getKemidSourcesOfFunds() == null || newKemid.getKemidSourcesOfFunds().size() == 0) {
            putFieldError(EndowPropertyConstants.KEMID_SOURCE_OF_FUNDS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_SOURCE_OF_FUNDS);
            valid = false;
        }
        else {
            // Make sure that the KEMID has at least one ACTIVE Source of Funds
            for (KemidSourceOfFunds kemidSourceOfFunds : newKemid.getKemidSourcesOfFunds()) {
                if (kemidSourceOfFunds.isActive()) {
                    hasActiveRecord = true;
                    break;
                }
            }
        }
        if (!hasActiveRecord) {
            putFieldError(EndowPropertyConstants.KEMID_SOURCE_OF_FUNDS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_SOURCE_OF_FUNDS);
            valid = false;
        }

        return valid;
    }

    /**
     * Validates that the KEMID has at least one ACTIVE Benefitting Org defined.
     * 
     * @return true if valid, false otherwise
     */
    private boolean validateBenefittingOrgs() {
        boolean valid = true;
        boolean hasActiveRecord = false;

        if (newKemid.getKemidBenefittingOrganizations() == null || newKemid.getKemidBenefittingOrganizations().size() == 0) {
            putFieldError(EndowPropertyConstants.KEMID_BENEFITTING_ORGS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_BENEFITTING_ORG);
            valid = false;
        }
        else {
            // Make sure that the KEMID has at least one ACTIVE Benefitting Org
            for (KemidBenefittingOrganization kemidBenefittingOrganization : newKemid.getKemidBenefittingOrganizations()) {
                if (kemidBenefittingOrganization.isActive()) {
                    hasActiveRecord = true;
                    break;
                }
            }
            if (!hasActiveRecord) {
                putFieldError(EndowPropertyConstants.KEMID_BENEFITTING_ORGS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_BENEFITTING_ORG);
                return false;
            }
            // Check: the total of BENE_PCT for all records where ROW_ACTV_IND is equal to Yes must be 1(100%).
            KualiDecimal benefittingPercentage = KualiDecimal.ZERO;
            for (KemidBenefittingOrganization benefittingOrganization : newKemid.getKemidBenefittingOrganizations()) {
                if (benefittingOrganization.isActive()) {
                    benefittingPercentage = benefittingPercentage.add(benefittingOrganization.getBenefitPrecent());
                }
            }

            if (benefittingPercentage.compareTo(new KualiDecimal(1)) != 0) {
                putFieldError(EndowPropertyConstants.KEMID_BENEFITTING_ORGS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_ACTIVE_BENE_ORGS_PCT_SUM_MUST_BE_ONE);
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Validates the GeneralLedgerAccounts tab. In KEMID spec, section 6.5.1.1, item 1 and 2, the rules should be updated to : 1.
     * One and ONLY ONE ACTIVE END_KEMID_GL_LNK_T record with the IP_IND_CD field equal to I must exist for each END_KEMID_T record.
     * 2. One and ONLY ONE ACTIVE END_KEMID_GL_LNK_T record with the IP_IND_CD field equal to P must exist for each END_KEMID_T
     * record where the TYP_PRIN_RESTR_CD for the associated END_KEMID_T: TYP_CD is NOT equal to NA (Not Applicable) 3. If the
     * TYP_PRIN_RESTR_CD for the associated END_KEMID_T: TYP_CD is equal to NA (Not Applicable), each END_KEMID_T record can have
     * either zero or one INACTIVE END_KEMID_GL_LNK_T record with the IP_IND_CD field equal to P
     * 
     * @return true if valid, false otherwise
     */
    private boolean validateGeneralLedgerAccounts() {
        boolean valid = true;

        boolean hasIncomeGL = false;
        boolean hasPrincipalGL = false;
        boolean hasActiveIncomeGL = false;
        boolean hasActivePrincipalGL = false;


        if (newKemid.getKemidGeneralLedgerAccounts() == null || newKemid.getKemidGeneralLedgerAccounts().size() == 0) {
            putFieldError(EndowPropertyConstants.KEMID_GENERAL_LEDGER_ACCOUNTS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_INCOME_GL_ACC);
            return false;
        }
        else {
            valid &= validateIncomePrincipalGLAccounts(newKemid.getKemidGeneralLedgerAccounts());
        }

        return valid;

    }

    /**
     * Validates that there is no more than one active entry with IP indicator I or P, that there is at least one active income GL
     * account, if principal restriction code is NA then there is no principal GL account and if principal restriction code is not
     * NA then there is at least one principal GL account.
     * 
     * @param generalLedgerAccounts
     * @return true if valid, false otherwise
     */
    private boolean validateIncomePrincipalGLAccounts(List<KemidGeneralLedgerAccount> generalLedgerAccounts) {
        boolean valid = true;

        boolean hasIncomeGL = false;
        boolean hasPrincipalGL = false;
        boolean hasActiveIncomeGL = false;
        boolean hasActivePrincipalGL = false;


        if (generalLedgerAccounts != null && generalLedgerAccounts.size() != 0) {
            for (KemidGeneralLedgerAccount kemidGeneralLedgerAccount : generalLedgerAccounts) {
                if (kemidGeneralLedgerAccount.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
                    // One and ONLY ONE END_KEMID_GL_LNK_T record with the IP_IND_CD field equal to I must exist for each
                    // END_KEMID_T record.
                    if (!hasIncomeGL) {
                        hasIncomeGL = true;
                    }
                    else {
                        // Error: There are more than one END_KEMID_GL_LNK_T record with the IP_IND_CD field equal to I
                        putFieldError(EndowPropertyConstants.KEMID_GENERAL_LEDGER_ACCOUNTS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_CAN_ONLY_HAVE_ONE_INCOME_GL_ACC);
                        return false;
                    }
                    if (hasIncomeGL) {
                        hasActiveIncomeGL = kemidGeneralLedgerAccount.isActive();
                    }
                }
                else if (kemidGeneralLedgerAccount.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
                    if (!hasPrincipalGL) {
                        hasPrincipalGL = true;
                    }
                    else {
                        // Error: There is more than one END_KEMID_GL_LNK_T record with the IP_IND_CD field equal to P
                        putFieldError(EndowPropertyConstants.KEMID_GENERAL_LEDGER_ACCOUNTS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_CAN_ONLY_HAVE_ONE_PRINCIPAL_GL_ACC);
                        return false;
                    }
                    if (hasPrincipalGL) {
                        hasActivePrincipalGL = kemidGeneralLedgerAccount.isActive();
                    }
                }
                
                hasActivePrincipalGL = kemidGeneralLedgerAccount.isActive();
            }

            if (!hasIncomeGL || !hasActiveIncomeGL) {
                putFieldError(EndowPropertyConstants.KEMID_GENERAL_LEDGER_ACCOUNTS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_INCOME_GL_ACC);
                return false;
            }

            if (newKemid.getPrincipalRestrictionCode() != null && newKemid.getPrincipalRestrictionCode().equalsIgnoreCase(EndowConstants.TypeRestrictionPresetValueCodes.NOT_APPLICABLE_TYPE_RESTRICTION_CODE) && hasActivePrincipalGL) {
                putFieldError(EndowPropertyConstants.KEMID_GENERAL_LEDGER_ACCOUNTS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_CAN_NOT_HAVE_A_PRINCIPAL_GL_ACC_IF_PRINCIPAL_RESTR_CD_IS_NA);
                return false;
            }

            if (newKemid.getPrincipalRestrictionCode() != null && !newKemid.getPrincipalRestrictionCode().equalsIgnoreCase(EndowConstants.TypeRestrictionPresetValueCodes.NOT_APPLICABLE_TYPE_RESTRICTION_CODE) && !hasActivePrincipalGL) {
                putFieldError(EndowPropertyConstants.KEMID_GENERAL_LEDGER_ACCOUNTS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_PRINCIPAL_GL_ACC_IF_PRINCIPAL_CD_NOT_NA);
                return false;
            }
        }

        return valid;
    }

    /**
     * Validates the KEMID Authorizations.
     * 
     * @return true if valid, false otherwise
     */
    private boolean validateKemidAuthorizations() {
        boolean isValid = true;
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_AUTHORIZATIONS_TAB + ".";
        List<KemidAuthorizations> authorizations = newKemid.getKemidAuthorizations();

        // if sys param END_KEMID_ROLE_T_RECORD_REQUIRED_IND is yes the Kemid must have at least one active entry in the
        // authorizations tab
        String authorizationReqParamVal = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KEMID.class, EndowParameterKeyConstants.ROLE_REQUIRED_IND);

        if (KFSConstants.ParameterValues.YES.equalsIgnoreCase(authorizationReqParamVal)) {
            // At least one active records must exist
            if (authorizations == null || authorizations.size() == 0) {
                putFieldError(EndowPropertyConstants.KEMID_AUTHORIZATIONS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_AUTHORIZATION);
                return false;
            }
            isValid &= validateKemidAuthorizationsHaveOneActiveEntry();
        }

        // check all authorizations are valid
        for (int i = 0; i < authorizations.size(); i++) {
            KemidAuthorizations authorization = (KemidAuthorizations) authorizations.get(i);
            isValid &= checkAuthorization(authorization, i);
        }

        return isValid;
    }

    /**
     * Checks if the Authorizations tab has at least one active entry.
     * 
     * @return true if it has one, false otherwise
     */
    private boolean validateKemidAuthorizationsHaveOneActiveEntry() {
        boolean hasActiveAuthorization = false;
        for (KemidAuthorizations authorization : newKemid.getKemidAuthorizations()) {
            if (authorization.isActive()) {
                hasActiveAuthorization = true;
                break;
            }
        }
        return hasActiveAuthorization;
    }

    /**
     * Validates that the role namespace is KFS-ENDOW.
     * 
     * @param authorization
     * @return true if valid, false otherwise
     */
    private boolean validateRoleInKFSEndowNamespace(KemidAuthorizations authorization, int index) {

        if (!authorization.getRole().getNamespaceCode().equalsIgnoreCase(EndowConstants.KFS_ENDOW_ROLE_NAMESPACE)) {
            if (index == -1) {
                putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_AUTHORIZATIONS_TAB + "." + EndowPropertyConstants.KEMID_AUTHORIZATIONS_ROLE_ID, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_AUTHORIZATION_ROLE_NAMESPACE_ENDOW);
            }
            else {
                putFieldError(EndowPropertyConstants.KEMID_AUTHORIZATIONS_TAB + "[" + index + "]" + "." + EndowPropertyConstants.KEMID_AUTHORIZATIONS_ROLE_ID, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_AUTHORIZATION_ROLE_NAMESPACE_ENDOW);
            }
            return false;
        }
        else
            return true;

    }

    /**
     * Validates KEMID Donor Statements.
     * 
     * @return true if valid, false otherwise
     */
    private boolean validateKemidDonorStatements() {
        boolean isValid = true;
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_DONOR_STATEMENTS_TAB + ".";
        for (KemidDonorStatement donorStatement : newKemid.getKemidDonorStatements()) {
            if (!validCombineWithDonorId(donorStatement)) {
                isValid = false;
            }

            if (donorStatement.getTerminationDate() != null && StringUtils.isEmpty(donorStatement.getTerminationReason())) {
                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_DONOR_STATEMENT_TERMINATION_REASON, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_DONOR_STMNT_TERM_RSN_CANT_BE_EMPTY_IS_TERM_DATE_ENTERED);
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * Checks that the combine with donor is different from the donor.
     * 
     * @param donorStatement
     * @return true if valid, false otherwise
     */
    private boolean validCombineWithDonorId(KemidDonorStatement donorStatement) {
        String combineWithDonorId = donorStatement.getCombineWithDonorId();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_DONOR_STATEMENTS_TAB + ".";

        if (StringUtils.isNotEmpty(combineWithDonorId)) {
            if (combineWithDonorId.equalsIgnoreCase(donorStatement.getDonorId())) {
                putFieldError(errorPathPrefix + EndowPropertyConstants.KEMID_DONOR_STATEMENT_COMBINE_WITH_DONOR_ID, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_DONOR_STMNT_COMBINE_WITH_DONR_MUST_BE_DIFF_FROM_DONOR);
                return false;
            }

        }
        return true;

    }

    /**
     * Validates that the KEMID has at least one Pay Instruction defined.
     * 
     * @return true if valid, false otherwise
     */
    private boolean validatePayoutInstructions() {
        boolean valid = true;

        if (newKemid.getKemidPayoutInstructions() == null || newKemid.getKemidPayoutInstructions().size() == 0) {
            putFieldError(EndowPropertyConstants.KEMID_PAY_INSTRUCTIONS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_PAYOUT_INSTRUCTION);
            valid = false;
        }

        if (valid) {
            int index = 0;
            for (KemidPayoutInstruction payoutInstruction : newKemid.getKemidPayoutInstructions()) {
                checkPayoutInstruction(payoutInstruction, index);
                index++;
            }
            validatePayoutInstructionsPercentTotal();
        }

        return valid;
    }

    /**
     * Validates that the total of all non-terminated records is 1 (100%).
     * 
     * @return true if valid, false otherwise
     */
    private boolean validatePayoutInstructionsPercentTotal() {
        boolean isValid = true;
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        Date currentDate = dateTimeService.getCurrentSqlDate();
        KualiDecimal total = KualiDecimal.ZERO;

        for (KemidPayoutInstruction payoutInstruction : newKemid.getKemidPayoutInstructions()) {
            if (payoutInstruction.getEndDate() == null || payoutInstruction.getEndDate().after(currentDate)) {
                total = total.add(payoutInstruction.getPercentOfIncomeToPayToKemid());
            }
        }
        KualiDecimal one = new KualiDecimal(1);
        if (one.compareTo(total) != 0) {
            putFieldError(EndowPropertyConstants.KEMID_PAY_INSTRUCTIONS_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_TOTAL_OFF_ALL_PAYOUT_RECORDS_MUST_BE_ONE);
            isValid = false;
        }
        return isValid;
    }

    /**
     * Validates the Kemid Fees.
     * 
     * @return true if valid, false otherwise
     */
    private boolean validateFees() {
        boolean valid = true;
        List<KemidFee> fees = newKemid.getKemidFees();

        if (fees != null && fees.size() != 0) {
            for (int i = 0; i < fees.size(); i++) {
                KemidFee fee = fees.get(i);
                if (!validateFeePercentageTotal(fee, i)) {
                    valid = false;
                }
                if (!validatePercentageOfFeeChargedToPrincipal(fee, i)) {
                    valid = false;
                }
                if (!validateKemidFeeStartDate(fee, i)) {
                    valid = false;
                }
            }
        }
        return valid;
    }

    /**
     * Validates that the total of the Percentage of Fee Charged to Income plus Percentage Of Fee Charged to Principal cannot exceed
     * 1 (100%).
     * 
     * @param fee
     * @return true if valid, false otherwise
     */
    private boolean validateFeePercentageTotal(KemidFee fee, int index) {
        boolean valid = true;
        KualiDecimal percentage = fee.getPercentOfFeeChargedToIncome().add(fee.getPercentOfFeeChargedToPrincipal());
        if (percentage.isGreaterThan(new KualiDecimal(1))) {
            valid = false;
            if (index != -1) {
                putFieldError(EndowPropertyConstants.KEMID_FEES_TAB + "[" + index + "]", EndowKeyConstants.KEMIDConstants.ERROR_KEMID_FEE_PCT_CHRG_FEE_SUM_MUST_NOT_BE_GREATER_THAN_ONE);
            }
            else {
                putFieldError(EndowPropertyConstants.KEMID_FEES_TAB, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_FEE_PCT_CHRG_FEE_SUM_MUST_NOT_BE_GREATER_THAN_ONE);
            }
        }
        return valid;
    }

    /**
     * Validates that the percentage if fee charged to principal does not exceed zero when the type restriction code is NA(Not
     * Available).
     * 
     * @param fee
     * @return true if valid, false otherwise
     */
    private boolean validatePercentageOfFeeChargedToPrincipal(KemidFee fee, int index) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(newKemid.getType()) && EndowConstants.TypeRestrictionPresetValueCodes.NOT_APPLICABLE_TYPE_RESTRICTION_CODE.equalsIgnoreCase(newKemid.getPrincipalRestrictionCode())) {
            if (fee.getPercentOfFeeChargedToPrincipal().isGreaterThan(KualiDecimal.ZERO)) {
                valid = false;
                if (index >= 0) {
                    putFieldError(EndowPropertyConstants.KEMID_FEES_TAB + "[" + index + "]" + "." + EndowPropertyConstants.KEMID_FEE_PERCENT_OF_FEE_CHARGED_TO_PRINCIPAL, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_FEE_PCT_CHRG_TO_PRIN_CANNOT_EXCEED_ZERO_IF_TYPE_RESTR_CD_NA);
                }
                else {
                    putFieldError(EndowPropertyConstants.KEMID_FEE_PERCENT_OF_FEE_CHARGED_TO_PRINCIPAL, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_FEE_PCT_CHRG_TO_PRIN_CANNOT_EXCEED_ZERO_IF_TYPE_RESTR_CD_NA);
                }
            }
        }
        return valid;

    }

    /**
     * Validates that the kemid fee start date is a valid value for the fee frequency.
     * 
     * @param fee
     * @return true if valid, false otherwise
     */
    private boolean validateKemidFeeStartDate(KemidFee fee, int index) {
        boolean isValid = true;
        ValidateDateBasedOnFrequencyCodeService validateService = SpringContext.getBean(ValidateDateBasedOnFrequencyCodeService.class);

        Date feeStartDate = fee.getFeeStartDate();
        fee.refreshReferenceObject(EndowPropertyConstants.FEE_METHOD);

        String frequencyCode = fee.getFeeMethod() != null ? fee.getFeeMethod().getFeeFrequencyCode() : null;

        if (feeStartDate != null && frequencyCode != null) {
            isValid = validateService.validateDateBasedOnFrequencyCode(feeStartDate, frequencyCode);
        }

        if (!isValid) {
            if (index == -1) {
                putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + EndowPropertyConstants.KEMID_FEES_TAB + "." + EndowPropertyConstants.KEMID_FEE_START_DATE, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_FEE_START_DATE_NOT_VALID);
            }
            else {
                putFieldError(EndowPropertyConstants.KEMID_FEES_TAB + "[" + index + "]" + "." + EndowPropertyConstants.KEMID_FEE_START_DATE, EndowKeyConstants.KEMIDConstants.ERROR_KEMID_FEE_START_DATE_NOT_VALID);
            }

        }

        return isValid;
    }
}
