-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/13/10 10:02 AM
-- Against: KULDEV@jdbc:oracle:thin:@esdbk02.uits.indiana.edu:1521:KUALI
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-1::Winston::(MD5Sum: 94ac8632b61d51ec718a02cd0626d11)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fbudgetdonstructionselection.htm' WHERE DOC_TYP_NM='BC';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-2::Winston::(MD5Sum: cd17a1ba3427d4efabd11c1d6a66cc)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fcashreceipt.htm' WHERE DOC_TYP_NM='CR';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-3::Winston::(MD5Sum: 946f56b45dc2333fe59e5ebf913c785)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fvendorcreditmemo.htm' WHERE DOC_TYP_NM='EAD';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-4::Winston::(MD5Sum: 26fde025067b1b18553e027d4646970)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2F assetdecrease.htm' WHERE DOC_TYP_NM='EAI';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-5::Winston::(MD5Sum: ae48545b2ec66e4677b308d92581823)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2F assetincrease.htm' WHERE DOC_TYP_NM='ECD';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-6::Winston::(MD5Sum: ccd08d297e60f412852cf2118b73dd6)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2F cashdecrease.htm' WHERE DOC_TYP_NM='ECI';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-7::Winston::(MD5Sum: 333a34fd5e7e6e3659361c95aa13d8f)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2F cashincrease.htm' WHERE DOC_TYP_NM='ECT';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-8::Winston::(MD5Sum: f5da33ae852ca2541949ce262e47de2)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2F cashtransfer.htm' WHERE DOC_TYP_NM='EGLT';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-9::Winston::(MD5Sum: 902d6451a0142db447348ef96afa5347)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2F endowmenttogltransferoffunds.htm' WHERE DOC_TYP_NM='GLET';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-10::Winston::(MD5Sum: ab4f7e1b198e2acddd34a97bcce9ab0)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2F gltoendowmenttransferoffunds.htm' WHERE DOC_TYP_NM='ELD';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-11::Winston::(MD5Sum: abf7c32c345a3477e3b93855bd5455e4)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2F liabilitydecrease.htm' WHERE DOC_TYP_NM='ELI';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-12::Winston::(MD5Sum: 529e15fbd657cb6131c6ef744db51dc)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2F liabilityincrease.htm' WHERE DOC_TYP_NM='EST';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-13::Winston::(MD5Sum: 8d2a4d39a454b8cb7f2c54a49ed279d8)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fcorporatereorganization.htm' WHERE DOC_TYP_NM='ECR';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-14::Winston::(MD5Sum: 8ad7712e87dc4e1a675318d971bce60)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fcorpusadjustment.htm' WHERE DOC_TYP_NM='ECA';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-15::Winston::(MD5Sum: d4ddae181c7f2d2de48c207bc7a918ea)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fholdingadjustment.htm' WHERE DOC_TYP_NM='EHA';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-16::Winston::(MD5Sum: fc8db06e1bbd41e252dd96412937ed)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fholdinghistoryvalueadjustment.htm' WHERE DOC_TYP_NM='EHVA';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-17::Winston::(MD5Sum: c335db497f1fa32fc67e4a510c86f)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fholdingtaxlotrebalance.htm' WHERE DOC_TYP_NM='HoldingTaxLotRebalanceMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-18::Winston::(MD5Sum: 34474aed46f0327cfce4b4e2f8aac9)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Funit/shareadjustment.htm' WHERE DOC_TYP_NM='EUSA';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-19::Winston::(MD5Sum: 77532835aef281d2983314b0b11ab6b4)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fkemid.htm' WHERE DOC_TYP_NM='KEMIDMaintenanceDocument ';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-20::Winston::(MD5Sum: e85488a49dc41bae5cf91f634c5709d)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Frecurringcashtransfer.htm' WHERE DOC_TYP_NM='RecurringCashTransferMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-21::Winston::(MD5Sum: 278756e038a783bcc1a817786b454593)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fsecurity.htm' WHERE DOC_TYP_NM='SecurityMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-22::Winston::(MD5Sum: b903de45d169d8d1e4ae4df4916c48)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Ftickler.htm' WHERE DOC_TYP_NM='TicklerMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-23::Winston::(MD5Sum: b09e3b6c119669cdfbe9f89479a06a33)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fholdingtaxlotmaintenance.htm' WHERE DOC_TYP_NM='HoldingTaxLotMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-24::Winston::(MD5Sum: 870df12fcdcd4f3eb2e9c7dc8f571)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fautomatedcashinvestmentmodel.htm' WHERE DOC_TYP_NM='ACIModelMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-25::Winston::(MD5Sum: 90e4dddf5a372166a2ec95642b7aab8a)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fcashsweepmodel.htm' WHERE DOC_TYP_NM='CashSweepModelMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-26::Winston::(MD5Sum: 1f40a536c71172d8e5f9a14dd9ed568)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fclasscode.htm' WHERE DOC_TYP_NM='ClassCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-27::Winston::(MD5Sum: b1b3902667e1d3614586c075e9bb2cd6)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fendowmenttransactioncode.htm' WHERE DOC_TYP_NM='EndowmentTransactionCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-28::Winston::(MD5Sum: e058811b6c8f0289e34f444575ee9)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fpooledfundcontrol.htm' WHERE DOC_TYP_NM='PooledFundControlMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-29::Winston::(MD5Sum: b898f6c2e17f36345cfc14a9f878a7a)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fpooledfundvalue.htm' WHERE DOC_TYP_NM='PooledFundValueMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-30::Winston::(MD5Sum: d368d0699734c3244cfd21e37f555ca3)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fregistrationcode.htm' WHERE DOC_TYP_NM='RegistrationCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-31::Winston::(MD5Sum: 99c460765b33d715ebc7e0d0c0ea21a5)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Freportinggroup.htm' WHERE DOC_TYP_NM='SecurityReportingGroupMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-32::Winston::(MD5Sum: d522a68a9344cd4049f5d3a093221)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fsubclasscode.htm' WHERE DOC_TYP_NM='SubclassCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-33::Winston::(MD5Sum: c192ef1cf1d6b548fcd140f3a6463af)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fagreementspecialinstructioncode.htm' WHERE DOC_TYP_NM='AgreementSpecialInstructionMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-34::Winston::(MD5Sum: 4c709676434a8c3f890e19e61c83fce)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fagreementstatuscode.htm' WHERE DOC_TYP_NM='AgreementStatusMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-35::Winston::(MD5Sum: 55cd85e35d3c663ff7ed11428c69532)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fagreementtypecode.htm' WHERE DOC_TYP_NM='AgreementTypeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-36::Winston::(MD5Sum: afd26f9ad2734836a9933d53dc27c33e)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fclosecode.htm' WHERE DOC_TYP_NM='CloseCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-37::Winston::(MD5Sum: 959d24eb349de4868fe04fc2361b7d6)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fcombinegroupcode.htm' WHERE DOC_TYP_NM='CombineGroupCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-38::Winston::(MD5Sum: b3d63294228815f8a7546a29409f7cbd)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fdonorrecord.htm' WHERE DOC_TYP_NM='DonorMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-39::Winston::(MD5Sum: f27dc92ede993dd87b14ef8de287bac4)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fdonorstatementcode.htm' WHERE DOC_TYP_NM='DonorStatementCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-40::Winston::(MD5Sum: 894dcb58ba0a1a9b9af70158a8f5df2)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Ffeemethod.htm' WHERE DOC_TYP_NM='FeeMethodMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-41::Winston::(MD5Sum: ba2f8aa88e511d68cfff890f18e74c)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fpurposecode.htm' WHERE DOC_TYP_NM='PurposeCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-42::Winston::(MD5Sum: 9491242b68bfae15b0a52e4396c2476c)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fresponsibleadministrationcode.htm' WHERE DOC_TYP_NM='ResponsibleAdministrationCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-43::Winston::(MD5Sum: 58ca7c9686b14dedeeb8b092d154736)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Frolecode.htm' WHERE DOC_TYP_NM='RoleCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-44::Winston::(MD5Sum: 8a9084d869c45ff88fdb8e7dcac77af)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fsourceoffundscode.htm' WHERE DOC_TYP_NM='FundSourceCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-45::Winston::(MD5Sum: 54824a8320dbb3dc7eb9f803ad678c6)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fticklertypecode.htm' WHERE DOC_TYP_NM='TicklerTypeCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-46::Winston::(MD5Sum: ef2cbd50bb55d68ca74938824364d8dd)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Ftransactionrestrictioncode.htm' WHERE DOC_TYP_NM='TransactionRestrictionCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-47::Winston::(MD5Sum: da38a276a4ff5652e2875c1149ccb1)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Ftypecode.htm' WHERE DOC_TYP_NM='TypeCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-48::Winston::(MD5Sum: eccdc9f4186f5c712561d8871ce7a79)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Ftyperestrictioncode.htm' WHERE DOC_TYP_NM='TypeRestrictionCodeMaintenanceDocument';


-- Changeset updates/2010-09-13-5986-1-krew_doc_typ_t.xml::5986-1-49::Winston::(MD5Sum: 92a5add3a36ee90fd6db2aa88ba06c)
-- Inert help Definition url into krew_doc_typ_t
UPDATE krew_doc_typ_t SET HELP_DEF_URL = 'default.htm?turl=Documents%2Fusecriteriacodemaintenancecocument.htm' WHERE DOC_TYP_NM='UseCriteriaCodeMaintenanceDocument';


-- Release Database Lock

-- Release Database Lock

