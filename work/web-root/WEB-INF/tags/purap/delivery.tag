<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:tab tabTitle="Delivery" defaultOpen="true" tabErrorKey="${Constants.DELIVERY_TAB_ERRORS}">
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Delivery</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Delivery Section">

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCampusCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryCampusCode}" 
                    	property="document.deliveryCampusCode" 
                    	onchange="submitForm()" 
                    	readOnly="${readOnly}"/>&nbsp;
                    <c:if test="${!readOnly}">
                       <html:image property="methodToCall.refresh" src="images/buttonsmall_refresh.gif" alt="refresh" styleClass="tinybutton"/>
                    </c:if> 
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToName}" 
                    	property="document.deliveryToName" readOnly="${readOnly}"/>
                    <kul:lookup boClassName="org.kuali.core.bo.user.KualiUser" 
                    	fieldConversions="universalUser.personName:document.deliveryToName,universalUser.personEmailAddress:document.deliveryToEmailAddress,universalUser.personLocalPhoneNumber:document.deliveryToPhoneNumber"/>
                </td>
            </tr>
            <tr>
            	<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingName}" 
                    	property="document.deliveryBuildingName" readOnly="${readOnly}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToEmailAddress}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToEmailAddress}" 
                    	property="document.deliveryToEmailAddress" readOnly="${readOnly}"/>
                </td>
            </tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine1Address}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine1Address}" 
                    	property="document.deliveryBuildingLine1Address"  readOnly="${readOnly}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToPhoneNumber}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToPhoneNumber}" 
                    	property="document.deliveryToPhoneNumber" readOnly="${readOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine2Address}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine2Address}" 
                    	property="document.deliveryBuildingLine2Address" readOnly="${readOnly}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryRequiredDate}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryRequiredDate}" datePicker="true" 
                    	property="document.deliveryRequiredDate" readOnly="${readOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}" 
                    	property="document.deliveryBuildingRoomNumber" readOnly="${readOnly}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryRequiredDateReasonCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryRequiredDateReasonCode}" 
                    	property="document.deliveryRequiredDateReasonCode" readOnly="${readOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCityName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCityName}" 
                    	property="document.deliveryCityName" readOnly="${readOnly}"/>
                </td>
                <th align=right valign=middle class="bord-l-b" rowspan="3">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryInstructionText}"/></div>
                </th>
                <td align=left valign=middle class="datacell"  rowspan="3">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryInstructionText}" 
                    	property="document.deliveryInstructionText" readOnly="${readOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryStateCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryStateCode}" 
                    	property="document.deliveryStateCode" readOnly="${readOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryPostalCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryPostalCode}" 
                    	property="document.deliveryPostalCode" readOnly="${readOnly}"/>
                </td>
			</tr>
        </table>
    </div>
</kul:tab>
