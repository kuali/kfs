/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerNote;
import org.kuali.kfs.module.ar.document.service.CustomerNoteService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation class for CustomerNoteService.
 */
public class CustomerNoteServiceImpl implements CustomerNoteService {

    private SequenceAccessorService sequenceAccessorService;

    protected static final String CUST_NTE_ID_SEQ = "CUST_NTE_SEQ";

    /**
     * Gets the sequenceAccessorService attribute.
     * 
     * @return Returns sequenceAccessorService attribute.
     */
    public SequenceAccessorService getSequenceAccessorService() {
        return sequenceAccessorService;
    }

    /**
     * Sets the sequenceAccessorService attribute.
     * 
     * @param sequenceAccessorService The sequenceAccessorService attribute to set.
     */
    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerNoteService#getByPrimaryKey(java.lang.String, java.lang.Integer)
     */
    @Override
    public CustomerNote getByPrimaryKey(String customerNumber, Integer customerNoteIdentifier) {
        CustomerNote customerNote = null;
        if (StringUtils.isNotBlank(customerNumber) && ObjectUtils.isNotNull(customerNoteIdentifier)) {
            Map criteria = new HashMap();
            criteria.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
            criteria.put(ArPropertyConstants.CustomerFields.CUSTOMER_NOTE_IDENTIFIER, customerNoteIdentifier);

            customerNote = (CustomerNote) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CustomerNote.class, criteria);
        }
        return customerNote;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerNoteService#getNextCustomerNoteIdentifier()
     */
    @Override
    public Integer getNextCustomerNoteIdentifier() {
        Long nextId = sequenceAccessorService.getNextAvailableSequenceNumber(CUST_NTE_ID_SEQ, CustomerNote.class);
        return nextId.intValue();
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerNoteService#customerNoteExists(java.lang.String, java.lang.Integer)
     */
    @Override
    public boolean customerNoteExists(String customerNumber, Integer customerNoteIdentifier) {
        return ObjectUtils.isNotNull(getByPrimaryKey(customerNumber, customerNoteIdentifier));
    }

}
