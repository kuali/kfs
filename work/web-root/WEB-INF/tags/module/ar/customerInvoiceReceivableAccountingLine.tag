<%--
 Copyright 2006-2009 The Kuali Foundation
 
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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
    description="The DataDictionary entry containing attributes for this row's fields." %>              
<%@ attribute name="readOnly" required="true" 
	description="used to decide editability of overview fields" %>
<%@ attribute name="receivableValuesMap" required="false" type="java.util.Map"
    description="map of the accounting line primitive fields and values, for inquiry keys" %>     
<%@ attribute name="accountsCanCrossCharts" required="false"
	description="Whether or not accounts can cross charts"%>
	
<script language="JavaScript" type="text/javascript" src="scripts/module/ar/receivableObjectInfo.js"></script>        
                         
<kul:tab tabTitle="Receivable" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_RECEIVABLE_ACCOUNTING_LINE}">
    <div class="tab-container" align=center>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
            <tr>
                <td colspan="7" class="subhead">Receivable</td>
            </tr>               
            <tr>
			    <kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.paymentChartOfAccountsCode}"/>
			    <kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.paymentAccountNumber}"/>
			    <kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.paymentSubAccountNumber}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.paymentFinancialObjectCode}"/>
			    <kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.paymentFinancialSubObjectCode}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.paymentProjectCode}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.paymentOrganizationReferenceIdentifier}" />            
            </tr>
            <tr>
                <td align=left valign=middle class="datacell">
                  <c:if test="${!accountsCanCrossCharts}">
					<span id="document.paymentChartOfAccountsCode.div">
						<bean:write name="KualiForm" property="document.paymentChartOfAccountsCode" />
					</span>
				  </c:if> 
				  <c:if test="${accountsCanCrossCharts}">
                	<span class="nowrap">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.paymentChartOfAccountsCode}" 
                    	property="document.paymentChartOfAccountsCode"
                    	styleClass="${dataCellCssClass}" 
                    	onblur="loadChartInfo( this.name, 'document.paymentChartOfAccounts.finChartOfAccountDescription' );" 
                    	readOnly="${readOnly}"/>
                    </span>
				  </c:if>
                    <br />
                    <div id="document.paymentChartOfAccounts.finChartOfAccountDescription.div" class="fineprint">${document.paymentChartOfAccounts.finChartOfAccountDescription}</div>
                </td>
				
                <td align=left valign=middle class="datacell">
                	<span class="nowrap">
                  <c:if test="${!accountsCanCrossCharts}">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.paymentAccountNumber}" 
                    	property="document.paymentAccountNumber"
                    	styleClass="${dataCellCssClass}" 
                    	onblur="loadReceivableChartAccountInfo( this.name, 'document.paymentAccount.accountName' );" 
                    	readOnly="${readOnly}"/>
				  </c:if>
				  <c:if test="${accountsCanCrossCharts}">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.paymentAccountNumber}" 
                    	property="document.paymentAccountNumber"
                    	styleClass="${dataCellCssClass}" 
                    	onblur="loadReceivableAccountInfo( this.name, 'document.paymentAccount.accountName' );" 
                    	readOnly="${readOnly}"/>
 				  </c:if>
					<c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Account" fieldConversions="accountNumber:document.paymentAccountNumber" lookupParameters="document.paymentAccountNumber:accountNumber,document.paymentChartOfAccountsCode:chartOfAccountsCode"/>
                    </c:if>
                    </span>
                    <br />
                    <div id="document.paymentAccount.accountName.div" class="fineprint">${document.paymentAccount.accountName}</div>
                </td>
				
                <td align=left valign=middle class="datacell">
                	<span class="nowrap">
                    <kul:htmlControlAttribute 
                   		attributeEntry="${documentAttributes.paymentSubAccountNumber}" 
                   		property="document.paymentSubAccountNumber" 
                    	styleClass="${dataCellCssClass}" 
                    	onblur="loadReceivableSubAccountInfo( this.name, 'document.paymentSubAccount.subAccountName' );" 
                   		readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubAccount" fieldConversions="subAccountNumber:document.paymentSubAccountNumber" lookupParameters="document.paymentSubAccountNumber:subAccountNumber,document.paymentAccountNumber:accountNumber,document.paymentChartOfAccountsCode:chartOfAccountsCode"/>
                    </c:if>
                    </span>
                    <br />
                    <div id="document.paymentSubAccount.subAccountName.div" class="fineprint">${document.paymentSubAccount.subAccountName}</div>
                </td>
				 
				<td align=left valign=middle class="datacell">
                	<span class="nowrap">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.paymentFinancialObjectCode}" 
                    	property="document.paymentFinancialObjectCode" 
                    	styleClass="${dataCellCssClass}" 
                    	onblur="loadReceivableObjectInfo( '2009', 'document.paymentobjectType.name', 'document.paymentObjectTypeCode', this.name, 'document.paymentFinancialObject.financialObjectCodeName' );" 
                    	readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ObjectCode" fieldConversions="financialObjectCode:document.paymentFinancialObjectCode" lookupParameters="document.paymentFinancialObjectCode:financialObjectCode,document.paymentChartOfAccountsCode:chartOfAccountsCode"/>
                    </c:if>
                    </span>
                    <br />
                    <div id="document.paymentFinancialObject.financialObjectCodeName.div" class="fineprint">${document.paymentFinancialObject.financialObjectCodeName}</div>
                </td>
				
                <td align=left valign=middle class="datacell">
                	<span class="nowrap">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.paymentFinancialSubObjectCode}" 
                    	property="document.paymentFinancialSubObjectCode" 
                    	styleClass="${dataCellCssClass}" 
                    	onblur="loadReceivableSubObjectInfo( this.name, 'document.paymentFinancialSubObject.financialSubObjectCodeName' );" 
                    	readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubObjCd" fieldConversions="financialSubObjectCode:document.paymentFinancialSubObjectCode" lookupParameters="document.paymentFinancialSubObjectCode:financialSubObjectCode,document.paymentFinancialObjectCode:financialObjectCode,document.paymentChartOfAccountsCode:chartOfAccountsCode"/>
                    </c:if>
                    </span>
                    <br />
                    <div id="document.paymentFinancialSubObject.financialSubObjectCodeName.div" class="fineprint">${document.paymentFinancialSubObject.financialSubObjectCodeName}</div>
                </td>
				
				<td align=left valign=middle class="datacell">
                	<span class="nowrap">
					<kul:htmlControlAttribute 
						attributeEntry="${documentAttributes.paymentProjectCode}" 
						property="document.paymentProjectCode" 
                    	styleClass="${dataCellCssClass}" 
                    	onblur="loadReceivableProjectInfo( this.name, 'document.paymentProject.name' );" 
						readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ProjectCode" fieldConversions="code:document.paymentProjectCode" lookupParameters="document.paymentProjectCode:code"/>
                    </c:if>
                    </span>
                    <br />
                    <div id="document.paymentProject.name.div" class="fineprint">${document.paymentProject.name}</div>
                </td>
				
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.paymentOrganizationReferenceIdentifier}" 
                    	property="document.paymentOrganizationReferenceIdentifier" 
                    	styleClass="${dataCellCssClass}" 
                    	readOnly="${readOnly}"/>
                </td>          

            </tr>
        </table>
    </div>
</kul:tab>
