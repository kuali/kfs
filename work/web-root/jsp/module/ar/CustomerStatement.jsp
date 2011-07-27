<%--
 Copyright 2006-2008 The Kuali Foundation
 
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
	
	<c:set var="orgAttributes" value="${DataDictionary.Organization.attributes}" />
	<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />
	<c:set var="invoiceAttributes" value="${DataDictionary.CustomerInvoiceDocument.attributes}" />
	<c:set var="accountAttributes" value="${DataDictionary.Account.attributes}" />
	<c:set var="customerBillingStatementAttributes" value="${DataDictionary.CustomerBillingStatement.attributes}" />
	
<kul:page  showDocumentInfo="false"
	headerTitle="Billing Statement Generation" docTitle="Billing Statement Generation" renderMultipart="true"
	transactionalDocument="false" htmlFormAction="arCustomerStatement" errorKey="foo">

	 <table cellpadding="0" cellspacing="0" class="datatable-80" summary="Billing Statement">
			<tr>		
                <th align=right valign=middle class="grid" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${orgAttributes.chartOfAccountsCode}" readOnly="true" /></div>
                </th>
                <td align=left valign=middle class="grid" style="width: 25%;">
					<kul:htmlControlAttribute attributeEntry="${orgAttributes.chartOfAccountsCode}" property="chartCode"  />	
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Chart"  fieldConversions="chartOfAccountsCode:chartCode"  />
                </td>
				                       
            </tr>
            <tr>
				<th align=right valign=middle class="grid">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${orgAttributes.organizationCode}" readOnly="true" /></div>
                </th>
                <td align=left valign=middle class="grid">
                    <kul:htmlControlAttribute attributeEntry="${orgAttributes.organizationCode}" property="orgCode"  />
                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Organization"  fieldConversions="organizationCode:orgCode" lookupParameters="orgCode:organizationCode,chartCode:chartOfAccountsCode"/>
                </td>                
				            
            </tr>
             <tr>
				<th align=right valign=middle class="grid">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.customerNumber}" readOnly="true"/></div>
                </th>
                <td align=left valign=middle class="grid">
                	<kul:htmlControlAttribute attributeEntry="${arDocHeaderAttributes.customerNumber}" property="customerNumber"  />
                	<kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.Customer" fieldConversions="customerNumber:customerNumber" lookupParameters="customerNumber:customerNumber" />
                </td>                
				            
            </tr>
              <tr>
				<th align=right valign=middle class="grid">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${accountAttributes.accountNumber}" readOnly="true"/></div>
                </th>
                <td align=left valign=middle class="grid">
                	<kul:htmlControlAttribute attributeEntry="${accountAttributes.accountNumber}" property="accountNumber"  />
                	<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Account" fieldConversions="accountNbr:accountNumber" lookupParameters="accountNumber:accountNbr" />
                </td>                
				            
            </tr>
            <tr>		
                <th align=right valign=middle class="grid"">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerBillingStatementAttributes.statementFormat}" readOnly="true" /></div>
                </th>
                <td align=left valign=middle class="grid">                	
                	<%--<kul:htmlControlAttribute attributeEntry="${customerBillingStatementAttributes.statementFormat}" property="statementFormat" />--%>
	                <input type="radio" name="statementFormat" value="Summary" checked />Summary&nbsp;&nbsp;
					<input type="radio" name="statementFormat" value="Detail" />Detail&nbsp;&nbsp;
                </td>				                      
            </tr>            
            <tr>		
                <th align=right valign=middle class="grid">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerBillingStatementAttributes.includeZeroBalanceCustomers}" readOnly="true" /></div>
                </th>
                <td align=left valign=middle class="grid">
	                <input type="radio" name="includeZeroBalanceCustomers" value="Yes" />Yes&nbsp;&nbsp;
					<input type="radio" name="includeZeroBalanceCustomers" value="No" checked />No&nbsp;&nbsp;
                </td>				                      
            </tr>  
       
        </tr>
        </table>
    
     <c:set var="extraButtons" value="${KualiForm.extraButtons}"/>  	
  	
	
     <div id="globalbuttons" class="globalbuttons">
	        	
	        	<c:if test="${!empty extraButtons}">
		        	<c:forEach items="${extraButtons}" var="extraButton">
		        		<html:image src="${extraButton.extraButtonSource}" styleClass="globalbuttons" property="${extraButton.extraButtonProperty}" title="${extraButton.extraButtonAltText}" alt="${extraButton.extraButtonAltText}"/>
		        	</c:forEach>
	        	</c:if>
	</div>
	
	<div>
	  <c:if test="${!empty KualiForm.message }">
            	 ${KualiForm.message }
            </c:if>
   </div>
	

</kul:page>
