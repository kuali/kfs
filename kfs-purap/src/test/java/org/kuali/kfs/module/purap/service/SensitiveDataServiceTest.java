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
package org.kuali.kfs.module.purap.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignment;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignmentDetail;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.SensitiveDataFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class SensitiveDataServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SensitiveDataServiceTest.class);
    
    protected BusinessObjectService boService;    
    protected SensitiveDataService sdService;
    protected PurchaseOrderDocument po;    
    protected SensitiveData sdNew;  
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if ( boService == null ) {
            boService = SpringContext.getBean(BusinessObjectService.class);    
        }
        if ( sdService == null ) {
            sdService = SpringContext.getBean(SensitiveDataService.class);
        }
        
        if ( po == null ) {
            po = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        }
        if (po.getPurapDocumentIdentifier() == null) {
            po.setPurapDocumentIdentifier(new Integer(9001));
        }

        if (po.getRequisitionIdentifier() == null) {
            po.setRequisitionIdentifier(new Integer(9001));
        }
        
        // create a new sensitive data entry and save it (if not exists yet), to make sure that at least one entry exist
        // in the sensitive data table, for the use of the tests here
        if (sdNew == null) {
            sdNew = SensitiveDataFixture.SENSITIVE_DATA_ACTIVE.getSensitiveDataBO();
        }                
        if (boService.retrieve(sdNew) == null) {
            boService.save(sdNew);
        }        
    }
            
    @ConfigureContext(session = parke, shouldCommitTransactions = true)
    public void testGetSensitiveData() {
        List<SensitiveData> sds = sdService.getAllSensitiveDatas();
        assertTrue(sds.size() > 0);
        String code = sdNew.getSensitiveDataCode();
        SensitiveData sdRet = sdService.getSensitiveDataByCode(code);
        assertTrue(sdRet != null);
        assertTrue(sdRet.getSensitiveDataCode().equalsIgnoreCase(code));     
    }
    
    /**
     * Test access to the three tables involved in the process of sensitive data assignment.
     */
    @ConfigureContext(session = parke, shouldCommitTransactions = true)
    public void testSensitiveDataAssignment() {
        Integer poId = po.getPurapDocumentIdentifier();
        String reason = "test";
        String userId = "parke";
        Date date = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        List<SensitiveData> sds = sdService.getAllSensitiveDatas();
                
        // test SensitiveDataAssignment save
        SensitiveDataAssignment sda = new SensitiveDataAssignment();
        sda.setPurapDocumentIdentifier(poId);
        sda.setSensitiveDataAssignmentReasonText(reason);
        sda.setSensitiveDataAssignmentChangeDate(date);
        sda.setSensitiveDataAssignmentPersonIdentifier(userId);
        sdService.saveSensitiveDataAssignment(sda);        
        
        // test SensitiveDataAssignment retrieval
        SensitiveDataAssignment sdaRet = sdService.getLastSensitiveDataAssignment(poId);
        assertTrue(sdaRet.getPurapDocumentIdentifier().equals(poId));     
        assertTrue(sdaRet.getSensitiveDataAssignmentReasonText().equals(reason));     
        //assertTrue(sdaRet.getSensitiveDataAssignmentChangeDate().compareTo(date) == 0);     
        assertTrue(sdaRet.getSensitiveDataAssignmentPersonIdentifier().equals(userId));     
        
        // check that the retrieved sda is the latest one, by comparing the assignment ID
        Integer sdaId = sdaRet.getSensitiveDataAssignmentIdentifier();
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(PurapPropertyConstants.PURAP_DOC_ID, poId);
        Collection<SensitiveDataAssignment> sdas = boService.findMatching(SensitiveDataAssignment.class, fieldValues);        
        for (SensitiveDataAssignment sdaR : sdas) {
            assertTrue(sdaId >= sdaR.getSensitiveDataAssignmentIdentifier());   
        }
        
        // test SensitiveDataAssignmentDetail save
        List<SensitiveDataAssignmentDetail> sdads = new ArrayList<SensitiveDataAssignmentDetail>();
        for (SensitiveData sd : sds) {
            SensitiveDataAssignmentDetail sdad = new SensitiveDataAssignmentDetail();
            sdad.setSensitiveDataAssignmentIdentifier(sdaId);
            sdad.setSensitiveDataCode(sd.getSensitiveDataCode());
            sdads.add(sdad);
        }
        sdService.saveSensitiveDataAssignmentDetails(sdads);        

        // test SensitiveDataAssignmentDetail retrieval
        List<SensitiveDataAssignmentDetail> sdadsRet = sdService.getLastSensitiveDataAssignmentDetails(poId);   
        assertTrue( sdadsRet.size() == sdads.size());
        for (SensitiveDataAssignmentDetail sdadR : sdadsRet) {
            assertTrue(sdadR.getSensitiveDataAssignmentIdentifier().equals(sdaId));   
        }
        
        // test PurchaseOrderSensitiveData delete
        sdService.deletePurchaseOrderSensitiveDatas(poId);
        List<SensitiveData> sdsRet = sdService.getSensitiveDatasAssignedByPoId(poId);
        assertTrue(sdsRet.size() == 0);

        // test PurchaseOrderSensitiveData save        
        List<PurchaseOrderSensitiveData> posds = new ArrayList<PurchaseOrderSensitiveData>();
        for (SensitiveData sd : sds) {
            PurchaseOrderSensitiveData posd = new PurchaseOrderSensitiveData();
            posd.setPurapDocumentIdentifier(poId);
            posd.setRequisitionIdentifier(po.getRequisitionIdentifier());
            posd.setSensitiveDataCode(sd.getSensitiveDataCode());
            posds.add(posd);
        }
        sdService.savePurchaseOrderSensitiveDatas(posds);
        
        // test PurchaseOrderSensitiveData retrieval
        sdsRet = sdService.getSensitiveDatasAssignedByPoId(poId);
        assertTrue(sdsRet.size() == posds.size());
    }

}
