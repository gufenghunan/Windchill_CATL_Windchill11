package com.catl.doc.maturityUpReport;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _NFAEMaturityUp3DocPartLink extends wt.fc.ObjectToObjectLink implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final java.lang.String RESOURCE = "com.catl.doc.maturityUpReport.maturityUpReportResource";
   static final java.lang.String CLASSNAME = NFAEMaturityUp3DocPartLink.class.getName();

   /**
    * @see com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink
    */
   public static final java.lang.String INITIAL_PART = "initialPart";
   wt.fc.ObjectReference initialPart;
   /**
    * @see com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink
    */
   public wt.fc.ObjectReference getInitialPart() {
      return initialPart;
   }
   /**
    * @see com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink
    */
   public void setInitialPart(wt.fc.ObjectReference initialPart) throws wt.util.WTPropertyVetoException {
      initialPartValidate(initialPart);
      this.initialPart = initialPart;
   }
   void initialPartValidate(wt.fc.ObjectReference initialPart) throws wt.util.WTPropertyVetoException {
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink
    */
   public static final java.lang.String DOC_MASTER_ROLE = "docMaster";
   /**
    * <b>Supported API: </b>true
    *
    * @see com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink
    */
   public wt.doc.WTDocumentMaster getDocMaster() {
      return (wt.doc.WTDocumentMaster) getRoleAObject();
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink
    */
   public void setDocMaster(wt.doc.WTDocumentMaster the_docMaster) throws wt.util.WTPropertyVetoException {
      setRoleAObject((wt.fc.Persistable) the_docMaster);
   }

   /**
    * <b>Supported API: </b>true
    *
    * @see com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink
    */
   public static final java.lang.String PART_MASTER_ROLE = "partMaster";
   /**
    * <b>Supported API: </b>true
    *
    * @see com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink
    */
   public wt.part.WTPartMaster getPartMaster() {
      return (wt.part.WTPartMaster) getRoleBObject();
   }
   /**
    * <b>Supported API: </b>true
    *
    * @see com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink
    */
   public void setPartMaster(wt.part.WTPartMaster the_partMaster) throws wt.util.WTPropertyVetoException {
      setRoleBObject((wt.fc.Persistable) the_partMaster);
   }

   public java.lang.String getConceptualClassname() {
      return CLASSNAME;
   }

   public wt.introspection.ClassInfo getClassInfo() throws wt.introspection.WTIntrospectionException {
      return wt.introspection.WTIntrospector.getClassInfo(getConceptualClassname());
   }

   public java.lang.String getType() {
      try { return getClassInfo().getDisplayName(); }
      catch (wt.introspection.WTIntrospectionException wte) { return wt.util.WTStringUtilities.tail(getConceptualClassname(), '.'); }
   }

   public static final long EXTERNALIZATION_VERSION_UID = -3152797497084041739L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( initialPart );
   }

   protected void super_writeExternal_NFAEMaturityUp3DocPartLink(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_NFAEMaturityUp3DocPartLink(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.writeObject( "initialPart", initialPart, wt.fc.ObjectReference.class, true );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      initialPart = (wt.fc.ObjectReference) input.readObject( "initialPart", initialPart, wt.fc.ObjectReference.class, true );
   }

   boolean readVersion_3152797497084041739L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      initialPart = (wt.fc.ObjectReference) input.readObject();
      return true;
   }

   protected boolean readVersion( NFAEMaturityUp3DocPartLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion_3152797497084041739L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_NFAEMaturityUp3DocPartLink( _NFAEMaturityUp3DocPartLink thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
