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
package org.kuali.module.kra.document;

import java.util.LinkedHashMap;

import org.kuali.core.document.DocumentBase;
import org.kuali.core.util.SpringServiceLocator;

/**
 * Research Administration Document Base
 * 
 * @author KRA (era_team@indiana.edu)
 */
public abstract class ResearchDocumentBase extends DocumentBase implements ResearchDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResearchDocumentBase.class);

    protected Integer postingYear;
    /**
     * Sets up the collection instances and common document attributes.
     */
    public ResearchDocumentBase() {
        super();
        this.postingYear = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();
    }

    /**
     * 
     * @return
     */
    public Integer getPostingYear() {
        return this.postingYear;
    }

    /**
     * 
     * @param postingYear
     */
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("postingYear", getPostingYear());
        m.put("versionNumber", getVersionNumber());
        m.put("level", getDocumentHeader().getWorkflowDocument().getDocRouteLevel());
        m.put("comp", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isCompletionRequested()));
        m.put("app", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isApprovalRequested()));
        m.put("ack", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isAcknowledgeRequested()));
        m.put("fyi", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isFYIRequested()));

        return m;
    }
}

