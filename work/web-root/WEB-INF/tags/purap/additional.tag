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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<%@ attribute name="displayRequisitionFields" required="false"
              description="Boolean to indicate if REQ specific fields should be displayed" %>

<c:set var="contentReadOnly" value="${not empty KualiForm.editingMode['lockContentEntry']}" />
<c:set var="internalPurchasingReadOnly" value="${not empty KualiForm.editingMode['lockInternalPurchasingEntry']}" />
<c:set var="amendmentEntry" value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />

<kul:tab tabTitle="Additional Institutional Info" defaultOpen="false" tabErrorKey="${PurapConstants.ADDITIONAL_TAB_ERRORS}">

    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Additional</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Additional Section">

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderTransmissionMethodCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderTransmissionMethodCode}" property="document.purchaseOrderTransmissionMethodCode" 
                    extraReadOnlyProperty="document.purchaseOrderTransmissionMethod.purchaseOrderTransmissionMethodDescription" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonName}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonName}" property="document.requestorPersonName" readOnly="${not (fullEntryMode or amendmentEntry)}" />
			        <c:if test="${(fullEntryMode or amendmentEntry)}" >
                        <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" fieldConversions="personName:document.requestorPersonName,personLocalPhoneNumber:document.requestorPersonPhoneNumber,personEmailAddress:document.requestorPersonEmailAddress" /></div>
			        </c:if>
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderCostSourceCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderCostSourceCode}" property="document.purchaseOrderCostSourceCode" 
                    extraReadOnlyProperty="document.purchaseOrderCostSource.purchaseOrderCostSourceDescription" readOnly="${not (fullEntryMode or amendmentEntry) or displayRequisitionFields}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonPhoneNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonPhoneNumber}" property="document.requestorPersonPhoneNumber" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactName}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactName}" property="document.institutionContactName" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                    <c:if test="${(fullEntryMode or amendmentEntry)}" >
                        <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" fieldConversions="personName:document.institutionContactName,personLocalPhoneNumber:document.institutionContactPhoneNumber,personEmailAddress:document.institutionContactEmailAddress" /></div>
                    </c:if>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonEmailAddress}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonEmailAddress}" property="document.requestorPersonEmailAddress" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactPhoneNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactPhoneNumber}" property="document.institutionContactPhoneNumber" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                </td>
                
                <c:choose>
	                <c:when test="${displayRequisitionFields}">
		                <th align=right valign=middle class="bord-l-b">
		                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference1Text}" /></div>
		                </th>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference1Text}" property="document.requisitionOrganizationReference1Text" readOnly="${not (fullEntryMode or amendmentEntry)}" />
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
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactEmailAddress}" property="document.institutionContactEmailAddress" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                </td>

                <c:choose>
	                <c:when test="${displayRequisitionFields}">
		                <th align=right valign=middle class="bord-l-b">
		                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference2Text}" /></div>
		                </th>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference2Text}" property="document.requisitionOrganizationReference2Text" readOnly="${not (fullEntryMode or amendmentEntry)}" />
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
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderTotalLimit}" property="document.purchaseOrderTotalLimit" readOnly="${not (fullEntryMode or amendmentEntry)}" />
                </td>

                <c:choose>
	                <c:when test="${displayRequisitionFields}">
		                <th align=right valign=middle class="bord-l-b">
		                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference3Text}" /></div>
		                </th>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference3Text}" property="document.requisitionOrganizationReference3Text" readOnly="${not (fullEntryMode or amendmentEntry)}" />
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

        </table>

    </div>
</kul:tab>
