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
<%@ tag description="render accounting lines for capitalization tag that contains the given accounting lines for capitalization info"%>
<%@ attribute name="readOnly" required="false" description="Whether the accounting lines for capitalization information should be read only" %>
<c:set var="capitalAccountingLinesAttributes" value="${DataDictionary.CapitalAccountingLines.attributes}"/>
<c:set var="capitalAccountingLines" value="${KualiForm.document.capitalAccountingLines}" />
<c:set var="capitalAccountingLineAttributes" value="${DataDictionary.CapitalAccountingLine.attributes}"/>

<c:set var="defaultOpen" value="false"/>
<c:if test="${!empty KualiForm.document.capitalAccountingLines and KualiForm.editCreateOrModify}" >
	<c:set var="defaultOpen" value="true"/>
</c:if>

<kul:tab tabTitle="${KFSConstants.CapitalAssets.ACCOUNTING_LINES_FOR_CAPITALIZATION_TAB_TITLE}" defaultOpen="${defaultOpen}" tabErrorKey="${KFSConstants.EDIT_ACCOUNTING_LINES_FOR_CAPITALIZATION_ERRORS}" >
     <div class="tab-container" align="center">
     	<h3>Accounting Lines for Capitalization</h3>
	    <c:if test="${empty KualiForm.document.capitalAccountingLines}">
		    <table cellpadding="0" cellspacing="0" summary="Accounting Lines for Capitalization">
		    	<tr>
					<td class="datacell" height="50" colspan="11"><div align="center">There are currently no Accounting lines for capitalization entries associated with this Transaction Processing document.</div></td>
				</tr>
		    </table>
	    </c:if>
		<c:if test="${not empty KualiForm.document.capitalAccountingLines}">
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
						attributeEntry="${capitalAccountingLinesAttributes.accountLinePercent}"
						useShortLabel="true"
						/>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${capitalAccountingLinesAttributes.financialDocumentLineDescription}"
						useShortLabel="false"
						/>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${capitalAccountingLinesAttributes.selectLine}"
						useShortLabel="false"
						/>
				</tr>
				
				<logic:iterate id="capitalAccountingLinesCollection" name="KualiForm" property="document.capitalAccountingLines" indexId="ctr">
					<bean:define id="amountDistributed" name="capitalAccountingLinesCollection" property="amountDistributed"/>
					<c:set var="lineReadOnly" value="false"/>
	                <c:if test="${amountDistributed == true}" >
	                	<c:set var="lineReadOnly" value="true"/>
	               	</c:if>

			    	<tr>
			            <td class="datacell">
							<div align="center">
								<kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.sequenceNumber}" property="document.capitalAccountingLines[${ctr}].sequenceNumber" readOnly="true"/>					
							</div>		            
			            </td>
			            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.lineType}" property="document.capitalAccountingLines[${ctr}].lineType" readOnly="true"/></td>
			            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.chartOfAccountsCode}" property="document.capitalAccountingLines[${ctr}].chartOfAccountsCode" readOnly="true"/></td>
			            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.accountNumber}" property="document.capitalAccountingLines[${ctr}].accountNumber" readOnly="true"/></td>
			            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.subAccountNumber}" property="document.capitalAccountingLines[${ctr}].subAccountNumber" readOnly="true"/></td>
			            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.financialObjectCode}" property="document.capitalAccountingLines[${ctr}].financialObjectCode" readOnly="true"/></td>
			            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.financialSubObjectCode}" property="document.capitalAccountingLines[${ctr}].financialSubObjectCode" readOnly="true"/></td>
			            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.projectCode}" property="document.capitalAccountingLines[${ctr}].projectCode" readOnly="true"/></td>
			            <td class="datacell">
			            	<div align="right">
			            		<kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.amount}" property="document.capitalAccountingLines[${ctr}].amount" readOnly="true"/>
							</div>		            		
			            </td>
			            <td class="datacell">
			            	<div align="right">
			            		<kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.accountLinePercent}" property="document.capitalAccountingLines[${ctr}].accountLinePercent" readOnly="true"/>
							</div>		            		
			            </td>
			            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.financialDocumentLineDescription}" property="document.capitalAccountingLines[${ctr}].financialDocumentLineDescription" readOnly="true"/></td>
	                    <td class="datacell">
	                    	<div align="center">
								<kul:htmlControlAttribute attributeEntry="${capitalAccountingLinesAttributes.selectLine}" property="document.capitalAccountingLines[${ctr}].selectLine" readOnly="${readOnly}" disabled="${lineReadOnly}"/>                    	
	                    	</div>
	                    </td>
			        </tr>
				</logic:iterate>
				<c:if test="${!empty KualiForm.document.capitalAccountingLines}">
					<c:if test="${!readOnly}">
						<tr height="40">
							<td colSpan="12">
							<div align="center"><b>Select Amount Distribution Method&nbsp;</b>
									<kul:htmlControlAttribute
									attributeEntry="${capitalAccountingLineAttributes.distributionCode}"
									property="capitalAccountingLine.distributionCode"
									readOnly="${readOnly}"/>
								</div>
							</td>
						</tr>
					</c:if>
					<c:if test="${!readOnly}">
						<tr height="40">
				            <td class="datacell" colSpan="12">
				            	<div align="center">
				            		<c:if test="${KualiForm.capitalAccountingLine.canCreateAsset}">
				                		<html:image property="methodToCall.createAsset" src="${ConfigProperties.externalizable.images.url}tinybutton-createasset.gif" alt="Create Asset Details" title="Create Asset Details" styleClass="tinybutton"/>
									</c:if>
									<html:image property="methodToCall.modifyAsset" src="${ConfigProperties.externalizable.images.url}tinybutton-modifyasset.gif" alt="Modify Asset Details" title="Modify Asset Details" styleClass="tinybutton"/>                    		
				                 </div>
				            </td>
						</tr>
					</c:if>					
				</c:if>
	     	</table>	 
		</c:if>     	
	 </div>	
</kul:tab>	 
