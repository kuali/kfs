/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cab.fixture;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum DocumentRouteHeaderValueFixture {

    REC1 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("11");
            obj.setDocumentTypeId(docTypeId("REQS"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(6);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Requisition - CAB Test Data 1");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("khuntley"));
            obj.setRoutedByUserWorkflowId(principalId("khuntley"));
            obj.setObjectId("JHBGJHGJKHJS5456677678");
            return obj;
        }
    },
    REC2 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("12");
            obj.setDocumentTypeId(docTypeId("REQS"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(6);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Requisition - CAB Test Data 1");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("khuntley"));
            obj.setRoutedByUserWorkflowId(principalId("khuntley"));
            obj.setObjectId("JHBGJHGJKHJS5456677677");
            return obj;
        }
    },
    REC3 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("13");
            obj.setDocumentTypeId(docTypeId("REQS"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(0);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Contract Manager Assignment - Contract Manager Assigned");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("khuntley"));
            obj.setRoutedByUserWorkflowId(principalId("khuntley"));
            obj.setObjectId("JHBGJHGJKHJS5456677676");
            return obj;
        }
    },
    REC4 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("21");
            obj.setDocumentTypeId(docTypeId("PO"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(6);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Purchase Order - CAB Test Data 1");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("khuntley"));
            obj.setRoutedByUserWorkflowId(principalId("khuntley"));
            obj.setObjectId("JHBGJHGJKHJS5456677675");
            obj.setAppDocStatus(CabConstants.PO_STATUS_CODE_OPEN);
            return obj;
        }
    },
    REC5 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("22");
            obj.setDocumentTypeId(docTypeId("PO"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(6);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Purchase Order - CAB Test Data 1");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("khuntley"));
            obj.setRoutedByUserWorkflowId(principalId("khuntley"));
            obj.setObjectId("JHBGJHGJKHJS5456677674");
            obj.setAppDocStatus(CabConstants.PO_STATUS_CODE_OPEN);
            return obj;
        }
    },
    REC6 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("23");
            obj.setDocumentTypeId(docTypeId("PO"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(6);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Purchase Order - CAB Test Data 1");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("khuntley"));
            obj.setRoutedByUserWorkflowId(principalId("khuntley"));
            obj.setObjectId("JHBGJHGJKHJS5456677673");
            obj.setAppDocStatus(CabConstants.PO_STATUS_CODE_OPEN);
            return obj;
        }
    },
    REC7 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("31");
            obj.setDocumentTypeId(docTypeId("PREQ"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Payment Request - PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("appleton"));
            obj.setRoutedByUserWorkflowId(principalId("appleton"));
            obj.setObjectId("JHBGJHGJKHJS5456677672");
            return obj;
        }
    },
    REC8 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("32");
            obj.setDocumentTypeId(docTypeId("PREQ"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Payment Request - PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("appleton"));
            obj.setRoutedByUserWorkflowId(principalId("appleton"));
            obj.setObjectId("JHBGJHGJKHJS5456677671");
            return obj;
        }
    },
    REC9 {

        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("33");
            obj.setDocumentTypeId(docTypeId("PREQ"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Payment Request - PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("appleton"));
            obj.setRoutedByUserWorkflowId(principalId("appleton"));
            obj.setObjectId("JHBGJHGJKHJS5456677670");
            return obj;
        }
    },
    REC10 {

        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("34");
            obj.setDocumentTypeId(docTypeId("PREQ"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Payment Request - PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("appleton"));
            obj.setRoutedByUserWorkflowId(principalId("appleton"));
            obj.setObjectId("JHBGJHGJKHJS5456677669");
            return obj;
        }
    },
    REC11 {

        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("35");
            obj.setDocumentTypeId(docTypeId("PREQ"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Payment Request - PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("appleton"));
            obj.setRoutedByUserWorkflowId(principalId("appleton"));
            obj.setObjectId("JHBGJHGJKHJS5456677668");
            return obj;
        }
    },
    REC12 {

        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("36");
            obj.setDocumentTypeId(docTypeId("PREQ"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Payment Request - PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("appleton"));
            obj.setRoutedByUserWorkflowId(principalId("appleton"));
            obj.setObjectId("JHBGJHGJKHJS5456677667");
            return obj;
        }
    },
    REC13 {

        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("41");
            obj.setDocumentTypeId(docTypeId("CM"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(2);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Credit Memo - PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("appleton"));
            obj.setRoutedByUserWorkflowId(principalId("appleton"));
            obj.setObjectId("JHBGJHGJKHJS5456677666");
            return obj;
        }
    },
    REC14 {

        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("51");
            obj.setDocumentTypeId(docTypeId("INV"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(0);
            obj.setAppDocStatusDate(timeStamp);
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Customer Invoice - Customer Invoice");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("khuntley"));
            obj.setRoutedByUserWorkflowId(principalId("khuntley"));
            obj.setObjectId("JHBGJHGJKHJS5456677665");
            return obj;
        }
    },
    REC15 {

        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            obj.setDocumentId("52");
            obj.setDocumentTypeId(docTypeId("INV"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(0);
            long time = SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime();
            obj.setAppDocStatusDate(new java.sql.Timestamp(time));
            obj.setCreateDate(timeStamp);
            obj.setApprovedDate(timeStamp);
            obj.setFinalizedDate(timeStamp);
            obj.setRouteStatusDate(timeStamp);
            obj.setDateModified(timeStamp);

            obj.setDocTitle("Customer Invoice - Customer Invoice");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("khuntley"));
            obj.setRoutedByUserWorkflowId(principalId("khuntley"));
            obj.setObjectId("JHBGJHGJKHJS5456677664");
            return obj;
        }
    };

    public abstract DocumentRouteHeaderValue newRecord();

    public static String docTypeId(String documentTypeName) {
        try {
            DocumentType docType = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(documentTypeName);
            if (docType != null) {
                return docType.getId();
            }
        }
        catch (Exception e) {}
        return null;
    }

    public static String principalId(String principalName) {
        try {
            Person person = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(principalName);
            if (person != null) {
                return person.getPrincipalId();
            }
        }
        catch (Exception e) {
        }
        return null;
    }

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());

    }

    private static List<PersistableBusinessObjectBase> getAll() {
        List<PersistableBusinessObjectBase> recs = new ArrayList<PersistableBusinessObjectBase>();
        recs.add(REC1.newRecord());
        recs.add(REC2.newRecord());
        recs.add(REC3.newRecord());
        recs.add(REC4.newRecord());
        recs.add(REC5.newRecord());
        recs.add(REC6.newRecord());
        recs.add(REC7.newRecord());
        recs.add(REC8.newRecord());
        recs.add(REC9.newRecord());
        recs.add(REC10.newRecord());
        recs.add(REC11.newRecord());
        recs.add(REC12.newRecord());
        recs.add(REC13.newRecord());
        recs.add(REC14.newRecord());
        recs.add(REC15.newRecord());
        return recs;
    }


}
