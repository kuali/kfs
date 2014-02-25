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
 * limitations under the License. awardLookupable
 */
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.KFSPropertyConstants.DOCUMENT;
import static org.kuali.kfs.sys.KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.validation.impl.MilestoneScheduleRuleUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Methods for the Milestone Schedule maintenance document UI.
 */
public class MilestoneScheduleMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * Constructs an MilestoneScheduleMaintainableImpl.
     */
    public MilestoneScheduleMaintainableImpl() {
        super();
    }

    /**
     * Constructs a MilestoneScheduleMaintainableImpl.
     *
     * @param award
     */
    public MilestoneScheduleMaintainableImpl(MilestoneSchedule milestoneSchedule) {
        super(milestoneSchedule);
        this.setBoClass(milestoneSchedule.getClass());
    }

    /**
     * This method is called to check if the award already has milestones set, and to validate on refresh
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */

    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        if (StringUtils.equals("awardLookupable", (String) fieldValues.get(KFSConstants.REFRESH_CALLER))) {

            boolean isMilestonesExist = MilestoneScheduleRuleUtil.checkIfMilestonesExists(getMilestoneSchedule());
            if (isMilestonesExist) {
                String pathToMaintainable = DOCUMENT + "." + NEW_MAINTAINABLE_OBJECT;
                GlobalVariables.getMessageMap().addToErrorPath(pathToMaintainable);
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_AWARD_MILESTONE_SCHEDULE_EXISTS, new String[] { getMilestoneSchedule().getProposalNumber().toString() });
                GlobalVariables.getMessageMap().removeFromErrorPath(pathToMaintainable);

            }
        }
        else {
            super.refresh(refreshCaller, fieldValues, document);
        }
    }

   /**
    * Not to copy over the Milestones billedIndicators and billIdentifiers to prevent
    * bad data and PK issues when saving new Milestones.
    */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);

        // clear out Bill IDs so new ones will get generated for these bills
        // reset billed indicator in case bill we're copying from was already billed
        List<Milestone> milestonss = getMilestoneSchedule().getMilestones();
        if (ObjectUtils.isNotNull(milestonss)) {
            for (Milestone milestone:milestonss) {
                milestone.setBilledIndicator(false);
                milestone.setMilestoneIdentifier(null);
            }
        }
    }

    /**
     * This method is called for refreshing the Agency before display to show the full name in case the agency number was changed by
     * hand before any submit that causes a redisplay.
     */
    @Override
    public void processAfterRetrieve() {
        MilestoneSchedule milestoneSchedule = getMilestoneSchedule();
        milestoneSchedule.refreshNonUpdateableReferences();
        super.processAfterRetrieve();
    }

    /**
     * Override the getSections method on this maintainable so that the active field can be set to read-only when
     * a CINV doc has been created with this Milestone Schedule and Milestones
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        MilestoneSchedule milestoneSchedule = (MilestoneSchedule) document.getNewMaintainableObject().getBusinessObject();
        Long proposalNumber = milestoneSchedule.getProposalNumber();

        for (Section section : sections) {
            String sectionId = section.getSectionId();
            if (sectionId.equalsIgnoreCase(ArPropertyConstants.MILESTONES_SECTION)) {
                prepareMilestonesTab(section, proposalNumber);
            }
        }

        return sections;
    }

    /**
     * Sets the Milestone in the passed in section to be readonly if it has been copied to a CG Invoice doc.
     *
     * @param section Milestone section to review and possibly set readonly
     * @param proposalNumber used to look for CG Invoice docs
     */
    private void prepareMilestonesTab(Section section, Long proposalNumber) {
        ContractsGrantsInvoiceDocumentService cgInvDocService  = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

        for (Row row : section.getRows()) {
            for (Field field : row.getFields()) {
                if (field.getCONTAINER().equalsIgnoreCase(field.getFieldType())) {
                    for (Row containerRow : field.getContainerRows()) {
                        for (Field containerRowfield : containerRow.getFields()) {
                            // a record is no longer editable if the bill has been copied to a CINV doc
                            if (ObjectUtils.getNestedAttributePrimitive(containerRowfield.getPropertyName()).matches(ArPropertyConstants.MilestoneFields.MILESTONE_IDENTIFIER)) {
                                String milestoneId = containerRowfield.getPropertyValue();
                                if (StringUtils.isNotEmpty(milestoneId)) {
                                    if (cgInvDocService.hasMilestoneBeenCopiedToInvoice(proposalNumber, milestoneId)) {
                                        for (Field rowfield : row.getFields()) {
                                            if (rowfield.getCONTAINER().equalsIgnoreCase(rowfield.getFieldType())) {
                                                for (Row fieldContainerRow : rowfield.getContainerRows()) {
                                                    for (Field fieldContainerRowField : fieldContainerRow.getFields()) {
                                                        fieldContainerRowField.setReadOnly(true);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Gets the underlying Milestone Schedule.
     *
     * @return
     */
    public MilestoneSchedule getMilestoneSchedule() {

        return (MilestoneSchedule) getBusinessObject();
    }

}
