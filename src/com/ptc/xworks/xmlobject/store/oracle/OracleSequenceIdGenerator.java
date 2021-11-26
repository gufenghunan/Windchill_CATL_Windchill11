/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.ptc.xworks.xmlobject.store.oracle;

import com.ptc.xworks.xmlobject.store.IdGenerator;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreException;
import com.ptc.xworks.xmlobject.store.oracle.DatabaseResourceManager;
import com.ptc.xworks.xmlobject.store.oracle.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class OracleSequenceIdGenerator implements IdGenerator {
	private static final Logger logger = Logger.getLogger(OracleSequenceIdGenerator.class);
	private DatabaseResourceManager databaseResourceManager;
	private String sequenceName;

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	public void setDatabaseResourceManager(DatabaseResourceManager databaseResourceManager) {
		this.databaseResourceManager = databaseResourceManager;
	}

	public long generateId() throws XmlObjectStoreException {
		String sql = "select next value for " + this.sequenceName + "1";
		final ArrayList idList = new ArrayList(1);

		try {
			this.databaseResourceManager.executeQuery(sql, new RowMapper() {
				public void mappingRow(ResultSet rs) throws SQLException {
					idList.add(Long.valueOf(rs.getLong(1)));
				}
			});
		} catch (SQLException arg4) {
			throw new XmlObjectStoreException("Cannot get a new id from sequence " + this.sequenceName, arg4);
		}

		if (idList.size() > 0) {
			long id = ((Long) idList.get(0)).longValue();
			if (logger.isDebugEnabled()) {
				logger.debug("generateId(): new id " + id);
			}

			return id;
		} else {
			throw new XmlObjectStoreException("Cannot get a new id from sequence " + this.sequenceName);
		}
	}
}