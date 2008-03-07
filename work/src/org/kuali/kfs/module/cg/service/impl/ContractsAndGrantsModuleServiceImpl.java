/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cg.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cg.bo.Award;
import org.kuali.module.cg.bo.AwardAccount;
import org.kuali.module.cg.bo.ProposalProjectDirector;
import org.kuali.module.cg.dao.AwardDao;
import org.kuali.module.integration.service.ContractsAndGrantsModuleService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.user.UuId;

/**
 * This class exposes operations on Awards.
 */
@Transactional
public class ContractsAndGrantsModuleServiceImpl implements ContractsAndGrantsModuleService {

    private AwardDao awardDao;
    private KualiGroupService kualiGroupService;

    public void deleteAll() {
        awardDao.deleteAll();
    }

    public void setAwardDao(AwardDao awardDao) {
        this.awardDao = awardDao;
    }

    /**
     * @see org.kuali.module.cg.service.AwardService#getKualiGroup(java.lang.String)
     */
    public KualiGroup getKualiGroup(String groupId) {
        KualiGroup group = null;

        if (kualiGroupService.groupExists(groupId)) {
            try {
                group = kualiGroupService.getByGroupName(groupId);
            }
            catch (GroupNotFoundException gnfe) {
                group = null;
            }
        }

        return group;
    }

    /**
     * @return
     */
    public KualiGroupService getKualiGroupService() {
        return kualiGroupService;
    }

    /**
     * @param kualiGroupService
     */
    public void setKualiGroupService(KualiGroupService kualiGroupService) {
        this.kualiGroupService = kualiGroupService;
    }

    /**
     * @see org.kuali.module.cg.service.AwardService#save(org.kuali.module.cg.bo.Award)
     */
    public void save(Award award) {
        awardDao.save(award);
    }

    /**
     * @see org.kuali.module.cg.service.AwardService#getAwardWorkgroupForAccount(java.lang.String, java.lang.String)
     */
    public String getAwardWorkgroupForAccount(String chart, String account) {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        Long maxProposalNumber;
        Map awardAccountMap = new HashMap();
        Map awardMap = new HashMap();
        awardAccountMap.put("chartOfAccountsCode", chart);
        awardAccountMap.put("accountNumber", account);
        Collection proposals = boService.findMatchingOrderBy(AwardAccount.class, awardAccountMap, "proposalNumber", false);
        if (proposals != null && !proposals.isEmpty()) {
            maxProposalNumber = ((AwardAccount) proposals.iterator().next()).getProposalNumber();
            awardMap.put("proposalNumber", maxProposalNumber);
            return ((Award) boService.findByPrimaryKey(Award.class, awardMap)).getWorkgroupName();
        }
        else {
            return null;
        }

    }
    
    public UuId getProjectDirectorForAccount(String chart, String account){
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        Long maxProposalNumber;
        Map awardAccountMap = new HashMap();
        Map awardMap = new HashMap();
        awardAccountMap.put("chartOfAccountsCode", chart);
        awardAccountMap.put("accountNumber", account);
        Collection proposals = boService.findMatchingOrderBy(AwardAccount.class, awardAccountMap, "proposalNumber", false);
        if (proposals != null && !proposals.isEmpty()) {
            maxProposalNumber = ((AwardAccount) proposals.iterator().next()).getProposalNumber();
            awardMap.put("proposalNumber", maxProposalNumber);
            return new UuId(((AwardAccount) boService.findByPrimaryKey(AwardAccount.class, awardMap)).getPersonUniversalIdentifier());
        }
        else {
            return null;
        }

    }
}
