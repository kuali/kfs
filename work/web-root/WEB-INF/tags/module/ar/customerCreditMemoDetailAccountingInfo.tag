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

<%@ attribute name="invPropertyName" required="true" description="The customer invoice detail" %>              
<%@ attribute name="cssClass" required="true" description="The cssClass" %>
              
<c:set var="customerInvoiceDetailAttributes" value="${DataDictionary.CustomerInvoiceDetail.attributes}" />

<%-- generate unique tab key from invPropertyName --%>
<c:set var="tabKey" value="${kfunc:generateTabKey(invPropertyName)}"/>
<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>

<%-- default to closed --%>
<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="false" />
    </c:when>
    <c:when test="${!empty currentTab}" >
        <c:set var="isOpen" value="${currentTab == 'OPEN'}" />
    </c:when>
</c:choose>

<tr>
    <td colspan="10" style="padding: 0px;">
        <table style="width: 100%;" cellpadding="0" cellspacing="0" class="datatable" >
            <tr>
                <td class="tab-subhead" style="border-right: none;">Accounting Information
                	<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}"> 
                		<html:image property="methodToCall.toggleTab.tab${tabKey}" 
                        			src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" 
                                    alt="hide" title="toggle" styleClass="tinybutton"
                                    styleId="tab-${tabKey}-imageToggle"
                                    onclick="javascript: return toggleTab(document, '${tabKey}'); " />
                    </c:if>
                    <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
                    	<html:image property="methodToCall.toggleTab.tab${tabKey}"
                    				src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif"
                    				alt="show" title="toggle" styleClass="tinybutton"
                    				styleId="tab-${tabKey}-imageToggle"
                    				onclick="javascript: return toggleTab(document, '${tabKey}'); " />
                 	</c:if>
                </td>
            </tr>
        </table>
        
        <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
            <div style="display: block;" id="tab-${tabKey}-div" class="accountingInfo">
        </c:if>
        <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
            <div style="display: none;" id="tab-${tabKey}-div" class="accountingInfo">
        </c:if>
        
        <table style="width: 100%;" cellpadding="0" cellspacing="0" class="datatable" >
        	<tr>
        		<kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.chartOfAccountsCode}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.accountNumber}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.subAccountNumber}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.financialObjectCode}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.financialSubObjectCode}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.projectCode}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.organizationReferenceId}" />
        	</tr>
        	<tr>
        		<!--  Chart -->
	            <td align=left class="${cssClass}">
					<kul:htmlControlAttribute
						attributeEntry="${customerInvoiceDetailAttributes.chartOfAccountsCode }"
						property="${invPropertyName}.chartOfAccountsCode"
						readOnly="true"/>
				</td>
				<!--  Account -->               
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute
						attributeEntry="${customerInvoiceDetailAttributes.accountNumber }"
						property="${invPropertyName}.accountNumber"
						readOnly="true"/>
				</td>
				<!--  Sub-Account -->               
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute
						attributeEntry="${customerInvoiceDetailAttributes.subAccountNumber }"
						property="${invPropertyName}.subAccountNumber"
						readOnly="true"/>
				</td>
				<!--  Object -->               
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute
						attributeEntry="${customerInvoiceDetailAttributes.financialObjectCode }"
						property="${invPropertyName}.financialObjectCode"
						readOnly="true"/>
				</td>
				<!--  Sub-Object -->               
				<td align=left class="${cssClass}">
			  	    <kul:htmlControlAttribute
			  	    	attributeEntry="${customerInvoiceDetailAttributes.financialSubObjectCode }"
			  	    	property="${invPropertyName}.financialSubObjectCode"
			  	    	readOnly="true"/>
			    </td>
			    <!--  Project -->               
				<td align=left class="${cssClass}">
			     	<kul:htmlControlAttribute
			     		attributeEntry="${customerInvoiceDetailAttributes.projectCode }"
			     		property="${invPropertyName}.projectCode"
			     		readOnly="true"/>
				</td>
				<!--  Org. Ref. Id -->
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute
						attributeEntry="${customerInvoiceDetailAttributes.organizationReferenceId }"
						property="${invPropertyName}.organizationReferenceId"
						readOnly="true"/>
				</td> 
			</tr>
         </table>    
    </td>
</tr>
