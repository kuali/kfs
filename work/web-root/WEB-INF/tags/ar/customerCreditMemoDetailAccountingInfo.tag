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

<%@ attribute name="invPropertyName" required="true" description="The customer invoice detail" %>              
<%@ attribute name="cssClass" required="true" description="The cssClass" %>
              
<c:set var="customerInvoiceDetailAttributes" value="${DataDictionary.CustomerInvoiceDetail.attributes}" />

<tr>
    <td colspan="9" style="padding: 0px;">
        <table style="width: 100%;" cellpadding="0" cellspacing="0" class="datatable" >
            <tr>
                <td class="tab-subhead" style="border-right: none;">Accounting Information 
                   <html:image  property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
                </td>
            </tr>
        </table>    
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
        </div>
    </td>
</tr>