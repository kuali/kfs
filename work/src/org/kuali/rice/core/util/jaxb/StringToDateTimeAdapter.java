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

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;

/**
 * An XML Adapter that relies on the DateTimeService to marshal and unmarshal datetime values in String form.
 * Converts Strings to java.util.Date instances and vice versa.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StringToDateTimeAdapter extends XmlAdapter<String,Date> {

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Date unmarshal(String v) throws Exception {
        return (v != null) ? CoreApiServiceLocator.getDateTimeService().convertToDateTime(StringUtils.trim(v)) : null;
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(Date v) throws Exception {
        return (v != null) ? CoreApiServiceLocator.getDateTimeService().toDateTimeString(v) : null;
    }

}
