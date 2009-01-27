update krim_role_t set nmspc_cd = 'KR-WKFLW' where nmspc_cd = 'KR_WKFLW'
/

update krim_role_perm_t set role_id = '66' where role_perm_id = '214'
/

ALTER TABLE KREW_RULE_EXPR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_EXPR_T ADD (VER_NBR NUMBER(8) DEFAULT 0)
/
ALTER TABLE KREW_RULE_ATTR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_DOC_HDR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_DOC_TYP_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_TMPL_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_TMPL_ATTR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_RSP_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_STYLE_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_EDL_DEF_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_EDL_ASSCTN_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_DLGN_RSP_T ADD (OBJ_ID VARCHAR2(36))
/
UPDATE KREW_RULE_EXPR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_ATTR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_DOC_HDR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_DOC_TYP_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_TMPL_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_TMPL_ATTR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_RSP_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_STYLE_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_EDL_DEF_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_EDL_ASSCTN_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_DLGN_RSP_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
ALTER TABLE KREW_RULE_EXPR_T ADD CONSTRAINT KREW_RULE_EXPR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_ATTR_T ADD CONSTRAINT KREW_RULE_ATTR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_DOC_HDR_T ADD CONSTRAINT KREW_DOC_HDR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_DOC_TYP_T ADD CONSTRAINT KREW_DOC_TYP_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_TMPL_T ADD CONSTRAINT KREW_RULE_TMPL_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_TMPL_ATTR_T ADD CONSTRAINT KREW_RULE_TMPL_ATTR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_T ADD CONSTRAINT KREW_RULE_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_RSP_T ADD CONSTRAINT KREW_RULE_RSP_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_STYLE_T ADD CONSTRAINT KREW_STYLE_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_EDL_DEF_T ADD CONSTRAINT KREW_EDL_DEF_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_EDL_ASSCTN_T ADD CONSTRAINT KREW_EDL_ASSCTN_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_DLGN_RSP_T ADD CONSTRAINT KREW_DLGN_RSP_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_EXPR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_ATTR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_DOC_HDR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_DOC_TYP_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_TMPL_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_TMPL_ATTR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_RSP_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_STYLE_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_EDL_DEF_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_EDL_ASSCTN_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_DLGN_RSP_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/

update krim_role_rsp_actn_t set ignore_prev_ind = 'N' where role_rsp_actn_id = '115'
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3472
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3473
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3474
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3475
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3476
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3477
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3478
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3479
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3480
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3481
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3482
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3483
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3484
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3485
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3486
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3487
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3488
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3489
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3490
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3491
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3492
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3493
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3494
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3495
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3496
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3497
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3498
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3499
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3500
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3501
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3502
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3503
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3504
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3505
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3506
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3507
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3508
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3509
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3510
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3511
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3512
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3513
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3514
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3515
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3516
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3517
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3518
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3519
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3520
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3521
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3522
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3523
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3524
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3525
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3526
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3527
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3528
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3529
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3530
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3531
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3532
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3533
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3534
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3537
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3538
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3539
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3540
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3541
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3542
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3543
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3544
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3545
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3546
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3547
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3548
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3549
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3550
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3551
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3552
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3553
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3554
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3555
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3556
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3557
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3692
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3693
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3712
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3713
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3714
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3715
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3716
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3717
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3718
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3719
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3720
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3721
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3722
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3723
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3724
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3725
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3726
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3727
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3728
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3729
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3730
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3731
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3732
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3733
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3734
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3735
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3736
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3737
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3738
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3739
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3740
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3741
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3742
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3743
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3744
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3745
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3746
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3747
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3748
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3749
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3750
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3751
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3752
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3753
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3754
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3755
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3756
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3757
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3758
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3759
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3760
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3761
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3762
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3763
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3764
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3765
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3766
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3767
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3768
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3769
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3770
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3771
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3772
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3773
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3774
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3775
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3776
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3777
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3778
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3779
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3780
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3781
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3782
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3783
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3784
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3785
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3786
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3787
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3788
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3789
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3790
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3791
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3792
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3793
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3794
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3795
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3796
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3812
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3813
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3814
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3819
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3820
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3829
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3830
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3831
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3832
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3833
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3834
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3835
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3836
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3837
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3838
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3839
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3840
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3841
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3842
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3843
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3844
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3846
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3888
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3889
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3890
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3891
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3892
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3893
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3894
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3895
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3896
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3897
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3898
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3899
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3900
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3901
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3902
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3903
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3904
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3905
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3906
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3907
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3908
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3909
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3910
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3911
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3912
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3913
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3914
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3915
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3916
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3917
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3918
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3919
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3920
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3921
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3922
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3923
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3924
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3925
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3926
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3927
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3928
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3929
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3930
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3931
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3932
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3933
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3972
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3992
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3993
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3994
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3995
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3996
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3997
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3998
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3999
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2704
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2705
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2706
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2707
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2708
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2709
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2710
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2711
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2712
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2713
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2714
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2715
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2716
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2717
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2718
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2719
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2720
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2721
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2722
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2723
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2724
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2725
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2726
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2727
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2728
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2729
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2730
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2731
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2732
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2733
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2750
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2751
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2752
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2753
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2754
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2755
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2756
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2757
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2758
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2759
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2760
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2761
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2762
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2763
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2764
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2765
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2770
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2771
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2772
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2773
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2774
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2775
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2776
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2777
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2778
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2779
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2780
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2781
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2782
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2783
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2784
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2785
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2790
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2791
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2792
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2810
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2811
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2812
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2813
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2814
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2815
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2830
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2831
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2832
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2833
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2834
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2835
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2836
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2837
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2838
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2839
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2840
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2841
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2842
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3558
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3559
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3560
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3561
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3562
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3563
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3564
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3565
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3566
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3567
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3568
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3569
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3570
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3571
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3572
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3573
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3574
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3575
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3576
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3577
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3578
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3579
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3580
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3581
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3582
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3583
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3584
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3585
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3586
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3587
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3588
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3589
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3590
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3591
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3592
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3593
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3594
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3595
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3596
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3597
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3598
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3599
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3600
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3601
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3602
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3603
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3604
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3605
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3606
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3607
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3608
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3609
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3610
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3611
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3612
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3613
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3614
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3615
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3616
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3617
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3618
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3619
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3630
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3631
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3650
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3651
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3652
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3653
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3654
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3655
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3656
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3657
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3658
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3659
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3660
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3661
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3662
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3663
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3664
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3665
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3666
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3667
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3668
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3669
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3670
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2983
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2984
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2985
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2986
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2987
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2988
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2989
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2990
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2991
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2992
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2993
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2994
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2995
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2996
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2997
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2998
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2999
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3000
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3001
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3002
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3003
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3004
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3005
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3006
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3007
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3008
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3009
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3010
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3011
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3012
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3013
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3014
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3015
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3016
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3017
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3018
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3019
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3020
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3021
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3022
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3023
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3024
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3025
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3026
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3027
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3028
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3029
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3030
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3031
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3032
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3033
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3034
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3035
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3036
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3037
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3038
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3039
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3040
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3041
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3042
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3043
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3044
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3045
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3046
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3047
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3050
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3051
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3052
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3053
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3054
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3055
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3056
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3057
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3058
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3059
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3060
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3061
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3062
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3063
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3064
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3065
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3066
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3067
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3068
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3069
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2570
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2571
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2572
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2573
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2574
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2575
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2590
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2610
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2611
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2612
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2613
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2614
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2615
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2616
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2387
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2617
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2618
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2619
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2620
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2621
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2622
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2630
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2631
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2632
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2633
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2205
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2206
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2207
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2208
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2209
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2210
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2211
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2227
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2228
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2229
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2247
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2248
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2249
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2634
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2287
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2288
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2289
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2635
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2636
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2637
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2328
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2329
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2330
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2638
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2639
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2367
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2388
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2640
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2641
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2642
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2650
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2651
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2531
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2652
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2533
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2534
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2653
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2654
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2655
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2656
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2657
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2540
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2541
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2542
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2550
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2551
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2188
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2189
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2193
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2200
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2203
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2307
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2327
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2430
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2470
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2530
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2532
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2535
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2536
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2538
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2539
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2552
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2553
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2554
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2555
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2658
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2659
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2660
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2661
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2662
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2663
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2670
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2671
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2672
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2673
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2674
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2675
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2676
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2677
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2678
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2679
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2680
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2681
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2682
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2683
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2684
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2685
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2686
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2687
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2688
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2689
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2690
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2691
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2692
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2693
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2694
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2695
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2696
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2697
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2698
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2699
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2700
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2701
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2702
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2703
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2843
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2844
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2845
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2846
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2847
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2848
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2849
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2850
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2851
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2870
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2871
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2872
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2873
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2874
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2875
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2876
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2890
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2891
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2892
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2893
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2894
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2895
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2896
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2910
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2911
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2912
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2913
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2914
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2915
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2916
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2917
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2918
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2919
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2920
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2921
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2922
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2923
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2924
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2925
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2930
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2931
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2932
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2933
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2934
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2935
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2936
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2937
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2938
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2939
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2940
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2941
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2942
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2943
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2944
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2945
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2946
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2947
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2948
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2949
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2950
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2951
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2952
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2953
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2954
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2955
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2956
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2957
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2958
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2959
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2960
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2961
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2970
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2971
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2972
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2973
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2974
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2975
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2976
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2977
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2978
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2979
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2980
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2981
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 2982
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3070
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3071
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3072
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3073
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3074
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3075
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3076
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3077
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3078
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3079
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3080
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3081
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3082
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3083
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3084
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3085
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3086
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3087
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3088
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3089
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3090
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3110
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3111
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3112
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3113
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3114
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3115
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3116
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3117
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3118
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3119
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3120
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3121
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3122
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3123
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3124
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3125
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3130
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3131
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3132
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3133
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3134
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3135
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3136
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3137
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3138
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3139
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3140
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3141
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3142
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3143
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3144
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3145
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3146
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3147
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3148
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3149
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3150
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3151
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3152
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3153
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3154
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3155
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3156
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3157
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3158
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3159
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3160
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3161
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3162
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3163
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3164
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3165
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3166
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3167
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3168
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3169
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3170
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3190
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3191
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3192
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3193
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3194
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3195
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3196
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3197
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3198
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3199
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3200
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3201
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3202
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3203
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3204
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3205
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3206
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3207
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3208
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3209
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3210
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3211
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3212
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3230
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3231
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3232
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3233
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3234
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3235
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3236
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3237
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3238
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3239
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3240
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3241
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3242
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3243
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3244
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3245
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3246
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3247
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3248
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3249
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3250
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3251
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3252
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3253
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3254
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3255
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3256
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3257
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3258
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3259
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3260
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3261
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3262
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3263
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3264
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3265
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3266
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3267
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3268
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3269
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3270
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3271
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3272
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3273
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3274
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3275
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3276
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3277
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3278
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3279
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3280
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3281
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3282
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3283
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3284
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3285
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3286
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3287
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3288
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3289
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3290
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3291
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3292
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3293
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3294
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3295
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3296
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3297
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3298
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3299
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3300
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3301
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3302
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3303
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3304
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3305
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3306
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3307
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3308
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3309
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3310
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3311
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3312
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3313
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3314
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3315
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3316
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3317
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3330
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3331
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3332
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3333
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3334
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3335
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3336
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3337
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3338
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3339
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3340
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3341
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3342
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3343
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3344
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3345
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3346
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3347
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3348
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3349
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3350
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3351
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3352
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3353
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3354
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3355
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3356
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3357
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3358
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3359
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3360
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3361
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3362
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3363
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3364
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3365
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3366
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3367
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3368
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3369
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3370
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3371
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3372
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3373
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3374
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3375
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3376
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3377
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3378
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3379
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3380
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3381
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3382
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3383
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3384
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3385
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3386
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3387
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3388
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3389
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3390
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3391
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3392
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3393
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3394
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3395
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3396
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3397
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3398
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3399
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3400
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3401
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3402
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3403
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3404
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3405
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3406
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3407
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3408
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3409
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3410
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3411
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3412
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3413
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3414
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3535
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3450
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3536
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3451
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3452
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3453
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3454
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3455
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3456
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3457
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3458
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3459
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3460
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3461
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3462
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3463
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3464
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3465
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3466
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3467
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3468
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3469
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3470
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 3471
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4012
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4013
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4014
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4015
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4016
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4017
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4018
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4019
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4020
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4021
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4022
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4023
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4024
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4025
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4026
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4027
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4028
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4029
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4030
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4031
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4032
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4033
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4034
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4035
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4036
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4037
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4038
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4039
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4040
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4041
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4042
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4043
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4044
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4045
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4046
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4047
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4048
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4049
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4050
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4051
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4052
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4072
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4073
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4074
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4075
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4076
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4077
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4078
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4079
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4080
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4081
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4082
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4083
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4084
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4085
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4086
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4087
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4088
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4092
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4093
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4094
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4095
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4096
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4097
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4098
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4099
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4100
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4101
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4102
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4103
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4104
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4105
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4106
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4107
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4108
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4109
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4110
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4111
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4112
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4113
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4114
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4115
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4116
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4117
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4118
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4119
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4120
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4121
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4122
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4123
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4124
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4125
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4126
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4127
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4128
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4129
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4130
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4131
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4132
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4133
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4134
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4135
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4136
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4137
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4138
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4139
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4140
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4141
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4142
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4143
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4144
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4145
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4146
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4147
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4148
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4149
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4150
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4151
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4152
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4153
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4154
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4155
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4156
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4157
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4158
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4159
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4160
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4161
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4172
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4173
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4174
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4175
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4176
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4177
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4178
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4179
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4192
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4193
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4194
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4195
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4196
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4197
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4198
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4199
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4200
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4201
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4202
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4203
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4204
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4205
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4206
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4207
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4208
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4209
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4210
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4211
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4212
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4213
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4214
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4215
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4216
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4217
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4218
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4219
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4220
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4221
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4222
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4223
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4224
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4225
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4226
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4227
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4228
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4229
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4230
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4231
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4232
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4233
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4234
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4235
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4236
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4252
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4253
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4254
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4255
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4256
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4257
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4258
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4259
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4260
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4261
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4262
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4263
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4264
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4265
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4266
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4267
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4268
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4269
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4270
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4271
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4272
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4273
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4274
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4275
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4276
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4292
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4293
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4294
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4295
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4296
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4297
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4298
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4299
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4300
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4301
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4312
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4313
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4314
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4315
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4316
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4317
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4318
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4319
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4320
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4321
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4322
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4323
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4324
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4325
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4326
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4327
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4328
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4329
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4330
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4331
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4332
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4333
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4334
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4352
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4353
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4354
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4355
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4356
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4357
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4358
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4359
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4360
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4361
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4362
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4363
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4364
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4365
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4366
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4367
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4368
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4369
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4370
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4371
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4372
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4373
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4374
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4392
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4393
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4394
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4395
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4396
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4397
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4398
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4399
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4400
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4401
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4402
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4403
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4404
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4405
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4406
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4407
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4408
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4409
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4410
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4411
/
delete from krew_doc_typ_attr_t where doc_typ_attrib_id = 4412
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 12759
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 12761
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 1005
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 1007
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 1009
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 1010
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 1011
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 1013
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 1015
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 1017
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 1032
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3735
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3737
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3739
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3740
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3742
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3744
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3746
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3748
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3749
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3751
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3855
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3915
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3937
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 3939
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 8332
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 8433
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 8612
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 9376
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 9415
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 9427
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 9576
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 9755
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 9957
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 9837
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 9855
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10018
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10019
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10074
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10075
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10076
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10077
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10079
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10196
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10336
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10497
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10659
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10796
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 10804
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 11457
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 11558
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 13038
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 13138
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 13158
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 13375
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 13699
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id = 13701
/
delete from krew_rule_attr_t where rule_attr_id = 1001
/
delete from krew_rule_attr_t where rule_attr_id = 1002
/
delete from krew_rule_attr_t where rule_attr_id = 1003
/
delete from krew_rule_attr_t where rule_attr_id = 1030
/
delete from krew_rule_attr_t where rule_attr_id = 1129
/
delete from krew_rule_attr_t where rule_attr_id = 1177
/
delete from krew_rule_attr_t where rule_attr_id = 1240
/
delete from krew_rule_attr_t where rule_attr_id = 1241
/
delete from krew_rule_attr_t where rule_attr_id = 1242
/
delete from krew_rule_attr_t where rule_attr_id = 1243
/
delete from krew_rule_attr_t where rule_attr_id = 1245
/
delete from krew_rule_attr_t where rule_attr_id = 1246
/
delete from krew_rule_attr_t where rule_attr_id = 3694
/
delete from krew_rule_attr_t where rule_attr_id = 3935
/
delete from krew_rule_attr_t where rule_attr_id = 8371
/
delete from krew_rule_attr_t where rule_attr_id = 8431
/
delete from krew_rule_attr_t where rule_attr_id = 8651
/
delete from krew_rule_attr_t where rule_attr_id = 8652
/
delete from krew_rule_attr_t where rule_attr_id = 8653
/
delete from krew_rule_attr_t where rule_attr_id = 8976
/
delete from krew_rule_attr_t where rule_attr_id = 9374
/
delete from krew_rule_attr_t where rule_attr_id = 9574
/
delete from krew_rule_attr_t where rule_attr_id = 9754
/
delete from krew_rule_attr_t where rule_attr_id = 9834
/
delete from krew_rule_attr_t where rule_attr_id = 9854
/
delete from krew_rule_attr_t where rule_attr_id = 9942
/
delete from krew_rule_attr_t where rule_attr_id = 10014
/
delete from krew_rule_attr_t where rule_attr_id = 10015
/
delete from krew_rule_attr_t where rule_attr_id = 10054
/
delete from krew_rule_attr_t where rule_attr_id = 10055
/
delete from krew_rule_attr_t where rule_attr_id = 10056
/
delete from krew_rule_attr_t where rule_attr_id = 10057
/
delete from krew_rule_attr_t where rule_attr_id = 10058
/
delete from krew_rule_attr_t where rule_attr_id = 10334
/
delete from krew_rule_attr_t where rule_attr_id = 10354
/
delete from krew_rule_attr_t where rule_attr_id = 10374
/
delete from krew_rule_attr_t where rule_attr_id = 10375
/
delete from krew_rule_attr_t where rule_attr_id = 10376
/
delete from krew_rule_attr_t where rule_attr_id = 10377
/
delete from krew_rule_attr_t where rule_attr_id = 10378
/
delete from krew_rule_attr_t where rule_attr_id = 10379
/
delete from krew_rule_attr_t where rule_attr_id = 10380
/
delete from krew_rule_attr_t where rule_attr_id = 10381
/
delete from krew_rule_attr_t where rule_attr_id = 10382
/
delete from krew_rule_attr_t where rule_attr_id = 10383
/
delete from krew_rule_attr_t where rule_attr_id = 10385
/
delete from krew_rule_attr_t where rule_attr_id = 10414
/
delete from krew_rule_attr_t where rule_attr_id = 10454
/
delete from krew_rule_attr_t where rule_attr_id = 10455
/
delete from krew_rule_attr_t where rule_attr_id = 10494
/
delete from krew_rule_attr_t where rule_attr_id = 10498
/
delete from krew_rule_attr_t where rule_attr_id = 10499
/
delete from krew_rule_attr_t where rule_attr_id = 10514
/
delete from krew_rule_attr_t where rule_attr_id = 10515
/
delete from krew_rule_attr_t where rule_attr_id = 10516
/
delete from krew_rule_attr_t where rule_attr_id = 10517
/
delete from krew_rule_attr_t where rule_attr_id = 10518
/
delete from krew_rule_attr_t where rule_attr_id = 10519
/
delete from krew_rule_attr_t where rule_attr_id = 10520
/
delete from krew_rule_attr_t where rule_attr_id = 10521
/
delete from krew_rule_attr_t where rule_attr_id = 10522
/
delete from krew_rule_attr_t where rule_attr_id = 10523
/
delete from krew_rule_attr_t where rule_attr_id = 10524
/
delete from krew_rule_attr_t where rule_attr_id = 10525
/
delete from krew_rule_attr_t where rule_attr_id = 10526
/
delete from krew_rule_attr_t where rule_attr_id = 10527
/
delete from krew_rule_attr_t where rule_attr_id = 10574
/
delete from krew_rule_attr_t where rule_attr_id = 10575
/
delete from krew_rule_attr_t where rule_attr_id = 10576
/
delete from krew_rule_attr_t where rule_attr_id = 10577
/
delete from krew_rule_attr_t where rule_attr_id = 10578
/
delete from krew_rule_attr_t where rule_attr_id = 10579
/
delete from krew_rule_attr_t where rule_attr_id = 10580
/
delete from krew_rule_attr_t where rule_attr_id = 10594
/
delete from krew_rule_attr_t where rule_attr_id = 10614
/
delete from krew_rule_attr_t where rule_attr_id = 10634
/
delete from krew_rule_attr_t where rule_attr_id = 10635
/
delete from krew_rule_attr_t where rule_attr_id = 10657
/
delete from krew_rule_attr_t where rule_attr_id = 10694
/
delete from krew_rule_attr_t where rule_attr_id = 10695
/
delete from krew_rule_attr_t where rule_attr_id = 10696
/
delete from krew_rule_attr_t where rule_attr_id = 10697
/
delete from krew_rule_attr_t where rule_attr_id = 10698
/
delete from krew_rule_attr_t where rule_attr_id = 10699
/
delete from krew_rule_attr_t where rule_attr_id = 10714
/
delete from krew_rule_attr_t where rule_attr_id = 10715
/
delete from krew_rule_attr_t where rule_attr_id = 10716
/
delete from krew_rule_attr_t where rule_attr_id = 10717
/
delete from krew_rule_attr_t where rule_attr_id = 10718
/
delete from krew_rule_attr_t where rule_attr_id = 10719
/
delete from krew_rule_attr_t where rule_attr_id = 10720
/
delete from krew_rule_attr_t where rule_attr_id = 10721
/
delete from krew_rule_attr_t where rule_attr_id = 10722
/
delete from krew_rule_attr_t where rule_attr_id = 10723
/
delete from krew_rule_attr_t where rule_attr_id = 10724
/
delete from krew_rule_attr_t where rule_attr_id = 10725
/
delete from krew_rule_attr_t where rule_attr_id = 10726
/
delete from krew_rule_attr_t where rule_attr_id = 10727
/
delete from krew_rule_attr_t where rule_attr_id = 10728
/
delete from krew_rule_attr_t where rule_attr_id = 10729
/
delete from krew_rule_attr_t where rule_attr_id = 10730
/
delete from krew_rule_attr_t where rule_attr_id = 10731
/
delete from krew_rule_attr_t where rule_attr_id = 10734
/
delete from krew_rule_attr_t where rule_attr_id = 10735
/
delete from krew_rule_attr_t where rule_attr_id = 10736
/
delete from krew_rule_attr_t where rule_attr_id = 10737
/
delete from krew_rule_attr_t where rule_attr_id = 10738
/
delete from krew_rule_attr_t where rule_attr_id = 10739
/
delete from krew_rule_attr_t where rule_attr_id = 10740
/
delete from krew_rule_attr_t where rule_attr_id = 10741
/
delete from krew_rule_attr_t where rule_attr_id = 10742
/
delete from krew_rule_attr_t where rule_attr_id = 10743
/
delete from krew_rule_attr_t where rule_attr_id = 10744
/
delete from krew_rule_attr_t where rule_attr_id = 10745
/
delete from krew_rule_attr_t where rule_attr_id = 10746
/
delete from krew_rule_attr_t where rule_attr_id = 10747
/
delete from krew_rule_attr_t where rule_attr_id = 10748
/
delete from krew_rule_attr_t where rule_attr_id = 10749
/
delete from krew_rule_attr_t where rule_attr_id = 10795
/
delete from krew_rule_attr_t where rule_attr_id = 10803
/
delete from krew_rule_attr_t where rule_attr_id = 10874
/
delete from krew_rule_attr_t where rule_attr_id = 11455
/
delete from krew_rule_attr_t where rule_attr_id = 11556
/
delete from krew_rule_attr_t where rule_attr_id = 11576
/
delete from krew_rule_attr_t where rule_attr_id = 11656
/
delete from krew_rule_attr_t where rule_attr_id = 12736
/
delete from krew_rule_attr_t where rule_attr_id = 12756
/
delete from krew_rule_attr_t where rule_attr_id = 12757
/
delete from krew_rule_attr_t where rule_attr_id = 13036
/
delete from krew_rule_attr_t where rule_attr_id = 13136
/
delete from krew_rule_attr_t where rule_attr_id = 13156
/
delete from krew_rule_attr_t where rule_attr_id = 13373
/
delete from krew_rule_attr_t where rule_attr_id = 13556
/
delete from krew_rule_attr_t where rule_attr_id = 13557
/
delete from krew_rule_attr_t where rule_attr_id = 13616
/
delete from krew_rule_attr_t where rule_attr_id = 13617
/
delete from krew_rule_attr_t where rule_attr_id = 13618
/
delete from krew_rule_attr_t where rule_attr_id = 13619
/
delete from krew_rule_attr_t where rule_attr_id = 13620
/
delete from krew_rule_attr_t where rule_attr_id = 13621
/
delete from krew_rule_attr_t where rule_attr_id = 13622
/
delete from krew_rule_attr_t where rule_attr_id = 13623
/
delete from krew_rule_attr_t where rule_attr_id = 13624
/
delete from krew_rule_attr_t where rule_attr_id = 13625
/
delete from krew_rule_attr_t where rule_attr_id = 13626
/
delete from krew_rule_attr_t where rule_attr_id = 13627
/
delete from krew_rule_attr_t where rule_attr_id = 13628
/
delete from krew_rule_attr_t where rule_attr_id = 13629
/
delete from krew_rule_attr_t where rule_attr_id = 13630
/
delete from krew_rule_attr_t where rule_attr_id = 13631
/
delete from krew_rule_attr_t where rule_attr_id = 13632
/
delete from krew_rule_attr_t where rule_attr_id = 13633
/
delete from krew_rule_attr_t where rule_attr_id = 13634
/
delete from krew_rule_attr_t where rule_attr_id = 13635
/
delete from krew_rule_attr_t where rule_attr_id = 13636
/
delete from krew_rule_attr_t where rule_attr_id = 13637
/
delete from krew_rule_attr_t where rule_attr_id = 13638
/
delete from krew_rule_attr_t where rule_attr_id = 13639
/
delete from krew_rule_attr_t where rule_attr_id = 13640
/
delete from krew_rule_attr_t where rule_attr_id = 13641
/
delete from krew_rule_attr_t where rule_attr_id = 13642
/
delete from krew_rule_attr_t where rule_attr_id = 13643
/
delete from krew_rule_attr_t where rule_attr_id = 13644
/
delete from krew_rule_attr_t where rule_attr_id = 13696
/
delete from krew_rule_attr_t where rule_attr_id = 13697
/
delete from krew_rule_attr_t where rule_attr_id = 13736
/
delete from krew_rule_attr_t where rule_attr_id = 13737
/
delete from krew_rule_attr_t where rule_attr_id = 13738
/
delete from krew_rule_attr_t where rule_attr_id = 13739
/
delete from krew_rule_attr_t where rule_attr_id = 13740
/
delete from krew_rule_attr_t where rule_attr_id = 13756
/
delete from krew_doc_typ_t where doc_typ_id = 317560
/
delete from krew_doc_typ_t where doc_typ_id = 317662
/
delete from krew_doc_typ_t where doc_typ_id = 317540
/
delete from krew_doc_typ_t where doc_typ_id = 317541
/
delete from krew_doc_typ_t where doc_typ_id = 317542
/
delete from krew_doc_typ_t where doc_typ_id = 317543
/
delete from krew_doc_typ_t where doc_typ_id = 317544
/
delete from krew_doc_typ_t where doc_typ_id = 317545
/
delete from krew_doc_typ_t where doc_typ_id = 317546
/
delete from krew_doc_typ_t where doc_typ_id = 317547
/
delete from krew_doc_typ_t where doc_typ_id = 317582
/
delete from krew_doc_typ_t where doc_typ_id = 317601
/
delete from krew_doc_typ_t where doc_typ_id = 317602
/
delete from krew_doc_typ_t where doc_typ_id = 317603
/
delete from krew_doc_typ_t where doc_typ_id = 317641
/
delete from krew_doc_typ_t where doc_typ_id = 317665
/
delete from krew_doc_typ_t where doc_typ_id = 317083
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357736 or to_rte_node_id = 563357736
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357736
/
delete from krew_rte_node_t where rte_node_id = 563357736
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357737 or to_rte_node_id = 563357737
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357737
/
delete from krew_rte_node_t where rte_node_id = 563357737
/
delete from krew_doc_typ_t where doc_typ_id = 317084
/
delete from krew_doc_typ_t where doc_typ_id = 317240
/
delete from krew_doc_typ_t where doc_typ_id = 317260
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357849 or to_rte_node_id = 563357849
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357849
/
delete from krew_rte_node_t where rte_node_id = 563357849
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357850 or to_rte_node_id = 563357850
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357850
/
delete from krew_rte_node_t where rte_node_id = 563357850
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357851 or to_rte_node_id = 563357851
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357851
/
delete from krew_rte_node_t where rte_node_id = 563357851
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357852 or to_rte_node_id = 563357852
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357852
/
delete from krew_rte_node_t where rte_node_id = 563357852
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357853 or to_rte_node_id = 563357853
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357853
/
delete from krew_rte_node_t where rte_node_id = 563357853
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357854 or to_rte_node_id = 563357854
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357854
/
delete from krew_rte_node_t where rte_node_id = 563357854
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357855 or to_rte_node_id = 563357855
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357855
/
delete from krew_rte_node_t where rte_node_id = 563357855
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357856 or to_rte_node_id = 563357856
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357856
/
delete from krew_rte_node_t where rte_node_id = 563357856
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357857 or to_rte_node_id = 563357857
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357857
/
delete from krew_rte_node_t where rte_node_id = 563357857
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357858 or to_rte_node_id = 563357858
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357858
/
delete from krew_rte_node_t where rte_node_id = 563357858
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357859 or to_rte_node_id = 563357859
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357859
/
delete from krew_rte_node_t where rte_node_id = 563357859
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357860 or to_rte_node_id = 563357860
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357860
/
delete from krew_rte_node_t where rte_node_id = 563357860
/
delete from krew_doc_typ_t where doc_typ_id = 317300
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357862 or to_rte_node_id = 563357862
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357862
/
delete from krew_rte_node_t where rte_node_id = 563357862
/
delete from krew_doc_typ_t where doc_typ_id = 317301
/
delete from krew_doc_typ_t where doc_typ_id = 317320
/
delete from krew_doc_typ_t where doc_typ_id = 317321
/
delete from krew_doc_typ_t where doc_typ_id = 317322
/
delete from krew_doc_typ_t where doc_typ_id = 317323
/
delete from krew_doc_typ_t where doc_typ_id = 317325
/
delete from krew_doc_typ_t where doc_typ_id = 317360
/
delete from krew_doc_typ_t where doc_typ_id = 317361
/
delete from krew_doc_typ_t where doc_typ_id = 317402
/
delete from krew_doc_typ_t where doc_typ_id = 317403
/
delete from krew_doc_typ_t where doc_typ_id = 317461
/
delete from krew_doc_typ_t where doc_typ_id = 317460
/
delete from krew_doc_typ_t where doc_typ_id = 316962
/
delete from krew_doc_typ_t where doc_typ_id = 316963
/
delete from krew_doc_typ_t where doc_typ_id = 316964
/
delete from krew_doc_typ_t where doc_typ_id = 316965
/
delete from krew_doc_typ_t where doc_typ_id = 316966
/
delete from krew_doc_typ_t where doc_typ_id = 316967
/
delete from krew_doc_typ_t where doc_typ_id = 316968
/
delete from krew_doc_typ_t where doc_typ_id = 316969
/
delete from krew_doc_typ_t where doc_typ_id = 316980
/
delete from krew_doc_typ_t where doc_typ_id = 316981
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357659 or to_rte_node_id = 563357659
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357659
/
delete from krew_rte_node_t where rte_node_id = 563357659
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357660 or to_rte_node_id = 563357660
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357660
/
delete from krew_rte_node_t where rte_node_id = 563357660
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357661 or to_rte_node_id = 563357661
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357661
/
delete from krew_rte_node_t where rte_node_id = 563357661
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357662 or to_rte_node_id = 563357662
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357662
/
delete from krew_rte_node_t where rte_node_id = 563357662
/
delete from krew_doc_typ_t where doc_typ_id = 317046
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357664 or to_rte_node_id = 563357664
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357664
/
delete from krew_rte_node_t where rte_node_id = 563357664
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357665 or to_rte_node_id = 563357665
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357665
/
delete from krew_rte_node_t where rte_node_id = 563357665
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357666 or to_rte_node_id = 563357666
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357666
/
delete from krew_rte_node_t where rte_node_id = 563357666
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357667 or to_rte_node_id = 563357667
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357667
/
delete from krew_rte_node_t where rte_node_id = 563357667
/
delete from krew_doc_typ_t where doc_typ_id = 317047
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357671 or to_rte_node_id = 563357671
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357671
/
delete from krew_rte_node_t where rte_node_id = 563357671
/
delete from krew_doc_typ_t where doc_typ_id = 317049
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357687 or to_rte_node_id = 563357687
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357687
/
delete from krew_rte_node_t where rte_node_id = 563357687
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357688 or to_rte_node_id = 563357688
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357688
/
delete from krew_rte_node_t where rte_node_id = 563357688
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357689 or to_rte_node_id = 563357689
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357689
/
delete from krew_rte_node_t where rte_node_id = 563357689
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357690 or to_rte_node_id = 563357690
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357690
/
delete from krew_rte_node_t where rte_node_id = 563357690
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357691 or to_rte_node_id = 563357691
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357691
/
delete from krew_rte_node_t where rte_node_id = 563357691
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357692 or to_rte_node_id = 563357692
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357692
/
delete from krew_rte_node_t where rte_node_id = 563357692
/
delete from krew_doc_typ_t where doc_typ_id = 317052
/
delete from krew_doc_typ_t where doc_typ_id = 316961
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357729 or to_rte_node_id = 563357729
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357729
/
delete from krew_rte_node_t where rte_node_id = 563357729
/
delete from krew_doc_typ_t where doc_typ_id = 317080
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357731 or to_rte_node_id = 563357731
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357731
/
delete from krew_rte_node_t where rte_node_id = 563357731
/
delete from krew_doc_typ_t where doc_typ_id = 317081
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357559 or to_rte_node_id = 563357559
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357559
/
delete from krew_rte_node_t where rte_node_id = 563357559
/
delete from krew_doc_typ_t where doc_typ_id = 316927
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357561 or to_rte_node_id = 563357561
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357561
/
delete from krew_rte_node_t where rte_node_id = 563357561
/
delete from krew_doc_typ_t where doc_typ_id = 316928
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357563 or to_rte_node_id = 563357563
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357563
/
delete from krew_rte_node_t where rte_node_id = 563357563
/
delete from krew_doc_typ_t where doc_typ_id = 316929
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357565 or to_rte_node_id = 563357565
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357565
/
delete from krew_rte_node_t where rte_node_id = 563357565
/
delete from krew_doc_typ_t where doc_typ_id = 316930
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357567 or to_rte_node_id = 563357567
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357567
/
delete from krew_rte_node_t where rte_node_id = 563357567
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357568 or to_rte_node_id = 563357568
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357568
/
delete from krew_rte_node_t where rte_node_id = 563357568
/
delete from krew_doc_typ_t where doc_typ_id = 316931
/
delete from krew_doc_typ_t where doc_typ_id = 316956
/
delete from krew_doc_typ_t where doc_typ_id = 316957
/
delete from krew_doc_typ_t where doc_typ_id = 316958
/
delete from krew_doc_typ_t where doc_typ_id = 316959
/
delete from krew_doc_typ_t where doc_typ_id = 316960
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563350325 or to_rte_node_id = 563350325
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563350325
/
delete from krew_rte_node_t where rte_node_id = 563350325
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563350326 or to_rte_node_id = 563350326
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563350326
/
delete from krew_rte_node_t where rte_node_id = 563350326
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563350327 or to_rte_node_id = 563350327
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563350327
/
delete from krew_rte_node_t where rte_node_id = 563350327
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563350328 or to_rte_node_id = 563350328
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563350328
/
delete from krew_rte_node_t where rte_node_id = 563350328
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563350329 or to_rte_node_id = 563350329
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563350329
/
delete from krew_rte_node_t where rte_node_id = 563350329
/
delete from krew_doc_typ_t where doc_typ_id = 313157
/
delete from krew_doc_typ_t where doc_typ_id = 316650
/
delete from krew_doc_typ_t where doc_typ_id = 316651
/
delete from krew_doc_typ_t where doc_typ_id = 316652
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357102 or to_rte_node_id = 563357102
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357102
/
delete from krew_rte_node_t where rte_node_id = 563357102
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357103 or to_rte_node_id = 563357103
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357103
/
delete from krew_rte_node_t where rte_node_id = 563357103
/
delete from krew_doc_typ_t where doc_typ_id = 316653
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357329 or to_rte_node_id = 563357329
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357329
/
delete from krew_rte_node_t where rte_node_id = 563357329
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357330 or to_rte_node_id = 563357330
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357330
/
delete from krew_rte_node_t where rte_node_id = 563357330
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357331 or to_rte_node_id = 563357331
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357331
/
delete from krew_rte_node_t where rte_node_id = 563357331
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357332 or to_rte_node_id = 563357332
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357332
/
delete from krew_rte_node_t where rte_node_id = 563357332
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357333 or to_rte_node_id = 563357333
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357333
/
delete from krew_rte_node_t where rte_node_id = 563357333
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357334 or to_rte_node_id = 563357334
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357334
/
delete from krew_rte_node_t where rte_node_id = 563357334
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357335 or to_rte_node_id = 563357335
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357335
/
delete from krew_rte_node_t where rte_node_id = 563357335
/
delete from krew_doc_typ_t where doc_typ_id = 316760
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357351 or to_rte_node_id = 563357351
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357351
/
delete from krew_rte_node_t where rte_node_id = 563357351
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357352 or to_rte_node_id = 563357352
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357352
/
delete from krew_rte_node_t where rte_node_id = 563357352
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357353 or to_rte_node_id = 563357353
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357353
/
delete from krew_rte_node_t where rte_node_id = 563357353
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357349 or to_rte_node_id = 563357349
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357349
/
delete from krew_rte_node_t where rte_node_id = 563357349
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357350 or to_rte_node_id = 563357350
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357350
/
delete from krew_rte_node_t where rte_node_id = 563357350
/
delete from krew_doc_typ_t where doc_typ_id = 316780
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357355 or to_rte_node_id = 563357355
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357355
/
delete from krew_rte_node_t where rte_node_id = 563357355
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357356 or to_rte_node_id = 563357356
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357356
/
delete from krew_rte_node_t where rte_node_id = 563357356
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357357 or to_rte_node_id = 563357357
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357357
/
delete from krew_rte_node_t where rte_node_id = 563357357
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357358 or to_rte_node_id = 563357358
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357358
/
delete from krew_rte_node_t where rte_node_id = 563357358
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357359 or to_rte_node_id = 563357359
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357359
/
delete from krew_rte_node_t where rte_node_id = 563357359
/
delete from krew_doc_typ_t where doc_typ_id = 316781
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357509 or to_rte_node_id = 563357509
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357509
/
delete from krew_rte_node_t where rte_node_id = 563357509
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357510 or to_rte_node_id = 563357510
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357510
/
delete from krew_rte_node_t where rte_node_id = 563357510
/
delete from krew_doc_typ_t where doc_typ_id = 316900
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357529 or to_rte_node_id = 563357529
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357529
/
delete from krew_rte_node_t where rte_node_id = 563357529
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357530 or to_rte_node_id = 563357530
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357530
/
delete from krew_rte_node_t where rte_node_id = 563357530
/
delete from krew_doc_typ_t where doc_typ_id = 316920
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563357557 or to_rte_node_id = 563357557
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563357557
/
delete from krew_rte_node_t where rte_node_id = 563357557
/
delete from krew_doc_typ_t where doc_typ_id = 316926
/
delete from krew_doc_typ_t where doc_typ_id = 310032
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 140885 or to_rte_node_id = 140885
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 140885
/
delete from krew_rte_node_t where rte_node_id = 140885
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 140886 or to_rte_node_id = 140886
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 140886
/
delete from krew_rte_node_t where rte_node_id = 140886
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 140887 or to_rte_node_id = 140887
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 140887
/
delete from krew_rte_node_t where rte_node_id = 140887
/
delete from krew_doc_typ_t where doc_typ_id = 133414
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 140893 or to_rte_node_id = 140893
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 140893
/
delete from krew_rte_node_t where rte_node_id = 140893
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 140894 or to_rte_node_id = 140894
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 140894
/
delete from krew_rte_node_t where rte_node_id = 140894
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 140895 or to_rte_node_id = 140895
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 140895
/
delete from krew_rte_node_t where rte_node_id = 140895
/
delete from krew_doc_typ_t where doc_typ_id = 133416
/
delete from krew_doc_typ_t where doc_typ_id = 154837
/
delete from krew_doc_typ_t where doc_typ_id = 172086
/
delete from krew_doc_typ_t where doc_typ_id = 172089
/
delete from krew_doc_typ_t where doc_typ_id = 184299
/
delete from krew_doc_typ_t where doc_typ_id = 184301
/
delete from krew_doc_typ_t where doc_typ_id = 184307
/
delete from krew_doc_typ_t where doc_typ_id = 184309
/
delete from krew_doc_typ_t where doc_typ_id = 184311
/
delete from krew_doc_typ_t where doc_typ_id = 184313
/
delete from krew_doc_typ_t where doc_typ_id = 184315
/
delete from krew_doc_typ_t where doc_typ_id = 184316
/
delete from krew_doc_typ_t where doc_typ_id = 184317
/
delete from krew_doc_typ_t where doc_typ_id = 184318
/
delete from krew_doc_typ_t where doc_typ_id = 184322
/
delete from krew_doc_typ_t where doc_typ_id = 184325
/
delete from krew_doc_typ_t where doc_typ_id = 184328
/
delete from krew_doc_typ_t where doc_typ_id = 184335
/
delete from krew_doc_typ_t where doc_typ_id = 184336
/
delete from krew_doc_typ_t where doc_typ_id = 184337
/
delete from krew_doc_typ_t where doc_typ_id = 184338
/
delete from krew_doc_typ_t where doc_typ_id = 184339
/
delete from krew_doc_typ_t where doc_typ_id = 184340
/
delete from krew_doc_typ_t where doc_typ_id = 184341
/
delete from krew_doc_typ_t where doc_typ_id = 184342
/
delete from krew_doc_typ_t where doc_typ_id = 184343
/
delete from krew_doc_typ_t where doc_typ_id = 184344
/
delete from krew_doc_typ_t where doc_typ_id = 184345
/
delete from krew_doc_typ_t where doc_typ_id = 184346
/
delete from krew_doc_typ_t where doc_typ_id = 184350
/
delete from krew_doc_typ_t where doc_typ_id = 184353
/
delete from krew_doc_typ_t where doc_typ_id = 184303
/
delete from krew_doc_typ_t where doc_typ_id = 184304
/
delete from krew_doc_typ_t where doc_typ_id = 184358
/
delete from krew_doc_typ_t where doc_typ_id = 184359
/
delete from krew_doc_typ_t where doc_typ_id = 184363
/
delete from krew_doc_typ_t where doc_typ_id = 184372
/
delete from krew_doc_typ_t where doc_typ_id = 184377
/
delete from krew_doc_typ_t where doc_typ_id = 184378
/
delete from krew_doc_typ_t where doc_typ_id = 184330
/
delete from krew_doc_typ_t where doc_typ_id = 184331
/
delete from krew_doc_typ_t where doc_typ_id = 234351
/
delete from krew_doc_typ_t where doc_typ_id = 234482
/
delete from krew_doc_typ_t where doc_typ_id = 236528
/
delete from krew_doc_typ_t where doc_typ_id = 184355
/
delete from krew_doc_typ_t where doc_typ_id = 184357
/
delete from krew_doc_typ_t where doc_typ_id = 238900
/
delete from krew_doc_typ_t where doc_typ_id = 240726
/
delete from krew_doc_typ_t where doc_typ_id = 248508
/
delete from krew_doc_typ_t where doc_typ_id = 249623
/
delete from krew_doc_typ_t where doc_typ_id = 258912
/
delete from krew_doc_typ_t where doc_typ_id = 258918
/
delete from krew_doc_typ_t where doc_typ_id = 258919
/
delete from krew_doc_typ_t where doc_typ_id = 286903
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563295701 or to_rte_node_id = 563295701
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563295701
/
delete from krew_rte_node_t where rte_node_id = 563295701
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563295702 or to_rte_node_id = 563295702
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563295702
/
delete from krew_rte_node_t where rte_node_id = 563295702
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563295703 or to_rte_node_id = 563295703
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563295703
/
delete from krew_rte_node_t where rte_node_id = 563295703
/
delete from krew_doc_typ_t where doc_typ_id = 287551
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563303904 or to_rte_node_id = 563303904
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563303904
/
delete from krew_rte_node_t where rte_node_id = 563303904
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563303905 or to_rte_node_id = 563303905
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563303905
/
delete from krew_rte_node_t where rte_node_id = 563303905
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563303906 or to_rte_node_id = 563303906
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563303906
/
delete from krew_rte_node_t where rte_node_id = 563303906
/
delete from krew_doc_typ_t where doc_typ_id = 290799
/
delete from krew_doc_typ_t where doc_typ_id = 291487
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563308591 or to_rte_node_id = 563308591
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563308591
/
delete from krew_rte_node_t where rte_node_id = 563308591
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563308592 or to_rte_node_id = 563308592
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563308592
/
delete from krew_rte_node_t where rte_node_id = 563308592
/
delete from krew_doc_typ_t where doc_typ_id = 292444
/
delete from krew_doc_typ_t where doc_typ_id = 293342
/
delete from krew_doc_typ_t where doc_typ_id = 293343
/
delete from krew_doc_typ_t where doc_typ_id = 293347
/
delete from krew_doc_typ_t where doc_typ_id = 293348
/
delete from krew_doc_typ_t where doc_typ_id = 293349
/
delete from krew_doc_typ_t where doc_typ_id = 293350
/
delete from krew_doc_typ_t where doc_typ_id = 293351
/
delete from krew_doc_typ_t where doc_typ_id = 293353
/
delete from krew_doc_typ_t where doc_typ_id = 293355
/
delete from krew_doc_typ_t where doc_typ_id = 293356
/
delete from krew_doc_typ_t where doc_typ_id = 293357
/
delete from krew_doc_typ_t where doc_typ_id = 293359
/
delete from krew_doc_typ_t where doc_typ_id = 293360
/
delete from krew_doc_typ_t where doc_typ_id = 293364
/
delete from krew_doc_typ_t where doc_typ_id = 293368
/
delete from krew_doc_typ_t where doc_typ_id = 293369
/
delete from krew_doc_typ_t where doc_typ_id = 293370
/
delete from krew_doc_typ_t where doc_typ_id = 293372
/
delete from krew_doc_typ_t where doc_typ_id = 293373
/
delete from krew_doc_typ_t where doc_typ_id = 293374
/
delete from krew_doc_typ_t where doc_typ_id = 293375
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317875 or to_rte_node_id = 563317875
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317875
/
delete from krew_rte_node_t where rte_node_id = 563317875
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317876 or to_rte_node_id = 563317876
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317876
/
delete from krew_rte_node_t where rte_node_id = 563317876
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317877 or to_rte_node_id = 563317877
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317877
/
delete from krew_rte_node_t where rte_node_id = 563317877
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317878 or to_rte_node_id = 563317878
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317878
/
delete from krew_rte_node_t where rte_node_id = 563317878
/
delete from krew_doc_typ_t where doc_typ_id = 296653
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317887 or to_rte_node_id = 563317887
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317887
/
delete from krew_rte_node_t where rte_node_id = 563317887
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317888 or to_rte_node_id = 563317888
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317888
/
delete from krew_rte_node_t where rte_node_id = 563317888
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317889 or to_rte_node_id = 563317889
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317889
/
delete from krew_rte_node_t where rte_node_id = 563317889
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317890 or to_rte_node_id = 563317890
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317890
/
delete from krew_rte_node_t where rte_node_id = 563317890
/
delete from krew_doc_typ_t where doc_typ_id = 296655
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317892 or to_rte_node_id = 563317892
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317892
/
delete from krew_rte_node_t where rte_node_id = 563317892
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317893 or to_rte_node_id = 563317893
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317893
/
delete from krew_rte_node_t where rte_node_id = 563317893
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317894 or to_rte_node_id = 563317894
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317894
/
delete from krew_rte_node_t where rte_node_id = 563317894
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317895 or to_rte_node_id = 563317895
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317895
/
delete from krew_rte_node_t where rte_node_id = 563317895
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317896 or to_rte_node_id = 563317896
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317896
/
delete from krew_rte_node_t where rte_node_id = 563317896
/
delete from krew_doc_typ_t where doc_typ_id = 296657
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317934 or to_rte_node_id = 563317934
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317934
/
delete from krew_rte_node_t where rte_node_id = 563317934
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317935 or to_rte_node_id = 563317935
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317935
/
delete from krew_rte_node_t where rte_node_id = 563317935
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317936 or to_rte_node_id = 563317936
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317936
/
delete from krew_rte_node_t where rte_node_id = 563317936
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317937 or to_rte_node_id = 563317937
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317937
/
delete from krew_rte_node_t where rte_node_id = 563317937
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317938 or to_rte_node_id = 563317938
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317938
/
delete from krew_rte_node_t where rte_node_id = 563317938
/
delete from krew_doc_typ_t where doc_typ_id = 296665
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317940 or to_rte_node_id = 563317940
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317940
/
delete from krew_rte_node_t where rte_node_id = 563317940
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317941 or to_rte_node_id = 563317941
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317941
/
delete from krew_rte_node_t where rte_node_id = 563317941
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317942 or to_rte_node_id = 563317942
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317942
/
delete from krew_rte_node_t where rte_node_id = 563317942
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317943 or to_rte_node_id = 563317943
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317943
/
delete from krew_rte_node_t where rte_node_id = 563317943
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317944 or to_rte_node_id = 563317944
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317944
/
delete from krew_rte_node_t where rte_node_id = 563317944
/
delete from krew_doc_typ_t where doc_typ_id = 296666
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317951 or to_rte_node_id = 563317951
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317951
/
delete from krew_rte_node_t where rte_node_id = 563317951
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317952 or to_rte_node_id = 563317952
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317952
/
delete from krew_rte_node_t where rte_node_id = 563317952
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317953 or to_rte_node_id = 563317953
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317953
/
delete from krew_rte_node_t where rte_node_id = 563317953
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317954 or to_rte_node_id = 563317954
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317954
/
delete from krew_rte_node_t where rte_node_id = 563317954
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317955 or to_rte_node_id = 563317955
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317955
/
delete from krew_rte_node_t where rte_node_id = 563317955
/
delete from krew_doc_typ_t where doc_typ_id = 296668
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317961 or to_rte_node_id = 563317961
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317961
/
delete from krew_rte_node_t where rte_node_id = 563317961
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317957 or to_rte_node_id = 563317957
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317957
/
delete from krew_rte_node_t where rte_node_id = 563317957
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317958 or to_rte_node_id = 563317958
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317958
/
delete from krew_rte_node_t where rte_node_id = 563317958
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317959 or to_rte_node_id = 563317959
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317959
/
delete from krew_rte_node_t where rte_node_id = 563317959
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563317960 or to_rte_node_id = 563317960
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563317960
/
delete from krew_rte_node_t where rte_node_id = 563317960
/
delete from krew_doc_typ_t where doc_typ_id = 296669
/
delete from krew_doc_typ_t where doc_typ_id = 296670
/
delete from krew_doc_typ_t where doc_typ_id = 299642
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563324395 or to_rte_node_id = 563324395
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563324395
/
delete from krew_rte_node_t where rte_node_id = 563324395
/
delete from krew_doc_typ_t where doc_typ_id = 300340
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563324397 or to_rte_node_id = 563324397
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563324397
/
delete from krew_rte_node_t where rte_node_id = 563324397
/
delete from krew_doc_typ_t where doc_typ_id = 300341
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563324399 or to_rte_node_id = 563324399
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563324399
/
delete from krew_rte_node_t where rte_node_id = 563324399
/
delete from krew_doc_typ_t where doc_typ_id = 300342
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563324401 or to_rte_node_id = 563324401
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563324401
/
delete from krew_rte_node_t where rte_node_id = 563324401
/
delete from krew_doc_typ_t where doc_typ_id = 300343
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563324405 or to_rte_node_id = 563324405
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563324405
/
delete from krew_rte_node_t where rte_node_id = 563324405
/
delete from krew_doc_typ_t where doc_typ_id = 300345
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563324418 or to_rte_node_id = 563324418
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563324418
/
delete from krew_rte_node_t where rte_node_id = 563324418
/
delete from krew_doc_typ_t where doc_typ_id = 300350
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563324426 or to_rte_node_id = 563324426
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563324426
/
delete from krew_rte_node_t where rte_node_id = 563324426
/
delete from krew_doc_typ_t where doc_typ_id = 300354
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563324430 or to_rte_node_id = 563324430
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563324430
/
delete from krew_rte_node_t where rte_node_id = 563324430
/
delete from krew_doc_typ_t where doc_typ_id = 300356
/
delete from krew_doc_typ_t where doc_typ_id = 301131
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563331849 or to_rte_node_id = 563331849
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563331849
/
delete from krew_rte_node_t where rte_node_id = 563331849
/
delete from krew_doc_typ_t where doc_typ_id = 303654
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563331891 or to_rte_node_id = 563331891
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563331891
/
delete from krew_rte_node_t where rte_node_id = 563331891
/
delete from krew_doc_typ_t where doc_typ_id = 303675
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563337533
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563337536 or to_rte_node_id = 563337536
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563337536
/
delete from krew_rte_node_t where rte_node_id = 563337536
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563337537 or to_rte_node_id = 563337537
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563337537
/
delete from krew_rte_node_t where rte_node_id = 563337537
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563337538
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563337539 or to_rte_node_id = 563337539
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563337539
/
delete from krew_rte_node_t where rte_node_id = 563337539
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563337540 or to_rte_node_id = 563337540
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563337540
/
delete from krew_rte_node_t where rte_node_id = 563337540
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563337531 or to_rte_node_id = 563337531
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563337531
/
delete from krew_rte_node_t where rte_node_id = 563337531
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563337532 or to_rte_node_id = 563337532
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563337532
/
delete from krew_rte_node_t where rte_node_id = 563337532
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563337533
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563337534 or to_rte_node_id = 563337534
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563337534
/
delete from krew_rte_node_t where rte_node_id = 563337534
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563337533
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563337535 or to_rte_node_id = 563337535
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563337535
/
delete from krew_rte_node_t where rte_node_id = 563337535
/
delete from krew_doc_typ_t where doc_typ_id = 306389
/
delete from krew_doc_typ_t where doc_typ_id = 317880
/
delete from krew_doc_typ_t where doc_typ_id = 317900
/
delete from krew_doc_typ_t where doc_typ_id = 317901
/
delete from krew_doc_typ_t where doc_typ_id = 317902
/
delete from krew_doc_typ_t where doc_typ_id = 317903
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358169 or to_rte_node_id = 563358169
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358169
/
delete from krew_rte_node_t where rte_node_id = 563358169
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358170 or to_rte_node_id = 563358170
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358170
/
delete from krew_rte_node_t where rte_node_id = 563358170
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358171 or to_rte_node_id = 563358171
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358171
/
delete from krew_rte_node_t where rte_node_id = 563358171
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358172 or to_rte_node_id = 563358172
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358172
/
delete from krew_rte_node_t where rte_node_id = 563358172
/
delete from krew_doc_typ_t where doc_typ_id = 317940
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358174 or to_rte_node_id = 563358174
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358174
/
delete from krew_rte_node_t where rte_node_id = 563358174
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358175 or to_rte_node_id = 563358175
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358175
/
delete from krew_rte_node_t where rte_node_id = 563358175
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358176 or to_rte_node_id = 563358176
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358176
/
delete from krew_rte_node_t where rte_node_id = 563358176
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358177 or to_rte_node_id = 563358177
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358177
/
delete from krew_rte_node_t where rte_node_id = 563358177
/
delete from krew_doc_typ_t where doc_typ_id = 317941
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358179 or to_rte_node_id = 563358179
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358179
/
delete from krew_rte_node_t where rte_node_id = 563358179
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358180 or to_rte_node_id = 563358180
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358180
/
delete from krew_rte_node_t where rte_node_id = 563358180
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358181 or to_rte_node_id = 563358181
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358181
/
delete from krew_rte_node_t where rte_node_id = 563358181
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358182 or to_rte_node_id = 563358182
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358182
/
delete from krew_rte_node_t where rte_node_id = 563358182
/
delete from krew_doc_typ_t where doc_typ_id = 317942
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358184 or to_rte_node_id = 563358184
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358184
/
delete from krew_rte_node_t where rte_node_id = 563358184
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358185 or to_rte_node_id = 563358185
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358185
/
delete from krew_rte_node_t where rte_node_id = 563358185
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358186 or to_rte_node_id = 563358186
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358186
/
delete from krew_rte_node_t where rte_node_id = 563358186
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358187 or to_rte_node_id = 563358187
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358187
/
delete from krew_rte_node_t where rte_node_id = 563358187
/
delete from krew_doc_typ_t where doc_typ_id = 317943
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358189 or to_rte_node_id = 563358189
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358189
/
delete from krew_rte_node_t where rte_node_id = 563358189
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358190 or to_rte_node_id = 563358190
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358190
/
delete from krew_rte_node_t where rte_node_id = 563358190
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358191 or to_rte_node_id = 563358191
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358191
/
delete from krew_rte_node_t where rte_node_id = 563358191
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358192 or to_rte_node_id = 563358192
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358192
/
delete from krew_rte_node_t where rte_node_id = 563358192
/
delete from krew_doc_typ_t where doc_typ_id = 317944
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358194 or to_rte_node_id = 563358194
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358194
/
delete from krew_rte_node_t where rte_node_id = 563358194
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358195 or to_rte_node_id = 563358195
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358195
/
delete from krew_rte_node_t where rte_node_id = 563358195
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358196 or to_rte_node_id = 563358196
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358196
/
delete from krew_rte_node_t where rte_node_id = 563358196
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358197 or to_rte_node_id = 563358197
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358197
/
delete from krew_rte_node_t where rte_node_id = 563358197
/
delete from krew_doc_typ_t where doc_typ_id = 317945
/
delete from krew_doc_typ_t where doc_typ_id = 317960
/
delete from krew_doc_typ_t where doc_typ_id = 318000
/
delete from krew_doc_typ_t where doc_typ_id = 318021
/
delete from krew_doc_typ_t where doc_typ_id = 318022
/
delete from krew_doc_typ_t where doc_typ_id = 318060
/
delete from krew_doc_typ_t where doc_typ_id = 318080
/
delete from krew_doc_typ_t where doc_typ_id = 318120
/
delete from krew_doc_typ_t where doc_typ_id = 318141
/
delete from krew_doc_typ_t where doc_typ_id = 318160
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358229 or to_rte_node_id = 563358229
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358229
/
delete from krew_rte_node_t where rte_node_id = 563358229
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358230 or to_rte_node_id = 563358230
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358230
/
delete from krew_rte_node_t where rte_node_id = 563358230
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358231 or to_rte_node_id = 563358231
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358231
/
delete from krew_rte_node_t where rte_node_id = 563358231
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358232 or to_rte_node_id = 563358232
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358232
/
delete from krew_rte_node_t where rte_node_id = 563358232
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358233 or to_rte_node_id = 563358233
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358233
/
delete from krew_rte_node_t where rte_node_id = 563358233
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358234
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358235 or to_rte_node_id = 563358235
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358235
/
delete from krew_rte_node_t where rte_node_id = 563358235
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358236 or to_rte_node_id = 563358236
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358236
/
delete from krew_rte_node_t where rte_node_id = 563358236
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358237
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358238 or to_rte_node_id = 563358238
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358238
/
delete from krew_rte_node_t where rte_node_id = 563358238
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358239 or to_rte_node_id = 563358239
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358239
/
delete from krew_rte_node_t where rte_node_id = 563358239
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358240 or to_rte_node_id = 563358240
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358240
/
delete from krew_rte_node_t where rte_node_id = 563358240
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358241
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358242 or to_rte_node_id = 563358242
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358242
/
delete from krew_rte_node_t where rte_node_id = 563358242
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358243 or to_rte_node_id = 563358243
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358243
/
delete from krew_rte_node_t where rte_node_id = 563358243
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358244
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358245 or to_rte_node_id = 563358245
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358245
/
delete from krew_rte_node_t where rte_node_id = 563358245
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358246 or to_rte_node_id = 563358246
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358246
/
delete from krew_rte_node_t where rte_node_id = 563358246
/
delete from krew_doc_typ_t where doc_typ_id = 318180
/
delete from krew_doc_typ_t where doc_typ_id = 318200
/
delete from krew_doc_typ_t where doc_typ_id = 318240
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358289 or to_rte_node_id = 563358289
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358289
/
delete from krew_rte_node_t where rte_node_id = 563358289
/
delete from krew_doc_typ_t where doc_typ_id = 318280
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358291 or to_rte_node_id = 563358291
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358291
/
delete from krew_rte_node_t where rte_node_id = 563358291
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358292 or to_rte_node_id = 563358292
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358292
/
delete from krew_rte_node_t where rte_node_id = 563358292
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358293 or to_rte_node_id = 563358293
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358293
/
delete from krew_rte_node_t where rte_node_id = 563358293
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358294 or to_rte_node_id = 563358294
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358294
/
delete from krew_rte_node_t where rte_node_id = 563358294
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358295 or to_rte_node_id = 563358295
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358295
/
delete from krew_rte_node_t where rte_node_id = 563358295
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358296 or to_rte_node_id = 563358296
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358296
/
delete from krew_rte_node_t where rte_node_id = 563358296
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358297 or to_rte_node_id = 563358297
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358297
/
delete from krew_rte_node_t where rte_node_id = 563358297
/
delete from krew_doc_typ_t where doc_typ_id = 318281
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358299 or to_rte_node_id = 563358299
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358299
/
delete from krew_rte_node_t where rte_node_id = 563358299
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358300 or to_rte_node_id = 563358300
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358300
/
delete from krew_rte_node_t where rte_node_id = 563358300
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358301 or to_rte_node_id = 563358301
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358301
/
delete from krew_rte_node_t where rte_node_id = 563358301
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358302 or to_rte_node_id = 563358302
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358302
/
delete from krew_rte_node_t where rte_node_id = 563358302
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358303 or to_rte_node_id = 563358303
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358303
/
delete from krew_rte_node_t where rte_node_id = 563358303
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358304 or to_rte_node_id = 563358304
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358304
/
delete from krew_rte_node_t where rte_node_id = 563358304
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358305 or to_rte_node_id = 563358305
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358305
/
delete from krew_rte_node_t where rte_node_id = 563358305
/
delete from krew_doc_typ_t where doc_typ_id = 318282
/
delete from krew_doc_typ_t where doc_typ_id = 318300
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358311
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358315 or to_rte_node_id = 563358315
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358315
/
delete from krew_rte_node_t where rte_node_id = 563358315
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358316 or to_rte_node_id = 563358316
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358316
/
delete from krew_rte_node_t where rte_node_id = 563358316
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358317
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358318 or to_rte_node_id = 563358318
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358318
/
delete from krew_rte_node_t where rte_node_id = 563358318
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358319 or to_rte_node_id = 563358319
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358319
/
delete from krew_rte_node_t where rte_node_id = 563358319
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358309 or to_rte_node_id = 563358309
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358309
/
delete from krew_rte_node_t where rte_node_id = 563358309
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358310 or to_rte_node_id = 563358310
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358310
/
delete from krew_rte_node_t where rte_node_id = 563358310
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358311
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358312 or to_rte_node_id = 563358312
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358312
/
delete from krew_rte_node_t where rte_node_id = 563358312
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358311
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358313 or to_rte_node_id = 563358313
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358313
/
delete from krew_rte_node_t where rte_node_id = 563358313
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358311
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358314 or to_rte_node_id = 563358314
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358314
/
delete from krew_rte_node_t where rte_node_id = 563358314
/
delete from krew_doc_typ_t where doc_typ_id = 318320
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358329 or to_rte_node_id = 563358329
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358329
/
delete from krew_rte_node_t where rte_node_id = 563358329
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358330 or to_rte_node_id = 563358330
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358330
/
delete from krew_rte_node_t where rte_node_id = 563358330
/
delete from krew_doc_typ_t where doc_typ_id = 318340
/
delete from krew_doc_typ_t where doc_typ_id = 318360
/
delete from krew_doc_typ_t where doc_typ_id = 318400
/
delete from krew_doc_typ_t where doc_typ_id = 318420
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358349 or to_rte_node_id = 563358349
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358349
/
delete from krew_rte_node_t where rte_node_id = 563358349
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358350 or to_rte_node_id = 563358350
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358350
/
delete from krew_rte_node_t where rte_node_id = 563358350
/
delete from krew_doc_typ_t where doc_typ_id = 318460
/
delete from krew_doc_typ_t where doc_typ_id = 318461
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358369 or to_rte_node_id = 563358369
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358369
/
delete from krew_rte_node_t where rte_node_id = 563358369
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358370 or to_rte_node_id = 563358370
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358370
/
delete from krew_rte_node_t where rte_node_id = 563358370
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358371 or to_rte_node_id = 563358371
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358371
/
delete from krew_rte_node_t where rte_node_id = 563358371
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358372 or to_rte_node_id = 563358372
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358372
/
delete from krew_rte_node_t where rte_node_id = 563358372
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358373 or to_rte_node_id = 563358373
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358373
/
delete from krew_rte_node_t where rte_node_id = 563358373
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358374 or to_rte_node_id = 563358374
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358374
/
delete from krew_rte_node_t where rte_node_id = 563358374
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358375 or to_rte_node_id = 563358375
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358375
/
delete from krew_rte_node_t where rte_node_id = 563358375
/
delete from krew_doc_typ_t where doc_typ_id = 318480
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358389 or to_rte_node_id = 563358389
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358389
/
delete from krew_rte_node_t where rte_node_id = 563358389
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358390 or to_rte_node_id = 563358390
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358390
/
delete from krew_rte_node_t where rte_node_id = 563358390
/
delete from krew_doc_typ_t where doc_typ_id = 318500
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358409 or to_rte_node_id = 563358409
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358409
/
delete from krew_rte_node_t where rte_node_id = 563358409
/
delete from krew_doc_typ_t where doc_typ_id = 318520
/
delete from krew_doc_typ_t where doc_typ_id = 318560
/
delete from krew_doc_typ_t where doc_typ_id = 318580
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358429 or to_rte_node_id = 563358429
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358429
/
delete from krew_rte_node_t where rte_node_id = 563358429
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358430 or to_rte_node_id = 563358430
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358430
/
delete from krew_rte_node_t where rte_node_id = 563358430
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358431 or to_rte_node_id = 563358431
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358431
/
delete from krew_rte_node_t where rte_node_id = 563358431
/
delete from krew_doc_typ_t where doc_typ_id = 318603
/
delete from krew_doc_typ_t where doc_typ_id = 318640
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358473 or to_rte_node_id = 563358473
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358473
/
delete from krew_rte_node_t where rte_node_id = 563358473
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358474 or to_rte_node_id = 563358474
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358474
/
delete from krew_rte_node_t where rte_node_id = 563358474
/
delete from krew_doc_typ_t where doc_typ_id = 318661
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358489 or to_rte_node_id = 563358489
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358489
/
delete from krew_rte_node_t where rte_node_id = 563358489
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358490 or to_rte_node_id = 563358490
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358490
/
delete from krew_rte_node_t where rte_node_id = 563358490
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358491
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358492 or to_rte_node_id = 563358492
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358492
/
delete from krew_rte_node_t where rte_node_id = 563358492
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358491
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358493 or to_rte_node_id = 563358493
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358493
/
delete from krew_rte_node_t where rte_node_id = 563358493
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358491
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358494 or to_rte_node_id = 563358494
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358494
/
delete from krew_rte_node_t where rte_node_id = 563358494
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358491
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358495 or to_rte_node_id = 563358495
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358495
/
delete from krew_rte_node_t where rte_node_id = 563358495
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358496 or to_rte_node_id = 563358496
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358496
/
delete from krew_rte_node_t where rte_node_id = 563358496
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358497
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358498 or to_rte_node_id = 563358498
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358498
/
delete from krew_rte_node_t where rte_node_id = 563358498
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358499 or to_rte_node_id = 563358499
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358499
/
delete from krew_rte_node_t where rte_node_id = 563358499
/
delete from krew_doc_typ_t where doc_typ_id = 318680
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358501 or to_rte_node_id = 563358501
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358501
/
delete from krew_rte_node_t where rte_node_id = 563358501
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358502 or to_rte_node_id = 563358502
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358502
/
delete from krew_rte_node_t where rte_node_id = 563358502
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358503 or to_rte_node_id = 563358503
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358503
/
delete from krew_rte_node_t where rte_node_id = 563358503
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358504 or to_rte_node_id = 563358504
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358504
/
delete from krew_rte_node_t where rte_node_id = 563358504
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358505 or to_rte_node_id = 563358505
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358505
/
delete from krew_rte_node_t where rte_node_id = 563358505
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358506
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358507 or to_rte_node_id = 563358507
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358507
/
delete from krew_rte_node_t where rte_node_id = 563358507
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358508 or to_rte_node_id = 563358508
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358508
/
delete from krew_rte_node_t where rte_node_id = 563358508
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358509
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358510 or to_rte_node_id = 563358510
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358510
/
delete from krew_rte_node_t where rte_node_id = 563358510
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358511 or to_rte_node_id = 563358511
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358511
/
delete from krew_rte_node_t where rte_node_id = 563358511
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358512 or to_rte_node_id = 563358512
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358512
/
delete from krew_rte_node_t where rte_node_id = 563358512
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358513
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358514 or to_rte_node_id = 563358514
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358514
/
delete from krew_rte_node_t where rte_node_id = 563358514
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358515 or to_rte_node_id = 563358515
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358515
/
delete from krew_rte_node_t where rte_node_id = 563358515
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358516
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358517 or to_rte_node_id = 563358517
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358517
/
delete from krew_rte_node_t where rte_node_id = 563358517
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358518 or to_rte_node_id = 563358518
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358518
/
delete from krew_rte_node_t where rte_node_id = 563358518
/
delete from krew_doc_typ_t where doc_typ_id = 318681
/
delete from krew_doc_typ_t where doc_typ_id = 318720
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358549 or to_rte_node_id = 563358549
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358549
/
delete from krew_rte_node_t where rte_node_id = 563358549
/
delete from krew_doc_typ_t where doc_typ_id = 318740
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358551 or to_rte_node_id = 563358551
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358551
/
delete from krew_rte_node_t where rte_node_id = 563358551
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358552 or to_rte_node_id = 563358552
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358552
/
delete from krew_rte_node_t where rte_node_id = 563358552
/
delete from krew_doc_typ_t where doc_typ_id = 318741
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358569 or to_rte_node_id = 563358569
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358569
/
delete from krew_rte_node_t where rte_node_id = 563358569
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358570 or to_rte_node_id = 563358570
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358570
/
delete from krew_rte_node_t where rte_node_id = 563358570
/
delete from krew_doc_typ_t where doc_typ_id = 318760
/
delete from krew_doc_typ_t where doc_typ_id = 318781
/
delete from krew_doc_typ_t where doc_typ_id = 318800
/
delete from krew_doc_typ_t where doc_typ_id = 318840
/
delete from krew_doc_typ_t where doc_typ_id = 318860
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358649 or to_rte_node_id = 563358649
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358649
/
delete from krew_rte_node_t where rte_node_id = 563358649
/
delete from krew_doc_typ_t where doc_typ_id = 318880
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358651 or to_rte_node_id = 563358651
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358651
/
delete from krew_rte_node_t where rte_node_id = 563358651
/
delete from krew_doc_typ_t where doc_typ_id = 318881
/
delete from krew_doc_typ_t where doc_typ_id = 318900
/
delete from krew_doc_typ_t where doc_typ_id = 318901
/
delete from krew_doc_typ_t where doc_typ_id = 318940
/
delete from krew_doc_typ_t where doc_typ_id = 318960
/
delete from krew_doc_typ_t where doc_typ_id = 318961
/
delete from krew_doc_typ_t where doc_typ_id = 318962
/
delete from krew_doc_typ_t where doc_typ_id = 318963
/
delete from krew_doc_typ_t where doc_typ_id = 318964
/
delete from krew_doc_typ_t where doc_typ_id = 318965
/
delete from krew_doc_typ_t where doc_typ_id = 318966
/
delete from krew_doc_typ_t where doc_typ_id = 318980
/
delete from krew_doc_typ_t where doc_typ_id = 319000
/
delete from krew_doc_typ_t where doc_typ_id = 319020
/
delete from krew_doc_typ_t where doc_typ_id = 319080
/
delete from krew_doc_typ_t where doc_typ_id = 319100
/
delete from krew_doc_typ_t where doc_typ_id = 319120
/
delete from krew_doc_typ_t where doc_typ_id = 319180
/
delete from krew_doc_typ_t where doc_typ_id = 319200
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358689 or to_rte_node_id = 563358689
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358689
/
delete from krew_rte_node_t where rte_node_id = 563358689
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358690 or to_rte_node_id = 563358690
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358690
/
delete from krew_rte_node_t where rte_node_id = 563358690
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358691 or to_rte_node_id = 563358691
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358691
/
delete from krew_rte_node_t where rte_node_id = 563358691
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358692 or to_rte_node_id = 563358692
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358692
/
delete from krew_rte_node_t where rte_node_id = 563358692
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358693 or to_rte_node_id = 563358693
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358693
/
delete from krew_rte_node_t where rte_node_id = 563358693
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358694 or to_rte_node_id = 563358694
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358694
/
delete from krew_rte_node_t where rte_node_id = 563358694
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358695 or to_rte_node_id = 563358695
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358695
/
delete from krew_rte_node_t where rte_node_id = 563358695
/
delete from krew_doc_typ_t where doc_typ_id = 319220
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358697 or to_rte_node_id = 563358697
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358697
/
delete from krew_rte_node_t where rte_node_id = 563358697
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358698 or to_rte_node_id = 563358698
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358698
/
delete from krew_rte_node_t where rte_node_id = 563358698
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358699 or to_rte_node_id = 563358699
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358699
/
delete from krew_rte_node_t where rte_node_id = 563358699
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358700 or to_rte_node_id = 563358700
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358700
/
delete from krew_rte_node_t where rte_node_id = 563358700
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358701 or to_rte_node_id = 563358701
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358701
/
delete from krew_rte_node_t where rte_node_id = 563358701
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358702 or to_rte_node_id = 563358702
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358702
/
delete from krew_rte_node_t where rte_node_id = 563358702
/
delete from krew_doc_typ_t where doc_typ_id = 319221
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358704 or to_rte_node_id = 563358704
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358704
/
delete from krew_rte_node_t where rte_node_id = 563358704
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358705 or to_rte_node_id = 563358705
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358705
/
delete from krew_rte_node_t where rte_node_id = 563358705
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358706 or to_rte_node_id = 563358706
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358706
/
delete from krew_rte_node_t where rte_node_id = 563358706
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358707 or to_rte_node_id = 563358707
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358707
/
delete from krew_rte_node_t where rte_node_id = 563358707
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358708 or to_rte_node_id = 563358708
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358708
/
delete from krew_rte_node_t where rte_node_id = 563358708
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358709 or to_rte_node_id = 563358709
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358709
/
delete from krew_rte_node_t where rte_node_id = 563358709
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358710 or to_rte_node_id = 563358710
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358710
/
delete from krew_rte_node_t where rte_node_id = 563358710
/
delete from krew_doc_typ_t where doc_typ_id = 319222
/
delete from krew_doc_typ_t where doc_typ_id = 319223
/
delete from krew_doc_typ_t where doc_typ_id = 319260
/
delete from krew_doc_typ_t where doc_typ_id = 319261
/
delete from krew_doc_typ_t where doc_typ_id = 319262
/
delete from krew_doc_typ_t where doc_typ_id = 319263
/
delete from krew_doc_typ_t where doc_typ_id = 319264
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358729 or to_rte_node_id = 563358729
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358729
/
delete from krew_rte_node_t where rte_node_id = 563358729
/
delete from krew_doc_typ_t where doc_typ_id = 319280
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358731 or to_rte_node_id = 563358731
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358731
/
delete from krew_rte_node_t where rte_node_id = 563358731
/
delete from krew_doc_typ_t where doc_typ_id = 319281
/
delete from krew_doc_typ_t where doc_typ_id = 319282
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358733 or to_rte_node_id = 563358733
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358733
/
delete from krew_rte_node_t where rte_node_id = 563358733
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358734 or to_rte_node_id = 563358734
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358734
/
delete from krew_rte_node_t where rte_node_id = 563358734
/
delete from krew_doc_typ_t where doc_typ_id = 319283
/
delete from krew_doc_typ_t where doc_typ_id = 319284
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358736 or to_rte_node_id = 563358736
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358736
/
delete from krew_rte_node_t where rte_node_id = 563358736
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358737 or to_rte_node_id = 563358737
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358737
/
delete from krew_rte_node_t where rte_node_id = 563358737
/
delete from krew_doc_typ_t where doc_typ_id = 319285
/
delete from krew_doc_typ_t where doc_typ_id = 319286
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358739 or to_rte_node_id = 563358739
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358739
/
delete from krew_rte_node_t where rte_node_id = 563358739
/
delete from krew_doc_typ_t where doc_typ_id = 319287
/
delete from krew_doc_typ_t where doc_typ_id = 319288
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358741 or to_rte_node_id = 563358741
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358741
/
delete from krew_rte_node_t where rte_node_id = 563358741
/
delete from krew_doc_typ_t where doc_typ_id = 319289
/
delete from krew_doc_typ_t where doc_typ_id = 319291
/
delete from krew_doc_typ_t where doc_typ_id = 319292
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358743 or to_rte_node_id = 563358743
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358743
/
delete from krew_rte_node_t where rte_node_id = 563358743
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358744 or to_rte_node_id = 563358744
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358744
/
delete from krew_rte_node_t where rte_node_id = 563358744
/
delete from krew_doc_typ_t where doc_typ_id = 319293
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358746 or to_rte_node_id = 563358746
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358746
/
delete from krew_rte_node_t where rte_node_id = 563358746
/
delete from krew_doc_typ_t where doc_typ_id = 319294
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358748 or to_rte_node_id = 563358748
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358748
/
delete from krew_rte_node_t where rte_node_id = 563358748
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358749 or to_rte_node_id = 563358749
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358749
/
delete from krew_rte_node_t where rte_node_id = 563358749
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358750 or to_rte_node_id = 563358750
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358750
/
delete from krew_rte_node_t where rte_node_id = 563358750
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358751 or to_rte_node_id = 563358751
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358751
/
delete from krew_rte_node_t where rte_node_id = 563358751
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358752 or to_rte_node_id = 563358752
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358752
/
delete from krew_rte_node_t where rte_node_id = 563358752
/
delete from krew_doc_typ_t where doc_typ_id = 319295
/
delete from krew_doc_typ_t where doc_typ_id = 319296
/
delete from krew_doc_typ_t where doc_typ_id = 319297
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358754 or to_rte_node_id = 563358754
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358754
/
delete from krew_rte_node_t where rte_node_id = 563358754
/
delete from krew_doc_typ_t where doc_typ_id = 319298
/
delete from krew_doc_typ_t where doc_typ_id = 319299
/
delete from krew_doc_typ_t where doc_typ_id = 319300
/
delete from krew_doc_typ_t where doc_typ_id = 319302
/
delete from krew_doc_typ_t where doc_typ_id = 319303
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358756 or to_rte_node_id = 563358756
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358756
/
delete from krew_rte_node_t where rte_node_id = 563358756
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358757 or to_rte_node_id = 563358757
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358757
/
delete from krew_rte_node_t where rte_node_id = 563358757
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358758 or to_rte_node_id = 563358758
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358758
/
delete from krew_rte_node_t where rte_node_id = 563358758
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358759 or to_rte_node_id = 563358759
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358759
/
delete from krew_rte_node_t where rte_node_id = 563358759
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358760 or to_rte_node_id = 563358760
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358760
/
delete from krew_rte_node_t where rte_node_id = 563358760
/
delete from krew_doc_typ_t where doc_typ_id = 319304
/
delete from krew_doc_typ_t where doc_typ_id = 319305
/
delete from krew_doc_typ_t where doc_typ_id = 319306
/
delete from krew_doc_typ_t where doc_typ_id = 319307
/
delete from krew_doc_typ_t where doc_typ_id = 319308
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358762 or to_rte_node_id = 563358762
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358762
/
delete from krew_rte_node_t where rte_node_id = 563358762
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358763 or to_rte_node_id = 563358763
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358763
/
delete from krew_rte_node_t where rte_node_id = 563358763
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358764 or to_rte_node_id = 563358764
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358764
/
delete from krew_rte_node_t where rte_node_id = 563358764
/
delete from krew_doc_typ_t where doc_typ_id = 319309
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358766 or to_rte_node_id = 563358766
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358766
/
delete from krew_rte_node_t where rte_node_id = 563358766
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358767 or to_rte_node_id = 563358767
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358767
/
delete from krew_rte_node_t where rte_node_id = 563358767
/
delete from krew_doc_typ_t where doc_typ_id = 319310
/
delete from krew_doc_typ_t where doc_typ_id = 319311
/
delete from krew_doc_typ_t where doc_typ_id = 319312
/
delete from krew_doc_typ_t where doc_typ_id = 319313
/
delete from krew_doc_typ_t where doc_typ_id = 319315
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358769 or to_rte_node_id = 563358769
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358769
/
delete from krew_rte_node_t where rte_node_id = 563358769
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358770 or to_rte_node_id = 563358770
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358770
/
delete from krew_rte_node_t where rte_node_id = 563358770
/
delete from krew_doc_typ_t where doc_typ_id = 319316
/
delete from krew_doc_typ_t where doc_typ_id = 319318
/
delete from krew_doc_typ_t where doc_typ_id = 319319
/
delete from krew_doc_typ_t where doc_typ_id = 319320
/
delete from krew_doc_typ_t where doc_typ_id = 319340
/
delete from krew_doc_typ_t where doc_typ_id = 319341
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358789 or to_rte_node_id = 563358789
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358789
/
delete from krew_rte_node_t where rte_node_id = 563358789
/
delete from krew_doc_typ_t where doc_typ_id = 319342
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358791 or to_rte_node_id = 563358791
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358791
/
delete from krew_rte_node_t where rte_node_id = 563358791
/
delete from krew_doc_typ_t where doc_typ_id = 319343
/
delete from krew_doc_typ_t where doc_typ_id = 319344
/
delete from krew_doc_typ_t where doc_typ_id = 319345
/
delete from krew_doc_typ_t where doc_typ_id = 319346
/
delete from krew_doc_typ_t where doc_typ_id = 319347
/
delete from krew_doc_typ_t where doc_typ_id = 319360
/
delete from krew_doc_typ_t where doc_typ_id = 319362
/
delete from krew_doc_typ_t where doc_typ_id = 319363
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358809 or to_rte_node_id = 563358809
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358809
/
delete from krew_rte_node_t where rte_node_id = 563358809
/
delete from krew_doc_typ_t where doc_typ_id = 319364
/
delete from krew_doc_typ_t where doc_typ_id = 319365
/
delete from krew_doc_typ_t where doc_typ_id = 319380
/
delete from krew_doc_typ_t where doc_typ_id = 319400
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358829 or to_rte_node_id = 563358829
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358829
/
delete from krew_rte_node_t where rte_node_id = 563358829
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358830 or to_rte_node_id = 563358830
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358830
/
delete from krew_rte_node_t where rte_node_id = 563358830
/
delete from krew_doc_typ_t where doc_typ_id = 319420
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358832 or to_rte_node_id = 563358832
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358832
/
delete from krew_rte_node_t where rte_node_id = 563358832
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358833 or to_rte_node_id = 563358833
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358833
/
delete from krew_rte_node_t where rte_node_id = 563358833
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358834 or to_rte_node_id = 563358834
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358834
/
delete from krew_rte_node_t where rte_node_id = 563358834
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358835 or to_rte_node_id = 563358835
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358835
/
delete from krew_rte_node_t where rte_node_id = 563358835
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358836 or to_rte_node_id = 563358836
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358836
/
delete from krew_rte_node_t where rte_node_id = 563358836
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358837 or to_rte_node_id = 563358837
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358837
/
delete from krew_rte_node_t where rte_node_id = 563358837
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358838 or to_rte_node_id = 563358838
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358838
/
delete from krew_rte_node_t where rte_node_id = 563358838
/
delete from krew_doc_typ_t where doc_typ_id = 319421
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358840 or to_rte_node_id = 563358840
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358840
/
delete from krew_rte_node_t where rte_node_id = 563358840
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358841 or to_rte_node_id = 563358841
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358841
/
delete from krew_rte_node_t where rte_node_id = 563358841
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358842 or to_rte_node_id = 563358842
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358842
/
delete from krew_rte_node_t where rte_node_id = 563358842
/
delete from krew_doc_typ_t where doc_typ_id = 319422
/
delete from krew_doc_typ_t where doc_typ_id = 319423
/
delete from krew_doc_typ_t where doc_typ_id = 319440
/
delete from krew_doc_typ_t where doc_typ_id = 319441
/
delete from krew_doc_typ_t where doc_typ_id = 319442
/
delete from krew_doc_typ_t where doc_typ_id = 319443
/
delete from krew_doc_typ_t where doc_typ_id = 319444
/
delete from krew_doc_typ_t where doc_typ_id = 319445
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358849 or to_rte_node_id = 563358849
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358849
/
delete from krew_rte_node_t where rte_node_id = 563358849
/
delete from krew_doc_typ_t where doc_typ_id = 319446
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358851 or to_rte_node_id = 563358851
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358851
/
delete from krew_rte_node_t where rte_node_id = 563358851
/
delete from krew_doc_typ_t where doc_typ_id = 319447
/
delete from krew_doc_typ_t where doc_typ_id = 319448
/
delete from krew_doc_typ_t where doc_typ_id = 319449
/
delete from krew_doc_typ_t where doc_typ_id = 319450
/
delete from krew_doc_typ_t where doc_typ_id = 319451
/
delete from krew_doc_typ_t where doc_typ_id = 319453
/
delete from krew_doc_typ_t where doc_typ_id = 319454
/
delete from krew_doc_typ_t where doc_typ_id = 319455
/
delete from krew_doc_typ_t where doc_typ_id = 319456
/
delete from krew_doc_typ_t where doc_typ_id = 319457
/
delete from krew_doc_typ_t where doc_typ_id = 319458
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358853 or to_rte_node_id = 563358853
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358853
/
delete from krew_rte_node_t where rte_node_id = 563358853
/
delete from krew_doc_typ_t where doc_typ_id = 319459
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358855 or to_rte_node_id = 563358855
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358855
/
delete from krew_rte_node_t where rte_node_id = 563358855
/
delete from krew_doc_typ_t where doc_typ_id = 319460
/
delete from krew_doc_typ_t where doc_typ_id = 319461
/
delete from krew_doc_typ_t where doc_typ_id = 319462
/
delete from krew_doc_typ_t where doc_typ_id = 319463
/
delete from krew_doc_typ_t where doc_typ_id = 319464
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358869 or to_rte_node_id = 563358869
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358869
/
delete from krew_rte_node_t where rte_node_id = 563358869
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358870 or to_rte_node_id = 563358870
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358870
/
delete from krew_rte_node_t where rte_node_id = 563358870
/
delete from krew_doc_typ_t where doc_typ_id = 319480
/
delete from krew_doc_typ_t where doc_typ_id = 319481
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358889 or to_rte_node_id = 563358889
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358889
/
delete from krew_rte_node_t where rte_node_id = 563358889
/
delete from krew_doc_typ_t where doc_typ_id = 319500
/
delete from krew_doc_typ_t where doc_typ_id = 319501
/
delete from krew_doc_typ_t where doc_typ_id = 319502
/
delete from krew_doc_typ_t where doc_typ_id = 319503
/
delete from krew_doc_typ_t where doc_typ_id = 319504
/
delete from krew_doc_typ_t where doc_typ_id = 319505
/
delete from krew_doc_typ_t where doc_typ_id = 319506
/
delete from krew_doc_typ_t where doc_typ_id = 319507
/
delete from krew_doc_typ_t where doc_typ_id = 319509
/
delete from krew_doc_typ_t where doc_typ_id = 319510
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358893 or to_rte_node_id = 563358893
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358893
/
delete from krew_rte_node_t where rte_node_id = 563358893
/
delete from krew_doc_typ_t where doc_typ_id = 319511
/
delete from krew_doc_typ_t where doc_typ_id = 319512
/
delete from krew_doc_typ_t where doc_typ_id = 319514
/
delete from krew_doc_typ_t where doc_typ_id = 319515
/
delete from krew_doc_typ_t where doc_typ_id = 319516
/
delete from krew_doc_typ_t where doc_typ_id = 319517
/
delete from krew_doc_typ_t where doc_typ_id = 319518
/
delete from krew_doc_typ_t where doc_typ_id = 319519
/
delete from krew_doc_typ_t where doc_typ_id = 319520
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358895 or to_rte_node_id = 563358895
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358895
/
delete from krew_rte_node_t where rte_node_id = 563358895
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358896 or to_rte_node_id = 563358896
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358896
/
delete from krew_rte_node_t where rte_node_id = 563358896
/
delete from krew_doc_typ_t where doc_typ_id = 319521
/
delete from krew_doc_typ_t where doc_typ_id = 319524
/
delete from krew_doc_typ_t where doc_typ_id = 319525
/
delete from krew_doc_typ_t where doc_typ_id = 319527
/
delete from krew_doc_typ_t where doc_typ_id = 319528
/
delete from krew_doc_typ_t where doc_typ_id = 319529
/
delete from krew_doc_typ_t where doc_typ_id = 319530
/
delete from krew_doc_typ_t where doc_typ_id = 319531
/
delete from krew_doc_typ_t where doc_typ_id = 319532
/
delete from krew_doc_typ_t where doc_typ_id = 319533
/
delete from krew_doc_typ_t where doc_typ_id = 319534
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358898 or to_rte_node_id = 563358898
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358898
/
delete from krew_rte_node_t where rte_node_id = 563358898
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358899 or to_rte_node_id = 563358899
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358899
/
delete from krew_rte_node_t where rte_node_id = 563358899
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358900 or to_rte_node_id = 563358900
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358900
/
delete from krew_rte_node_t where rte_node_id = 563358900
/
delete from krew_doc_typ_t where doc_typ_id = 319535
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358902 or to_rte_node_id = 563358902
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358902
/
delete from krew_rte_node_t where rte_node_id = 563358902
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358903 or to_rte_node_id = 563358903
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358903
/
delete from krew_rte_node_t where rte_node_id = 563358903
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358904 or to_rte_node_id = 563358904
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358904
/
delete from krew_rte_node_t where rte_node_id = 563358904
/
delete from krew_doc_typ_t where doc_typ_id = 319536
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358906 or to_rte_node_id = 563358906
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358906
/
delete from krew_rte_node_t where rte_node_id = 563358906
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358907 or to_rte_node_id = 563358907
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358907
/
delete from krew_rte_node_t where rte_node_id = 563358907
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358908 or to_rte_node_id = 563358908
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358908
/
delete from krew_rte_node_t where rte_node_id = 563358908
/
delete from krew_doc_typ_t where doc_typ_id = 319537
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358910 or to_rte_node_id = 563358910
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358910
/
delete from krew_rte_node_t where rte_node_id = 563358910
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358911 or to_rte_node_id = 563358911
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358911
/
delete from krew_rte_node_t where rte_node_id = 563358911
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358912 or to_rte_node_id = 563358912
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358912
/
delete from krew_rte_node_t where rte_node_id = 563358912
/
delete from krew_doc_typ_t where doc_typ_id = 319538
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358914 or to_rte_node_id = 563358914
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358914
/
delete from krew_rte_node_t where rte_node_id = 563358914
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358915 or to_rte_node_id = 563358915
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358915
/
delete from krew_rte_node_t where rte_node_id = 563358915
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358916 or to_rte_node_id = 563358916
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358916
/
delete from krew_rte_node_t where rte_node_id = 563358916
/
delete from krew_doc_typ_t where doc_typ_id = 319539
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358918 or to_rte_node_id = 563358918
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358918
/
delete from krew_rte_node_t where rte_node_id = 563358918
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358919 or to_rte_node_id = 563358919
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358919
/
delete from krew_rte_node_t where rte_node_id = 563358919
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358920 or to_rte_node_id = 563358920
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358920
/
delete from krew_rte_node_t where rte_node_id = 563358920
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358921 or to_rte_node_id = 563358921
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358921
/
delete from krew_rte_node_t where rte_node_id = 563358921
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358922 or to_rte_node_id = 563358922
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358922
/
delete from krew_rte_node_t where rte_node_id = 563358922
/
delete from krew_doc_typ_t where doc_typ_id = 319540
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358924 or to_rte_node_id = 563358924
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358924
/
delete from krew_rte_node_t where rte_node_id = 563358924
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358925 or to_rte_node_id = 563358925
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358925
/
delete from krew_rte_node_t where rte_node_id = 563358925
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358926
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358927 or to_rte_node_id = 563358927
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358927
/
delete from krew_rte_node_t where rte_node_id = 563358927
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358926
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358928 or to_rte_node_id = 563358928
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358928
/
delete from krew_rte_node_t where rte_node_id = 563358928
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358929 or to_rte_node_id = 563358929
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358929
/
delete from krew_rte_node_t where rte_node_id = 563358929
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358930
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358931 or to_rte_node_id = 563358931
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358931
/
delete from krew_rte_node_t where rte_node_id = 563358931
/
delete from krew_doc_typ_t where doc_typ_id = 319541
/
delete from krew_doc_typ_t where doc_typ_id = 319542
/
delete from krew_doc_typ_t where doc_typ_id = 319543
/
delete from krew_doc_typ_t where doc_typ_id = 319544
/
delete from krew_doc_typ_t where doc_typ_id = 319545
/
delete from krew_doc_typ_t where doc_typ_id = 319546
/
delete from krew_doc_typ_t where doc_typ_id = 319547
/
delete from krew_doc_typ_t where doc_typ_id = 319548
/
delete from krew_doc_typ_t where doc_typ_id = 319549
/
delete from krew_doc_typ_t where doc_typ_id = 319550
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358933 or to_rte_node_id = 563358933
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358933
/
delete from krew_rte_node_t where rte_node_id = 563358933
/
delete from krew_doc_typ_t where doc_typ_id = 319551
/
delete from krew_doc_typ_t where doc_typ_id = 319552
/
delete from krew_doc_typ_t where doc_typ_id = 319553
/
delete from krew_doc_typ_t where doc_typ_id = 319554
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358935 or to_rte_node_id = 563358935
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358935
/
delete from krew_rte_node_t where rte_node_id = 563358935
/
delete from krew_doc_typ_t where doc_typ_id = 319556
/
delete from krew_doc_typ_t where doc_typ_id = 319557
/
delete from krew_doc_typ_t where doc_typ_id = 319559
/
delete from krew_doc_typ_t where doc_typ_id = 319560
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358937 or to_rte_node_id = 563358937
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358937
/
delete from krew_rte_node_t where rte_node_id = 563358937
/
delete from krew_doc_typ_t where doc_typ_id = 319561
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358953 or to_rte_node_id = 563358953
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358953
/
delete from krew_rte_node_t where rte_node_id = 563358953
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358954 or to_rte_node_id = 563358954
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358954
/
delete from krew_rte_node_t where rte_node_id = 563358954
/
delete from krew_doc_typ_t where doc_typ_id = 319564
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358956 or to_rte_node_id = 563358956
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358956
/
delete from krew_rte_node_t where rte_node_id = 563358956
/
delete from krew_doc_typ_t where doc_typ_id = 319565
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358958 or to_rte_node_id = 563358958
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358958
/
delete from krew_rte_node_t where rte_node_id = 563358958
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358959 or to_rte_node_id = 563358959
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358959
/
delete from krew_rte_node_t where rte_node_id = 563358959
/
delete from krew_doc_typ_t where doc_typ_id = 319566
/
delete from krew_doc_typ_t where doc_typ_id = 319570
/
delete from krew_doc_typ_t where doc_typ_id = 319571
/
delete from krew_doc_typ_t where doc_typ_id = 319572
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358964 or to_rte_node_id = 563358964
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358964
/
delete from krew_rte_node_t where rte_node_id = 563358964
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358965 or to_rte_node_id = 563358965
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358965
/
delete from krew_rte_node_t where rte_node_id = 563358965
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358966 or to_rte_node_id = 563358966
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358966
/
delete from krew_rte_node_t where rte_node_id = 563358966
/
delete from krew_doc_typ_t where doc_typ_id = 319573
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358969 or to_rte_node_id = 563358969
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358969
/
delete from krew_rte_node_t where rte_node_id = 563358969
/
delete from krew_doc_typ_t where doc_typ_id = 319580
/
delete from krew_doc_typ_t where doc_typ_id = 319582
/
delete from krew_doc_typ_t where doc_typ_id = 319583
/
delete from krew_doc_typ_t where doc_typ_id = 319584
/
delete from krew_doc_typ_t where doc_typ_id = 319585
/
delete from krew_doc_typ_t where doc_typ_id = 319586
/
delete from krew_doc_typ_t where doc_typ_id = 319587
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358971 or to_rte_node_id = 563358971
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358971
/
delete from krew_rte_node_t where rte_node_id = 563358971
/
delete from krew_doc_typ_t where doc_typ_id = 319588
/
delete from krew_doc_typ_t where doc_typ_id = 319589
/
delete from krew_doc_typ_t where doc_typ_id = 319590
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358973 or to_rte_node_id = 563358973
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358973
/
delete from krew_rte_node_t where rte_node_id = 563358973
/
delete from krew_doc_typ_t where doc_typ_id = 319591
/
delete from krew_doc_typ_t where doc_typ_id = 319593
/
delete from krew_doc_typ_t where doc_typ_id = 319600
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358975 or to_rte_node_id = 563358975
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358975
/
delete from krew_rte_node_t where rte_node_id = 563358975
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358976 or to_rte_node_id = 563358976
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358976
/
delete from krew_rte_node_t where rte_node_id = 563358976
/
delete from krew_doc_typ_t where doc_typ_id = 319601
/
delete from krew_doc_typ_t where doc_typ_id = 319604
/
delete from krew_doc_typ_t where doc_typ_id = 319605
/
delete from krew_doc_typ_t where doc_typ_id = 319607
/
delete from krew_doc_typ_t where doc_typ_id = 319608
/
delete from krew_doc_typ_t where doc_typ_id = 319609
/
delete from krew_doc_typ_t where doc_typ_id = 319610
/
delete from krew_doc_typ_t where doc_typ_id = 319611
/
delete from krew_doc_typ_t where doc_typ_id = 319612
/
delete from krew_doc_typ_t where doc_typ_id = 319614
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358978 or to_rte_node_id = 563358978
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358978
/
delete from krew_rte_node_t where rte_node_id = 563358978
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358979 or to_rte_node_id = 563358979
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358979
/
delete from krew_rte_node_t where rte_node_id = 563358979
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358980 or to_rte_node_id = 563358980
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358980
/
delete from krew_rte_node_t where rte_node_id = 563358980
/
delete from krew_doc_typ_t where doc_typ_id = 319615
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358982 or to_rte_node_id = 563358982
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358982
/
delete from krew_rte_node_t where rte_node_id = 563358982
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358983 or to_rte_node_id = 563358983
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358983
/
delete from krew_rte_node_t where rte_node_id = 563358983
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358984 or to_rte_node_id = 563358984
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358984
/
delete from krew_rte_node_t where rte_node_id = 563358984
/
delete from krew_doc_typ_t where doc_typ_id = 319616
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358986 or to_rte_node_id = 563358986
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358986
/
delete from krew_rte_node_t where rte_node_id = 563358986
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358987 or to_rte_node_id = 563358987
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358987
/
delete from krew_rte_node_t where rte_node_id = 563358987
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358988 or to_rte_node_id = 563358988
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358988
/
delete from krew_rte_node_t where rte_node_id = 563358988
/
delete from krew_doc_typ_t where doc_typ_id = 319617
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358990 or to_rte_node_id = 563358990
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358990
/
delete from krew_rte_node_t where rte_node_id = 563358990
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358991 or to_rte_node_id = 563358991
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358991
/
delete from krew_rte_node_t where rte_node_id = 563358991
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358992 or to_rte_node_id = 563358992
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358992
/
delete from krew_rte_node_t where rte_node_id = 563358992
/
delete from krew_doc_typ_t where doc_typ_id = 319618
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358998 or to_rte_node_id = 563358998
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358998
/
delete from krew_rte_node_t where rte_node_id = 563358998
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358999 or to_rte_node_id = 563358999
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358999
/
delete from krew_rte_node_t where rte_node_id = 563358999
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359000 or to_rte_node_id = 563359000
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359000
/
delete from krew_rte_node_t where rte_node_id = 563359000
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359001 or to_rte_node_id = 563359001
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359001
/
delete from krew_rte_node_t where rte_node_id = 563359001
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359002 or to_rte_node_id = 563359002
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359002
/
delete from krew_rte_node_t where rte_node_id = 563359002
/
delete from krew_doc_typ_t where doc_typ_id = 319620
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359004 or to_rte_node_id = 563359004
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359004
/
delete from krew_rte_node_t where rte_node_id = 563359004
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359005 or to_rte_node_id = 563359005
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359005
/
delete from krew_rte_node_t where rte_node_id = 563359005
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359006
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359007 or to_rte_node_id = 563359007
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359007
/
delete from krew_rte_node_t where rte_node_id = 563359007
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359006
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359008 or to_rte_node_id = 563359008
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359008
/
delete from krew_rte_node_t where rte_node_id = 563359008
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359009 or to_rte_node_id = 563359009
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359009
/
delete from krew_rte_node_t where rte_node_id = 563359009
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359010
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359011 or to_rte_node_id = 563359011
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359011
/
delete from krew_rte_node_t where rte_node_id = 563359011
/
delete from krew_doc_typ_t where doc_typ_id = 319621
/
delete from krew_doc_typ_t where doc_typ_id = 319622
/
delete from krew_doc_typ_t where doc_typ_id = 319623
/
delete from krew_doc_typ_t where doc_typ_id = 319624
/
delete from krew_doc_typ_t where doc_typ_id = 319625
/
delete from krew_doc_typ_t where doc_typ_id = 319626
/
delete from krew_doc_typ_t where doc_typ_id = 319627
/
delete from krew_doc_typ_t where doc_typ_id = 319628
/
delete from krew_doc_typ_t where doc_typ_id = 319629
/
delete from krew_doc_typ_t where doc_typ_id = 319630
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359013 or to_rte_node_id = 563359013
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359013
/
delete from krew_rte_node_t where rte_node_id = 563359013
/
delete from krew_doc_typ_t where doc_typ_id = 319631
/
delete from krew_doc_typ_t where doc_typ_id = 319635
/
delete from krew_doc_typ_t where doc_typ_id = 319637
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359036 or to_rte_node_id = 563359036
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359036
/
delete from krew_rte_node_t where rte_node_id = 563359036
/
delete from krew_doc_typ_t where doc_typ_id = 319645
/
delete from krew_doc_typ_t where doc_typ_id = 319650
/
delete from krew_doc_typ_t where doc_typ_id = 319651
/
delete from krew_doc_typ_t where doc_typ_id = 319652
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359044 or to_rte_node_id = 563359044
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359044
/
delete from krew_rte_node_t where rte_node_id = 563359044
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359045 or to_rte_node_id = 563359045
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359045
/
delete from krew_rte_node_t where rte_node_id = 563359045
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359046 or to_rte_node_id = 563359046
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359046
/
delete from krew_rte_node_t where rte_node_id = 563359046
/
delete from krew_doc_typ_t where doc_typ_id = 319653
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359049 or to_rte_node_id = 563359049
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359049
/
delete from krew_rte_node_t where rte_node_id = 563359049
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359050 or to_rte_node_id = 563359050
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359050
/
delete from krew_rte_node_t where rte_node_id = 563359050
/
delete from krew_doc_typ_t where doc_typ_id = 319660
/
delete from krew_doc_typ_t where doc_typ_id = 319661
/
delete from krew_doc_typ_t where doc_typ_id = 319662
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359069 or to_rte_node_id = 563359069
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359069
/
delete from krew_rte_node_t where rte_node_id = 563359069
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359070 or to_rte_node_id = 563359070
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359070
/
delete from krew_rte_node_t where rte_node_id = 563359070
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359071 or to_rte_node_id = 563359071
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359071
/
delete from krew_rte_node_t where rte_node_id = 563359071
/
delete from krew_doc_typ_t where doc_typ_id = 319680
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359089 or to_rte_node_id = 563359089
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359089
/
delete from krew_rte_node_t where rte_node_id = 563359089
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359090 or to_rte_node_id = 563359090
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359090
/
delete from krew_rte_node_t where rte_node_id = 563359090
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359091 or to_rte_node_id = 563359091
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359091
/
delete from krew_rte_node_t where rte_node_id = 563359091
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359092 or to_rte_node_id = 563359092
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359092
/
delete from krew_rte_node_t where rte_node_id = 563359092
/
delete from krew_doc_typ_t where doc_typ_id = 319700
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359094 or to_rte_node_id = 563359094
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359094
/
delete from krew_rte_node_t where rte_node_id = 563359094
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359095 or to_rte_node_id = 563359095
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359095
/
delete from krew_rte_node_t where rte_node_id = 563359095
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359096 or to_rte_node_id = 563359096
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359096
/
delete from krew_rte_node_t where rte_node_id = 563359096
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359097 or to_rte_node_id = 563359097
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359097
/
delete from krew_rte_node_t where rte_node_id = 563359097
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359098 or to_rte_node_id = 563359098
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359098
/
delete from krew_rte_node_t where rte_node_id = 563359098
/
delete from krew_doc_typ_t where doc_typ_id = 319701
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359109 or to_rte_node_id = 563359109
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359109
/
delete from krew_rte_node_t where rte_node_id = 563359109
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359110 or to_rte_node_id = 563359110
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359110
/
delete from krew_rte_node_t where rte_node_id = 563359110
/
delete from krew_doc_typ_t where doc_typ_id = 319740
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359112 or to_rte_node_id = 563359112
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359112
/
delete from krew_rte_node_t where rte_node_id = 563359112
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359113 or to_rte_node_id = 563359113
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359113
/
delete from krew_rte_node_t where rte_node_id = 563359113
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359114 or to_rte_node_id = 563359114
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359114
/
delete from krew_rte_node_t where rte_node_id = 563359114
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359115 or to_rte_node_id = 563359115
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359115
/
delete from krew_rte_node_t where rte_node_id = 563359115
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359116 or to_rte_node_id = 563359116
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359116
/
delete from krew_rte_node_t where rte_node_id = 563359116
/
delete from krew_doc_typ_t where doc_typ_id = 319741
/
delete from krew_doc_typ_t where doc_typ_id = 319742
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359118 or to_rte_node_id = 563359118
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359118
/
delete from krew_rte_node_t where rte_node_id = 563359118
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359119 or to_rte_node_id = 563359119
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359119
/
delete from krew_rte_node_t where rte_node_id = 563359119
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359120 or to_rte_node_id = 563359120
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359120
/
delete from krew_rte_node_t where rte_node_id = 563359120
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359121 or to_rte_node_id = 563359121
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359121
/
delete from krew_rte_node_t where rte_node_id = 563359121
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359122 or to_rte_node_id = 563359122
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359122
/
delete from krew_rte_node_t where rte_node_id = 563359122
/
delete from krew_doc_typ_t where doc_typ_id = 319743
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359124 or to_rte_node_id = 563359124
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359124
/
delete from krew_rte_node_t where rte_node_id = 563359124
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359125 or to_rte_node_id = 563359125
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359125
/
delete from krew_rte_node_t where rte_node_id = 563359125
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359126 or to_rte_node_id = 563359126
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359126
/
delete from krew_rte_node_t where rte_node_id = 563359126
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359127 or to_rte_node_id = 563359127
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359127
/
delete from krew_rte_node_t where rte_node_id = 563359127
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359128 or to_rte_node_id = 563359128
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359128
/
delete from krew_rte_node_t where rte_node_id = 563359128
/
delete from krew_doc_typ_t where doc_typ_id = 319744
/
delete from krew_doc_typ_t where doc_typ_id = 319745
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359130 or to_rte_node_id = 563359130
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359130
/
delete from krew_rte_node_t where rte_node_id = 563359130
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359131 or to_rte_node_id = 563359131
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359131
/
delete from krew_rte_node_t where rte_node_id = 563359131
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359132
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359133 or to_rte_node_id = 563359133
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359133
/
delete from krew_rte_node_t where rte_node_id = 563359133
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359132
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359134 or to_rte_node_id = 563359134
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359134
/
delete from krew_rte_node_t where rte_node_id = 563359134
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359132
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359135 or to_rte_node_id = 563359135
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359135
/
delete from krew_rte_node_t where rte_node_id = 563359135
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359132
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359136 or to_rte_node_id = 563359136
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359136
/
delete from krew_rte_node_t where rte_node_id = 563359136
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359137 or to_rte_node_id = 563359137
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359137
/
delete from krew_rte_node_t where rte_node_id = 563359137
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359138
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359139 or to_rte_node_id = 563359139
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359139
/
delete from krew_rte_node_t where rte_node_id = 563359139
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359140 or to_rte_node_id = 563359140
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359140
/
delete from krew_rte_node_t where rte_node_id = 563359140
/
delete from krew_doc_typ_t where doc_typ_id = 319746
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359142 or to_rte_node_id = 563359142
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359142
/
delete from krew_rte_node_t where rte_node_id = 563359142
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359143 or to_rte_node_id = 563359143
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359143
/
delete from krew_rte_node_t where rte_node_id = 563359143
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359144 or to_rte_node_id = 563359144
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359144
/
delete from krew_rte_node_t where rte_node_id = 563359144
/
delete from krew_doc_typ_t where doc_typ_id = 319747
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359146 or to_rte_node_id = 563359146
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359146
/
delete from krew_rte_node_t where rte_node_id = 563359146
/
delete from krew_doc_typ_t where doc_typ_id = 319748
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359148 or to_rte_node_id = 563359148
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359148
/
delete from krew_rte_node_t where rte_node_id = 563359148
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359149 or to_rte_node_id = 563359149
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359149
/
delete from krew_rte_node_t where rte_node_id = 563359149
/
delete from krew_doc_typ_t where doc_typ_id = 319749
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359151 or to_rte_node_id = 563359151
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359151
/
delete from krew_rte_node_t where rte_node_id = 563359151
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359152 or to_rte_node_id = 563359152
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359152
/
delete from krew_rte_node_t where rte_node_id = 563359152
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359153 or to_rte_node_id = 563359153
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359153
/
delete from krew_rte_node_t where rte_node_id = 563359153
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359154 or to_rte_node_id = 563359154
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359154
/
delete from krew_rte_node_t where rte_node_id = 563359154
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359155 or to_rte_node_id = 563359155
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359155
/
delete from krew_rte_node_t where rte_node_id = 563359155
/
delete from krew_doc_typ_t where doc_typ_id = 319750
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359157 or to_rte_node_id = 563359157
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359157
/
delete from krew_rte_node_t where rte_node_id = 563359157
/
delete from krew_doc_typ_t where doc_typ_id = 319751
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359159 or to_rte_node_id = 563359159
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359159
/
delete from krew_rte_node_t where rte_node_id = 563359159
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359160 or to_rte_node_id = 563359160
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359160
/
delete from krew_rte_node_t where rte_node_id = 563359160
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359161 or to_rte_node_id = 563359161
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359161
/
delete from krew_rte_node_t where rte_node_id = 563359161
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359162 or to_rte_node_id = 563359162
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359162
/
delete from krew_rte_node_t where rte_node_id = 563359162
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359163 or to_rte_node_id = 563359163
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359163
/
delete from krew_rte_node_t where rte_node_id = 563359163
/
delete from krew_doc_typ_t where doc_typ_id = 319752
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359165 or to_rte_node_id = 563359165
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359165
/
delete from krew_rte_node_t where rte_node_id = 563359165
/
delete from krew_doc_typ_t where doc_typ_id = 319753
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359167 or to_rte_node_id = 563359167
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359167
/
delete from krew_rte_node_t where rte_node_id = 563359167
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359168 or to_rte_node_id = 563359168
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359168
/
delete from krew_rte_node_t where rte_node_id = 563359168
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359169 or to_rte_node_id = 563359169
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359169
/
delete from krew_rte_node_t where rte_node_id = 563359169
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359170 or to_rte_node_id = 563359170
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359170
/
delete from krew_rte_node_t where rte_node_id = 563359170
/
delete from krew_doc_typ_t where doc_typ_id = 319754
/
delete from krew_doc_typ_t where doc_typ_id = 319755
/
delete from krew_doc_typ_t where doc_typ_id = 319756
/
delete from krew_doc_typ_t where doc_typ_id = 319757
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359189 or to_rte_node_id = 563359189
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359189
/
delete from krew_rte_node_t where rte_node_id = 563359189
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359191 or to_rte_node_id = 563359191
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359191
/
delete from krew_rte_node_t where rte_node_id = 563359191
/
delete from krew_doc_typ_t where doc_typ_id = 319760
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359193 or to_rte_node_id = 563359193
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359193
/
delete from krew_rte_node_t where rte_node_id = 563359193
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359194 or to_rte_node_id = 563359194
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359194
/
delete from krew_rte_node_t where rte_node_id = 563359194
/
delete from krew_doc_typ_t where doc_typ_id = 319762
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359196 or to_rte_node_id = 563359196
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359196
/
delete from krew_rte_node_t where rte_node_id = 563359196
/
delete from krew_doc_typ_t where doc_typ_id = 319763
/
delete from krew_doc_typ_t where doc_typ_id = 319765
/
delete from krew_doc_typ_t where doc_typ_id = 319766
/
delete from krew_doc_typ_t where doc_typ_id = 319767
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359206 or to_rte_node_id = 563359206
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359206
/
delete from krew_rte_node_t where rte_node_id = 563359206
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359207 or to_rte_node_id = 563359207
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359207
/
delete from krew_rte_node_t where rte_node_id = 563359207
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359208 or to_rte_node_id = 563359208
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359208
/
delete from krew_rte_node_t where rte_node_id = 563359208
/
delete from krew_doc_typ_t where doc_typ_id = 319769
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359210 or to_rte_node_id = 563359210
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359210
/
delete from krew_rte_node_t where rte_node_id = 563359210
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359211 or to_rte_node_id = 563359211
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359211
/
delete from krew_rte_node_t where rte_node_id = 563359211
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359212 or to_rte_node_id = 563359212
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359212
/
delete from krew_rte_node_t where rte_node_id = 563359212
/
delete from krew_doc_typ_t where doc_typ_id = 319770
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359214 or to_rte_node_id = 563359214
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359214
/
delete from krew_rte_node_t where rte_node_id = 563359214
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359215 or to_rte_node_id = 563359215
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359215
/
delete from krew_rte_node_t where rte_node_id = 563359215
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359216 or to_rte_node_id = 563359216
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359216
/
delete from krew_rte_node_t where rte_node_id = 563359216
/
delete from krew_doc_typ_t where doc_typ_id = 319771
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359218 or to_rte_node_id = 563359218
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359218
/
delete from krew_rte_node_t where rte_node_id = 563359218
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359219 or to_rte_node_id = 563359219
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359219
/
delete from krew_rte_node_t where rte_node_id = 563359219
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359220 or to_rte_node_id = 563359220
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359220
/
delete from krew_rte_node_t where rte_node_id = 563359220
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359221 or to_rte_node_id = 563359221
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359221
/
delete from krew_rte_node_t where rte_node_id = 563359221
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359222
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359223 or to_rte_node_id = 563359223
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359223
/
delete from krew_rte_node_t where rte_node_id = 563359223
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359224 or to_rte_node_id = 563359224
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359224
/
delete from krew_rte_node_t where rte_node_id = 563359224
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359225 or to_rte_node_id = 563359225
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359225
/
delete from krew_rte_node_t where rte_node_id = 563359225
/
delete from krew_doc_typ_t where doc_typ_id = 319772
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359227 or to_rte_node_id = 563359227
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359227
/
delete from krew_rte_node_t where rte_node_id = 563359227
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359228 or to_rte_node_id = 563359228
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359228
/
delete from krew_rte_node_t where rte_node_id = 563359228
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359229 or to_rte_node_id = 563359229
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359229
/
delete from krew_rte_node_t where rte_node_id = 563359229
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359230 or to_rte_node_id = 563359230
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359230
/
delete from krew_rte_node_t where rte_node_id = 563359230
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359231 or to_rte_node_id = 563359231
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359231
/
delete from krew_rte_node_t where rte_node_id = 563359231
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359232 or to_rte_node_id = 563359232
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359232
/
delete from krew_rte_node_t where rte_node_id = 563359232
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359233 or to_rte_node_id = 563359233
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359233
/
delete from krew_rte_node_t where rte_node_id = 563359233
/
delete from krew_doc_typ_t where doc_typ_id = 319773
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359235 or to_rte_node_id = 563359235
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359235
/
delete from krew_rte_node_t where rte_node_id = 563359235
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359236 or to_rte_node_id = 563359236
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359236
/
delete from krew_rte_node_t where rte_node_id = 563359236
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359237 or to_rte_node_id = 563359237
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359237
/
delete from krew_rte_node_t where rte_node_id = 563359237
/
delete from krew_doc_typ_t where doc_typ_id = 319774
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359239 or to_rte_node_id = 563359239
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359239
/
delete from krew_rte_node_t where rte_node_id = 563359239
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359240 or to_rte_node_id = 563359240
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359240
/
delete from krew_rte_node_t where rte_node_id = 563359240
/
delete from krew_doc_typ_t where doc_typ_id = 319775
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359242 or to_rte_node_id = 563359242
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359242
/
delete from krew_rte_node_t where rte_node_id = 563359242
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359243 or to_rte_node_id = 563359243
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359243
/
delete from krew_rte_node_t where rte_node_id = 563359243
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359244 or to_rte_node_id = 563359244
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359244
/
delete from krew_rte_node_t where rte_node_id = 563359244
/
delete from krew_doc_typ_t where doc_typ_id = 319776
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359246 or to_rte_node_id = 563359246
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359246
/
delete from krew_rte_node_t where rte_node_id = 563359246
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359247 or to_rte_node_id = 563359247
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359247
/
delete from krew_rte_node_t where rte_node_id = 563359247
/
delete from krew_doc_typ_t where doc_typ_id = 319777
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359259 or to_rte_node_id = 563359259
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359259
/
delete from krew_rte_node_t where rte_node_id = 563359259
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359260 or to_rte_node_id = 563359260
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359260
/
delete from krew_rte_node_t where rte_node_id = 563359260
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359261 or to_rte_node_id = 563359261
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359261
/
delete from krew_rte_node_t where rte_node_id = 563359261
/
delete from krew_doc_typ_t where doc_typ_id = 319780
/
delete from krew_doc_typ_t where doc_typ_id = 319782
/
delete from krew_doc_typ_t where doc_typ_id = 319784
/
delete from krew_doc_typ_t where doc_typ_id = 319785
/
delete from krew_doc_typ_t where doc_typ_id = 319787
/
delete from krew_doc_typ_t where doc_typ_id = 319788
/
delete from krew_doc_typ_t where doc_typ_id = 319790
/
delete from krew_doc_typ_t where doc_typ_id = 319791
/
delete from krew_doc_typ_t where doc_typ_id = 319794
/
delete from krew_doc_typ_t where doc_typ_id = 319795
/
delete from krew_doc_typ_t where doc_typ_id = 319798
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359263 or to_rte_node_id = 563359263
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359263
/
delete from krew_rte_node_t where rte_node_id = 563359263
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359264 or to_rte_node_id = 563359264
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359264
/
delete from krew_rte_node_t where rte_node_id = 563359264
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359265 or to_rte_node_id = 563359265
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359265
/
delete from krew_rte_node_t where rte_node_id = 563359265
/
delete from krew_doc_typ_t where doc_typ_id = 319800
/
delete from krew_doc_typ_t where doc_typ_id = 319802
/
delete from krew_doc_typ_t where doc_typ_id = 319803
/
delete from krew_doc_typ_t where doc_typ_id = 319805
/
delete from krew_doc_typ_t where doc_typ_id = 319806
/
delete from krew_doc_typ_t where doc_typ_id = 319807
/
delete from krew_doc_typ_t where doc_typ_id = 319808
/
delete from krew_doc_typ_t where doc_typ_id = 319809
/
delete from krew_doc_typ_t where doc_typ_id = 319810
/
delete from krew_doc_typ_t where doc_typ_id = 319812
/
delete from krew_doc_typ_t where doc_typ_id = 319815
/
delete from krew_doc_typ_t where doc_typ_id = 319818
/
delete from krew_doc_typ_t where doc_typ_id = 319819
/
delete from krew_doc_typ_t where doc_typ_id = 319821
/
delete from krew_doc_typ_t where doc_typ_id = 319823
/
delete from krew_doc_typ_t where doc_typ_id = 319825
/
delete from krew_doc_typ_t where doc_typ_id = 319826
/
delete from krew_doc_typ_t where doc_typ_id = 319827
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359278 or to_rte_node_id = 563359278
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359278
/
delete from krew_rte_node_t where rte_node_id = 563359278
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359279 or to_rte_node_id = 563359279
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359279
/
delete from krew_rte_node_t where rte_node_id = 563359279
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359280
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359281 or to_rte_node_id = 563359281
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359281
/
delete from krew_rte_node_t where rte_node_id = 563359281
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359280
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359282 or to_rte_node_id = 563359282
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359282
/
delete from krew_rte_node_t where rte_node_id = 563359282
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359280
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359283 or to_rte_node_id = 563359283
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359283
/
delete from krew_rte_node_t where rte_node_id = 563359283
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359280
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359284 or to_rte_node_id = 563359284
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359284
/
delete from krew_rte_node_t where rte_node_id = 563359284
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359285 or to_rte_node_id = 563359285
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359285
/
delete from krew_rte_node_t where rte_node_id = 563359285
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359286
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359287 or to_rte_node_id = 563359287
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359287
/
delete from krew_rte_node_t where rte_node_id = 563359287
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359288 or to_rte_node_id = 563359288
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359288
/
delete from krew_rte_node_t where rte_node_id = 563359288
/
delete from krew_doc_typ_t where doc_typ_id = 319831
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359298 or to_rte_node_id = 563359298
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359298
/
delete from krew_rte_node_t where rte_node_id = 563359298
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359299 or to_rte_node_id = 563359299
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359299
/
delete from krew_rte_node_t where rte_node_id = 563359299
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359300
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359301 or to_rte_node_id = 563359301
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359301
/
delete from krew_rte_node_t where rte_node_id = 563359301
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359302 or to_rte_node_id = 563359302
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359302
/
delete from krew_rte_node_t where rte_node_id = 563359302
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359303
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359304 or to_rte_node_id = 563359304
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359304
/
delete from krew_rte_node_t where rte_node_id = 563359304
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359305 or to_rte_node_id = 563359305
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359305
/
delete from krew_rte_node_t where rte_node_id = 563359305
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359306 or to_rte_node_id = 563359306
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359306
/
delete from krew_rte_node_t where rte_node_id = 563359306
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359307 or to_rte_node_id = 563359307
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359307
/
delete from krew_rte_node_t where rte_node_id = 563359307
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359308
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359309 or to_rte_node_id = 563359309
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359309
/
delete from krew_rte_node_t where rte_node_id = 563359309
/
delete from krew_doc_typ_t where doc_typ_id = 319835
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359361 or to_rte_node_id = 563359361
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359361
/
delete from krew_rte_node_t where rte_node_id = 563359361
/
delete from krew_doc_typ_t where doc_typ_id = 319851
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359369 or to_rte_node_id = 563359369
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359369
/
delete from krew_rte_node_t where rte_node_id = 563359369
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359370 or to_rte_node_id = 563359370
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359370
/
delete from krew_rte_node_t where rte_node_id = 563359370
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359371
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359372 or to_rte_node_id = 563359372
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359372
/
delete from krew_rte_node_t where rte_node_id = 563359372
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359373 or to_rte_node_id = 563359373
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359373
/
delete from krew_rte_node_t where rte_node_id = 563359373
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359374
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359375 or to_rte_node_id = 563359375
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359375
/
delete from krew_rte_node_t where rte_node_id = 563359375
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359376 or to_rte_node_id = 563359376
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359376
/
delete from krew_rte_node_t where rte_node_id = 563359376
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359377 or to_rte_node_id = 563359377
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359377
/
delete from krew_rte_node_t where rte_node_id = 563359377
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359378 or to_rte_node_id = 563359378
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359378
/
delete from krew_rte_node_t where rte_node_id = 563359378
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359379
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359380 or to_rte_node_id = 563359380
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359380
/
delete from krew_rte_node_t where rte_node_id = 563359380
/
delete from krew_doc_typ_t where doc_typ_id = 319860
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359389 or to_rte_node_id = 563359389
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359389
/
delete from krew_rte_node_t where rte_node_id = 563359389
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359390 or to_rte_node_id = 563359390
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359390
/
delete from krew_rte_node_t where rte_node_id = 563359390
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359391 or to_rte_node_id = 563359391
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359391
/
delete from krew_rte_node_t where rte_node_id = 563359391
/
delete from krew_doc_typ_t where doc_typ_id = 319880
/
delete from krew_doc_typ_t where doc_typ_id = 319941
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359486 or to_rte_node_id = 563359486
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359486
/
delete from krew_rte_node_t where rte_node_id = 563359486
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359487 or to_rte_node_id = 563359487
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359487
/
delete from krew_rte_node_t where rte_node_id = 563359487
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359488 or to_rte_node_id = 563359488
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359488
/
delete from krew_rte_node_t where rte_node_id = 563359488
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359489 or to_rte_node_id = 563359489
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359489
/
delete from krew_rte_node_t where rte_node_id = 563359489
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359490 or to_rte_node_id = 563359490
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359490
/
delete from krew_rte_node_t where rte_node_id = 563359490
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359491 or to_rte_node_id = 563359491
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359491
/
delete from krew_rte_node_t where rte_node_id = 563359491
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359492 or to_rte_node_id = 563359492
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359492
/
delete from krew_rte_node_t where rte_node_id = 563359492
/
delete from krew_doc_typ_t where doc_typ_id = 319950
/
delete from krew_doc_typ_t where doc_typ_id = 319968
/
delete from krew_doc_typ_t where doc_typ_id = 319783
/
delete from krew_doc_typ_t where doc_typ_id = 319781
/
delete from krew_doc_typ_t where doc_typ_id = 319786
/
delete from krew_doc_typ_t where doc_typ_id = 319789
/
delete from krew_doc_typ_t where doc_typ_id = 319792
/
delete from krew_doc_typ_t where doc_typ_id = 319793
/
delete from krew_doc_typ_t where doc_typ_id = 319796
/
delete from krew_doc_typ_t where doc_typ_id = 319799
/
delete from krew_doc_typ_t where doc_typ_id = 319801
/
delete from krew_doc_typ_t where doc_typ_id = 319804
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359249 or to_rte_node_id = 563359249
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359249
/
delete from krew_rte_node_t where rte_node_id = 563359249
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359250 or to_rte_node_id = 563359250
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359250
/
delete from krew_rte_node_t where rte_node_id = 563359250
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359251 or to_rte_node_id = 563359251
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359251
/
delete from krew_rte_node_t where rte_node_id = 563359251
/
delete from krew_doc_typ_t where doc_typ_id = 319778
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359253 or to_rte_node_id = 563359253
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359253
/
delete from krew_rte_node_t where rte_node_id = 563359253
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359254 or to_rte_node_id = 563359254
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359254
/
delete from krew_rte_node_t where rte_node_id = 563359254
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359255 or to_rte_node_id = 563359255
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359255
/
delete from krew_rte_node_t where rte_node_id = 563359255
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359256 or to_rte_node_id = 563359256
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359256
/
delete from krew_rte_node_t where rte_node_id = 563359256
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359257 or to_rte_node_id = 563359257
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359257
/
delete from krew_rte_node_t where rte_node_id = 563359257
/
delete from krew_doc_typ_t where doc_typ_id = 319779
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358994 or to_rte_node_id = 563358994
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358994
/
delete from krew_rte_node_t where rte_node_id = 563358994
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358995 or to_rte_node_id = 563358995
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358995
/
delete from krew_rte_node_t where rte_node_id = 563358995
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358996 or to_rte_node_id = 563358996
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358996
/
delete from krew_rte_node_t where rte_node_id = 563358996
/
delete from krew_doc_typ_t where doc_typ_id = 319619
/
delete from krew_doc_typ_t where doc_typ_id = 319813
/
delete from krew_doc_typ_t where doc_typ_id = 319814
/
delete from krew_doc_typ_t where doc_typ_id = 319816
/
delete from krew_doc_typ_t where doc_typ_id = 319817
/
delete from krew_doc_typ_t where doc_typ_id = 319797
/
delete from krew_doc_typ_t where doc_typ_id = 319822
/
delete from krew_doc_typ_t where doc_typ_id = 319824
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359033 or to_rte_node_id = 563359033
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359033
/
delete from krew_rte_node_t where rte_node_id = 563359033
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359034 or to_rte_node_id = 563359034
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359034
/
delete from krew_rte_node_t where rte_node_id = 563359034
/
delete from krew_doc_typ_t where doc_typ_id = 319644
/
delete from krew_doc_typ_t where doc_typ_id = 319811
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358939 or to_rte_node_id = 563358939
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358939
/
delete from krew_rte_node_t where rte_node_id = 563358939
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358940 or to_rte_node_id = 563358940
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358940
/
delete from krew_rte_node_t where rte_node_id = 563358940
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358941 or to_rte_node_id = 563358941
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358941
/
delete from krew_rte_node_t where rte_node_id = 563358941
/
delete from krew_doc_typ_t where doc_typ_id = 319562
/
delete from krew_doc_typ_t where doc_typ_id = 319555
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358943 or to_rte_node_id = 563358943
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358943
/
delete from krew_rte_node_t where rte_node_id = 563358943
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358944 or to_rte_node_id = 563358944
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358944
/
delete from krew_rte_node_t where rte_node_id = 563358944
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358945 or to_rte_node_id = 563358945
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358945
/
delete from krew_rte_node_t where rte_node_id = 563358945
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358946
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358947 or to_rte_node_id = 563358947
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358947
/
delete from krew_rte_node_t where rte_node_id = 563358947
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358948 or to_rte_node_id = 563358948
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358948
/
delete from krew_rte_node_t where rte_node_id = 563358948
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563358949
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358950 or to_rte_node_id = 563358950
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358950
/
delete from krew_rte_node_t where rte_node_id = 563358950
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563358951 or to_rte_node_id = 563358951
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563358951
/
delete from krew_rte_node_t where rte_node_id = 563358951
/
delete from krew_doc_typ_t where doc_typ_id = 319563
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359597 or to_rte_node_id = 563359597
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359597
/
delete from krew_rte_node_t where rte_node_id = 563359597
/
delete from krew_doc_typ_t where doc_typ_id = 320083
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359038 or to_rte_node_id = 563359038
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359038
/
delete from krew_rte_node_t where rte_node_id = 563359038
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359039 or to_rte_node_id = 563359039
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359039
/
delete from krew_rte_node_t where rte_node_id = 563359039
/
delete from krew_doc_typ_t where doc_typ_id = 319646
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359549 or to_rte_node_id = 563359549
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359549
/
delete from krew_rte_node_t where rte_node_id = 563359549
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359550 or to_rte_node_id = 563359550
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359550
/
delete from krew_rte_node_t where rte_node_id = 563359550
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359551
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359552 or to_rte_node_id = 563359552
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359552
/
delete from krew_rte_node_t where rte_node_id = 563359552
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359553 or to_rte_node_id = 563359553
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359553
/
delete from krew_rte_node_t where rte_node_id = 563359553
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563359554
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563359555 or to_rte_node_id = 563359555
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563359555
/
delete from krew_rte_node_t where rte_node_id = 563359555
/
delete from krew_doc_typ_t where doc_typ_id = 320060
/
delete from krew_doc_typ_t where doc_typ_id = 293354
/
delete from krew_doc_typ_t where doc_typ_id = 293358
/
delete from krew_doc_typ_t where doc_typ_id = 319558
/
delete from krew_doc_typ_t where doc_typ_id = 319613
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563312527 or to_rte_node_id = 563312527
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563312527
/
delete from krew_rte_node_t where rte_node_id = 563312527
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563312528
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563312529 or to_rte_node_id = 563312529
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563312529
/
delete from krew_rte_node_t where rte_node_id = 563312529
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563312528
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563312530 or to_rte_node_id = 563312530
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563312530
/
delete from krew_rte_node_t where rte_node_id = 563312530
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563312531 or to_rte_node_id = 563312531
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563312531
/
delete from krew_rte_node_t where rte_node_id = 563312531
/
delete from krew_rte_brch_proto_t where rte_brch_proto_id = 563312532
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563312533 or to_rte_node_id = 563312533
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563312533
/
delete from krew_rte_node_t where rte_node_id = 563312533
/
delete from krew_rte_node_lnk_t where from_rte_node_id = 563312526 or to_rte_node_id = 563312526
/
delete from krew_rte_node_cfg_parm_t where rte_node_id = 563312526
/
delete from krew_rte_node_t where rte_node_id = 563312526
/
delete from krew_doc_typ_t where doc_typ_id = 294349
/
