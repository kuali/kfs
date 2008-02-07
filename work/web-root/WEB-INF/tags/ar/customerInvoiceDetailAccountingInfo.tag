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

<%@ attribute name="propertyName" required="true" description="The customer invoice detail" %>              
<%@ attribute name="cssClass" required="true" description="The cssClass" %>
              
<c:set var="tabTitle" value="Customer Invoice Detail Accounting Info"/>
<c:set var="customerInvoiceDetailAttributes" value="${DataDictionary.CustomerInvoiceDetail.attributes}" />

<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}"/>
<c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
<!--  hit form method to increment tab index -->
<c:set var="dummyIncrementer" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />

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

<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

<tr>
    <th>&nbsp;</th>
    <td class="total-line" colspan="7" style="padding: 0px;">
        <table style="width: 100%;" cellpadding="0" cellspacing="0" class="datatable">
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
            <div style="display: block;" id="tab-${tabKey}-div">
        </c:if>
        <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
            <div style="display: none;" id="tab-${tabKey}-div">
        </c:if>

        <table class="datatable" style="width: 100%;">
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
				<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.chartOfAccountsCode }" property="${propertyName}.chartOfAccountsCode"/>
				&nbsp;
				<kul:lookup boClassName="org.kuali.module.chart.bo.Chart" fieldConversions="chartOfAccountsCode:${propertyName}.chartOfAccountsCode" />
				</td>               
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.accountNumber }" property="${propertyName}.accountNumber"/>
					&nbsp;
					<kul:lookup boClassName="org.kuali.module.chart.bo.Account" fieldConversions="accountNumber:${propertyName}.accountNumber" />
				</td>               
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.subAccountNumber }" property="${propertyName}.subAccountNumber"/>
					&nbsp;
					<kul:lookup boClassName="org.kuali.module.chart.bo.SubAccount" fieldConversions="subAccountNumber:${propertyName}.subAccountNumber" />
				</td>               
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.financialObjectCode }" property="${propertyName}.financialObjectCode"/>
					&nbsp;
					<kul:lookup boClassName="org.kuali.module.chart.bo.ObjectCode" fieldConversions="financialObjectCode:${propertyName}.financialObjectCode" />
				</td>               
				<td align=left class="${cssClass}">
			  	    <kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.financialSubObjectCode }" property="${propertyName}.financialSubObjectCode"/>
				    &nbsp;
			        <kul:lookup boClassName="org.kuali.module.chart.bo.SubObjCd" fieldConversions="financialSubObjectCode:${propertyName}.financialSubObjectCode" />
			    </td>               
				<td align=left class="${cssClass}">
			     	<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.projectCode }" property="${propertyName}.projectCode"/>
					&nbsp;
					<kul:lookup boClassName="org.kuali.module.chart.bo.ProjectCode" fieldConversions="code:${propertyName}.projectCode" />
				</td>
				<td align=left class="${cssClass}">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceDetailAttributes.organizationReferenceId }" property="${propertyName}.organizationReferenceId"/>
				</td> 
			</tr> 
          </table>
        </div>
    </td>
    <th>&nbsp;</th>
</tr>