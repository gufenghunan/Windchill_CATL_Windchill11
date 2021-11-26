/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.ptc.xworks.xmlobject.store.oracle;

import com.ptc.xworks.util.ObjectUtils;
import com.ptc.xworks.windchill.listener.XWorksEventService;
import com.ptc.xworks.xmlobject.PersistentState;
import com.ptc.xworks.xmlobject.XmlObject;
import com.ptc.xworks.xmlobject.XmlObjectIdentifier;
import com.ptc.xworks.xmlobject.annotation.processor.XmlObjectColumnMetadata;
import com.ptc.xworks.xmlobject.annotation.processor.XmlObjectStoreMetadata;
import com.ptc.xworks.xmlobject.search.OrderBy;
import com.ptc.xworks.xmlobject.search.XmlObjectDeleteCriteria;
import com.ptc.xworks.xmlobject.search.XmlObjectUpdateCriteria;
import com.ptc.xworks.xmlobject.search.XmlSearchCriteria;
import com.ptc.xworks.xmlobject.store.ConcurrentUpdateException;
import com.ptc.xworks.xmlobject.store.XmlObjectEvent;
import com.ptc.xworks.xmlobject.store.XmlObjectNotExistException;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreException;
import com.ptc.xworks.xmlobject.store.oracle.AbstractXmlObjectStoreMapper;
import com.ptc.xworks.xmlobject.store.oracle.DatabaseResourceManager;
import com.ptc.xworks.xmlobject.util.AttributeUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

public class BaseXmlObjectStoreMapper<T extends XmlObject> extends AbstractXmlObjectStoreMapper<T> {
	private static final Logger logger = Logger.getLogger(BaseXmlObjectStoreMapper.class);
	private XWorksEventService eventService = (XWorksEventService) ManagerServiceFactory.getDefault()
			.getManager(XWorksEventService.class);
	private boolean throwExceptionIfNoDataInserted = true;

	public void setThrowExceptionIfNoDataInserted(boolean throwExceptionIfNoDataInserted) {
		this.throwExceptionIfNoDataInserted = throwExceptionIfNoDataInserted;
	}

	public T load(XmlObjectIdentifier xmlOid) throws XmlObjectStoreException {
		if (logger.isDebugEnabled()) {
			logger.debug("load(): start to load " + xmlOid);
		}

		XmlObjectEvent event = new XmlObjectEvent("PRE_LOAD", xmlOid);

		try {
			this.eventService.dispatchXmlObjectEvent(event);
		} catch (WTException arg14) {
			throw new XmlObjectStoreException(arg14);
		}

		DatabaseResourceManager dbManager = this.getDatabaseResourceManager();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		XmlObject e1;
		try {
			conn = dbManager.getConnection();
			pstmt = this.getLoadStatement(conn, xmlOid);
			rs = pstmt.executeQuery();
			if (!rs.next()) {
				throw new XmlObjectNotExistException(xmlOid, "Cannot load XmlObject by " + xmlOid);
			}

			XmlObject e = this.createInstance(xmlOid.getType());
			this.mappingFromXml(e, rs);
			this.mappingRow((T) e, rs);
			if (logger.isDebugEnabled()) {
				logger.debug("load(): finished to load " + xmlOid);
			}

			event = new XmlObjectEvent("POST_LOAD", xmlOid);

			try {
				this.eventService.dispatchXmlObjectEvent(event);
			} catch (WTException arg13) {
				throw new XmlObjectStoreException(arg13);
			}

			e1 = e;
		} catch (SQLException arg15) {
			throw this.convertSQLException(arg15);
		} finally {
			dbManager.releaseAll(new AutoCloseable[] { rs, pstmt, conn });
		}

		return (T) e1;
	}

	protected String getLoadSql(XmlObjectIdentifier xmlOid) {
		String loadSql = "SELECT * FROM " + this.getTableName(xmlOid.getType()) + " WHERE "
				+ this.getStoreMetadata(xmlOid).getIdColumn() + " = ?";
		if (logger.isDebugEnabled()) {
			logger.debug("getLoadSql():Load SQL - " + loadSql);
		}

		return loadSql;
	}

	protected PreparedStatement getLoadStatement(Connection conn, XmlObjectIdentifier xmlOid) throws SQLException {
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(this.getLoadSql(xmlOid));
		pstmt.setLong(1, xmlOid.getId());
		return pstmt;
	}

	public T save(T xmlObj) throws XmlObjectStoreException {
		if (logger.isDebugEnabled()) {
			logger.debug("save(): start to save " + xmlObj);
		}

		if (xmlObj == null) {
			throw new NullPointerException("XmlObject to save cannot be null!");
		} else if (PersistentState.NON_PERSISTED == xmlObj.getIdentifier().getState()) {
			try {
				return this.insert(xmlObj);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (PersistentState.SAVED == xmlObj.getIdentifier().getState()) {
			xmlObj.setUpdatedBy(this.getCurrentUserResolver().getCurrentUser());
			try {
				return this.update(xmlObj);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			throw new IllegalArgumentException("Cannot save a XmlObject that PersistentState is DELETED!");
		}
		return xmlObj;
	}

	public T insert(T xmlObject) throws XmlObjectStoreException, SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug("insert(): start to insert " + xmlObject);
		}

		if (xmlObject == null) {
			throw new NullPointerException("XmlObject to save is null");
		} else {
			XmlObjectEvent event = new XmlObjectEvent("PRE_INSERT", xmlObject);

			try {
				this.eventService.dispatchXmlObjectEvent(event);
			} catch (WTException arg24) {
				throw new XmlObjectStoreException(arg24);
			}

			DatabaseResourceManager dbManager = this.getDatabaseResourceManager();
			Connection conn = null;
			PreparedStatement pstmt = null;
			long oldId = xmlObject.getIdentifier().getId();
			LinkedList resourcesToClose = new LinkedList();
			boolean arg22 = false;

			XmlObject e1;
			try {
				arg22 = true;
				conn = dbManager.getConnection();
				long e = this.getIdGenerator().generateId();
				xmlObject.getIdentifier().setId(e);
				xmlObject.getIdentifier().setState(PersistentState.SAVED);
				Timestamp currentTime = new Timestamp(System.currentTimeMillis());
				if (xmlObject.getCreateStamp() == null) {
					xmlObject.setCreateStamp(currentTime);
				}

				if (xmlObject.getUpdateStamp() == null) {
					xmlObject.setUpdateStamp(currentTime);
				}

				pstmt = this.getInsertStatement(conn, xmlObject, resourcesToClose);
				int insertedRows = pstmt.executeUpdate();
				if (this.throwExceptionIfNoDataInserted && insertedRows <= 0) {
					throw new XmlObjectStoreException("Cannot insert a new XmlObject " + xmlObject.getIdentifier()
							+ "  to database, inserted rows is 0");
				}

				if (logger.isDebugEnabled()) {
					logger.debug("insert(): finished to insert " + xmlObject);
				}

				event = new XmlObjectEvent("POST_INSERT", xmlObject);

				try {
					this.eventService.dispatchXmlObjectEvent(event);
				} catch (WTException arg23) {
					throw new XmlObjectStoreException(arg23);
				}

				e1 = xmlObject;
				arg22 = false;
			} catch (SQLException arg25) {
				xmlObject.getIdentifier().setId(oldId);
				xmlObject.getIdentifier().setState(PersistentState.NON_PERSISTED);
				throw this.convertSQLException(arg25);
			} finally {
				if (arg22) {
					Iterator arg16 = resourcesToClose.iterator();

					while (arg16.hasNext()) {
						SQLXML xmlType1 = (SQLXML) arg16.next();
						xmlType1.free();
					}

					dbManager.releaseAll(new AutoCloseable[] { pstmt, conn });
				}
			}

			Iterator arg13 = resourcesToClose.iterator();

			while (arg13.hasNext()) {
				SQLXML xmlType = (SQLXML) arg13.next();
				xmlType.free();
			}

			dbManager.releaseAll(new AutoCloseable[] { pstmt, conn });
			return (T) e1;
		}
	}

	protected String getInsertSql(T xmlObject) {
		StringBuilder sqlInsert = new StringBuilder();
		sqlInsert.append("INSERT INTO " + this.getTableName(xmlObject.getClass()) + "(");
		XmlObjectStoreMetadata storeMetadata = this.getStoreMetadata(xmlObject.getClass());
		List columns = storeMetadata.getColumns();
		int i = 0;

		for (Iterator arg5 = columns.iterator(); arg5.hasNext(); ++i) {
			XmlObjectColumnMetadata column = (XmlObjectColumnMetadata) arg5.next();
			if (i == 0) {
				sqlInsert.append(column.getColumnName());
			} else {
				sqlInsert.append("," + column.getColumnName());
			}
		}

		sqlInsert.append("," + storeMetadata.getXmlColumn());
		sqlInsert.append(")");
		sqlInsert.append(" VALUES(");

		for (i = 0; i < columns.size(); ++i) {
			if (i == 0) {
				sqlInsert.append("?");
			} else {
				sqlInsert.append(", ?");
			}
		}

		sqlInsert.append(", ?)");
		if (logger.isDebugEnabled()) {
			logger.debug("getInsertSql():Insert SQL - " + sqlInsert);
		}

		return sqlInsert.toString();
	}

	protected PreparedStatement getInsertStatement(Connection conn, T xmlObject, List<SQLXML> resourcesToClose)
			throws SQLException, XmlObjectStoreException {
		if (logger.isDebugEnabled()) {
			logger.debug("getInsertStatement(): start to create insert PreparedStatement for " + xmlObject);
		}

		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(this.getInsertSql(xmlObject));
		XmlObjectStoreMetadata storeMetadata = this.getStoreMetadata(xmlObject.getClass());
		List columns = storeMetadata.getColumns();
		int xmlContentPosition = 1;
		int i = 0;

		for (Iterator xmlDoc = columns.iterator(); xmlDoc.hasNext(); ++xmlContentPosition) {
			XmlObjectColumnMetadata xmlType = (XmlObjectColumnMetadata) xmlDoc.next();
			this.setColumnValue(xmlObject, xmlType.getAttributeName(), pstmt, i + 1);
			++i;
		}

		String arg11 = this.getXmlObjectConverterManager().toXml(xmlObject);
		SQLXML arg10 = conn.createSQLXML();
		arg10.setString(arg11);
		//XMLType.createXML(conn, arg11);
		pstmt.setObject(xmlContentPosition, arg10);
		resourcesToClose.add(arg10);
		if (logger.isDebugEnabled()) {
			logger.debug("getInsertStatement(): finished to create insert PreparedStatement for " + xmlObject);
		}

		return pstmt;
	}

	public T update(T xmlObject) throws XmlObjectStoreException, SQLException {
		if (xmlObject == null) {
			throw new NullPointerException("XmlObject to update is null");
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("update(): start to update " + xmlObject);
			}

			XmlObjectEvent event = new XmlObjectEvent("PRE_UPDATE", xmlObject);

			try {
				this.eventService.dispatchXmlObjectEvent(event);
			} catch (WTException arg20) {
				throw new XmlObjectStoreException(arg20);
			}

			DatabaseResourceManager dbManager = this.getDatabaseResourceManager();
			Connection conn = null;
			PreparedStatement pstmt = null;
			LinkedList resourcesToClose = new LinkedList();
			int oldUpdateCount = xmlObject.getUpdateCount();
			boolean arg18 = false;

			XmlObject e1;
			try {
				arg18 = true;
				conn = dbManager.getConnection();
				pstmt = this.getUpdateStatement(conn, xmlObject, resourcesToClose);
				int e = pstmt.executeUpdate();
				if (e <= 0) {
					throw new ConcurrentUpdateException("Concurrent update when try to update "
							+ xmlObject.getIdentifier() + ", updateCount=" + xmlObject.getUpdateCount());
				}

				if (e > 1) {
					throw new XmlObjectStoreException("More than one XmlObject\'s has been updated when try to update "
							+ xmlObject.getIdentifier());
				}

				if (logger.isDebugEnabled()) {
					logger.debug("update(): finished to update " + xmlObject);
				}

				event = new XmlObjectEvent("POST_UPDATE", xmlObject);

				try {
					this.eventService.dispatchXmlObjectEvent(event);
				} catch (WTException arg19) {
					throw new XmlObjectStoreException(arg19);
				}

				e1 = xmlObject;
				arg18 = false;
			} catch (SQLException arg21) {
				xmlObject.setUpdateCount(oldUpdateCount);
				throw this.convertSQLException(arg21);
			} finally {
				if (arg18) {
					Iterator arg12 = resourcesToClose.iterator();

					while (arg12.hasNext()) {
						SQLXML xmlType1 = (SQLXML) arg12.next();
						xmlType1.free();
					}

					dbManager.releaseAll(new AutoCloseable[] { pstmt, conn });
				}
			}

			Iterator arg9 = resourcesToClose.iterator();

			while (arg9.hasNext()) {
				SQLXML xmlType = (SQLXML) arg9.next();
				xmlType.free();
			}

			dbManager.releaseAll(new AutoCloseable[] { pstmt, conn });
			return (T) e1;
		}
	}

	protected String getUpdateSql(T xmlObject) {
		XmlObjectStoreMetadata storeMetadata = this.getStoreMetadata(xmlObject.getClass());
		List columns = storeMetadata.getColumns();
		StringBuilder sqlUpdate = new StringBuilder();
		sqlUpdate.append("UPDATE " + this.getTableName(xmlObject.getClass()) + " SET ");
		int position = 0;
		Iterator idColumn = columns.iterator();

		while (idColumn.hasNext()) {
			XmlObjectColumnMetadata updateCountColumn = (XmlObjectColumnMetadata) idColumn.next();
			if (!"identifier.id".equals(updateCountColumn.getAttributeName())
					&& !"identifier.type".equals(updateCountColumn.getAttributeName())) {
				if (position == 0) {
					sqlUpdate.append(" " + updateCountColumn.getColumnName() + " = ? ");
				} else {
					sqlUpdate.append("," + updateCountColumn.getColumnName() + " = ? ");
				}

				++position;
			}
		}

		sqlUpdate.append("," + storeMetadata.getXmlColumn() + " = ? ");
		String arg7 = storeMetadata.getIdColumn();
		String arg8 = storeMetadata.getColumnMetadata("updateCount").getColumnName();
		sqlUpdate.append(" WHERE " + arg7 + " = ? AND " + arg8 + " = ? ");
		if (logger.isDebugEnabled()) {
			logger.debug("getUpdateSql():Update SQL - " + sqlUpdate);
		}

		return sqlUpdate.toString();
	}

	protected PreparedStatement getUpdateStatement(Connection conn, T xmlObject, List<SQLXML> resourcesToClose)
			throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug("getUpdateStatement(): start to create update PreparedStatement for " + xmlObject);
		}

		PreparedStatement pstmt = null;
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		xmlObject.setUpdateStamp(currentTime);
		int currentUpdateCount = xmlObject.getUpdateCount();
		int newUpdateCount = xmlObject.getUpdateCount() + 1;
		xmlObject.setUpdateCount(newUpdateCount);
		pstmt = conn.prepareStatement(this.getUpdateSql(xmlObject));
		XmlObjectStoreMetadata storeMetadata = this.getStoreMetadata(xmlObject.getClass());
		List columns = storeMetadata.getColumns();
		int xmlContentPosition = 1;
		Iterator xmlDoc = columns.iterator();

		while (xmlDoc.hasNext()) {
			XmlObjectColumnMetadata xmlType = (XmlObjectColumnMetadata) xmlDoc.next();
			if (!"identifier.id".equals(xmlType.getAttributeName())
					&& !"identifier.type".equals(xmlType.getAttributeName())) {
				this.setColumnValue(xmlObject, xmlType.getAttributeName(), pstmt, xmlContentPosition);
				++xmlContentPosition;
			}
		}

		String arg12 = this.getXmlObjectConverterManager().toXml(xmlObject);
		SQLXML arg13 = conn.createSQLXML();
		arg13.setString(arg12);
		resourcesToClose.add(arg13);
		pstmt.setObject(xmlContentPosition++, arg13);
		pstmt.setLong(xmlContentPosition++, xmlObject.getIdentifier().getId());
		pstmt.setInt(xmlContentPosition++, currentUpdateCount);
		if (logger.isDebugEnabled()) {
			logger.debug("getUpdateStatement(): finished to create update PreparedStatement for " + xmlObject);
		}

		return pstmt;
	}

	public T delete(T xmlObject) throws XmlObjectStoreException {
		if (logger.isDebugEnabled()) {
			logger.debug("delete(): start to delete " + xmlObject);
		}

		XmlObjectEvent event = new XmlObjectEvent("PRE_DELETE", xmlObject);

		try {
			this.eventService.dispatchXmlObjectEvent(event);
		} catch (WTException arg4) {
			throw new XmlObjectStoreException(arg4);
		}

		this.delete(xmlObject.getIdentifier());
		xmlObject.getIdentifier().setId(0L);
		xmlObject.getIdentifier().setState(PersistentState.DELETED);
		event = new XmlObjectEvent("POST_DELETE", xmlObject);

		try {
			this.eventService.dispatchXmlObjectEvent(event);
		} catch (WTException arg3) {
			throw new XmlObjectStoreException(arg3);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("delete(): finished to delete " + xmlObject);
		}

		return xmlObject;
	}

	public int delete(XmlObjectIdentifier xmlOid) throws XmlObjectStoreException {
		if (xmlOid != null && xmlOid.getState() != PersistentState.NON_PERSISTED
				&& xmlOid.getState() != PersistentState.DELETED) {
			if (logger.isDebugEnabled()) {
				logger.debug("delete(XmlObjectIdentifier): start to delete " + xmlOid);
			}

			XmlObjectEvent event = new XmlObjectEvent("PRE_DELETE", xmlOid);

			try {
				this.eventService.dispatchXmlObjectEvent(event);
			} catch (WTException arg14) {
				throw new XmlObjectStoreException(arg14);
			}

			DatabaseResourceManager dbManager = this.getDatabaseResourceManager();
			Connection conn = null;
			PreparedStatement pstmt = null;
			Object rs = null;

			int e1;
			try {
				conn = dbManager.getConnection();
				pstmt = this.getDeleteStatement(conn, xmlOid);
				int e = pstmt.executeUpdate();
				if (e <= 0) {
					logger.error("Cannot delete a XmlObject that not exists in database by " + xmlOid);
				}

				if (e > 1) {
					logger.error("More than one rows be deleted from database when deleting  " + xmlOid);
				}

				event = new XmlObjectEvent("POST_DELETE", xmlOid);

				try {
					this.eventService.dispatchXmlObjectEvent(event);
				} catch (WTException arg13) {
					throw new XmlObjectStoreException(arg13);
				}

				e1 = e;
			} catch (SQLException arg15) {
				throw this.convertSQLException(arg15);
			} finally {
				dbManager.releaseAll(new AutoCloseable[] { (AutoCloseable) rs, pstmt, conn });
			}

			return e1;
		} else {
			throw new IllegalArgumentException(
					"XmlObjectIdentifier to delete is not a valid (null or not exists in database)");
		}
	}

	protected String getDeleteSql(XmlObjectIdentifier xmlOid) {
		XmlObjectStoreMetadata storeMetadata = this.getStoreMetadata(xmlOid.getType());
		String sqlDelete = "DELETE FROM " + this.getTableName(xmlOid.getType()) + " WHERE "
				+ storeMetadata.getIdColumn() + " = ?";
		if (logger.isDebugEnabled()) {
			logger.debug("getDeleteSql():Delete SQL - " + sqlDelete);
		}

		return sqlDelete;
	}

	protected PreparedStatement getDeleteStatement(Connection conn, XmlObjectIdentifier xmlOid) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug("getDeleteStatement(): start to create deleting PreparedStatement for " + xmlOid);
		}

		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(this.getDeleteSql(xmlOid));
		pstmt.setLong(1, xmlOid.getId());
		if (logger.isDebugEnabled()) {
			logger.debug("getDeleteStatement(): finished to create deleting PreparedStatement for " + xmlOid);
		}

		return pstmt;
	}

	protected T createInstance(String type) {
		if (logger.isDebugEnabled()) {
			logger.debug("createInstance(): create new instance of  " + type);
		}

		return (T) ObjectUtils.createNewInstance(type);
	}

	protected void mappingRow(T xmlObject, ResultSet rs) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug("mappingRow(): start mapping from ResultSet for " + xmlObject);
		}

		XmlObjectStoreMetadata storeMetadata = this.getStoreMetadata(xmlObject.getClass());
		List columns = storeMetadata.getColumns();
		Iterator arg4 = columns.iterator();

		while (arg4.hasNext()) {
			XmlObjectColumnMetadata column = (XmlObjectColumnMetadata) arg4.next();
			String attributeName = column.getAttributeName();
			Object value = this.getColumnValue(xmlObject.getClass(), attributeName, rs);
			AttributeUtils.setNestedAttribute(xmlObject, attributeName, value);
			if (logger.isDebugEnabled()) {
				logger.debug("mappingRow(): set attribute " + attributeName + " to " + value);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("mappingRow(): finished mapping from ResultSet for " + xmlObject);
		}

	}

	protected void mappingFromXml(XmlObject e, ResultSet rs) throws SQLException {
		if (logger.isDebugEnabled()) {
			logger.debug("mappingFromXml(): start mapping from XML...");
		}

		Object xmlCol = rs.getObject(this.getStoreMetadata(e.getClass()).getXmlColumn());
		System.out.println("xmlCol Class\t"+xmlCol.getClass().getName());
		System.out.println("xmlCol\t"+xmlCol);
		if (xmlCol != null && (xmlCol instanceof SQLXML||xmlCol instanceof String)) {
			//SQLXML xmlType = (SQLXML) xmlCol;
			String xmlString = (String) xmlCol;//xmlType.getString();
			if (logger.isDebugEnabled()) {
				logger.debug("mappingFromXml(): mapping XML:\n" + xmlString);
			}

			if (xmlString != null) {
				this.getXmlObjectConverterManager().fromXml(xmlString, e);
			} else {
				logger.info("XML Content from resultset is null!");
			}
		} else {
			logger.info("XML_CONTENT from resultset is not a SQLXML or null!");
		}

	}

	public List<T> search(XmlSearchCriteria<T> criteria) throws XmlObjectStoreException {
		return this.search(criteria, (OrderBy) null);
	}

	public List<T> search(XmlSearchCriteria<T> criteria, OrderBy orderBy) throws XmlObjectStoreException {
		if (logger.isDebugEnabled()) {
			logger.debug("search(): start search ... ");
		}

		DatabaseResourceManager dbManager = this.getDatabaseResourceManager();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList result = new ArrayList();

		try {
			conn = dbManager.getConnection();
			pstmt = this.getSearchStatement(conn, criteria, orderBy);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				XmlObject e = this.createInstance(criteria.getType().getName());
				this.mappingFromXml(e, rs);
				this.mappingRow((T) e, rs);
				result.add(e);
			}
		} catch (SQLException arg11) {
			throw this.convertSQLException(arg11);
		} finally {
			dbManager.releaseAll(new AutoCloseable[] { rs, pstmt, conn });
		}

		if (logger.isDebugEnabled()) {
			logger.debug("search(): finished searching. rows " + result.size() + " returned.");
		}

		return result;
	}

	protected PreparedStatement getSearchStatement(Connection conn, XmlSearchCriteria<T> criteria, OrderBy orderBy)
			throws SQLException {
		XmlObjectStoreMetadata storeMetadata = this.getStoreMetadata(criteria.getType());
		String where = criteria.getExpression(storeMetadata);
		String sql = "SELECT * FROM " + storeMetadata.getTable() + " WHERE " + where;
		if (orderBy != null) {
			String params = orderBy.getExpression(storeMetadata);
			sql = sql + params;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getSearchStatement():Search SQL - " + sql);
		}

		List arg11 = criteria.getParameters(storeMetadata);
		PreparedStatement pstmt = conn.prepareStatement(sql);
		int i = 1;

		for (Iterator arg9 = arg11.iterator(); arg9.hasNext(); ++i) {
			Object param = arg9.next();
			pstmt.setObject(i, param);
		}

		return pstmt;
	}

	public int batchUpdate(XmlObjectUpdateCriteria<T> updateCriteria) throws XmlObjectStoreException {
		if (logger.isDebugEnabled()) {
			logger.debug("batchUpdate(): start batchUpdate.");
		}

		int result = 0;
		if (!updateCriteria.getUpdateColumns().isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("batchUpdate(): start to update columns...");
			}

			DatabaseResourceManager count = this.getDatabaseResourceManager();
			Connection xmlObjects = null;
			PreparedStatement pstmt = null;

			try {
				xmlObjects = count.getConnection();
				pstmt = this.getBatchUpdateStatement(xmlObjects, updateCriteria);
				result = pstmt.executeUpdate();
			} catch (SQLException arg10) {
				throw this.convertSQLException(arg10);
			} finally {
				count.releaseAll(new AutoCloseable[] { pstmt, xmlObjects });
			}

			if (logger.isDebugEnabled()) {
				logger.debug("batchUpdate(): finished to update columns.");
			}

			if (result == 0) {
				return result;
			}
		}

		int arg12 = 0;
		if (!updateCriteria.getUpdateAttributes().isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("batchUpdate(): start to update attributes that stored in XML ...");
			}

			List arg13 = this.search(updateCriteria);

			for (Iterator arg14 = arg13.iterator(); arg14.hasNext(); ++arg12) {
				XmlObject xmlObj = (XmlObject) arg14.next();
				Iterator arg6 = updateCriteria.getUpdateAttributes().entrySet().iterator();

				while (arg6.hasNext()) {
					Entry entry = (Entry) arg6.next();
					AttributeUtils.setAttributeValue(xmlObj, (String) entry.getKey(), entry.getValue());
				}

				this.save((T) xmlObj);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("batchUpdate(): finished to update attributes that stored in XML, rows " + arg12
						+ " has been updated.");
			}
		}

		return Math.max(result, arg12);
	}

	protected PreparedStatement getBatchUpdateStatement(Connection conn, XmlObjectUpdateCriteria<T> updateCriteria)
			throws SQLException {
		XmlObjectStoreMetadata storeInfo = this.getStoreMetadata(updateCriteria.getType());
		StringBuilder sql = new StringBuilder();
		int i = 1;
		sql.append("UPDATE " + storeInfo.getTable() + " SET ");
		LinkedList allParams = new LinkedList();

		for (Iterator where = updateCriteria.getUpdateColumns().entrySet().iterator(); where.hasNext(); ++i) {
			Entry whereParams = (Entry) where.next();
			String pstmt = storeInfo.getColumnMetadata((String) whereParams.getKey()).getColumnName();
			if (i == 1) {
				sql.append(" " + pstmt + " = ? ");
			} else {
				sql.append("," + pstmt + " = ? ");
			}

			allParams.add(whereParams.getValue());
		}

		String arg11 = updateCriteria.getExpression(storeInfo);
		sql.append(" where " + arg11);
		if (logger.isDebugEnabled()) {
			logger.debug("getBatchUpdateStatement():BatchUpdate SQL - " + sql);
		}

		List arg12 = updateCriteria.getParameters(storeInfo);
		allParams.addAll(arg12);
		PreparedStatement arg13 = conn.prepareStatement(sql.toString());
		i = 1;

		for (Iterator arg9 = allParams.iterator(); arg9.hasNext(); ++i) {
			Object param = arg9.next();
			arg13.setObject(i, param);
		}

		return arg13;
	}

	public int batchDelete(XmlObjectDeleteCriteria<T> deleteCriteria) throws XmlObjectStoreException {
		if (logger.isDebugEnabled()) {
			logger.debug("batchDelete(): start to batch delete ...");
		}

		DatabaseResourceManager dbManager = this.getDatabaseResourceManager();
		Connection conn = null;
		PreparedStatement pstmt = null;

		int arg5;
		try {
			conn = dbManager.getConnection();
			pstmt = this.getBatchDeleteStatement(conn, deleteCriteria);
			int e = pstmt.executeUpdate();
			if (logger.isDebugEnabled()) {
				logger.debug("batchDelete(): finished to batch delete.");
			}

			arg5 = e;
		} catch (SQLException arg9) {
			throw this.convertSQLException(arg9);
		} finally {
			dbManager.releaseAll(new AutoCloseable[] { pstmt, conn });
		}

		return arg5;
	}

	protected PreparedStatement getBatchDeleteStatement(Connection conn, XmlObjectDeleteCriteria<T> deleteCriteria)
			throws SQLException {
		XmlObjectStoreMetadata storeInfo = this.getStoreMetadata(deleteCriteria.getType());
		String where = deleteCriteria.getExpression(storeInfo);
		String sql = "DELETE FROM " + storeInfo.getTable() + " WHERE " + where;
		if (logger.isDebugEnabled()) {
			logger.debug("getBatchDeleteStatement():BatchDelete SQL - " + sql);
		}

		List params = deleteCriteria.getParameters(storeInfo);
		PreparedStatement pstmt = conn.prepareStatement(sql);
		int i = 1;

		for (Iterator arg8 = params.iterator(); arg8.hasNext(); ++i) {
			Object param = arg8.next();
			pstmt.setObject(i, param);
		}

		return pstmt;
	}
}