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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.ojb.broker.accesslayer.QueryCustomizerDefaultImpl;
/**
 * 
 * Contains methods of use to other QueryCustomizers
 */
public abstract class KualiQueryCustomizerDefaultImpl extends QueryCustomizerDefaultImpl {
    /**
     * exposes the list of attributes specified in the ojb file.  This is necessary since
     * the super class does not expose this.
     * @return a list of attributes
     */
    public Map<String, String> getAttributes() {
        // this is necessary since the attributes are not exposed as a list by default
        Field field = null;
        try {
            field = KualiQueryCustomizerDefaultImpl.class.getSuperclass().getDeclaredField("m_attributeList");
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
        return m_attributeList;
    }
}
