import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbQueries {

	private Connection conn;

	public DbQueries(Connection connection) {
		conn = connection;
	}

	public int retrieveItemIdGivenCode(String itemCode) {
		int itemId = 0;

		try {
			Statement st = conn.createStatement();
			String sql = "select item_id from inv_item_code where code = '" + itemCode + "'";

			ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {
				itemId = rs.getInt("item_id");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return itemId;
	}

	public void updateItemName(int itemId, String name) {
		try {
			String sql = "update inv_item set name = ? where item_id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, name);
			st.setInt(2, itemId);

			st.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
