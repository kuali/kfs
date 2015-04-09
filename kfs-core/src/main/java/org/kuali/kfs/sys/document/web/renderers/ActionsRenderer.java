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
