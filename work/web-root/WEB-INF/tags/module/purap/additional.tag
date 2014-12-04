<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<%@ attribute name="displayRequisitionFields" required="false"
              description="Boolean to indicate if REQ specific fields should be displayed" %>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:set var="contentReadOnly" value="${not empty KualiForm.editingMode['lockContentEntry']}" />
<c:set var="internalPurchasingReadOnly" value="${not empty KualiForm.editingMode['lockInternalPurchasingEntry']}" />
<c:set var="amendmentEntry" value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />
<c:set var="lockB2BEntry" value="${(not empty KualiForm.editingMode['lockB2BEntry'])}" />
<c:set var="tabindexOverrideBase" value="80" />

<kul:tab tabTitle="Additional Institutional Info" defaultOpen="true" tabErrorKey="${PurapConstants.ADDITIONAL_TAB_ERRORS}">

    <div class="tab-container" align=center>
            <h3>Additional</h3>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Additional Section">

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderTransmissionMethodCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderTransmissionMethodCode}" property="document.purchaseOrderTransmissionMethodCode" 
                    extraReadOnlyProperty="document.purchaseOrderTransmissionMethod.purchaseOrderTransmissionMethodDescription" readOnly="${lockB2BEntry or (not (fullEntryMode or amendmentEntry))}" tabindexOverride="${tabindexOverrideBase + 0}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonName}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonName}" property="document.requestorPersonName" readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 5}" />
			        <c:if test="${(fullEntryMode or amendmentEntry)}" >
                        <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" fieldConversions="name:document.requestorPersonName,phoneNumber:document.requestorPersonPhoneNumber,emailAddress:document.requestorPersonEmailAddress" /></div>
			        </c:if>
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderCostSourceCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderCostSourceCode}" property="document.purchaseOrderCostSourceCode" 
                    extraReadOnlyProperty="document.purchaseOrderCostSource.purchaseOrderCostSourceDescription" readOnly="${lockB2BEntry or (not (fullEntryMode or amendmentEntry)) or displayRequisitionFields}" tabindexOverride="${tabindexOverrideBase + 0}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonPhoneNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonPhoneNumber}" property="document.requestorPersonPhoneNumber" readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 5}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactName}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactName}" property="document.institutionContactName" readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}" />
                    <c:if test="${(fullEntryMode or amendmentEntry)}" >
                        <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" fieldConversions="name:document.institutionContactName,phoneNumber:document.institutionContactPhoneNumber,emailAddress:document.institutionContactEmailAddress" /></div>
                    </c:if>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonEmailAddress}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonEmailAddress}" property="document.requestorPersonEmailAddress" readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 5}" />
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactPhoneNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactPhoneNumber}" property="document.institutionContactPhoneNumber" readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}" />
                </td>
                <c:choose>
                	<c:when test="${displayRequisitionFields}">
		                <th align=right valign=middle class="bord-l-b">
		                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference1Text}" /></div>
		                </th>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference1Text}" property="document.requisitionOrganizationReference1Text" readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 5}" />
		                </td>
	                </c:when>
        			<c:otherwise>
		                <th align=right valign=middle class="bord-l-b" rowspan=3>
		                    Sensitive Data
		                </th>
		                <td align=left valign=middle class="datacell" rowspan=3>
		                	<logic:iterate indexId="ctr" name="KualiForm" property="document.purchaseOrderSensitiveData" id="sd">
		                		${sd.sensitiveData.sensitiveDataDescription}<br>
		                	</logic:iterate>	
		                </td>
	                </c:otherwise>
				</c:choose>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactEmailAddress}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactEmailAddress}" property="document.institutionContactEmailAddress" readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}" />
                </td>
                <c:if test="${displayRequisitionFields}">
		                <th align=right valign=middle class="bord-l-b">
		                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference2Text}" /></div>
		                </th>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference2Text}" property="document.requisitionOrganizationReference2Text" readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 5}" />
		                </td>
	            </c:if>
            </tr>

            <c:if test="${!lockB2BEntry or displayRequisitionFields}">
            <tr>
	                <c:if test="${!lockB2BEntry}">
                		<th align=right valign=middle class="bord-l-b">
                    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderTotalLimit}" /></div>
                		</th>
                		<td align=left valign=middle class="datacell">
                    		<kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderTotalLimit}" property="document.purchaseOrderTotalLimit" readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 0}" />
                		</td>
		            </c:if>
	                <c:if test="${lockB2BEntry}">
	                    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
	                    <td align=left valign=middle class="datacell">&nbsp;</td>
	                </c:if>
	                <c:if test="${displayRequisitionFields}">
		                <th align=right valign=middle class="bord-l-b">
		                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference3Text}" /></div>
		                </th>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference3Text}" property="document.requisitionOrganizationReference3Text" readOnly="${not (fullEntryMode or amendmentEntry)}" tabindexOverride="${tabindexOverrideBase + 5}" />
		                </td>
	                </c:if>
            </tr>
            </c:if>

        </table>

    </div>
</kul:tab>

