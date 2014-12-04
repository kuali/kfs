/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
