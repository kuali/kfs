<%--
 Copyright 2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ attribute name="valign" required="false" description="The verticle alignment of the table cell" %>
<%@ attribute name="cellClass" required="false" description="Style class of the table cell" %>
<%@ attribute name="textAlign" required="false" description="Horizontal Text alignment property" %>
<%@ attribute name="cellStyle" required="false" description="Individual style properties of the table cell"%>
<%@ attribute name="cellWidth" required="false" description="Width of table cell" %>

<%@ attribute name="isLabel" required="false" description="If set, this tag will output the equivalent of a 'th'." %>
<%@ attribute name="inquiry" required="false" description="This tells this tag whether it is displying these cells in an inquiry screen" %>
<%@ attribute name="preText" required="false" description="Text that will be displayed before the properties value"%>
<%@ attribute name="postText" required="false" description="Text that will be displayed after the properties value"%>

<%@ attribute name="property" required="false" description="The property being rendered." %>
<%@ attribute name="attributeEntry" required="true" type="java.util.Map" description="The Map of data dictionary attributes about the property to render a control for." %>
<%@ attribute name="onblur" required="false" description="If set, this will be used as the onblur method on the control." %>
<%@ attribute name="readOnly" required="false" description="Whether this control should be rendered as read only (ie, not a control but rather text) or not." %>
<%@ attribute name="datePicker" required="false" description="Whether this control should be rendered with a date picker." %>
<%@ attribute name="expandedTextArea" required="false" description="whether to render an expanded textarea control.  Only applicable for textareas. "%>
<%@ attribute name="disabled" required="false" description="Whether this control should be rendered as disabled or not." %>
<%@ attribute name="onchange" required="false" description="If set, this will be used as the onchange method on the control." %>
<%@ attribute name="onclick" required="false" description="If set, this will be used as the onclick method on the control." %>
<%@ attribute name="tabindexOverride" required="false" description="If set, this will be used as the text index on the control." %>
<%@ attribute name="readOnlyBody" required="false"
              description="when readOnly, use the tag body instead of a written hidden field.
              This allows mixing in module-specific inquiries." %>
<%@ attribute name="extraReadOnlyProperty" required="false"
			  description="when readOnly, you can specify extra properties to display alongside of
			  the main property.  The readOnlyBody attribute takes precedence." %>
<%@ attribute name="readOnlyAlternateDisplay" required="false"
              description="when readOnly, you can specify a String value to display instead of
              the main property.  The readOnlyBody and extraReadOnlyProperty attributes take precedence.
              THIS VALUE WILL BE DISPLAYED WITHOUT ANY XML FILTERING/ESCAPING, AND NEEDS TO BE PROPERLY ESCAPED TO PREVENT CROSS-SITE SCRIPTING VULNERABILITIES" %>
<%@ attribute name="displayMask" required="false"
              description="Specify whether to mask the given field using the displayMaskValue rather than showing the actual value." %>
<%@ attribute name="displayMaskValue" required="false"
			  description="when a field is not to be displayed in clear text and encrypted as hidden, the
			  string to display." %>
<%@ attribute name="styleClass" required="false"
			  description="When a field has a css class applied to it, make sure that
			  we carry it through."%>
<%@ attribute name="accessibilityHint" required="false"
        description="Use this to attach further information to the title attribute of a field
        if present"%>
<%@ attribute name="forceRequired" required="false" description="Whether this control should be rendered as required, no matter the information from the data dictionary about the required state of the attribute." %>
<%@ attribute name="kimTypeId" required="false" description="If the rendered attribute is a KIM attribute, the ID of the type of that KIM attribute." %>


<%@ attribute name="attributeEntryName" required="false"
              description="The full name of the DataDictionary entry to use,
              e.g., 'DataDictionary.Budget.attributes.budgetProjectDirectorUniversalIdentifier'.
              Either attributeEntry or attributeEntryName is required." %>
<%@ attribute name="useShortLabel" required="false" description="Whether the short label for the control should be used." %>
<%@ attribute name="labelFor" required="false" description="The control name which this label is associated with; typically the property name will be sent in here." %>
<%@ attribute name="includeHelpUrl" required="false" description="If set to true, then the help link will render a help URL regardless of the skipHelpUrl parameter value." %>
<%@ attribute name="skipHelpUrl" required="false" description="If set to true and includeHelpUrl is set to false, then the help link will not be rendered for this attribute.  If both
              this attribute and includeHelpUrl are set to false, then the KualiForm.fieldLevelHelpEnabled will control whether to render the help link." %>
<%@ attribute name="noColon" required="false" description="Whether a colon should be rendered after the label or not." %>

<c:if test="${readOnly && kfunc:isInquiryHiddenField(attributeEntry.fullClassName, attributeEntry.name, KualiForm, property)}">
	<c:set var="doNotWriteToScreen" value="true"/>
</c:if>

<c:if test="${empty doNotWriteToScreen}">
  <c:choose>
  <c:when test="${isLabel}">
    <th width="${cellWidth}">
      <div align="${textAlign}">${preText} 
        <kul:htmlAttributeLabel attributeEntry="${attributeEntry}"
        						useShortLabel="${useShortLabel}" 
        						labelFor="${labelFor}"
        						includeHelpUrl="${includeHelpUrl}"
        						skipHelpUrl="${skipHelpUrl}"
        						noColon="${noColon}"
        						forceRequired="${forceRequired}" /> ${postText}
      </div>
    </th>
  </c:when>
  <c:otherwise>
    <td align="left" width="${cellWidth}" valign="${valign}" class="${cellClass}">
	  <div align="${textAlign}"> ${preText}
	  	<kul:htmlControlAttribute property="${property}" 
	  							  attributeEntry="${attributeEntry}" 
	  							  onblur="${onblur}"
	  							  readOnly="${readOnly}" 
	  							  datePicker="${datePicker}"
	  							  expandedTextArea="${expandedTextArea}"
	  							  disabled="${disabled}"
	  							  onchange="${onchange}"
	  							  onclick="${onclick}"
	  							  tabindexOverride="${tabindexOverride}"
	  							  readOnlyBody="${readOnlyBody}"
	  							  extraReadOnlyProperty="${extraReadOnlyProperty}"
	  							  readOnlyAlternateDisplay="${readOnlyAlternateDisplay}"
	  							  displayMask="${displayMask}"
	  							  displayMaskValue="${displayMaskValue}"
	  							  styleClass="${styleClass}"
	  							  accessibilityHint="${accessibilityHint}"
	  							  forceRequired="${forceRequired}"
	  							  /> ${postText}
	  </div>
    </td>
  </c:otherwise>
  </c:choose>  	  
</c:if>

