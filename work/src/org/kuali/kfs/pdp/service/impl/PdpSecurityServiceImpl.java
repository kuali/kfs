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
/*
 * Created on Sep 22, 2004
 *
 */
package org.kuali.kfs.pdp.service.impl;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.SecurityRecord;
import org.kuali.kfs.pdp.service.PdpSecurityService;
import org.kuali.rice.kns.bo.user.KualiGroup;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiGroupService;


/**
 * 
 * This class...
 * 
 * @author jsissom
 */
public class PdpSecurityServiceImpl implements PdpSecurityService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpSecurityServiceImpl.class);

    private KualiGroupService kualiGroupService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * 
     * Constructs a PdpSecurityServiceImpl.java.
     */
    public PdpSecurityServiceImpl() {
        super();
    }

    /**
     * Sets the groupService attribute value.
     * @param groupService The groupService to set.
     */
    public void setKualiGroupService(KualiGroupService groupService) {
        this.kualiGroupService = groupService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
    /**
     * 
     * @see org.kuali.kfs.pdp.service.PdpSecurityService#getSecurityRecord(org.kuali.rice.kns.bo.user.UniversalUser)
     */
    public SecurityRecord getSecurityRecord(UniversalUser user) {
        LOG.debug("getSecurityRecord() started");

        List<KualiGroup> groups = user.getGroups();

        // All of these group names are names in the application settings table.
        SecurityRecord sr = new SecurityRecord();
        sr.setCancelRole(isGroupMember(groups, PdpConstants.Groups.CANCEL_GROUP));
        sr.setHoldRole(isGroupMember(groups, PdpConstants.Groups.HOLD_GROUP));
        sr.setLimitedViewRole(isGroupMember(groups, PdpConstants.Groups.LIMITEDVIEW_GROUP));
        sr.setProcessRole(isGroupMember(groups, PdpConstants.Groups.PROCESS_GROUP));
        sr.setRangesRole(isGroupMember(groups, PdpConstants.Groups.RANGES_GROUP));
        sr.setSubmitRole(isGroupMember(groups, PdpConstants.Groups.SUBMIT_GROUP));
        sr.setSysAdminRole(isGroupMember(groups, PdpConstants.Groups.SYSADMIN_GROUP));
        sr.setTaxHoldersRole(isGroupMember(groups, PdpConstants.Groups.TAXHOLDERS_GROUP));
        sr.setViewAllRole(isGroupMember(groups, PdpConstants.Groups.VIEWALL_GROUP));
        sr.setViewIdRole(isGroupMember(groups, PdpConstants.Groups.VIEWID_GROUP));
        sr.setViewBankRole(isGroupMember(groups, PdpConstants.Groups.VIEWBANK_GROUP));
        sr.setViewIdPartialBank(isGroupMember(groups, PdpConstants.Groups.VIEWIDPARTIALBANK_GROUP));

        return sr;
    }

    /**
     * 
     * This method...
     * @param groups
     * @param groupName
     * @return
     */
    private boolean isGroupMember(List<KualiGroup> groups, String groupName) {
        for (KualiGroup element : groups) {
            if (element.getGroupName().equals(groupName)) {
                return true;
            }
        }

        return false;
    }

}
