/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.ParseException;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

// Created for Research Participant Upload
public class TimestampPropertyEditor extends PropertyEditorSupport {

    /**
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            Timestamp time =  SpringContext.getBean(DateTimeService.class).convertToSqlTimestamp(text);
            setValue(time);
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Could not parse timestamp: " + ex.getMessage(), ex);
        }
    }

    /**
     * Format the Timestamp as String, using the DateTimeService.
     */
    @Override
    public String getAsText() {
        Timestamp value = (Timestamp) getValue();
        return (value != null ? SpringContext.getBean(DateTimeService.class).toDateTimeString(value) : "");
    }
}