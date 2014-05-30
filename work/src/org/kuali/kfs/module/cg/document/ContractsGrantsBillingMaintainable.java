/*
 * Copyright 2011 The Kuali Foundation.
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
        catch (InstantiationException e) {
            LOG.error("Unable to create instance of object class" + e.getMessage());
            throw new RuntimeException("Unable to create instance of object class" + e.getMessage());
        }
        catch (IllegalAccessException e) {
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
