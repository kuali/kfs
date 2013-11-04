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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidAgreement;
import org.kuali.kfs.module.endow.businessobject.KemidAuthorizations;
import org.kuali.kfs.module.endow.businessobject.KemidBenefittingOrganization;
import org.kuali.kfs.module.endow.businessobject.KemidCombineDonorStatement;
import org.kuali.kfs.module.endow.businessobject.KemidDonorStatement;
import org.kuali.kfs.module.endow.businessobject.KemidFee;
import org.kuali.kfs.module.endow.businessobject.KemidPayoutInstruction;
import org.kuali.kfs.module.endow.businessobject.KemidReportGroup;
import org.kuali.kfs.module.endow.businessobject.KemidSourceOfFunds;
import org.kuali.kfs.module.endow.businessobject.KemidSpecialInstruction;
import org.kuali.kfs.module.endow.businessobject.KemidUseCriteria;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This KemidPreRule class implements the pre rules for the Kemid BO.
 */
public class KemidPreRule extends MaintenancePreRulesBase {

    private KEMID oldKemid;
    private KEMID newKemid;

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean doCustomPreRules(MaintenanceDocument maintenanceDocument) {
        boolean preRulesOK = true;
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        String kemidValueSystemParam = parameterService.getParameterValueAsString(KEMID.class, EndowParameterKeyConstants.KEMID_VALUE);

        setupConvenienceObjects(maintenanceDocument);

        setAgreementsIds();
        setSourceOfFundsSeq();
        updateBenefittingOrgs(maintenanceDocument);
        updateAuthorizations(maintenanceDocument);
        setPayoutInstructionsSeq();
        setUseCriteriaSeq();
        setFeeMethodSequence();
        setReportGroupSequence();
        setSpecialInstructionSeq();
        setDonorStatementsSeq();
        setCombineDonorSeq();

        return preRulesOK;
    }

    /**
     * Sets the ID for all the new agreements as the next sequencial number.
     */
    private void setAgreementsIds() {
        List<KemidAgreement> oldAgreements = new ArrayList<KemidAgreement>();
        List<KemidAgreement> newAgreements = new ArrayList<KemidAgreement>();


        for (KemidAgreement kemidAgreement : newKemid.getKemidAgreements()) {
            if (kemidAgreement.isNewCollectionRecord()) {
                newAgreements.add(kemidAgreement);
            }
            else {
                oldAgreements.add(kemidAgreement);
            }
        }

        int sequenceStart = oldAgreements.size();

        for (KemidAgreement agreement : newAgreements) {
            agreement.setAgreementId(new KualiInteger(++sequenceStart));
        }

    }

    /**
     * Sets the source of fund sequence number for all the new sources of funds.
     */
    private void setSourceOfFundsSeq() {
        List<KemidSourceOfFunds> oldKemidSourcesOfFunds = new ArrayList<KemidSourceOfFunds>();
        List<KemidSourceOfFunds> newKemidSourcesOfFunds = new ArrayList<KemidSourceOfFunds>();


        for (KemidSourceOfFunds kemidSourceOfFunds : newKemid.getKemidSourcesOfFunds()) {
            if (kemidSourceOfFunds.isNewCollectionRecord()) {
                newKemidSourcesOfFunds.add(kemidSourceOfFunds);
            }
            else {
                oldKemidSourcesOfFunds.add(kemidSourceOfFunds);
            }
        }

        int sequenceStart = oldKemidSourcesOfFunds.size();

        for (KemidSourceOfFunds kemidSourceOfFunds : newKemidSourcesOfFunds) {
            kemidSourceOfFunds.setKemidFundSourceSequenceNumber(new KualiInteger(++sequenceStart));
        }

    }

    /**
     * Sets the benefitting organization sequence number for all the new benefitting organizations.
     */
    private void setBenefittingOrgsSeq() {
        List<KemidBenefittingOrganization> oldKemidBenefittingOrgs = new ArrayList<KemidBenefittingOrganization>();
        List<KemidBenefittingOrganization> newKemidBenefittingOrgs = new ArrayList<KemidBenefittingOrganization>();


        for (KemidBenefittingOrganization kemidBenefittingOrg : newKemid.getKemidBenefittingOrganizations()) {
            if (kemidBenefittingOrg.isNewCollectionRecord()) {
                newKemidBenefittingOrgs.add(kemidBenefittingOrg);
            }
            else {
                oldKemidBenefittingOrgs.add(kemidBenefittingOrg);
            }
        }

        int sequenceStart = oldKemidBenefittingOrgs.size();

        for (KemidBenefittingOrganization kemidBenefittingOrg : newKemidBenefittingOrgs) {
            kemidBenefittingOrg.setBenefittingOrgSeqNumber(new KualiInteger(++sequenceStart));
        }

    }

    /**
     * Sets the benefitting organization last change date to the current date if the benefit percent changed. This method should
     * only be called after the setBenefittingOrgsSeq method is called.
     * 
     * @param maintenanceDocument
     */
    private void setBenefittingOrgsLastChangeDate(MaintenanceDocument maintenanceDocument) {

        KEMService kemService = SpringContext.getBean(KEMService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceDocument.getNewMaintainableObject().getMaintenanceAction())) {
            if (newKemid.getKemidBenefittingOrganizations().size() != 0) {

                Map<KualiInteger, KemidBenefittingOrganization> oldKemidBenefittingOrganizations = new HashMap<KualiInteger, KemidBenefittingOrganization>();

                if (oldKemid.getKemidBenefittingOrganizations().size() != 0) {
                    for (KemidBenefittingOrganization benefittingOrganization : oldKemid.getKemidBenefittingOrganizations()) {
                        oldKemidBenefittingOrganizations.put(benefittingOrganization.getBenefittingOrgSeqNumber(), benefittingOrganization);
                    }

                }

                for (KemidBenefittingOrganization benefittingOrganization : newKemid.getKemidBenefittingOrganizations()) {
                    KemidBenefittingOrganization oldBenefittingOrg = oldKemidBenefittingOrganizations.get(benefittingOrganization.getBenefittingOrgSeqNumber());

                    boolean isBenefitPctChanged = false;
                    if (ObjectUtils.isNotNull(oldBenefittingOrg)) {
                        isBenefitPctChanged = (benefittingOrganization.getBenefitPrecent() == null && oldBenefittingOrg.getBenefitPrecent() != null) || (benefittingOrganization.getBenefitPrecent() != null && oldBenefittingOrg.getBenefitPrecent() == null) || (benefittingOrganization.getBenefitPrecent() != null && oldBenefittingOrg.getBenefitPrecent() != null && benefittingOrganization.getBenefitPrecent().compareTo(oldBenefittingOrg.getBenefitPrecent()) != 0);


                    }

                    if (isBenefitPctChanged || benefittingOrganization.isNewCollectionRecord()) {
                        Date lastChangeDate;
                        try {
                            lastChangeDate = dateTimeService.convertToSqlDate(kemService.getCurrentSystemProcessDate());
                            benefittingOrganization.setLastChangeDate(lastChangeDate);
                        }
                        catch (ParseException ex) {
                            // do nothing
                        }

                    }
                }
            }
        }

    }

    /**
     * Updates the necessary fields on the Benefitting organizations like the sequence numbers and the last change date.
     * 
     * @param maintenanceDocument
     */
    private void updateBenefittingOrgs(MaintenanceDocument maintenanceDocument) {
        // the order in which these methods are called should be preserved
        setBenefittingOrgsSeq();
        setBenefittingOrgsLastChangeDate(maintenanceDocument);
    }

    /**
     * Sets the Authorizations sequence number for all the new Authorizations.
     */
    private void setAuthorizationsSeqNbr() {
        List<KemidAuthorizations> oldKemidAuthorizations = new ArrayList<KemidAuthorizations>();
        List<KemidAuthorizations> newKemidAuthorizations = new ArrayList<KemidAuthorizations>();


        for (KemidAuthorizations kemidAuthorizations : newKemid.getKemidAuthorizations()) {
            if (kemidAuthorizations.isNewCollectionRecord()) {
                newKemidAuthorizations.add(kemidAuthorizations);
            }
            else {
                oldKemidAuthorizations.add(kemidAuthorizations);
            }
        }

        int sequenceStart = oldKemidAuthorizations.size();

        for (KemidAuthorizations kemidAuthorizations : newKemidAuthorizations) {
            kemidAuthorizations.setRoleSequenceNumber(new KualiInteger(++sequenceStart));
        }

    }

    /**
     * Updates the necessary fields on the Authorizations like the sequence numbers and the role termination date.
     * 
     * @param maintenanceDocument
     */
    private void updateAuthorizations(MaintenanceDocument maintenanceDocument) {
        // the order in which these methods are called should be preserved
        setAuthorizationsSeqNbr();
        setAuthorizationsTerminationDate(maintenanceDocument);
    }

    /**
     * Sets the role termination date to current date if authorization has been inactivated.
     * 
     * @param maintenanceDocument
     */
    private void setAuthorizationsTerminationDate(MaintenanceDocument maintenanceDocument) {
        KEMService kemService = SpringContext.getBean(KEMService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceDocument.getNewMaintainableObject().getMaintenanceAction())) {
            if (newKemid.getKemidAuthorizations().size() != 0) {

                Map<KualiInteger, KemidAuthorizations> oldKemidAuthorizations = new HashMap<KualiInteger, KemidAuthorizations>();

                if (oldKemid.getKemidAuthorizations().size() != 0) {
                    for (KemidAuthorizations authorization : oldKemid.getKemidAuthorizations()) {
                        oldKemidAuthorizations.put(authorization.getRoleSequenceNumber(), authorization);
                    }
                }

                for (KemidAuthorizations authorization : newKemid.getKemidAuthorizations()) {
                    KemidAuthorizations oldAuthorization = oldKemidAuthorizations.get(authorization.getRoleSequenceNumber());

                    boolean isActiveIndChanged = false;
                    if (ObjectUtils.isNotNull(oldAuthorization)) {
                        isActiveIndChanged = (!authorization.isActive() && oldAuthorization.isActive());

                    }

                    if (isActiveIndChanged) {
                        authorization.setRoleTerminationDate(kemService.getCurrentDate());
                    }
                }
            }
        }
    }

    /**
     * Sets the payout instructions sequence number for all the new payout instructions.
     */
    private void setPayoutInstructionsSeq() {
        List<KemidPayoutInstruction> oldKemidPayoutInstructions = new ArrayList<KemidPayoutInstruction>();
        List<KemidPayoutInstruction> newKemidPayoutInstructions = new ArrayList<KemidPayoutInstruction>();


        for (KemidPayoutInstruction kemidPayoutInstruction : newKemid.getKemidPayoutInstructions()) {
            if (kemidPayoutInstruction.isNewCollectionRecord()) {
                newKemidPayoutInstructions.add(kemidPayoutInstruction);
            }
            else {
                oldKemidPayoutInstructions.add(kemidPayoutInstruction);
            }
        }

        int sequenceStart = oldKemidPayoutInstructions.size();

        for (KemidPayoutInstruction kemidPayoutInstruction : newKemidPayoutInstructions) {
            kemidPayoutInstruction.setPayoutIncomeSequenceNumber(new KualiInteger(++sequenceStart));
        }

    }

    /**
     * Sets the use criteria sequence number for all the new sources of funds.
     */
    private void setUseCriteriaSeq() {
        List<KemidUseCriteria> oldKemidUseCriteria = new ArrayList<KemidUseCriteria>();
        List<KemidUseCriteria> newKemidUseCriteria = new ArrayList<KemidUseCriteria>();


        for (KemidUseCriteria useCriteria : newKemid.getKemidUseCriteria()) {
            if (useCriteria.isNewCollectionRecord()) {
                newKemidUseCriteria.add(useCriteria);
            }
            else {
                oldKemidUseCriteria.add(useCriteria);
            }
        }

        int sequenceStart = oldKemidUseCriteria.size();

        for (KemidUseCriteria useCriteria : newKemidUseCriteria) {
            useCriteria.setUseCriteriaSeq(new KualiInteger(++sequenceStart));
        }

    }

    /**
     * Sets the fee method sequence for all the new fees as the next sequencial number.
     */
    private void setFeeMethodSequence() {
        List<KemidFee> oldFees = new ArrayList<KemidFee>();
        List<KemidFee> newFees = new ArrayList<KemidFee>();


        for (KemidFee kemidFee : newKemid.getKemidFees()) {
            if (kemidFee.isNewCollectionRecord()) {
                newFees.add(kemidFee);
            }
            else {
                oldFees.add(kemidFee);
            }
        }

        int sequenceStart = oldFees.size();

        for (KemidFee fee : newFees) {
            fee.setFeeMethodSeq(new KualiInteger(++sequenceStart));
        }

    }

    /**
     * Sets the fee method sequence for all the new fees as the next sequencial number.
     */
    private void setReportGroupSequence() {
        List<KemidReportGroup> oldReportGroups = new ArrayList<KemidReportGroup>();
        List<KemidReportGroup> newReportGroups = new ArrayList<KemidReportGroup>();


        for (KemidReportGroup kemidReportGroup : newKemid.getKemidReportGroups()) {
            if (kemidReportGroup.isNewCollectionRecord()) {
                newReportGroups.add(kemidReportGroup);
            }
            else {
                oldReportGroups.add(kemidReportGroup);
            }
        }

        int sequenceStart = oldReportGroups.size();

        for (KemidReportGroup reportGroup : newReportGroups) {
            reportGroup.setCombineGroupSeq(new KualiInteger(++sequenceStart));
        }

    }

    /**
     * Sets the special instruction sequence for all the new special instructions as the next sequencial number.
     */
    private void setSpecialInstructionSeq() {
        List<KemidSpecialInstruction> oldSpecialInstructions = new ArrayList<KemidSpecialInstruction>();
        List<KemidSpecialInstruction> newSpecialInstructions = new ArrayList<KemidSpecialInstruction>();


        for (KemidSpecialInstruction kemidSpecialInstruction : newKemid.getKemidSpecialInstructions()) {
            if (kemidSpecialInstruction.isNewCollectionRecord()) {
                newSpecialInstructions.add(kemidSpecialInstruction);
            }
            else {
                oldSpecialInstructions.add(kemidSpecialInstruction);
            }
        }

        int sequenceStart = oldSpecialInstructions.size();

        for (KemidSpecialInstruction specialInstruction : newSpecialInstructions) {
            specialInstruction.setInstructionSeq(new KualiInteger(++sequenceStart));
        }

    }

    /**
     * Sets the donor sequence for all the new donor statements as the next sequencial number.
     */
    private void setDonorStatementsSeq() {
        List<KemidDonorStatement> oldDonorStatement = new ArrayList<KemidDonorStatement>();
        List<KemidDonorStatement> newDonorStatement = new ArrayList<KemidDonorStatement>();


        for (KemidDonorStatement donorStatement : newKemid.getKemidDonorStatements()) {
            if (donorStatement.isNewCollectionRecord()) {
                newDonorStatement.add(donorStatement);
            }
            else {
                oldDonorStatement.add(donorStatement);
            }
        }

        int sequenceStart = oldDonorStatement.size();

        for (KemidDonorStatement donorStatement : newDonorStatement) {
            donorStatement.setDonorSeq(new KualiInteger(++sequenceStart));
        }

    }

    /**
     * Sets the combine donor sequence for all the new combine donor statements as the next sequencial number.
     */
    private void setCombineDonorSeq() {
        List<KemidCombineDonorStatement> oldCombineDonorStatement = new ArrayList<KemidCombineDonorStatement>();
        List<KemidCombineDonorStatement> newCombineDonorStatement = new ArrayList<KemidCombineDonorStatement>();


        for (KemidCombineDonorStatement combineDonorStatement : newKemid.getKemidCombineDonorStatements()) {
            if (combineDonorStatement.isNewCollectionRecord()) {
                newCombineDonorStatement.add(combineDonorStatement);
            }
            else {
                oldCombineDonorStatement.add(combineDonorStatement);
            }
        }

        int sequenceStart = oldCombineDonorStatement.size();

        for (KemidCombineDonorStatement combineDonorStatement : newCombineDonorStatement) {
            combineDonorStatement.setCombineDonorSeq(new KualiInteger(++sequenceStart));
        }

    }

    /**
     * Sets the old and new Kemid values.
     * 
     * @param document
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newSecurity convenience objects, make sure all possible sub-objects are populated
        newKemid = (KEMID) document.getNewMaintainableObject().getBusinessObject();
        oldKemid = (KEMID) document.getOldMaintainableObject().getBusinessObject();
        newKemid.refreshNonUpdateableReferences();
    }

}
