/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ClassOfService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class ClassOfServiceValuesFinder extends KeyValuesBase {

    private String expenseTypeCode;

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        Collection<ClassOfService> bos = null;
        if(ObjectUtils.isNotNull(expenseTypeCode)){
            Map<String, String> searchMap = new HashMap<String, String>();
            searchMap.put(TemPropertyConstants.EXPENSE_TYPE_CODE, expenseTypeCode);
            bos = SpringContext.getBean(BusinessObjectService.class).findMatching(ClassOfService.class, searchMap);
        }
        else{
            bos = SpringContext.getBean(BusinessObjectService.class).findAll(ClassOfService.class);
        }

        keyValues.add(new ConcreteKeyValue("", ""));
        for (ClassOfService typ : bos) {
            keyValues.add(new ConcreteKeyValue(typ.getCode(), typ.getClassOfServiceName()));
        }

        return keyValues;
    }

    public void setExpenseTypeCode(String expenseTypeCode){
        this.expenseTypeCode = expenseTypeCode;
    }

    public String getExpenseTypeCode(){
        return this.expenseTypeCode;
    }

}
