package com.catl.part.platformChange;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _PartPlatformChangeLog extends wt.fc.WTObject implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final java.lang.String CLASSNAME = PartPlatformChangeLog.class.getName();

   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public static final java.lang.String VERSION = "version";
   static int VERSION_UPPER_LIMIT = -1;
   java.lang.String version;
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public java.lang.String getVersion() {
      return version;
   }
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
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
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public static final java.lang.String OPERATOR = "operator";
   wt.org.WTPrincipalReference operator;
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public wt.org.WTPrincipalReference getOperator() {
      return operator;
   }
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public void setOperator(wt.org.WTPrincipalReference operator) throws wt.util.WTPropertyVetoException {
      operatorValidate(operator);
      this.operator = operator;
   }
   void operatorValidate(wt.org.WTPrincipalReference operator) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public static final java.lang.String OLD_PLATFORM = "oldPlatform";
   static int OLD_PLATFORM_UPPER_LIMIT = -1;
   java.lang.String oldPlatform;
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public java.lang.String getOldPlatform() {
      return oldPlatform;
   }
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public void setOldPlatform(java.lang.String oldPlatform) throws wt.util.WTPropertyVetoException {
      oldPlatformValidate(oldPlatform);
      this.oldPlatform = oldPlatform;
   }
   void oldPlatformValidate(java.lang.String oldPlatform) throws wt.util.WTPropertyVetoException {
      if (OLD_PLATFORM_UPPER_LIMIT < 1) {
         try { OLD_PLATFORM_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("oldPlatform").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OLD_PLATFORM_UPPER_LIMIT = 250; }
      }
      if (oldPlatform != null && !wt.fc.PersistenceHelper.checkStoredLength(oldPlatform.toString(), OLD_PLATFORM_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "oldPlatform"), java.lang.String.valueOf(java.lang.Math.min(OLD_PLATFORM_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "oldPlatform", this.oldPlatform, oldPlatform));
   }

   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public static final java.lang.String NEW_PLATFORM = "newPlatform";
   static int NEW_PLATFORM_UPPER_LIMIT = -1;
   java.lang.String newPlatform;
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public java.lang.String getNewPlatform() {
      return newPlatform;
   }
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public void setNewPlatform(java.lang.String newPlatform) throws wt.util.WTPropertyVetoException {
      newPlatformValidate(newPlatform);
      this.newPlatform = newPlatform;
   }
   void newPlatformValidate(java.lang.String newPlatform) throws wt.util.WTPropertyVetoException {
      if (NEW_PLATFORM_UPPER_LIMIT < 1) {
         try { NEW_PLATFORM_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("newPlatform").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { NEW_PLATFORM_UPPER_LIMIT = 250; }
      }
      if (newPlatform != null && !wt.fc.PersistenceHelper.checkStoredLength(newPlatform.toString(), NEW_PLATFORM_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "newPlatform"), java.lang.String.valueOf(java.lang.Math.min(NEW_PLATFORM_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "newPlatform", this.newPlatform, newPlatform));
   }

   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public static final java.lang.String CHANGE_REASON = "changeReason";
   static int CHANGE_REASON_UPPER_LIMIT = -1;
   java.lang.String changeReason;
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public java.lang.String getChangeReason() {
      return changeReason;
   }
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
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
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public static final java.lang.String PART_MASTER = "partMaster";
   wt.fc.ObjectReference partMaster;
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
    */
   public wt.fc.ObjectReference getPartMaster() {
      return partMaster;
   }
   /**
    * @see com.catl.part.platformChange.PartPlatformChangeLog
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
      output.writeObject( newPlatform );
      output.writeObject( oldPlatform );
      output.writeObject( operator );
      output.writeObject( partMaster );
      output.writeObject( version );
   }

   protected void super_writeExternal_PartPlatformChangeLog(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (PartPlatformChangeLog) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_PartPlatformChangeLog(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "changeReason", changeReason );
      output.setString( "newPlatform", newPlatform );
      output.setString( "oldPlatform", oldPlatform );
      output.writeObject( "operator", operator, wt.org.WTPrincipalReference.class, false );
      output.writeObject( "partMaster", partMaster, wt.fc.ObjectReference.class, true );
      output.setString( "version", version );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      changeReason = input.getString( "changeReason" );
      newPlatform = input.getString( "newPlatform" );
      oldPlatform = input.getString( "oldPlatform" );
      operator = (wt.org.WTPrincipalReference) input.readObject( "operator", operator, wt.org.WTPrincipalReference.class, false );
      partMaster = (wt.fc.ObjectReference) input.readObject( "partMaster", partMaster, wt.fc.ObjectReference.class, true );
      version = input.getString( "version" );
   }

   boolean readVersion5898271657844817401L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      changeReason = (java.lang.String) input.readObject();
      newPlatform = (java.lang.String) input.readObject();
      oldPlatform = (java.lang.String) input.readObject();
      operator = (wt.org.WTPrincipalReference) input.readObject();
      partMaster = (wt.fc.ObjectReference) input.readObject();
      version = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( PartPlatformChangeLog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion5898271657844817401L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_PartPlatformChangeLog( _PartPlatformChangeLog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
