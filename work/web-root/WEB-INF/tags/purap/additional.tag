<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<%@ attribute name="displayRequisitionFields" required="false"
              description="Boolean to indicate if REQ specific fields should be displayed" %>

<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:tab tabTitle="Additional" defaultOpen="true" tabErrorKey="${PurapConstants.ADDITIONAL_TAB_ERRORS}">

    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Additional</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Additional Section">

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.organizationCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.chartOfAccountsCode}" property="document.chartOfAccountsCode" readOnly="true" />
                    &nbsp;/&nbsp;<kul:htmlControlAttribute attributeEntry="${documentAttributes.organizationCode}" property="document.organizationCode"  readOnly="true"/>
			        <c:if test="${!readOnly}" >
			            <kul:lookup boClassName="org.kuali.module.chart.bo.Org" fieldConversions="organizationCode:document.organizationCode,chartOfAccountsCode:document.chartOfAccountsCode"/>
			        </c:if>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonName}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonName}" property="document.requestorPersonName" readOnly="${readOnly}" />
			        <c:if test="${!readOnly}" >
                        <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" fieldConversions="personName:document.requestorPersonName,personLocalPhoneNumber:document.requestorPersonPhoneNumber,personEmailAddress:document.requestorPersonEmailAddress" /></div>
			        </c:if>
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderTransmissionMethodCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderTransmissionMethodCode}" property="document.purchaseOrderTransmissionMethodCode" readOnly="${readOnly}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonPhoneNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonPhoneNumber}" property="document.requestorPersonPhoneNumber" readOnly="${readOnly}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderCostSourceCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderCostSourceCode}" property="document.purchaseOrderCostSourceCode" readOnly="${readOnly}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonEmailAddress}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonEmailAddress}" property="document.requestorPersonEmailAddress" readOnly="${readOnly}" />
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactName}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactName}" property="document.institutionContactName" readOnly="${readOnly}" />
			        <c:if test="${!readOnly}" >
                        <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" fieldConversions="personName:document.institutionContactName,personLocalPhoneNumber:document.institutionContactPhoneNumber,personEmailAddress:document.institutionContactEmailAddress" /></div>
			        </c:if>
                </td>
                
                <c:choose>
	                <c:when test="${displayRequisitionFields}">
		                <th align=right valign=middle class="bord-l-b">
		                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference1Text}" /></div>
		                </th>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference1Text}" property="document.requisitionOrganizationReference1Text" readOnly="${readOnly}" />
		                </td>
	                </c:when>
        			<c:otherwise>
		                <th align=right valign=middle class="bord-l-b">
		                    &nbsp;
		                </th>
		                <td align=left valign=middle class="datacell">
		                    &nbsp;
		                </td>
	                </c:otherwise>
				</c:choose>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactPhoneNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactPhoneNumber}" property="document.institutionContactPhoneNumber" readOnly="${readOnly}" />
                </td>

                <c:choose>
	                <c:when test="${displayRequisitionFields}">
		                <th align=right valign=middle class="bord-l-b">
		                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference2Text}" /></div>
		                </th>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference2Text}" property="document.requisitionOrganizationReference1Text" readOnly="${readOnly}" />
		                </td>
	                </c:when>
        			<c:otherwise>
		                <th align=right valign=middle class="bord-l-b">
		                    &nbsp;
		                </th>
		                <td align=left valign=middle class="datacell">
		                    &nbsp;
		                </td>
	                </c:otherwise>
				</c:choose>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactEmailAddress}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactEmailAddress}" property="document.institutionContactEmailAddress" readOnly="${readOnly}" />
                </td>

                <c:choose>
	                <c:when test="${displayRequisitionFields}">
		                <th align=right valign=middle class="bord-l-b">
		                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference3Text}" /></div>
		                </th>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference1Text}" property="document.requisitionOrganizationReference3Text" readOnly="${readOnly}" />
		                </td>
	                </c:when>
        			<c:otherwise>
		                <th align=right valign=middle class="bord-l-b">
		                    &nbsp;
		                </th>
		                <td align=left valign=middle class="datacell">
		                    &nbsp;
		                </td>
	                </c:otherwise>
				</c:choose>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderTotalLimit}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderTotalLimit}" property="document.purchaseOrderTotalLimit" readOnly="${readOnly}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    &nbsp;
                </th>
                <td align=left valign=middle class="datacell">
                    &nbsp;
                </td>
            </tr>

        </table>

    </div>
</kul:tab>
