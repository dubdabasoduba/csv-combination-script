import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

public class ReadCsv {

	private static final String[] FILE_HEADER_MAPPING = { "Stock Code", "Item Description", "Strength", "Unit Pack",
			"Unit Price KSH", "Quantity", "Reorder Level", "NOTES" };

	private static DbConnection conn;

	private static DbQueries queries;

	public static void readCSV(String fileName) throws SQLException {
		Connection connection = conn.getConnection();
		queries = new DbQueries(connection);
		FileReader fileReader = null;
		CSVParser csvFileParser = null;
		// Create the CSVFormat object with the header mapping
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
		try {
			fileReader = new FileReader(fileName);
			csvFileParser = new CSVParser(fileReader, csvFileFormat);
			List<CSVRecord> csvRecords = csvFileParser.getRecords();
			int count = 0;
			for (CSVRecord record : csvRecords) {
				if(++count == 1)
					continue;
				String itemCode = record.get("Stock Code");
				System.out.println("stock code = " + itemCode);
				int itemId = queries.retrieveItemIdGivenCode(itemCode);
				System.out.println("Item ID == " + itemId);
				// retrieve item_id from the inv_item_code.
				String name = record.get("Item Description");
				String strength = record.get("Strength");

				if(StringUtils.isNotEmpty(strength)){
					name += " (" + strength  + ")";
				}
				
				/*if(name.contains("Amoxicillin")){
					System.out.println(record);
					System.out.println("Item Code == " + itemCode);
					System.out.println("Update name == " + name);
					System.out.println("Item Id == " + itemId);
					
				}*/
				
				System.out.println("Update name == " + name);
				queries.updateItemName(itemId, name);
				//break;

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
				csvFileParser.close();
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
