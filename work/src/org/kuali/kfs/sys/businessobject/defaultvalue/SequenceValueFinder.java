/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject.defaultvalue;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Abstract ValueFinder which looks up a value based on a sequence
 */
public abstract class SequenceValueFinder implements ValueFinder {

    @Override
    public String getValue() {
        return getLongValue().toString();
    }

    /**
     * Gets the next sequence number as a long.
     *
     * @return
     */
    public Long getLongValue() {
        // no constant because this is the only place the sequence name is used
        SequenceAccessorService sas = SpringContext.getBean(SequenceAccessorService.class);
        return sas.getNextAvailableSequenceNumber(getSequenceName(), getAssociatedClass());
    }

    /**
     * @return the name of the sequence which should be used to find the next value
     */
    public abstract String getSequenceName();

    /**
     * @return the class most closely associated with this sequence (so that the DAO knows which DB to look in to find the sequence)
     */
    public abstract Class<? extends PersistableBusinessObject> getAssociatedClass();

}
