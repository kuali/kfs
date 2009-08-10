--p kfsmi-3932
update krim_perm_t
set desc_txt = 'Allows access to the Electronic Fund Transfer interface for the claiming of electronic funds using the Cash Control Document (CTRL).'
where nmspc_cd = 'KFS-AR'
and nm = 'Claim Electronic Payment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Claim Electronic Payment');
update krim_perm_t
set desc_txt = 'Allows modification of the Customer Tax Number field on the Customer document.'
where nmspc_cd = 'KFS-AR'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Edit permission for the Requisition document prior to the document being submitted.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Edit Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Edit Document');
update krim_perm_t
set desc_txt = 'Edit permission for the Purchase Order document prior to the document being submitted.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Edit Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Edit Document');
update krim_perm_t
set desc_txt = 'Edit permission for Accounts Payable Transactional  documents  prior to the document being submitted.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Edit Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Edit Document');
update krim_perm_t
set desc_txt = 'Edit permission for Receiving Transactional documents  prior to the document being submitted.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Edit Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Edit Document');
update krim_perm_t
set desc_txt = 'Allows access to the Electronic Fund Transfer interface for the claiming of electronic funds.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Claim Electronic Payment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Claim Electronic Payment');
update krim_perm_t
set desc_txt = 'Allows access to the Error Correction button on KFS Transactional documents.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Error Correct Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Error Correct Document');
update krim_perm_t
set desc_txt = 'Authorizes users to take the Approve action on KFS documents Ad Hoc routed to them.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Ad Hoc Review Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Ad Hoc Review Document');
update krim_perm_t
set desc_txt = 'Allows access to the Customer Upload Batch Upload page.'
where nmspc_cd = 'KFS-AR'
and nm = 'Upload Batch Input File(s)'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Upload Batch Input File(s)');
update krim_perm_t
set desc_txt = 'Authorizes users to take the FYI action on KFS documents Ad Hoc routed to them.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Ad Hoc Review Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Ad Hoc Review Document');
update krim_perm_t
set desc_txt = 'Authorizes users to take the Acknowledge action on KFS documents Ad Hoc routed to them.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Ad Hoc Review Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Ad Hoc Review Document');
update krim_perm_t
set desc_txt = '"Allows users to open Financial System Documents via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node)."'
where nmspc_cd = 'KFS-SYS'
and nm = 'Administer Routing for Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Administer Routing for Document');
update krim_perm_t
set desc_txt = 'Allows access to the Blanket Approval button on KFS Transactional Documents.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Blanket Approve Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Blanket Approve Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of Financial System Documents.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of Financial System Simple Maintenance documents.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Bank Maintenance Document.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes users to modify the information on the Assignees Tab of the Role Document and the Roles section of the Membership Tab on the Person Document for roles with a Module Code beginning with KFS.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Assign Role'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-IDM'
and nm = 'Assign Role');
update krim_perm_t
set desc_txt = 'Allows access to the Payment Application screen.'
where nmspc_cd = 'KFS-AR'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Authorizes users to modify the information on the Permissions tab of the Role Document for roles with a module code beginning with KFS.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Grant Permission'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-IDM'
and nm = 'Grant Permission');
update krim_perm_t
set desc_txt = 'Authorizes users to modify the information on the Responsibility tab of the Role Document for roles with a Module Code that begins with KFS.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Grant Responsibility'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-IDM'
and nm = 'Grant Responsibility');
update krim_perm_t
set desc_txt = 'Authorizes users to modify the information on the Assignees Tab of the Group Document and the Group section of the Membership Tab on the Person Document for groups with namespaces beginning with KFS.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Populate Group'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-IDM'
and nm = 'Populate Group');
update krim_perm_t
set desc_txt = 'Allows access to the Copy button on KFS Financial System Documents.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Copy Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Copy Document');
update krim_perm_t
set desc_txt = 'Authorizes users to view the entire bank account number on the Bank document and Inquiry.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Full Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Full Unmask Field');
update krim_perm_t
set desc_txt = 'Allows users to access KFS inquiries.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Inquire Into Records'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Inquire Into Records');
update krim_perm_t
set desc_txt = 'Allow users to access KFS lookups.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Look Up Records'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Look Up Records');
update krim_perm_t
set desc_txt = 'Authorizes users to initiate and edit the KFS Parameter document for pameters with a module code beginning with KFS.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Maintain System Parameter'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Maintain System Parameter');
update krim_perm_t
set desc_txt = 'Allows users to access and run Batch Jobs associated with KFS modules via the Schedule link.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Batch Job'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Batch Job');
update krim_perm_t
set desc_txt = 'Authorizes users to open KFS Financial System Documents.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Open Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Open Document');
update krim_perm_t
set desc_txt = 'Authorizes users to view the last four-digits of the bank account number on the Bank document and Inquiry.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Partial Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Partial Unmask Field');
update krim_perm_t
set desc_txt = 'Authorizes users to access any KFS screen.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Authorizes users to initiate the Vendor Maintenance Document.'
where nmspc_cd = 'KFS-VND'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes users to view the entire Tax Number on the Vendor Maintenance Document and Inquiry.'
where nmspc_cd = 'KFS-VND'
and nm = 'Full Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Full Unmask Field');
update krim_perm_t
set desc_txt = 'Authorizes users to modify the Tax Number on a Vendor Maintenance Document.'
where nmspc_cd = 'KFS-VND'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Authorizes users to see and edit the Contracts Tab on the Vendor Maintenance Document.'
where nmspc_cd = 'KFS-VND'
and nm = 'Modify Maintenance Document Section'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Section');
update krim_perm_t
set desc_txt = 'Allows users to access the Workflow Document Operation screen.'
where nmspc_cd = 'KR-WKFLW'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = '???'
where nmspc_cd = 'KR-BUS'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Allows users to access the Message Queue screen.'
where nmspc_cd = 'KR-BUS'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Allows users to access the Service Registry screen.'
where nmspc_cd = 'KR-BUS'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Allows users to access the Thread Pool screen.'
where nmspc_cd = 'KR-BUS'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = '???'
where nmspc_cd = 'KR-BUS'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Authorizes users to take any requested action on RICE  documents Ad Hoc routed to them.'
where nmspc_cd = 'KR-SYS'
and nm = 'Ad Hoc Review Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Ad Hoc Review Document');
update krim_perm_t
set desc_txt = '"Allows users to open RICE Documents via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node)."'
where nmspc_cd = 'KR-SYS'
and nm = 'Administer Routing for Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Administer Routing for Document');
update krim_perm_t
set desc_txt = 'Allows access to the Blanket Approval button on RICE Documents.'
where nmspc_cd = 'KR-SYS'
and nm = 'Blanket Approve Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Blanket Approve Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of RICE Documents.'
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Allows users to access the Capital Asset Builder screens (Purchasing/Accounts Payable Transactions and Other General Ledger Transactions).'
where nmspc_cd = 'KFS-CAB'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Authorizes users to modify the information on the Assignees Tab of the Role Document and the Roles section of the Membership Tab on the Person Document for Roles with a Module Code beginning with KR. '
where nmspc_cd = 'KR-SYS'
and nm = 'Assign Role'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-IDM'
and nm = 'Assign Role');
update krim_perm_t
set desc_txt = 'Authorizes users to modify the information on the Permissions tab of the Role Document for roles with a module code beginning with KR.'
where nmspc_cd = 'KR-SYS'
and nm = 'Grant Permission'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-IDM'
and nm = 'Grant Permission');
update krim_perm_t
set desc_txt = 'Authorizes users to modify the information on the Responsibility tab of the Role Document for roles with a Module Code that begins with KR.'
where nmspc_cd = 'KR-SYS'
and nm = 'Grant Responsibility'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-IDM'
and nm = 'Grant Responsibility');
update krim_perm_t
set desc_txt = 'Authorizes users to modify the information on the Assignees Tab of the Group Document and the Group section of the Membership Tab on the Person Document for groups with namespaces beginning with KR.'
where nmspc_cd = 'KR-SYS'
and nm = 'Populate Group'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-IDM'
and nm = 'Populate Group');
update krim_perm_t
set desc_txt = 'Allows access to the Copy button on KFS Financial System Documents.'
where nmspc_cd = 'KR-SYS'
and nm = 'Copy Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Copy Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Asset Payments Document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Allows users to access Kuali RICE inquiries.'
where nmspc_cd = 'KR-SYS'
and nm = 'Inquire Into Records'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Inquire Into Records');
update krim_perm_t
set desc_txt = 'Allow users to access Kuali RICE lookups.'
where nmspc_cd = 'KR-SYS'
and nm = 'Look Up Records'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Look Up Records');
update krim_perm_t
set desc_txt = 'Authorizes to initiate and edit the KFS Parameter document for parameters with a module code beginning with KR.'
where nmspc_cd = 'KR-SYS'
and nm = 'Maintain System Parameter'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Maintain System Parameter');
update krim_perm_t
set desc_txt = 'Allows users to access and run Batch Jobs associated with KR modules via the Schedule link.'
where nmspc_cd = 'KR-SYS'
and nm = 'Modify Batch Job'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Batch Job');
update krim_perm_t
set desc_txt = 'Authorizes users to open RICE Documents.'
where nmspc_cd = 'KR-SYS'
and nm = 'Open Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Open Document');
update krim_perm_t
set desc_txt = 'Allows users to access all RICE screens.'
where nmspc_cd = 'KR-SYS'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Authorizes users to cancel a document prior to it being submitted for routing.'
where nmspc_cd = 'KUALI'
and nm = 'Cancel Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Cancel Document');
update krim_perm_t
set desc_txt = 'Authorizes users to submit a document for routing.'
where nmspc_cd = 'KUALI'
and nm = 'Route Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Route Document');
update krim_perm_t
set desc_txt = 'Authorizes user to save documents answering to the FinancialSystemDocument parent document Type.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Save Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Save Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Barcode Inventory Error Document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes users to take the Approve action on KFS documents routed to them.'
where nmspc_cd = 'KUALI'
and nm = 'Take Requested Action'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Take Requested Action');
update krim_perm_t
set desc_txt = 'Authorizes users to take the FYI action on KFS documents routed to them.'
where nmspc_cd = 'KUALI'
and nm = 'Take Requested Action'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Take Requested Action');
update krim_perm_t
set desc_txt = 'Authorizes users to take the Acknowledge action on KFS documents routed to them.'
where nmspc_cd = 'KUALI'
and nm = 'Take Requested Action'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Take Requested Action');
update krim_perm_t
set desc_txt = 'Authorizes users to login to the Kuali portal.'
where nmspc_cd = 'KUALI'
and nm = 'Log In'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Capital Asset Management Transactional Documents.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Allows users to edit Kuali documents prior to them being submitted for routing.'
where nmspc_cd = 'KUALI'
and nm = 'Edit Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Edit Document');
update krim_perm_t
set desc_txt = 'Allows users to edit Kuali documents that are in ENROUTE status.'
where nmspc_cd = 'KUALI'
and nm = 'Edit Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Edit Document');
update krim_perm_t
set desc_txt = 'Authorizes users to view the password field on the Person document and inquriy.'
where nmspc_cd = 'KR-SYS'
and nm = 'Full Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Full Unmask Field');
update krim_perm_t
set desc_txt = 'Allows users to modify the Vendor Commodity Codes tab on the Vendor document. '
where nmspc_cd = 'KFS-VND'
and nm = 'Modify Maintenance Document Section'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Section');
update krim_perm_t
set desc_txt = 'Authorizes users who can edit the Cash Management Document prior to it being submitted for routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Edit Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Edit Document');
update krim_perm_t
set desc_txt = '??'
where nmspc_cd = 'KFS-FP'
and nm = 'Full Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Full Unmask Field');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on an Invoice document that is at the Account Node of routing.'
where nmspc_cd = 'KFS-AR'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on a Service Billing document that is at the Account Node of routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Capital Asset Complex Maintenance Documents.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Financial Processing Transactional Document  when a document is at the Account Node of routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Target accounting lines on documents answering to the parent document Financial Processing Transactional Document  when a document is at the Account Node of routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on a Disbursement Voucher document that is at the Campus Node of routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on a Disbursement Voucher document that is at the Tax Node of routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on a Disbursement Voucher document that is at the Travel Node of routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on a Disbursement Voucher document that is at the Payment Method Node of routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Target accounting lines on documents answering to the parent document Labor Distribution Transactional Document  when a document is at the Account Node of routing.'
where nmspc_cd = 'KFS-LD'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the object code on Target (To side) accounting lines of the Salary Expense Transfer document.'
where nmspc_cd = 'KFS-LD'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Purchasing Transactional Document  when a document is at the Account Node of routing.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Accounts PayableTransactional Document  when a document is at the Account Node of routing.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allow the creation of negative payments when processing an Asset document. '
where nmspc_cd = 'KFS-CAM'
and nm = 'Add Negative Payments'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on Asset Payment document at the Account Node of routing.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows access to the Blanket Approval button on the Proposal Document.'
where nmspc_cd = 'KFS-CG'
and nm = 'Blanket Approve Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Blanket Approve Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Salary Expense Transfer Document.'
where nmspc_cd = 'KFS-LD'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = '"Allows users to open Purchase Order Documents (and their children document types) via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node)."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Administer Routing for Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Administer Routing for Document');
update krim_perm_t
set desc_txt = '"Allows users to open documents answering to Accounts Payable Transactional Documents (and their children document types) via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node)."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Administer Routing for Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Administer Routing for Document');
update krim_perm_t
set desc_txt = 'Allows access to the Blanket Approval button on the Salary Expense Transfer Document.'
where nmspc_cd = 'KFS-LD'
and nm = 'Blanket Approve Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Blanket Approve Document');
update krim_perm_t
set desc_txt = 'Users who can access the Effort Detail Tab on the Effort Certification Document.'
where nmspc_cd = 'KFS-EC'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users who can access the Effort Summary Tab on the Effort Certification Document.'
where nmspc_cd = 'KFS-EC'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users who can modify accounting line information on Disbursement Voucher documents.'
where nmspc_cd = 'KFS-FP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users who can complete the Nonresident Alien Tax Tab on the Disbursement Voucher document.'
where nmspc_cd = 'KFS-FP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users allowed to merge assets on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Merge'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Users who can modify accounting line information on Disbursement Voucher documents.'
where nmspc_cd = 'KFS-FP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users who can modify accounting line information on Disbursement Voucher documents.'
where nmspc_cd = 'KFS-FP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users who can modify accounting line information on Disbursement Voucher documents.'
where nmspc_cd = 'KFS-FP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users who can modify accounting line information on Disbursement Voucher documents.'
where nmspc_cd = 'KFS-FP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = '????'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = '????'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = '????'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = '????'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users who can edit the Tax Information Tab on the Payment Request Document.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = '????'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users allowed to capitalize assets with dollar amounts below the institution''s Capitalization limit.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Override CAPITALIZATION_LIMIT_AMOUNT'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Users who edit the Bank Code field on documents answering to the parent document Financial System Transactional Document.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Edit Bank Code'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Edit Bank Code');
update krim_perm_t
set desc_txt = 'Users who can open the Budget Construction document.'
where nmspc_cd = 'KFS-BC'
and nm = 'Open Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Open Document');
update krim_perm_t
set desc_txt = 'Users who can open the Routing Form document.'
where nmspc_cd = 'KFS-CG'
and nm = 'Open Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Open Document');
update krim_perm_t
set desc_txt = 'Users who can open the Budget document.'
where nmspc_cd = 'KFS-CG'
and nm = 'Open Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Open Document');
update krim_perm_t
set desc_txt = '????'
where nmspc_cd = 'KFS-BC'
and nm = 'Import / Export Payrate'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = '?????'
where nmspc_cd = 'KFS-BC'
and nm = 'Unlock'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Users who can choose the Raze Asset Retirement Reason.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Raze'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Users who receive an option to override the edit preventing the submission of Salary Expense Transfers involving an open Effort Certification Period.'
where nmspc_cd = 'KFS-LD'
and nm = 'Override Transfer Impacting Open Effort Certification'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Authorizes users to view the last four-digits of the bank account number on the Payee ACH document and Inquiry.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Partial Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Partial Unmask Field');
update krim_perm_t
set desc_txt = 'Authorizes users to view the entire bank routing number on the ACH Bank document and Inquiry.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Full Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Full Unmask Field');
update krim_perm_t
set desc_txt = 'Authorizes users to view the entire bank account number on the ACH Bank document and Inquiry.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Full Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Full Unmask Field');
update krim_perm_t
set desc_txt = 'Authorizes users to view the entire bank routing number on the Payee ACH document and Inquiry.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Full Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Full Unmask Field');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Accounts Financial Processing Transactional Document  when a document has not yet been submitted for routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Target accounting lines on documents answering to the parent document Financial Processing Transactional Document  when a document has not yet been submitted for routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Users allowed to retire more than one asset on an Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Retire Multiple'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Allows users to modify the Target accounting lines on documents answering to the parent document Labor Distribution Transactional Document  when a document has not yet been submitted for routing.'
where nmspc_cd = 'KFs-LD'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Purchasing Transactional Document  when a document has not yet been submitted for routing.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Accounts PayableTransactional Document  when a document has not yet been submitted for routing.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on an Invoice document that has not yet been submitted for routing.'
where nmspc_cd = 'KFS-AR'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Capital Asset ManagementTransactional Document  when a document has not yet been submitted for routing.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Customer Document.'
where nmspc_cd = 'KFS-AR'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Invoice Document.'
where nmspc_cd = 'KFS-AR'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Budget Construction Document.'
where nmspc_cd = 'KFS-BC'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Users allowed to create new and maintain existing records using the Organization Options document.'
where nmspc_cd = 'KFS-AR'
and nm = 'Create / Maintain Record(s)'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Create / Maintain Record(s)');
update krim_perm_t
set desc_txt = 'Users allowed to maintain existing (but not create new) records using the Organization Options document.'
where nmspc_cd = 'KFS-AR'
and nm = 'Create / Maintain Record(s)'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Create / Maintain Record(s)');
update krim_perm_t
set desc_txt = 'Users authorized to retire non-movable assets.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Retire Non-Movable Assets'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Users allowed to create new and maintain existing  records using the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Create / Maintain Record(s)'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Create / Maintain Record(s)');
update krim_perm_t
set desc_txt = 'Allows users to access Budget Construction Selection.'
where nmspc_cd = 'KFS-BC'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Allow users to access Kuali RICE lookups.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Look Up Records'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Look Up Records');
update krim_perm_t
set desc_txt = 'Allows users to access B2B functionality on the Requisition.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Users who can add notes and attachments to the Payment Request document when it is at the Invoice Attachment route node.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Add Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Add Note / Attachment');
update krim_perm_t
set desc_txt = 'Users who can add notes and attachments to any document answering to the Kuali Document parent document type.'
where nmspc_cd = 'KUALI'
and nm = 'Add Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Add Note / Attachment');
update krim_perm_t
set desc_txt = 'Users authorized to separate assets.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Separate'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = '"Authorizes users to view attachments with a type of ""Invoice Image"" on Payment Request documents."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'View Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Note / Attachment');
update krim_perm_t
set desc_txt = 'Authorizes users to view notes and attachments on documents answering to the KualiDocument parent document type.'
where nmspc_cd = 'KUALI'
and nm = 'View Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Note / Attachment');
update krim_perm_t
set desc_txt = 'Authorizes users to delete notes and attachments they have created on Kuali Documents.'
where nmspc_cd = 'KUALI'
and nm = 'Delete Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Delete Note / Attachment');
update krim_perm_t
set desc_txt = 'Authorizes users to delete notes and attachments created by any user on documents answering to the Financial System Document parent document type.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Delete Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Delete Note / Attachment');
update krim_perm_t
set desc_txt = 'Authorizes users to delete notes and attachments created by any user on documents answering to the RICE Document parent document type.'
where nmspc_cd = 'KR-SYS'
and nm = 'Delete Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Delete Note / Attachment');
update krim_perm_t
set desc_txt = 'Allows users to access the XML Ingester screen.'
where nmspc_cd = 'KR-WKFLW'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Authorizes users who can edit Budget Construction Documents that are in FINAL status.'
where nmspc_cd = 'KFS-BC'
and nm = 'Edit Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Edit Document');
update krim_perm_t
set desc_txt = 'Allows users to edit Routing Form documents that are in at the ResearchAdHoc route node.'
where nmspc_cd = 'KFS-CG'
and nm = 'Edit Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Edit Document');
update krim_perm_t
set desc_txt = 'Authorizes users who can edit Payment Request Documents that are in ENROUTE status.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Edit Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Edit Document');
update krim_perm_t
set desc_txt = 'Authorizes users to open Accounts Payable Transactional Documents.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Open Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Open Document');
update krim_perm_t
set desc_txt = 'Authorizes users who can transfer non-movable assets.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Transfer Non-Movable Assets'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Authorizes users to take the Print action on a Purchase Order.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Allows users to access the Electronic Funds Transfer screen.'
where nmspc_cd = 'KFS-SYS'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Users who can print a Purchase Order document.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users who can preview a Purchase Order document before printing it.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = '????'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users who can take the Resend action on Purchase Order documents.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Users authorized to take the Request Cancel action on Payment Request documents.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Authorizes users to remove Holds or Cancels on Payment Request documents.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = '???'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Labor Distribution Transactional Document  when a document has yet to be submitted for routing.'
where nmspc_cd = 'KFS-LD'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = '"Authorizes users who can use the Acquisition Type of ""New"" on the Asset document."'
where nmspc_cd = 'KFS-CAM'
and nm = '"Use Acquisition Type ""New"""'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on a Procurement Card Document that is at the Account Full Entry Node of routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Target accounting lines on a Procurement Card Document that is at the Account Full Entry Node of routing.'
where nmspc_cd = 'KFS-FP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = '???'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = '????'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Authorizes users to open the Contract Manager Assignment Document.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Open Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Open Document');
update krim_perm_t
set desc_txt = 'Allows users to access all Pre-Disbursement Processor screens.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Authorizes users to put Payment Request documents on Hold.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Allows users to access Balance Inquiry screens.'
where nmspc_cd = 'KFS-GL'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = '???'
where nmspc_cd = 'KR-NS'
and nm = 'Administer Pessimistic Locking'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Allows the use of restricted retirement reasons on the Asset or Asset Retirement Global documents.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Use Restricted Retirement Reason'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Users who can save RICE documents.'
where nmspc_cd = 'KR-SYS'
and nm = 'Save Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Save Document');
update krim_perm_t
set desc_txt = 'Authorizes users to open the Electronic Invoice Reject Document.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Open Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Open Document');
update krim_perm_t
set desc_txt = 'Authorizes users to upload eInvoice files.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Upload Batch Input File(s)'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Upload Batch Input File(s)');
update krim_perm_t
set desc_txt = 'Authorizes users to put Credit Memo documents on Hold.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = 'Authorizes users to remove a Hold from Credit Memo documents.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Use Transactional Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Transactional Document');
update krim_perm_t
set desc_txt = '???'
where nmspc_cd = 'KFS-BC'
and nm = 'Use Organization Salary Setting'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'User authorize to edit the Appointment Funding values on the Budget Construction document.'
where nmspc_cd = 'KFS-BC'
and nm = 'Edit Appointment Funding'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'User authorize to view the Appointment Funding values on the Budget Construction document.'
where nmspc_cd = 'KFS-BC'
and nm = 'View Appointment Funding Amounts'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Authorizes users to access other user''s action lists via the Help Desk Action List Login.'
where nmspc_cd = 'KR-WKFLW'
and nm = 'View Other Action List'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Users who can perform a document search with no criteria or result limits.'
where nmspc_cd = 'KR-WKFLW'
and nm = 'Unrestricted Document Search'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Accounts Receivable Complex Maintenance Documents.'
where nmspc_cd = 'KFS-AR'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes users to edit the Capital Asset Type Code on Asset documents.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = '????'
where nmspc_cd = 'KR-WKFLW'
and nm = 'Partial Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Partial Unmask Field');
update krim_perm_t
set desc_txt = 'Allows users to access Balance Inquiry screens. (not sure how this one differs from the BalanceInquiryAction one)'
where nmspc_cd = 'KFS-GL'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Asset Depreciation Document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Allows users to access the Schedule option.'
where nmspc_cd = 'KFS-CAB'
and nm = 'Look Up Records'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Look Up Records');
update krim_perm_t
set desc_txt = 'Allows users to access Capital Asset Builder inquiries.'
where nmspc_cd = 'KFS-CAB'
and nm = 'Inquire Into Records'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Inquire Into Records');
update krim_perm_t
set desc_txt = 'Users allowed to create new and maintain existing  records using the Customer Invoice Item Code document.'
where nmspc_cd = 'KFS-AR'
and nm = 'Create / Maintain Record(s)'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Create / Maintain Record(s)');
update krim_perm_t
set desc_txt = 'Authorizes users to view the entire Tax Identification Number on the Payee ACH document and Inquiry.'
where nmspc_cd = 'KR-SYS'
and nm = 'Full Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Full Unmask Field');
update krim_perm_t
set desc_txt = 'Users who can modify entity records in Kuali Identity Management.'
where nmspc_cd = 'KR-IDM'
and nm = 'Modify Entity'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on a Requisition Document that is at the Initiator Node of routing.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = 'Allows users to modify the Capital Asset Description field on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = '"Authorizes users to add notes and attachments with a type of ""Contracts"" to the Purchase Order document. "'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Add Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Add Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to view attachments with a type of ""Contract"" on Purchase Order documents and documents answering to that document type."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'View Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to add notes and attachments with a type of ""Contract"" on Purchase Order documents and documents answering to that document type."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Add Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Add Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to view attachments with a type of ""Contract Amendments"" on Purchase Order documents  and documents answering to that document type."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'View Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to add notes and attachments with a type of ""Quotes"" on Purchase Order documents  and documents answering to that document type."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Add Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Add Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to view attachments with a type of ""Quotes"" on Purchase Order documents  and documents answering to that document type."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'View Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to add notes and attachments with a type of ""RFPs"" on Purchase Order documents  and documents answering to that document type."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Add Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Add Note / Attachment');
update krim_perm_t
set desc_txt = 'Users authorized to modify the Organization Owner Chart of Accounts Code on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = '"Authorizes users to view attachments with a type of ""RFPs"" on Purchase Order documents  and documents answering to that document type."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'View Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to add notes and attachments with a type of ""RFP"" on Purchase Order documents  and documents answering to that document type."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Add Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Add Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to view attachments with a type of ""RFP"" on Purchase Order documents  and documents answering to that document type."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'View Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to add notes or attachments with a type of ""Other-Restricted"" on Purchase Order documents  and documents answering to that document type."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Add Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Add Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to view attachments with a type of ""Other-Restricted"" on Purchase Order documents  and documents answering to that document type."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'View Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to add notes or attachments with a type of ""Credit Memo Image"" on Credit Memo documents."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Add Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Add Note / Attachment');
update krim_perm_t
set desc_txt = '"Authorizes users to view attachments with a type of ""Credit Memo Image"" on Credit Memo documents."'
where nmspc_cd = 'KFS-PURAP'
and nm = 'View Note / Attachment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Note / Attachment');
update krim_perm_t
set desc_txt = 'Allows user to maintain the location of assets on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Maintain Asset Location'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Allows users to modify the Source accounting lines on a Requisition Document that is at the Organization Node of routing.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Modify Accounting Lines'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Modify Accounting Lines');
update krim_perm_t
set desc_txt = '???'
where nmspc_cd = 'KFS-FP'
and nm = 'Use Screen'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Users who can modify the Organization Owner Account Number on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = '???'
where nmspc_cd = 'KFS-FP'
and nm = ''
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = '???'
where nmspc_cd = 'KFS-FP'
and nm = ''
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Use Screen');
update krim_perm_t
set desc_txt = 'Users who can modify the Capital Asset in Service Date on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can modify the Agency Number on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can modify the Vendor Name on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can modify the Old Tag Number field on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can modify the Government Tag Number field on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can modify the National Stock Number field on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can modify the Financial Object Sub Type Code field on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Authorizes users to access the Barcode Inventory Process upload page.'
where nmspc_cd = 'KFS-CAM'
and nm = 'Upload Batch Input File(s)'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Upload Batch Input File(s)');
update krim_perm_t
set desc_txt = 'Users who can view the Replacement Amount field on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'View Inquiry or Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Inquiry or Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can view the Estimated Selling Price field on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'View Inquiry or Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Inquiry or Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can view the Condition Code on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'View Inquiry or Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Inquiry or Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can view the Land County Name on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'View Inquiry or Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Inquiry or Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can view the Land Acreage Size on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'View Inquiry or Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Inquiry or Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can view the Land Parcel Number on the Asset document.'
where nmspc_cd = 'KFS-CAM'
and nm = 'View Inquiry or Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Inquiry or Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Application Document.'
where nmspc_cd = 'KFS-AR'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Routing Form Document.'
where nmspc_cd = 'KFS-CG'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the CFDA Close Document.'
where nmspc_cd = 'KFS-CG'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Proposal Document.'
where nmspc_cd = 'KFS-CG'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes users to vew the Research Risk tab on the Proposal document.'
where nmspc_cd = 'KFS-CG'
and nm = 'View Inquiry or Maintenance Document Section'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'View Inquiry or Maintenance Document Section');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Global Object Code Document.'
where nmspc_cd = 'KFS-COA'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Organization Reversion Document.'
where nmspc_cd = 'KFS-COA'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Users who can edit Accounts that are inactive.'
where nmspc_cd = 'KFS-COA'
and nm = 'Edit Inactive Account'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = '"Identifies users that can be Account Managers, Supervisors or Fiscal Officers."'
where nmspc_cd = 'KFS-COA'
and nm = '"Serve As Account Manager, Supervisor, or Fiscal Officer"'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Cash Control Document.'
where nmspc_cd = 'KFS-AR'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes users to edit the Sub-Account Type Code on the Sub-Account document.'
where nmspc_cd = 'KFS-COA'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can edit the Organization Plant Chart Code on the Organization document.'
where nmspc_cd = 'KFS-COA'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can edit the Organization Plant Account Number on the Organization document.'
where nmspc_cd = 'KFS-COA'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can edit the Campus Plant Chart Code on the Organization document.'
where nmspc_cd = 'KFS-COA'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Users who can edit the Campus Plant Account Number on the Organization document.'
where nmspc_cd = 'KFS-COA'
and nm = 'Modify Maintenance Document Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Modify Maintenance Document Field');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Effort Certification Document.'
where nmspc_cd = 'KFS-EC'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Users who can use a Distribution of Income and Expense document to claim Electronic Payments.'
where nmspc_cd = 'KFS-FP'
and nm = 'Claim Electronic Payment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Claim Electronic Payment');
update krim_perm_t
set desc_txt = 'Users who can use a Year End Distribution of Income and Expense document to claim Electronic Payments.'
where nmspc_cd = 'KFS-FP'
and nm = 'Claim Electronic Payment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KFS-SYS'
and nm = 'Claim Electronic Payment');
update krim_perm_t
set desc_txt = '"Allows users to open Procurement Card Documents via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node)."'
where nmspc_cd = 'KFS-FP'
and nm = 'Administer Routing for Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Administer Routing for Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Cash Management Document.'
where nmspc_cd = 'KFS-FP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Organizaton Accounting Default Document.'
where nmspc_cd = 'KFS-AR'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Service Billing Document.'
where nmspc_cd = 'KFS-FP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Journal Voucher Document.'
where nmspc_cd = 'KFS-FP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the  Procurement Card Document.'
where nmspc_cd = 'KFS-FP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes users to view the entire Credit Card number on the Procurement Card document and Inquiry.'
where nmspc_cd = 'KFS-FP'
and nm = 'Full Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Full Unmask Field');
update krim_perm_t
set desc_txt = 'Authorizes users to access the Procurement Card Upload page.'
where nmspc_cd = 'KFS-FP'
and nm = 'Upload Batch Input File(s)'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Upload Batch Input File(s)');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the  General Ledger Correction Process Document.'
where nmspc_cd = 'KFS-GL'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes users to access the Collector Upload page.'
where nmspc_cd = 'KFS-GL'
and nm = 'Upload Batch Input File(s)'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Upload Batch Input File(s)');
update krim_perm_t
set desc_txt = 'Authorizes user to access the Enterprise Feed Upload page.'
where nmspc_cd = 'KFS-GL'
and nm = 'Upload Batch Input File(s)'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Upload Batch Input File(s)');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Labor Journal Voucher Document.'
where nmspc_cd = 'KFS-LD'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the  Labor Ledger Correction Process Document.'
where nmspc_cd = 'KFS-LD'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of Accounts Receivable Transactional Documents.'
where nmspc_cd = 'KFS-AR'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes users to set and remove immediate status on payments in PDP.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Set as Immmediate Pay'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of Pre-Disbursement Processor Simple Maintenance Documents.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes users to take the Cancel action on payments in PDP.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Cancel Payment'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Authorizes users to access and run the Format Checks /ACH screen in PDP'
where nmspc_cd = 'KFS-PDP'
and nm = 'Format'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Authorizes users to hold payments and remove non-tax related holds on payments in PDP.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Hold Payment / Remove Non-Tax Payment Hold'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Users who can reset a format process in PDP.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Remove Format Lock'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Authorizes users to remove payments held for tax review in PDP.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Remove Payment Tax Hold'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KUALI'
and nm = 'Default');
update krim_perm_t
set desc_txt = 'Allows users to access Pre Disbursement Processor inquiries.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Inquire Into Records'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Inquire Into Records');
update krim_perm_t
set desc_txt = 'Allow users to access Pre Disbursement Processor lookups.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Look Up Records'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Look Up Records');
update krim_perm_t
set desc_txt = 'Authorizes users to view the entire Tax Number on the Customer document and Inquiry.'
where nmspc_cd = 'KFS-AR'
and nm = 'Full Unmask Field'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Full Unmask Field');
update krim_perm_t
set desc_txt = 'Allows access to the Manually Upload Payment File screen in PDP.'
where nmspc_cd = 'KFS-PDP'
and nm = 'Upload Batch Input File(s)'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-NS'
and nm = 'Upload Batch Input File(s)');
update krim_perm_t
set desc_txt = 'Allows access to the Blanket Approval button on the Payment Request Document.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Blanket Approve Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-WKFLW'
and nm = 'Blanket Approve Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Requisition Document.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of Purchasing Transactional Documents.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Purchase Order Document.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Purchase Order Close Document.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Purchase Order Retransmit Document.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of Accounts Payable Transactional Documents.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of Receiving Transactional Documents.'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');
update krim_perm_t
set desc_txt = 'Authorizes the initiation of the Electronic Invoice Reject  Document.
'
where nmspc_cd = 'KFS-PURAP'
and nm = 'Initiate Document'
and perm_tmpl_id = (select perm_tmpl_id
from krim_perm_tmpl_t
where nmspc_cd = 'KR-SYS'
and nm = 'Initiate Document');

--p kfsmi-3949
update krns_parm_t
set parm_nm = 'TAXABLE_PAYMENT_REASON_CODES_FOR_BLANK_CORPORATION_OWNERSHIP_TYPE_CATEGORIES'
where parm_nm = 'TAXABLE_PAYMENT_REASON_CODES_FOR_BLANK_CORPORATION_OWNERSHIP_TYPE_CATEGORIES '
;

--p kfsmi-4046 should update 2 records
update krim_perm_attr_data_t
set attr_val = 'AP'
where attr_val = 'PREQ'
and attr_data_id in ('545','546');

--p kfsmi-4031
update krim_role_perm_t
set role_id = '28'
where role_perm_id = '515'
and role_id = '61';
insert into krim_role_perm_t
select '695', sys_guid(),1, '68','260', 'Y' from dual;
insert into krim_role_perm_t
select '700', sys_guid(),1, '41','260', 'Y' from dual;
insert into krim_role_perm_t
select '703', sys_guid(),1, '50','260', 'Y' from dual;
insert into krim_perm_t
select '384',sys_guid(),1, '46', 'KFS-PURAP', 'View Note / Attachment',
'Authorizes users to view attachments with a type of "Credit Memo Image" on Credit Memo documents.', 'Y' from dual;
insert into krim_perm_attr_data_t
SELECT '584', sys_guid(), 1, '384', '9', '13', 'CM' from dual;
insert into krim_perm_attr_data_t
SELECT '585', sys_guid(), 1, '384', '9', '9', 'Invoice Image' from dual;
insert into krim_role_perm_t
select '704', sys_guid(),1, '22','384', 'Y' from dual;
insert into krim_role_perm_t
select '705', sys_guid(),1, '26','384', 'Y' from dual;
insert into krim_role_perm_t
select '706', sys_guid(),1, '41','384', 'Y' from dual;

--p kfsmi-3945
insert into krim_perm_t values
('379',sys_guid(), 1, '10', 'KFS-SYS','Initiate Document', '','Y' );
insert into krim_perm_attr_data_t values
('574', sys_guid(), 1, '379', '3', '13', 'IdentityManagementGroupDocument');
insert into krim_role_perm_t values
('693', sys_guid(), 1, '54', '379','Y');

--p kfsmi-3885
insert into krim_perm_t values
('380',sys_guid(), 1, '10', 'KFS-COA', 'Initiate Document', '','Y' );
insert into krim_perm_attr_data_t values
('575', sys_guid(), 1, '380', '3', '13', 'GORV');
insert into krim_role_perm_t values
('694', sys_guid(), 1, '37', '380','Y');

--p kfsmi-3316
update krew_doc_typ_t
set lbl = 'Proposal/Award Close'
where doc_typ_nm = 'CLOS';

--p kfsmi-3888
insert into krim_rsp_t
select 121, sys_guid(), 1, 1, 'KFS-SYS', 'Review', NULL,'Y' from dual;
insert into krim_rsp_t
select 122, sys_guid(), 1, 1, 'KFS-SYS', 'Review', NULL,'Y' from dual;
insert into krim_rsp_attr_data_t
select 454, sys_guid(), 1, 121, 7, 16, 'AccountingOrganizationHierarchy' from dual;
insert into krim_rsp_attr_data_t
select 455, sys_guid(), 1, 121, 7, 13, 'FPYE' from dual;
insert into krim_rsp_attr_data_t
select 456, sys_guid(), 1, 121, 7, 41, 'true' from dual;
insert into krim_rsp_attr_data_t
select 457, sys_guid(), 1, 121, 7, 40, 'true' from dual;
insert into krim_rsp_attr_data_t
select 458, sys_guid(), 1, 122, 7, 16, 'AccountingOrganizationHierarchy' from dual;
insert into krim_rsp_attr_data_t
select 459, sys_guid(), 1, 122, 7, 13, 'LDYE' from dual;
insert into krim_rsp_attr_data_t
select 460, sys_guid(), 1, 122, 7, 41, 'true' from dual;
insert into krim_rsp_attr_data_t
select 461, sys_guid(), 1, 122, 7, 40, 'true' from dual;
insert into krim_role_rsp_t
select 1122, sys_guid(), 1, 28, 121, 'Y' from dual;
insert into krim_role_rsp_t
select 1123, sys_guid(), 1, 28, 122, 'Y' from dual;
update krim_role_rsp_actn_t
set role_rsp_id = '*'
where role_rsp_id = '1012'
and role_mbr_id !='*';
insert into krim_role_rsp_actn_t
select '170', sys_guid(), 1, 'A', 2 , 'F', '1705', '*', 'N' from dual;

--p kfsmi-3908
insert into krns_parm_t 
(SELECT 'KFS-GL', 'EncumbranceForwardStep',
'FORWARD_ENCUMBRANCE_BALANCE_TYPE_AND_ORIGIN_CODE', sys_guid(),1,
'CONFG', 'IE:LD',
'Either includes or excludes all encumbrances from forward',
'D', 'KUALI'
FROM dual)
;

--p kfsmi-3717
update krew_doc_typ_t
set lbl = 'Financial Reporting Code'
where doc_typ_nm = 'RPTC';

--p kfsmi-3933
insert into krns_parm_t 
(SELECT 'KFS-PDP', 'PaymentDetail',
'DISBURSEMENT_CANCELLATION_DAYS', sys_guid(),1,
'VALID', '30',
'Number of days past since a payment was disbursed before cancel or cancel and reissue is disallowed.',
'A', 'KUALI'
FROM dual)
;

--p kfsmi-3982
insert into krns_parm_t 
(SELECT 'KFS-COA', 'Account',
'DERIVED_ROLE_MEMBER_INACTIVATION_NOTIFICATION_EMAIL_ADDRESSES', sys_guid(),1,
'CONFG', 'kbatch-l@indiana.edu',
'The e-mail address to send a notification to when a member of an account derived role - Fiscal Officer, Account Supervisor, Fiscal Officer Primary Delegate, or Fiscal Officer Secondary Delegate - is inactivated.',
'A', 'KUALI'
FROM dual)
;

--p kfsmi-3828
insert into krim_role_mbr_t
select '1705', 1, sys_guid(), '28', '2906405069', 'P', NULL, NULL, sysdate from dual;
insert into krim_role_mbr_t
select '1706', 1, sys_guid(), '28', '2906405069', 'P', NULL, NULL, sysdate from dual;
insert into krim_role_mbr_attr_data_t
select '4033', sys_guid(), 1, '1705', '30', '22', 'IU' from dual;
insert into krim_role_mbr_attr_data_t
select '4034', sys_guid(), 1, '1705', '30', '24', 'UNIV' from dual;
insert into krim_role_mbr_attr_data_t
select '4035', sys_guid(), 1, '1705', '30', '13', 'BA' from dual;
insert into krim_role_mbr_attr_data_t
select '4036', sys_guid(), 1, '1706', '30', '22', 'IU' from dual;
insert into krim_role_mbr_attr_data_t
select '4037', sys_guid(), 1, '1706', '30', '24', 'UNIV' from dual;
insert into krim_role_mbr_attr_data_t
select '4038', sys_guid(), 1, '1706', '30', '13', 'YEBA' from dual;
insert into krim_role_rsp_actn_t
select '200', sys_guid(), 1, 'A', NULL , 'F', '1706', '*', 'N' from dual;

--p kfsmi-3800
INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
  VALUES('123', SYS_GUID(), 1, '1', NULL, NULL, 'Y', 'KFS-SYS')
;
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('449', SYS_GUID(), 1, '123', '7', '13', 'IdentityManagementDocument')
;
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('450', SYS_GUID(), 1, '123', '7', '16', 'RoleType')
;
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('451', SYS_GUID(), 1, '123', '7', '40', 'false')
;
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('452', SYS_GUID(), 1, '123', '7', '41', 'true')
;
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('453', SYS_GUID(), 1, '123', '7', '46', '29')
;
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND)
  VALUES('1124', SYS_GUID(), 2, '7', '123', 'Y')
; 

--p kfsmi-3650
UPDATE KRIM_PERM_ATTR_DATA_T SET ATTR_VAL='false' WHERE ATTR_DATA_ID='565';

--p kfsmi-3889: Not on new list?
insert into krns_parm_t ( nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd )
values ( 'KFS-PURAP', 'All', 'CXML_DATE_FORMAT', SYS_GUID(), 1, 'CONFG', '0000-00-00', 'Format with 0s is so if vendor sends time information we will only check the year, month and date.', 'A' );

insert into krns_parm_t ( nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd )
values ( 'KFS-PURAP', 'All', 'CXML_SIMPLE_DATE_FORMAT', SYS_GUID(), 1, 'CONFG', 'yyyy-MM-dd', 'Simple data format for CXML.', 'A' );

insert into krns_parm_t ( nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd )
values ( 'KFS-PURAP', 'All', 'KUALI_DATE_FORMAT', SYS_GUID(), 1, 'CONFG', '00/00/0000', 'Format with 0s is so if vendor sends time information we will only check the year, month and date.', 'A' );

insert into krns_parm_t ( nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd )
values ( 'KFS-PURAP', 'All', 'KUALI_SIMPLE_DATE_FORMAT', SYS_GUID(), 1, 'CONFG', 'MM/dd/yyyy', 'Generic PURAP KFS date format.', 'A' );

insert into krns_parm_t ( nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd )
values ( 'KFS-PURAP', 'All', 'CXML_SIMPLE_TIME_FORMAT', SYS_GUID(), 1, 'CONFG', 'HH:mm:ss.sss', 'Simple time format used in CXML.', 'A' );

insert into krns_parm_t ( nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd )
values ( 'KFS-PURAP', 'All', 'KUALI_SIMPLE_DATE_FORMAT_2', SYS_GUID(), 1, 'CONFG', 'MM-dd-yyyy', 'Month first simple date format.', 'A' );

--p kfsmi-4113
update krim_perm_tmpl_t
set nm = 'Administer Batch File'
where nm = 'View Batch File(s)';

--p kfsmi-4087
delete from krns_parm_t
where parm_nm = 'ALLOW_ENROUTE_EDIT_OBJECT_CODES_IND';

--p kfsmi-4066
insert into krim_perm_t values ('381', sys_guid(), 1, '27', 'KFS-FP', 'Full Unmask Field', null, 'Y');

insert into krim_perm_attr_data_t values ('576', sys_guid(), 1, '381', '11', '5', 'DisbursementVoucherWireTransfer');
insert into krim_perm_attr_data_t values ('577', sys_guid(), 1, '381', '11', '6', 'disbVchrPayeeAccountNumber');

insert into krim_role_perm_t values ('696', sys_guid(), 1, '60', '381', 'Y');
insert into krim_role_perm_t values ('697', sys_guid(), 1, '70', '381', 'Y');

--p KFSMI-3382
insert into krim_perm_t
select '382', sys_guid(),1, '16','KFS-SYS','Edit Document','Editable Ad-hoc for
Simple Maint docs','Y'
from dual;
insert into krim_perm_t
select '383', sys_guid(),1, '16','KFS-SYS','Edit Document','Editable Ad-hoc for
Complex Maint docs','Y'
from dual;
insert into krim_perm_attr_data_t select '578', sys_guid(), 1, '382', '8', '13',
 'FSSM' from dual;
insert into krim_perm_attr_data_t select '579', sys_guid(), 1, '383', '8', '13',
 'KFSM' from dual;
insert into krim_perm_attr_data_t select '580', sys_guid(), 1, '382', '8', '16',
 'AdHoc' from dual;
insert into krim_perm_attr_data_t select '581', sys_guid(), 1, '383', '8', '16',
 'AdHoc' from dual;
insert into krim_perm_attr_data_t select '582', sys_guid(), 1, '382', '8', '15',
 'R' from dual;
insert into krim_perm_attr_data_t select '583', sys_guid(), 1, '383', '8', '15',
 'R' from dual;
insert into krim_role_perm_t select '698', sys_guid(), 1, '59', '382', 'Y' from
dual;
insert into krim_role_perm_t select '699', sys_guid(), 1, '59', '383', 'Y' from
dual ;

--KFSMI-4047
insert into krns_parm_t 
(SELECT 'KFS-FP', 'DisbursementVoucher',
'USE_DEFAULT_EMPLOYEE_ADDRESS_IND', sys_guid(),1,
'CONFG', 'N',
'If the value is Yes then use whatever address is marked as the default address for the person record as the default address for employee payees on the Disbursement Voucher. If the value is No then consult the DEFAULT_EMPLOYEE_ADDRESS_TYPE parameter to determine which address to use.',
'A', 'KUALI'
FROM dual);

insert into krns_parm_t 
(SELECT 'KFS-FP', 'DisbursementVoucher',
'DEFAULT_EMPLOYEE_ADDRESS_TYPE', sys_guid(),1,
'CONFG', 'HOME',
'If USE_DEFAULT_EMPLOYEE_ADDRESS_IND is No then this parameter defines the address type that should be used as the default for employee payees on the Disbursement Voucher. ',
'A', 'KUALI'
FROM dual);

-- kfsmi-3977
insert into krim_role_mbr_attr_data_t
select '4039',sys_guid(),1,'1640','17','12','*' from dual
;
update krim_role_mbr_t
set mbr_id = '1610003826'
where role_mbr_id = '933'
;

--kfsmi-3865
delete from krim_perm_attr_data_t
where perm_id = '262';
delete from krim_role_perm_t
where perm_id = '262';
delete from krim_perm_t
where perm_id = '262';

--kfsmi-4208
insert into krns_parm_t
select 'KFS-SYS', 'FilePurgeStep', 'DEFAULT_NUMBER_OF_DAYS_OLD',
sys_guid(), 1, 'CONFG', '30', 'The FilePurgeStep will traverse the application I/O directories and their sub-directories deleting files with a last modified date more that this number of days prior to today.' , 'A', 'KUALI' from dual;

--kfsmi-4115
insert into krew_doc_typ_t (doc_typ_id, parnt_id,doc_typ_nm,
doc_typ_ver_nbr, actv_ind, cur_ind, lbl, obj_id)
select KREW_DOC_HDR_S.nextval, doc_typ_id, 'ACHD', 1, 1,1,
'ACH Disbursement' , sys_guid() from krew_doc_typ_t
where doc_typ_nm = 'FSLO'
;
insert into krew_doc_typ_t (doc_typ_id, parnt_id,doc_typ_nm,
doc_typ_ver_nbr, actv_ind, cur_ind, lbl, obj_id)
select KREW_DOC_HDR_S.nextval, doc_typ_id, 'CHKD', 1, 1,1,
'Check Disbursement' , sys_guid() from krew_doc_typ_t
where doc_typ_nm = 'FSLO'
;

--kfsmi-4226
insert into krns_parm_t 
(SELECT 'KFS-SYS', 'All',
'ACCOUNTS_CAN_CROSS_CHARTS_IND', sys_guid(),1,
'CONFG', 'Y',
'This parameter indicates whether the same account number may be allowed to be associated with multiple chart codes. For example, if account number "123" is in chart "BL", it may not also be in chart "BA". When the value is false, it will turn on the pre-scrubber for the GL scrubber, LL scrubber, GLCP scrubber, LLCP scrubber, and GL collector. When the work is complete, if the parameter is false, the Account maintenance document will prevent the creation of account numbers that span mulitple charts and will allow accounting documents deduce the chart code based on the account number.',
'A', 'KUALI'
FROM dual);

-- KULRICE-3212 
ALTER TABLE KREW_DOC_HDR_T MODIFY (APP_DOC_ID VARCHAR2(255));

-- KULRICE-3015 - Standardize length of document type name and lbl columns
ALTER TABLE KREW_ACTN_ITM_T MODIFY (DOC_TYP_NM VARCHAR2(64));
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY (DOC_TYP_NM VARCHAR2(64));
ALTER TABLE KREW_DOC_TYP_T MODIFY (DOC_TYP_NM VARCHAR2(64));
ALTER TABLE KREW_RULE_T MODIFY (DOC_TYP_NM VARCHAR2(64));
ALTER TABLE KREW_EDL_ASSCTN_T MODIFY (DOC_TYP_NM VARCHAR2(64));
ALTER TABLE KREW_EDL_DMP_T MODIFY (DOC_TYP_NM VARCHAR2(64));
ALTER TABLE KREW_DOC_TYP_T MODIFY (LBL VARCHAR2(128));
ALTER TABLE KREW_ACTN_ITM_T MODIFY (DOC_TYP_LBL VARCHAR2(128));
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY (DOC_TYP_LBL VARCHAR2(128));
ALTER TABLE KRNS_PARM_T ADD APPL_NMSPC_CD  varchar2(20) default 'KUALI' not null;
ALTER TABLE KRNS_PARM_T DROP CONSTRAINT KRNS_PARM_TP1;
ALTER TABLE KRNS_PARM_T ADD CONSTRAINT KRNS_PARM_TP1 PRIMARY KEY(NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM, APPL_NMSPC_CD);
create table temp_t as select * from KRIM_ENTITY_ADDR_T;
update KRIM_ENTITY_ADDR_T
set ADDR_LINE_1 = NULL, ADDR_LINE_2 = NULL, ADDR_LINE_3 = NULL;
ALTER TABLE KRIM_ENTITY_ADDR_T MODIFY ADDR_LINE_1 VARCHAR2(45);
ALTER TABLE KRIM_ENTITY_ADDR_T MODIFY ADDR_LINE_2 VARCHAR2(45);
ALTER TABLE KRIM_ENTITY_ADDR_T MODIFY ADDR_LINE_3 VARCHAR2(45);
update KRIM_ENTITY_ADDR_T ka
set (ADDR_LINE_1, ADDR_LINE_2, ADDR_LINE_3) = 
(select substr(ADDR_LINE_1,0,45), substr(ADDR_LINE_2,0,45),
 substr(ADDR_LINE_3,0,45) from temp_t
where entity_addr_id = ka.entity_addr_id);
drop table temp_t;

--kfsmi-4230
update krns_parm_t
set txt = 'File Upload Successful'
where nmspc_cd = 'KFS-GL'
and parm_dtl_typ_cd = 'CollectorStep'
and parm_nm = 'VALIDATION_EMAIL_SUBJECT_LINE';

--kfsmi-4231
insert into krns_parm_t 
(SELECT 'KFS-GL', 'EncumbranceForwardStep',
'FORWARDING_ENCUMBRANCE_BALANCE_TYPES', sys_guid(),1,
'CONFG', 'CE',
'Used to narrow down the set of encumbrance balance types (obtained from the balance type table) that will be used to identify the encumbrances that should be carried forward to the next fiscal year.',
'D', 'KUALI'
FROM dual);

--kfmsi-4031
insert into krim_role_perm_t
select '700', sys_guid(),1, '41','260', 'Y' from dual;
insert into krim_role_perm_t
select '701', sys_guid(),1, '42','260', 'Y' from dual;
insert into krim_role_perm_t
select '702', sys_guid(),1, '43','260', 'Y' from dual;
insert into krim_role_perm_t
select '703', sys_guid(),1, '50','260', 'Y' from dual;
insert into krim_perm_t
select '384',sys_guid(),1, '46', 'KFS-PURAP', 'View Note / Attachment',
'Authorizes users to view attachments with a type of "Credit Memo Image" on Credit Memo documents.', 'Y' from dual;
insert into krim_perm_attr_data_t
SELECT '584', sys_guid(), 1, '384', '9', '13', 'CM' from dual;
insert into krim_perm_attr_data_t
SELECT '585', sys_guid(), 1, '384', '9', '9', 'Invoice Image' from dual;
insert into krim_role_perm_t
select '704', sys_guid(),1, '22','384', 'Y' from dual;
insert into krim_role_perm_t
select '705', sys_guid(),1, '26','384', 'Y' from dual;
insert into krim_role_perm_t
select '706', sys_guid(),1, '41','384', 'Y' from dual;
insert into krim_role_perm_t
select '707', sys_guid(),1, '42','384', 'Y' from dual;
insert into krim_role_perm_t
select '708', sys_guid(),1, '43','384', 'Y' from dual;
insert into krim_role_perm_t
select '709', sys_guid(),1, '42','384', 'Y' from dual;
insert into krim_role_perm_t
select '710', sys_guid(),1, '43','384', 'Y' from dual;

--KULRICE-3263
DECLARE
   CURSOR seq_cur IS SELECT * from user_sequences where SEQUENCE_NAME like 'KRIM_%';
   seq_rec  seq_cur%ROWTYPE;
   curr_val INTEGER;
   curr_inc INTEGER;
   curr_min INTEGER;
   newnumber INTEGER := 10000;
BEGIN
   OPEN seq_cur;
   LOOP
        FETCH seq_cur INTO seq_rec;
        EXIT WHEN seq_cur%NOTFOUND;
        SELECT INCREMENT_BY, MIN_VALUE into curr_inc, curr_min from user_sequences where sequence_name = seq_rec.SEQUENCE_NAME;
        EXECUTE IMMEDIATE 'ALTER SEQUENCE ' ||seq_rec.SEQUENCE_NAME||' MINVALUE ' || LEAST((newnumber - curr_inc - 1) , curr_min) ;
        EXECUTE IMMEDIATE 'SELECT ' ||seq_rec.SEQUENCE_NAME||'.nextval FROM dual' INTO curr_val;
        IF (newnumber - curr_val - curr_inc) != 0 THEN
            EXECUTE IMMEDIATE 'ALTER SEQUENCE ' ||seq_rec.SEQUENCE_NAME||' INCREMENT BY '||(newnumber - curr_val - curr_inc);
        END IF;
        EXECUTE IMMEDIATE 'SELECT ' ||seq_rec.SEQUENCE_NAME ||'.nextval FROM dual' INTO curr_val;
        EXECUTE IMMEDIATE 'ALTER SEQUENCE ' ||seq_rec.SEQUENCE_NAME||' INCREMENT BY ' || curr_inc;
   END LOOP;
END;

ALTER TABLE KRIM_GRP_MBR_T DISABLE CONSTRAINT KRIM_GRP_MBR_TR1; 

UPDATE KRIM_GRP_T SET GRP_ID='9997' where KRIM_GRP_T.GRP_ID = '1000000';
UPDATE KRIM_GRP_T SET GRP_ID='9998' where KRIM_GRP_T.GRP_NM = '1000001';
UPDATE KRIM_GRP_T SET GRP_ID='9999' where KRIM_GRP_T.GRP_NM = '1000002';

UPDATE KRIM_GRP_MBR_T SET KRIM_GRP_MBR_T.GRP_ID = '9997' where GRP_ID = '1000000'; 
UPDATE KRIM_GRP_MBR_T SET KRIM_GRP_MBR_T.GRP_ID = '9998' where GRP_ID = '1000001';
UPDATE KRIM_GRP_MBR_T SET KRIM_GRP_MBR_T.GRP_ID = '9999' where GRP_ID = '1000002';

ALTER TABLE KRIM_GRP_MBR_T ENABLE CONSTRAINT KRIM_GRP_MBR_TR1;

--KFSMI-3949
update krns_parm_t
 set parm_nm= 'CORPORATION_OWNERSHIP_TYPE'
 WHERE PARM_NM = 'CORPORATION_OWNERSHIP_TYPE ';
update krns_parm_t
set parm_nm= 'NUMBER_OF_DAYS_SINCE_LAST_ UPDATE'
WHERE PARM_NM = ' NUMBER_OF_DAYS_SINCE_LAST_UPDATE ';

--KFSMI-4294
update krim_perm_t
set desc_txt = 'Authorizes user to access the Collector Flat File Upload page.'
where perm_id = '385';
insert into krim_role_perm_t 
select '707',sys_guid(),1, '17', '385','Y'
from dual;

--KFSMI-3916
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'ACADEMIC_YEAR_SUBDIVISION_NAMES';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'BASE_CODE_DEFAULT_VALUE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'COST_SHARE_PERMISSION';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'COST_SHARE_PERMISSION_CODE';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'DEFAULT_BUDGET_TASK_NAME';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'INDIRECT_COST_MAX_MANUAL_RATE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'INDIRECT_COST_PROVIDED_MANUALLY';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'INDIRECT_COST_PROVIDED_SYSTEM';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MANUAL_RATE_INDICATOR_DEFAULT_VALUE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MAXIMUM_NUMBER_MODULAR_PERIODS';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MAXIMUM_NUMBER_OF_PERIODS';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MAXIMUM_NUMBER_OF_TASKS';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MAXIMUM_PERIOD_LENGTH';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MAX_INFLATION_RATE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MINIMUM_NUMBER_OF_PERIODS';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MINIMUM_NUMBER_OF_TASKS';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'NEW_PERIOD_IDENTIFIER';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'NUMBER_OF_ACADEMIC_YEAR_SUBDIVISIONS';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_GENERIC_BY_PERIOD_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_GENERIC_BY_TASK_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_NIH2590_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_NIH398_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_NIH_MODULAR_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_NSF_SUMMARY_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_PATH_PREFIX';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_SF424_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_ACADEMIC_YEAR_APPOINTMENT_TYPE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_FULL_YEAR_APPOINTMENT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_GRADUATE_RESEARCH_ASSISTANT_APPOINTMENT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_HOURLY_APPOINTMENT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PROJECT_DIRECTOR_ORG_PERMISSION';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PROJECT_DIRECTOR_PERMISSION';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PURPOSE_CODE_DEFAULT_VALUE';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'APPROVALS_DEFAULT_WORDING';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'APPROVALS_INITIATOR_WORDING';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'APPROVALS_PROJECT_DIRECTOR_WORDING';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'CREATE_PROPOSAL_PROJECT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'OUTPUT_PATH_PREFIX';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'OUTPUT_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PERSON_ROLE_CODE_CONTACT_PERSON';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PERSON_ROLE_CODE_CO_PROJECT_DIRECTOR';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PERSON_ROLE_CODE_OTHER';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PERSON_ROLE_CODE_PROJECT_DIRECTOR';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPE_BUDGET_REVISION_ACTIVE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPE_BUDGET_REVISION_PENDING';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPE_NEW';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPE_OTHER';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPE_TIME_EXTENTION';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PURPOSE_OTHER';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PURPOSE_RESEARCH';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'ROUTE_TO_COST_SHARE_ORGANIZATIONS_IND';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'SUBMISSION_TYPE_CHANGE'; 
delete from krew_doc_hdr_t where doc_typ_id in 
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('BudgetDocument', 'RoutingFormDocument', 'APPT','PRSN','DDT','GAR','IDCL','KEYW','NPC','NPOC','NPSC','PRJT','PRPS','QNT','RTCM')); 
delete from krew_doc_typ_attr_t where doc_typ_id in 
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('BudgetDocument', 'RoutingFormDocument', 'APPT','PRSN','DDT','GAR','IDCL','KEYW','NPC','NPOC','NPSC','PRJT','PRPS','QNT','RTCM'));
delete from krew_doc_typ_plcy_reln_t where doc_typ_id in 
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('BudgetDocument', 'RoutingFormDocument', 'APPT','PRSN','DDT','GAR','IDCL','KEYW','NPC','NPOC','NPSC','PRJT','PRPS','QNT','RTCM')); 
delete from krew_doc_typ_proc_t where doc_typ_id in 
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('BudgetDocument', 'RoutingFormDocument', 'APPT','PRSN','DDT','GAR','IDCL','KEYW','NPC','NPOC','NPSC','PRJT','PRPS','QNT','RTCM')); 
delete from krew_rte_node_t where doc_typ_id in 
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('BudgetDocument', 'RoutingFormDocument', 'APPT','PRSN','DDT','GAR','IDCL','KEYW','NPC','NPOC','NPSC','PRJT','PRPS','QNT','RTCM')); 
delete from krew_doc_typ_t where doc_typ_nm='BudgetDocument';
delete from krew_doc_typ_t where doc_typ_nm='RoutingFormDocument';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='APPT';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='PRSN';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='DDT';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='GAR';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='IDCL';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='KEYW';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='NPC';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='NPOC';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='NPSC';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='PRJT';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='PRPS';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='QNT';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='RTCM ';

--KFSMI-4251
update krim_perm_attr_data_t
set attr_val = 'collectorXmlInputFileType'
where attr_data_id = '103';
insert into krim_perm_t
(SELECT '385', sys_guid(), 1, '33', 'KFS-GL', 'Upload Batch Input File(s)',
       'Authorizes user to access the Enterprise Feed Upload page.', 'Y'
  FROM dual);
insert into krim_perm_attr_data_t
 ( SELECT '586', sys_guid(), 1, '385', '15',
       1, 'collectorFlatFileInputFileType'
  FROM dual);
  
--KFSMI-3919
delete from KRIM_ROLE_MBR_T
    WHERE role_id IN (
        select role_id from krim_role_t
            WHERE kim_typ_id IN ( '55', '49', '63' )
)
;
delete from KRIM_ROLE_PERM_T
    WHERE role_id IN (
        select role_id from krim_role_t
            WHERE kim_typ_id IN ( '55', '49', '63' )
)
;
delete from krim_role_t
    WHERE kim_typ_id IN ( '55', '49', '63' )
;

--KFSMI-4207
delete from KRNS_PARM_T 
where nmspc_cd = 'KFS-PDP' 
and parm_nm = 'DISBURSEMENT_CANCELLATION_TO_EMAIL_ADDRESSES'
;

--KFSMI-3888 fixing typo
update krim_rsp_attr_data_t
set attr_val = 'LDYE'
where attr_data_id = 459;

--KFSMI-3932
update krim_role_t
set desc_txt = 'Users who can initiate CAM transactional and maintenance documents, create new records using the asset document, maintain asset locations and modify accounting lines on invoice documents.'
where nmspc_cd = 'KFS-CAM'
and role_nm = 'Processor';
update krim_role_t
set desc_txt = 'Users identified as part of an AR billing organization. They have access to the basic functions of the KFS AR module such as creating invoices or customers.'
where nmspc_cd = 'KFS-AR'
and role_nm = 'Biller';
update krim_role_t
set desc_txt = 'Users that manage the KFS AR module. They receive Accounts Receivable transactional and maintenance documents for approval.'
where nmspc_cd = 'KFS-AR'
and role_nm = 'Manager';
update krim_role_t
set desc_txt = 'Users associated with KFS AR processing organizations. They can initiate Application and Cash control documents and add new records to the Organization Options and Organization Accounting Defaults tables.'
where nmspc_cd = 'KFS-AR'
and role_nm = 'Processor';
update krim_role_t
set desc_txt = 'Users who receive Invoice and Invoice Recurrence documents for approval.'
where nmspc_cd = 'KFS-AR'
and role_nm = 'Invoice Recurrence Instance Reviewer';
update krim_role_t
set desc_txt = 'Users authorized to create Invoice Recurrence documents.'
where nmspc_cd = 'KFS-AR'
and role_nm = 'Invoice Recurrence Creator';
update krim_role_t
set desc_txt = 'Users authorized to edit Budget Construction Documents.'
where nmspc_cd = 'KFS-BC'
and role_nm = 'Document Editor';
update krim_role_t
set desc_txt = 'Users who have view-only access to Budget Construction Documents.'
where nmspc_cd = 'KFS-BC'
and role_nm = 'Document Viewer';
update krim_role_t
set desc_txt = 'Users who can use the Organization Salary Setting, import/export payrate and unlock options on the Budget Construction Document.'
where nmspc_cd = 'KFS-BC'
and role_nm = 'Processor';
update krim_role_t
set desc_txt = 'Users that manage the KFS-CAM module. They can initiate Barcode Inventory Error documents and have access to take restricted actions on assets and modify fields on the asset document that other users do not.'
where nmspc_cd = 'KFS-CAM'
and role_nm = 'Manager';
update krim_role_t
set desc_txt = 'Users who can initiate CAM transactional and maintenance documents, create new records using the asset document, maintain asset locations and modify accounting lines on invoice documents.'
where nmspc_cd = 'KFS-CAM'
and role_nm = 'Processor';
update krim_role_t
set desc_txt = 'Users borrowing assets on the Equipment Loan/Return document.'
where nmspc_cd = 'KFS-CAM'
and role_nm = 'Asset Borrower';
update krim_role_t
set desc_txt = 'Users who receive  workflow action requests when Proposal or Award documents involve research risk.'
where nmspc_cd = 'KFS-CG'
and role_nm = 'Research Risk Reviewer';
update krim_role_t
set desc_txt = 'Users authorized to work the Cash Management Document and verify Cash Receipt documents for a given campus.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Cash Manager';
update krim_role_t
set desc_txt = 'Users who receive workflow action requests for Disbursement Vouchers based on the campus code associated with the initiator of the document.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Disbursement Manager';
update krim_role_t
set desc_txt = 'Users authorized to use the Service Billing document and enter specified accounts on the "Income" side of the document.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Service Bill Processor';
update krim_role_t
set desc_txt = 'Users who receive workflow action requests for Disbursement Vouchers for travel payment reasons and can edit the accounting line and Non-Employee Travel Expense or Pre-Paid Travel Expenses tabs.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Travel Manager';
update krim_role_t
set desc_txt = 'Users who receive workflow action requests for Disbursement Vouchers with specified payment methods and can edit the accounting lines and Wire Transfer and Foreign Draft tabs.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Disbursement Method Reviewer';
update krim_role_t
set desc_txt = 'Users authorized to initiate Cash Receipt documents. This role exists to exclude Cash Managers from being able to initiate Cash Receipt documents. You do not need to add explicit members to this role to accomplish this exclusion.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Cash Receipt Initiator';
update krim_role_t
set desc_txt = 'Users that manage the KFS-FP module. This role has no inherent permissions or responsibilities.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Financial Processing Manager';
update krim_role_t
set desc_txt = 'Users authorized to use the Collector Upload screen.'
where nmspc_cd = 'KFS-GL'
and role_nm = 'Interdepartmental Billing Processor';
update krim_role_t
set desc_txt = 'Users associated with PDP customers that can use the  Payment File Batch Upload screen and have basic PDP inquiry access. '
where nmspc_cd = 'KFS-PDP'
and role_nm = 'Customer Contact';
update krim_role_t
set desc_txt = 'Users who can cancel or hold payments reset locked format processes and view unmasked bank routing and account numbers in PDP.'
where nmspc_cd = 'KFS-PDP'
and role_nm = 'Manager';
update krim_role_t
set desc_txt = 'Users who can set payments for immediate pay and use the Format Checks/ACH screen in PDP.'
where nmspc_cd = 'KFS-PDP'
and role_nm = 'Processor';
update krim_role_t
set desc_txt = 'Accounts Payable users who can initiate Payment Requests and Credit Memo documents.  They also have several permissions related to processing these document types and receive workflow action requests for them. '
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Accounts Payable Processor';
update krim_role_t
set desc_txt = 'Users who receive workflow action requests for Purchasing transactional documents that contain a specific commodity code and campus combination.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Commodity Reviewer';
update krim_role_t
set desc_txt = 'Users who receive incomplete Requisition documents for completion for a given Chart and Organization.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Content Reviewer';
update krim_role_t
set desc_txt = 'Contract Managers review and approve Purchase Order documents. A Purchase Order is assigned to a given Contract Manager for their review and approval.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Contract Manager';
update krim_role_t
set desc_txt = 'This role represents central or campus Purchasing staff. They have additional permissions for and receive action requests for most Purchasing document types as well as receiving action requests for Disbursement Vouchers paying PO Type Vendors.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Purchasing Processor';
update krim_role_t
set desc_txt = 'Users authorized to view KFS-PURAP documents identified with a specific Sensitive Data Code. '
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Sensitive Data Viewer';
update krim_role_t
set desc_txt = 'Users who wish to receive workflow action requests for KFS-PURAP documents that involve a specific account number and sub-account number.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Sub-Account Reviewer';
update krim_role_t
set desc_txt = 'Identifies the user who routed the source document (Requisition) for a KFS-PURAP document.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Source Document Router';
update krim_role_t
set desc_txt = 'Central administration users charged with reviewing Purchase Order documents that exceed an account''s sufficient funds balance.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Budget Reviewer';
update krim_role_t
set desc_txt = 'This role houses other roles and indicates which of those can view KFS-PURAP documents that have been identified as potentially sensitive. '
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Potentially Sensitive Document User';
update krim_role_t
set desc_txt = 'A role that derives the users who initiated or received a workflow action request for a sensitive KFS-PURAP document.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Sensitive Related Document Initiator Or Reviewer';
update krim_role_t
set desc_txt = 'This role derives users who placed a Payment Request or Credit Memo on hold or canceled it in order to determine who can remove those actions. '
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Payment Request Hold / Cancel Initiator';
update krim_role_t
set desc_txt = 'Users who can use the Electronic Fund Transfer screen and use DI or YEDI documents to claim those funds.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Treasury Manager';
update krim_role_t
set desc_txt = 'An optional role that allows users to receive workflow action requests for documents of a specified type that contain accounts belonging to a specified chart and organization (including the organization hierarchy) and within a certain dollar amount  or involving a specified override code.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Accounting Reviewer';
update krim_role_t
set desc_txt = 'Users with manager-level access to Accounts Payable documents. This includes the ability to hold or cancel (or remove those states) from Payment Request and Credit Memo documents. '
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Accounts Payable Manager';
update krim_role_t
set desc_txt = 'Central Accounts Receivable staff that receive workflow action requests for Cash Control and Lockbox documents. They can also use the Electronic Fund Transfer screen and claim those funds using a Cash Control document.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Accounts Receivable Lockbox Manager';
update krim_role_t
set desc_txt = 'Users that manage the KFS-AR module.  This role has no inherent permissions or responsibilities.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Accounts Receivable Manager';
update krim_role_t
set desc_txt = 'A role that uses the Affiliation Type and Employee Status on a Principal record to determine if a user is an active faculty or staff employee. These users can initiate some KFS-PURAP documents and inquire into certain KFS screens.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Active Faculty or Staff';
update krim_role_t
set desc_txt = 'A role that uses the Employee Status (A,L or P) and Employee Type (P) to determine that a given Principal represents a professional staff employee. These users are allowed to be Account Supervisors or Account Managers on Accounts.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Active Professional Employee';
update krim_role_t
set desc_txt = 'Central Capital Assets staff capable of taking restricted actions on Assets, including retiring or transferring non-moveable assets.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Asset Manager';
update krim_role_t
set desc_txt = 'Central Capital Assets staff capable of applying asset payments, using KFS-CAB and adding negative payments.  This role contains permissions to modify restricted asset fields and to override the defined capitalization threshold.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Asset Processor';
update krim_role_t
set desc_txt = 'Defines users responsible for managing the chart data for a given Chart of Accounts code. They may initiate Global Object Code and Organization Reversion maintenance documents and modify the Campus and Organization Plant Chart Code and Account on Organization documents.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Chart Manager';
update krim_role_t
set desc_txt = 'Central contract and grant staff that have special permissions related to Effort Certification. They can override the edit that prevents transferring salary for an open effort reporting period and receive workflow action requests for Effort Certification Recreates.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Contracts & Grants Manager';
update krim_role_t
set desc_txt = 'Central contract and grant staff that receive workflow action requests for transactions involving grant accounts.  They can view Research Risk information on Proposal documents, establish cost share sub-accounts and modify the object codes on Salary Expense Transfer documents.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Contracts & Grants Processor';
update krim_role_t
set desc_txt = 'This role defines the list of users that may be selected as Project Directors on the Proposal or Award document.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Contracts & Grants Project Director';
update krim_role_t
set desc_txt = 'This role derives its members from the Fiscal Officer field on the Account. Fiscal Officers receive workflow action requests for most transactional documents and have edit permissions that allow them to change accounting lines involving their accounts.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Fiscal Officer';
update krim_role_t
set desc_txt = 'This role derives its members from the Primary delegates defined in the Account Delegate table in KFS.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Fiscal Officer Primary Delegate';
update krim_role_t
set desc_txt = 'This role derives its members from the Secondary delegates defined in the Account Delegate table in KFS.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Fiscal Officer Secondary Delegate';
update krim_role_t
set desc_txt = 'This role represents a collection of all  the KFS module manager roles and has permission to initiate simple maintenance documents and restricted documents such as the JV and LLJV. These users also have the ability to blanket approve most document types and assign roles and permissions for all KFS namespaces.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Manager';
update krim_role_t
set desc_txt = 'This role represents a very select central processing function allowed to run KFS batch jobs, initiate GLCP and LLCP documents and upload Enterprise Feed and Procurement Card files.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Operations';
update krim_role_t
set desc_txt = 'This role manages the plant fund functions associated with KFS-CAM and has special permissions related to assets in support of these functions. It can also edit the Organization and Campus Plant Chart and Account fields on the Organization document.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Plant Fund Accountant';
update krim_role_t
set desc_txt = 'Users that manage the KFS-PURAP module.  This role can take the resend  action on Purchase Order documents.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Purchasing Manager';
update krim_role_t
set desc_txt = 'Users who receive workflow action requests for documents that include accounts belonging to particular sub-funds groups.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Sub-Fund Reviewer';
update krim_role_t
set desc_txt = 'Users with a need to view unmasked Tax ID numbers. They can also modify the tax number associated with AR customer records and PURAP vendor records.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Tax Identification Number User';
update krim_role_t
set desc_txt = 'Represents a central tax area that receives workflow action requests for DVs, Payment Requests, and POs involving payments to non-resident aliens or employees. They can also edit the Tax tabs on the DV and Payment Request documents.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Tax Manager';
update krim_role_t
set desc_txt = 'A technical administrator that is specific to the KFS system. This role has no inherent permissions or responsibilities.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Technical Administrator';
update krim_role_t
set desc_txt = 'This role derives its members from the KFS Chart table. It is used to determine the Chart Manager of the top level Chart in the organization hierarchy. This role receives workflow action requests for Chart documents and has the ability to edit the organization and campus Plant Chart and Account fields on the Organization document.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'University Chart Manager';
update krim_role_t
set desc_txt = 'The basic role that grants users access to KFS. It gives users the ability to initiate most documents and use inquiries and search screens.  Users are qualified by namespace, chart and organization. If these fields are not defined the chart and organization are inherited from the Department ID on the users'' Principal record.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'User';
update krim_role_t
set desc_txt = 'Users capable of taking superuser action on KFS documents and blanket approving some document types not available to the KFS-SYS Manager role.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Workflow Administrator';
update krim_role_t
set desc_txt = 'This role represents the KFS System User, that is the user ID the system uses when it takes programmed actions (such as auto-initiating or approving documents such as the PCDO and PO).'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'System User';
update krim_role_t
set desc_txt = 'An optional role that allows users to receive workflow action requests for documents of a specified type that include a specified chart and organization (including the organization hierarchy).,'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Organization Reviewer';
update krim_role_t
set desc_txt = 'This role is derived from the accounts appearing on an Effort Certification document. KFS finds the most recent award associated with each account and routes workflow action requests to the Project Director''s associated with the accounts on the Effort Certification document.,'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Award Project Director';
update krim_role_t
set desc_txt = 'This role is derived from users with the Modify Batch Job permission. They are able to use the Schedule lookup.,'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Batch Job Modifier';
update krim_role_t
set desc_txt = 'This role derives its members from the Account Supervisor field on the Account. Account Supervisors receive workflow action requests for Asset and Asset Retirement Global documents.,'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Account Supervisor';
update krim_role_t
set desc_txt = 'This role derives its members from the Account Supervisor field on the Account. Account Supervisors receive workflow action requests for Asset and Asset Retirement Global documents.,'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Active Employee & Financial System User';
update krim_role_t
set desc_txt = 'A role that uses the Employee Status (A,L or P) and Employee Type (P), along with the presence of the KFS-SYS User role to determine that a given Principal represents a professional staff employee with KFS access. These users are allowed to be fiscal Officers on Accounts.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Active Professional Employee & Financial System User';
update krim_role_t
set desc_txt = 'This role receives workflow action requests for the Vendor document.'
where nmspc_cd = 'KFS-VND'
and role_nm = 'Reviewer';
update krim_role_t
set desc_txt = 'This role derives its members from users with the Edit Document permission for a given document type.,'
where nmspc_cd = 'KR-NS'
and role_nm = 'Document Editor';
update krim_role_t
set desc_txt = 'This role derives its members from users with the Open Document permission for a given document type.,'
where nmspc_cd = 'KR-NS'
and role_nm = 'Document Opener';
update krim_role_t
set desc_txt = 'This  role can take superuser actions and blanket approve RICE documents as well as being able to modify and assign permissions, responsibilities and roles belonging to the KR namespaces.'
where nmspc_cd = 'KR-SYS'
and role_nm = 'Technical Administrator';
update krim_role_t
set desc_txt = 'This role represents the KR System User, that is the user ID RICE uses when it takes programmed actions.'
where nmspc_cd = 'KR-SYS'
and role_nm = 'System User';
update krim_role_t
set desc_txt = 'This role derives its members from users with the Initiate Document permission for a given document type.'
where nmspc_cd = 'KR-SYS'
and role_nm = 'Document Initiator';
update krim_role_t
set desc_txt = 'This role derives its members from users with that have received an action request for a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Approve Request Recipient';
update krim_role_t
set desc_txt = 'This role derives its members from the initiator listed within the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Initiator';
update krim_role_t
set desc_txt = 'This role derives its members from the initiator and action request recipients listed within the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Initiator or Reviewer';
update krim_role_t
set desc_txt = 'This role derives its members from the user who took the Complete action on a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Router';
update krim_role_t
set desc_txt = 'This role derives its members from users with an acknowledge action request in the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Acknowledge Request Recipient';
update krim_role_t
set desc_txt = 'This role derives its members from users with an FYI action request in the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'FYI Request Recipient';
update krim_role_t
set desc_txt = 'This role derives its members from users with an Approval action request (that was not generated via the ad-hoc recipients tab) in the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Non-Ad Hoc Approve Request Recipient';
update krim_role_t
set desc_txt = 'This role derives its members from the users in the Principal table. This role gives users high-level permissions to interact with RICE documents and to login to KUALI.'
where nmspc_cd = 'KUALI'
and role_nm = 'User';

--KFSMI-4289
update krim_role_rsp_actn_t set frc_actn = 'Y' where role_rsp_actn_id = 103;

--KFSMI-4283
insert into krew_doc_typ_t
(doc_typ_id, parnt_id,doc_typ_nm, doc_typ_ver_nbr, actv_ind, cur_ind, lbl, obj_id)
select KREW_DOC_HDR_S.nextval, doc_typ_id, 'CHKR', 1, 1,1,'Check Reissued', sys_guid() from krew_doc_typ_t where doc_typ_nm = 'FSLO'
/
insert into krew_doc_typ_t
(doc_typ_id, parnt_id,doc_typ_nm, doc_typ_ver_nbr, actv_ind, cur_ind, lbl, obj_id)
select KREW_DOC_HDR_S.nextval, doc_typ_id, 'CHKC', 1, 1,1,'Check Canceled', sys_guid() from krew_doc_typ_t where doc_typ_nm = 'FSLO'
/
insert into krew_doc_typ_t
(doc_typ_id, parnt_id,doc_typ_nm, doc_typ_ver_nbr, actv_ind, cur_ind, lbl, obj_id)
select KREW_DOC_HDR_S.nextval, doc_typ_id, 'ACHR', 1, 1,1,'ACH Reissued', sys_guid() from krew_doc_typ_t where doc_typ_nm = 'FSLO'
/
insert into krew_doc_typ_t
(doc_typ_id, parnt_id,doc_typ_nm, doc_typ_ver_nbr, actv_ind, cur_ind, lbl, obj_id)
select KREW_DOC_HDR_S.nextval, doc_typ_id, 'ACHC', 1, 1,1,'ACH Canceled', sys_guid() from krew_doc_typ_t where doc_typ_nm = 'FSLO'
/

--KFSMI-3977
insert into krim_role_mbr_attr_data_t
select '4040',sys_guid(),1,'1311','17','12','*' from dual
/
insert into krim_role_mbr_attr_data_t
select '4041',sys_guid(),1,'1354','17','12','*' from dual
/

--KFSMI-4146
INSERT INTO KRNS_PARM_T(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD)
    VALUES('KR-NS', 'Document', 'ALLOW_ENROUTE_BLANKET_APPROVE_WITHOUT_APPROVAL_REQUEST_IND', sys_guid(), 1, 'CONFG', 'N', 'Controls whether the nervous system will show the blanket approve button to a user who is authorized for blanket approval but is neither the initiator of the particular document nor the recipient of an active, pending, approve action request.', 'A')
/

--KFSMI-4290
update krns_parm_t set parm_nm = 'CAPITAL_SUB_FUND_GROUPS' where nmspc_cd = 'KFS-COA' and parm_dtl_typ_cd = 'Account' and parm_nm = 'CAPITAL_SUB_FUND_GROUP' 
/

--KFSMI-4291
delete from krns_parm_t where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'PurgeCorrectionProcessFilesStep' and parm_nm = 'NUMBER_OF_DAYS_FINAL'
/
delete from krns_parm_t where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'ClearOldOriginEntryStep' and parm_nm = 'RETAIN_DAYS'
/