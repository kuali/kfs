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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.html.HiddenTag;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.HideShowBlock;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.taglib.html.KNSImageTag;

/**
 * Renders a hide show block
 */
public class HideShowBlockRenderer implements Renderer {
    private HideShowBlock hideShowBlock;
    private HiddenTag tabStateTag = new HiddenTag();
    private KNSImageTag showHideButton = new KNSImageTag();
    
    protected String riceImageURLProperty = "kr.externalizable.images.url";

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        hideShowBlock = null;
        cleanTabStateTag();
        cleanShowHideButton();
    }
    
    /**
     * Cleans the tab state hidden tag
     */
    protected void cleanTabStateTag() {
        tabStateTag.setPageContext(null);
        tabStateTag.setParent(null);
        tabStateTag.setProperty(null);
        tabStateTag.setValue(null);
    }
    
    /**
     * Cleans the show/hide button up
     */
    protected void cleanShowHideButton() {
        showHideButton.setPageContext(null);
        showHideButton.setParent(null);
        showHideButton.setSrc(null);
        showHideButton.setAlt(null);
        showHideButton.setTitle(null);
        showHideButton.setProperty(null);
        showHideButton.setStyleClass(null);
        showHideButton.setStyleId(null);
        showHideButton.setOnclick(null);
    }

    /**
     * Renders the title row and forces the rendering of child content
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        
        try {
            out.write(buildLabelButtonTableOpening());
            renderTabStateTag(pageContext, parentTag);
            if (!StringUtils.isBlank(hideShowBlock.getLabel())) {
                out.write(hideShowBlock.getLabel());
            }
            renderShowHideButton(pageContext, parentTag);
            out.write(buildLabelButtonTableClosing());
            out.write(buildInnerTableOpening());
            hideShowBlock.renderChildRows(pageContext, parentTag);
            out.write(buildInnerTableClosing());
        } catch (IOException ioe) {
            throw new JspException("Difficulty rendering Hide/Show block", ioe);
        }
    }
    
    /**
     * @return the HTML for the opening of the button table
     */
    protected String buildLabelButtonTableOpening() {
        return "<table class=\"datatable\" style=\"padding: 0px\"><tr><td class=\"tab-subhead\">";
    }
    
    /**
     * Renders a hidden tag which holds the current tab state
     * @param pageContext the pageContext to render to
     * @param parentTag the tag requesting all this rendering
     * @throws JspException thrown if something goes wrong
     */
    protected void renderTabStateTag(PageContext pageContext, Tag parentTag) throws JspException {
        tabStateTag.setPageContext(pageContext);
        tabStateTag.setParent(parentTag);
        tabStateTag.setProperty("tabStates("+hideShowBlock.getTabKey()+")");
        tabStateTag.setValue(hideShowBlock.getTabState());
        
        tabStateTag.doStartTag();
        tabStateTag.doEndTag();
    }
    
    /**
     * @return the HTML for the closing of the label button table
     */
    protected String buildLabelButtonTableClosing() {
        return "</td></tr></table>";
    }
    
    /**
     * Renders the hide/show image button
     * @param pageContext the pageContext to render to
     * @param parentTag the tag requesting all this rendering
     * @throws JspException thrown if something goes wrong
     */
    protected void renderShowHideButton(PageContext pageContext, Tag parentTag) throws JspException {
        showHideButton.setPageContext(pageContext);
        showHideButton.setParent(parentTag);
        showHideButton.setProperty("methodToCall.toggleTab.tab"+hideShowBlock.getTabKey());
        showHideButton.setStyleClass("tinybutton");
        showHideButton.setStyleId("tab-"+hideShowBlock.getTabKey()+"-imageToggle");
        showHideButton.setOnclick("javascript: return toggleTab(document, '"+hideShowBlock.getTabKey()+"'); ");
        
        String riceImageDir = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(riceImageURLProperty);
        if (hideShowBlock.isShowing()) {
            showHideButton.setSrc(riceImageDir+"tinybutton-hide.gif");
            showHideButton.setAlt("Hide "+hideShowBlock.getFullLabel());
            showHideButton.setTitle("Hide "+hideShowBlock.getFullLabel());
        } else {
            showHideButton.setSrc(riceImageDir+"tinybutton-show.gif");
            showHideButton.setAlt("Show "+hideShowBlock.getFullLabel());
            showHideButton.setTitle("Show "+hideShowBlock.getFullLabel());
        }
        
        showHideButton.doStartTag();
        showHideButton.doEndTag();
    }
    
    /**
     * Creates the HTML for the hiding/showing div and inner table to display children in
     * @return the HTML for the opening of the inner table
     */
    protected String buildInnerTableOpening() {
        StringBuilder opening = new StringBuilder();
        opening.append("<div id=\"tab-"+hideShowBlock.getTabKey()+"-div\" style=\"display: ");
        opening.append(hideShowBlock.isShowing() ? "block" : "none");
        opening.append("\">");
        
        opening.append("<table class=\"datatable\" style=\"width: 100%;\">");
        
        return opening.toString();
    }
    
    /**
     * Creates the HTML to close the inner table and hide/show div
     * @return the HTML for the closing of the inner table
     */
    protected String buildInnerTableClosing() {
        return "</table></div>";
    }
    

    /**
     * Gets the hideShowBlock attribute. 
     * @return Returns the hideShowBlock.
     */
    public HideShowBlock getHideShowBlock() {
        return hideShowBlock;
    }

    /**
     * Sets the hideShowBlock attribute value.
     * @param hideShowBlock The hideShowBlock to set.
     */
    public void setHideShowBlock(HideShowBlock hideShowBlock) {
        this.hideShowBlock = hideShowBlock;
    }

}
