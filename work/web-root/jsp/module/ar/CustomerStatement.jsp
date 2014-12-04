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
