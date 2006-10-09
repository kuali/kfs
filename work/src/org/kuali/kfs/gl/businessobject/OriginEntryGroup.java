/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.bo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

public class OriginEntryGroup extends BusinessObjectBase {

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

    // This does not normally get populated.  It only gets populated if
    // getAllOriginEntryGroup() is called
    private Integer rows = new Integer(0);

    private OriginEntrySource source;

    /**
     * 
     */
    public OriginEntryGroup() {
        super();
    }

    protected LinkedHashMap toStringMapper() {
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
}
