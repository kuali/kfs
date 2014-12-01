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

<%@ attribute name="propertyName" required="true" description="The customer invoice detail" %>              
<%@ attribute name="cssClass" required="true" description="The cssClass" %>
<%@ attribute name="readOnly" required="true" description="If its readOnly mode" %>

<c:set var="customerInvoiceDetailAttributes" value="${DataDictionary.CustomerInvoiceDetail.attributes}" />

<%-- generate unique tab key from propertyName --%>
<c:set var="tabKey" value="${kfunc:generateTabKey(propertyName)}"/>
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
    <td colspan="9" style="padding: 0px;">
        <table style="width: 100%;" cellpadding="0" cellspacing="0" class="datatable" >
            <tr>
                <td class="tab-subhead" style="border-right: none;">Accounting Information 
                  <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
                    <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton"  styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
                 </c:if>
                 <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
                   <html:image  property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
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
	            <td align=left class="${cssClass}">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.chartOfAccountsCode }" property="${propertyName}.chartOfAccountsCode" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
						&nbsp;
						<kul:lookup 
							boClassName="org.kuali.kfs.coa.businessobject.Chart" 
							fieldConversions="chartOfAccountsCode:${propertyName}.chartOfAccountsCode" />
					</c:if>
				</td>               
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.accountNumber }" property="${propertyName}.accountNumber" readOnly="${readOnly}"/>
					&nbsp;
					<kul:lookup 
						boClassName="org.kuali.kfs.coa.businessobject.Account" 
						fieldConversions="accountNumber:${propertyName}.accountNumber,chartOfAccountsCode:${propertyName}.chartOfAccountsCode"
						lookupParameters="${propertyName}.chartOfAccountsCode:chartOfAccountsCode" />
				</td>               
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.subAccountNumber }" property="${propertyName}.subAccountNumber" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
						&nbsp;
						<kul:lookup 
							boClassName="org.kuali.kfs.coa.businessobject.SubAccount" 
							fieldConversions="subAccountNumber:${propertyName}.subAccountNumber,chartOfAccountsCode:${propertyName}.chartOfAccountsCode,accountNumber:${propertyName}.accountNumber"
							lookupParameters="${propertyName}.chartOfAccountsCode:chartOfAccountsCode,${propertyName}.accountNumber:accountNumber" />
					</c:if>
				</td>               
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.financialObjectCode }" property="${propertyName}.financialObjectCode" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
						&nbsp;
						<kul:lookup 
							boClassName="org.kuali.kfs.coa.businessobject.ObjectCode" 
							fieldConversions="financialObjectCode:${propertyName}.financialObjectCode"
							lookupParameters="${propertyName}.chartOfAccountsCode:chartOfAccountsCode" />
					</c:if>
				</td>               
				<td align=left class="${cssClass}">
			  	    <kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.financialSubObjectCode }" property="${propertyName}.financialSubObjectCode" readOnly="${readOnly}"/>
				    <c:if test="${not readOnly}">
					    &nbsp;
				        <kul:lookup
				        	boClassName="org.kuali.kfs.coa.businessobject.SubObjCd" 
				        	fieldConversions="financialSubObjectCode:${propertyName}.financialSubObjectCode,chartOfAccountsCode:${propertyName}.chartOfAccountsCode,accountNumber:${propertyName}.accountNumber"
				        	lookupParameters="${propertyName}.chartOfAccountsCode:chartOfAccountsCode,${propertyName}.accountNumber:accountNumber" />
			        </c:if>
			    </td>               
				<td align=left class="${cssClass}">
			     	<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.projectCode }" property="${propertyName}.projectCode" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
						&nbsp;
						<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ProjectCode" fieldConversions="code:${propertyName}.projectCode" />
					</c:if>
				</td>
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.organizationReferenceId }" property="${propertyName}.organizationReferenceId" readOnly="${readOnly}"/>
				</td> 
			</tr> 
          </table>
        </div>
    </td>
</tr>
