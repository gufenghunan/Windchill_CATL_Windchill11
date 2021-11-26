set echo on
REM Creating table ClassificationNodeConfig for com.catl.part.classification.ClassificationNodeConfig
set echo off
/

CREATE TABLE ClassificationNodeConfig (
   attributeRef   VARCHAR2(600),
   buyNeedFae   NUMBER(1),
   customerNeedFae   NUMBER(1),
   makeBuyNeedFae   NUMBER(1),
   makeNeedFae   NUMBER(1),
   needFae   NUMBER(1),
   needNonFaeReport   NUMBER(1),
   nodeId   NUMBER,
   nodeInternalName   VARCHAR2(750),
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
   virtualNeedFae   NUMBER(1),
 CONSTRAINT PK_ClassificationNodeCon_NEW PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE ClassificationNodeConfig IS 'Table ClassificationNodeConfig created for com.catl.part.classification.ClassificationNodeConfig'
/
REM @//com/catl/part/classification/ClassificationNodeConfig_UserAdditions
