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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.dataaccess.AccountingDocumentRelationshipDao;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AccountingDocumentRelationshipServiceImpl implements AccountingDocumentRelationshipService {

    protected AccountingDocumentRelationshipDao accountingDocumentRelationshipDao;
    protected BusinessObjectService businessObjectService;
    protected IdentityService identityService;

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
                addRelationship(adr, allRelatedDocumentNumbers, documentNumber);
            }
        }

        return allRelatedDocumentNumbers;
    }

    /**
     * Adds the relationship to a Set of document numbers, skipping the given document number
     * @param adr the relationship with document numbers to add
     * @param relatedDocumentNumbers the Set of Document numbers to add the relationship to
     * @param documentNumber the document number to add the other side of the relationship to
     */
    protected void addRelationship(AccountingDocumentRelationship adr, Set<String> relatedDocumentNumbers, String documentNumber) {
        if (adr.getDocumentNumber().equals(documentNumber)) {
            // child docs
            relatedDocumentNumbers.add(adr.getRelDocumentNumber());
        }
        else {
            // parent docs
            relatedDocumentNumbers.add(adr.getDocumentNumber());
        }
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
    public void save(AccountingDocumentRelationship accountingDocumentRelationship) {
        if (accountingDocumentRelationship.getPrincipalId() == null) {
            Principal person = identityService.getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
            accountingDocumentRelationship.setPrincipalId(person.getPrincipalId());
        }

        accountingDocumentRelationshipDao.save(accountingDocumentRelationship);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService#delete(java.util.List)
     */
    @Override
    public void delete(List<AccountingDocumentRelationship> accountingDocumentRelationships) {
        for (AccountingDocumentRelationship adr : accountingDocumentRelationships) {
            delete(adr);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService#huntForRelatedDocumentNumbersWithADocumentType(java.lang.String, java.lang.String)
     */
    @Override
    public Set<String> huntForRelatedDocumentNumbersWithDocumentType(String documentNumber, String documentType) {
        List<AccountingDocumentRelationship> adrList = accountingDocumentRelationshipDao.findAccountingDocumentRelationshipByDocumentNumber(documentNumber);

        Set<String> docNumbers = new HashSet<String>();
        if (adrList != null && adrList != null) {
            for (AccountingDocumentRelationship adr: adrList) {
                if (!StringUtils.isBlank(adr.getDescription()) && adr.getDescription().matches(".*"+documentType+".*")) {
                    addRelationship(adr, docNumbers, documentNumber);
                }
            }
        }
        return docNumbers;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService#delete(org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship)
     */
    @Override
    public void delete(AccountingDocumentRelationship accountingDocumentRelationship) {
        accountingDocumentRelationshipDao.delete(accountingDocumentRelationship);
    }

    public AccountingDocumentRelationshipDao getAccountingDocumentRelationshipDao() {
        return accountingDocumentRelationshipDao;
    }

    public void setAccountingDocumentRelationshipDao(AccountingDocumentRelationshipDao accountingDocumentRelationshipDao) {
        this.accountingDocumentRelationshipDao = accountingDocumentRelationshipDao;
    }

    /**
     * Counts the number of accounting document relationships where the given document number is the related document number; returns true
     * if that count is greater than 0
     * @see org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService#isDocumentSomebodysChild(java.lang.String)
     */
    @Override
    public boolean isDocumentSomebodysChild(String documentNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(TemPropertyConstants.TRVL_RELATED_DOCUMENT_NUM, documentNumber);
        final int count = getBusinessObjectService().countMatching(AccountingDocumentRelationship.class, fieldValues);
        return count > 0;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    @Override
    public List<AccountingDocumentRelationship> find(AccountingDocumentRelationship adr) {
        return accountingDocumentRelationshipDao.findAccountingDocumentRelationship(adr);
    }

}
