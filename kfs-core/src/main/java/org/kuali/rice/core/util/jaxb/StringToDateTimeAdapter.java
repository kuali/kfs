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
