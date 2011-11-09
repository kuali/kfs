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
package org.kuali.kfs.sys.dataaccess.impl;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.QueryCustomizerDefaultImpl;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.krad.util.ObjectUtils;

public class OjbQueryCustomizer extends QueryCustomizerDefaultImpl {
    // used to AND in additional criteria on a collection
    protected static final String FIELD_PREFIX = "parent.";

    @Override
    public Query customizeQuery(Object arg0, PersistenceBroker arg1, CollectionDescriptor arg2, QueryByCriteria arg3) {
        // unfortunately OJB's default implementation has no getter for the map they construct
        // by accessing this map, we can provide a more generic interface by looping through any attributes
        // so, use reflection to get at the attribute anyway
        Field field = null;
        try {
            field = this.getClass().getSuperclass().getDeclaredField("m_attributeList");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        Map<String, String> m_attributeList = null;
        try {
            m_attributeList = (Map) field.get(this);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // now, do what we wanted to do to start with if we could've just gotten m_attributeList easily
        Criteria criteria = arg3.getCriteria();
        for (String key : m_attributeList.keySet()) {
            // if beginning with FIELD_PREFIX is too hacky, or more flexibility is needed, another query customizer class can be
            // made,
            // and this method can be renamed to take a parameter to specify which we want to do
            // (and the customizeQuery method here made to call the new method with the parameter).
            // However, making another class would mean you couldn't intermix constants and field values,
            // since OJB won't use have multiple query-customizers per collection-descriptor.
            if (this.getAttribute(key).startsWith(FIELD_PREFIX)) {
                criteria.addEqualTo(key, ObjectUtils.getPropertyValue(arg0, this.getAttribute(key).substring(FIELD_PREFIX.length())));
            }
            else {
                criteria.addEqualTo(key, this.getAttribute(key));
            }
        }
        arg3.setCriteria(criteria);
        return arg3;
    }
}
