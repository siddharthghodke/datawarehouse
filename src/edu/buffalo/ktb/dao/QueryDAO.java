package edu.buffalo.ktb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buffalo.ktb.bean.Patient;
import edu.buffalo.ktb.db.DBManager;

public class QueryDAO {

	private Connection connection;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	public List<Patient> getPatients() {
		
		try {
			List<Patient> patientList = new ArrayList<Patient>();
			String patientId, patientName;
			Patient patient;
			
			connection = DBManager.getInstance().getConnection();
			String sql = "Select * FROM PATIENT";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				patientId = rs.getString("p_id");
				patientName = rs.getString("name");
				patient = new Patient();
				patient.setPatientId(patientId);
				patient.setPatientName(patientName);
				patientList.add(patient);
			}
			
			return patientList;
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			close();
		}
		
		return null;
	}
	
	public Map<String, Integer> getResultQueryOne(String description, String name, String type) {
		
		try {
			Map<String, Integer> resultMap = new HashMap<String, Integer>();
			
			connection = DBManager.getInstance().getConnection();
			String sql = "select d.DESCRIPTION, count(*) FROM DISEASE d inner join DIAGNOSIS dg on d.DS_ID=dg.DS_ID and d.DESCRIPTION=? group by d.DESCRIPTION"
					+ " UNION select d.NAME, count(*) from DISEASE d inner join DIAGNOSIS dg on d.DS_ID=dg.DS_ID and d.NAME=? group by d.NAME"
					+ " UNION select d.TYPE, count(*) from DISEASE d inner join DIAGNOSIS dg on d.DS_ID=dg.DS_ID and d.TYPE=? group by d.TYPE";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, description);
			pstmt.setString(2, name);
			pstmt.setString(3, type);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				resultMap.put(rs.getString(1), rs.getInt(2));
			}
			
			return resultMap;
			
		} catch(Exception e) {
			System.err.println(e.getMessage());
		} finally {
			close();
		}
		
		return null;
	}
	
	
	public List<String> getResultQueryTwo(String description) {
		
		try {
			
			List<String> resultList = new ArrayList<String>();
			
			connection = DBManager.getInstance().getConnection();
			String sql = "select distinct d.TYPE from DRUG d join DRUG_USE du on d.DR_ID=du.DR_ID and (du.P_ID in "
					+ "(select dg.P_ID from DIAGNOSIS dg join DISEASE ds on dg.DS_ID=ds.DS_ID and ds.DESCRIPTION=?))";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, description);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				resultList.add(rs.getString(1));
			}
			
			return resultList;
			
		} catch(Exception e) {
			System.err.println(e.getMessage());
		} finally {
			close();
		}
		
		return null;
	}
	
	/**
	 * close the resources
	 */
	private void close() {
		try {
			/*if(rs != null) {
				rs.close();
			}
			if (connection != null) {
				connection.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(pstmt != null) {
				pstmt.close();
			}*/
			DBManager.getInstance().releaseResources(connection, pstmt, rs);
			
		} catch (Exception e) {
			
		}
	}
}
