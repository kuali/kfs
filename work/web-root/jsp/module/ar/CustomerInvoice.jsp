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
	
<kul:page  showDocumentInfo="false" 
	headerTitle="Customer Invoice Generation" docTitle="Customer Invoice Generation" renderMultipart="true"
	transactionalDocument="false" htmlFormAction="arCustomerInvoice" errorKey="foo">
	
	
	
	
	
	 <table cellpadding="0" cellspacing="0" class="datatable-80" summary="Invoice Section">
            
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
                    <div align="right">User Id:</div>
                </th>
                <td align=left valign=middle class="grid">
                <html-el:text property="userId"/>
            	</td>
            </tr>
             <tr>
				<th align=right valign=middle class="grid">
                    <div align="right">Print invoices for date:</div>
                </th>
                <td align=left valign=middle class="grid">
                    <kul:dateInput attributeEntry="${orgAttributes.organizationBeginDate}" property="runDate"/>
                </td>                
				            
            </tr>
            <tr>
           		<th align=right valign=middle class="grid">
                    <div align="right">Org Type:</div>
                </th>
            	<td align=left valign=middle class="grid">
                    <html-el:radio property="orgType" value="P"/>Processing
                    <html-el:radio property="orgType" value="B"/>Billing
                </td>
            </tr>
            
          
            
           
<%--            <tr>--%>
<%--        <c:choose>--%>
<%--            <c:when test="${!CustomerStatementForm.operationSelected}">--%>
<%--         <th>--%>
<%--            <html-el:image property="methodToCall.selectOperation" styleClass="tinybutton" src="${ConfigProperties.externalizable.images.url}tinybutton-generate.gif" />--%>
<%--           </th>--%>
<%--      --%>
<%--        </c:when>--%>
<%--        </c:choose>--%>
<%--        --%>
<%--        <tr>--%>
<%--        <th>--%>
<%--           <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="tinybutton" property="methodToCall.cancel" title="cancel" alt="cancel"/>--%>
<%--            <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif" styleClass="tinybutton" property="methodToCall.clear" title="clear" alt="clear"/>--%>
<%--           </th>--%>
<%--        </tr>--%>
<%--        --%>
<%--        </tr>--%>
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
