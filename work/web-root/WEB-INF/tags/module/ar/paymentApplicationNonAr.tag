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
<%@ attribute name="hasRelatedCashControlDocument" required="true"
    description="If has related cash control document"%>
<%@ attribute name="readOnly" required="true"
    description="If document is in read only mode"%>
<%@ attribute name="isCustomerSelected" required="true"
    description="Whether or not the customer is set" %>
<%@ attribute name="customerAttributes" required="true"
    description="Attributes of Customer according to the data dictionary" %>
    
<c:set var="nonInvoicedAttributes" value="${DataDictionary['NonInvoiced'].attributes}" scope="request" />
<c:set var="nonInvoicedAddLine" value="${KualiForm.nonInvoicedAddLine}" scope="request" />

    <kul:tab tabTitle="Non-AR" defaultOpen="true"
        tabErrorKey="${KFSConstants.PaymentApplicationTabErrorCodes.NON_AR_TAB}">

        <div class="tab-container" align="center">
            <SCRIPT type="text/javascript">
			    var kualiForm = document.forms['KualiForm'];
			    var kualiElements = kualiForm.elements;
			</SCRIPT>
			<h3>Non-AR</h3>
            <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
                <tr>
                    <kul:htmlAttributeHeaderCell literalLabel=" "/>             
                    <kul:htmlAttributeHeaderCell
	                    attributeEntry="${nonInvoicedAttributes.chartOfAccountsCode}" />
                    <kul:htmlAttributeHeaderCell 
	                    attributeEntry="${nonInvoicedAttributes.accountNumber}" />
                    <kul:htmlAttributeHeaderCell
						attributeEntry="${nonInvoicedAttributes.subAccountNumber}" />
                    <kul:htmlAttributeHeaderCell 
						attributeEntry="${nonInvoicedAttributes.financialObjectCode}" />
                    <kul:htmlAttributeHeaderCell 
						attributeEntry="${nonInvoicedAttributes.financialSubObjectCode}" />
                    <kul:htmlAttributeHeaderCell 
						attributeEntry="${nonInvoicedAttributes.projectCode}" />
                    <kul:htmlAttributeHeaderCell 
						attributeEntry="${nonInvoicedAttributes.financialDocumentLineAmount}" />
                    <kul:htmlAttributeHeaderCell literalLabel="Action"/>
                </tr>
				<c:if test="${readOnly ne true}">
	                <tr>
	                    <td>
	                        add
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                            onblur="loadChartInfo(this.name, 'nonInvoicedAddLine.chart.name')"
	                            attributeEntry="${nonInvoicedAttributes.chartOfAccountsCode}"
	                            property="nonInvoicedAddLine.chartOfAccountsCode"/>
	                        <div id="nonInvoicedAddLine.chart.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.chartOfAccounts.name"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                            onblur="loadAccountInfo(this.name, 'nonInvoicedAddLine.account.name')"
	                            attributeEntry="${nonInvoicedAttributes.accountNumber}"
	                            property="nonInvoicedAddLine.accountNumber"/>
	                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Account" 
	                            autoSearch="true"
	                            lookupParameters="nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.accountNumber:accountNumber"
	                            fieldConversions="chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,accountNumber:nonInvoicedAddLine.accountNumber" />
	                        <div id="nonInvoicedAddLine.account.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.account.name"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                            onblur="loadSubAccountInfo(this.name, 'nonInvoicedAddLine.subAccount.name')"
	                            attributeEntry="${nonInvoicedAttributes.subAccountNumber}"
	                            property="nonInvoicedAddLine.subAccountNumber"/>
	                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubAccount" 
	                            autoSearch="true"
	                            lookupParameters="nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.accountNumber:accountNumber,nonInvoicedAddLine.subAccountNumber:subAccountNumber"
	                            fieldConversions="chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,accountNumber:nonInvoicedAddLine.accountNumber,subAccountNumber:nonInvoicedAddLine.subAccountNumber" />
	                        <div id="nonInvoicedAddLine.subAccount.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.subAccount.subAccountName"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                            onblur="loadObjectInfo('${KualiForm.document.postingYear}', '', '', this.name, 'nonInvoicedAddLine.objectCode.name')"
	                            attributeEntry="${nonInvoicedAttributes.financialObjectCode}"
	                            property="nonInvoicedAddLine.financialObjectCode"/>
	                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ObjectCode" 
	                            autoSearch="true"
	                            lookupParameters="nonInvoicedAddLine.financialObjectCode:financialObjectCode,nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode"
	                            fieldConversions="financialObjectCode:nonInvoicedAddLine.financialObjectCode,chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode" />
	                        <div id="nonInvoicedAddLine.objectCode.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.financialObject.financialObjectCodeName"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                            onblur="loadSubObjectInfo('${KualiForm.document.postingYear}', this.name, 'nonInvoicedAddLine.subObjectCode.name')"
	                            attributeEntry="${nonInvoicedAttributes.financialSubObjectCode}"
	                            property="nonInvoicedAddLine.financialSubObjectCode"/>
	                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubObjectCode" 
	                            autoSearch="true"
	                            lookupParameters="nonInvoicedAddLine.financialSubObjectCode:financialSubObjectCode,nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.financialObjectCode:financialObjectCode,nonInvoicedAddLine.accountNumber:accountNumber"
	                            fieldConversions="financialSubObjectCode:nonInvoicedAddLine.financialSubObjectCode,chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,financialObjectCode:nonInvoicedAddLine.financialObjectCode,accountNumber:nonInvoicedAddLine.accountNumber" />
	                        <div id="nonInvoicedAddLine.subObjectCode.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.financialSubObject.financialSubObjectCodeName"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                            onblur="loadProjectInfo(this.name, 'nonInvoicedAddLine.projectCode.name')"
	                            attributeEntry="${nonInvoicedAttributes.projectCode}"
	                            property="nonInvoicedAddLine.projectCode"/>
	                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ProjectCode" 
	                            autoSearch="true"
	                            lookupParameters="nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.projectCode:code"
	                            fieldConversions="chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,code:nonInvoicedAddLine.projectCode" />
	                        <div id="nonInvoicedAddLine.projectCode.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.project.projectDescription"/></div>
	                    </td>
	                    <td>
	                        <kul:htmlControlAttribute
	                        	styleClass="amount"
	                            attributeEntry="${nonInvoicedAttributes.financialDocumentLineAmount}"
	                            property="nonInvoicedAddLine.financialDocumentLineAmount"/>
	                    </td>
	                    <td>
	                    	<html:image property="methodToCall.applyAllAmounts"
		                        src="${ConfigProperties.externalizable.images.url}tinybutton-add1.gif"
		                        alt="Add" title="Add" styleClass="tinybutton" />
		                </td>
	                </tr>
                </c:if>
                <logic:iterate id="nonInvoiced" name="KualiForm"
	                   property="paymentApplicationDocument.nonInvoiceds" indexId="ctr">
                    <tr>
                        <td>
                            <c:out value="${nonInvoiced.financialDocumentLineNumber}" />
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.chartOfAccountsCode}" 
                                readOnly="${readOnly}" 
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].chartOfAccountsCode"
                                onblur="loadChartInfo(this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].chartOfAccounts.name')" 
                                onchange="loadChartInfo(this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].chartOfAccounts.name')" />
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].chartOfAccounts.name.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].chartOfAccounts.name"/></div>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.accountNumber}"
                                readOnly="${readOnly}" 
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].accountNumber"
                                onblur="loadAccountInfo(this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].account.accountName')" />
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].account.accountName.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].account.accountName"/></div>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.subAccountNumber}"
                                readOnly="${readOnly}" 
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].subAccountNumber"
                                onblur="loadSubAccountInfo(this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].subAccount.subAccountName')" />
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].subAccount.subAccountName.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].subAccount.subAccountName"/></div>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.financialObjectCode}"
                                readOnly="${readOnly}" 
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].financialObjectCode"
                                onblur="loadObjectInfo('${KualiForm.document.postingYear}', '', '', this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].financialObject.name')"
                                onchange="loadObjectInfo('${paymentApplicationDocument.postingYear}', '', '', this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].financialObject.name')" />
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].financialObject.name.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].financialObject.name"/></div>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.financialSubObjectCode}"
                                readOnly="${readOnly}" 
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].financialSubObjectCode" 
                                onblur="loadSubObjectInfo('${KualiForm.document.postingYear}', this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].financialSubObject.financialSubObjectCodeName')" />
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].financialSubObject.financialSubObjectCodeName.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].financialSubObject.financialSubObjectCodeName"/></div>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedAttributes.projectCode}"
                                readOnly="${readOnly}" 
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].projectCode"
                                onblur="loadProjectInfo(this.name, 'paymentApplicationDocument.nonInvoiced[${ctr}].project.projectDescription')" />
                            <div id="paymentApplicationDocument.nonInvoiced[${ctr}].project.projectDescription.div"><bean:write name="KualiForm" property="paymentApplicationDocument.nonInvoiced[${ctr}].project.projectDescription"/></div>
                        </td>
                        <td style="text-align: right;">
                            <kul:htmlControlAttribute
                            	styleClass="amount"
                                attributeEntry="${nonInvoicedAttributes.financialDocumentLineAmount}"
                                readOnly="${readOnly}" 
                                property="paymentApplicationDocument.nonInvoiced[${ctr}].financialDocumentLineAmount"/>
                        </td>
                        <c:choose>
                        <c:when test="${readOnly ne true}">
                            <td>
	                    	    <html:image property="methodToCall.deleteNonArLine.line${nonInvoiced.financialDocumentLineNumber}"
		                            src="${ConfigProperties.externalizable.images.url}tinybutton-delete1.gif"
		                            alt="Delete" title="Delete" styleClass="tinybutton" />
                            </td>
                        </c:when>
                        <c:otherwise>
                        	<td>&nbsp;</td>
                        </c:otherwise>
                        </c:choose>
                    </tr>
                </logic:iterate>
                <tr>
                    <th colspan='6'>&nbsp;</th>
                    <kul:htmlAttributeHeaderCell literalLabel="Non-AR Total"/>
                    <td style="text-align: right;">
                        <kul:htmlControlAttribute
                            attributeEntry="${nonArTotal}"
                            property="nonArTotal" readOnly="true" />
                    	<!--${KualiForm.nonArTotal}-->
                    </td>
                    <td>&nbsp;</td>
                </tr>
            </table>
        </div>
    </kul:tab>
