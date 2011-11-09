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

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum FinancialSystemDocumentHeaderFixture {
    REQS1 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("11");
            obj.setDocumentDescription("CAB Test Data 1");
            return obj;
        }
    },
    REQS2 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("12");
            obj.setDocumentDescription("CAB Test Data 1");
            return obj;
        }
    },
    REQS3 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("13");
            obj.setDocumentDescription("CAB Test Data 1");
            return obj;
        }
    },
    PO1 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("21");
            obj.setDocumentDescription("CAB Test Data 1");
            return obj;
        }
    },
    PO2 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("22");
            obj.setDocumentDescription("CAB Test Data 1");
            return obj;
        }
    },
    PO3 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("23");
            obj.setDocumentDescription("CAB Test Data 1");
            return obj;
        }
    },
    PREQ1 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("31");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            return obj;
        }
    },
    PREQ2 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("32");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            return obj;
        }
    },
    PREQ3 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("33");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            return obj;
        }
    },
    PREQ4 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("34");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            return obj;
        }
    },
    PREQ5 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("35");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            return obj;
        }
    },
    PREQ6 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("36");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            return obj;
        }
    },
    CM1 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("41");
            obj.setDocumentDescription("PO: 1007 Vendor: BESCO WATER TREATMENT I");
            return obj;
        }
    },
    CINV1 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("51");
            obj.setDocumentDescription("Customer Invoice1");
            return obj;
        }
    },
    CINV2 {
        @Override
        public FinancialSystemDocumentHeader newRecord() {
            FinancialSystemDocumentHeader obj = new FinancialSystemDocumentHeader();
            obj.setDocumentNumber("52");
            obj.setDocumentDescription("Customer Invoice2");
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
}
