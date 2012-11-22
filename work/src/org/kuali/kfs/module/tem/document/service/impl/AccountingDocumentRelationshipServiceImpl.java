/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.dataaccess.AccountingDocumentRelationshipDao;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.springframework.transaction.annotation.Transactional;

public class AccountingDocumentRelationshipServiceImpl implements AccountingDocumentRelationshipService {

    private AccountingDocumentRelationshipDao accountingDocumentRelationshipDao;
    private PersonService personService;

    private static final Logger LOG = Logger.getLogger(AccountingDocumentRelationshipServiceImpl.class);

    /**
     * @see org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService#getRelatedDocumentNumbers(java.lang.String)
     */
    @Override
    public Set<String> getRelatedDocumentNumbers(String documentNumber) {
        List<AccountingDocumentRelationship> adrList = accountingDocumentRelationshipDao.findAccountingDocumentRelationshipByDocumentNumber(documentNumber);

        Set<String> allRelatedDocumentNumbers = new HashSet<String>();

        if (adrList != null) {
            for (AccountingDocumentRelationship adr : adrList) {
                if (adr.getDocumentNumber().equals(documentNumber)) {
                    // child docs
                    allRelatedDocumentNumbers.add(adr.getRelDocumentNumber());
                }
                else {
                    // parent docs
                    allRelatedDocumentNumbers.add(adr.getDocumentNumber());
                }
            }
        }

        return allRelatedDocumentNumbers;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService#getAllRelatedDocumentNumbers(java.lang.String)
     */
    @Override
    public Set<String> getAllRelatedDocumentNumbers(String documentNumber) {
        Set<String> allRelatedDocumentNumbers = new HashSet<String>();
        
        // get root document number
        String rootDocumentNumber = getRootDocumentNumber(documentNumber);
        rootDocumentNumber = rootDocumentNumber != null ? rootDocumentNumber : documentNumber;

        if (rootDocumentNumber != null) {
            allRelatedDocumentNumbers.add(rootDocumentNumber);

            // get all children document numbers
            allRelatedDocumentNumbers = getAllRelatedChildrenDocumentNumbers(allRelatedDocumentNumbers);

            // add the root document number if the initial lookup document number != root document number
            if (!rootDocumentNumber.equals(documentNumber)) {
                allRelatedDocumentNumbers.add(rootDocumentNumber);
            }

            // remove the document number used for initial lookup.
            allRelatedDocumentNumbers.remove(documentNumber);

            return allRelatedDocumentNumbers;
        }

        return allRelatedDocumentNumbers;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService#getRootDocumentNumber(java.lang.String)
     */
    @Override
    public String getRootDocumentNumber(String documentNumber) {
        List<AccountingDocumentRelationship> adrList = accountingDocumentRelationshipDao.findAccountingDocumentRelationship(new AccountingDocumentRelationship(null, documentNumber));

        if (!adrList.isEmpty() && adrList.size() > 1) {
            LOG.warn("Document has 2 parents. This should not happen.");
        }

        return adrList.isEmpty() ? documentNumber : getRootDocumentNumber(adrList.get(0).getDocumentNumber());
    }

    /**
     * This method finds all related children document numbers
     * 
     * @param documentNumbers
     * @return
     */
    private Set<String> getAllRelatedChildrenDocumentNumbers(Set<String> documentNumbers) {
        Set<String> allRelatedDocumentNumbers = new HashSet<String>();
        
        for (String documentNumber : documentNumbers) {
            List<AccountingDocumentRelationship> adrList = accountingDocumentRelationshipDao.findAccountingDocumentRelationship(new AccountingDocumentRelationship(documentNumber, null));
          
            for (AccountingDocumentRelationship adr : adrList) {
                allRelatedDocumentNumbers.add(adr.getRelDocumentNumber());
            }

            allRelatedDocumentNumbers.addAll(getAllRelatedChildrenDocumentNumbers(allRelatedDocumentNumbers));
        }

        return allRelatedDocumentNumbers.isEmpty() ? documentNumbers : allRelatedDocumentNumbers;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService#save(java.util.List)
     */
    @Override
    @Transactional
    public void save(List<AccountingDocumentRelationship> accountingDocumentRelationships) {
        for (AccountingDocumentRelationship adr : accountingDocumentRelationships) {
            save(adr);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService#save(org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship)
     *      If the principalId is not set, it will be defaulted to KualiUser.SYSTEM_USER's principalId.
     */
    @Override
    @Transactional
    public void save(AccountingDocumentRelationship accountingDocumentRelationship) {
        if (accountingDocumentRelationship.getPrincipalId() == null) {
            Person person = personService.getPersonByPrincipalName(KFSConstants.SYSTEM_USER);
            accountingDocumentRelationship.setPrincipalId(person.getPrincipalId());
        }

        accountingDocumentRelationshipDao.save(accountingDocumentRelationship);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService#delete(java.util.List)
     */
    @Override
    @Transactional
    public void delete(List<AccountingDocumentRelationship> accountingDocumentRelationships) {
        for (AccountingDocumentRelationship adr : accountingDocumentRelationships) {
            delete(adr);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService#delete(org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship)
     */
    @Override
    @Transactional
    public void delete(AccountingDocumentRelationship accountingDocumentRelationship) {
        accountingDocumentRelationshipDao.delete(accountingDocumentRelationship);
    }

    public AccountingDocumentRelationshipDao getAccountingDocumentRelationshipDao() {
        return accountingDocumentRelationshipDao;
    }

    public void setAccountingDocumentRelationshipDao(AccountingDocumentRelationshipDao accountingDocumentRelationshipDao) {
        this.accountingDocumentRelationshipDao = accountingDocumentRelationshipDao;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public List<AccountingDocumentRelationship> find(AccountingDocumentRelationship adr) {
        return accountingDocumentRelationshipDao.findAccountingDocumentRelationship(adr);
    }

}
