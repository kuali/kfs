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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.rice.kns.web.taglib.html.KNSImageTag;

/**
 * Renders an action for the accounting line view.
 */
public class ActionsRenderer implements Renderer {
    private List<AccountingLineViewAction> actions;
    private KNSImageTag actionButton = new KNSImageTag();
    private int tabIndex;
    private String postButtonSpacing;
    private String tagBeginning;
    private String tagEnding;
    
    /**
     * Constructs a ActionsRenderer, which sets values on the actionButton tag that never change
     */
    public ActionsRenderer() {
        actionButton.setStyleClass("tinybutton"); // this never changes
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        actions = null;
        
        resetButton();
        actionButton.setPageContext(null);
        actionButton.setParent(null);
    }
    
    /**
     * Clears out changing values the action button tag
     */
    protected void resetButton() {
        actionButton.setProperty(null);
        actionButton.setSrc(null);
        actionButton.setTitle(null);
        actionButton.setAlt(null);
        actionButton.setTabindex(null);
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        actionButton.setPageContext(pageContext);
        actionButton.setParent(parentTag);
        JspWriter out = pageContext.getOut();
        
        try {
            if (actions != null && actions.size() > 0) {
                out.write(this.getTagBeginning());
                for (AccountingLineViewAction action : actions) {
                    renderAction(action);
                    out.write(this.getPostButtonSpacing());
                }
                out.write(this.getTagEnding());
            } 
            else {
                out.write(buildNonBreakingSpace());
            }
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering actions block", ioe);
        }
    }
    
    /**
     * Renders a single action, using the action button
     * @param action the action to render
     * @throws JspException thrown if the actionButton cannot uphold its duties to render the 
     */
    protected void renderAction(AccountingLineViewAction action) throws JspException {
        actionButton.setProperty(KFSConstants.DISPATCH_REQUEST_PARAMETER+"."+action.getActionMethod());
        actionButton.setSrc(action.getImageName());
        actionButton.setTitle(action.getActionLabel());
        actionButton.setAlt(action.getActionLabel());
        if (!StringUtils.isBlank(getTabIndex())) {
            actionButton.setTabindex(getTabIndex());
        }
        actionButton.doStartTag();
        actionButton.doEndTag();
        resetButton();
    }
    
    /**
     * Builds the opening of the centering div tag
     * @return the opening of the centering div tag in HTML
     */
    protected String buildCenteringDivBeginning() {
        return "<div style=\"text-align: center;\">";
    }
    
    /**
     * Builds the close of the centering div tag
     * @return the close of the centering div tag in HTML
     */
    protected String buildCenteringDivEnding() {
        return "</div>";
    }
    
    /**
     * Builds spacing for after the button is displayed
     * @return a String of HTML that will space after the button
     */
    public String getPostButtonSpacing() {
        return postButtonSpacing == null ? "<br />" : postButtonSpacing;
    }
    
    /**
     * Sets the postButtonSpacing attribute value.
     * @param postButtonSpacing The postButtonSpacing to set.
     */
    public void setPostButtonSpacing(String postButtonSpacing) {
        this.postButtonSpacing = postButtonSpacing;
    }

    /**
     * Gets the action attribute. 
     * @return Returns the action.
     */
    public List<AccountingLineViewAction> getActions() {
        return actions;
    }

    /**
     * Sets the action attribute value.
     * @param action The action to set.
     */
    public void setActions(List<AccountingLineViewAction> actions) {
        this.actions = actions;
    }

    /**
     * Sets the tab index for the action
     * @param tabIndex the tab index to set
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;   
    }
    
    /**
     * Retrieves the set tab index as a String, or, if the tabIndex was never set, returns a null
     * @return the tab index as a String or null
     */
    protected String getTabIndex() {
        if (tabIndex > -1) return Integer.toString(tabIndex); 
        return null;
    }
    
    /**
     * @return the HTML for a non-breaking space, so the box isn't all empty
     */
    protected String buildNonBreakingSpace() {
        return "&nbsp;";
    }

    /**
     * Gets the tagBeginning attribute. 
     * @return Returns the tagBeginning.
     */
    public String getTagBeginning() {
        return tagBeginning == null ? this.buildCenteringDivBeginning() : tagBeginning;
    }

    /**
     * Sets the tagBeginning attribute value.
     * @param tagBeginning The tagBeginning to set.
     */
    public void setTagBeginning(String tagBeginning) {
        this.tagBeginning = tagBeginning;
    }

    /**
     * Gets the tagEnding attribute. 
     * @return Returns the tagEnding.
     */
    public String getTagEnding() {
        return tagEnding == null ? this.buildCenteringDivEnding() : tagEnding;
    }

    /**
     * Sets the tagEnding attribute value.
     * @param tagEnding The tagEnding to set.
     */
    public void setTagEnding(String tagEnding) {
        this.tagEnding = tagEnding;
    }
}
