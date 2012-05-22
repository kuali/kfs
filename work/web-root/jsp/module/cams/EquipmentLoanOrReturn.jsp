<%--
 Copyright 2007-2008 The Kuali Foundation
 
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


<kul:documentPage showDocumentInfo="true"
	htmlFormAction="camsEquipmentLoanOrReturn"
	documentTypeName="EquipmentLoanOrReturnDocument" renderMultipart="true"
	showTabButtons="true">

	<c:set var="equipAttributes" value="${DataDictionary.EquipmentLoanOrReturnDocument.attributes}" />
	<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>
	<c:set var="displayNewLoanTab" value="${KualiForm.editingMode['displayNewLoanTab']}" scope="request"/>
	<c:set var="displayReturnLoanFieldsReadOnly" value="${KualiForm.editingMode['displayReturnLoanFieldsReadOnly']}" scope="request"/>
	
	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
    <cams:viewAssetDetails defaultTabHide="false" /> 

	<kul:tab tabTitle="Equipment Loans" defaultOpen="true" tabErrorKey="document.borrowerUniversalIdentifier,document.borrowerPerson.principalName,document.loanDate,document.expectedReturnDate,document.loanReturnDate"> 
	    <div class="tab-container" align="center">
	      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
	      	<tr>
                <td colspan="4" class="tab-subhead">Equipment Loan Information</td>
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerUniversalIdentifier}" /></th>
				<c:choose>
					<c:when test="${!empty KualiForm.document.borrowerPerson.principalName and !displayNewLoanTab}">
						<td class="grid" width="25%"><c:out value="${KualiForm.document.borrowerPerson.principalName}"/>
			        </c:when>
			        <c:otherwise>
					   	<td class="grid" width="25%">
						   	<kul:checkErrors keyMatch="document.borrowerPerson.principalName,document.borrowerUniversalIdentifier" />
							<kul:user userIdFieldName="document.borrowerPerson.principalName" 
								userId="${KualiForm.document.borrowerPerson.principalName}" 
								universalIdFieldName="document.borrowerUniversalIdentifier" 
								universalId="${KualiForm.document.borrowerUniversalIdentifier}" 
								userNameFieldName="document.borrowerPerson.name" label="User" 
								userName="${KualiForm.document.borrowerPerson.name}"
								lookupParameters="document.borrowerPerson.principalName:principalName" 
								fieldConversions="principalName:document.borrowerPerson.principalName,principalId:document.borrowerUniversalIdentifier,name:document.borrowerPerson.name" 
								hasErrors="${hasErrors}" readOnly="${readOnly}" />
						</td>
					</c:otherwise>
				</c:choose>				
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.loanDate}" /></th>
			   	<c:choose>
		            <c:when test="${readOnly or !displayNewLoanTab}">
		                <td class="grid" width="25%"><kul:htmlControlAttribute attributeEntry="${equipAttributes.loanDate}" property="document.loanDate" readOnly="true" />
		            </c:when>
		            <c:otherwise>
				        <td class="grid" width="25%"><kul:dateInput attributeEntry="${equipAttributes.loanDate}" property="document.loanDate" /> </td>
		            </c:otherwise>
 		       </c:choose>
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.expectedReturnDate}" /></th>
			   	<c:choose>
		            <c:when test="${readOnly or displayReturnLoanFieldsReadOnly}">
		                <td class="grid" width="25%"><kul:htmlControlAttribute attributeEntry="${equipAttributes.expectedReturnDate}" property="document.expectedReturnDate" readOnly="true" />
		            </c:when>
		            <c:otherwise>
		                <td class="grid" width="25%"><kul:dateInput attributeEntry="${equipAttributes.expectedReturnDate}" property="document.expectedReturnDate" /> </td>
		            </c:otherwise>
 		       </c:choose>
			   	<c:choose>
	                <c:when test="${displayNewLoanTab or (empty KualiForm.document.loanReturnDate)}">
						<th class="grid" width="25%" align="right" colspan="2"></th>
		            </c:when>
		            <c:otherwise>
		    			<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.loanReturnDate}" /></th>	
		    			<c:choose>                
				            <c:when test="${readOnly}">
								<td class="grid" width="25%"><kul:htmlControlAttribute attributeEntry="${equipAttributes.loanReturnDate}" property="document.loanReturnDate" readOnly="true" />
				            </c:when>
				            <c:otherwise>
								<td class="grid" width="25%"><kul:dateInput attributeEntry="${equipAttributes.loanReturnDate}" property="document.loanReturnDate" /></td> 
				            </c:otherwise>
					    </c:choose>
				    </c:otherwise>
			    </c:choose>
       		</tr>		    
		</table>
		</div>
	</kul:tab>

	<kul:tab tabTitle="Borrower's Address" defaultOpen="true" tabErrorKey="document.borrowerA*,document.borrowerC*,document.borrowerZ*,document.borrowerP*,document.borrowerS*">
	         <!--Address,document.borrowerCityName,document.borrowerStateCode,document.borrowerZipCode,document.borrowerCountryCode,document.borrowerPhoneNumber,document.borrowerStorageStateCode,document.borrowerStorageZipCode,document.borrowerStorageCountryCode,document.borrowerStoragePhoneNumber" --> 

	    <div class="tab-container" align="center">
	      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
	      	<tr>
				<td class="tab-subhead"  width="50%" colspan="2">Borrower</td>
				<td class="tab-subhead"  width="50%" colspan="2">Stored at</td>			
			</tr>	
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerAddress}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerAddress" attributeEntry="${equipAttributes.borrowerAddress}" readOnly="${readOnly}" /></td>								
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerStorageAddress}" readOnly="true"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageAddress" attributeEntry="${equipAttributes.borrowerStorageAddress}" readOnly="${readOnly}" /></td>								
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerCityName}" /></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerCityName" attributeEntry="${equipAttributes.borrowerCityName}" readOnly="${readOnly}" /></td>		      	
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerStorageCityName}" readOnly="true"/></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageCityName" attributeEntry="${equipAttributes.borrowerStorageCityName}" readOnly="${readOnly}" /></td>		      	
			</tr>
			<tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerStateCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStateCode" attributeEntry="${equipAttributes.borrowerStateCode}" readOnly="${readOnly}" />								
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.rice.location.framework.state.StateEbo" fieldConversions="code:document.borrowerStateCode" lookupParameters="document.borrowerCountryCode:countryCode,document.borrowerStateCode:code" />
					</c:if>
                </td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerStorageStateCode}" readOnly="true"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageStateCode" attributeEntry="${equipAttributes.borrowerStorageStateCode}" readOnly="${readOnly}" />								
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.rice.location.framework.state.StateEbo" fieldConversions="code:document.borrowerStorageStateCode" lookupParameters="document.borrowerStorageCountryCode:countryCode,document.borrowerStorageStateCode:code" />
					</c:if>
                </td>
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerZipCode}" /></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerZipCode" attributeEntry="${equipAttributes.borrowerZipCode}" readOnly="${readOnly}" />
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.rice.location.framework.postalcode.PostalCodeEbo" fieldConversions="code:document.borrowerZipCode" lookupParameters="document.borrowerCountryCode:countryCode,document.borrowerZipCode:code,document.borrowerStateCode:stateCode" />
					</c:if>
		      	</td>		      	
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerStorageZipCode}" readOnly="true"/></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageZipCode" attributeEntry="${equipAttributes.borrowerStorageZipCode}" readOnly="${readOnly}" />
					<c:if test="${not readOnly}">
						&nbsp;
		                <kul:lookup boClassName="org.kuali.rice.location.framework.postalcode.PostalCodeEbo" fieldConversions="code:document.borrowerStorageZipCode" lookupParameters="document.borrowerStorageCountryCode:countryCode,document.borrowerStorageZipCode:code,document.borrowerStorageStateCode:stateCode" />
					</c:if>
		      	</td>		      	
		    </tr>		    
		    <tr>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerCountryCode}" /></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerCountryCode" attributeEntry="${equipAttributes.borrowerCountryCode}" readOnly="${readOnly}" />								
				</td>
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerStorageCountryCode}" readOnly="true"/></th>
				<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStorageCountryCode" attributeEntry="${equipAttributes.borrowerStorageCountryCode}" readOnly="${readOnly}" />								
				</td>
			</tr>
		    <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerPhoneNumber}" readOnly="true"/></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerPhoneNumber" attributeEntry="${equipAttributes.borrowerPhoneNumber}" readOnly="${readOnly}" /></td>		      	
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${equipAttributes.borrowerStoragePhoneNumber}" readOnly="true"/></th>
		      	<td class="grid" width="25%"><kul:htmlControlAttribute property="document.borrowerStoragePhoneNumber" attributeEntry="${equipAttributes.borrowerStoragePhoneNumber}" readOnly="${readOnly}" /></td>		      	
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
    <sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>

