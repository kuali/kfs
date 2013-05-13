/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidAgreement;
import org.kuali.kfs.module.endow.businessobject.KemidCombineDonorStatement;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.KemidFee;
import org.kuali.kfs.module.endow.businessobject.TypeCode;
import org.kuali.kfs.module.endow.businessobject.TypeFeeMethod;
import org.kuali.kfs.module.endow.document.service.FeeMethodService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class...
 */
public class KEMIDMaintainableImpl extends KualiMaintainableImpl {

    private KEMID oldKemid;
    private KEMID newKemid;

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {

        super.doRouteStatusChange(documentHeader);

        WorkflowDocument workflowDoc = documentHeader.getWorkflowDocument();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        FinancialSystemMaintenanceDocument maintDoc = null;

        try {
            maintDoc = (FinancialSystemMaintenanceDocument) documentService.getByDocumentHeaderId(getDocumentNumber());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        initializeAttributes(maintDoc);

        // This code is only executed if the maintenance action was NEW or COPY and when the final approval occurs
        if ((KRADConstants.MAINTENANCE_NEW_ACTION.equals(maintDoc.getNewMaintainableObject().getMaintenanceAction()) || KRADConstants.MAINTENANCE_COPY_ACTION.equals(maintDoc.getNewMaintainableObject().getMaintenanceAction())) && workflowDoc.isFinal()) {

            KemidCurrentCash newCurrentCashEntry = new KemidCurrentCash();
            newCurrentCashEntry.setKemid(newKemid.getKemid());
            newCurrentCashEntry.setCurrentIncomeCash(KualiDecimal.ZERO);
            newCurrentCashEntry.setCurrentPrincipalCash(KualiDecimal.ZERO);

            // save new current cash entry
            SpringContext.getBean(BusinessObjectService.class).save(newCurrentCashEntry);
        }
    }


    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {

        super.refresh(refreshCaller, fieldValues, document);

        // update fees and ACI, cash sweep models when type code is changed
        if (refreshCaller != null && refreshCaller.equalsIgnoreCase(KFSConstants.KUALI_LOOKUPABLE_IMPL) && fieldValues != null && fieldValues.containsKey(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + EndowPropertyConstants.KEMID_TYPE_CODE)) {
            initializeAttributes(document);

            if (ObjectUtils.isNotNull(oldKemid) && ObjectUtils.isNotNull(newKemid) && StringUtils.isNotBlank(newKemid.getTypeCode())) {
                BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
                Map<String, String> pkMap = new HashMap<String, String>();
                pkMap.put(EndowPropertyConstants.ENDOWCODEBASE_CODE, newKemid.getTypeCode());
                TypeCode typeCode = (TypeCode) businessObjectService.findByPrimaryKey(TypeCode.class, pkMap);

                if (ObjectUtils.isNotNull(typeCode)) {
                    newKemid.setCashSweepModelId(typeCode.getCashSweepModelId());
                    newKemid.setIncomeACIModelId(typeCode.getIncomeACIModelId());
                    newKemid.setPrincipalACIModelId(typeCode.getPrincipalACIModelId());
                }

                if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {
                    updateKemidFees(document);
                }
            }
        }

        // when the user looks up a fee method in the add kemid fee method tab the add fee method start date is updated to the
        // returned fee method next process date
        if (refreshCaller != null && refreshCaller.equalsIgnoreCase(KFSConstants.KUALI_LOOKUPABLE_IMPL) && fieldValues != null && fieldValues.containsKey(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + KFSConstants.ADD_PREFIX + "." + EndowPropertyConstants.KEMID_FEES_TAB + "." + EndowPropertyConstants.KEMID_FEE_MTHD_CD)) {

            KemidFee addKemidFee = (KemidFee) document.getNewMaintainableObject().getNewCollectionLine(EndowPropertyConstants.KEMID_FEES_TAB);
            String feeMethodCode = addKemidFee.getFeeMethodCode();

            if (StringUtils.isNotBlank(addKemidFee.getFeeMethodCode())) {
                FeeMethodService feeMethodService = SpringContext.getBean(FeeMethodService.class);
                addKemidFee.setFeeStartDate(feeMethodService.getFeeMethodNextProcessDate(feeMethodCode));
            }


        }


    }

    /**
     * Add the default kemid fees based on the kemid type.
     * 
     * @param document
     */
    private void updateKemidFees(MaintenanceDocument document) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, String> typeFeeMethodFieldValues = new HashMap<String, String>();
        List<KemidFee> kemidFees = new ArrayList<KemidFee>();
        List<TypeFeeMethod> typeFeeMethodList = new ArrayList<TypeFeeMethod>();
        KEMID kemid = (KEMID) getBusinessObject();

        typeFeeMethodFieldValues.put(EndowPropertyConstants.ENDOWCODEBASE_CODE, newKemid.getTypeCode());
        typeFeeMethodList = (List<TypeFeeMethod>) businessObjectService.findMatching(TypeFeeMethod.class, typeFeeMethodFieldValues);

        // remove the old kemid fees
        kemid.getKemidFees().clear();

        for (TypeFeeMethod feeMethod : typeFeeMethodList) {
            kemid.getKemidFees().add(createNewKemidFee(feeMethod));
        }

        // refresh non-updatable references
        kemid.refreshNonUpdateableReferences();
        refreshNonUpdateableReferences(kemid.getKemidFees());
    }

    /**
     * @param collection
     */
    private static void refreshNonUpdateableReferences(Collection<? extends PersistableBusinessObject> collection) {
        for (PersistableBusinessObject item : collection) {
            item.refreshNonUpdateableReferences();
        }
    }

    /**
     * Creates a new kemid fee based on a fee method.
     * 
     * @param feeMethod
     * @return the new kemid fee
     */
    private KemidFee createNewKemidFee(TypeFeeMethod typeFeeMethod) {

        KemidFee kemidFee = new KemidFee();
        kemidFee.setChargeFeeToKemid(newKemid.getKemid());
        kemidFee.setFeeMethodCode(typeFeeMethod.getFeeMethodCode());
        kemidFee.setPercentOfFeeChargedToIncome(new KualiDecimal(1));
        kemidFee.setPercentOfFeeChargedToPrincipal(KualiDecimal.ZERO);
        kemidFee.setTotalAccruedFees(KualiDecimal.ZERO);
        kemidFee.setWaiveFees(false);
        kemidFee.setAccrueFees(false);
        kemidFee.setTotalWaivedFeesThisFiscalYear(KualiDecimal.ZERO);
        kemidFee.setTotalWaivedFees(KualiDecimal.ZERO);

        typeFeeMethod.refreshReferenceObject(EndowPropertyConstants.FEE_METHOD);

        Date feeStartDate = ObjectUtils.isNotNull(typeFeeMethod.getFeeMethod()) ? typeFeeMethod.getFeeMethod().getFeeNextProcessDate() : null;
        kemidFee.setFeeStartDate(feeStartDate);
        kemidFee.setNewCollectionRecord(true);

        return kemidFee;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getSections(org.kuali.rice.kns.document.MaintenanceDocument,
     *      org.kuali.rice.kns.maintenance.Maintainable)
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {

        List<Section> sections = super.getSections(document, oldMaintainable);

        if (sections != null) {
            initializeAttributes(document);

            for (Section section : sections) {
                String sectionId = section.getSectionId();

                if (sectionId.equalsIgnoreCase(EndowPropertyConstants.KEMID_PAY_INSTRUCTIONS_SECTION)) {
                    preparePayoutInstructionsTab(document, section);
                }

                if (sectionId.equalsIgnoreCase(EndowPropertyConstants.KEMID_SPECIAL_INSTRUCTIONS_SECTION)) {
                    prepareSpecialInstructionsTab(document, section);
                }

                if (sectionId.equalsIgnoreCase(EndowPropertyConstants.KEMID_REPORT_GROUP_SECTION)) {
                    prepareReportGroupsTab(document, section);
                }

                if (sectionId.equalsIgnoreCase(EndowPropertyConstants.KEMID_DONOR_STATEMENTS_SECTION)) {
                    prepareDonorStatementsTab(document, section);
                }

                if (sectionId.equalsIgnoreCase(EndowPropertyConstants.KEMID_COMBINE_DONOR_STATEMENTS_SECTION)) {
                    prepareCombineDonorStatementsTab(document, section);
                }

                if (sectionId.equalsIgnoreCase(EndowPropertyConstants.KEMID_FEES_SECTION)) {
                    // section.setDefaultOpen(true);
                    prepareFeesTab(document, section);

                }

            }
        }
        return sections;
    }


    /**
     * Prepares the payout instructions tab for display.
     * 
     * @param document
     * @param section
     * @param field
     * 
     * KRAD Conversion: MaintainableImpl performs customization of the fields in the rows of the sections.
     */
    private void preparePayoutInstructionsTab(MaintenanceDocument document, Section section) {

        KEMService kemService = SpringContext.getBean(KEMService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        for (Row row : section.getRows()) {
            for (Field field : row.getFields()) {

                if (field.getCONTAINER().equalsIgnoreCase(field.getFieldType())) {

                    for (Row containerRow : field.getContainerRows()) {
                        for (Field containerRowfield : containerRow.getFields()) {

                            // the payout start date is no longer editable if the payout start date is less than the
                            // current process
                            // or
                            // system date

                            String indexedPropertyNamePrefix = EndowPropertyConstants.KEMID_PAY_INSTRUCTIONS_TAB + "\\[\\d+\\]\\" + ".";
                            String indexedStartDate = indexedPropertyNamePrefix + EndowPropertyConstants.KEMID_PAY_INC_START_DATE;
                            if (containerRowfield.getPropertyName().matches(indexedStartDate)) {
                                String payoutStartDateString = containerRowfield.getPropertyValue();
                                if (payoutStartDateString != null && !KFSConstants.EMPTY_STRING.equalsIgnoreCase(payoutStartDateString)) {
                                    String currentProcessDateString = kemService.getCurrentSystemProcessDate();

                                    try {
                                        Date currentProcessDate = dateTimeService.convertToSqlDate(currentProcessDateString);
                                        Date payoutStartDate = dateTimeService.convertToSqlDate(payoutStartDateString);

                                        if (payoutStartDate.compareTo(currentProcessDate) < 0) {
                                            containerRowfield.setReadOnly(true);
                                        }
                                    }
                                    catch (ParseException ex) {
                                        // do nothing
                                    }
                                }
                            }


                            // a record is no longer editable if the payout termination date is less than the current
                            // process or
                            // system
                            // date
                            String indexedEndDate = indexedPropertyNamePrefix + EndowPropertyConstants.KEMID_PAY_INC_END_DATE;
                            if (containerRowfield.getPropertyName().matches(indexedEndDate)) {
                                String payoutEndDateString = containerRowfield.getPropertyValue();
                                if (payoutEndDateString != null && !KFSConstants.EMPTY_STRING.equalsIgnoreCase(payoutEndDateString)) {

                                    String currentProcessDateString = kemService.getCurrentSystemProcessDate();

                                    try {
                                        Date currentProcessDate = dateTimeService.convertToSqlDate(currentProcessDateString);
                                        Date payoutEndDate = dateTimeService.convertToSqlDate(payoutEndDateString);

                                        if (payoutEndDate.compareTo(currentProcessDate) < 0) {
                                            containerRow.setHidden(true);
                                        }
                                    }
                                    catch (ParseException ex) {
                                        // do nothing
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
     * Prepares the special instructions tab.
     * 
     * @param document
     * @param section
     * @param field
     */
    private void prepareSpecialInstructionsTab(MaintenanceDocument document, Section section) {
        KEMService kemService = SpringContext.getBean(KEMService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        for (Row row : section.getRows()) {
            for (Field field : row.getFields()) {

                if (field.getCONTAINER().equalsIgnoreCase(field.getFieldType())) {

                    for (Row containerRow : field.getContainerRows()) {
                        for (Field containerRowfield : containerRow.getFields()) {


                            // a record is no longer editable if the instruction end date is less than the current
                            // process or
                            // system
                            // date
                            String specialInstructionEndDateField = KFSConstants.ADD_PREFIX + "." + EndowPropertyConstants.KEMID_SPECIAL_INSTRUCTIONS_TAB + "." + EndowPropertyConstants.KEMID_SPEC_INSTR_END_DATE;
                            if (specialInstructionEndDateField.equalsIgnoreCase(specialInstructionEndDateField)) {
                                String instructionEndDateString = containerRowfield.getPropertyValue();
                                if (instructionEndDateString != null && !KFSConstants.EMPTY_STRING.equalsIgnoreCase(instructionEndDateString)) {

                                    String currentProcessDateString = kemService.getCurrentSystemProcessDate();

                                    try {
                                        Date currentProcessDate = dateTimeService.convertToSqlDate(currentProcessDateString);
                                        Date specialInstructionEndDate = dateTimeService.convertToSqlDate(instructionEndDateString);

                                        if (specialInstructionEndDate.compareTo(currentProcessDate) < 0) {
                                            for (Field rowfield : row.getFields()) {
                                                rowfield.setReadOnly(true);

                                            }
                                        }
                                    }
                                    catch (ParseException ex) {
                                        // do nothing
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
     * Prepares the fees tab.
     * 
     * @param document
     * @param section
     * @param field
     */
    private void prepareFeesTab(MaintenanceDocument document, Section section) {
        KEMService kemService = SpringContext.getBean(KEMService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        // fees tab
        for (Row row : section.getRows()) {
            for (Field field : row.getFields()) {

                if (field.getCONTAINER().equalsIgnoreCase(field.getFieldType())) {

                    for (Row containerRow : field.getContainerRows()) {
                        for (Field containerRowfield : containerRow.getFields()) {
                            if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction()) || KRADConstants.MAINTENANCE_COPY_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {
                                String totalAccruedFeespropertyName = KFSConstants.ADD_PREFIX + "." + EndowPropertyConstants.KEMID_FEES_TAB + "." + EndowPropertyConstants.KEMID_FEE_TOTAL_ACCRUED_FEES;
                                String indexedTotalAccruedFeesPropertyName = EndowPropertyConstants.KEMID_FEES_TAB + "\\[\\d+\\]\\" + "." + EndowPropertyConstants.KEMID_FEE_TOTAL_ACCRUED_FEES;
                                if (totalAccruedFeespropertyName.equalsIgnoreCase(containerRowfield.getPropertyName()) || containerRowfield.getPropertyName().matches(indexedTotalAccruedFeesPropertyName)) {
                                    containerRowfield.setReadOnly(true);
                                }
                            }
                            if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {

                                String indexedFeeStartDatePropertyName = EndowPropertyConstants.KEMID_FEES_TAB + "\\[\\d+\\]\\" + "." + EndowPropertyConstants.KEMID_FEE_START_DATE;
                                if (containerRowfield.getPropertyName().matches(indexedFeeStartDatePropertyName)) {
                                    containerRowfield.setReadOnly(true);
                                }
                            }

                            String feeEndDateField = KFSConstants.ADD_PREFIX + "." + EndowPropertyConstants.KEMID_FEES_TAB + "." + EndowPropertyConstants.KEMID_FEE_END_DATE;
                            if (feeEndDateField.equalsIgnoreCase(feeEndDateField)) {
                                String feeEndDateString = containerRowfield.getPropertyValue();
                                if (feeEndDateString != null && !KFSConstants.EMPTY_STRING.equalsIgnoreCase(feeEndDateString)) {

                                    String currentProcessDateString = kemService.getCurrentSystemProcessDate();

                                    try {
                                        Date currentProcessDate = dateTimeService.convertToSqlDate(currentProcessDateString);
                                        Date specialInstructionEndDate = dateTimeService.convertToSqlDate(feeEndDateString);

                                        if (specialInstructionEndDate.compareTo(currentProcessDate) < 0) {
                                            for (Field rowfield : row.getFields()) {
                                                rowfield.setReadOnly(true);

                                            }
                                        }
                                    }
                                    catch (ParseException ex) {
                                        // do nothing
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
     * Prepares the Report Groups tab.
     * 
     * @param document
     * @param section
     * @param field
     */
    private void prepareReportGroupsTab(MaintenanceDocument document, Section section) {
        KEMService kemService = SpringContext.getBean(KEMService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        for (Row row : section.getRows()) {
            for (Field field : row.getFields()) {

                if (field.getCONTAINER().equalsIgnoreCase(field.getFieldType())) {

                    for (Row containerRow : field.getContainerRows()) {
                        for (Field containerRowfield : containerRow.getFields()) {


                            // a record is no longer editable if the instruction end date is less than the current process or system
                            // date

                            String indexedDonorStatementTermDateField = EndowPropertyConstants.KEMID_REPORT_GROUP_TAB + "\\[\\d+\\]\\" + "." + EndowPropertyConstants.KEMID_REPORT_GRP_DATE_TERMINATED;
                            if (containerRowfield.getPropertyName().matches(indexedDonorStatementTermDateField)) {
                                String reportTerminatedDateString = containerRowfield.getPropertyValue();
                                if (reportTerminatedDateString != null && !KFSConstants.EMPTY_STRING.equalsIgnoreCase(reportTerminatedDateString)) {

                                    String currentProcessDateString = kemService.getCurrentSystemProcessDate();

                                    try {
                                        Date currentProcessDate = dateTimeService.convertToSqlDate(currentProcessDateString);
                                        Date reportTerminatedDate = dateTimeService.convertToSqlDate(reportTerminatedDateString);

                                        if (reportTerminatedDate.compareTo(currentProcessDate) < 0) {

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
                                    catch (ParseException ex) {
                                        // do nothing
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
     * Prepares the Donor Statements tab.
     * 
     * @param document
     * @param section
     * @param row
     * @param field
     */
    private void prepareDonorStatementsTab(MaintenanceDocument document, Section section) {
        KEMService kemService = SpringContext.getBean(KEMService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        for (Row row : section.getRows()) {
            for (Field field : row.getFields()) {
                if (field.getCONTAINER().equalsIgnoreCase(field.getFieldType())) {

                    for (Row containerRow : field.getContainerRows()) {
                        for (Field containerRowfield : containerRow.getFields()) {


                            // a record is no longer editable if the donor statement termination date is less than the current
                            // process or system date

                            String indexedDonorStatementTermDateField = EndowPropertyConstants.KEMID_DONOR_STATEMENTS_TAB + "\\[\\d+\\]\\" + "." + EndowPropertyConstants.KEMID_DONOR_STATEMENT_TERMINATION_DATE;

                            if (containerRowfield.getPropertyName().matches(indexedDonorStatementTermDateField)) {
                                String donorStatementTermDateString = containerRowfield.getPropertyValue();
                                if (donorStatementTermDateString != null && !KFSConstants.EMPTY_STRING.equalsIgnoreCase(donorStatementTermDateString)) {

                                    String currentProcessDateString = kemService.getCurrentSystemProcessDate();

                                    try {
                                        Date currentProcessDate = dateTimeService.convertToSqlDate(currentProcessDateString);
                                        Date donorStatementTermDate = dateTimeService.convertToSqlDate(donorStatementTermDateString);

                                        if (donorStatementTermDate.compareTo(currentProcessDate) < 0) {
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
                                    catch (ParseException ex) {
                                        // do nothing
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
     * Prepares the Combine Donor statements tab.
     * 
     * @param document
     * @param section
     * @param row
     * @param field
     */
    private void prepareCombineDonorStatementsTab(MaintenanceDocument document, Section section) {
        KEMService kemService = SpringContext.getBean(KEMService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        for (Row row : section.getRows()) {
            for (Field field : row.getFields()) {
                if (field.getCONTAINER().equalsIgnoreCase(field.getFieldType())) {

                    for (Row containerRow : field.getContainerRows()) {
                        for (Field containerRowfield : containerRow.getFields()) {


                            // a record is no longer editable if the combine donor statement termination date is less than the
                            // current
                            // process or system date

                            String indexedCombineDonorStatementTermDateField = EndowPropertyConstants.KEMID_COMBINE_DONOR_STATEMENTS_TAB + "\\[\\d+\\]\\" + "." + EndowPropertyConstants.KEMID_COMBINE_DONOR_STATEMENT_TERMINATION_DATE;

                            if (containerRowfield.getPropertyName().matches(indexedCombineDonorStatementTermDateField)) {
                                String combineDonorStatementTermDateString = containerRowfield.getPropertyValue();
                                if (combineDonorStatementTermDateString != null && !KFSConstants.EMPTY_STRING.equalsIgnoreCase(combineDonorStatementTermDateString)) {

                                    String currentProcessDateString = kemService.getCurrentSystemProcessDate();

                                    try {
                                        Date currentProcessDate = dateTimeService.convertToSqlDate(currentProcessDateString);
                                        Date combineDonorStatementTermDate = dateTimeService.convertToSqlDate(combineDonorStatementTermDateString);

                                        if (combineDonorStatementTermDate.compareTo(currentProcessDate) < 0) {
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
                                    catch (ParseException ex) {
                                        // do nothing
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
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument arg0, Map<String, String[]> arg1) {
        super.processAfterCopy(arg0, arg1);

        initializeAttributes(arg0);

        newKemid.refreshNonUpdateableReferences();
        newKemid.getKemidAgreements().clear();
        oldKemid.getKemidAgreements().clear();
        newKemid.getKemidSourcesOfFunds().clear();
        oldKemid.getKemidSourcesOfFunds().clear();
        newKemid.getKemidBenefittingOrganizations().clear();
        oldKemid.getKemidBenefittingOrganizations().clear();
        newKemid.getKemidGeneralLedgerAccounts().clear();
        oldKemid.getKemidGeneralLedgerAccounts().clear();
        newKemid.getKemidPayoutInstructions().clear();
        oldKemid.getKemidPayoutInstructions().clear();
        newKemid.getKemidUseCriteria().clear();
        oldKemid.getKemidUseCriteria().clear();
        newKemid.getKemidFees().clear();
        oldKemid.getKemidFees().clear();
        newKemid.getKemidReportGroups().clear();
        oldKemid.getKemidReportGroups().clear();
        newKemid.getKemidSpecialInstructions().clear();
        oldKemid.getKemidSpecialInstructions().clear();
        newKemid.getKemidDonorStatements().clear();
        oldKemid.getKemidDonorStatements().clear();
        newKemid.getKemidCombineDonorStatements().clear();
        oldKemid.getKemidCombineDonorStatements().clear();
        newKemid.getKemidAuthorizations().clear();
        oldKemid.getKemidAuthorizations().clear();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        KEMID kemid = (KEMID) getBusinessObject();

        // set the KEMID transaction restriction code
        for (KemidAgreement kemidAgreement : kemid.getKemidAgreements()) {
            if (kemidAgreement.isUseTransactionRestrictionFromAgreement()) {
                kemidAgreement.refreshReferenceObject(EndowPropertyConstants.KEMID_AGRMNT_STATUS);
                if (ObjectUtils.isNotNull(kemidAgreement.getAgreementStatus())) {
                    kemid.setTransactionRestrictionCode(kemidAgreement.getAgreementStatus().getDefaultTransactionRestrictionCode());
                }
            }
        }
        super.prepareForSave();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        if (collectionName.equalsIgnoreCase(EndowPropertyConstants.KEMID_COMBINE_DONOR_STATEMENTS_TAB)) {
            KemidCombineDonorStatement addLine = (KemidCombineDonorStatement) newCollectionLines.get(collectionName);
            KEMService kemService = SpringContext.getBean(KEMService.class);
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            try {
                addLine.setCombineDate(dateTimeService.convertToSqlDate(kemService.getCurrentSystemProcessDate()));
            }
            catch (ParseException ex) {
                // do nothing?
            }
        }
        super.addNewLineToCollection(collectionName);
    }

    /**
     * Initializes newKemid and oldKemid.
     * 
     * @param document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newKemid == null) {
            newKemid = (KEMID) document.getNewMaintainableObject().getBusinessObject();
        }
        if (oldKemid == null) {
            oldKemid = (KEMID) document.getOldMaintainableObject().getBusinessObject();
        }
    }

}
