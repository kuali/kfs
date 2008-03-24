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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<%@ attribute name="readOnly" required="true" description="Controlls if values should be read only" %>              
                         
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
				<fin:accountingLineDataCell 
					dataCellCssClass="${dataCellCssClass}"
					accountingLine="document"
					field="paymentChartOfAccountsCode" 
					detailFunction="loadChartInfo"
					detailField="paymentChartOfAccounts.finChartOfAccountDescription"
					attributes="${documentAttributes}" lookup="false" inquiry="true"
					boClassSimpleName="Chart"
					readOnly="${readOnly}"
				 />            
				 
				<fin:accountingLineDataCell 
					dataCellCssClass="${dataCellCssClass}"
					accountingLine="document"
					field="paymentAccountNumber" 
					conversionField="accountNumber"
					detailFunction="loadReceivableAccountInfo"
					detailField="paymentAccount.accountName"
					attributes="${documentAttributes}" lookup="true" inquiry="true"
					boClassSimpleName="Account"
					lookupParameters="document.paymentChartOfAccountsCode:chartOfAccountsCode"
					lookupUnkeyedFieldConversions="chartOfAccountsCode:document.paymentChartOfAccountsCode,"
					readOnly="${readOnly}"
				 />  
				 
				<fin:accountingLineDataCell 
					dataCellCssClass="${dataCellCssClass}"
					accountingLine="document"
					field="paymentSubAccountNumber" 
					conversionField="subAccountNumber"
					detailFunction="loadReceivableSubAccountInfo"
					detailField="paymentSubAccount.subAccountName"
					attributes="${documentAttributes}" lookup="true" inquiry="true"
					boClassSimpleName="SubAccount"
					lookupParameters="document.paymentAccountNumber:accountNumber,document.paymentChartOfAccountsCode:chartOfAccountsCode"
					lookupUnkeyedFieldConversions="chartOfAccountsCode:document.paymentChartOfAccountsCode,accountNumber:document.paymentAccountNumber,"
					readOnly="${readOnly}"
				 />  
				 
				<fin:accountingLineDataCell 
					dataCellCssClass="${dataCellCssClass}"
					accountingLine="document"
					field="paymentFinancialObjectCode" 
					conversionField="financialObjectCode"
					detailFunction="loadReceivableObjectInfo"
					detailFunctionExtraParam="'${KualiForm.document.postingYear}', '', '', "
					detailField="paymentFinancialObject.financialObjectCodeName"
					attributes="${documentAttributes}" lookup="true" inquiry="true"
					boClassSimpleName="ObjectCode"
					lookupParameters="document.paymentChartOfAccountsCode:chartOfAccountsCode"
					lookupUnkeyedFieldConversions="chartOfAccountsCode:document.paymentChartOfAccountsCode,"
					readOnly="${readOnly}"
				 />
				 
				<fin:accountingLineDataCell 
					dataCellCssClass="${dataCellCssClass}"
					accountingLine="document"
					field="paymentFinancialSubObjectCode" 
					conversionField="financialSubObjectCode"
					detailFunction="loadReceivableSubObjectInfo"
					detailFunctionExtraParam="'${KualiForm.document.postingYear}', "
					detailField="paymentFinancialSubObject.financialSubObjectCodeName"
					attributes="${documentAttributes}" lookup="true" inquiry="true"
					boClassSimpleName="SubObjCd"
					lookupParameters="document.paymentChartOfAccountsCode:chartOfAccountsCode,document.paymentFinancialObjectCode:financialObjectCode,document.paymentAccountNumber:accountNumber"
					lookupUnkeyedFieldConversions="chartOfAccountsCode:document.paymentChartOfAccountsCode,financialObjectCode:document.paymentFinancialObjectCode,accountNumber:document.paymentAccountNumber"
					readOnly="${readOnly}"
				 />
				 
				<fin:accountingLineDataCell 
					dataCellCssClass="${dataCellCssClass}"
					accountingLine="document"
					field="paymentProjectCode" 
					conversionField="code"
					detailFunction="loadReceivableProjectInfo"
					detailField="paymentProject.name"
					attributes="${documentAttributes}" lookup="true" inquiry="true"
					boClassSimpleName="ProjectCode"
					readOnly="${readOnly}"
				 />
				 
				<fin:accountingLineDataCell 
					dataCellCssClass="${dataCellCssClass}"
					accountingLine="document"
					field="paymentOrganizationReferenceIdentifier" 
					attributes="${documentAttributes}"
					readOnly="${readOnly}"
				 />				 
				             
				<!--
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentAccountNumber}" property="document.paymentAccountNumber" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.module.chart.bo.Account" fieldConversions="accountNumber:document.paymentAccountNumber" lookupParameters="document.paymentAccountNumber:accountNumber,document.paymentChartOfAccountsCode:chartOfAccountsCode"/>
                    </c:if>
                </td>
                
                      
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentSubAccountNumber}" property="document.paymentSubAccountNumber" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.module.chart.bo.SubAccount" fieldConversions="subAccountNumber:document.paymentSubAccountNumber" lookupParameters="document.paymentSubAccountNumber:subAccountNumber,document.paymentAccountNumber:accountNumber,document.paymentChartOfAccountsCode:chartOfAccountsCode"/>
                    </c:if>
                </td>
                
                
				<td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentFinancialObjectCode}" property="document.paymentFinancialObjectCode" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.module.chart.bo.ObjectCode" fieldConversions="financialObjectCode:document.paymentFinancialObjectCode" lookupParameters="document.paymentFinancialObjectCode:financialObjectCode,document.paymentChartOfAccountsCode:chartOfAccountsCode"/>
                    </c:if>
                </td>
                
                
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentFinancialSubObjectCode}" property="document.paymentFinancialSubObjectCode" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.module.chart.bo.SubObjCd" fieldConversions="financialSubObjectCode:document.paymentFinancialSubObjectCode" lookupParameters="document.paymentFinancialSubObjectCode:financialSubObjectCode,document.paymentFinancialObjectCode:financialObjectCode,document.paymentChartOfAccountsCode:chartOfAccountsCode"/>
                    </c:if>
                </td>
				<td align=left valign=middle class="datacell">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentProjectCode}" property="document.paymentProjectCode" readOnly="${readOnly}"/>
					<c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.module.chart.bo.ProjectCode" fieldConversions="code:document.paymentProjectCode" lookupParameters="document.paymentProjectCode:code"/>
                    </c:if>
                </td>
                
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentOrganizationReferenceIdentifier}" property="document.paymentOrganizationReferenceIdentifier" readOnly="${readOnly}"/>
                </td>                
                -->

            </tr>
        </table>
    </div>
</kul:tab>