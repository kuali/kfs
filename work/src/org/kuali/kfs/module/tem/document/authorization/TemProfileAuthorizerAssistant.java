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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAccount;
import org.kuali.kfs.module.tem.businessobject.TemProfileArranger;
import org.kuali.kfs.module.tem.businessobject.TemProfileEmergencyContact;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Why does this class exist?  That's...kind of a long story.
 * TemProfileAuthorizer needs to override canCreate and canMaintain.  Sadly, those methods are final on the base class, so it has to implement
 * the interface for MaintenanceDocuemntAuthorizers.
 * But when it does that, it looses lots of cool extended functionality like the addRoleQualifiers getting passed everywhere, etc etc
 * when really, we're only overriding a couple methods.
 * Given that, this extends the normal maint doc authorizer for maint docs in KFS.  And when TemProfileAuthorizer is just deferring calls instead
 * of overriding the method completely, it will call to this
 */
public class TemProfileAuthorizerAssistant extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase#addRoleQualification(java.lang.Object, java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> attributes) {
        super.addRoleQualification(dataObject, attributes);
        if (dataObject instanceof FinancialSystemMaintenanceDocument) {
            FinancialSystemMaintenanceDocument maintDoc = (FinancialSystemMaintenanceDocument) dataObject;
            if (maintDoc.getNewMaintainableObject().getBusinessObject() instanceof TemProfile) {
                final TemProfile profile = (TemProfile) maintDoc.getNewMaintainableObject().getBusinessObject();
                addRoleQualificationsFromProfile(profile, attributes);
                if (!StringUtils.isBlank(maintDoc.getNewMaintainableObject().getMaintenanceAction())) {
                    attributes.put(KRADConstants.MAINTENANCE_ACTN, maintDoc.getNewMaintainableObject().getMaintenanceAction());
                }
            }
        } else if (dataObject instanceof TemProfile) {
            final TemProfile profile = (TemProfile)dataObject;
            addRoleQualificationsFromProfile(profile, attributes);
        } else if (dataObject instanceof TemProfileEmergencyContact) {
            final TemProfileEmergencyContact emergencyContact = (TemProfileEmergencyContact)dataObject;
            if (ObjectUtils.isNull(emergencyContact.getProfile())) {
                emergencyContact.refreshReferenceObject(TemPropertyConstants.PROFILE);
            }
            if (!ObjectUtils.isNull(emergencyContact.getProfile())) {
                addRoleQualificationsFromProfile(emergencyContact.getProfile(), attributes);
            }
        } else if (dataObject instanceof TemProfileArranger) {
            final TemProfileArranger arranger = (TemProfileArranger)dataObject;
            if (ObjectUtils.isNull(arranger.getProfile())) {
                arranger.refreshReferenceObject(TemPropertyConstants.PROFILE);
            }
            if (!ObjectUtils.isNull(arranger.getProfile())) {
                addRoleQualificationsFromProfile(arranger.getProfile(), attributes);
            }
        } else if (dataObject instanceof TemProfileAccount) {
            final TemProfileAccount profileAccount = (TemProfileAccount)dataObject;
            if (ObjectUtils.isNull(profileAccount.getProfile())) {
                profileAccount.refreshReferenceObject(TemPropertyConstants.PROFILE);
            }
            if (!ObjectUtils.isNull(profileAccount.getProfile())) {
                addRoleQualificationsFromProfile(profileAccount.getProfile(), attributes);
            }
        }
    }

    /**
     * Adds role qualifiers harvested from the TemProfile to the attributes Map
     * @param profile the TemProfile to harvest qualifiers from
     * @param attributes the Map of qualifiers to add into
     */
    protected void addRoleQualificationsFromProfile(TemProfile profile, Map<String, String> attributes) {
        // Add the principalId from the profile to grant permission to users modifying their own profile.
        if (!StringUtils.isBlank(profile.getPrincipalId())) {
            attributes.put(KfsKimAttributes.PROFILE_PRINCIPAL_ID, profile.getPrincipalId());
        }

        // OrgCode and COACode are needed for the org descending hierarchy qualification
        if (!StringUtils.isBlank(profile.getHomeDeptOrgCode())) {
            attributes.put(KfsKimAttributes.ORGANIZATION_CODE, profile.getHomeDeptOrgCode());
        }
        if (!StringUtils.isBlank(profile.getHomeDeptChartOfAccountsCode())) {
            attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, profile.getHomeDeptChartOfAccountsCode());
        }

        // Add the profileId from the profile to grant permission to the assigned arrangers modifying the profile.
        if (ObjectUtils.isNotNull(profile.getProfileId())) {
            attributes.put(TemPropertyConstants.TemProfileProperties.PROFILE_ID, profile.getProfileId().toString());
        }
    }

    @Override
    public boolean canCopy(Document document, Person user) {
        return false;
    }

    /**
     * There's a permission for out-of-the-box KFS to make the TemProfileAdministrator section read only
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase#getSecurePotentiallyReadOnlySectionIds()
     */
    @Override
    public Set<String> getSecurePotentiallyReadOnlySectionIds() {
        Set<String> readOnlySections = super.getSecurePotentiallyReadOnlySectionIds();
        readOnlySections.add(TemPropertyConstants.TemProfileProperties.TEM_PROFILE_ADMINISTRATOR);
        return readOnlySections;
    }

}
