<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
<c:set var="accountAttributes" value="${DataDictionary.Account.attributes}" />
<c:set var="assetPaymentAssetDetailAttributes" value="${DataDictionary.AssetPaymentAssetDetail.attributes}" />

<logic:iterate id="assetPaymentAssetDetail" name="KualiForm" property="document.assetPaymentAssetDetail" indexId="ctr">
		<c:out value="${ctr}"/>
		<c:set var="assetObject" value="document.assetPaymentAssetDetail[${ctr}].asset" />
		<c:set var="assetValue" value="${KualiForm.document.assetPaymentAssetDetail[ctr].asset}" />
	
	  	<html:hidden property="document.assetPaymentAssetDetail[${ctr}]."/>

		<table cellpadding="0" cellspacing="0" class="datatable" summary="AssetSummary" borders="0">
		      	<tr>
					<td class="subhead"  width="100%" colspan="7"><span class="subhead-left">
					${KualiForm.document.assetPaymentAssetDetail[ctr].capitalAssetNumber}</span></td>
				</tr>	
							
				<tr>
					<kul:htmlAttributeHeaderCell width="10%" align="center" attributeEntry="${assetAttributes.capitalAssetNumber}"/>
					<kul:htmlAttributeHeaderCell width="45%" align="center" attributeEntry="${assetAttributes.capitalAssetDescription}"/>
					<kul:htmlAttributeHeaderCell width="10%" align="center" attributeEntry="${accountAttributes.organizationCode}"/>
					<kul:htmlAttributeHeaderCell width="10%" align="center" attributeEntry="${assetPaymentAssetDetailAttributes.previousTotalCostAmount}"/>
			        <th class="grid" width="10%" align="center">Allocated</th>
			        <th class="grid" width="10%" align="center">New Total</th>
					<th class="grid" width="5%" align="center">Actions</th>			
				</tr>
		
			    <tr>
			      	<td class="grid">
			      		<kul:htmlControlAttribute property="document.assetPaymentAssetDetail[${ctr}].capitalAssetNumber" attributeEntry="${assetAttributes.capitalAssetNumber}" readOnly="true">
							<kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.Asset" keyValues="capitalAssetNumber=document.assetPaymentAssetDetail[${ctr}].capitalAssetNumber" render="true">
		              			<html:hidden write="true" property="document.assetPaymentAssetDetail[${ctr}].capitalAssetNumber" />
			           		</kul:inquiry>&nbsp;
			       		</kul:htmlControlAttribute>
			   		</td>
		   			
					<td class="grid" width="45%">
						<kul:htmlControlAttribute property="${assetObject}.capitalAssetDescription" attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true"/>
					</td>
					   		
			        <td class="grid" width="10%">
			        	<kul:htmlControlAttribute property="${assetObject}.organizationOwnerAccount.organizationCode" attributeEntry="${accountAttributes.organizationCode}" readOnly="true" readOnlyBody="true"/> 
				    </td>
				    
			        <th class="grid" width="10%" align="center">
				        <kul:htmlControlAttribute property="document.assetPaymentAssetDetail[${ctr}].previousTotalCostAmount" attributeEntry="${assetPaymentAssetDetailAttributes.previousTotalCostAmount}" readOnly="true"/>
			        </th>
			        
			        <th class="grid" width="10%" align="center">0</div></th>
			        <th class="grid" width="10%" align="center">0</div></th>       		
					<th class="datacell" rowspan="" nowrap="nowrap" width="5%">
			            <div align="center"><input name="methodToCall.deleteAssetPaymentAssetDetail.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" class="tinybutton" title="Delete Accounting Line 1" alt="Delete asset" type="image">
			        </th>
			    </tr>
		</table>

		<kul:tab tabTitle="Asset Information" defaultOpen="true"> 		    
			<div class="tab-container" align="center" id="tab-AssetInformation-div${ctrl}">
				<table cellpadding="0" cellspacing="0" class="datatable" summary="Asset" borders="0">
						<tr>
							<td class="total-line" colspan="12" style="padding: 0px;">		
					
								<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
									<tr>
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerAccountNumber}" readOnly="true" /></th>
										<td class="grid" width="25%">
											<kul:htmlControlAttribute property="${assetObject}.organizationOwnerAccountNumber" attributeEntry="${assetAttributes.organizationOwnerAccountNumber}" readOnly="true" readOnlyBody="true">								
												<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${assetValue.organizationOwnerChartOfAccountsCode}&amp;accountNumber=${assetValue.organizationOwnerAccountNumber}" render="true">
						                			<html:hidden write="true" property="${assetObject}.organizationOwnerAccountNumber" />
						                		</kul:inquiry>&nbsp;
						            		</kul:htmlControlAttribute>
								      	</td>
											
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true" /></th>
										<td class="grid" width="25%">
											<kul:htmlControlAttribute property="${assetObject}.organizationOwnerChartOfAccountsCode" attributeEntry="${assetAttributes.organizationOwnerChartOfAccountsCode}" readOnly="true" readOnlyBody="true">								
								      			<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=${assetValue.organizationOwnerChartOfAccountsCode}" render="true">
						                			<html:hidden write="true" property="${assetObject}.organizationOwnerChartOfAccountsCode" />
						                		</kul:inquiry>&nbsp;
						            		</kul:htmlControlAttribute>
								      	</td>
									</tr>			    
					
								    <tr>
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetTypeCode}" readOnly="true" /></th>
										<td class="grid" width="25%">
										   	<kul:htmlControlAttribute property="${assetObject}.capitalAssetTypeCode" attributeEntry="${assetAttributes.capitalAssetTypeCode}" readOnly="true" readOnlyBody="true"> 
												<kul:inquiry boClassName="org.kuali.kfs.module.cam.businessobject.AssetType" keyValues="capitalAssetTypeCode=${assetValue.capitalAssetTypeCode}" render="true">
								            		<html:hidden write="true" property="${assetObject}.capitalAssetTypeCode" />
								                </kul:inquiry>&nbsp;
								            </kul:htmlControlAttribute>
										 </td>
												 
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.vendorName}" readOnly="true" /></th>
										<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetObject}.vendorName" attributeEntry="${assetAttributes.vendorName}" readOnly="true"/></td>								
								    </tr>
								            
								    <tr>
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetInServiceDate}" readOnly="true" /></th>
										<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetObject}.capitalAssetInServiceDate" attributeEntry="${assetAttributes.capitalAssetInServiceDate}" readOnly="true"/></td>								
							
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.manufacturerName}" readOnly="true" /></th>
										<td class="grid" width="25%"><kul:htmlControlAttribute property="${assetObject}.manufacturerName" attributeEntry="${assetAttributes.manufacturerName}" readOnly="true"/></td>		
								    </tr>
								    <tr>
										<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true" /></th>
										<td class="grid" width="75%" colspan="3" ><kul:htmlControlAttribute property="${assetObject}.capitalAssetDescription" attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true"/></td>							
								    </tr>	            			            
								</table>
						  </td>
						<tr/>
				</table>
			</div>
		</kul:tab>
</logic:iterate>

