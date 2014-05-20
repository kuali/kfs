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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.MockWorkflowDocument;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum FinancialSystemDocumentHeaderFixture {
    REQS1 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("11");
            obj.setDocumentDescription("CAB Test Data 1");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("REQS");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CONTRACT_MANAGER_ASSGN);
            obj.setInitiatorPrincipalId(principalId("khuntley"));
            return obj;
        }
    },
    REQS2 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("12");
            obj.setDocumentDescription("CAB Test Data 1");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("REQS");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CONTRACT_MANAGER_ASSGN);
            obj.setInitiatorPrincipalId(principalId("khuntley"));
            return obj;
        }
    },
    REQS3 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("13");
            obj.setDocumentDescription("CAB Test Data 1");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("REQS");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.RequisitionStatuses.APPDOC_IN_PROCESS);
            obj.setInitiatorPrincipalId(principalId("khuntley"));
            return obj;
        }
    },
    PO1 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("21");
            obj.setDocumentDescription("CAB Test Data 1");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("PO");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN);
            obj.setInitiatorPrincipalId(principalId("khuntley"));
            return obj;
        }
    },
    PO2 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("22");
            obj.setDocumentDescription("CAB Test Data 1");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("PO");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN);
            obj.setInitiatorPrincipalId(principalId("khuntley"));
            return obj;
        }
    },
    PO3 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("23");
            obj.setDocumentDescription("CAB Test Data 1");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("PO");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN);
            obj.setInitiatorPrincipalId(principalId("khuntley"));
            return obj;
        }
    },
    PREQ1 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("31");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("PREQ");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setInitiatorPrincipalId(principalId("khuntley"));
            return obj;
        }
    },
    PREQ2 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("32");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("PREQ");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setInitiatorPrincipalId(principalId("appleton"));
            return obj;
        }
    },
    PREQ3 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("33");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("PREQ");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setInitiatorPrincipalId(principalId("appleton"));
            return obj;
        }
    },
    PREQ4 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("34");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("PREQ");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setInitiatorPrincipalId(principalId("appleton"));
            return obj;
        }
    },
    PREQ5 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("35");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("PREQ");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setInitiatorPrincipalId(principalId("appleton"));
            return obj;
        }
    },
    PREQ6 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("36");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("PREQ");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
            obj.setInitiatorPrincipalId(principalId("appleton"));
            return obj;
        }
    },
    CM1 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("41");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("CM");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setApplicationDocumentStatus(PurapConstants.CreditMemoStatuses.APPDOC_COMPLETE);
            obj.setInitiatorPrincipalId(principalId("appleton"));
            return obj;
        }
    },
    CINV1 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("51");
            obj.setDocumentDescription("Customer Invoice1");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("INV");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setInitiatorPrincipalId(principalId("khuntley"));
            return obj;
        }
    },
    CINV2 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("52");
            obj.setDocumentDescription("Customer Invoice2");
            obj.setWorkflowDocument(new MockWorkflowDocument() {});
            obj.setWorkflowDocumentTypeName("INV");
            obj.setWorkflowDocumentStatusCode(DocumentStatus.FINAL.getCode());
            obj.setInitiatorPrincipalId(principalId("khuntley"));
            return obj;
        }
    };
    public abstract FinancialSystemDocumentHeader newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());
    }

    private static List<FinancialSystemDocumentHeader> getAll() {
        List<FinancialSystemDocumentHeader> recs = new ArrayList<FinancialSystemDocumentHeader>();
        // recs.add(REC1.newRecord());
        // recs.add(REC2.newRecord());
        // recs.add(REC3.newRecord());
        // recs.add(PO1.newRecord());
        // recs.add(PO2.newRecord());
        // recs.add(PO3.newRecord());
        // recs.add(PREQ1.newRecord());
        // recs.add(PREQ2.newRecord());
        // recs.add(PREQ3.newRecord());
        // recs.add(PREQ4.newRecord());
        // recs.add(PREQ5.newRecord());
        // recs.add(PREQ6.newRecord());
        // recs.add(CM1.newRecord());
        recs.add(CINV1.newRecord());
        recs.add(CINV2.newRecord());
        return recs;
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
}