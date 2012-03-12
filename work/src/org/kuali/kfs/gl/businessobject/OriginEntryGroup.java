/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject;

import java.sql.Date;
import java.util.Comparator;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class OriginEntryGroup extends PersistableBusinessObjectBase {

    private static final String VALID_STRING = "Valid-";
    private static final String INVALID_STRING = "Error-";
    private static final String PROCESSED_STRING = "Process-";
    private static final String NOT_PROCESSED_STRING = "Don't Process-";
    private static final String SCRUB_STRING = "Scrub";
    private static final String NO_SCRUB_STRING = "Don't Scrub";

    private Integer id;
    private Date date;
    private String sourceCode;
    private Boolean valid;
    private Boolean process;
    private Boolean scrub;

    // This does not normally get populated. It only gets populated if
    // getAllOriginEntryGroup() is called
    private Integer rows = new Integer(0);

    private OriginEntrySource source;

    /**
     * 
     */
    public OriginEntryGroup() {
        super();
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("id", id);
        return map;
    }

    public String getName() {
        StringBuffer sb = new StringBuffer(this.getSourceCode());
        sb.append(" ");
        sb.append(source.getName());
        sb.append(" (");
        sb.append(rows);
        sb.append(") ");
        sb.append(this.getId());
        sb.append(" ");
        sb.append(this.getDate().toString());
        sb.append(" ");
        sb.append(valid ? VALID_STRING : INVALID_STRING);
        sb.append(process ? PROCESSED_STRING : NOT_PROCESSED_STRING);
        sb.append(scrub ? SCRUB_STRING : NO_SCRUB_STRING);
        return sb.toString();
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public OriginEntrySource getSource() {
        return source;
    }

    public void setSource(OriginEntrySource oes) {
        source = oes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getProcess() {
        return process;
    }

    public void setProcess(Boolean process) {
        this.process = process;
    }

    public Boolean getScrub() {
        return scrub;
    }

    public void setScrub(Boolean scrub) {
        this.scrub = scrub;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
    
    /**
     * An implementation of Comparator which compares origin entry groups by their source attribute
     */
    public static class GroupTypeComparator implements Comparator {
        /**
         * Constructs a GroupTypeComparator instance
         */
        public GroupTypeComparator() {
        }

        /**
         * Compares origin entry groups based on the alphabeticality of their source attributes
         * 
         * @param c1 the first origin entry group to compare
         * @param c2 the origin entry group compared to
         * @return a negative if c1's source is less than c2's, 0 if they are equal, a positive if c1's source is greater than c2's
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object c1, Object c2) {

            OriginEntryGroup oeg1 = (OriginEntryGroup) c1;
            OriginEntryGroup oeg2 = (OriginEntryGroup) c2;

            String sort1 = oeg1.getSourceCode();
            String sort2 = oeg2.getSourceCode();

            int c = sort1.compareTo(sort2);
            if (c != 0) {
                return c;
            }
            return oeg1.getId().compareTo(oeg2.getId());
        }
    }

}
