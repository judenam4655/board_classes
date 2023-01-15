package com.jsp.board;

import java.sql.*;
import java.util.*;
import javax.sql.*;
import javax.naming.*;

public class BoardDAO {
	
	private DataSource ds;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	// singleton Constructor
	private BoardDAO() {
		try {
			Context init = new InitialContext();
			ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle");
		}catch(NamingException e) {
			System.err.println("lookup 실패 : " + e.getMessage());
		}
	}
	
	// Generate BoardDAO Object
	private static BoardDAO instance = new BoardDAO();
	
	// Return static BoardDAO Object
	public static BoardDAO getInstance() {
		return instance;
	}
	
	// close Connection conn Object
	private void close(Connection conn) {
		try {
			if(conn != null) {
				conn.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// close PreparedStatement pstmt Object
	private void close(PreparedStatement pstmt) {
		try {
			if(pstmt != null) {
				pstmt.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// close ResultSet rs Object
	private void close(ResultSet rs) {
		try {
			if(rs != null) {
				rs.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// return ArrayList of BoardDTO Object
	public ArrayList<BoardDTO> list() {
		
		ArrayList<BoardDTO> list = new ArrayList<BoardDTO>();
		
		String query = "select * from board ORDER BY bGroup DESC, bStep ASC";
		// Group : bId 값
		// Step : 원래 글로부터 몇단계 밑으로 내려가서 list가 보일것인지
		// Indent : 몇번이나 들여쓰기 될 것인지
		
		try {
			conn = ds.getConnection();
			
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				int bId = rs.getInt("bId");
				String bWriter = rs.getString("bWriter");
				String bTitle = rs.getString("bTitle");
				String bContent = rs.getString("bContent");
				Timestamp bDate = rs.getTimestamp("bDate");
				int bHit = rs.getInt("bHit");
				int bGroup = rs.getInt("bGroup");
				int bStep = rs.getInt("bStep");
				int bIndent = rs.getInt("bIndent");
				
				list.add(new BoardDTO(bId, bWriter, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
			close(conn);
		}
		return list;
	}
	
	// inserts Data into DataBase
	public int insert(String bWirter, String bTitle, String bContent) {
		
		int result = 0;
		
		try {
			conn = ds.getConnection();
			String query = "insert into board (bId, bWriter, bTitle, bContent, "
					+ "bHit, bGroup, bStep, bIndent) values "
					+ "(board_seq.nextval, ?, ?, ?, 0, board_seq.currval, 0, 0 )";
			
			// nextval : 해당 시퀀스의 값을 증가 
			// currval : 현재 시퀀스를 얻기
			
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, bWirter);
			pstmt.setString(2, bTitle);
			pstmt.setString(3, bContent);
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}
		
		return result;
		
	}
	
	// return Data from DataBase with strID(uses upHit() method)
	public BoardDTO contentView(String strID) {
		
		upHit(strID);
		
		BoardDTO dto = null;
		
		try {
			
			conn = ds.getConnection();
			
			String query = "select * from board where bId = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, Integer.parseInt(strID));
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				int bId = rs.getInt("bId");
				String bWriter = rs.getString("bWriter");
				String bTitle = rs.getString("bTitle");
				String bContent = rs.getString("bContent");
				Timestamp bDate = rs.getTimestamp("bDate");
				int bHit = rs.getInt("bHit");
				int bGroup = rs.getInt("bGroup");
				int bStep = rs.getInt("bStep");
				int bIndent = rs.getInt("bIndent");
				
				dto = new BoardDTO(bId, bWriter, bTitle, bContent, 
							bDate, bHit, bGroup, bStep, bIndent);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
			close(conn);
		}
		return dto;
	}
	
	// 조회수 증가
	private void upHit(String bId) {
		
		try {
			conn = ds.getConnection();
			String query = "update board set bHit = bHit + 1 where bId = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, Integer.parseInt(bId));
			
			pstmt.executeUpdate();
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}
	}

	// modifies Data in DataBase
	public int modify(String bId, String bWriter, String bTitle, String bContent) {
		
		int result = 0;
		
		try {
			conn = ds.getConnection();
			
			String query = "update board set bWriter = ?, bTitle = ?, bContent = ? where bId = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, bWriter);
			pstmt.setString(2, bTitle);
			pstmt.setString(3, bContent);
			pstmt.setInt(4, Integer.parseInt(bId));
			result = pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}
		
		return result;
		
	}
	
	// deletes Data in DataBase
	public int delete(String bId) {
		int result = 0;
		try {
			
			conn = ds.getConnection();
			String query = "delete from board where bId = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, Integer.parseInt(bId));
			result = pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}
		
		return result;
	}
	
	// return Data from DataBase with strID(does not use upHit() method)
	public BoardDTO replyView(String str) {
		BoardDTO dto = null;
		
		try {
			
			conn = ds.getConnection();
			String query = "select * from board where bId = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, Integer.parseInt(str));
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				int bId = rs.getInt("bId");
				String bWriter = rs.getString("bWriter");
				String bTitle = rs.getString("bTitle");
				String bContent = rs.getString("bContent");
				Timestamp bDate = rs.getTimestamp("bDate");
				int bHit = rs.getInt("bHit");
				int bGroup = rs.getInt("bGroup");
				int bStep = rs.getInt("bStep");
				int bIndent = rs.getInt("bIndent");
				
				dto = new BoardDTO(bId, bWriter, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
			close(conn);
		}
		
		return dto;
	}
	
	// 답글 insert 하기 
	public int reply(String bWriter, String bTitle, String bContent, String bGroup, String bStep, String bIndent) {
		
		int result = 0;
		
		replyShape(bGroup, bStep);
		
		
		try {
			conn = ds.getConnection();
			String query = "insert into board (bId, bWriter, bTitle, bContent, "
					+ "bGroup, bStep, bIndent) values (board_seq.nextval, ?, ?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(query);
			 
			pstmt.setString(1, bWriter);
			pstmt.setString(2, bTitle);
			pstmt.setString(3, bContent);
			pstmt.setInt(4, Integer.parseInt(bGroup));
			pstmt.setInt(5, Integer.parseInt(bStep) + 1);
			pstmt.setInt(6, Integer.parseInt(bIndent) + 1);
			
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	// 답글형태
	private void replyShape(String strGroup, String strStep) {
		
		try {
			conn = ds.getConnection();
			String query = "update board set bStep = bStep + 1 "
									+ "where bGroup = ? and bStep > ?";
			// 그룹이 같고 스탭이 현재 스탭보다 큰 스탭들에 한해서 스탭을 올린다.
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, Integer.parseInt(strGroup));
			pstmt.setInt(2, Integer.parseInt(strStep));
			
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	

	
}
