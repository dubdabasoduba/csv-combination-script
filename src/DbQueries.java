import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;

public class DbQueries {

	private Connection conn;
	private static DecimalFormat decimalFormat = new DecimalFormat(".##");

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

	public void updateItem(int itemId, int departmentId, String name, String buyingPrice) {

		try {

			String sql = "update inv_item set name = \""+name+"\", buying_price = "+buyingPrice+", department_id = "+departmentId+" where item_id = "+itemId+"";
			PreparedStatement st = conn.prepareStatement(sql);

			System.out.println("SQL " +sql);

			st.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int insertNewItem(String itemName, int departmentId, String buyingPrice) {
		int itemId = 0;

		try {
			Date date = new Date();
			Double price = null;
			if (StringUtils.isNotEmpty(buyingPrice)) {
				price = Double.valueOf(buyingPrice);
			}
			System.out.println("Insert " + price);

			String uuid = getUuid();
			String sql ="INSERT INTO inv_item (name,department_id,uuid,date_created,creator";
			if (price != null){
				sql += ",buying_price";
			}
			sql += ") VALUES (?,?,?,?,?";
			if (price != null) {
				sql += ","+price;
			}
			sql += ")";
			PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, itemName);
			st.setInt(2, departmentId);
			st.setString(3, uuid);
			st.setDate(4, new java.sql.Date(date.getTime()));
			st.setInt(5, 1);

			st.executeUpdate();
			updateUuid(uuid);
			ResultSet resultSet = st.getGeneratedKeys();
			resultSet.next();
			itemId = resultSet.getInt(1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return itemId;
	}

	public int getDepartmentId (String departmentName) {
		int departmentId = 0;

		try {
			Statement st = conn.createStatement();
			String sql = "select department_id from inv_department where name=\"" + departmentName + "\"";

			ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {
				departmentId = rs.getInt("department_id");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return departmentId;
	}

	public void createItemCode (int itemId, String itemCode) {
		try {
			Date date = new Date();

			String uuid = getUuid();
			String sql ="INSERT INTO inv_item_code (item_id,code,creator,uuid,date_created) VALUES (?,?,?,?,?)";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, itemId);
			st.setString(2, itemCode);
			st.setInt(3, 1);
			st.setString(4, uuid);
			st.setDate(5, new java.sql.Date(date.getTime()));

			st.executeUpdate();

			updateUuid(uuid);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void updateItemCode (int itemId, String itemCode) {
		try {
			String sql = "update inv_item_code set code = ? where item_id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, itemCode);
			st.setInt(2, itemId);
			st.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getItemCode (int itemId) {
		String code = null;

		try {
				Statement st = conn.createStatement();
				String sql = "select code from inv_item_code WHERE item_id = " +itemId;

				ResultSet rs = st.executeQuery(sql);
				if (rs.next()) {
					code = rs.getString("code");
				}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return code;
	}

	public void createItemPrice (int itemId, String itemPrice, String itemPriceName) {
		try {
			String uuid = getUuid();
			Date date = new Date();

			Double price = null;
			System.out.println("Item Price "+itemPrice);
			if (StringUtils.isNotEmpty(itemPrice)) {
				price = Double.valueOf(itemPrice);
			}

			String sql ="INSERT INTO inv_item_price (item_id,price,name,creator,uuid,date_created) VALUES (?,"+price+",?,?,?,?)";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, itemId);
			st.setString(2, itemPriceName);
			st.setInt(3, 1);
			st.setString(4, uuid);
			st.setDate(5, new java.sql.Date(date.getTime()));

			st.executeUpdate();

			updateUuid(uuid);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void updateItemPrice (int itemId, String itemPrice, String itemPriceName) {
		try {
			Integer price = null;
			System.out.println("Item Price "+itemPrice);
			if (StringUtils.isNotEmpty(itemPrice)) {
				price = Integer.valueOf(itemPrice);
			}

			String sql = "update inv_item_price set price = ?, name = ? where item_id = ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, price);
			st.setString(2, itemPriceName);
			st.setInt(3, itemId);
			st.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getUuid (){
		String uuid = null;

		try {
			Statement st = conn.createStatement();
			String sql = "select uuid from generate_uuid WHERE used = 0";

			ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {
				uuid = rs.getString("uuid");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return uuid;
	}

	public void updateUuid (String uuid) {
		try {
			String sql = "update generate_uuid set used = 1 where uuid = '"+uuid+"'";
			PreparedStatement st = conn.prepareStatement(sql);
			st.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int retrieveItemGivenName (String itemName) {
		int itemId = 0;

		try {
			String sql = "select i.* from inv_item i where \n"
					+ "replace(replace(lower(i.name),\"  \", \" \"),\"\\\"\",\"\")  = ?";
			System.out.println("SQL = " + sql);
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, itemName);

			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				itemId = rs.getInt("item_id");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return itemId;
	}
}
