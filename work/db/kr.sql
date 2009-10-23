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

--KFSMI-4667
insert into krim_role_perm_t(role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
    values ('321',sys_guid(),1, '41','212','Y')
/

--KFSMI-3949 Part III
update krns_parm_t set parm_nm = 'NUMBER_OF_DAYS_SINCE_LAST_UPDATE' where nmspc_cd = 'KFS-SYS' and parm_dtl_typ_cd = 'PurgeSessionDocumentsStep' and parm_nm = 'NUMBER_OF_DAYS_SINCE_LAST_ UPDATE'

--KFSMI-4283 Part 2
update krew_doc_typ_t set lbl = 'Check Cancel & Re-Issue' where doc_typ_nm = 'CHKR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set lbl = 'Check Cancellation' where doc_typ_nm = 'CHKC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set lbl = 'ACH Cancel & Re-Issue' where doc_typ_nm = 'ACHR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set lbl = 'ACH Cancellation' where doc_typ_nm = 'ACHC' and actv_ind = 1 and cur_ind = 1
/

-- KFSMI-4632
insert into krim_perm_t (perm_id, obj_id, ver_nbr, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind)
values ('386', sys_guid(), 1, '27', 'KFS-PDP', 'Full Unmask Field', 'Authorizes users to view the entire bank routing number on the Payment Detail Inquiry.', 'Y')
/
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('587', sys_guid(), 1, '386', '11', '5', 'PaymentGroup')
/
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('588', sys_guid(), 1, '386', '11', '6', 'achBankRoutingNbr')
/
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
values ('710', sys_guid(), 1, '19', '386', 'Y')
/
insert into krim_perm_t (perm_id, obj_id, ver_nbr, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind)
values ('387', sys_guid(), 1, '27', 'KFS-PDP', 'Full Unmask Field', 'Authorizes users to view the entire bank account number on the Payment Detail Inquiry.', 'Y')
/
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('589', sys_guid(), 1, '387', '11', '5', 'AchAccountNumber')
/
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('590', sys_guid(), 1, '387', '11', '6', 'achBankAccountNbr')
/
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
values ('711', sys_guid(), 1, '19', '387', 'Y')
/

-- KFSMI-4794
insert into krns_parm_t (nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd, appl_nmspc_cd)
values ('KFS-CAM','AssetRetirementGlobal','CAPITALIZATION_LINE_DESCRIPTION',sys_guid(),1,'CONFG','Asset retirement cost reversal entry','The description of the ledger entry eliminating capitalized costs of an asset being retired.','A','KFS')
/
insert into krns_parm_t (nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd, appl_nmspc_cd)
values ('KFS-CAM','AssetRetirementGlobal','ACCUMULATED_DEPRECIATION_LINE_DESCRIPTION',sys_guid(),1,'CONFG','Asset retirement depreciation reversal','The description of the ledger entry eliminating accumulated depreciation of an asset being retired.','A','KFS')
/
insert into krns_parm_t (nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd, appl_nmspc_cd)
values ('KFS-CAM','AssetRetirementGlobal','OFFSET_AMOUNT_LINE_DESCRIPTION',sys_guid(),1,'CONFG','Asset retirement fund balance adjustment','The description of the ledger entry eliminating fund balance of an asset being retired.','A','KFS')
/

-- KFSMI-4266
insert into krim_role_mbr_t (role_mbr_id, ver_nbr, obj_id, role_id, mbr_id, mbr_typ_cd)
values ('1707', 1, sys_guid(), '28', '2615708929', 'P')
/
insert into krim_role_mbr_attr_data_t (attr_data_id, obj_id, ver_nbr, role_mbr_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('4042', sys_guid(), 1, '1707', '30', '22', 'IU')
/
insert into krim_role_mbr_attr_data_t (attr_data_id, obj_id, ver_nbr, role_mbr_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('4043', sys_guid(), 1, '1707', '30', '24', 'UNIV')
/
insert into krim_role_mbr_attr_data_t (attr_data_id, obj_id, ver_nbr, role_mbr_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('4044', sys_guid(), 1, '1707', '30', '13', 'FPYE')
/
insert into krim_role_rsp_actn_t (role_rsp_actn_id, obj_id, ver_nbr, actn_typ_cd, actn_plcy_cd, role_mbr_id, role_rsp_id, frc_actn)
values ('171', sys_guid(), 1, 'F', 'F', '1707', '1122', 'N')
/
insert into krim_role_mbr_t (role_mbr_id, ver_nbr, obj_id, role_id, mbr_id, mbr_typ_cd)
values ('1708', 1, sys_guid(), '28', '2615708929', 'P')
/
insert into krim_role_mbr_attr_data_t (attr_data_id, obj_id, ver_nbr, role_mbr_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('4045', sys_guid(), 1, '1708', '30', '22', 'IU')
/
insert into krim_role_mbr_attr_data_t (attr_data_id, obj_id, ver_nbr, role_mbr_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('4046', sys_guid(), 1, '1708', '30', '24', 'UNIV')
/
insert into krim_role_mbr_attr_data_t (attr_data_id, obj_id, ver_nbr, role_mbr_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('4047', sys_guid(), 1, '1708', '30', '13', 'LDYE')
/
insert into krim_role_rsp_actn_t (role_rsp_actn_id, obj_id, ver_nbr, actn_typ_cd, actn_plcy_cd, role_mbr_id, role_rsp_id, frc_actn)
values ('172', sys_guid(), 1, 'F', 'F', '1708', '1123', 'N')
/

-- KFSMI-4690
delete from krim_role_perm_t where perm_id = '226'
/
delete from krim_perm_attr_data_t where perm_id = '226'
/
delete from krim_perm_t where perm_id = '226'
/
delete from krim_role_perm_t where perm_id = '227'
/
delete from krim_perm_attr_data_t where perm_id = '227'
/
delete from krim_perm_t where perm_id = '227'
/
delete from krim_role_perm_t where perm_id = '267'
/
delete from krim_perm_attr_data_t where perm_id = '267'
/
delete from krim_perm_t where perm_id = '267'
/
delete from krim_role_perm_t where perm_id = '50'
/
delete from krim_perm_attr_data_t where perm_id = '50'
/
delete from krim_perm_t where perm_id = '50'
/
delete from krew_doc_hdr_t where doc_typ_id in
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('KBD', 'KRFD'))
/
delete from krew_doc_typ_attr_t where doc_typ_id in
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('KBD', 'KRFD'))
/
delete from krew_doc_typ_plcy_reln_t where doc_typ_id in
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('KBD', 'KRFD'))
/
delete from krew_doc_typ_proc_t where doc_typ_id in
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('KBD', 'KRFD'))
/
delete from KREW_RTE_NODE_CFG_PARM_T where rte_node_id in
(
select rte_node_id from krew_rte_node_t where doc_typ_id in
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('KBD', 'KRFD'))
)
/
delete from krew_rte_node_t where doc_typ_id in
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('KBD', 'KRFD'))
/
delete from krew_doc_typ_t where doc_typ_nm='KBD'
/
delete from krew_doc_typ_t where doc_typ_nm='KRFD'
/

-- KFSMI-4811
insert into krim_perm_t (perm_id, obj_id, ver_nbr, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind)
values ('390',sys_guid(), 1, '9', 'KFS-SYS', 'Ad Hoc Review Document', 'Authorizes users to take the Approve action on KFS documents Ad Hoc routed to them.', 'Y')
/
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('591', sys_guid(), 1, '390', '5', '13', 'KFS')
/
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('592', sys_guid(), 1, '390', '5', '14', 'A')
/
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
values ('712', sys_guid(), 1, '54', '390', 'Y')
/

insert into krim_perm_t (perm_id, obj_id, ver_nbr, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind)
values ('391',sys_guid(), 1, '9', 'KFS-SYS', 'Ad Hoc Review Document', 'Authorizes users to take the FYI action on KFS documents Ad Hoc routed to them.', 'Y')
/
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('593', sys_guid(), 1, '391', '5', '13', 'KFS')
/
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('594', sys_guid(), 1, '391', '5', '14', 'F')
/
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
values ('713', sys_guid(), 1, '54', '391', 'Y')
/
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
values ('714', sys_guid(), 1, '32', '391', 'Y')
/

insert into krim_perm_t (perm_id, obj_id, ver_nbr, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind)
values ('392',sys_guid(), 1, '9', 'KFS-SYS', 'Ad Hoc Review Document', 'Authorizes users to take the Acknowledge action on KFS documents Ad Hoc routed to them.', 'Y')
/
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('595', sys_guid(), 1, '392', '5', '13', 'KFS')
/
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('596', sys_guid(), 1, '392', '5', '14', 'K')
/
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
values ('715', sys_guid(), 1, '54', '392', 'Y')
/
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
values ('716', sys_guid(), 1, '32', '392', 'Y')
/

insert into krim_perm_t (perm_id, obj_id, ver_nbr, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind)
values ('393',sys_guid(), 1, '9', 'KUALI', 'Ad Hoc Review Document', 'Authorizes users to take any requested action on RICE documents Ad Hoc routed to them.', 'Y')
/
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('597', sys_guid(), 1, '393', '5', '13', 'Rice Document')
/
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
values ('717', sys_guid(), 1, '1', '393', 'Y')
/

-- KFSMI-4818
update krns_parm_t set appl_nmspc_cd = 'KFS' where nmspc_cd like 'KFS%'
/
update krns_parm_t set appl_nmspc_cd = 'KFS' where nmspc_cd = 'KR-NS' and parm_dtl_typ_cd = 'All' and parm_nm = 'ENABLE_DIRECT_INQUIRIES_IND'
/
update krns_parm_t set appl_nmspc_cd = 'KFS' where nmspc_cd = 'KR-NS' and parm_dtl_typ_cd = 'All' and parm_nm = 'ENABLE_FIELD_LEVEL_HELP_IND'
/
insert into krns_parm_t (nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd, appl_nmspc_cd)
values ('KR-NS','All','ENABLE_DIRECT_INQUIRIES_IND', sys_guid(), 1, 'CONFG', 'Y', 'Flag for enabling/disabling direct inquiries on screens that are drawn by the nervous system (i.e. lookups and maintenance documents)', 'A', 'KUALI')
/
insert into krns_parm_t (nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd, appl_nmspc_cd)
values ('KR-NS','All','ENABLE_FIELD_LEVEL_HELP_IND', sys_guid(), 1, 'CONFG', 'Y', 'Indicates whether field level help links are enabled on lookup pages and documents.', 'A', 'KUALI')
/

-- KFSMI-4680
update krim_role_t set desc_txt = 'An optional role created to receive action requests for Budget Adjustment documents at the Organization Route Node. Intended to receive requests for the top level chart and organization (thus receiving all Budget Adjustment documents).' where nmspc_cd = 'KFS-SYS' and role_nm = 'University Administration Budget Manager'
/
update krim_role_t set desc_txt = 'An optional role created to receive action requests for Budget Adjustment documents at the Organization route Node. Intended to receive only requests associated with regional campus charts and organizations.' where nmspc_cd = 'KFS-SYS' and role_nm = 'Regional Budget Manager'
/
update krim_role_t set desc_txt = 'A role that uses the Employee Status (A,L or P) along with the presence of the KFS-SYS User role to determine that a given Principal represents an employee with KFS access. These users are allowed to be fiscal Officers on Accounts.' where nmspc_cd = 'KFS-SYS' and role_nm = 'Active Employee & Financial System User'
/
delete from krim_role_perm_t where perm_id = '208'
/
delete from krim_perm_attr_data_t where perm_id = '208'
/
delete from krim_perm_t where perm_id = '208'
/
delete from krim_role_perm_t where perm_id = '213'
/
delete from krim_perm_attr_data_t where perm_id = '213'
/
delete from krim_perm_t where perm_id = '213'
/
delete from krim_role_perm_t where perm_id = '216'
/
delete from krim_perm_attr_data_t where perm_id = '216'
/
delete from krim_perm_t where perm_id = '216'
/
delete from krim_role_perm_t where perm_id = '218'
/
delete from krim_perm_attr_data_t where perm_id = '218'
/
delete from krim_perm_t where perm_id = '218'
/
delete from krim_role_perm_t where perm_id = '219'
/
delete from krim_perm_attr_data_t where perm_id = '219'
/
delete from krim_perm_t where perm_id = '219'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Financial Processing Transactional Document when a document is at the Account Node of routing.' where perm_id = '190'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Target accounting lines on documents answering to the parent document Financial Processing Transactional Document when a document is at the Account Node of routing.' where perm_id = '191'
/
update krim_perm_t set desc_txt = 'Allows users to modify the object code of Source accounting lines on a Disbursement Voucher document that is at the Campus Node of routing.' where perm_id = '192'
/
update krim_perm_t set desc_txt = 'Allows users to modify the amount of Source accounting lines on a Disbursement Voucher document that is at the Tax Node of routing.' where perm_id = '193'
/
update krim_perm_t set desc_txt = 'Allows users to modify the amount of Source accounting lines on a Disbursement Voucher document that is at the Travel Node of routing.' where perm_id = '194'
/
update krim_perm_t set desc_txt = 'Allows users to modify the amount of Source accounting lines on a Disbursement Voucher document that is at the Payment Method Node of routing.' where perm_id = '195'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Target accounting lines on documents answering to the parent document Labor Distribution Transactional Document when a document is at the Account Node of routing.' where perm_id = '196'
/
update krim_perm_t set desc_txt = 'Allows users to modify the object code on Target (To side) accounting lines of the Salary Expense Transfer document.' where perm_id = '197'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Purchasing Transactional Document when a document is at the Account Node of routing.' where perm_id = '198'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Accounts PayableTransactional Document when a document is at the Account Node of routing.' where perm_id = '199'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Source accounting lines on Asset Payment document at the Account Node of routing.' where perm_id = '200'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Accounts Financial Processing Transactional Document when a document has not yet been submitted for routing.' where perm_id = '238'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Target accounting lines on documents answering to the parent document Financial Processing Transactional Document when a document has not yet been submitted for routing.' where perm_id = '239'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Purchasing Transactional Document when a document has not yet been submitted for routing.' where perm_id = '241'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Source accounting lines on documents answering to the parent document Accounts PayableTransactional Document when a document has not yet been submitted for routing.' where perm_id = '242'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Source accounting lines on a Procurement Card Document that is at the Account Full Entry Node of routing.' where perm_id = '280'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Source accounting lines on a Requisition Document that is at the Initiator Node of routing.' where perm_id = '308'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Source accounting lines on a Purchase Order Amendment Document that is at the New Unordered Items Node of routing.' where perm_id = '355'
/
update krim_perm_t set desc_txt = 'Allows users to modify the object code of Source accounting lines on a Disbursement Voucher Document that is at the Travel Node of routing.' where perm_id = '358'
/
update krim_perm_t set desc_txt = 'Allows users to modify the Invoice Item Description of Source accounting lines on an Invoice document that is at the Recurrence Node of routing.' where perm_id = '371'
/
update krim_perm_t set desc_txt = 'Users who can edit the Bank Code field on documents answering to the parent document Financial System Transactional Document.' where perm_id = '220'
/
update krim_perm_t set desc_txt = 'Users who can edit the Bank Code field on the Cash Management Document.' where perm_id = '372'
/
update krim_perm_t set desc_txt = 'Users who can edit the Bank Code field on the Payment Request Document.' where perm_id = '373'
/
update krim_perm_t set desc_txt = 'Users who can edit the Bank Code field on the Disbursement Voucher Document.' where perm_id = '374'
/
update krim_perm_t set desc_txt = 'Users who can use a Distribution of Income and Expense document to claim Electronic Payments.' where perm_id = '66'
/
update krim_perm_t set desc_txt = 'Edit permission for the Requisition document prior to the document being submitted.' where perm_id = '100'
/
update krim_perm_t set desc_txt = 'Edit permission for the Purchase Order document prior to the document being submitted.' where perm_id = '101'
/
update krim_perm_t set desc_txt = 'Edit permission for Accounts Payable Transactional documents prior to the document being submitted.' where perm_id = '102'
/
update krim_perm_t set desc_txt = 'Edit permission for Receiving Transactional documents prior to the document being submitted.' where perm_id = '103'
/
update krim_perm_t set desc_txt = 'Allows users to edit Kuali documents prior to them being submitted for routing.' where perm_id = '180'
/
update krim_perm_t set desc_txt = 'The budget construction document does not really use workflow, and all editing is done after the workflow status is final. This permission allows the derived, Budget Construction Document Editor role to allow access to edit the document based on other roles, e.g. Fiscal Officer for account, Organization Reveiwer for org.' where perm_id = '266'
/
update krim_perm_t set desc_txt = 'Authorizes users who can edit Credit Memo Documents that are in PROCESSED status.' where perm_id = '367'
/
update krim_perm_t set desc_txt = 'Authorizes users who can edit Credit Memo Documents that are in FINAL status.' where perm_id = '368'
/
update krim_perm_t set desc_txt = 'Authorizes users to edit Simple Maintenance document at the AdHoc route node.' where perm_id = '382'
/
update krim_perm_t set desc_txt = 'Authorizes users to edit Complex Maintenance document at the AdHoc route node.' where perm_id = '383'
/
update krim_perm_t set desc_txt = 'Allows access to the Copy button on KFS Financial System Documents.' where perm_id = '156'
/
update krim_perm_t set desc_txt = 'Allow users to access KFS lookups.' where perm_id = '130'
/
update krim_perm_t set desc_txt = 'Allow users to access the Batch File lookup.' where perm_id = '256'
/
update krim_perm_t set desc_txt = 'Allows users to access the CAB AP and GL Transactions screens.' where perm_id = '303'
/
update krim_perm_t set desc_txt = 'Allows users to access the Pre-Asset Tagging screens.' where perm_id = '375'
/
update krim_perm_t set desc_txt = 'Users who can view the Old Tag Number on the Asset document.' where perm_id = '348'
/
update krim_perm_t set desc_txt = 'Users who can view the Replacement Amount field on the Asset document.' where perm_id = '43'
/
update krim_perm_t set desc_txt = 'Users who can view the Estimated Selling Price field on the Asset document.' where perm_id = '44'
/
update krim_perm_t set desc_txt = 'Users who can view the Condition Code on the Asset document.' where perm_id = '46'
/
update krim_perm_t set desc_txt = 'Users who can view the Land County Name on the Asset document.' where perm_id = '47'
/
update krim_perm_t set desc_txt = 'Users who can view the Land Acreage Size on the Asset document.' where perm_id = '48'
/
update krim_perm_t set desc_txt = 'Users who can view the Land Parcel Number on the Asset document.' where perm_id = '49'
/
update krim_perm_t set desc_txt = 'Users who can modify the Owner Chart of Accounts Code field on the Asset document.' where perm_id = '32'
/
update krim_perm_t set desc_txt = 'Users who can modify the Owner Account Number field on the Asset document.' where perm_id = '33'
/
update krim_perm_t set desc_txt = 'Authorizes users to modify the Tax Type Code on a Vendor Maintenance Document.' where perm_id = '335'
/
update krim_perm_t set desc_txt = 'Users who can modify the Capital Asset In Service Date field on the Asset document.' where perm_id = '34'
/
update krim_perm_t set desc_txt = 'Users who can modify the Capital Asset Type Code field on the Asset document.' where perm_id = '342'
/
update krim_perm_t set desc_txt = 'Users who can modify the Capital Asset Description field on the Asset document.' where perm_id = '343'
/
update krim_perm_t set desc_txt = 'Users who can modify the Campus Tag Number field on the Asset document.' where perm_id = '344'
/
update krim_perm_t set desc_txt = 'Users who can modify the Agency Number field on the Asset document.' where perm_id = '35'
/
update krim_perm_t set desc_txt = 'Users who can modify the Vendor Name field on the Asset document.' where perm_id = '36'
/
update krim_perm_t set desc_txt = 'Users who can modify the Old Tag Number field on the Asset document.' where perm_id = '37'
/
update krim_perm_t set desc_txt = 'Users who can modify the Government Tag Number field on the Asset document.' where perm_id = '38'
/
update krim_perm_t set desc_txt = 'Users who can modify the National Stock Number field on the Asset document.' where perm_id = '39'
/
update krim_perm_t set desc_txt = 'Authorizes users to edit the Sub-Account Type Code on the Sub-Account document.' where perm_id = '60'
/
update krim_perm_t set desc_txt = 'Users who can edit the Organization Plant Chart Code on the Organization document.' where perm_id = '61'
/
update krim_perm_t set desc_txt = 'Users who can edit the Organization Plant Account Number on the Organization document.' where perm_id = '62'
/
update krim_perm_t set desc_txt = 'Users who can edit the Campus Plant Chart Code on the Organization document.' where perm_id = '63'
/
update krim_perm_t set desc_txt = 'Users who can edit the Campus Plant Account Number on the Organization document.' where perm_id = '64'
/
update krim_perm_t set desc_txt = 'Authorizes users to view the password field on the Person document and inquriy.' where perm_id = '183'
/
update krim_perm_t set desc_txt = 'Allows users to view the tax id in the Disbursement Voucher.' where perm_id = '187'
/
update krim_perm_t set desc_txt = 'Authorizes users to view the entire bank routing number on the ACH Bank document and Inquiry.' where perm_id = '234'
/
update krim_perm_t set desc_txt = 'Authorizes users to view the entire bank account number on the Payee ACH document and Inquiry.' where perm_id = '235'
/
update krim_perm_t set desc_txt = 'Authorizes users to view the entire Tax Identification Number on the Person Document.' where perm_id = '306'
/
update krim_perm_t set desc_txt = 'Authorizes users to view the entire bank account number on the Wire Transfer tab of the Disbursement Voucher document.' where perm_id = '381'
/
update krim_perm_t set desc_txt = 'Allows users access to screens in the KFS-AR module that are not documents, lookups, inquiries, or batch uploads, i.e. this primarily provides access to AR reports' where perm_id = '12'
/
update krim_perm_t set desc_txt = 'Allows users access to screens in the KFS that are not documents, lookups, inquiries, or batch uploads.' where perm_id = '135'
/
update krim_perm_t set desc_txt = 'Allows users to access the Java Security Management screen. This should be granted to developers.' where perm_id = '141'
/
update krim_perm_t set desc_txt = 'Allows users to access the Message Queue screen.' where perm_id = '142'
/
update krim_perm_t set desc_txt = 'Allows users to access the Service Registry screen.' where perm_id = '143'
/
update krim_perm_t set desc_txt = 'Allows users to access the Thread Pool screen.' where perm_id = '144'
/
update krim_perm_t set desc_txt = 'Allows users to access the Quartz Queue screen. This should be granted to developers.' where perm_id = '145'
/
update krim_perm_t set desc_txt = 'Allows users to access Balance Inquiry screens. ' where perm_id = '287'
/
update krim_perm_t set desc_txt = 'Allows users to see menu of balance inquiries after hitting "balance inquiry" button on an accounting line in an accounting document.' where perm_id = '301'
/
update krim_perm_t set desc_txt = 'Allows access to the Cash Management Document screen that a user sees when they try to initiate and a document is already open for their campus. The screen provides them a link to the existing document for their campus.' where perm_id = '329'
/
update krim_perm_t set desc_txt = 'Allows access to the Cash Management Document screen from which a user can create new deposits.' where perm_id = '330'
/
update krim_perm_t set desc_txt = 'Allows access to the Cash Management Document screen from which a user can correct amounts on a closed cash drawer.' where perm_id = '331'
/
update krim_perm_t set desc_txt = 'Users who can access the Effort Detail Tab on the Effort Certification Document.' where perm_id = '206'
/
update krim_perm_t set desc_txt = 'Users who can complete the Nonresident Alien Tax Tab on the Disbursement Voucher document.' where perm_id = '209'
/
update krim_perm_t set desc_txt = 'Users who can modify the Foreign Draft tab and disbursement amount on Disbursement Voucher documents.' where perm_id = '210'
/
update krim_perm_t set desc_txt = 'Users who can modify Wire Transfer tab and disbursement amount on Disbursement Voucher documents.' where perm_id = '211'
/
update krim_perm_t set desc_txt = 'Users who can modify Non-Employee Travel Expense tab, disbursement amount on Disbursement Voucher documents.' where perm_id = '212'
/
update krim_perm_t set desc_txt = 'Users who can edit specific data on a Payment Request or Credit Memo before the document is extracted to PDP.' where perm_id = '217'
/
update krim_perm_t set desc_txt = 'Users who can print a Purchase Order document.' where perm_id = '272'
/
update krim_perm_t set desc_txt = 'Users who can preview a Purchase Order document before printing it.' where perm_id = '273'
/
update krim_perm_t set desc_txt = 'Users who can assign sensitive data to a Purchase Order document which locks down who is allowed to view the PO and its related documents.' where perm_id = '274'
/
update krim_perm_t set desc_txt = 'Users who can resend Purchase Order cxml to the B2B integrator.' where perm_id = '275'
/
update krim_perm_t set desc_txt = 'Users authorized to take the Request Cancel action on Payment Request documents.' where perm_id = '276'
/
update krim_perm_t set desc_txt = 'Authorizes users to remove Holds or Cancels on Payment Request documents.' where perm_id = '277'
/
update krim_perm_t set desc_txt = 'Users who can cancel Payment Request or Credit Memo documents at a manager level.' where perm_id = '282'
/
update krim_perm_t set desc_txt = 'Users who can cancel Payment Request or Credit Memo documents at a processor level.' where perm_id = '283'
/
update krim_perm_t set desc_txt = 'Authorizes users to put Payment Request documents on Hold.' where perm_id = '286'
/
update krim_perm_t set desc_txt = 'Authorizes users to put Credit Memo documents on Hold.' where perm_id = '293'
/
update krim_perm_t set desc_txt = 'Authorizes user to access the Collector XML Upload page.' where perm_id = '76'
/
update krim_perm_t set desc_txt = 'Authorizes users to open Purchasing Accounts Payable Transactional documents.' where perm_id = '269'
/
update krim_perm_t set desc_txt = 'Authorizes users to open the Contract Manager Assignment Document.' where perm_id = '284'
/
update krim_perm_t set desc_txt = 'Users allowed to create new and maintain existing records using the Organization Options document.' where perm_id = '248'
/
update krim_perm_t set desc_txt = 'Users allowed to maintain existing (but not create new) records using the Organization Options document.' where perm_id = '249'
/
update krim_perm_t set desc_txt = 'Users allowed to create new and maintain existing records using the Organization Accounting Default document.' where perm_id = '369'
/
update krim_perm_t set desc_txt = 'Users allowed to maintain existing (but not create new) records using the Organization Accounting Default document.' where perm_id = '370'
/
update krim_perm_t set desc_txt = 'Users allowed to create new and maintain existing records using the Pre-Asset Tagging document.' where perm_id = '376'
/
update krim_perm_t set desc_txt = 'Authorizes users to see and edit the Contracts Tab on the Vendor Maintenance Document.' where perm_id = '139'
/
update krim_perm_t set desc_txt = 'Users who can add notes and attachments to the Payment Request document when it is at the Invoice Attachment route node.' where perm_id = '258'
/
update krim_perm_t set desc_txt = 'Authorizes users to add notes and attachments with a type of "Contracts" to the Purchase Order document. ' where perm_id = '313'
/
update krim_perm_t set desc_txt = 'Authorizes users to add notes and attachments with a type of "Contract" on Purchase Order documents and documents answering to that document type.' where perm_id = '315'
/
update krim_perm_t set desc_txt = 'Authorizes users to add notes and attachments with a type of "Quotes" on Purchase Order documents and documents answering to that document type.' where perm_id = '317'
/
update krim_perm_t set desc_txt = 'Authorizes users to add notes and attachments with a type of "RFPs" on Purchase Order documents and documents answering to that document type.' where perm_id = '319'
/
update krim_perm_t set desc_txt = 'Authorizes users to add notes and attachments with a type of "RFP" on Purchase Order documents and documents answering to that document type.' where perm_id = '321'
/
update krim_perm_t set desc_txt = 'Authorizes users to add notes or attachments with a type of "Other-Restricted" on Purchase Order documents and documents answering to that document type.' where perm_id = '323'
/
update krim_perm_t set desc_txt = 'Authorizes users to add notes or attachments with a type of "Credit Memo Image" on Credit Memo documents.' where perm_id = '325'
/
update krim_perm_t set desc_txt = 'Authorizes users to view attachments with a type of "Invoice Image" on Payment Request documents.' where perm_id = '260'
/
update krim_perm_t set desc_txt = 'Authorizes users to view attachments with a type of "Contract" on Purchase Order documents and documents answering to that document type.' where perm_id = '314'
/
update krim_perm_t set desc_txt = 'Authorizes users to view attachments with a type of "Contract Amendments" on Purchase Order documents and documents answering to that document type.' where perm_id = '316'
/
update krim_perm_t set desc_txt = 'Authorizes users to view attachments with a type of "Quotes" on Purchase Order documents and documents answering to that document type.' where perm_id = '318'
/
update krim_perm_t set desc_txt = 'Authorizes users to view attachments with a type of "RFPs" on Purchase Order documents and documents answering to that document type.' where perm_id = '320'
/
update krim_perm_t set desc_txt = 'Authorizes users to view attachments with a type of "RFP" on Purchase Order documents and documents answering to that document type.' where perm_id = '322'
/
update krim_perm_t set desc_txt = 'Authorizes users to view attachments with a type of "Other-Restricted" on Purchase Order documents and documents answering to that document type.' where perm_id = '324'
/
update krim_perm_t set desc_txt = 'Authorizes users to view attachments with a type of "Invoice Image" on Credit Memo documents.' where perm_id = '384'
/
update krim_perm_t set desc_txt = 'Users authorized to send FYI Ad-Hoc action requests.' where perm_id = '332'
/
update krim_perm_t set desc_txt = 'Users authorized to send Acknowledge Ad-Hoc action requests.' where perm_id = '333'
/
update krim_perm_t set desc_txt = 'Users authorized to send Approve Ad-Hoc action requests.' where perm_id = '334'
/
update krim_perm_t set desc_txt = 'Authorizes users to view batch files using the Batch File lookup screen.' where perm_id = '362'
/
update krim_perm_t set desc_txt = 'Authorizes users to take the Approve action on KFS documents routed to them.' where perm_id = '170'
/
update krim_perm_t set desc_txt = 'Authorizes users to take the FYI action on KFS documents routed to them.' where perm_id = '172'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of Financial System Documents.' where perm_id = '115'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of Financial System Simple Maintenance documents.' where perm_id = '116'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Asset Payments Document.' where perm_id = '16'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Barcode Inventory Error Document.' where perm_id = '17'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Capital Asset Management Transactional Documents.' where perm_id = '18'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Capital Asset Complex Maintenance Documents.' where perm_id = '19'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Salary Expense Transfer Document.' where perm_id = '202'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Customer Document.' where perm_id = '245'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Invoice Document.' where perm_id = '246'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Budget Construction Document.' where perm_id = '247'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Accounts Receivable Complex Maintenance Documents.' where perm_id = '3'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of Customer Invoice Item Code Documents.' where perm_id = '349'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Cash Receipt Document.' where perm_id = '350'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Credit Memo Document.' where perm_id = '356'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of Invoice Recurrence Documents.' where perm_id = '360'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Cash Drawer Document.' where perm_id = '361'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the KIM Group document.' where perm_id = '379'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Organization Reversion Global Document.' where perm_id = '380'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of Application Documents.' where perm_id = '5'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the CFDA Close Document.' where perm_id = '51'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Global Object Code Document.' where perm_id = '55'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Cash Control Document.' where perm_id = '6'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Cash Management Document.' where perm_id = '69'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Service Billing Document.' where perm_id = '70'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Journal Voucher Document.' where perm_id = '71'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Labor Journal Voucher Document.' where perm_id = '78'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Requisition Document.' where perm_id = '92'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of Purchasing Transactional Documents.' where perm_id = '93'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Purchase Order Document.' where perm_id = '94'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Purchase Order Close Document.' where perm_id = '95'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Purchase Order Retransmit Document.' where perm_id = '96'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of the Payment Request Document.' where perm_id = '97'
/
update krim_perm_t set desc_txt = 'Authorizes the initiation of Receiving Transactional Documents.' where perm_id = '98'
/
update krim_perm_t set desc_txt = 'Allows users to open KFS documents via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node).' where perm_id = '113'
/
update krim_perm_t set desc_txt = 'Allows users to open Purchase Order Documents (and their children document types) via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node).' where perm_id = '203'
/
update krim_perm_t set desc_txt = 'Allows access to the Blanket Approval button on KFS Financial System Documents.' where perm_id = '114'
/
update krim_perm_t set desc_txt = 'Allows access to the Blanket Approval button on the Application Document.' where perm_id = '357'
/
update krim_perm_t set desc_txt = 'Authorizes users to take Approve, Acknowledge or FYI action on KUALI documents Ad Hoc routed to them.' where perm_id = '146'
/
update krim_perm_t set desc_txt = 'Users allowed to merge assets on the Asset Retirement document.' where perm_id = '21'
/
update krim_perm_t set desc_txt = 'Allows users to access the Import/Export Payrate screen in Budget Construction. By default granted to processors for the top level organization in the org hierarchy.' where perm_id = '228'
/
update krim_perm_t set desc_txt = 'Allows users to access the Unlock screen in Budget Construction. By default granted to processors for the top level organization in the org hierarchy.' where perm_id = '229'
/
update krim_perm_t set desc_txt = 'Users allowed to retire more than one asset on an Asset Retirement document.' where perm_id = '24'
/
update krim_perm_t set desc_txt = 'Authorizes users who can use the Acquisition Type of "New" on the Asset document.' where perm_id = '28'
/
update krim_perm_t set desc_txt = 'Some Rice client applications use pessimistic locking, i.e. if one user is editing a document, a lock is established to prevent other users from editing at the same time. This permission gives users the ability to administer those locks (when a user establishes a lock and leaves without submitting the document and another user needs to edit) via the pessimistic locking administration screen.' where perm_id = '289'
/
update krim_perm_t set desc_txt = 'Allows users to access Organizaton Salary Setting within Budget Construction.' where perm_id = '295'
/
update krim_perm_t set desc_txt = 'Authorizes users to edit Appointment Funding Lines within Budget Construction Salary Setting.' where perm_id = '296'
/
update krim_perm_t set desc_txt = 'Authorizes users to view Appointment Funding Lines within Budget Construction Salary Setting.' where perm_id = '297'
/
update krim_perm_t set desc_txt = 'Allows power users to bypass the security associated with certain document types to limit the result set.' where perm_id = '299'
/
update krim_perm_t set desc_txt = 'Identifies users that can be Account Supervisors.' where perm_id = '351'
/
update krim_perm_t set desc_txt = 'Identifies users that can be Account Fiscal Officers.' where perm_id = '352'
/
update krim_perm_t set desc_txt = 'Identifies users that can be Account Fiscal Officer Delegates.' where perm_id = '353'
/
update krim_perm_t set desc_txt = 'Authorizes users to edit assets tagged in a prior fiscal year.' where perm_id = '377'
/
update krim_perm_t set desc_txt = 'Identifies users that can be Account Managers' where perm_id = '59'
/

-- KFSMI-4884
update krns_parm_t set txt = 'IE=LD' where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'EncumbranceForwardStep' and parm_nm = 'FORWARD_ENCUMBRANCE_BALANCE_TYPE_AND_ORIGIN_CODE'
/

-- KFSMI-4903
insert into krns_parm_t (nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd, appl_nmspc_cd)
	values ('KFS-PURAP','Document','ENABLE_B2B_BY_VENDOR_DUNS_NUMBER_IND',sys_guid(),1,'CONFG','N','If set to N, the B2B integrator should use vendor ID to indicate the vendor. If set to Y, the B2B integrator should use the vendor DUNS number to indicate the vendor.','A','KFS')
/

-- KFSMI-4847
update krns_parm_t set txt = 'BD;BF;BI;BR;BX;IF;LE;LF;LI;LR' where nmspc_cd = 'KFS-CAM' and parm_dtl_typ_cd = 'Batch' and parm_nm = 'DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPE' and appl_nmspc_cd = 'KFS'
/

-- KFSMI-4859: please read the issue before running this
insert into krim_role_perm_t
	(role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
	values ('718', sys_guid(), 1, '61', '54', 'Y')
/

-- KFSMI-4936: fix parameter detail type on parameter
update krns_parm_t set parm_dtl_typ_cd = 'SufficientFundsAccountUpdateStep' where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'SufficientFundsRebuilderStep' and parm_nm = 'FISCAL_YEAR' and appl_nmspc_cd = 'KFS'
/

-- KFSMI-4899: still more RA data to stamp out!
delete from krim_role_mbr_attr_data_t where role_mbr_id in ('1283','1284')
/
delete from krim_role_mbr_t where role_mbr_id in ('1283','1284')
/
delete from krim_role_rsp_actn_t where role_rsp_id in (select role_rsp_id from krim_role_rsp_t where rsp_id in ('96','25'))
/
delete from krim_role_rsp_t where rsp_id in ('96','25')
/
delete from krim_rsp_attr_data_t where rsp_id in ('96','25')
/
delete from krim_rsp_t where rsp_id in ('96','25')
/

-- KFSMI-4948: RA data is like sand after a trip to the beach
delete from krim_role_mbr_attr_data_t where kim_typ_id in (select kim_typ_id from krim_typ_t where srvc_nm in ('researchTransactionalDocumentDerivedRoleTypeService', 'researchQuestionTypeRoleTypeService', 'researchTransactionalDocumentPermissionDerivedRoleTypeServiceImpl'))
/
delete from krim_typ_attr_t where kim_typ_id in (select kim_typ_id from krim_typ_t where srvc_nm in ('researchTransactionalDocumentDerivedRoleTypeService', 'researchQuestionTypeRoleTypeService', 'researchTransactionalDocumentPermissionDerivedRoleTypeServiceImpl'))
/
delete from krim_typ_t where srvc_nm in ('researchTransactionalDocumentDerivedRoleTypeService', 'researchQuestionTypeRoleTypeService', 'researchTransactionalDocumentPermissionDerivedRoleTypeServiceImpl')
/

-- KFSMI-4953: fix more permission descriptions
update krim_perm_t set desc_txt = 'Allows users to open documents answering to Accounts Payable Transactional Documents (and their children document types) via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node).' where perm_id = '204'
/
update krim_perm_t set desc_txt = 'Authorizes users to view attachments with a type of "Credit Memo Image" on Credit Memo documents.' where perm_id = '326'
/
update krim_perm_t set desc_txt = 'Allows users to open RICE Documents via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node).' where perm_id = '147'
/
update krim_perm_t set desc_txt = 'Allows users to open Procurement Card Documents via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node).' where perm_id = '68'
/

-- KFSMI-3331: add parameter so that temporary files are deleted more quickly
insert into krns_parm_t ( nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd, appl_nmspc_cd )
	values ( 'KFS-SYS', 'FilePurgeStep', 'TEMPORARY_FILES_NUMBER_OF_DAYS_OLD', SYS_GUID(), 1, 'CONFG', '1', 'The FilePurgeStep will traverse the temporary files directory, deleting files with a last modified date more that this number of days prior to today.', 'A', 'KFS' )
/

-- KFSMI-4440: new role type, new role, and update of role permissions for PREQ view/attach note for Invoice Image, CM view/attach note for Credit Memo Image, and all CM view/attach note for Invoice Image 
insert into krim_typ_t (kim_typ_id, obj_id, ver_nbr, nm, srvc_nm, actv_ind, nmspc_cd)
	values ('69', sys_guid(), 1, 'Derived Role: Accounts Payable Document Reviewer', 'accountsPayableDocumentDerivedRoleTypeService', 'Y', 'KFS-PURAP')
/
insert into krim_role_t (role_id, obj_id, ver_nbr, role_nm, nmspc_cd, desc_txt, kim_typ_id, actv_ind, last_updt_dt)
	values ('98', sys_guid(), 1,'Accounts Payable Document Reviewer','KFS-PURAP','Users who receive workflow action requests for Accounts Payable transactional documents.','69', 'Y', SYSDATE)
/
delete from krim_role_perm_t where perm_id = '260' and role_id = '68'
/
delete from krim_role_perm_t where perm_id = '260' and role_id = '28'
/
delete from krim_role_perm_t where perm_id = '260' and role_id = '41'
/
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
	values ('719', sys_guid(), 1, '98', '260', 'Y')
/
delete from krim_role_perm_t where perm_id = '384'
/
delete from krim_perm_attr_data_t where perm_id = '384'
/
delete from krim_perm_t where perm_id = '384'
/
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
	values ('721', sys_guid(), 1, '98', '326', 'Y')
/
delete from krim_role_perm_t where perm_id = '326' and role_id = '61'
/

-- KFSMI-4293: permission for Disbursement Method Reviewers to edit travel tab
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
	values ('722', sys_guid(), 1, '70', '212', 'Y')
/

-- KFSMI-4981: clean up dangling role mbr attr data
delete from krim_role_mbr_attr_data_t where role_mbr_id not in (select role_mbr_id from krim_role_mbr_t)
/

-- KFSMI-4699: clean up non current doc types
delete from krew_doc_typ_t where cur_ind = 0 and doc_typ_nm not in ('KualiDocument','RoutingRuleDocument','IdentityManagementPersonDocument', 'IdentityManagementRoleDocument', 'IdentityManagementGroupDocument')
/

-- KFSMI-4999: FOs get FYI on account delegate global (ingest the doc type first)
insert into krim_rsp_t (rsp_id, obj_id, ver_nbr, rsp_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind)
	values ('124', sys_guid(), 1, '1', 'KFS-COA', 'Review', '', 'Y')
/
insert into krim_rsp_attr_data_t(attr_data_id, obj_id, ver_nbr, rsp_id, kim_typ_id, kim_attr_defn_id, attr_val)
	values('462', sys_guid(), 1, '124', '7', '40', 'true') /* required */
/
insert into krim_rsp_attr_data_t(attr_data_id, obj_id, ver_nbr, rsp_id, kim_typ_id, kim_attr_defn_id, attr_val)
	values('463', sys_guid(), 1, '124', '7', '16', 'Account')
/
insert into krim_rsp_attr_data_t(attr_data_id, obj_id, ver_nbr, rsp_id, kim_typ_id, kim_attr_defn_id, attr_val)
	values('464', sys_guid(), 1, '124', '7', '13', 'GDLG')
/
insert into krim_rsp_attr_data_t(attr_data_id, obj_id, ver_nbr, rsp_id, kim_typ_id, kim_attr_defn_id, attr_val)
	values('465', sys_guid(), 1, '124', '7', '41', 'false') /* action details at role member level */
/
insert into krim_role_rsp_t (role_rsp_id, obj_id, ver_nbr, role_id, rsp_id, actv_ind)
	values ('1125', sys_guid(), 1, '41', '124', 'Y')
/
insert into krim_role_rsp_actn_t (role_rsp_actn_id, obj_id, ver_nbr, actn_typ_cd, actn_plcy_cd, role_mbr_id, role_rsp_id, frc_actn)
	values ('201', sys_guid(), 1, 'F', 'A', '*', '1125', 'Y')
/
delete from krew_doc_typ_t where doc_typ_nm = 'GDLG' and cur_ind = 0
/ 

-- KFSMI-5001: update help links in doc types
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCashControl.htm' where doc_typ_nm = 'CTRL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCustomerCreditMemo.htm' where doc_typ_nm = 'CRM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCustomerInvoice.htm' where doc_typ_nm = 'INV' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCustomerInvoiceWriteoff.htm' where doc_typ_nm = 'INVW' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPaymentApplication.htm' where doc_typ_nm = 'APP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fadvancedeposit.htm' where doc_typ_nm = 'AD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fauxiliaryvoucher.htm' where doc_typ_nm = 'AV' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbudgetadjustment.htm' where doc_typ_nm = 'BA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcashreceipt.htm' where doc_typ_nm = 'CR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcreditcardreceipt.htm' where doc_typ_nm = 'CCR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdisbursementvoucher.htm' where doc_typ_nm = 'DV' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdistributionofincomeandexpense.htm' where doc_typ_nm = 'DI' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fgeneralerrorcorrection.htm' where doc_typ_nm = 'GEC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FProcurementCard' where doc_typ_nm = 'PCDO' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Findirectcostadjustment.htm' where doc_typ_nm = 'ICA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Finternalbilling.htm' where doc_typ_nm = 'IB' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpreencumbrance.htm' where doc_typ_nm = 'PE' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ftransferoffunds.htm' where doc_typ_nm = 'TF' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbenefitexpensetransfer.htm' where doc_typ_nm = 'BT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsalaryexpensetransfer.htm' where doc_typ_nm = 'ST' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FBulkReceiving.htm' where doc_typ_nm = 'RCVB' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FContractManagerAssignment.htm' where doc_typ_nm = 'ACM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPaymentRequest.htm' where doc_typ_nm = 'PREQ' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FReceiving.htm' where doc_typ_nm = 'RCVL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FRequisition.htm' where doc_typ_nm = 'REQS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrder.htm' where doc_typ_nm = 'PO' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderAmend.htm' where doc_typ_nm = 'POA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderPaymentHold.htm' where doc_typ_nm = 'POPH' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderRemovePaymentHold.htm' where doc_typ_nm = 'PORH' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderRetransmit.htm' where doc_typ_nm = 'PORT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderVoidlhtm' where doc_typ_nm = 'POV' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderClose.htm' where doc_typ_nm = 'POC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderReopen.htm' where doc_typ_nm = 'POR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderSplit.htm' where doc_typ_nm = 'POSP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FVendorCreditMemo.htm' where doc_typ_nm = 'CM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbarcodeinventoryprocess.htm' where doc_typ_nm = 'BCIE' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Feffortcertificationrecreate.htm' where doc_typ_nm = 'ECD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcashmanagement.htm' where doc_typ_nm = 'CMD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fgeneralledgercorrectionprocess.htm' where doc_typ_nm = 'GLCP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fjournalvoucher.htm' where doc_typ_nm = 'JV' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'noncheckdisbursement.htm' where doc_typ_nm = 'ND' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fservicebilling.htm' where doc_typ_nm = 'SB' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flaborjournalvoucher.htm' where doc_typ_nm = 'LLJV' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flaborledgercorrectionprocess.htm' where doc_typ_nm = 'LLCP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpreassettagging.htm' where doc_typ_nm = 'PTAG' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetfabrication.htm' where doc_typ_nm = 'FR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetpayment.htm' where doc_typ_nm = 'MPAY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FSeparateAssets.htm' where doc_typ_nm = 'AA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FTransferAsset.htm' where doc_typ_nm = 'AT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FEquipmentLoan.htm' where doc_typ_nm = 'ELR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetretirementglobal.htm' where doc_typ_nm = 'ARG' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faccount.htm' where doc_typ_nm = 'ACCT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faccountglobal.htm' where doc_typ_nm = 'GACC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faccountdelegate.htm' where doc_typ_nm = 'ADEL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faccountdelegateglobal.htm' where doc_typ_nm = 'GDLG' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faccountdelegatemodel.htm' where doc_typ_nm = 'GDLM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fobjectcode.htm' where doc_typ_nm = 'OBJT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FObjectCodeGlobal.htm' where doc_typ_nm = 'GOBJ' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Forganization.htm' where doc_typ_nm = 'ORGN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Forganizationreview.htm' where doc_typ_nm = 'ORR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fprojectcode.htm' where doc_typ_nm = 'PROJ' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsubaccount.htm' where doc_typ_nm = 'SACC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsubobjectcode.htm' where doc_typ_nm = 'SOBJ' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsubobjectcodeglobal.htm' where doc_typ_nm = 'GSOB' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faward.htm' where doc_typ_nm = 'AWRD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fproposal.htm' where doc_typ_nm = 'PRPL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FDisbursementVoucherTravelCompany.htm' where doc_typ_nm = 'DVTC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fvendor2.htm' where doc_typ_nm = 'PVEN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FFinancialProcessing.htm' where doc_typ_nm = 'YEBA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FFinancialProcessing.htm' where doc_typ_nm = 'YEDI' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FFinancialProcessing.htm' where doc_typ_nm = 'YEGE' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FFinancialProcessing.htm' where doc_typ_nm = 'YETF' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FLaborDistribution.htm' where doc_typ_nm = 'YEBT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FLaborDistribution.htm' where doc_typ_nm = 'YEST' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faccounttype.htm' where doc_typ_nm = 'ATYP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faccountingperiod.htm' where doc_typ_nm = 'APRD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faicpafunction.htm' where doc_typ_nm = 'AFUN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbalancetype.htm' where doc_typ_nm = 'BTYP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbasicaccountingcategory.htm' where doc_typ_nm = 'ACTY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbudgetaggregationcode.htm' where doc_typ_nm = 'BAMD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbudgetrecordinglevel.htm' where doc_typ_nm = 'BRL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fchart.htm' where doc_typ_nm = 'COAT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffederalfunction.htm' where doc_typ_nm = 'FFUN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffederalfundedcode.htm' where doc_typ_nm = 'FFC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffinancialreportingcode.htm' where doc_typ_nm = 'RPTC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffundgroup.htm' where doc_typ_nm = 'FGRP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fhighereducationfunction.htm' where doc_typ_nm = 'HEFN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Findirectcostrecoveryrate.htm' where doc_typ_nm = 'ICRE' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Findirectcostrecoveryexclusionbyaccount.htm' where doc_typ_nm = 'ICRA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Findirectcostrecoverytype.htm' where doc_typ_nm = 'ITYP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fmandatorytransferelimination.htm' where doc_typ_nm = 'MTE' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fobjectconsolidation.htm' where doc_typ_nm = 'OBJC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fobjectlevel.htm' where doc_typ_nm = 'OBJL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fobjectsubtype.htm' where doc_typ_nm = 'OSTY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fobjecttype.htm' where doc_typ_nm = 'OTYP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Foffsetaccount.htm' where doc_typ_nm = 'OFAC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Foffsetdefinition.htm' where doc_typ_nm = 'OFSD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Forganizationreversion.htm' where doc_typ_nm = 'ORGR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Forganizationreversionglobal.htm' where doc_typ_nm = 'GORV' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Forganizationreversioncategory.htm' where doc_typ_nm = 'ORGC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Forganizationtype.htm' where doc_typ_nm = 'ORTY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fresponsibilitycenter.htm' where doc_typ_nm = 'RCEN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Frestrictedstatus.htm' where doc_typ_nm = 'RSTA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsubfundgroup.htm' where doc_typ_nm = 'SFGR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsubfundgrouptype.htm' where doc_typ_nm = 'SFGT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsufficientfundscode.htm' where doc_typ_nm = 'SFC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Funiversitybudgetofficefunction.htm' where doc_typ_nm = 'UFUN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCashDrawer.htm' where doc_typ_nm = 'CDS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcreditcardtype.htm' where doc_typ_nm = 'CCTY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcreditcardvendor.htm' where doc_typ_nm = 'CCVN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdisbursementvoucherdocumentationlocation.htm' where doc_typ_nm = 'DVDL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdisbursementvoucherpaymentreason.htm' where doc_typ_nm = 'DVPR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdisbursementvouchertaxincomeclass.htm' where doc_typ_nm = 'DVIC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdisbursementvouchertravelexpensetype.htm' where doc_typ_nm = 'DVET' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdisbursementvouchertravelmileagerate.htm' where doc_typ_nm = 'DVML' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdisbursementvouchertravelperdiem.htm' where doc_typ_nm = 'DVPD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdisbursementvoucherwirecharge.htm' where doc_typ_nm = 'DVWT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffiscalyearfunctioncontrol.htm' where doc_typ_nm = 'BFYC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffunctioncontrolcode.htm' where doc_typ_nm = 'BFNC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fnonresidentalientaxpercent.htm' where doc_typ_nm = 'DVTX' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faccountingchangecode.htm' where doc_typ_nm = 'ACTC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fachbank.htm' where doc_typ_nm = 'ABNK' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fachtransactioncode.htm' where doc_typ_nm = 'ACTR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fachtransactiontype.htm' where doc_typ_nm = 'ACHT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcustomerprofile.htm' where doc_typ_nm = 'CSPR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdisbursementnumberrange.htm' where doc_typ_nm = 'DBRG' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdisbursementtype.htm' where doc_typ_nm = 'DSTP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpayeeachaccount.htm' where doc_typ_nm = 'PAAT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpayeetype.htm' where doc_typ_nm = 'PYTP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpaymentchange.htm' where doc_typ_nm = 'PMTC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpaymenttype.htm' where doc_typ_nm = 'PMTP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpaymentstatus.htm' where doc_typ_nm = 'PMTS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCustomer.htm' where doc_typ_nm = 'CUS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCustomerType.htm' where doc_typ_nm = 'CTY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCustomerAddressType.htm' where doc_typ_nm = 'CATY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCustomerInvoiceItemCode.htm' where doc_typ_nm = 'IICO' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FInvoiceRecurrence.htm' where doc_typ_nm = 'INVR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FOrganizationOptions.htm' where doc_typ_nm = 'OOPT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FOrganizationAccountingDefault.htm' where doc_typ_nm = 'OADF' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPaymentMedium.htm' where doc_typ_nm = 'PMED' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FSystemInformation.htm' where doc_typ_nm = 'ARSI' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faddresstype.htm' where doc_typ_nm = 'PMAT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcampusparameter.htm' where doc_typ_nm = 'PMCP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcommoditycode.htm' where doc_typ_nm = 'PMCC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcontacttype.htm' where doc_typ_nm = 'PMCT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcontractmanager.htm' where doc_typ_nm = 'PMCO' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcostsource.htm' where doc_typ_nm = 'PMCS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fownershiptype.htm' where doc_typ_nm = 'PMOT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fownershiptypecategory.htm' where doc_typ_nm = 'PMOC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpaymenttermstype.htm' where doc_typ_nm = 'PMPA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fphonetype.htm' where doc_typ_nm = 'PMPT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fshippingpaymentterms.htm' where doc_typ_nm = 'PMSP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fshippingspecialconditions.htm' where doc_typ_nm = 'PMSS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fshippingtitle.htm' where doc_typ_nm = 'PMST' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsupplierdiversity.htm' where doc_typ_nm = 'PMSD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fvendorinactivereason.htm' where doc_typ_nm = 'PMIR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fvendortype.htm' where doc_typ_nm = 'PMVT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FBillingAddress.htm' where doc_typ_nm = 'PMBA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCapitalAssetSystemState.htm' where doc_typ_nm = 'PMTY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCapitalAssetSystemType.htm' where doc_typ_nm = 'PMSY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCarrier.htm' where doc_typ_nm = 'PMCA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCreditMemoStatus.htm' where doc_typ_nm = 'PMCM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FDeliveryRequiredDateReason.htm' where doc_typ_nm = 'PMDR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FElectronicInvoiceItemMapping.htm' where doc_typ_nm = 'EIIM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FFundingSource.htm' where doc_typ_nm = 'PMFS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FItemReasonAdded.htm' where doc_typ_nm = 'IRAD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FItemType.htm' where doc_typ_nm = 'PMIT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FLineItemReceivingStatus.htm' where doc_typ_nm = 'PMLI' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FMethodofPOTransmission.htm' where doc_typ_nm = 'PMTM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FNegativePaymentRequestApprovalLimit.htm' where doc_typ_nm = 'PMNP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FOrganizationParameter.htm' where doc_typ_nm = 'PMOP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPaymentRequestAutoApproveExclusions.htm' where doc_typ_nm = 'PMAA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPaymentRequestStatus.htm' where doc_typ_nm = 'PMPR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderContractLanguage.htm' where doc_typ_nm = 'PMCL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderQuoteLanguage.htm' where doc_typ_nm = 'PMQL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderQuoteList.htm' where doc_typ_nm = 'PMQT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'PurchaseOrderQuoteStatus.htm' where doc_typ_nm = 'PMQS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderStatus.htm' where doc_typ_nm = 'PMPS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FPurchaseOrderVendorChoice.htm' where doc_typ_nm = 'PMVC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FReceivingAddress.htm' where doc_typ_nm = 'PMRA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FReceivingThreshold.htm' where doc_typ_nm = 'THLD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FRecurringPaymentFrequency.htm' where doc_typ_nm = 'PMRF' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FRecurringPaymentType.htm' where doc_typ_nm = 'PMRP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FRequisitionSource.htm' where doc_typ_nm = 'PMSO' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FRequisitionStatus.htm' where doc_typ_nm = 'PMRS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FSensitiveData.htm' where doc_typ_nm = 'PMSN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FVendorStipulation.htm' where doc_typ_nm = 'PMSI' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetacquisitiontype.htm' where doc_typ_nm = 'ACQT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetcondition.htm' where doc_typ_nm = 'ACON' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetdepreciationconvention.htm' where doc_typ_nm = 'DPRC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetdepreciationmethod.htm' where doc_typ_nm = 'DPRM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetlocationtype.htm' where doc_typ_nm = 'ASLT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetobjectcode.htm' where doc_typ_nm = 'COBJ' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetretirementreason.htm' where doc_typ_nm = 'RRSN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetstatus.htm' where doc_typ_nm = 'ASTA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassettransactiontype.htm' where doc_typ_nm = 'PMTT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassettype.htm' where doc_typ_nm = 'ASST' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fagency.htm' where doc_typ_nm = 'AGCY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fagencytype.htm' where doc_typ_nm = 'AGTY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fawardstatus.htm' where doc_typ_nm = 'AWDS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCFDA.htm' where doc_typ_nm = 'CFDM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fletterofcreditfundgroup.htm' where doc_typ_nm = 'LFGR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FProposalAwardClose.htm' where doc_typ_nm = 'CLOS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fproposalpurpose.htm' where doc_typ_nm = 'PURP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fproposalstatus.htm' where doc_typ_nm = 'PRST' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fproposaltype.htm' where doc_typ_nm = 'PATY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fresearchrisktype.htm' where doc_typ_nm = 'RRT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsubcontractor.htm' where doc_typ_nm = 'SUBC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FReportDefinition.htm' where doc_typ_nm = 'ECRD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FReportEarnPaygroup.htm' where doc_typ_nm = 'ECPG' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FReportPeriodStatusCode.htm' where doc_typ_nm = 'ECPS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FReportType.htm' where doc_typ_nm = 'ECRT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flaborbenefitscalculation.htm' where doc_typ_nm = 'BCAL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flaborbenefitstype.htm' where doc_typ_nm = 'BENT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flaborobjectcode.htm' where doc_typ_nm = 'LOBJ' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flaborobjectcodebenefits.htm' where doc_typ_nm = 'LOBN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flaborpositionobjectgroupcode.htm' where doc_typ_nm = 'POGR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FAccountReports.htm' where doc_typ_nm = 'BCAR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FAppointmentFundingDuration.htm' where doc_typ_nm = 'DURA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FAppointmentFundingReasonCode.htm' where doc_typ_nm = 'BCRC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FBudgetConstructionPosition.htm' where doc_typ_nm = 'BCPS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FCalculatedSalaryFoundationTrackerOverride.htm' where doc_typ_nm = 'CSFO' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FIntendedIncumbent.htm' where doc_typ_nm = 'IINC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FOrganizationReports.htm' where doc_typ_nm = 'BCOR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbank.htm' where doc_typ_nm = 'BANK' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbuilding.htm' where doc_typ_nm = 'BLDG' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Foriginationcode.htm' where doc_typ_nm = 'ORIG' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Froom.htm' where doc_typ_nm = 'ROOM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ftaxregion.htm' where doc_typ_nm = 'TAXR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ftaxregiontype.htm' where doc_typ_nm = 'TRTP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Funitofmeasure.htm' where doc_typ_nm = 'PMUM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Funiversitydate.htm' where doc_typ_nm = 'UDAT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FFunctionalFieldDescription.htm' where doc_typ_nm = 'FFD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FMessageOfTheDay.htm' where doc_typ_nm = 'MOTD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2FSystemOptions.htm' where doc_typ_nm = 'SOPT' and actv_ind = 1 and cur_ind = 1
/

-- KULRICE-3636: renaming indexes to new "Rice" names
ALTER INDEX EN_DOC_TYP_TI1 RENAME TO KREW_DOC_TYP_TI1
/
ALTER INDEX EN_RULE_TMPL_TI1 RENAME TO KREW_RULE_TMPL_TI1
/
ALTER INDEX FP_DOC_HEADER_TC0 RENAME TO KRNS_DOC_HDR_TC0
/
ALTER INDEX FP_MAINT_DOC_ATTACHMENT_TC0 RENAME TO KRNS_MAINT_DOC_ATT_TC0
/
ALTER INDEX FP_MAINTENANCE_DOCUMENT_TC0 RENAME TO KRNS_MAINT_DOC_TC0
/
ALTER INDEX FS_ADHOC_RTE_ACTN_RECP_TC0 RENAME TO KRNS_ADHOC_RTE_ACTN_RECIP_TC0
/
ALTER INDEX FS_LOOKUP_RESULTS_MTC0 RENAME TO KRNS_LOOKUP_RSLT_TC0
/
ALTER INDEX FS_LOOKUP_SELECTIONS_MTC0 RENAME TO KRNS_LOOKUP_SEL_TC0
/
ALTER INDEX KNS_PESSIMISTIC_LOCK_TC0 RENAME TO KRNS_PESSIMISTIC_LOCK_TC0
/
ALTER INDEX SH_ATT_TC0 RENAME TO KRNS_ATT_TC0
/
ALTER INDEX SH_NTE_TC0 RENAME TO KRNS_NTE_TC0
/
ALTER INDEX SH_NTE_TYP_TC0 RENAME TO KRNS_NTE_TYP_TC0
/
ALTER INDEX SH_PARM_DTL_TYP_TC0 RENAME TO KRNS_PARM_DTL_TYP_TC0
/
ALTER INDEX SH_PARM_NMSPC_TC0 RENAME TO KRNS_NMSPC_TC0
/
ALTER INDEX SH_PARM_TC0 RENAME TO KRNS_PARM_TC0
/
ALTER INDEX SH_PARM_TYP_TC0 RENAME TO KRNS_PARM_TYP_TC0
/
ALTER INDEX USER_CHANNEL_SUBSCRIPTION_UK1 RENAME TO KREN_CHNL_SUBSCRP_TC0
/ 

-- KFSMI-5039: dropping mbr_id from pend role mbr...
ALTER TABLE KRIM_PND_ROLE_MBR_MT
DROP COLUMN MBR_NM
/

-- KFSMI-5055: pointing CFDA job to proper URL
update krns_parm_t set txt = 'ftp://ftp.cfda.gov/programs' where nmspc_cd = 'KFS-CG' and parm_dtl_typ_cd = 'CfdaBatchStep' and parm_nm = 'SOURCE_URL' and appl_nmspc_cd = 'KFS'

-- KFSMI-3623: add updates to KRIM tables
-- create ethnicity table
CREATE TABLE KRIM_ENTITY_ETHNIC_T
(
      ID VARCHAR2(40),
      ENTITY_ID VARCHAR2(40),
      ETHNCTY_CD VARCHAR2(40),
      SUB_ETHNCTY_CD VARCHAR2(40),
      VER_NBR NUMBER(8) default 1 NOT NULL,
      OBJ_ID VARCHAR2(36) NOT NULL,

      CONSTRAINT KRIM_ENTITY_ETHNIC_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KRIM_ENTITY_ETHNIC_T
    ADD CONSTRAINT KRIM_ENTITY_ETHNIC_TP1
PRIMARY KEY (ID)
/
CREATE SEQUENCE KRIM_ENTITY_ETHNIC_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/
-- create residency table
CREATE TABLE KRIM_ENTITY_RESIDENCY_T
(
      ID VARCHAR2(40),
      ENTITY_ID VARCHAR2(40),
      DETERMINATION_METHOD VARCHAR2(40),
      IN_STATE VARCHAR2(40),
      VER_NBR NUMBER(8) default 1 NOT NULL,
      OBJ_ID VARCHAR2(36) NOT NULL,

      CONSTRAINT KRIM_ENTITY_RESIDENCY_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KRIM_ENTITY_RESIDENCY_T
    ADD CONSTRAINT KRIM_ENTITY_RESIDENCY_TP1
PRIMARY KEY (ID)
/
CREATE SEQUENCE KRIM_ENTITY_RESIDENCY_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/
-- add visa table
CREATE TABLE KRIM_ENTITY_VISA_T
(
      ID VARCHAR2(40),
      ENTITY_ID VARCHAR2(40),
      VISA_TYPE_KEY VARCHAR2(40),
      VISA_ENTRY VARCHAR2(40),
      VISA_ID VARCHAR2(40),
      VER_NBR NUMBER(8) default 1 NOT NULL,
      OBJ_ID VARCHAR2(36) NOT NULL,

      CONSTRAINT KRIM_ENTITY_VISA_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KRIM_ENTITY_VISA_T
    ADD CONSTRAINT KRIM_ENTITY_VISA_TP1
PRIMARY KEY (ID)
/
CREATE SEQUENCE KRIM_ENTITY_VISA_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/
-- insert ethnicity values into new ethinicity table from bio table (copy ethnicityCode and entityId values... double check sequence usage)
INSERT INTO KRIM_ENTITY_ETHNIC_T ( ID, OBJ_ID, ENTITY_ID, ETHNCTY_CD )
    SELECT KRIM_ENTITY_ETHNIC_ID_S.NEXTVAL, SYS_GUID(), bio.ENTITY_ID, bio.ETHNCTY_CD
        FROM KRIM_ENTITY_BIO_T bio
/
-- alter bio table to add new fields
ALTER TABLE KRIM_ENTITY_BIO_T ADD DECEASED_DT DATE
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD MARITAL_STATUS VARCHAR2(40)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD PRIM_LANG_CD VARCHAR2(40)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD SEC_LANG_CD VARCHAR2(40)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD BIRTH_CNTRY_CD VARCHAR2(2)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD BIRTH_STATE_CD VARCHAR2(2)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD BIRTH_CITY VARCHAR2(30)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD GEO_ORIGIN VARCHAR2(100)
/
-- drop ethnicity from bio table
ALTER TABLE KRIM_ENTITY_BIO_T DROP COLUMN ETHNCTY_CD
/ 

-- KFSMI-5050: update help urls for doc types
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcashcontrol.htm' where doc_typ_nm = 'CTRL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcustomercreditmemo.htm' where doc_typ_nm = 'CRM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcustomerinvoice.htm' where doc_typ_nm = 'INV' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcustomerinvoicewriteoff.htm' where doc_typ_nm = 'INVW' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpaymentapplication.htm' where doc_typ_nm = 'APP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fprocurementcard' where doc_typ_nm = 'PCDO' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbulkreceiving.htm' where doc_typ_nm = 'RCVB' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcontractmanagerassignment.htm' where doc_typ_nm = 'ACM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpaymentrequest.htm' where doc_typ_nm = 'PREQ' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Freceiving.htm' where doc_typ_nm = 'RCVL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Frequisition.htm' where doc_typ_nm = 'REQS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseorder.htm' where doc_typ_nm = 'PO' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseorderamend.htm' where doc_typ_nm = 'POA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseorderpaymenthold.htm' where doc_typ_nm = 'POPH' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseorderremovepaymenthold.htm' where doc_typ_nm = 'PORH' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseorderretransmit.htm' where doc_typ_nm = 'PORT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseordervoidlhtm' where doc_typ_nm = 'POV' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseorderclose.htm' where doc_typ_nm = 'POC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseorderreopen.htm' where doc_typ_nm = 'POR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseordersplit.htm' where doc_typ_nm = 'POSP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fvendorcreditmemo.htm' where doc_typ_nm = 'CM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbarcodeinventoryprocess.htm' where doc_typ_nm = 'BCIE' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fnoncheckdisbursement.htm' where doc_typ_nm = 'ND' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Feditasset.htm' where doc_typ_nm = 'CASM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetglobaladd.htm' where doc_typ_nm = 'AA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassetglobaladd.htm' where doc_typ_nm = 'AA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ftransferasset.htm' where doc_typ_nm = 'AT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fequipmentloan.htm' where doc_typ_nm = 'ELR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fobjectcodeglobal.htm' where doc_typ_nm = 'GOBJ' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdisbursementvouchertravelcompany.htm' where doc_typ_nm = 'DVTC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fvendor3.htm' where doc_typ_nm = 'PVEN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffinancialprocessing.htm' where doc_typ_nm = 'YEBA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffinancialprocessing.htm' where doc_typ_nm = 'YEDI' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffinancialprocessing.htm' where doc_typ_nm = 'YEGE' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffinancialprocessing.htm' where doc_typ_nm = 'YETF' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flabordistribution.htm' where doc_typ_nm = 'YEBT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flabordistribution.htm' where doc_typ_nm = 'YEST' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcustomer.htm' where doc_typ_nm = 'CUS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcustomertype.htm' where doc_typ_nm = 'CTY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcustomeraddresstype.htm' where doc_typ_nm = 'CATY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcustomerinvoiceitemcode.htm' where doc_typ_nm = 'IICO' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Finvoicerecurrence.htm' where doc_typ_nm = 'INVR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Forganizationoptions.htm' where doc_typ_nm = 'OOPT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Forganizationaccountingdefault.htm' where doc_typ_nm = 'OADF' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpaymentmedium.htm' where doc_typ_nm = 'PMED' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsysteminformation.htm' where doc_typ_nm = 'ARSI' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbillingaddress.htm' where doc_typ_nm = 'PMBA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcapitalassetsystemstate.htm' where doc_typ_nm = 'PMTY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcapitalassetsystemtype.htm' where doc_typ_nm = 'PMSY' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcarrier.htm' where doc_typ_nm = 'PMCA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcreditmemostatus.htm' where doc_typ_nm = 'PMCM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdeliveryrequireddatereason.htm' where doc_typ_nm = 'PMDR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Felectronicinvoiceitemmapping.htm' where doc_typ_nm = 'EIIM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffundingsource.htm' where doc_typ_nm = 'PMFS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fitemreasonadded.htm' where doc_typ_nm = 'IRAD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fitemtype.htm' where doc_typ_nm = 'PMIT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flineitemreceivingstatus.htm' where doc_typ_nm = 'PMLI' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fmethodofpotransmission.htm' where doc_typ_nm = 'PMTM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fnegativepaymentrequestapprovallimit.htm' where doc_typ_nm = 'PMNP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Forganizationparameter.htm' where doc_typ_nm = 'PMOP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpaymentrequestautoapproveexclusions.htm' where doc_typ_nm = 'PMAA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpaymentrequeststatus.htm' where doc_typ_nm = 'PMPR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseordercontractlanguage.htm' where doc_typ_nm = 'PMCL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseorderquotelanguage.htm' where doc_typ_nm = 'PMQL' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseorderquotelist.htm' where doc_typ_nm = 'PMQT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseorderquotestatus.htm' where doc_typ_nm = 'PMQS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseorderstatus.htm' where doc_typ_nm = 'PMPS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpurchaseordervendorchoice.htm' where doc_typ_nm = 'PMVC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Freceivingaddress.htm' where doc_typ_nm = 'PMRA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Freceivingthreshold.htm' where doc_typ_nm = 'THLD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Frecurringpaymentfrequency.htm' where doc_typ_nm = 'PMRF' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Frecurringpaymenttype.htm' where doc_typ_nm = 'PMRP' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Frequisitionsource.htm' where doc_typ_nm = 'PMSO' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Frequisitionstatus.htm' where doc_typ_nm = 'PMRS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsensitivedata.htm' where doc_typ_nm = 'PMSN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fvendorstipulation.htm' where doc_typ_nm = 'PMSI' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fassettransactiontype.htm' where doc_typ_nm = 'PMTT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcatalogoffederaldomesticassistance.htm' where doc_typ_nm = 'CFDM' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fproposalawardclose.htm' where doc_typ_nm = 'CLOS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Freportdefinition.htm' where doc_typ_nm = 'ECRD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Freportearnpaygroup.htm' where doc_typ_nm = 'ECPG' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Freportperiodstatuscode.htm' where doc_typ_nm = 'ECPS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Freporttype.htm' where doc_typ_nm = 'ECRT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flaborobjectcodebenefitsmaintenancedocument' where doc_typ_nm = 'LOBN' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faccountreports.htm' where doc_typ_nm = 'BCAR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fappointmentfundingduration.htm' where doc_typ_nm = 'DURA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fappointmentfundingreasoncode.htm' where doc_typ_nm = 'BCRC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fbudgetconstructionposition.htm' where doc_typ_nm = 'BCPS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcalculatedsalaryfoundationtrackeroverride.htm' where doc_typ_nm = 'CSFO' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fintendedincumbent.htm' where doc_typ_nm = 'IINC' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Forganizationreports.htm' where doc_typ_nm = 'BCOR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ftaxregion.htm' where doc_typ_nm = 'TAXR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcashdrawer.htm' where doc_typ_nm = 'CDS' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffunctionalfielddescription.htm' where doc_typ_nm = 'FFD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fmessageoftheday.htm' where doc_typ_nm = 'MOTD' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fsystemoptions.htm' where doc_typ_nm = 'SOPT' and actv_ind = 1 and cur_ind = 1
/ 

-- KFSMI-4279: update parameter to use real address type
update krns_parm_t set txt = 'HM' where NMSPC_CD = 'KFS-FP' and PARM_DTL_TYP_CD = 'DisbursementVoucher' and parm_nm = 'DEFAULT_EMPLOYEE_ADDRESS_TYPE' and appl_nmspc_cd = 'KFS'
/

-- KFSMI-5051: help for the Rice doc types
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fperson.htm' where doc_typ_nm = 'IdentityManagementPersonDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fgroup.htm' where doc_typ_nm = 'IdentityManagementGroupDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Frole.htm' where doc_typ_nm = 'IdentityManagementRoleDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpermission.htm' where doc_typ_nm = 'IdentityManagementGenericPermissionMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fresponsibility.htm' where doc_typ_nm = 'IdentityManagementReviewResponsibilityMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcampus.htm' where doc_typ_nm = 'CampusMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcountry.htm' where doc_typ_nm = 'CountryMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcounty.htm' where doc_typ_nm = 'CountyMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpostalcode.htm' where doc_typ_nm = 'PostalCodeMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fstate.htm' where doc_typ_nm = 'StateMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faddresstype.htm' where doc_typ_nm = 'PMAT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcampustype.htm' where doc_typ_nm = 'CampusTypeMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fphonetype.htm' where doc_typ_nm = 'PMPT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdocumenttype.htm' where doc_typ_nm = 'DocumentTypeDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fparameter.htm' where doc_typ_nm = 'ParameterMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fparametercomponent.htm' where doc_typ_nm = 'ParameterDetailTypeMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fparametertype.htm' where doc_typ_nm = 'ParameterTypeMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fnamespace.htm' where doc_typ_nm = 'NamespaceMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
/

--KFSMI-5047: fix help urls for year end labor doc types
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flabordistributionyearend.htm' where doc_typ_nm = 'YEST' and cur_ind = 1 and actv_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flabordistributionyearend.htm' where doc_typ_nm = 'YEBT' and cur_ind = 1 and actv_ind = 1
/

--KFSMI-5105: more doc type url fixes
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffinancialprocessingyearend.htm' where doc_typ_nm = 'YEBA' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffinancialprocessingyearend.htm' where doc_typ_nm = 'YEDI' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffinancialprocessingyearend.htm' where doc_typ_nm = 'YEGE' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Ffinancialprocessingyearend.htm' where doc_typ_nm = 'YETF' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flabordistributionyearend.htm' where doc_typ_nm = 'YEBT' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Flabordistributionyearend.htm' where doc_typ_nm = 'YEST' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fprocurementcard.htm' where doc_typ_nm = 'PCDO' and actv_ind = 1 and cur_ind = 1
/

--KFSMI-3800: organization review document clean up
delete from krew_doc_typ_t where doc_typ_nm = 'ORGG' and parnt_id in (select doc_typ_id from krew_doc_typ_t where doc_typ_nm = 'COA' and cur_ind = 1 and actv_ind = 1)
/
update krew_doc_typ_t set lbl = 'Organization Review Group' where doc_typ_nm = 'ORG' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set lbl = 'Organization Review Role' where doc_typ_nm = 'ORR' and actv_ind = 1 and cur_ind = 1
/
update krew_doc_typ_t set lbl = 'Organization Review' where doc_typ_nm = 'OR' and actv_ind = 1 and cur_ind = 1
/
delete from krew_doc_typ_t where cur_ind = 0
/