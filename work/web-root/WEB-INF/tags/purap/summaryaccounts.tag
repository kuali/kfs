<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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
			            <th height=30 colspan="10">&nbsp;</th>
		            </tr>
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
							<kul:inquiry boClassName="org.kuali.core.bo.Options" keyValues="universityFiscalYear=${summaryAccount.postingYear}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.postingYear" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.Chart" keyValues="chartOfAccountsCode=${summaryAccount.chartOfAccountsCode}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.chartOfAccountsCode" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.Account" keyValues="chartOfAccountsCode=${summaryAccount.chartOfAccountsCode}&accountNumber=${summaryAcount.accountNumber}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.accountNumber" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.SubAccount" keyValues="chartOfAccountsCode=${summaryAccount.chartOfAccountsCode}&accountNumber=${summaryAccount.accountNumber}&subAccountNumber=${summaryAccount.subAccountNumber}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.subAccountNumber" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.ObjectCode" keyValues="financialObjectCode=${summaryAccount.financialObjectCode}&chartOfAccountsCode=${summaryAccount.chartOfAccountsCode}&postingYear=${summaryAccount.postingYear}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.financialObjectCode" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.SubObjCd" keyValues="financialSubObjectCode=${summaryAccount.financialSubObjectCode}&financialObjectCode=${summaryAccount.financialObjectCode}&chartOfAccountsCode=${summaryAccount.chartOfAccountsCode}&postingYear=${summaryAccount.postingYear}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.financialSubObjectCode" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
						<td class="datacell center">
							<kul:inquiry boClassName="org.kuali.module.chart.bo.ProjectCode" keyValues="projectCode=${summaryAccount.projectCode}" render="true">
								<html:hidden property="summaryAccounts[${ctr}].account.projectCode" write="true"/>
							</kul:inquiry>
							&nbsp;
						</td>
					    <td class="datacell center"><c:out value="${document.summaryAccountsWithItemsKey[ctr].organizationReferenceId}"/>&nbsp;</td>
					    <td class="datacell center"><c:out value="${document.documentHeader.organizationDocumentNumber}"/>&nbsp;</td>
						<td class="datacell center"><div align="right"><html:hidden property="summaryAccounts[${ctr}].account.amount" write="true"/>&nbsp;</div></td> 
					</tr>
					
				    <tr>
					    <td colspan="10" class="tab-subhead" style="border-right: none;">
					        Items of Account Summary ${ctr+1} 
					    </td>
					</tr>

			        <tr>
				        <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
				        <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTypeCode}" />
				        <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}" />
				        <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" />
				        <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
				        <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />
				        <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitPrice}" />
				        <kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" />
  			        </tr>	
					<tr>				
					    <logic:iterate id="itemValue" name="KualiForm" property="summaryAccounts[${ctr}].items" indexId="ctrItem">
					        <tr>
					            <td class="datacell center"><c:out value="${itemValue.itemLineNumber}"/>&nbsp;</td>
					            <td class="datacell center"><c:out value="${itemValue.itemTypeCode}"/>&nbsp;</td>
					            <td class="datacell center"><c:out value="${itemValue.itemQuantity}"/>&nbsp;</td>
					            <td class="datacell center"><c:out value="${itemValue.itemUnitOfMeasureCode}"/>&nbsp;</td>
					            <td class="datacell center"><c:out value="${itemValue.itemCatalogNumber}"/>&nbsp;</td>
                                <td class="datacell center"><c:out value="${itemValue.itemDescription}"/>&nbsp;</td>
                                <td class="datacell center"><c:out value="${itemValue.itemUnitPrice}"/>&nbsp;</td>
                                <td class="datacell center"><c:out value="${itemValue.extendedPriceForAccountSummary}"/>&nbsp;</td>
                            </tr>
                        </logic:iterate>
                    </tr>

				</logic:iterate>
			</logic:notEmpty>
		</table>
	</div>
</kul:tab>