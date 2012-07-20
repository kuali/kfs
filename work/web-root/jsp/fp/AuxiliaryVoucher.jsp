<%--
 Copyright 2005-2006 The Kuali Foundation
 
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp" %>

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialAuxiliaryVoucher" documentTypeName="AuxiliaryVoucherDocument" renderMultipart="true" showTabButtons="true">
		<%-- derive displayReadOnly value --%>
		<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

  	   	<SCRIPT type="text/javascript">
		<!--
		    function submitForChangedType() {
        		document.forms[0].submit();
		    }
		//-->
	    </SCRIPT>

		
        <sys:documentOverview editingMode="${KualiForm.editingMode}"/>
		<!-- AUXILIARY VOUCHER SPECIFIC FIELDS -->
		<kul:tab tabTitle="Auxiliary Voucher Details" defaultOpen="true" tabErrorKey="${KFSConstants.EDIT_AUXILIARY_VOUCHER_ERRORS}" >
	    	
	    	<div class="tab-container" align="center">
		<h3>Auxiliary Voucher Details</h3>
	    	<table cellpadding="0" class="datatable" summary="view/edit ad hoc recipients">
            <tbody>
              <tr>
                <th width="35%" class="bord-l-b">
                  <div align="right">
                    <kul:htmlAttributeLabel labelFor="selectedAccountingPeriod" attributeEntry="${DataDictionary.AuxiliaryVoucherDocument.attributes.accountingPeriod}" useShortLabel="false" />
                  </div>
                </th>
                <td class="datacell-nowrap">
               		<c:if test="${readOnly}">
                        ${KualiForm.accountingPeriod.universityFiscalPeriodName}
					</c:if>
                	<c:if test="${!readOnly}">
						<html:select property="selectedAccountingPeriod"
						 onchange ="submitForChangedType()">
					 		<html:options property="accountingPeriodCompositeValueList" labelProperty="accountingPeriodLabelList"  />
                	 	</html:select>
               		</c:if>
                </td>
              </tr>
              <tr>
                  <th width="35%" class="bord-l-b">
                      <div align="right">
                          <kul:htmlAttributeLabel attributeEntry="${DataDictionary.AuxiliaryVoucherDocument.attributes.typeCode}" useShortLabel="false" />
                      </div>
                  </th>
                  <td class="datacell-nowrap">
         	          <kul:htmlControlAttribute
					    	attributeEntry="${DataDictionary.AuxiliaryVoucherDocument.attributes.typeCode}"
                            property="document.typeCode"
                            readOnly="${readOnly}" 
                            readOnlyAlternateDisplay="${fn:escapeXml(KualiForm.formattedAuxiliaryVoucherType)}" 
                            onchange="submitForChangedType()"/>          
						<NOSCRIPT>
    						<html:submit value="select" alt="press this button to refresh the page after changing the voucher type." />
						</NOSCRIPT>                
                  </td>
              </tr>
              <c:choose>
                  <c:when test="${empty KualiForm.document.typeCode || KualiForm.document.typeCode == KFSConstants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE}">
                  </c:when>                  
                  <c:otherwise>
                      <c:set var="reversalReadOnly" value="${readOnly}"/>
                      <c:if test="${!reversalReadOnly}">  <!--  if we're already readOnly b/c of authz permissions, then we want to stay that way -->
	                      <c:if test="${KualiForm.document.typeCode == KFSConstants.AuxiliaryVoucher.RECODE_DOC_TYPE}">
						      <c:set var="reversalReadOnly" value="true"/>
	                      </c:if>
	                  </c:if>
                      <tr>
                          <kul:htmlAttributeHeaderCell
                                  attributeEntry="${DataDictionary.AuxiliaryVoucherDocument.attributes.reversalDate}"
                                  horizontal="true"
                                  width="35%"
                                  />
                          <td class="datacell-nowrap">
                              <kul:htmlControlAttribute
                                      attributeEntry="${DataDictionary.AuxiliaryVoucherDocument.attributes.reversalDate}"
                                      datePicker="true"
                                      property="document.reversalDate"
                                      readOnly="${reversalReadOnly}"
                                      readOnlyAlternateDisplay="${fn:escapeXml(KualiForm.formattedReversalDate)}"
                                      />
                          </td>
                      </tr>
                  </c:otherwise>
              </c:choose>
            </tbody>
          </table>
	    	</div>
		</kul:tab>
        <kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
			<sys-java:accountingLines>
				<sys-java:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
			</sys-java:accountingLines>
		</kul:tab>
		<gl:generalLedgerPendingEntries/>

		<kul:notes/>
						
		<kul:adHocRecipients />
			
		<kul:routeLog/>

		<kul:superUserActions />

		<kul:panelFooter/>

		<sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" extraButtons="${KualiForm.extraButtons}"/>

</kul:documentPage>
