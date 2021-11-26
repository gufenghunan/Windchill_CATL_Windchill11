set echo on
REM Creating table ObjectLinkedByRdm for com.catl.integration.rdm.ObjectLinkedByRdm
set echo off
CREATE TABLE ObjectLinkedByRdm (
   branchId   NUMBER,
   deliverableId   VARCHAR2(600),
   objectNumber   VARCHAR2(600),
   objectType   VARCHAR2(600),
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
 CONSTRAINT PK_ObjectLinkedByRdm PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE ObjectLinkedByRdm IS 'Table ObjectLinkedByRdm created for com.catl.integration.rdm.ObjectLinkedByRdm'
/
REM @//com/catl/integration/rdm/ObjectLinkedByRdm_UserAdditions
