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
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.ui.Field;

/**
 * Renderer which displays a read only field
 */
public class ReadOnlyRenderer extends FieldRendererBase {
    private boolean shouldRenderInquiry = true;

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    @Override
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();

        try {
            String value = discoverRenderValue();
            out.write(buildBeginSpan());

            if (!StringUtils.isEmpty(value)) {
                if (shouldRenderInquiryLink()) {
                    out.write(buildBeginInquiryLink());
                }
                out.write(value);
                if (shouldRenderInquiryLink()) {
                    out.write(buildEndInquiryLink());
                }
            } else {
                out.write(buildNonBreakingSpace());
            }

            out.write(buildEndSpan());
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering read only field", ioe);
        }
    }

    /**
     * Clears the persisting tag.
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#clear()
     */
    @Override
    public void clear() {
        super.clear();
    }

    /**
     * Generates the HTML for the opening span tag to wrap the displayed value
     * @param propertyPrefix the property path from the form the business object being rendered
     * @return the HTML for the opening span
     */
    protected String buildBeginSpan() {
        StringBuilder beginSpan = new StringBuilder();
        beginSpan.append("<span id=\"");
        beginSpan.append(getFieldName());
        beginSpan.append(".div\">");
        return beginSpan.toString();
    }

    /**
     * Generates the HTML for the closing span tag to wrap the displayed value
     * @return the HTML for the closing span
     */
    protected String buildEndSpan() {
        return "</span>";
    }

    /**
     * Builds the opening anchor tag to make the displayed read only value open up an inquiry screen
     * @return the HTML for the opening inquiry anchor tag
     */
    protected String buildBeginInquiryLink() {
        StringBuilder beginInquiryLink = new StringBuilder();

        if (getField().getInquiryURL() instanceof AnchorHtmlData) {
            AnchorHtmlData htmlData = (AnchorHtmlData) getField().getInquiryURL();

            if(htmlData.getHref().startsWith("http")) {
                beginInquiryLink.append("<a href=\"");
            }
            else {
                beginInquiryLink.append("<a href=\"");
                beginInquiryLink.append(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY));
                beginInquiryLink.append("/kr/");
            }

            beginInquiryLink.append(htmlData.getHref());
            beginInquiryLink.append("\" title=\"");
            beginInquiryLink.append(htmlData.getTitle());
            beginInquiryLink.append("\" target=\"blank\">");

        }

        return beginInquiryLink.toString();
    }

    /**
     * Builds the closing anchor tag for the inquiry link
     * @return the HTML for the closing inquiry anchor tag
     */
    protected String buildEndInquiryLink() {
        if (getField().getInquiryURL() instanceof AnchorHtmlData) {
            return "</a>";
        }
        return "";
    }

    /**
     * Determines if this read only field should attempt to display the inquiry link around the rendered value
     * @return true if the inquiry link should be rendered, false otherwise
     */
    protected boolean shouldRenderInquiryLink() {
        return getField().getInquiryURL() != null && !StringUtils.isBlank(((AnchorHtmlData)getField().getInquiryURL()).getHref()) && isInquirableValue(getField().getPropertyValue())  && shouldRenderInquiry;
    }

    /**
     * Determines if the given property value is worthy of having an inquiry for it
     * @param propertyValue the value of the property to potentially render an inquiry for
     * @return true if the value is inquirable; false otherwise
     */
    protected boolean isInquirableValue(String propertyValue) {
        return !StringUtils.isBlank(propertyValue) && !propertyValue.matches("^-*$");
    }

    /**
     * Sets the shouldRenderInquiry attribute value.
     * @param shouldRenderInquiry The shouldRenderInquiry to set.
     */
    public void setShouldRenderInquiry(boolean shouldRenderInquiry) {
        this.shouldRenderInquiry = shouldRenderInquiry;
    }

    /**
     * Dropdowns are typically fields with codes, which may be close to meaningless, with more explanative labels.  Therefore,
     * fields which are drop downs should display the label instead
     * @return the label for the chosen key on the field if possible; otherwise, an empty String
     */
    protected String getValueForDropDown() {
        for (Object keyLabelPairAsObject : getField().getFieldValidValues()) {
            final KeyValue keyLabelPair = (KeyValue)keyLabelPairAsObject;
            if (getField().getPropertyValue().equalsIgnoreCase(keyLabelPair.getKey().toString())) {
                return keyLabelPair.getValue();
            }
        }
        return null;
    }

    /**
     * An algorithm to discover the actual read only value to render.  If this is a drop down, it finds the renderable value for the drop down;
     * if the value is unavailable, it searches for the property in unconverted values
     * @return the value to display
     *
     * KRAD Conversion: Discovering fields values
     */
    protected String discoverRenderValue() {
        String value = getField().getPropertyValue();
        if (getField().getFieldType().equals(Field.DROPDOWN) && !StringUtils.isEmpty(value)) {
            value = getValueForDropDown();
        }

        return value;
    }

    /**
     * @return the HTML for a non-breaking space, so the box isn't all empty
     */
    protected String buildNonBreakingSpace() {
        return "&nbsp;";
    }

    /**
     * Nope, no quick finder here
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    @Override
    public boolean renderQuickfinder() {
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#closeNoWrapSpan(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    @Override
    public void closeNoWrapSpan(PageContext pageContext, Tag parentTag) throws JspException {
        // do nothing - read onlys don't need "no wrap"
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#openNoWrapSpan(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    @Override
    public void openNoWrapSpan(PageContext pageContext, Tag parentTag) throws JspException {
        // do nothing - read onlys don't need "no wrap"
    }

}
