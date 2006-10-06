<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:tab tabTitle="Additional" defaultOpen="true" tabErrorKey="${Constants.ADDITIONAL_TAB_ERRORS}">

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
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.chartOfAccountsCode}" property="document.chartOfAccountsCode" readOnly="${readOnly}" />
                    &nbsp;/&nbsp;<kul:htmlControlAttribute attributeEntry="${documentAttributes.organizationCode}" property="document.organizationCode"  readOnly="${readOnly}"/>
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
                        <kul:lookup boClassName="org.kuali.core.bo.user.KualiUser" fieldConversions="universalUser.personName:document.requestorPersonName,universalUser.personLocalPhoneNumber:document.requestorPersonPhoneNumber,universalUser.personEmailAddress:document.requestorPersonEmailAddr" /></div>
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
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requestorPersonEmailAddr}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requestorPersonEmailAddr}" property="document.requestorPersonEmailAddr" readOnly="${readOnly}" />
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactName}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactName}" property="document.institutionContactName" readOnly="${readOnly}" />
			        <c:if test="${!readOnly}" >
                        <kul:lookup boClassName="org.kuali.core.bo.user.KualiUser" fieldConversions="universalUser.personName:document.institutionContactName,universalUser.personLocalPhoneNumber:document.institutionContactPhoneNumber,universalUser.personEmailAddress:document.institutionContactEmailAddress" /></div>
			        </c:if>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference1Text}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference1Text}" property="document.requisitionOrganizationReference1Text" readOnly="${readOnly}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactPhoneNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactPhoneNumber}" property="document.institutionContactPhoneNumber" readOnly="${readOnly}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference2Text}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference2Text}" property="document.requisitionOrganizationReference2Text" readOnly="${readOnly}" />
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.institutionContactEmailAddress}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.institutionContactEmailAddress}" property="document.institutionContactEmailAddress" readOnly="${readOnly}" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionOrganizationReference3Text}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.requisitionOrganizationReference3Text}" property="document.requisitionOrganizationReference3Text" readOnly="${readOnly}" />
                </td>
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
