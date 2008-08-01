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


<kul:documentPage showDocumentInfo="true"
	htmlFormAction="camsEquipmentLoanOrReturn"
	documentTypeName="EquipmentLoanOrReturnDocument" renderMultipart="true"
	showTabButtons="true">

	<c:set var="eqipAttributes" value="${DataDictionary.EquipmentLoanOrReturnDocument.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
	<c:set var="readOnly" value="${!empty KualiForm.editingMode['viewOnly']}" />
	<c:set var="displayNewLoanTab" value="${KualiForm.editingMode['displayNewLoanTab']}" scope="request"/>
	
	<kfs:hiddenDocumentFields isTransactionalDocument="true" isFinancialDocument="false"/>
	<html:hidden property="document.capitalAssetNumber" />
	<html:hidden property="document.documentNumber" />
	<html:hidden property="document.versionNumber" />	
	<html:hidden property="document.newLoan" />	

    <kfs:documentOverview editingMode="${KualiForm.editingMode}" />
    <cams:viewAssetDetails defaultTabHide="false" /> 

	<kul:tab tabTitle="Equipment Loan" defaultOpen="true" tabErrorKey="document.borrowerUniversalIdentifier,document.borrowerUniversalUser.personUserIdentifier,document.loanDate,document.expectedReturnDate,document.loanReturnDate"> 
	    <div class="tab-container" align="center">
	      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
	      	<tr>
                <td colspan="4" class="tab-subhead">Equipment Loan Information</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerUniversalIdentifier}" /></th>
		      	<td class="grid" width="25%">
		      	
		      	<!-- kul:checkErrors keyMatch="document.borrowerUniversalUser.personUserIdentifier" / -->
		      	
				<kul:user userIdFieldName="document.borrowerUniversalUser.personUserIdentifier" 
					      userId="${KualiForm.document.borrowerUniversalUser.personUserIdentifier}" 
				          universalIdFieldName="document.borrowerUniversalIdentifier" 
					      universalId="${KualiForm.document.borrowerUniversalIdentifier}" 
				          userNameFieldName="document.borrowerUniversalUser.personName" label="User" 
					      userName="${KualiForm.document.borrowerUniversalUser.personName}"
 						  renderOtherFields="true"						  
 						  lookupParameters="document.borrowerUniversalUser.personUserIdentifier:personUserIdentifier" 
						  fieldConversions="personUserIdentifier:document.borrowerUniversalUser.personUserIdentifier,personUniversalIdentifier:document.borrowerUniversalIdentifier,personName:document.borrowerUniversalUser.personName" 
						  hasErrors="${hasErrors}" readOnly="${readOnly}" />
				</td>

				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.loanDate}" readOnly="true"/></th>
			   	<c:choose>
		            <c:when test="${readOnly or !displayNewLoanTab}">
		                <td class="grid" width="25%"><kul:htmlControlAttribute attributeEntry="${eqipAttributes.loanDate}" property="document.loanDate" readOnly="true" />
		            </c:when>
		            <c:otherwise>
				        <td class="grid" width="25%"><kul:dateInput attributeEntry="${eqipAttributes.loanDate}" property="document.loanDate" /> </td>
		            </c:otherwise>
 		       </c:choose>
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.expectedReturnDate}" /></th>
			   	<c:choose>
		            <c:when test="${readOnly}">
		                <td class="grid" width="25%"><kul:htmlControlAttribute attributeEntry="${eqipAttributes.expectedReturnDate}" property="document.expectedReturnDate" readOnly="true" />
		            </c:when>
		            <c:otherwise>
		                <td class="grid" width="25%"><kul:dateInput attributeEntry="${eqipAttributes.expectedReturnDate}" property="document.expectedReturnDate" /> </td>
		            </c:otherwise>
 		       </c:choose>

			   	<c:choose>
	                <c:when test="${displayNewLoanTab or (empty KualiForm.document.loanReturnDate)}">
						<th class="grid" width="25%" align="right" colspan="2"></th>
		            </c:when>
		            <c:otherwise>
		    			<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.loanReturnDate}" /></th>	
		    			<c:choose>                
				            <c:when test="${readOnly}">
								<td class="grid" width="25%"><kul:htmlControlAttribute attributeEntry="${eqipAttributes.loanReturnDate}" property="document.loanReturnDate" readOnly="true" />
				            </c:when>
				            <c:otherwise>
								<td class="grid" width="25%"><kul:dateInput attributeEntry="${eqipAttributes.loanReturnDate}" property="document.loanReturnDate" /></td> 
				            </c:otherwise>
					    </c:choose>
				    </c:otherwise>
			    </c:choose>

       		</tr>		    
		</table>
		</div>
	</kul:tab>

	<kul:tab tabTitle="Borrower's Address" defaultOpen="true" 
	         tabErrorKey="document.borrowerAddress,document.borrowerCityName,document.borrowerStateCode,document.borrowerZipCode,document.borrowerCountryCode,document.borrowerPhoneNumber,document.borrowerStorageStateCode,document.borrowerStorageZipCode,document.borrowerStorageCountryCode,document.borrowerStoragePhoneNumber"> 

	    <div class="tab-container" align="center">
	      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
	      	<tr>
				<td class="tab-subhead"  width="50%" colspan="2">Borrower</td>
				<td class="tab-subhead"  width="50%" colspan="2">Stored at</td>			
			</tr>	
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerAddress}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerAddress" attributeEntry="${eqipAttributes.borrowerAddress}" readOnly="${readOnly}" /></td>								
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStorageAddress}" readOnly="true"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageAddress" attributeEntry="${eqipAttributes.borrowerStorageAddress}" readOnly="${readOnly}" /></td>								
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerCityName}" /></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerCityName" attributeEntry="${eqipAttributes.borrowerCityName}" readOnly="${readOnly}" /></td>		      	
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStorageCityName}" readOnly="true"/></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageCityName" attributeEntry="${eqipAttributes.borrowerStorageCityName}" readOnly="${readOnly}" /></td>		      	
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStateCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStateCode" attributeEntry="${eqipAttributes.borrowerStateCode}" readOnly="${readOnly}" />								
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.State" fieldConversions="postalStateCode:document.borrowerStateCode" lookupParameters="document.borrowerStateCode:postalStateCode" />
					</c:if>
                </td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStorageStateCode}" readOnly="true"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageStateCode" attributeEntry="${eqipAttributes.borrowerStorageStateCode}" readOnly="${readOnly}" />								
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.State" fieldConversions="postalStateCode:document.borrowerStorageStateCode" lookupParameters="document.borrowerStorageStateCode:postalStateCode" />
					</c:if>
                </td>
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerZipCode}" /></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerZipCode" attributeEntry="${eqipAttributes.borrowerZipCode}" readOnly="${readOnly}" />
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.PostalZipCode" fieldConversions="postalZipCode:document.borrowerZipCode" lookupParameters="document.borrowerZipCode:postalZipCode,document.borrowerStateCode:postalStateCode" />
					</c:if>
		      	</td>		      	
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStorageZipCode}" readOnly="true"/></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageZipCode" attributeEntry="${eqipAttributes.borrowerStorageZipCode}" readOnly="${readOnly}" />
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.PostalZipCode" fieldConversions="postalZipCode:document.borrowerStorageZipCode" lookupParameters="document.borrowerStorageZipCode:postalZipCode,document.borrowerStorageStateCode:postalStateCode" />
					</c:if>
		      	</td>		      	
		    </tr>		    
		    <tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerCountryCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerCountryCode" attributeEntry="${eqipAttributes.borrowerCountryCode}" readOnly="${readOnly}" />								
				</td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStorageCountryCode}" readOnly="true"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageCountryCode" attributeEntry="${eqipAttributes.borrowerStorageCountryCode}" readOnly="${readOnly}" />								
				</td>
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerPhoneNumber}" readOnly="true"/></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerPhoneNumber" attributeEntry="${eqipAttributes.borrowerPhoneNumber}" readOnly="${readOnly}" /></td>		      	
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${eqipAttributes.borrowerStoragePhoneNumber}" readOnly="true"/></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStoragePhoneNumber" attributeEntry="${eqipAttributes.borrowerStoragePhoneNumber}" readOnly="${readOnly}" /></td>		      	
		    </tr>		    
		  </table>   
        </div>
	  </kul:tab>
    
    <cams:assetLocation defaultTabHide="true" />  
	<cams:organizationInfo defaultTabHide="true"/>

	<cams:viewPayments defaultTabHide="true" assetPayments="${KualiForm.document.asset.assetPayments}" />	
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />
    <kfs:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
