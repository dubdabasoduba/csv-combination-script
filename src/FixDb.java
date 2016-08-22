import java.sql.SQLException;

public class FixDb {

	public static void main(String[] args) {
		System.out.println("Run fix..");
		String fileName = "/Users/macbookpro/Documents/Kapsowar/item_price.csv";
		ReadCsv reader = new ReadCsv();
		try {
			reader.readCSV(fileName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
