/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.businessobject.inquiry;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidAgreement;
import org.kuali.kfs.module.endow.businessobject.KemidBenefittingOrganization;
import org.kuali.kfs.module.endow.businessobject.KemidCombineDonorStatement;
import org.kuali.kfs.module.endow.businessobject.KemidPayoutInstruction;
import org.kuali.kfs.module.endow.businessobject.KemidReportGroup;
import org.kuali.kfs.module.endow.businessobject.KemidSourceOfFunds;
import org.kuali.kfs.module.endow.businessobject.KemidUseCriteria;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.DateTimeService;

public class KemidInquirableImpl extends KfsInquirableImpl {

    /**
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
     */
    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {

        KEMID kemid = (KEMID) super.getBusinessObject(fieldValues);
        KEMService kemService = SpringContext.getBean(KEMService.class);
        String currentProcessDateString = kemService.getCurrentSystemProcessDate();

        setViewableAgreements(kemid);
        setViewableSourcesOfFunds(kemid);
        setViewableBenefittingOrgs(kemid);
        setViewablePayoutInstructions(kemid, currentProcessDateString);
        setViewableUseCriteria(kemid);
        setViewableReportGroups(kemid, currentProcessDateString);
        setViewableCombineDonorStatements(kemid, currentProcessDateString);

        return kemid;
    }


    /**
     * Sets the viewable agreements list - if an agreement is not active it is not viewable
     * 
     * @param kemid
     */
    private void setViewableAgreements(KEMID kemid) {
        // show only active agreements
        List<KemidAgreement> activeKemidAgreements = new ArrayList<KemidAgreement>();
        List<KemidAgreement> kemidAgreements = kemid.getKemidAgreements();

        for (KemidAgreement kemidAgreement : kemidAgreements) {
            if (kemidAgreement.isActive()) {
                activeKemidAgreements.add(kemidAgreement);
            }
        }

        kemid.setKemidAgreements(activeKemidAgreements);
    }

    /**
     * Sets the viewable sources of funds list - if a source of funds is not active it is not viewable
     * 
     * @param kemid
     */
    private void setViewableSourcesOfFunds(KEMID kemid) {
        // show only the active source of funds
        List<KemidSourceOfFunds> activeKemidSourcesOfFunds = new ArrayList<KemidSourceOfFunds>();
        List<KemidSourceOfFunds> kemidSourcesOfFunds = kemid.getKemidSourcesOfFunds();

        for (KemidSourceOfFunds kemidSourceOfFunds : kemidSourcesOfFunds) {
            if (kemidSourceOfFunds.isActive()) {
                activeKemidSourcesOfFunds.add(kemidSourceOfFunds);
            }
        }

        kemid.setKemidSourcesOfFunds(activeKemidSourcesOfFunds);
    }

    /**
     * Sets the viewable sources of funds list - if a source of funds is not active it is not viewable
     * 
     * @param kemid
     */
    private void setViewableBenefittingOrgs(KEMID kemid) {
        // show only the active benefitting organizations
        List<KemidBenefittingOrganization> activeKemidBenefittingOrgs = new ArrayList<KemidBenefittingOrganization>();
        List<KemidBenefittingOrganization> kemidBenefittingOrgs = kemid.getKemidBenefittingOrganizations();

        for (KemidBenefittingOrganization kemidBenefittingOrganization : kemidBenefittingOrgs) {
            if (kemidBenefittingOrganization.isActive()) {
                activeKemidBenefittingOrgs.add(kemidBenefittingOrganization);
            }
        }

        kemid.setKemidBenefittingOrganizations(activeKemidBenefittingOrgs);
    }

    /**
     * Ste the viewable Payout Instructions - a record is no longer viewable if the payout termination date is less than the current
     * processor system date.
     * 
     * @param kemid
     * @param currentProcessDateString
     */
    private void setViewablePayoutInstructions(KEMID kemid, String currentProcessDateString) {
        // a record is no longer viewable if the payout termination date is less than the current processor system date
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        List<KemidPayoutInstruction> activeKemidPayoutInstructions = new ArrayList<KemidPayoutInstruction>();
        List<KemidPayoutInstruction> kemidPayoutInstructions = kemid.getKemidPayoutInstructions();

        try {
            Date currentProcessDate = dateTimeService.convertToSqlDate(currentProcessDateString);
            for (KemidPayoutInstruction kemidPayoutInstruction : kemidPayoutInstructions) {
                if (kemidPayoutInstruction.getEndDate() == null || kemidPayoutInstruction.getEndDate().compareTo(currentProcessDate) < 0) {
                    activeKemidPayoutInstructions.add(kemidPayoutInstruction);
                }
            }

            kemid.setKemidPayoutInstructions(activeKemidPayoutInstructions);
        }
        catch (ParseException ex) {

        }
    }

    /**
     * Sets the viewable use criteria list - if a use criteria is not active it is not viewable
     * 
     * @param kemid
     */
    private void setViewableUseCriteria(KEMID kemid) {
        // show only the active use criteria
        List<KemidUseCriteria> activeKemidUseCriteria = new ArrayList<KemidUseCriteria>();
        List<KemidUseCriteria> kemidUseCriteria = kemid.getKemidUseCriteria();

        for (KemidUseCriteria useCriteria : kemidUseCriteria) {
            if (useCriteria.isActive()) {
                activeKemidUseCriteria.add(useCriteria);
            }
        }

        kemid.setKemidUseCriteria(activeKemidUseCriteria);
    }

    /**
     * Sets the viewable report groups - a record is no longer viewable if the report Group terminated date is less than the current
     * processor system date.
     * 
     * @param kemid
     * @param currentProcessDateString
     */
    private void setViewableReportGroups(KEMID kemid, String currentProcessDateString) {

        // a record is no longer viewable if the report Group terminated date is less than the current processor system date
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        List<KemidReportGroup> activeKemidReportGroups = new ArrayList<KemidReportGroup>();
        List<KemidReportGroup> kemidReportGroups = kemid.getKemidReportGroups();

        try {
            Date currentProcessDate = dateTimeService.convertToSqlDate(currentProcessDateString);
            for (KemidReportGroup kemidReportGroup : kemidReportGroups) {
                if (kemidReportGroup.getDateTerminated() == null || kemidReportGroup.getDateTerminated().compareTo(currentProcessDate) < 0) {
                    activeKemidReportGroups.add(kemidReportGroup);
                }
            }

            kemid.setKemidReportGroups(activeKemidReportGroups);
        }
        catch (ParseException ex) {

        }
    }


    /**
     * Sets the viewable combine donor statements - a record is no longer viewable if the terminate combine date is less than the
     * current processor system date.
     * 
     * @param kemid
     * @param currentProcessDateString
     */
    private void setViewableCombineDonorStatements(KEMID kemid, String currentProcessDateString) {
        // a record is no longer viewable if the terminate combine date is less than the current processor system date
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        List<KemidCombineDonorStatement> activeKemidCombineDonorStatement = new ArrayList<KemidCombineDonorStatement>();
        List<KemidCombineDonorStatement> kemidCombineDonorStatements = kemid.getKemidCombineDonorStatements();

        try {
            Date currentProcessDate = dateTimeService.convertToSqlDate(currentProcessDateString);
            for (KemidCombineDonorStatement combineDonorStatement : kemidCombineDonorStatements) {
                if (combineDonorStatement.getTerminateCombineDate().compareTo(currentProcessDate) < 0) {
                    activeKemidCombineDonorStatement.add(combineDonorStatement);
                }
            }

            kemid.setKemidCombineDonorStatements(activeKemidCombineDonorStatement);
        }
        catch (ParseException ex) {

        }
    }


}
