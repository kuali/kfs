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
package org.kuali.kfs.module.cg.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.service.AwardService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation of the Award service.
 */
public class AwardServiceImpl implements AwardService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cg.service.AwardService#getByPrimaryId(String)
     */
    @Override
    public Award getByPrimaryId(Long proposalNumber) {
        return businessObjectService.findByPrimaryKey(Award.class, mapPrimaryKeys(proposalNumber));
    }

    protected Map<String, Object> mapPrimaryKeys(Long proposalNumber) {
        Map<String, Object> primaryKeys = new HashMap();
        primaryKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        return primaryKeys;
    }

    /**
     * Sets the BusinessObjectService. Provides Spring compatibility.
     *
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * This method checks the Contract Control account set for Award Account based on award's invoicing option.
     *
     * @return
     */
    @Override
    public List<String> hasValidContractControlAccounts(Award award) {


        List<String> errorString = new ArrayList<String>();
        boolean isValid = true;
        int accountNum = award.getActiveAwardAccounts().size();
        // To check if invoicing options exist on the award
        if (ObjectUtils.isNotNull(award.getInvoicingOptions())) {

            // To check if the award account is associated with a contract control account.
            for (AwardAccount awardAccount : award.getAwardAccounts()) {
                if (awardAccount.isActive()) {
                    if (ObjectUtils.isNull(awardAccount.getAccount().getContractControlAccount())) {
                        isValid = false;
                        break;
                    }
                }
            }

            // if the Invoicing option is "By Contract Control Account" and there are no contract control accounts for one / all
            // award accounts, then throw error.
            if (award.getInvoicingOptions().equalsIgnoreCase(CGPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT)) {
                if (!isValid) {
                    errorString.add(CGKeyConstants.AwardConstants.ERROR_NO_CTRL_ACCT);
                    errorString.add(CGPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT);
                }
            }

            // if the Invoicing option is "By Award" and there are no contract control accounts for one / all award accounts, then
            // throw error.
            else if (award.getInvoicingOptions().equalsIgnoreCase(CGPropertyConstants.INV_AWARD)) {
                if (!isValid) {
                    errorString.add(CGKeyConstants.AwardConstants.ERROR_NO_CTRL_ACCT);
                    errorString.add(CGPropertyConstants.INV_AWARD);
                }
                else {
                    if (accountNum != 1) {
                        Account tmpAcct1, tmpAcct2;

                        Object[] awardAccounts = award.getActiveAwardAccounts().toArray();
                        for (int i = 0; i < awardAccounts.length - 1; i++) {
                            tmpAcct1 = ((AwardAccount) awardAccounts[i]).getAccount().getContractControlAccount();
                            tmpAcct2 = ((AwardAccount) awardAccounts[i + 1]).getAccount().getContractControlAccount();
                            // if the Invoicing option is "By Award" and there are more than one contract control account assigned
                            // for the award, then throw error.
                            if (ObjectUtils.isNull(tmpAcct1) || ObjectUtils.isNull(tmpAcct2) || !areTheSameAccounts(tmpAcct1, tmpAcct2)) {
                                errorString.add(CGKeyConstants.AwardConstants.ERROR_MULTIPLE_CTRL_ACCT);
                                errorString.add(CGPropertyConstants.INV_AWARD);
                            }
                        }
                    }
                }
            }
        }
        return errorString;
    }

    /**
     * This method validate if two accounts present the same account by comparing their "account number" and
     * "chart of account code",which are primary key.
     *
     * @param obj1
     * @param obj2
     * @return True if these two accounts are the same
     */
    protected boolean areTheSameAccounts(Account obj1, Account obj2) {
        boolean isEqual = false;

        if (obj1 != null && obj2 != null) {
            if (StringUtils.equals(obj1.getChartOfAccountsCode(), obj2.getChartOfAccountsCode())) {
                if (StringUtils.equals(obj1.getAccountNumber(), obj2.getAccountNumber())) {
                    isEqual = true;
                }
            }
        }

        return isEqual;
    }

}