set echo on
REM Creating table ECAPartLink for com.catl.change.inventory.ECAPartLink
set echo off
CREATE TABLE ECAPartLink (
   dispositionOption   VARCHAR2(600),
   dueDay   DATE,
   materialStatus   VARCHAR2(600),
   owner   VARCHAR2(600),
   quantity   NUMBER,
   remarks   VARCHAR2(600),
   classnamekeyroleAObjectRef   VARCHAR2(600),
   idA3A5   NUMBER,
   classnamekeyroleBObjectRef   VARCHAR2(600),
   idA3B5   NUMBER,
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
 CONSTRAINT PK_ECAPartLink PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE ECAPartLink IS 'Table ECAPartLink created for com.catl.change.inventory.ECAPartLink'
/
REM @//com/catl/change/inventory/ECAPartLink_UserAdditions
