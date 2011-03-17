/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAccountAwardInformation;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.Agency;
import org.kuali.kfs.module.external.kc.businessobject.Award;
import org.kuali.kfs.module.external.kc.businessobject.AwardAccount;
import org.kuali.kfs.module.external.kc.businessobject.AwardAccountDTO;
import org.kuali.kfs.module.external.kc.businessobject.Proposal;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.webService.AwardAccountService;
import org.kuali.kfs.module.external.kc.webService.AwardAccountSoapService;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;

public class AwardAccountServiceImpl implements ExternalizableBusinessObjectService {

    protected  AwardAccountService getWebService() {
        AwardAccountSoapService soapService = new AwardAccountSoapService();
        AwardAccountService port = soapService.getAwardAccountServicePort(); 
        return port;
    }

    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        return getTestAwardAccount();
    }

    public Collection findMatching(Map fieldValues) {
        Map hashMapList = new HashMap();

        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            String val = (String) e.getValue();

            if ( KcConstants.Unit.KC_ALLOWABLE_CRITERIA_PARAMETERS.contains(key)  && (val.length() > 0)) {
                hashMapList.put(key, val);
            }
        }
        
        List awardAccounts = new ArrayList();             
        AwardAccountDTO awardAccountDTO = getWebService().getAwardAccount((String)hashMapList.get("accountNumber"));
        
        if (awardAccountDTO != null) {
            ContractsAndGrantsAccountAwardInformation awardAccount = new AwardAccount(awardAccountDTO, (String)hashMapList.get("accountNumber"), (String)hashMapList.get("chartOfAccountsCode"));
            awardAccounts.add(awardAccount);
        }
        
        return awardAccounts;
    }

    private ContractsAndGrantsAccountAwardInformation getTestAwardAccount(){
        AwardAccount awardAccount = new AwardAccount();
        Proposal proposal = new Proposal();
        Award award = new Award();
        Agency agency = new Agency();
        
        awardAccount.setAccountNumber("0142900");
        awardAccount.setActive(true);
        awardAccount.setChartOfAccountsCode("BL");
        awardAccount.setPrincipalId("0000151844");
        awardAccount.setProposalNumber(new Long(10000));
        
        proposal.setProposalFederalPassThroughIndicator(false);
        proposal.setFederalPassThroughAgencyNumber("12345");
        proposal.setGrantNumber("67890");
        proposal.setProposalNumber(new Long(10000));
        award.setProposalNumber(new Long(10000));
        award.setAgencyNumber("000102");
        proposal.setAward(award);
        awardAccount.setAward(award);
        awardAccount.getAward().setProposal(proposal);
                
        agency.setAgencyNumber("000102");
        agency.setReportingName("Sponsor Name");
        awardAccount.getAward().setAgency(agency);
        
        return (ContractsAndGrantsAccountAwardInformation)awardAccount;
    }
}
