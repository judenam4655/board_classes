package board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class BoardDAO {
	private DataSource ds;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private static BoardDAO instance = new BoardDAO();

	private BoardDAO() {
		try {
			Context init = new InitialContext();
			this.ds = (DataSource) init.lookup("java:comp/env/jdbc/oracle");
		} catch (NamingException var2) {
			System.err.println("lookup 실패 : " + var2.getMessage());
		}

	}

	public static BoardDAO getInstance() {
		return instance;
	}

	private void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception var3) {
			var3.printStackTrace();
		}

	}

	private void close(PreparedStatement pstmt) {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (Exception var3) {
			var3.printStackTrace();
		}

	}

	private void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception var3) {
			var3.printStackTrace();
		}

	}

	public ArrayList<BoardDTO> list() {
		ArrayList<BoardDTO> list = new ArrayList<>();
		String query = "select * from board ORDER BY bGroup DESC, bStep ASC";

		try {
			this.conn = this.ds.getConnection();
			this.pstmt = this.conn.prepareStatement(query);
			this.rs = this.pstmt.executeQuery();

			while (this.rs.next()) {
				int bId = this.rs.getInt("bId");
				String bWriter = this.rs.getString("bWriter");
				String bTitle = this.rs.getString("bTitle");
				String bContent = this.rs.getString("bContent");
				Timestamp bDate = this.rs.getTimestamp("bDate");
				int bHit = this.rs.getInt("bHit");
				int bGroup = this.rs.getInt("bGroup");
				int bStep = this.rs.getInt("bStep");
				int bIndent = this.rs.getInt("bIndent");
				list.add(new BoardDTO(bId, bWriter, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent));
			}
		} catch (Exception var15) {
			var15.printStackTrace();
		} finally {
			this.close(this.rs);
			this.close(this.pstmt);
			this.close(this.conn);
		}

		return list;
	}

	public int insert(String bWirter, String bTitle, String bContent) {
		int result = 0;

		try {
			this.conn = this.ds.getConnection();
			String query = "insert into board (bId, bWriter, bTitle, bContent, bHit, bGroup, bStep, bIndent) values (board_seq.nextval, ?, ?, ?, 0, board_seq.currval, 0, 0 )";
			this.pstmt = this.conn.prepareStatement(query);
			this.pstmt.setString(1, bWirter);
			this.pstmt.setString(2, bTitle);
			this.pstmt.setString(3, bContent);
			result = this.pstmt.executeUpdate();
		} catch (Exception var9) {
			var9.printStackTrace();
		} finally {
			this.close(this.pstmt);
			this.close(this.conn);
		}

		return result;
	}

	public BoardDTO contentView(String strID) {
		this.upHit(strID);
		BoardDTO dto = null;

		try {
			this.conn = this.ds.getConnection();
			String query = "select * from board where bId = ?";
			this.pstmt = this.conn.prepareStatement(query);
			this.pstmt.setInt(1, Integer.parseInt(strID));
			this.rs = this.pstmt.executeQuery();
			if (this.rs.next()) {
				int bId = this.rs.getInt("bId");
				String bWriter = this.rs.getString("bWriter");
				String bTitle = this.rs.getString("bTitle");
				String bContent = this.rs.getString("bContent");
				Timestamp bDate = this.rs.getTimestamp("bDate");
				int bHit = this.rs.getInt("bHit");
				int bGroup = this.rs.getInt("bGroup");
				int bStep = this.rs.getInt("bStep");
				int bIndent = this.rs.getInt("bIndent");
				dto = new BoardDTO(bId, bWriter, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent);
			}
		} catch (Exception var16) {
			var16.printStackTrace();
		} finally {
			this.close(this.rs);
			this.close(this.pstmt);
			this.close(this.conn);
		}

		return dto;
	}

	private void upHit(String bId) {
		try {
			this.conn = this.ds.getConnection();
			String query = "update board set bHit = bHit + 1 where bId = ?";
			this.pstmt = this.conn.prepareStatement(query);
			this.pstmt.setInt(1, Integer.parseInt(bId));
			this.pstmt.executeUpdate();
		} catch (Exception var6) {
			var6.printStackTrace();
		} finally {
			this.close(this.pstmt);
			this.close(this.conn);
		}

	}

	public int modify(String bId, String bWriter, String bTitle, String bContent) {
		int result = 0;

		try {
			this.conn = this.ds.getConnection();
			String query = "update board set bWriter = ?, bTitle = ?, bContent = ? where bId = ?";
			this.pstmt = this.conn.prepareStatement(query);
			this.pstmt.setString(1, bWriter);
			this.pstmt.setString(2, bTitle);
			this.pstmt.setString(3, bContent);
			this.pstmt.setInt(4, Integer.parseInt(bId));
			result = this.pstmt.executeUpdate();
		} catch (Exception var10) {
			var10.printStackTrace();
		} finally {
			this.close(this.pstmt);
			this.close(this.conn);
		}

		return result;
	}

	public int delete(String bId) {
		int result = 0;

		try {
			this.conn = this.ds.getConnection();
			String query = "delete from board where bId = ?";
			this.pstmt = this.conn.prepareStatement(query);
			this.pstmt.setInt(1, Integer.parseInt(bId));
			result = this.pstmt.executeUpdate();
		} catch (Exception var7) {
			var7.printStackTrace();
		} finally {
			this.close(this.pstmt);
			this.close(this.conn);
		}

		return result;
	}

	public BoardDTO replyView(String str) {
		BoardDTO dto = null;

		try {
			this.conn = this.ds.getConnection();
			String query = "select * from board where bId = ?";
			this.pstmt = this.conn.prepareStatement(query);
			this.pstmt.setInt(1, Integer.parseInt(str));
			this.rs = this.pstmt.executeQuery();
			if (this.rs.next()) {
				int bId = this.rs.getInt("bId");
				String bWriter = this.rs.getString("bWriter");
				String bTitle = this.rs.getString("bTitle");
				String bContent = this.rs.getString("bContent");
				Timestamp bDate = this.rs.getTimestamp("bDate");
				int bHit = this.rs.getInt("bHit");
				int bGroup = this.rs.getInt("bGroup");
				int bStep = this.rs.getInt("bStep");
				int bIndent = this.rs.getInt("bIndent");
				dto = new BoardDTO(bId, bWriter, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent);
			}
		} catch (Exception var16) {
			var16.printStackTrace();
		} finally {
			this.close(this.rs);
			this.close(this.pstmt);
			this.close(this.conn);
		}

		return dto;
	}

	public int reply(String bWriter, String bTitle, String bContent, String bGroup, String bStep, String bIndent) {
		int result = 0;
		this.replyShape(bGroup, bStep);

		try {
			this.conn = this.ds.getConnection();
			String query = "insert into board (bId, bWriter, bTitle, bContent, bGroup, bStep, bIndent) values (board_seq.nextval, ?, ?, ?, ?, ?, ?)";
			this.pstmt = this.conn.prepareStatement(query);
			this.pstmt.setString(1, bWriter);
			this.pstmt.setString(2, bTitle);
			this.pstmt.setString(3, bContent);
			this.pstmt.setInt(4, Integer.parseInt(bGroup));
			this.pstmt.setInt(5, Integer.parseInt(bStep) + 1);
			this.pstmt.setInt(6, Integer.parseInt(bIndent) + 1);
			result = this.pstmt.executeUpdate();
		} catch (Exception var17) {
			var17.printStackTrace();
		} finally {
			try {
				if (this.pstmt != null) {
					this.pstmt.close();
				}

				if (this.conn != null) {
					this.conn.close();
				}
			} catch (Exception var16) {
				var16.printStackTrace();
			}

		}

		return result;
	}

	private void replyShape(String strGroup, String strStep) {
		try {
			this.conn = this.ds.getConnection();
			String query = "update board set bStep = bStep + 1 where bGroup = ? and bStep > ?";
			this.pstmt = this.conn.prepareStatement(query);
			this.pstmt.setInt(1, Integer.parseInt(strGroup));
			this.pstmt.setInt(2, Integer.parseInt(strStep));
			this.pstmt.executeUpdate();
		} catch (Exception var12) {
			var12.printStackTrace();
		} finally {
			try {
				if (this.pstmt != null) {
					this.pstmt.close();
				}

				if (this.conn != null) {
					this.conn.close();
				}
			} catch (Exception var11) {
				var11.printStackTrace();
			}

		}

	}
}