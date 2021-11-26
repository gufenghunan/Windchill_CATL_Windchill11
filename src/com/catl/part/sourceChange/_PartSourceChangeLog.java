package com.catl.part.sourceChange;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _PartSourceChangeLog extends wt.fc.WTObject implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final java.lang.String RESOURCE = "com.catl.part.sourceChange.sourceChangeResource";
   static final java.lang.String CLASSNAME = PartSourceChangeLog.class.getName();

   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public static final java.lang.String VERSION = "version";
   static int VERSION_UPPER_LIMIT = -1;
   java.lang.String version;
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public java.lang.String getVersion() {
      return version;
   }
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public void setVersion(java.lang.String version) throws wt.util.WTPropertyVetoException {
      versionValidate(version);
      this.version = version;
   }
   void versionValidate(java.lang.String version) throws wt.util.WTPropertyVetoException {
      if (VERSION_UPPER_LIMIT < 1) {
         try { VERSION_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("version").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { VERSION_UPPER_LIMIT = 250; }
      }
      if (version != null && !wt.fc.PersistenceHelper.checkStoredLength(version.toString(), VERSION_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "version"), java.lang.String.valueOf(java.lang.Math.min(VERSION_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "version", this.version, version));
   }

   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public static final java.lang.String OPERATOR = "operator";
   wt.org.WTPrincipalReference operator;
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public wt.org.WTPrincipalReference getOperator() {
      return operator;
   }
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public void setOperator(wt.org.WTPrincipalReference operator) throws wt.util.WTPropertyVetoException {
      operatorValidate(operator);
      this.operator = operator;
   }
   void operatorValidate(wt.org.WTPrincipalReference operator) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public static final java.lang.String OLD_SOURCE = "oldSource";
   static int OLD_SOURCE_UPPER_LIMIT = -1;
   java.lang.String oldSource;
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public java.lang.String getOldSource() {
      return oldSource;
   }
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public void setOldSource(java.lang.String oldSource) throws wt.util.WTPropertyVetoException {
      oldSourceValidate(oldSource);
      this.oldSource = oldSource;
   }
   void oldSourceValidate(java.lang.String oldSource) throws wt.util.WTPropertyVetoException {
      if (OLD_SOURCE_UPPER_LIMIT < 1) {
         try { OLD_SOURCE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("oldSource").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OLD_SOURCE_UPPER_LIMIT = 250; }
      }
      if (oldSource != null && !wt.fc.PersistenceHelper.checkStoredLength(oldSource.toString(), OLD_SOURCE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "oldSource"), java.lang.String.valueOf(java.lang.Math.min(OLD_SOURCE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "oldSource", this.oldSource, oldSource));
   }

   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public static final java.lang.String NEW_SOURCE = "newSource";
   static int NEW_SOURCE_UPPER_LIMIT = -1;
   java.lang.String newSource;
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public java.lang.String getNewSource() {
      return newSource;
   }
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public void setNewSource(java.lang.String newSource) throws wt.util.WTPropertyVetoException {
      newSourceValidate(newSource);
      this.newSource = newSource;
   }
   void newSourceValidate(java.lang.String newSource) throws wt.util.WTPropertyVetoException {
      if (NEW_SOURCE_UPPER_LIMIT < 1) {
         try { NEW_SOURCE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("newSource").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { NEW_SOURCE_UPPER_LIMIT = 250; }
      }
      if (newSource != null && !wt.fc.PersistenceHelper.checkStoredLength(newSource.toString(), NEW_SOURCE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "newSource"), java.lang.String.valueOf(java.lang.Math.min(NEW_SOURCE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "newSource", this.newSource, newSource));
   }

   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public static final java.lang.String OLD_FAE = "oldFAE";
   static int OLD_FAE_UPPER_LIMIT = -1;
   java.lang.String oldFAE;
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public java.lang.String getOldFAE() {
      return oldFAE;
   }
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public void setOldFAE(java.lang.String oldFAE) throws wt.util.WTPropertyVetoException {
      oldFAEValidate(oldFAE);
      this.oldFAE = oldFAE;
   }
   void oldFAEValidate(java.lang.String oldFAE) throws wt.util.WTPropertyVetoException {
      if (OLD_FAE_UPPER_LIMIT < 1) {
         try { OLD_FAE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("oldFAE").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OLD_FAE_UPPER_LIMIT = 250; }
      }
      if (oldFAE != null && !wt.fc.PersistenceHelper.checkStoredLength(oldFAE.toString(), OLD_FAE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "oldFAE"), java.lang.String.valueOf(java.lang.Math.min(OLD_FAE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "oldFAE", this.oldFAE, oldFAE));
   }

   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public static final java.lang.String NEW_FAE = "newFAE";
   static int NEW_FAE_UPPER_LIMIT = -1;
   java.lang.String newFAE;
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public java.lang.String getNewFAE() {
      return newFAE;
   }
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public void setNewFAE(java.lang.String newFAE) throws wt.util.WTPropertyVetoException {
      newFAEValidate(newFAE);
      this.newFAE = newFAE;
   }
   void newFAEValidate(java.lang.String newFAE) throws wt.util.WTPropertyVetoException {
      if (NEW_FAE_UPPER_LIMIT < 1) {
         try { NEW_FAE_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("newFAE").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { NEW_FAE_UPPER_LIMIT = 250; }
      }
      if (newFAE != null && !wt.fc.PersistenceHelper.checkStoredLength(newFAE.toString(), NEW_FAE_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "newFAE"), java.lang.String.valueOf(java.lang.Math.min(NEW_FAE_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "newFAE", this.newFAE, newFAE));
   }

   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public static final java.lang.String CHANGE_REASON = "changeReason";
   static int CHANGE_REASON_UPPER_LIMIT = -1;
   java.lang.String changeReason;
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public java.lang.String getChangeReason() {
      return changeReason;
   }
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public void setChangeReason(java.lang.String changeReason) throws wt.util.WTPropertyVetoException {
      changeReasonValidate(changeReason);
      this.changeReason = changeReason;
   }
   void changeReasonValidate(java.lang.String changeReason) throws wt.util.WTPropertyVetoException {
      if (CHANGE_REASON_UPPER_LIMIT < 1) {
         try { CHANGE_REASON_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("changeReason").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { CHANGE_REASON_UPPER_LIMIT = 1000; }
      }
      if (changeReason != null && !wt.fc.PersistenceHelper.checkStoredLength(changeReason.toString(), CHANGE_REASON_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "changeReason"), java.lang.String.valueOf(java.lang.Math.min(CHANGE_REASON_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "changeReason", this.changeReason, changeReason));
   }

   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public static final java.lang.String PART_MASTER = "partMaster";
   wt.fc.ObjectReference partMaster;
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public wt.fc.ObjectReference getPartMaster() {
      return partMaster;
   }
   /**
    * @see com.catl.part.sourceChange.PartSourceChangeLog
    */
   public void setPartMaster(wt.fc.ObjectReference partMaster) throws wt.util.WTPropertyVetoException {
      partMasterValidate(partMaster);
      this.partMaster = partMaster;
   }
   void partMasterValidate(wt.fc.ObjectReference partMaster) throws wt.util.WTPropertyVetoException {
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

   public static final long EXTERNALIZATION_VERSION_UID = 5898271657844817401L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( changeReason );
      output.writeObject( newFAE );
      output.writeObject( newSource );
      output.writeObject( oldFAE );
      output.writeObject( oldSource );
      output.writeObject( operator );
      output.writeObject( partMaster );
      output.writeObject( version );
   }

   protected void super_writeExternal_PartSourceChangeLog(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (com.catl.part.sourceChange.PartSourceChangeLog) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_PartSourceChangeLog(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "changeReason", changeReason );
      output.setString( "newFAE", newFAE );
      output.setString( "newSource", newSource );
      output.setString( "oldFAE", oldFAE );
      output.setString( "oldSource", oldSource );
      output.writeObject( "operator", operator, wt.org.WTPrincipalReference.class, false );
      output.writeObject( "partMaster", partMaster, wt.fc.ObjectReference.class, true );
      output.setString( "version", version );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      changeReason = input.getString( "changeReason" );
      newFAE = input.getString( "newFAE" );
      newSource = input.getString( "newSource" );
      oldFAE = input.getString( "oldFAE" );
      oldSource = input.getString( "oldSource" );
      operator = (wt.org.WTPrincipalReference) input.readObject( "operator", operator, wt.org.WTPrincipalReference.class, false );
      partMaster = (wt.fc.ObjectReference) input.readObject( "partMaster", partMaster, wt.fc.ObjectReference.class, true );
      version = input.getString( "version" );
   }

   boolean readVersion5898271657844817401L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      changeReason = (java.lang.String) input.readObject();
      newFAE = (java.lang.String) input.readObject();
      newSource = (java.lang.String) input.readObject();
      oldFAE = (java.lang.String) input.readObject();
      oldSource = (java.lang.String) input.readObject();
      operator = (wt.org.WTPrincipalReference) input.readObject();
      partMaster = (wt.fc.ObjectReference) input.readObject();
      version = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( PartSourceChangeLog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion5898271657844817401L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_PartSourceChangeLog( _PartSourceChangeLog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
