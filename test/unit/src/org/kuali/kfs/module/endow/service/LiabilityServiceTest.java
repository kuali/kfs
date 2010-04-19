/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.endow.service; 

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPullup;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocumentBase;
import org.kuali.kfs.module.endow.document.LiabilityIncreaseDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * This class...
 */
@ConfigureContext(session = khuntley)
public class LiabilityServiceTest extends KualiTestBase 
{
    private SequenceAccessorService sequenceAccessorService;
    private BusinessObjectService businessObjectService;

    private boolean runTests() 
    { // change this to return false to prevent running tests
        return true;
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception 
    {
        super.setUp();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
    }

    @ConfigureContext(shouldCommitTransactions = false)
    public void testOJBConfiguration() throws Exception 
    {
    
        if (!runTests())
        return;
            
        //Insert Transaction Document
        LiabilityIncreaseDocument tx = new LiabilityIncreaseDocument();
        tx.setDocumentNumber("4160");
        tx.setTransactionSubTypeCode("C");
        tx.setTransactionSourceTypeCode("M");
        
        //Saving a Trans Line
        EndowmentTransactionLine tranLine = new EndowmentSourceTransactionLine();
        tranLine.setDocumentNumber("4160");
        tranLine.setTransactionLineNumber(new KualiInteger("1"));
        tranLine.setKemid("099PLTF013");
        tranLine.setEtranCode("00100");
        tranLine.setTransactionIPIndicatorCode("I");
        tranLine.setTransactionAmount(new KualiDecimal(2.3));
        
        //Setting the transaction line.
        List tranList = new ArrayList();
        tranList.add(tranLine);
        tx.setSourceTransactionLines(tranList);

        //Saving a Holding line for a Trans Line
        EndowmentTransactionTaxLotLine hldg = new EndowmentTransactionTaxLotLine();
        hldg.setDocumentNumber("4160");
        hldg.setDocumentLineNumber(new KualiInteger("1"));
        hldg.setDocumentLineTypeCode("F");
        hldg.setTransactionHoldingLongTermNumber(new KualiInteger("99"));
        hldg.setLotUnits(new KualiDecimal("22"));
        
        //Setting the tax lot line.
        List taxList = new ArrayList();
        taxList.add(hldg);
        tranLine.setTaxLotLines(taxList);
        //businessObjectService.save(hldg);
        
        //Creating Tran Security
        EndowmentTransactionSecurity tranSec = new EndowmentSourceTransactionSecurity();
        tranSec.setDocumentNumber("4160");
        tranSec.setSecurityID("9128273E0");
        tranSec.setRegistrationCode("01P");
        
        List secList = new ArrayList();
        secList.add(tranSec);
        
        tx.setSourceTransactionSecurity(tranSec);
        
        //saving Part of a Liability Transaction Document
        businessObjectService.save(tx);
        
        //Retrirve Tx Doc
        LiabilityIncreaseDocument newTx = businessObjectService.findBySinglePrimaryKey(LiabilityIncreaseDocument.class, "4160");
        
        assertEquals("S0urce Lines",1, newTx.getSourceTransactionLines().size());
        assertEquals("Target Lines", 0,newTx.getTargetTransactionLines().size());
        assertNotNull("Source Security",newTx.getSourceTransactionSecurity());
        
        /*
         EndowmentSourceTransactionSecurity sec = new EndowmentSourceTransactionSecurity();
         
        sec.setDocumentNumber("4160");
        sec.setSecurityID("004764106");
        sec.setRegistrationCode("01P");
       //tx.setSourceTransactionSecurity(sec);
        //saving Part of a Liability Transaction Document - Security 
        //businessObjectService.save(sec);
        */


    }
}

;