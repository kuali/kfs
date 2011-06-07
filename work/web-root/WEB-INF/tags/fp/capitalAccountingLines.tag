<%--
 Copyright 2005-2009 The Kuali Foundation
 
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
<%@ tag description="render the accounting lines for capitalization tag that contains the given accounting lines for capitalization info"%>
<%@ attribute name="readOnly" required="false" description="Whether the accounting lines for capitalization information should be read only" %>
<c:set var="capitalAccountingLinesAttributes" value="${DataDictionary.CapitalAccountingLines.attributes}"/>

<kul:tab tabTitle="Accounting Lines for Capitalization" defaultOpen="true" tabErrorKey="${KFSConstants.EDIT_CAPITAL_ASSET_INFORMATION_ERRORS}" >
     <div class="tab-container" align="center">
     	<h3>Accounting Lines for Capitalization</h3>
     	<table cellpadding="0" cellspacing="0" summary="Accounting Lines for Capitalization">
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.sequenceNumber}"
					useShortLabel="true"
					/>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.lineType}"
					useShortLabel="true"
					/>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.chartOfAccountsCode}"
					useShortLabel="true"
					hideRequiredAsterisk="true"
					/>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.accountNumber}"
					useShortLabel="false"
					/>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.subAccountNumber}"
					useShortLabel="true"
					/>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.financialObjectCode}"
					useShortLabel="true"
					/>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.financialSubObjectCode}"
					useShortLabel="true"
					/>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.projectCode}"
					useShortLabel="true"
					/>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.amount}"
					useShortLabel="false"
					/>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.financialDocumentLineDescription}"
					useShortLabel="true"
					/>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.selectLine}"
					useShortLabel="false"
					/>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${capitalAccountingLinesAttributes.distributionAmountCode}"
					useShortLabel="false"
					/>
				<kul:htmlAttributeHeaderCell literalLabel="Actions"/>					
			</tr>
   	        <logic:iterate id="capitalAccountingLinesCollection" name="KualiForm" property="capitalAccountingLines" indexId="ctr">
		    	<tr>
		            <td class="datacell">
						<div align="center">
							<kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.sequenceNumber}" property="capitalAccountingLines[${ctr}].sequenceNumber" readOnly="true"/>					
						</div>		            
		            </td>
		            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.lineType}" property="capitalAccountingLines[${ctr}].lineType" readOnly="true"/></td>
		            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.chartOfAccountsCode}" property="capitalAccountingLines[${ctr}].chartOfAccountsCode" readOnly="true"/></td>
		            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.accountNumber}" property="capitalAccountingLines[${ctr}].accountNumber" readOnly="true"/></td>
		            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.subAccountNumber}" property="capitalAccountingLines[${ctr}].subAccountNumber" readOnly="true"/></td>
		            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.financialObjectCode}" property="capitalAccountingLines[${ctr}].financialObjectCode" readOnly="true"/></td>
		            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.financialSubObjectCode}" property="capitalAccountingLines[${ctr}].financialSubObjectCode" readOnly="true"/></td>
		            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.projectCode}" property="capitalAccountingLines[${ctr}].projectCode" readOnly="true"/></td>
		            <td class="datacell">
		            	<div align="right">
		            		<kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.amount}" property="capitalAccountingLines[${ctr}].amount" readOnly="true"/>
						</div>		            		
		            </td>
		            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.financialDocumentLineDescription}" property="capitalAccountingLines[${ctr}].financialDocumentLineDescription" readOnly="true"/></td>
                    <td class="datacell">
                    	<div align="center">
							<kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.selectLine}" property="capitalAccountingLines[${ctr}].selectLine"/>                    	
                    	</div>
                    </td>
                    <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.distributionAmountCode}" property="capitalAccountingLines[${ctr}].distributionAmountCode" readOnly="false"/></td>
                    <td class="datacell">
                    	<div align="center">
                    		<html:image property="methodToCall.createAsset.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-createasset.gif" alt="Create Asset Details" title="Create Asset Details" styleClass="tinybutton"/>
							<html:image property="methodToCall.modifyAsset.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-modifyasset.gif" alt="Modify Asset Details" title="Modify Asset Details" styleClass="tinybutton"/>                    		
                    	</div>
                    </td>
		        </tr>
		   </logic:iterate>
     	</table>	 
	 </div>	
</kul:tab>	 
