/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.web.renderers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.struts.taglib.html.ErrorsTag;

/**
 * Renders any errors associated with an accounting line group
 */
public class GroupErrorsRenderer implements Renderer {
    private List errorPropertyList;
    private String sectionTitle;
    private List<String> errorsRendered;
    private String errorKeyMatch;
    private int colSpan = -1;
    private ErrorsTag errorTag = new ErrorsTag();

    /**
     * Cleans up the errorPropertyList, the sectionTitle, the errorsRendered (so you'd better read that first),
     * and the ErrorTag used to display the errors
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        errorPropertyList = null;
        sectionTitle = null;
        errorsRendered = null;
        errorKeyMatch = null;
        colSpan = -1;
        
        cleanUpErrorTag();
    }
    
    /**
     * Cleans up the ErrorTag
     */
    protected void cleanUpErrorTag() {
        errorTag.setPageContext(null);
        errorTag.setParent(null);
        errorTag.setProperty(null);
    }

    /**
     * Renders the errors
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        final List<String> matchingErrorKeys = getMatchingErrorKeys(getKeysToMatch());
        
        if (matchingErrorKeys.size() > 0) {
            JspWriter out = pageContext.getOut();
            
            try {
                out.write(buildTableRowAndCellOpening());
                
                out.write(buildErrorTitle());
                
                for (String errorKey : matchingErrorKeys) {
                    errorTag.setPageContext(pageContext);
                    errorTag.setParent(parentTag);
                    errorTag.setProperty(errorKey);
                       
                    errorTag.doStartTag();
                    errorTag.doEndTag();
                        
                    getErrorsRendered().add(errorKey);
                }
                
                out.write(buildTableRowAndCellClosing());
            }
            catch (IOException ioe) {
                throw new JspException("Difficulty while rendering errors for group", ioe);
            }
        }
    }
    
    /**
     * @return the HTML for the table row and cell and div to open the error display
     */
    protected String buildTableRowAndCellOpening() {
        StringBuilder html = new StringBuilder();
        html.append("<tr>");
        html.append("<td colspan=\"");
        html.append(colSpan);
        html.append("\">");
        html.append("<div class=\"error\">");
        return html.toString();
    }
    
    /**
     * @return the HTML for the table row and cell and div which closes the error display 
     */
    protected String buildTableRowAndCellClosing() {
        StringBuilder html = new StringBuilder();
        html.append("</div>");
        html.append("</td>");
        html.append("</tr>");
        return html.toString();
    }
    
    /**
     * Returns a list of all error keys that should be rendered
     * @param keysToMatch the keys that this group will match
     * @return a List of all error keys this group will match
     */
    protected List<String> getMatchingErrorKeys(String[] keysToMatch) {
        List<String> matchingErrorKeys = new ArrayList<String>();
        
        for (Object keyAsObject : errorPropertyList) {
            String key = (String)keyAsObject;
            if (matchesGroup(key, keysToMatch)) {
                matchingErrorKeys.add(key);
            }
        }
        
        return matchingErrorKeys;
    }
    
    /**
     * @return the list of individual keys or wildcard keys that this group will match 
     */
    protected String[] getKeysToMatch() {
        return errorKeyMatch.split(",");
    }
    
    /**
     * Determines if the given error key matches the keyToMatch - either because the two keys are
     * equal, or if the keyToMatch is a wildcard key and would wildcard match the key
     * @param key the error key to match
     * @param keyToMatch one of the error keys this group will display
     * @return true if the keys match, false if not
     */
    protected boolean foundErrorMatch(String key, String keyToMatch) {
        return key.equals(keyToMatch) || (keyToMatch.endsWith("*") && key.startsWith(keyToMatch.replaceAll("\\*", "")));
    }
    
    /**
     * @return the HTML for the title of the list of errors that will be displayed
     */
    protected String buildErrorTitle() {
        return "<strong>Errors found in "+sectionTitle+" section:</strong>";
    }
    
    /**
     * Determines if the given key matches any error key associated with this group
     * @param key the error key that may or may not be displayed here
     * @param keysToMatch the keys that this group will match against
     * @return true if this group can display the given key, false otherwise
     */
    protected boolean matchesGroup(String key, String[] keysToMatch) {
        for (String keyToMatch : keysToMatch) {
            if (foundErrorMatch(key, keyToMatch)) return true;
        }
        return false;
    }

    /**
     * Gets the errorPropertyList attribute. 
     * @return Returns the errorPropertyList.
     */
    public List getErrorPropertyList() {
        return errorPropertyList;
    }

    /**
     * Sets the errorPropertyList attribute value.
     * @param errorPropertyList The errorPropertyList to set.
     */
    public void setErrorPropertyList(List errorPropertyList) {
        this.errorPropertyList = errorPropertyList;
    }

    /**
     * Gets the sectionTitle attribute. 
     * @return Returns the sectionTitle.
     */
    public String getSectionTitle() {
        return sectionTitle;
    }

    /**
     * Sets the sectionTitle attribute value.
     * @param sectionTitle The sectionTitle to set.
     */
    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    /**
     * Gets the errorsRendered attribute. 
     * @return Returns the errorsRendered.
     */
    public List<String> getErrorsRendered() {
        if (errorsRendered == null) {
            errorsRendered = new ArrayList<String>();
        }
        return errorsRendered;
    }

    /**
     * Gets the errorKeyMatch attribute. 
     * @return Returns the errorKeyMatch.
     */
    public String getErrorKeyMatch() {
        return errorKeyMatch;
    }

    /**
     * Sets the errorKeyMatch attribute value.
     * @param errorKeyMatch The errorKeyMatch to set.
     */
    public void setErrorKeyMatch(String errorKeyMatch) {
        this.errorKeyMatch = errorKeyMatch;
    }

    /**
     * Gets the colSpan attribute. 
     * @return Returns the colSpan.
     */
    public int getColSpan() {
        return colSpan;
    }

    /**
     * Sets the colSpan attribute value.
     * @param colSpan The colSpan to set.
     */
    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

}
