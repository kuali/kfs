/*
 * Created on Sep 22, 2004
 *
 */
package org.kuali.module.pdp.service.impl;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.service.PdpSecurityService;
import org.kuali.module.pdp.service.SecurityRecord;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author jsissom
 *
 */
@Transactional
public class PdpSecurityServiceImpl implements PdpSecurityService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpSecurityServiceImpl.class);

    private KualiGroupService groupService;
    private KualiConfigurationService kualiConfigurationService;

    public PdpSecurityServiceImpl() {
        super();
    }

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        this.kualiConfigurationService = kcs;
    }

    public void setKualiGroupService(KualiGroupService gs) {
        groupService = gs;
    }

    public SecurityRecord getSecurityRecord(PdpUser user) {
        LOG.debug("getSecurityRecord() started");

        List groups = groupService.getUsersGroups(user.getUniversalUser());

        // All of these group names are names in the application settings table.
        SecurityRecord sr = new SecurityRecord();
        sr.setCancelRole(groupMember(groups,PdpConstants.Groups.CANCEL_GROUP));
        sr.setHoldRole(groupMember(groups,PdpConstants.Groups.HOLD_GROUP));
        sr.setLimitedViewRole(groupMember(groups,PdpConstants.Groups.LIMITEDVIEW_GROUP));
        sr.setProcessRole(groupMember(groups,PdpConstants.Groups.PROCESS_GROUP));
        sr.setRangesRole(groupMember(groups,PdpConstants.Groups.RANGES_GROUP));
        sr.setSubmitRole(groupMember(groups,PdpConstants.Groups.SUBMIT_GROUP));
        sr.setSysAdminRole(groupMember(groups,PdpConstants.Groups.SYSADMIN_GROUP));
        sr.setTaxHoldersRole(groupMember(groups,PdpConstants.Groups.TAXHOLDERS_GROUP));
        sr.setViewAllRole(groupMember(groups,PdpConstants.Groups.VIEWALL_GROUP));
        sr.setViewIdRole(groupMember(groups,PdpConstants.Groups.VIEWID_GROUP));
        sr.setViewBankRole(groupMember(groups,PdpConstants.Groups.VIEWBANK_GROUP));

        return sr;
    }

    private boolean groupMember(List groups,String groupName) {
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            KualiGroup element = (KualiGroup)iter.next();
            if ( element.getGroupName().equals(groupName) ) {
                return true;
            }
        }

        return false;
    }
}
