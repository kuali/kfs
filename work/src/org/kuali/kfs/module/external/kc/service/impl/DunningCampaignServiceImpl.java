/*
 * Copyright 2010 The Kuali Foundation.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;

import org.kuali.kfs.integration.ar.AccountsReceivableDunningCampaign;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.dto.DunningCampaignDTO;
import org.kuali.kfs.module.external.kc.service.DunningCampaignService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;

public class DunningCampaignServiceImpl implements DunningCampaignService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DunningCampaignServiceImpl.class);

    @Override
    public DunningCampaignDTO getDunningCampaign(String campaignID) {

        ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(AccountsReceivableDunningCampaign.class);
        if (responsibleModuleService != null) {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("campaignID", campaignID);
            return getDunningCampaignDTO(responsibleModuleService.getExternalizableBusinessObject(AccountsReceivableDunningCampaign.class, values));
        } else {
            return null;
        }
    }

    @Override
    public List<DunningCampaignDTO> getMatching(@WebParam(name = "searchCriteria") List<HashMapElement> searchCriteria) {
        ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(AccountsReceivableDunningCampaign.class);
        if (responsibleModuleService != null) {
            Map<String, Object> values = new HashMap<String, Object>();
            for (HashMapElement element : searchCriteria) {
                values.put(element.getKey(), element.getValue());
            }
            return getDunningCampaignDTO(responsibleModuleService.getExternalizableBusinessObjectsListForLookup(AccountsReceivableDunningCampaign.class, values, false));
        } else {
            return new ArrayList<DunningCampaignDTO>();
        }
    }

    protected DunningCampaignDTO getDunningCampaignDTO(AccountsReceivableDunningCampaign dunningCampaign) {
        if (dunningCampaign != null) {
            DunningCampaignDTO dto = new DunningCampaignDTO();
            dto.setCampaignID(dunningCampaign.getCampaignID());
            dto.setCampaignDescription(dunningCampaign.getCampaignDescription());
            dto.setActive(dunningCampaign.isActive());
            return dto;
        } else {
            return null;
        }
    }

    protected List<DunningCampaignDTO> getDunningCampaignDTO(List<AccountsReceivableDunningCampaign> campaigns) {
        if (campaigns != null) {
            List<DunningCampaignDTO> results = new ArrayList<DunningCampaignDTO>();
            for (AccountsReceivableDunningCampaign campaign : campaigns) {
                results.add(getDunningCampaignDTO(campaign));
            }
            return results;
        } else {
            return new ArrayList<DunningCampaignDTO>();
        }
    }
}
