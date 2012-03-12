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
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.businessobject.TicklerKEMID;
import org.kuali.kfs.module.endow.businessobject.TicklerRecipientGroup;
import org.kuali.kfs.module.endow.businessobject.TicklerRecipientPrincipal;
import org.kuali.kfs.module.endow.businessobject.TicklerSecurity;
import org.kuali.kfs.module.endow.businessobject.TicklerTypeCode;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;

/**
 * This class...
 */
@ConfigureContext(session = khuntley)
public class TicklerServiceTest extends KualiTestBase 
{
    private SequenceAccessorService sequenceAccessorService;
    private BusinessObjectService businessObjectService;

    private boolean runTests() 
    { // change this to return false to prevent running tests
        return false;
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

//  used to compare OJB version with JDBC version - leave out for now
//    @ConfigureContext(shouldCommitTransactions = true)
//    public void testBuildPullup() throws Exception {
//
//        if (!runTests())
//            return;
//
//        budgetOrganizationTreeService.buildPullup(principalId, chartOfAccountsCode, organizationCode);
//        HashMap map = new HashMap();
//        map.put("principalId", principalId);
//        map.put("chartOfAccountsCode", chartOfAccountsCode);
//        map.put("organizationCode", organizationCode);
//
//        // verify that the root is in the tree at the least
//        BudgetConstructionPullup bcPullup = (BudgetConstructionPullup) businessObjectService.findByPrimaryKey(BudgetConstructionPullup.class, map);
//        assertTrue(bcPullup.getChartOfAccountsCode().equalsIgnoreCase(chartOfAccountsCode));
//        assertTrue(bcPullup.getOrganizationCode().equalsIgnoreCase(organizationCode));
//        assertTrue(bcPullup.getPrincipalId().equalsIgnoreCase(principalId));
//
//        budgetOrganizationTreeService.cleanPullup(principalId);
//        bcPullup = (BudgetConstructionPullup) businessObjectService.findByPrimaryKey(BudgetConstructionPullup.class, map);
//        assertTrue(bcPullup == null);
//    }

    @ConfigureContext(shouldCommitTransactions = false)
    public void testOJBConfiguration() throws Exception 
    {
        if (!runTests())
            return;

        //Creating tickler
        Tickler tickler = new Tickler();
        tickler.setActive(true);
        tickler.setTypeCode("A");
        tickler.setNextDueDate(new Date(2009,1,1));
        tickler.setDetail("tickler_details1");
        tickler.setEntryDate(new Date(2010,1,1));
        tickler.setFrequencyCode("AA02");
        
        //Obtain Tickler number from Sequence:
        String number  = sequenceAccessorService.getNextAvailableSequenceNumber(EndowConstants.Sequences.END_TICKLER_SEQ).toString();
        tickler.setNumber(number);
        
        //saving Tickler
        businessObjectService.save(tickler);
        
        //Retrieveing and asserting Tickler
        assertEquals("A", businessObjectService.findBySinglePrimaryKey(Tickler.class, tickler.getNumber()).getTypeCode());

        //Creating TicklerType
        TicklerTypeCode tt = new TicklerTypeCode();
        tt.setCode("T2");
        tt.setName("Testing code 1");
        tt.setActive(true);
        
        //Inserting tickler type
        businessObjectService.save(tt);

        //Retrieveing and asserting Tickler Type
        assertEquals("Testing code 1", businessObjectService.findBySinglePrimaryKey(TicklerTypeCode.class, "T2").getName());

      //Creating TicklerKEMID
      TicklerKEMID tk = new TicklerKEMID();
      tk.setNumber(number);
      tk.setKemId("038RT04091");
      tk.setActive(false);
      
      //Inserting tickler type
      businessObjectService.save(tk);

      //Retrieveing and asserting Tickler Type
      LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
      m.put(EndowPropertyConstants.TICKLER_NUMBER,tk.getNumber());
      m.put(EndowPropertyConstants.TICKLER_KEMID,tk.getKemId());

      assertEquals(false,((TicklerKEMID) businessObjectService.findByPrimaryKey(TicklerKEMID.class,m)).isActive()) ;

        //Creating Tickler Security ID
        TicklerSecurity ts = new TicklerSecurity();
        ts.setNumber(number);
        ts.setSecurityId("99STRMTH4");
        ts.setActive(false);
        
        //Inserting tickler type
        businessObjectService.save(ts);

        //Retrieveing and asserting Tickler Type
        LinkedHashMap<String, String> m1 = new LinkedHashMap<String, String>();
        m1.put(EndowPropertyConstants.TICKLER_NUMBER,ts.getNumber());
        m1.put(EndowPropertyConstants.TICKLER_SECURITYID,ts.getSecurityId());

        assertEquals(false,((TicklerSecurity) businessObjectService.findByPrimaryKey(TicklerSecurity.class,m1)).isActive()) ;
  


         //Creating Tickler Principal ID
 
        TicklerRecipientPrincipal trp = new TicklerRecipientPrincipal();
        trp.setNumber(number);
        trp.setPrincipalId("1106901514");
        trp.setActive(false);
        
        //Inserting tickler Principal ID
        businessObjectService.save(trp);

        //Retrieveing and asserting Tickler Principal ID
        LinkedHashMap<String, String> m2 = new LinkedHashMap<String, String>();
        m2.put(EndowPropertyConstants.TICKLER_NUMBER,trp.getNumber());
        m2.put(EndowPropertyConstants.TICKLER_RECIPIENT_PRINCIPALID,trp.getPrincipalId());

        assertEquals(false,((TicklerRecipientPrincipal) businessObjectService.findByPrimaryKey(TicklerRecipientPrincipal.class,m2)).isActive()) ;      


        

        //Creating Tickler Group ID
        TicklerRecipientGroup trg = new TicklerRecipientGroup();
        trg.setNumber(number);
        trg.setGroupId("10");
        trg.setActive(false);
        
        //Inserting tickler type
        businessObjectService.save(trg);

        //Retrieveing and asserting Tickler Group ID
        LinkedHashMap<String, String> m3 = new LinkedHashMap<String, String>();
        m3.put(EndowPropertyConstants.TICKLER_NUMBER,trg.getNumber());
        m3.put(EndowPropertyConstants.TICKLER_RECIPIENT_GROUPID,trg.getGroupId());

        assertEquals(false,((TicklerRecipientGroup) businessObjectService.findByPrimaryKey(TicklerRecipientGroup.class,m3)).isActive()) ;    

        
        
        //Testing collections
        
        //Retrieveing and asserting Tickler
        Tickler t = businessObjectService.findBySinglePrimaryKey(Tickler.class, number);
        
        t.refresh();
        
        assertNotNull(t);
        assertEquals("AA02", t.getFrequency().getCode());
        assertEquals(1, t.getKemIds().size());
        assertEquals(1, t.getSecurities().size());
        assertEquals(1, t.getRecipientPrincipals().size());
        assertEquals(1, t.getRecipientGroups().size());
        //assertEquals("A", businessObjectService.findBySinglePrimaryKey(Tickler.class, "1").getCode());
        
    }
}

