/*
 * Copyright 2011 The Kuali Foundation
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

package org.kuali.kfs.coa.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.springframework.beans.BeanUtils;

/**
 * IndirectCostRecoveryAccount for A21SubAccount
 */
public class PriorYearIndirectCostRecoveryAccount extends IndirectCostRecoveryAccount {
    private static Logger LOG = Logger.getLogger(PriorYearIndirectCostRecoveryAccount.class);

    private Integer priorYearIndirectCostRecoveryAccountGeneratedIdentifier;
    
    /**
     * Default constructor.
     */
    public PriorYearIndirectCostRecoveryAccount() {
    }
    
    public PriorYearIndirectCostRecoveryAccount(IndirectCostRecoveryAccount icr) {
        BeanUtils.copyProperties(this, icr);
    }
    
    /**
     * static instantiate an A21ICRAccount from an ICRAccount
     *
     * @param icrAccount
     * @return
     */
    public static PriorYearIndirectCostRecoveryAccount copyICRAccount(IndirectCostRecoveryAccount icrAccount) {
        return (PriorYearIndirectCostRecoveryAccount)copyICRAccount(icrAccount); 
    }

    public Integer getPriorYearIndirectCostRecoveryAccountGeneratedIdentifier() {
        return priorYearIndirectCostRecoveryAccountGeneratedIdentifier;
    }

    public void setPriorYearIndirectCostRecoveryAccountGeneratedIdentifier(Integer priorYearIndirectCostRecoveryAccountGeneratedIdentifier) {
        this.priorYearIndirectCostRecoveryAccountGeneratedIdentifier = priorYearIndirectCostRecoveryAccountGeneratedIdentifier;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.priorYearIndirectCostRecoveryAccountGeneratedIdentifier != null) {
            m.put("priorYearIndirectCostRecoveryAccountGeneratedIdentifier", this.priorYearIndirectCostRecoveryAccountGeneratedIdentifier.toString());
        }
        return m;
    }

}
