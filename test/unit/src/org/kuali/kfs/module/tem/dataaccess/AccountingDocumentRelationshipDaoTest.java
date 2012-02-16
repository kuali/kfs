/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.module.tem.dataaccess;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class AccountingDocumentRelationshipDaoTest extends KualiTestBase {

    private AccountingDocumentRelationshipDao dao;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = SpringContext.getBean(AccountingDocumentRelationshipDao.class);
        dao.save(new AccountingDocumentRelationship("test1","test2"));
        dao.save(new AccountingDocumentRelationship("test2","test3"));
        dao.save(new AccountingDocumentRelationship("test2","test4"));
    }
    
    /**
     * 
     * This method tests {@link AccountingDocumentRelationshipDao#save(AccountingDocumentRelationship)}.
     */
    @Test
    public void testSave(){
        //test save with incomplete AccountingDocumentRelationship
        AccountingDocumentRelationship adr = new AccountingDocumentRelationship("test1",null);
        dao.save(adr);       
        assertNull(adr.getId());
        
        //test save with an existing AccountingDocumentRelationship
        adr = new AccountingDocumentRelationship("test1","test2");
        dao.save(adr);       
        assertNotNull(adr.getId());     
        
        //test save with a valid AccountingDocumentRelationship
        adr = new AccountingDocumentRelationship("test4","test5");
        dao.save(adr);
        assertNotNull(adr.getId());         
    }      
    
    /**
     * 
     * This method tests {@link AccountingDocumentRelationshipDao#findAccountingDocumentRelationshipByDocumentNumber(String, String)}.
     */
    @Test
    public void testFindAccountingDocumentRelationshipByDocumentNumber(){
        //test find using bad documentNumber
        List<AccountingDocumentRelationship> adrList = dao.findAccountingDocumentRelationshipByDocumentNumber(AccountingDocumentRelationship.DOC_NBR, "-1");
        assertTrue(adrList.isEmpty());
        
        //test find using existing documentNumber
        adrList = dao.findAccountingDocumentRelationshipByDocumentNumber(AccountingDocumentRelationship.DOC_NBR, "test1");
        assertTrue(!adrList.isEmpty());      
    }
    
    /**
     * 
     * This method tests {@link AccountingDocumentRelationshipDao#findAccountingDocumentRelationship(AccountingDocumentRelationship)}.
     */
    @Test
    public void testFindAccountingDocumentRelationship(){
        //test find using documentNumber
        AccountingDocumentRelationship adr = new AccountingDocumentRelationship("test2",null);       
        List<AccountingDocumentRelationship> adrList = dao.findAccountingDocumentRelationship(adr);
        assertTrue(adrList.size() == 2);
        
        //test find using documentNumber and relDocumentNumber
        adr.setRelDocumentNumber("test3");
        adrList = dao.findAccountingDocumentRelationship(adr);
        assertTrue(adrList.size() == 1);        
    }    
}
