/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.labor.bo;

import org.kuali.core.bo.user.PersonPayrollId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.dao.LaborDao;

public class AccountStatusCurrentFunds extends LedgerBalance {

    private String personName;
    private KualiDecimal ytdActualAmount;
    private KualiDecimal outstandingEncum;
    private LaborDao laborDao;
    
    
    /**
     * Constructs an AccountStatusCurrentFunds.java.
     */
    public AccountStatusCurrentFunds() {
        super();
        setMonth1Amount(KualiDecimal.ZERO);
        this.setOutstandingEncum(KualiDecimal.ZERO);
    }

    /**
     * 
     * This method returns the person name
     * @return
     */
    public String getPersonName() {
        UserId empl = new PersonPayrollId(getEmplid());
        UniversalUser universalUser = null;
        
        try{
            universalUser = SpringContext.getBean(UniversalUserService.class, "universalUserService").getUniversalUser(empl);
        }catch(UserNotFoundException e){
            return LaborConstants.BalanceInquiries.UnknownPersonName;
        }

        return universalUser.getPersonName();
    }        
    
    /**
     * 
     * This method set thes persons name
     * @param personName
     */
    
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    /**
     * 
     * This method returns an outstanding encumberance value 
     * @return
     */
    public KualiDecimal getOutstandingEncum() {
        return outstandingEncum;
    }

    /**
     * 
     * This method sets an outstanding encumberance value 
     * @param outstandingEncum
     */
    public void setOutstandingEncum(KualiDecimal outstandingEncum) {
        this.outstandingEncum = outstandingEncum;
    }

}
