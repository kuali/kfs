/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web.renderers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.struts.Globals;
import org.apache.struts.taglib.html.ErrorsTag;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Renders any errors associated with an accounting line group
 */
public class GroupErrorsRenderer implements Renderer {
    private List<String> errorsRendered;
    private List<String> warningsRendered;
    private List<String> infoRendered;
    private String errorKeyMatch;
    private int colSpan = -1;
    private ErrorsTag errorTag = new ErrorsTag();

    /**
     * Cleans up the errorPropertyList, the sectionTitle, the errorsRendered (so you'd better read that first),
     * and the ErrorTag used to display the errors
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        errorsRendered = null;
        warningsRendered = null;
        infoRendered = null;
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
     * Renders the errors, warnings, and messages for this page
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        renderMessages(pageContext, parentTag, KFSKeyConstants.MESSAGE_ACCOUNTING_LINES_ERROR_SECTION_TITLE, getErrorPropertyList(pageContext), "errormark.gif", "error", getErrorsRendered(), Globals.ERROR_KEY);
        renderMessages(pageContext, parentTag, KFSKeyConstants.MESSAGE_ACCOUNTING_LINES_WARNING_SECTION_TITLE, getWarningPropertyList(pageContext), "warning.png", "warning", getWarningsRendered(), "WarningActionMessages");
        renderMessages(pageContext, parentTag, KFSKeyConstants.MESSAGE_ACCOUNTING_LINES_INFORMATION_SECTION_TITLE, getInfoPropertyList(pageContext), "info.png", "info", getInfoRendered(), "InfoActionMessages");
    }
    
    /**
     * Renders a group of messages
     * @param pageContext the page context to render to
     * @param parentTag the name of the parent tag requesting this rendering
     * @param titleConstant the Key Constant to text for the title
     * @param propertyList the list of properties to display
     * @param sectionMarkGraphicName the file name of the mark graphic to display
     * @param sectionGraphicAlt the wording to be used in the "alt" section of the mark graphic
     * @throws JspException thrown if rendering cannot be successfully completed
     */
    protected void renderMessages(PageContext pageContext, Tag parentTag, String titleConstant, List propertyList, String sectionMarkGraphicName, String sectionGraphicAlt, List<String> keysRendered, String requestScopeBeanNameContainingMessages ) throws JspException {
        JspWriter out = pageContext.getOut();
        
        try {
            final List<String> matchingKeys = getMatchingKeys(propertyList, getKeysToMatch());
            if (matchingKeys.size() > 0) {
                out.write(buildTableRowAndCellOpening());
                out.write(buildSectionTitle(titleConstant, sectionMarkGraphicName, sectionGraphicAlt));
            }
            
            for (String matchingKey : matchingKeys) {
                out.write(buildKeyComment(matchingKey, sectionGraphicAlt));
                if (!keysRendered.contains(matchingKey)) {
                    errorTag.setPageContext(pageContext);
                    errorTag.setParent(parentTag);
                    errorTag.setProperty(matchingKey);
                    errorTag.setName(requestScopeBeanNameContainingMessages);
                   
                    errorTag.doStartTag();
                    errorTag.doEndTag();
                    
                    keysRendered.add(matchingKey);
                }
            }
            
            if (matchingKeys.size() > 0) {
                out.write(buildTableRowAndCellClosing());
            }
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty while rendering errors for group", ioe);
        }
    }
    
    /**
     * Builds the HTML String for a section title
     * @param titleConstant the Key Constant to find the text for the title
     * @param sectionMarkGraphicName the name of the graphic file to use
     * @param sectionGraphicAlt the alt for the graphic
     * @return the String to output as HTML for the section title
     */
    protected String buildSectionTitle(String titleConstant, String sectionMarkGraphicName, String sectionGraphicAlt) {
        final ConfigurationService configurationService = SpringContext.getBean(ConfigurationService.class);
        final String titleMessage = configurationService.getPropertyValueAsString(titleConstant);
        final String riceImageUrl = configurationService.getPropertyValueAsString(KRADConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        
        StringBuilder sectionTitle = new StringBuilder();
        
        sectionTitle.append("<img src=\"")
                    .append(riceImageUrl)
                    .append(sectionMarkGraphicName)
                    .append("\" alt=\"")
                    .append(sectionGraphicAlt)
                    .append("\" /><strong>")
                    .append(titleMessage)
                    .append("</strong>");
        
        return sectionTitle.toString();
    }
    
    /**
     * Builds an HTML comment, useful for debugging, which dumps out the message key being displayed
     * @param matchingKey the key to display
     * @param sectionGraphicAlt the alt for this section, we'll reuse it for the comments
     * @return the String to output for the key comment
     */
    protected String buildKeyComment(String matchingKey, String sectionGraphicAlt) {
        StringBuilder keyComment = new StringBuilder();
        
        keyComment.append("\n<!-- ")
                  .append(sectionGraphicAlt)
                  .append(" key = '")
                  .append(matchingKey)
                  .append("' -->\n");
        
        return keyComment.toString();
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
        html.append("<div class=\"left-errmsg-tab\">");
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
    protected List<String> getMatchingKeys(List messagePropertyList, String[] keysToMatch) {
        List<String> matchingKeys = new ArrayList<String>();
        
        if (messagePropertyList != null && messagePropertyList.size() > 0) {
            for (Object keyAsObject : messagePropertyList) {
                String key = (String)keyAsObject;
                if (matchesGroup(key, keysToMatch)) {
                    matchingKeys.add(key);
                }
            }
        }
        
        return matchingKeys;
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
    protected boolean foundKeyMatch(String key, String keyToMatch) {
        return key.equals(keyToMatch) || (keyToMatch.endsWith("*") && key.startsWith(keyToMatch.replaceAll("\\*", "")));
    }
    
    /**
     * Determines if the given key matches any error key associated with this group
     * @param key the error key that may or may not be displayed here
     * @param keysToMatch the keys that this group will match against
     * @return true if this group can display the given key, false otherwise
     */
    protected boolean matchesGroup(String key, String[] keysToMatch) {
        for (String keyToMatch : keysToMatch) {
            if (foundKeyMatch(key, keyToMatch)) return true;
        }
        return false;
    }

    /**
     * Looks up the InfoPropertyList from the generating request
     * @param pageContext the pageContext which this tag is rendering to
     * @return the ErrorPropertyList from the request
     */
    public List getErrorPropertyList(PageContext pageContext) {
        return (List)pageContext.getRequest().getAttribute("ErrorPropertyList");
    }
    
    /**
     * Looks up the InfoPropertyList from the generating request
     * @param pageContext the pageContext which this tag is rendering to
     * @return the WarningPropertyList from the request
     */
    protected List getWarningPropertyList(PageContext pageContext) {
        return (List)pageContext.getRequest().getAttribute("WarningPropertyList");
    }
    
    /**
     * Looks up the InfoPropertyList from the generating request
     * @param pageContext the pageContext which this tag is rendering to
     * @return the InfoPropertyList from the request
     */
    protected List getInfoPropertyList(PageContext pageContext) {
        return (List)pageContext.getRequest().getAttribute("InfoPropertyList");
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
     * Gets the warningsRendered attribute. 
     * @return Returns the warningsRendered.
     */
    public List<String> getWarningsRendered() {
        if (warningsRendered == null) {
            warningsRendered = new ArrayList<String>();
        }
        return warningsRendered;
    }
    
    /**
     * Gets the infoRendered attribute. 
     * @return Returns the infoRendered.
     */
    public List<String> getInfoRendered() {
        if (infoRendered == null) {
            infoRendered = new ArrayList<String>();
        }
        return infoRendered;
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
