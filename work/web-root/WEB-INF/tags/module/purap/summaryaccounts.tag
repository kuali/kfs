<%--
 Copyright 2007-2009 The Kuali Foundation
 
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
              description="The DataDictionary entry containing attributes for this tag's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>

<c:set var="summaryItemAttributes" value="${DataDictionary.PurApSummaryItem.attributes}"/>

<kul:tab tabTitle="Account Summary" defaultOpen="false" tabErrorKey="${PurapConstants.ACCOUNT_SUMMARY_TAB_ERRORS}">

	<div class="tab-container" align="center" valign="middle">
        <c:if test="${!KualiForm.document.inquiryRendered}">
	        <div align="left">
	            Object Code and Sub-Object Code inquiries and descriptions have been removed because this is a prior year document.
            </div>
            <br>
        </c:if>
    
		<h3>Account Summary   <html:image property="methodToCall.refreshAccountSummary" src="${ConfigProperties.externalizable.images.url}tinybutton-refaccsum.gif" alt="refresh account summary"/></h3>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="view/edit pending entries">
			<logic:empty name="KualiForm" property="summaryAccounts">
				<h4>No Accounts</h4>
			</logic:empty>
			<logic:notEmpty name="KualiForm" property="summaryAccounts">
				<logic:iterate id="summaryAccount" name="KualiForm" property="summaryAccounts" indexId="ctr">		
				    <tr>
					    <td colspan="9" class="tab-subhead" style="border-right: none;">
					        Account Summary ${ctr+1} 
					    </td>
				    </tr>		
				
				    <tr>
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.chartOfAccountsCode}" hideRequiredAsterisk="true" scope="col"/>
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.accountNumber}" hideRequiredAsterisk="true" scope="col"/>
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.subAccountNumber}" hideRequiredAsterisk="true" scope="col"/>
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.financialObjectCode}" hideRequiredAsterisk="true" scope="col"/>
            	    	<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.financialSubObjectCode}" hideRequiredAsterisk="true" scope="col"/>
            		    <kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.projectCode}" hideRequiredAsterisk="true" scope="col"/>
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.organizationReferenceId}" hideRequiredAsterisk="true" scope="col"/>
 	    				<kul:htmlAttributeHeaderCell attributeEntry="${DataDictionary.DocumentHeader.attributes.organizationDocumentNumber}" hideRequiredAsterisk="true" scope="col"/>           		
                		<kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.amount}" hideRequiredAsterisk="true" scope="col"/>
			    	</tr>
								    		    
					<tr>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}" render="true">
								<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.chartOfAccountsCode"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&accountNumber=${summaryAccount.account.accountNumber}" render="true">
								<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.accountNumber"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.SubAccount" keyValues="chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&accountNumber=${summaryAccount.account.accountNumber}&subAccountNumber=${summaryAccount.account.subAccountNumber}" render="true">
								<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.subAccountNumber"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
						    <c:choose>
  						        <c:when test="${KualiForm.document.inquiryRendered}">
							        <kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ObjectCode" keyValues="financialObjectCode=${summaryAccount.account.financialObjectCode}&chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&universityFiscalYear=${KualiForm.document.postingYear}" render="true">
								        <bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.financialObjectCode"/>
							        </kul:inquiry>
							        &nbsp;
						        </c:when>
						        <c:otherwise>
						            <bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.financialObjectCode"/>&nbsp;
						        </c:otherwise>
						    </c:choose>	
						</td>
						<td class="datacell center">
						    <c:choose>
  						        <c:when test="${KualiForm.document.inquiryRendered}">
							        <kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.SubObjectCode" keyValues="accountNumber=${summaryAccount.account.accountNumber}&financialSubObjectCode=${summaryAccount.account.financialSubObjectCode}&financialObjectCode=${summaryAccount.account.financialObjectCode}&chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&universityFiscalYear=${KualiForm.document.postingYear}" render="true">
								        <bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.financialSubObjectCode"/>
							        </kul:inquiry>
							        &nbsp;
							    </c:when>
							    <c:otherwise>
							        <bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.financialSubObjectCode"/>&nbsp;
							    </c:otherwise>
							</c:choose>
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ProjectCode" keyValues="projectCode=${summaryAccount.account.projectCode}" render="true">
								<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.projectCode"/>
							</kul:inquiry>
							&nbsp;
						</td>
					    <td class="datacell center">
							<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.organizationReferenceId"/>&nbsp;
						</td>
					    <td class="datacell center">
							<bean:write name="KualiForm" property="document.documentHeader.organizationDocumentNumber"/>&nbsp;
						</td>
						<td class="datacell center"><div align="right">
							<bean:write name="KualiForm" property="summaryAccounts[${ctr}].account.amount"/>&nbsp;</div>
						</td> 
					</tr>
					
                    <tr>
                        <td colspan="9" height=30 style="padding: 20px;">
                            <div align="center">
                            <table width="75%" border="0" cellpadding="0" cellspacing="0" class="datatable">
                                <tr>
                                    <th colspan="3" style="padding: 0px; border-right: none; border-top: 1px solid #999999;">
                                        <div align="left">Items of Account Summary ${ctr+1} </div>
                                    </th>
                                </tr>            
                                <tr>
                                    <kul:htmlAttributeHeaderCell width="25%" >Item</kul:htmlAttributeHeaderCell>
                                    <kul:htmlAttributeHeaderCell width="50%" attributeEntry="${summaryItemAttributes.itemDescription}" />
                                    <kul:htmlAttributeHeaderCell width="25%" attributeEntry="${summaryItemAttributes.estimatedEncumberanceAmount}" />
                                </tr>           
                                <logic:iterate id="itemValue" name="KualiForm" property="summaryAccounts[${ctr}].items" indexId="ctrItem">
                                    <tr>
                                        <td width="25%" class="datacell center">
                                            <kul:htmlControlAttribute attributeEntry="${summaryItemAttributes.itemLineNumber}" property="summaryAccounts[${ctr}].items[${ctrItem}].itemIdentifierString" readOnly="true" />&nbsp;
                                        </td>	                                        
                                        <td width="50%" class="datacell center">
                                            <kul:htmlControlAttribute attributeEntry="${summaryItemAttributes.itemDescription}" property="summaryAccounts[${ctr}].items[${ctrItem}].itemDescription" readOnly="true" />&nbsp;
                                        </td>
                                        <td width="25%" class="datacell center">
                                            <kul:htmlControlAttribute attributeEntry="${summaryItemAttributes.estimatedEncumberanceAmount}" property="summaryAccounts[${ctr}].items[${ctrItem}].estimatedEncumberanceAmount" readOnly="true" />&nbsp;
                                        </td>
                                    </tr>
                                </logic:iterate>
                            </table>
                            </div>
                        </td>
                    </tr>

				</logic:iterate>
			</logic:notEmpty>
		</table>
	</div>
</kul:tab>
