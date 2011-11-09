/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignment;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignmentDetail;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.SensitiveDataFixture;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

public class SensitiveDataRuleTest extends PurapRuleTestBase {

    private SensitiveDataRule sensitiveDataRule;    
    private Map<String, GenericValidation> validations;
    PurchaseOrderDocument po;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sensitiveDataRule = new SensitiveDataRule();
        validations = SpringContext.getBeansOfType(GenericValidation.class);        
        po = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
    }
    
    private MaintenanceDocumentBase getMaintenanceDocument(SensitiveDataFixture oldSDFixture, SensitiveDataFixture newSDFixture){
        MaintenanceDocumentBase doc = null;
        try {
            doc = (MaintenanceDocumentBase) SpringContext.getBean(DocumentService.class).getNewDocument("PMSN");
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        doc.getDocumentHeader().setExplanation("JUnit test document");
        Maintainable oldObj = doc.getOldMaintainableObject();
        Maintainable newObj = doc.getNewMaintainableObject();
        oldObj.setBusinessObject(oldSDFixture.getSensitiveDataBO());
        newObj.setBusinessObject(newSDFixture.getSensitiveDataBO());
        return doc;
    }
    
    @ConfigureContext(session = parke)
    public final void testAssignSensitiveDataReasonEmpty() {
        List<SensitiveData> sds = new ArrayList<SensitiveData>();
        sds.add(SensitiveDataFixture.SENSITIVE_DATA_ACTIVE.getSensitiveDataBO());

        PurchaseOrderAssignSensitiveDataValidation validation = (PurchaseOrderAssignSensitiveDataValidation)validations.get("PurchaseOrder-assignSensitiveDataValidation-test");
        validation.setAccountingDocumentForValidation(po);
        validation.setSensitiveDataAssignmentReason(null);
        validation.setSensitiveDatasAssigned(sds);
        assertFalse( validation.validate(null) );               
    }
    
    @ConfigureContext(session = parke)
    public final void testAssignSensitiveDataInactive() {
        List<SensitiveData> sds = new ArrayList<SensitiveData>();
        sds.add(SensitiveDataFixture.SENSITIVE_DATA_INACTIVE.getSensitiveDataBO());

        PurchaseOrderAssignSensitiveDataValidation validation = (PurchaseOrderAssignSensitiveDataValidation)validations.get("PurchaseOrder-assignSensitiveDataValidation-test");
        validation.setAccountingDocumentForValidation(po);
        validation.setSensitiveDatasAssigned(sds);
        assertFalse( validation.validate(null) );               
    }
    
    @ConfigureContext(session = parke)
    public final void testAssignSensitiveDataIDuplicate() {
        List<SensitiveData> sds = new ArrayList<SensitiveData>();
        sds.add(SensitiveDataFixture.SENSITIVE_DATA_ACTIVE.getSensitiveDataBO());
        sds.add(SensitiveDataFixture.SENSITIVE_DATA_ACTIVE.getSensitiveDataBO());

        PurchaseOrderAssignSensitiveDataValidation validation = (PurchaseOrderAssignSensitiveDataValidation)validations.get("PurchaseOrder-assignSensitiveDataValidation-test");
        validation.setAccountingDocumentForValidation(po);
        validation.setSensitiveDatasAssigned(sds);
        assertFalse( validation.validate(null) );               
    }
    
    /**
     * This test combines the test for SensitiveDataService and SensitiveDataRule on InactivationBlocking
     * since the latter involves all major operations provided by the service class. 
     */
    @ConfigureContext(session = parke)
    public final void testSensitiveDataInactivationBlocking() {
        // create a new sensitive data entry and save it (if not exists yet) for this test     
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        SensitiveData sdNew = SensitiveDataFixture.SENSITIVE_DATA_TO_INACTIVATE.getSensitiveDataBO();
        if (boService.retrieve(sdNew) == null) {
            boService.save(sdNew);
        }
        
        // add an active sensitive data entry for the PO, update all 3 tables to keep consistency
        List<SensitiveData> sds = new ArrayList<SensitiveData>();
        sds.add(sdNew);        
        SensitiveDataService sdService = SpringContext.getBean(SensitiveDataService.class); 
        
        Integer poId = po.getPurapDocumentIdentifier();
        if (poId == null) {
            poId = new Integer(1000);
        }
        
        // update table SensitiveDataAssignment
        Date currentDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        SensitiveDataAssignment sda = new SensitiveDataAssignment();
        sda.setPurapDocumentIdentifier(poId);
        sda.setSensitiveDataAssignmentReasonText("test");
        sda.setSensitiveDataAssignmentChangeDate(currentDate);
        sda.setSensitiveDataAssignmentPersonIdentifier("parke");
        sdService.saveSensitiveDataAssignment(sda);        

        // update table SensitiveDataAssignmentDetail
        Integer sdaId = sdService.getLastSensitiveDataAssignmentId(poId);
        List<SensitiveDataAssignmentDetail> sdads = new ArrayList<SensitiveDataAssignmentDetail>();
        for (SensitiveData sd : sds) {
            SensitiveDataAssignmentDetail sdad = new SensitiveDataAssignmentDetail();
            sdad.setSensitiveDataAssignmentIdentifier(sdaId);
            sdad.setSensitiveDataCode(sd.getSensitiveDataCode());
            sdads.add(sdad);
        }
        sdService.saveSensitiveDataAssignmentDetails(sdads);        
        
        // update table PurchaseOrderSensitiveData
        sdService.deletePurchaseOrderSensitiveDatas(poId);
        List<PurchaseOrderSensitiveData> posds = new ArrayList<PurchaseOrderSensitiveData>();
        for (SensitiveData sd : sds) {
            PurchaseOrderSensitiveData posd = new PurchaseOrderSensitiveData();
            posd.setPurapDocumentIdentifier(poId);
            posd.setRequisitionIdentifier(po.getRequisitionIdentifier());
            posd.setSensitiveDataCode(sd.getSensitiveDataCode());
            posds.add(posd);
        }
        sdService.deletePurchaseOrderSensitiveDatas(poId);
        sdService.savePurchaseOrderSensitiveDatas(posds);
        
        // try to inactivate the sensitive data just assigned to the PO, should fail rule
        GlobalVariables.getUserSession().setBackdoorUser("khuntley");
        MaintenanceDocumentBase maintDoc = getMaintenanceDocument(SensitiveDataFixture.SENSITIVE_DATA_TO_INACTIVATE, SensitiveDataFixture.SENSITIVE_DATA_INACTIVATED);
        sensitiveDataRule.setupBaseConvenienceObjects(maintDoc);
        assertFalse(sensitiveDataRule.processCustomRouteDocumentBusinessRules(maintDoc));
        assertFalse(sensitiveDataRule.processCustomApproveDocumentBusinessRules(maintDoc));
        GlobalVariables.getUserSession().clearBackdoorUser();
    }
        
}
