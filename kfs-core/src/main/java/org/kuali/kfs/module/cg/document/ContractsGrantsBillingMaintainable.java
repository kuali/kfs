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
package org.kuali.kfs.module.cg.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationController;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.kns.web.ui.SectionBridge;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Abstract class that overrides getCoreSections to ignore CGB-specific sections if CGB is disabled.
 */
public abstract class ContractsGrantsBillingMaintainable extends FinancialSystemMaintainable {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsBillingMaintainable.class);

    /**
     * Essentially a copy of the overridden method with the addition of a call to getSectionIdsToIgnore, which is currently
     * used to avoid processing / returning sections that are specific to the Contracts & Grants Billing (CGB) enhancement
     * if CGB is not enabled.
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getCoreSections(org.kuali.rice.kns.document.MaintenanceDocument, org.kuali.rice.kns.maintenance.Maintainable)
     */
    @Override
    public List<Section> getCoreSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = new ArrayList<Section>();
        MaintenanceDocumentRestrictions maintenanceRestrictions = KNSServiceLocator
                .getBusinessObjectAuthorizationService().getMaintenanceDocumentRestrictions(document,
                        GlobalVariables.getUserSession().getPerson());

        MaintenanceDocumentPresentationController maintenanceDocumentPresentationController = (MaintenanceDocumentPresentationController) getDocumentHelperService()
                .getDocumentPresentationController(document);
        Set<String> conditionallyRequiredFields = maintenanceDocumentPresentationController
                .getConditionallyRequiredPropertyNames(document);

        List<MaintainableSectionDefinition> sectionDefinitions = getMaintenanceDocumentDictionaryService()
                .getMaintainableSections(getDocumentTypeName());
        try {
            Collection<?> sectionIdsToIgnore = getSectionIdsToIgnore();

            // iterate through section definitions and create Section UI object
            for (Iterator iter = sectionDefinitions.iterator(); iter.hasNext();) {
                MaintainableSectionDefinition maintSectionDef = (MaintainableSectionDefinition) iter.next();

                List<String> displayedFieldNames = new ArrayList<String>();
                if (!maintenanceRestrictions.isHiddenSectionId(maintSectionDef.getId()) && !sectionIdsToIgnore.contains(maintSectionDef.getId())) {

                    for (Iterator iter2 = maintSectionDef.getMaintainableItems().iterator(); iter2.hasNext();) {
                        MaintainableItemDefinition item = (MaintainableItemDefinition) iter2.next();
                        if (item instanceof MaintainableFieldDefinition) {
                            displayedFieldNames.add(((MaintainableFieldDefinition) item).getName());
                        }
                    }

                    Section section = SectionBridge
                            .toSection(maintSectionDef, getBusinessObject(), this, oldMaintainable,
                                    getMaintenanceAction(), displayedFieldNames, conditionallyRequiredFields);
                    if (maintenanceRestrictions.isReadOnlySectionId(maintSectionDef.getId())) {
                        section.setReadOnly(true);
                    }

                    // add to section list
                    sections.add(section);
                }

            }

        }
        catch (InstantiationException | IllegalAccessException e) {
            LOG.error("Unable to create instance of object class" + e.getMessage());
            throw new RuntimeException("Unable to create instance of object class" + e.getMessage());
        }

        return sections;
    }

    /**
     * If the Contracts & Grants Billing (CGB) enhancement is disabled, we don't want to
     * process sections only related to CGB.
     *
     * @return Collection of section ids to ignore
     */
    protected abstract Collection<?> getSectionIdsToIgnore();

}
