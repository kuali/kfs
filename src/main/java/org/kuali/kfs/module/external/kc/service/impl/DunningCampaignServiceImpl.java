/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;

import org.kuali.kfs.integration.ar.AccountsReceivableDunningCampaign;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
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
            values.put(KcConstants.DunningCampaignService.DUNNING_CAMPAIGN_ID, campaignID);
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
