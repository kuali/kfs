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

<%@ attribute name="invPropertyName" required="true" description="The invoice" %>
<%@ attribute name="ctr" required="true" description="The invoice index" %>              
              
<c:set var="cgInvoiceAttributes" value="${DataDictionary['ContractsGrantsInvoiceDocument'].attributes}" />
<c:set var="cgInvoiceDetail" value="${DataDictionary['ContractsGrantsInvoiceDetail'].attributes}" />
<c:set var="invoiceGeneralDetailAttributes" value="${DataDictionary['InvoiceGeneralDetail'].attributes}" />

<%-- generate unique tab key from invPropertyName --%>
<c:set var="tabKey" value="${kfunc:generateTabKey(invPropertyName)}"/>
<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>

<%-- default to open --%>
<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="true" />
    </c:when>
    <c:when test="${!empty currentTab}" >
        <c:set var="isOpen" value="${currentTab == 'OPEN'}" />
    </c:when>
</c:choose>

<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

<tr>
    <td colspan="10" style="padding: 0px;">
        <table style="width: 100%;" cellpadding="0" cellspacing="0" class="datatable" >
            <tr>
                <td class="tab-subhead" style="border-right: none;">
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
				<th align="right">Invoice Document Number:</th>
				<td>
					<div id="document.invoices.invoiceDocumentNumber.div">
						<kul:htmlControlAttribute attributeEntry="${cgInvoiceDetail.documentNumber}"
							property="${invPropertyName}.documentNumber" readOnly="true" />
					</div>
				</td>									
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${cgInvoiceAttributes.billingDate}" horizontal="true"/>
				<td>
					<div id="document.invoices.billingPeriod.div">
						<kul:htmlControlAttribute attributeEntry="${cgInvoiceAttributes.billingDate}"
							property="${invPropertyName}.billingDate" readOnly="true" />
					</div>
				</td>									
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${invoiceGeneralDetailAttributes.billingPeriod}" horizontal="true"/>
				<td>
					<div id="document.invoices.billingPeriod.div">
						<kul:htmlControlAttribute attributeEntry="${invoiceGeneralDetailAttributes.billingPeriod}"
							property="${invPropertyName}.invoiceGeneralDetail.billingPeriod" readOnly="true" />
					</div>
				</td>									
			</tr>
			<c:if test="${not readOnly}">
				<tr>
					<th class="datacell" colspan="2">
						<div align="center">
							<html:image property="methodToCall.deleteInvoice.line${ctr}"
								src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" 
								alt="Delete Invoice" 
								title="Delete Invoice"
								styleClass="tinybutton" />
						</div>
					</th>
				</tr>
			</c:if>
         </table>    
    </td>
</tr>
