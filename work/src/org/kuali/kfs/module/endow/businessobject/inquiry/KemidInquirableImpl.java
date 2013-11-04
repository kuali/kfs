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
package org.kuali.kfs.module.endow.businessobject.inquiry;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentAvailableBalance;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentBalance;
import org.kuali.kfs.module.endow.businessobject.KEMIDHistoricalBalance;
import org.kuali.kfs.module.endow.businessobject.KemidAgreement;
import org.kuali.kfs.module.endow.businessobject.KemidAuthorizations;
import org.kuali.kfs.module.endow.businessobject.KemidBenefittingOrganization;
import org.kuali.kfs.module.endow.businessobject.KemidCombineDonorStatement;
import org.kuali.kfs.module.endow.businessobject.KemidPayoutInstruction;
import org.kuali.kfs.module.endow.businessobject.KemidReportGroup;
import org.kuali.kfs.module.endow.businessobject.KemidSourceOfFunds;
import org.kuali.kfs.module.endow.businessobject.KemidUseCriteria;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

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
        setViewableAuthorizations(kemid);
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
     * Sets the viewable Authorizations list - if an Authorizations is not active it is not viewable
     * 
     * @param kemid
     */
    private void setViewableAuthorizations(KEMID kemid) {
        // show only active Authorizations
        List<KemidAuthorizations> activeKemidAuthorizations = new ArrayList<KemidAuthorizations>();
        List<KemidAuthorizations> kemidAuthorizations = kemid.getKemidAuthorizations();

        for (KemidAuthorizations kemidAuthorization : kemidAuthorizations) {
            if (kemidAuthorization.isActive()) {
                activeKemidAuthorizations.add(kemidAuthorization);
            }
        }

        kemid.setKemidAuthorizations(activeKemidAuthorizations);
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
                if (combineDonorStatement.getTerminateCombineDate() == null || combineDonorStatement.getTerminateCombineDate().compareTo(currentProcessDate) < 0) {
                    activeKemidCombineDonorStatement.add(combineDonorStatement);
                }
            }

            kemid.setKemidCombineDonorStatements(activeKemidCombineDonorStatement);
        }
        catch (ParseException ex) {

        }
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        KEMID kemid = (KEMID) businessObject;

        // if the attribute is currentAvailableFunds, currentBalances, historicalBalances, ticklers then we build the lookup links
        // for
        // Current Available Funds, Current Balances, Historical Balances and Ticklers
        if (EndowPropertyConstants.KEMID_CURRENT_AVAILABLE_FUNDS.equalsIgnoreCase(attributeName) || EndowPropertyConstants.KEMID_CURRENT_BALANCES.equalsIgnoreCase(attributeName) || EndowPropertyConstants.KEMID_HISTORICAL_BALANCES.equalsIgnoreCase(attributeName) || EndowPropertyConstants.KEMID_TICKLERS.equals(attributeName)) {
            // || EndowPropertyConstants.KEMID_RECURRING_TRANSFERS.equalsIgnoreCase(attributeName)) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);

            // the only difference between the two links is the BO class
            // if currentAvailableFunds set the BO class to be KEMIDCurrentAvailableBalance
            if (EndowPropertyConstants.KEMID_CURRENT_AVAILABLE_FUNDS.equals(attributeName)) {
                params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, KEMIDCurrentAvailableBalance.class.getName());
            }
            // if currentBalances set the BO to be KEMIDCurrentBalance
            if (EndowPropertyConstants.KEMID_CURRENT_BALANCES.equals(attributeName)) {
                params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, KEMIDCurrentBalance.class.getName());
            }
            // if historicalBalances set the BO to be KEMIDHistoricalBalance
            if (EndowPropertyConstants.KEMID_HISTORICAL_BALANCES.equals(attributeName)) {
                params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, KEMIDHistoricalBalance.class.getName());
            }

            // if ticklers set the BO to be Tickler
            if (EndowPropertyConstants.KEMID_TICKLERS.equals(attributeName)) {
                params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Tickler.class.getName());
            }

            // if ticklers set the BO to be EndowmentRecurringCashTransfer
            // if (EndowPropertyConstants.KEMID_RECURRING_TRANSFERS.equals(attributeName)) {
            // params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, EndowmentRecurringCashTransfer.class.getName());
            // }

            params.put(KRADConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");

            if (EndowPropertyConstants.KEMID_TICKLERS.equals(attributeName)) {
                params.put(EndowPropertyConstants.TICKLER_LOOKUP_KEMID, UrlFactory.encode(kemid.getKemid()));
            }
            // else if (EndowPropertyConstants.KEMID_RECURRING_TRANSFERS.equalsIgnoreCase(attributeName)) {
            // params.put(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_SOURCE_KEMID, UrlFactory.encode(kemid.getKemid()));
            // }
            else {
                params.put(EndowPropertyConstants.KEMID, UrlFactory.encode(kemid.getKemid()));
            }
            params.put(KFSConstants.SUPPRESS_ACTIONS, "true");
            String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();

            if (EndowPropertyConstants.KEMID_TICKLERS.equals(attributeName)) {
                fieldList.put(EndowPropertyConstants.TICKLER_LOOKUP_KEMID, kemid.getKemid());
            }
            else {
                fieldList.put(EndowPropertyConstants.KEMID, kemid.getKemid());
            }

            return getHyperLink(Security.class, fieldList, url);
        }


        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }


}
