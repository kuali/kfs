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
