/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.fixture;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.workflow.service.impl.KualiWorkflowInfoImpl;

public enum DocumentRouteHeaderValueFixture {
    REC1 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();
            obj.setRouteHeaderId(11L);
            obj.setDocumentTypeId(docTypeId("RequisitionDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(6);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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

            obj.setRouteHeaderId(12L);
            obj.setDocumentTypeId(docTypeId("RequisitionDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(6);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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

            obj.setRouteHeaderId(13L);
            obj.setDocumentTypeId(docTypeId("RequisitionDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(0);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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

            obj.setRouteHeaderId(21L);
            obj.setDocumentTypeId(docTypeId("PurchaseOrderDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(6);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
            obj.setDocTitle("Purchase Order - CAB Test Data 1");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("khuntley"));
            obj.setRoutedByUserWorkflowId(principalId("khuntley"));
            obj.setObjectId("JHBGJHGJKHJS5456677675");
            return obj;
        }
    },
    REC5 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();

            obj.setRouteHeaderId(22L);
            obj.setDocumentTypeId(docTypeId("PurchaseOrderDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(6);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
            obj.setDocTitle("Purchase Order - CAB Test Data 1");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("khuntley"));
            obj.setRoutedByUserWorkflowId(principalId("khuntley"));
            obj.setObjectId("JHBGJHGJKHJS5456677674");
            return obj;
        }
    },
    REC6 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();

            obj.setRouteHeaderId(23L);
            obj.setDocumentTypeId(docTypeId("PurchaseOrderDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(6);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
            obj.setDocTitle("Purchase Order - CAB Test Data 1");
            obj.setAppDocId(null);
            obj.setDocVersion(1);
            obj.setInitiatorWorkflowId(principalId("khuntley"));
            obj.setRoutedByUserWorkflowId(principalId("khuntley"));
            obj.setObjectId("JHBGJHGJKHJS5456677673");
            return obj;
        }
    },
    REC7 {
        @Override
        public DocumentRouteHeaderValue newRecord() {
            DocumentRouteHeaderValue obj = new DocumentRouteHeaderValue();

            obj.setRouteHeaderId(31L);
            obj.setDocumentTypeId(docTypeId("PaymentRequestDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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

            obj.setRouteHeaderId(32L);
            obj.setDocumentTypeId(docTypeId("PaymentRequestDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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

            obj.setRouteHeaderId(33L);
            obj.setDocumentTypeId(docTypeId("PaymentRequestDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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

            obj.setRouteHeaderId(34L);
            obj.setDocumentTypeId(docTypeId("PaymentRequestDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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

            obj.setRouteHeaderId(35L);
            obj.setDocumentTypeId(docTypeId("PaymentRequestDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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

            obj.setRouteHeaderId(36L);
            obj.setDocumentTypeId(docTypeId("PaymentRequestDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(5);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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

            obj.setRouteHeaderId(41L);
            obj.setDocumentTypeId(docTypeId("VendorCreditMemoDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(2);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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

            obj.setRouteHeaderId(51L);
            obj.setDocumentTypeId(docTypeId("CustomerInvoiceDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(0);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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
            obj.setRouteHeaderId(52L);
            obj.setDocumentTypeId(docTypeId("CustomerInvoiceDocument"));
            obj.setDocRouteStatus("F");
            obj.setDocRouteLevel(0);
            obj.setStatusModDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setApprovedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setFinalizedDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteStatusDate(new java.sql.Timestamp(new Date().getTime()));
            obj.setRouteLevelDate(null);
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


    public static Long docTypeId(String documentTypeName) {
        try {
            DocumentTypeDTO docType = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeVO(documentTypeName);
            if (docType != null) {
                return docType.getDocTypeId();
            }
        }
        catch (Exception e) {
        }
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
