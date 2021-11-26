/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.ptc.xworks.xmlobject.store.oracle;

import com.ptc.xworks.xmlobject.BaseXmlObjectRef;
import com.ptc.xworks.xmlobject.XmlObject;
import com.ptc.xworks.xmlobject.XmlObjectIdentifier;
import com.ptc.xworks.xmlobject.XmlObjectLink;
import com.ptc.xworks.xmlobject.annotation.processor.XmlObjectColumnMetadata;
import com.ptc.xworks.xmlobject.annotation.processor.XmlObjectStoreMetadata;
import com.ptc.xworks.xmlobject.store.DefaultXmlObjectRefInflator;
import com.ptc.xworks.xmlobject.store.XmlObjectLinkStoreMapper;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreException;
import com.ptc.xworks.xmlobject.store.oracle.BaseXmlObjectStoreMapper;
import com.ptc.xworks.xmlobject.store.oracle.RowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

public class BaseXmlObjectLinkStoreMapper<T extends XmlObjectLink> extends BaseXmlObjectStoreMapper<T>
		implements XmlObjectLinkStoreMapper<T> {
	private static final Logger logger = Logger.getLogger(BaseXmlObjectLinkStoreMapper.class);
	private Class<? extends XmlObjectLink> xmlObjectLinkClass;

	public void setXmlObjectLinkClass(Class<? extends XmlObjectLink> xmlObjectLinkClass) {
		this.xmlObjectLinkClass = xmlObjectLinkClass;
	}

	protected Class<? extends XmlObjectLink> getXmlObjectLinkClass() {
		return this.xmlObjectLinkClass;
	}

	protected String getInsertSql(T xmlObject) {
		XmlObjectStoreMetadata storeMetadata = this.getStoreMetadata(xmlObject.getClass());
		List columns = storeMetadata.getColumns();
		StringBuilder sqlInsert = new StringBuilder();
		sqlInsert.append("INSERT INTO " + this.getTableName(xmlObject.getClass()) + "(");
		int i = 0;

		Iterator arg5;
		XmlObjectColumnMetadata column;
		for (arg5 = columns.iterator(); arg5.hasNext(); ++i) {
			column = (XmlObjectColumnMetadata) arg5.next();
			if (i == 0) {
				sqlInsert.append(column.getColumnName());
			} else {
				sqlInsert.append("," + column.getColumnName());
			}
		}

		sqlInsert.append("," + storeMetadata.getXmlColumn());
		sqlInsert.append(")");
		sqlInsert.append(" SELECT ");
		i = 0;

		for (arg5 = columns.iterator(); arg5.hasNext(); ++i) {
			column = (XmlObjectColumnMetadata) arg5.next();
			if (i == 0) {
				sqlInsert.append("?");
			} else {
				sqlInsert.append(", ?");
			}
		}

		//sqlInsert.append(", ? FROM DUAL ");
		sqlInsert.append(", ? FROM ");
		sqlInsert.append(storeMetadata.getTable()+" ");
		sqlInsert.append(" WHERE NOT EXISTS (SELECT 1 FROM " + storeMetadata.getTable());
		sqlInsert.append(" WHERE " + storeMetadata.getColumnName("parent.type") + " = ? ");
		sqlInsert.append(" AND " + storeMetadata.getColumnName("parent.id") + " = ? ");
		sqlInsert.append(" AND " + storeMetadata.getColumnName("parentRole") + " = ? ");
		sqlInsert.append(" AND " + storeMetadata.getColumnName("child.type") + " = ? ");
		sqlInsert.append(" AND " + storeMetadata.getColumnName("child.id") + " = ? ");
		sqlInsert.append(" AND " + storeMetadata.getColumnName("childRole") + " = ? ");
		sqlInsert.append(")");
		if (logger.isDebugEnabled()) {
			logger.debug("getInsertSql():Insert SQL - " + sqlInsert);
		}
		System.out.println("getInsertSql():Insert SQL - "+sqlInsert);

		return sqlInsert.toString();
	}

	protected PreparedStatement getInsertStatement(Connection conn, T link, List<SQLXML> resourcesToClose)
			throws SQLException, XmlObjectStoreException {
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(this.getInsertSql(link));
		XmlObjectStoreMetadata storeMetadata = this.getStoreMetadata(link.getClass());
		List columns = storeMetadata.getColumns();
		int xmlContentPosition = 1;
		int i = 0;

		for (Iterator xmlDoc = columns.iterator(); xmlDoc.hasNext(); ++xmlContentPosition) {
			XmlObjectColumnMetadata xmlType = (XmlObjectColumnMetadata) xmlDoc.next();
			this.setColumnValue(link, xmlType.getAttributeName(), pstmt, i + 1);
			++i;
		}

		String arg10 = this.getXmlObjectConverterManager().toXml(link);
		SQLXML arg11 = conn.createSQLXML();//SQLXML.createXML(conn, arg10);
		arg11.setString(arg10);
		resourcesToClose.add(arg11);
		pstmt.setObject(xmlContentPosition++, arg11);
		pstmt.setString(xmlContentPosition++, link.getParent().getType());
		pstmt.setLong(xmlContentPosition++, link.getParent().getId());
		pstmt.setString(xmlContentPosition++, link.getParentRole());
		pstmt.setString(xmlContentPosition++, link.getChild().getType());
		pstmt.setLong(xmlContentPosition++, link.getChild().getId());
		pstmt.setString(xmlContentPosition++, link.getChildRole());
		System.out.println("Pstmt:\t"+pstmt.toString());
		return pstmt;
	}

	protected void mappingRow(T link, ResultSet rs) throws SQLException {
		XmlObjectStoreMetadata storeMeta = this.getStoreMetadata(link.getClass());
		String columnParentId = storeMeta.getColumnName("parent.id");
		String columnParentType = storeMeta.getColumnName("parent.type");
		BaseXmlObjectRef parent = new BaseXmlObjectRef(rs.getLong(columnParentId), rs.getString(columnParentType));
		parent.setXmlObjectRefInflator(new DefaultXmlObjectRefInflator(this.getXmlObjectStoreManager()));
		String columnChildId = storeMeta.getColumnName("child.id");
		String columnChildType = storeMeta.getColumnName("child.type");
		BaseXmlObjectRef child = new BaseXmlObjectRef(rs.getLong(columnChildId), rs.getString(columnChildType));
		child.setXmlObjectRefInflator(new DefaultXmlObjectRefInflator(this.getXmlObjectStoreManager()));
		link.setParent(parent);
		link.setChild(child);
		super.mappingRow(link, rs);
	}

	public List<T> expand(XmlObject parent, String role) throws XmlObjectStoreException {
		return this.expand(parent.getIdentifier(), role);
	}

	public List<T> expand(XmlObjectIdentifier parentOid, String childRole) throws XmlObjectStoreException {
		XmlObjectStoreMetadata storeMeta = this.getStoreMetadata(this.getXmlObjectLinkClass());
		StringBuilder sqlNavigate = new StringBuilder();
		sqlNavigate.append("SELECT * FROM " + storeMeta.getTable());
		sqlNavigate.append(" WHERE " + storeMeta.getColumnName("parent.type") + " = ?");
		sqlNavigate.append("  AND " + storeMeta.getColumnName("parent.id") + " = ?");
		sqlNavigate.append("  AND " + storeMeta.getColumnName("childRole") + " = ?");
		sqlNavigate.append(" ORDER BY " + storeMeta.getColumnName("identifier.id"));
		if (logger.isDebugEnabled()) {
			logger.debug("expand():Expand SQL - " + sqlNavigate);
		}

		Connection cnn = null;
		PreparedStatement pstmt = null;
		final ArrayList result = new ArrayList();

		try {
			cnn = this.getDatabaseResourceManager().getConnection();
			pstmt = cnn.prepareStatement(sqlNavigate.toString());
			pstmt.setString(1, parentOid.getType());
			pstmt.setLong(2, parentOid.getId());
			pstmt.setString(3, childRole);
			final String e = storeMeta.getColumnName("identifier.type");
			this.getDatabaseResourceManager().executeQuery(pstmt, new RowMapper() {
				public void mappingRow(ResultSet rs) throws SQLException {
					String type = rs.getString(e);
					XmlObjectLink link = (XmlObjectLink) BaseXmlObjectLinkStoreMapper.this.createInstance(type);
					BaseXmlObjectLinkStoreMapper.this.mappingFromXml(link, rs);
					BaseXmlObjectLinkStoreMapper.this.mappingRow((T) link, rs);
					result.add(link);
				}
			});
		} catch (SQLException arg11) {
			throw this.convertSQLException(arg11);
		} finally {
			this.getDatabaseResourceManager().releaseAll(new AutoCloseable[] { pstmt, cnn });
		}

		return result;
	}

	public List<T> linkedBy(XmlObject child) throws XmlObjectStoreException {
		return this.linkedBy((XmlObjectIdentifier) child.getIdentifier(), (String) null);
	}

	public List<T> linkedBy(XmlObject child, String parentRole) throws XmlObjectStoreException {
		return this.linkedBy(child.getIdentifier(), parentRole);
	}

	public List<T> linkedBy(XmlObjectIdentifier childOid, String parentRole) throws XmlObjectStoreException {
		XmlObjectStoreMetadata storeMeta = this.getStoreMetadata(this.getXmlObjectLinkClass());
		StringBuilder sqlLinkedBy = new StringBuilder();
		sqlLinkedBy.append("SELECT * FROM " + storeMeta.getTable());
		sqlLinkedBy.append(" WHERE " + storeMeta.getColumnName("child.type") + " = ?");
		sqlLinkedBy.append("  AND " + storeMeta.getColumnName("child.id") + " = ?");
		if (parentRole != null) {
			sqlLinkedBy.append("  AND " + storeMeta.getColumnName("parentRole") + " = ?");
		}

		sqlLinkedBy.append(
				" ORDER BY " + storeMeta.getColumnName("parentRole") + "," + storeMeta.getColumnName("identifier.id"));
		if (logger.isDebugEnabled()) {
			logger.debug("linkedBy():LinkedBy SQL - " + sqlLinkedBy);
		}

		Connection cnn = null;
		PreparedStatement pstmt = null;
		final ArrayList result = new ArrayList();

		try {
			cnn = this.getDatabaseResourceManager().getConnection();
			pstmt = cnn.prepareStatement(sqlLinkedBy.toString());
			pstmt.setString(1, childOid.getType());
			pstmt.setLong(2, childOid.getId());
			if (parentRole != null) {
				pstmt.setString(3, parentRole);
			}

			final String e = storeMeta.getColumnName("identifier.type");
			this.getDatabaseResourceManager().executeQuery(pstmt, new RowMapper() {
				public void mappingRow(ResultSet rs) throws SQLException {
					String type = rs.getString(e);
					XmlObjectLink link = (XmlObjectLink) BaseXmlObjectLinkStoreMapper.this.createInstance(type);
					BaseXmlObjectLinkStoreMapper.this.mappingFromXml(link, rs);
					BaseXmlObjectLinkStoreMapper.this.mappingRow((T) link, rs);
					result.add(link);
				}
			});
		} catch (SQLException arg11) {
			throw this.convertSQLException(arg11);
		} finally {
			this.getDatabaseResourceManager().releaseAll(new AutoCloseable[] { pstmt, cnn });
		}

		return result;
	}

	public List<T> removeChilds(XmlObject parent, String childRole) throws XmlObjectStoreException {
		return this.removeChilds(parent.getIdentifier(), childRole);
	}

	public List<T> removeChilds(XmlObjectIdentifier parentOid, String childRole) throws XmlObjectStoreException {
		List result = this.expand(parentOid, childRole);
		if (result.isEmpty()) {
			return result;
		} else {
			XmlObjectStoreMetadata storeMeta = this.getStoreMetadata(this.getXmlObjectLinkClass());
			StringBuilder sqlRemoveLinks = new StringBuilder();
			sqlRemoveLinks.append("DELETE FROM " + storeMeta.getTable());
			sqlRemoveLinks.append(" WHERE " + storeMeta.getColumnName("parent.type") + " = ?");
			sqlRemoveLinks.append("  AND  " + storeMeta.getColumnName("parent.id") + " = ?");
			sqlRemoveLinks.append("  AND  " + storeMeta.getColumnName("childRole") + " = ?");
			if (logger.isDebugEnabled()) {
				logger.debug("removeChilds():RemoveChildLink SQL - " + sqlRemoveLinks);
			}

			Connection cnn = null;
			PreparedStatement pstmt = null;

			try {
				cnn = this.getDatabaseResourceManager().getConnection();
				pstmt = cnn.prepareStatement(sqlRemoveLinks.toString());
				pstmt.setString(1, parentOid.getType());
				pstmt.setLong(2, parentOid.getId());
				pstmt.setString(3, childRole);
				int e = pstmt.executeUpdate();
			} catch (SQLException arg11) {
				throw this.convertSQLException(arg11);
			} finally {
				this.getDatabaseResourceManager().releaseAll(new AutoCloseable[] { pstmt, cnn });
			}

			return result;
		}
	}
}