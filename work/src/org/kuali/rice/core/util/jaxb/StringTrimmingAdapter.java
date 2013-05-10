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
package org.kuali.rice.core.util.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.StringUtils;

/**
 * An XML adapter that simply performs a null-safe trim on the value to be marshalled or unmarshalled.
 * 
 * <p>Only use this adapter when it is necessary for the remaining whitespace-related characters to
 * remain as-is.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StringTrimmingAdapter extends XmlAdapter<String,String> {

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public String unmarshal(String v) throws Exception {
        return StringUtils.trim(v);
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(String v) throws Exception {
        return StringUtils.trim(v);
    }

}
