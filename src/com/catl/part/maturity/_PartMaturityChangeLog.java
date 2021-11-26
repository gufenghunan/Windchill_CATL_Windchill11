package com.catl.part.maturity;

@SuppressWarnings({"cast", "deprecation", "unchecked"})
public abstract class _PartMaturityChangeLog extends wt.fc.WTObject implements java.io.Externalizable {
   static final long serialVersionUID = 1;

   static final java.lang.String RESOURCE = "com.catl.part.maturity.maturityResource";
   static final java.lang.String CLASSNAME = PartMaturityChangeLog.class.getName();

   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public static final java.lang.String VERSION = "version";
   static int VERSION_UPPER_LIMIT = -1;
   java.lang.String version;
   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public java.lang.String getVersion() {
      return version;
   }
   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
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
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public static final java.lang.String OPERATOR = "operator";
   wt.org.WTPrincipalReference operator;
   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public wt.org.WTPrincipalReference getOperator() {
      return operator;
   }
   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public void setOperator(wt.org.WTPrincipalReference operator) throws wt.util.WTPropertyVetoException {
      operatorValidate(operator);
      this.operator = operator;
   }
   void operatorValidate(wt.org.WTPrincipalReference operator) throws wt.util.WTPropertyVetoException {
   }

   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public static final java.lang.String OLD_MATURITY = "oldMaturity";
   static int OLD_MATURITY_UPPER_LIMIT = -1;
   java.lang.String oldMaturity;
   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public java.lang.String getOldMaturity() {
      return oldMaturity;
   }
   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public void setOldMaturity(java.lang.String oldMaturity) throws wt.util.WTPropertyVetoException {
      oldMaturityValidate(oldMaturity);
      this.oldMaturity = oldMaturity;
   }
   void oldMaturityValidate(java.lang.String oldMaturity) throws wt.util.WTPropertyVetoException {
      if (OLD_MATURITY_UPPER_LIMIT < 1) {
         try { OLD_MATURITY_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("oldMaturity").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { OLD_MATURITY_UPPER_LIMIT = 250; }
      }
      if (oldMaturity != null && !wt.fc.PersistenceHelper.checkStoredLength(oldMaturity.toString(), OLD_MATURITY_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "oldMaturity"), java.lang.String.valueOf(java.lang.Math.min(OLD_MATURITY_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "oldMaturity", this.oldMaturity, oldMaturity));
   }

   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public static final java.lang.String NEW_MATURITY = "newMaturity";
   static int NEW_MATURITY_UPPER_LIMIT = -1;
   java.lang.String newMaturity;
   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public java.lang.String getNewMaturity() {
      return newMaturity;
   }
   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public void setNewMaturity(java.lang.String newMaturity) throws wt.util.WTPropertyVetoException {
      newMaturityValidate(newMaturity);
      this.newMaturity = newMaturity;
   }
   void newMaturityValidate(java.lang.String newMaturity) throws wt.util.WTPropertyVetoException {
      if (NEW_MATURITY_UPPER_LIMIT < 1) {
         try { NEW_MATURITY_UPPER_LIMIT = (java.lang.Integer) wt.introspection.WTIntrospector.getClassInfo(CLASSNAME).getPropertyDescriptor("newMaturity").getValue(wt.introspection.WTIntrospector.UPPER_LIMIT); }
         catch (wt.introspection.WTIntrospectionException e) { NEW_MATURITY_UPPER_LIMIT = 250; }
      }
      if (newMaturity != null && !wt.fc.PersistenceHelper.checkStoredLength(newMaturity.toString(), NEW_MATURITY_UPPER_LIMIT, true))
         throw new wt.util.WTPropertyVetoException("wt.introspection.introspectionResource", wt.introspection.introspectionResource.UPPER_LIMIT,
               new java.lang.Object[] { new wt.introspection.PropertyDisplayName(CLASSNAME, "newMaturity"), java.lang.String.valueOf(java.lang.Math.min(NEW_MATURITY_UPPER_LIMIT, wt.fc.PersistenceHelper.DB_MAX_SQL_STRING_SIZE/wt.fc.PersistenceHelper.DB_MAX_BYTES_PER_CHAR)) },
               new java.beans.PropertyChangeEvent(this, "newMaturity", this.newMaturity, newMaturity));
   }

   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public static final java.lang.String PART_MASTER = "partMaster";
   wt.fc.ObjectReference partMaster;
   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
    */
   public wt.fc.ObjectReference getPartMaster() {
      return partMaster;
   }
   /**
    * @see com.catl.part.maturity.PartMaturityChangeLog
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

   public static final long EXTERNALIZATION_VERSION_UID = 3027148423292307438L;

   public void writeExternal(java.io.ObjectOutput output) throws java.io.IOException {
      output.writeLong( EXTERNALIZATION_VERSION_UID );

      super.writeExternal( output );

      output.writeObject( newMaturity );
      output.writeObject( oldMaturity );
      output.writeObject( operator );
      output.writeObject( partMaster );
      output.writeObject( version );
   }

   protected void super_writeExternal_PartMaturityChangeLog(java.io.ObjectOutput output) throws java.io.IOException {
      super.writeExternal(output);
   }

   public void readExternal(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      long readSerialVersionUID = input.readLong();
      readVersion( (com.catl.part.maturity.PartMaturityChangeLog) this, input, readSerialVersionUID, false, false );
   }
   protected void super_readExternal_PartMaturityChangeLog(java.io.ObjectInput input) throws java.io.IOException, java.lang.ClassNotFoundException {
      super.readExternal(input);
   }

   public void writeExternal(wt.pds.PersistentStoreIfc output) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.writeExternal( output );

      output.setString( "newMaturity", newMaturity );
      output.setString( "oldMaturity", oldMaturity );
      output.writeObject( "operator", operator, wt.org.WTPrincipalReference.class, false );
      output.writeObject( "partMaster", partMaster, wt.fc.ObjectReference.class, true );
      output.setString( "version", version );
   }

   public void readExternal(wt.pds.PersistentRetrieveIfc input) throws java.sql.SQLException, wt.pom.DatastoreException {
      super.readExternal( input );

      newMaturity = input.getString( "newMaturity" );
      oldMaturity = input.getString( "oldMaturity" );
      operator = (wt.org.WTPrincipalReference) input.readObject( "operator", operator, wt.org.WTPrincipalReference.class, false );
      partMaster = (wt.fc.ObjectReference) input.readObject( "partMaster", partMaster, wt.fc.ObjectReference.class, true );
      version = input.getString( "version" );
   }

   boolean readVersion3027148423292307438L( java.io.ObjectInput input, long readSerialVersionUID, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      if ( !superDone )
         super.readExternal( input );

      newMaturity = (java.lang.String) input.readObject();
      oldMaturity = (java.lang.String) input.readObject();
      operator = (wt.org.WTPrincipalReference) input.readObject();
      partMaster = (wt.fc.ObjectReference) input.readObject();
      version = (java.lang.String) input.readObject();
      return true;
   }

   protected boolean readVersion( PartMaturityChangeLog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      boolean success = true;

      if ( readSerialVersionUID == EXTERNALIZATION_VERSION_UID )
         return readVersion3027148423292307438L( input, readSerialVersionUID, superDone );
      else
         success = readOldVersion( input, readSerialVersionUID, passThrough, superDone );

      if (input instanceof wt.pds.PDSObjectInput)
         wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();

      return success;
   }
   protected boolean super_readVersion_PartMaturityChangeLog( _PartMaturityChangeLog thisObject, java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      return super.readVersion(thisObject, input, readSerialVersionUID, passThrough, superDone);
   }

   boolean readOldVersion( java.io.ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone ) throws java.io.IOException, java.lang.ClassNotFoundException {
      throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID="+readSerialVersionUID+" local class externalizationVersionUID="+EXTERNALIZATION_VERSION_UID);
   }
}
