<%--
 Copyright 2007 The Kuali Foundation.
 
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
              description="The DataDictionary entry containing attributes for this tag's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>

<kul:tab tabTitle="Account Summary" defaultOpen="false" tabErrorKey="${PurapConstants.ACCOUNT_SUMMARY_TAB_ERRORS}">
	<div class="tab-container" align="center" valign="middle">
		<div class="h2-container">
			<h2>Account Summary   <html:image property="methodToCall.refreshAccountSummary" src="${ConfigProperties.externalizable.images.url}tinybutton-refaccsum.gif" alt="refresh account summary"/></h2>
		</div>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="view/edit pending entries">
			<logic:empty name="KualiForm" property="summaryAccounts">
				<h3>No Accounts</h3>
			</logic:empty>
			<logic:notEmpty name="KualiForm" property="summaryAccounts">
				<logic:iterate id="summaryAccount" name="KualiForm" property="summaryAccounts" indexId="ctr">		
				    <tr>
					    <td colspan="10" class="tab-subhead" style="border-right: none;">
					        Account Summary ${ctr+1} 
					    </td>
				    </tr>		
				
				    <tr>
            		    <kul:htmlAttributeHeaderCell attributeEntry="${documentAttributes.postingYear}" hideRequiredAsterisk="true" scope="col"/>
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
							<kul:inquiry boClassName="org.kuali.core.bo.Options" keyValues="universityFiscalYear=${summaryAccount.account.postingYear}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.postingYear" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.Chart" keyValues="chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.chartOfAccountsCode" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.Account" keyValues="chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&accountNumber=${summaryAccount.account.accountNumber}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.accountNumber" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.SubAccount" keyValues="chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&accountNumber=${summaryAccount.account.accountNumber}&subAccountNumber=${summaryAccount.account.subAccountNumber}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.subAccountNumber" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.ObjectCode" keyValues="financialObjectCode=${summaryAccount.account.financialObjectCode}&chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&postingYear=${summaryAccount.account.postingYear}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.financialObjectCode" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.SubObjCd" keyValues="financialSubObjectCode=${summaryAccount.account.financialSubObjectCode}&financialObjectCode=${summaryAccount.account.financialObjectCode}&chartOfAccountsCode=${summaryAccount.account.chartOfAccountsCode}&postingYear=${summaryAccount.account.postingYear}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.financialSubObjectCode" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.ProjectCode" keyValues="projectCode=${summaryAccount.account.projectCode}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.projectCode" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
					    <td class="datacell center"><html:hidden property="summaryAccounts[${ctr}].account.organizationReferenceId" write="true"/>&nbsp;</td>
					    <td class="datacell center"><html:hidden property="document.documentHeader.organizationDocumentNumber" write="true"/>&nbsp;</td>
						<td class="datacell center"><div align="right"><html:hidden property="summaryAccounts[${ctr}].account.amount" write="true"/>&nbsp;</div></td> 
					</tr>
					
                    <tr>
                        <td colspan="10" height=30 style="padding: 20px;">
                            <div align="center">
                            <table width="75%" border="0" cellpadding="0" cellspacing="0" class="datatable">
                                <tr>
                                    <th colspan="4" style="padding: 0px; border-right: none; border-top: 1px solid #999999;">
                                        <div align="left">Items of Account Summary ${ctr+1} </div>
                                    </th>
                                </tr>
            
                                <tr>
                                    <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
                                    <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTypeCode}" />
                                    <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />
                                    <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.estimatedEncumberanceAmount}" />
                                </tr>   
                                <tr>                
                                    <logic:iterate id="itemValue" name="KualiForm" property="summaryAccounts[${ctr}].items" indexId="ctrItem">
                                        <tr>
                                            <td class="datacell center">
                                                <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemLineNumber}" property="summaryAccounts[${ctr}].items[${ctrItem}].itemLineNumber" readOnly="true" />&nbsp;
                                            </td>
                                            <td class="datacell center">
                                                <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemTypeCode}" 
                                                	property="summaryAccounts[${ctr}].items[${ctrItem}].itemTypeCode"
                                                	extraReadOnlyProperty="summaryAccounts[${ctr}].items[${ctrItem}].itemType.itemTypeDescription" 
                                                	readOnly="true" />&nbsp;
                                            </td>
                                            <td class="datacell center">
                                                <kul:htmlControlAttribute attributeEntry="${itemAttributes.itemDescription}" property="summaryAccounts[${ctr}].items[${ctrItem}].itemDescription" readOnly="true" />&nbsp;
                                            </td>
                                            <td class="datacell center">
                                                <kul:htmlControlAttribute attributeEntry="${itemAttributes.estimatedEncumberanceAmount}" property="summaryAccounts[${ctr}].items[${ctrItem}].estimatedEncumberanceAmount" readOnly="true" />&nbsp;
                                            </td>
                                        </tr>
                                    </logic:iterate>
                                </tr>
                            </table>
                            </div>
                        </td>
                    </tr>

				</logic:iterate>
			</logic:notEmpty>
		</table>
	</div>
</kul:tab>