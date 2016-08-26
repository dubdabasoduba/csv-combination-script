import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReadCsv {

	private static final String[] FILE_HEADER_MAPPING = { "Department","Codes","Name","Buying Price","Cash","NHIF Standard",
			"NHIF Civil Servant","Insurance","Transfer" };

	private static DbConnection conn;

	private static DbQueries queries;

	public static void readCSV(String fileName) throws SQLException {
		Connection connection = conn.getConnection();
		queries = new DbQueries(connection);
		FileReader fileReader = null;
		CSVParser csvFileParser = null;
		// Create the CSVFormat object with the header mapping
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
		int errorCount = 0;
		int count = 0,total = 0;
		try {
			fileReader = new FileReader(fileName);
			csvFileParser = new CSVParser(fileReader, csvFileFormat);
			List<CSVRecord> csvRecords = csvFileParser.getRecords();
			for (CSVRecord record : csvRecords) {

				//total++;
				if (++total == 1)
					continue;
				String itemName = record.get("Name").toLowerCase();
				System.out.println("Item Name = " + itemName);
				itemName = itemName.replaceAll("\"","");
				System.out.println("Trimmed Item Names = " +itemName.trim());
				int itemId = queries.retrieveItemGivenName(itemName.trim());

				if (itemId > 0) {
					count++;
					updateItem(record,itemId);
				} else {
					createItem(record);
					//break;
				}
				//System.out.println("Item ID == " + itemId);
				// retrieve item_id from the inv_item_code.
				/*String name = record.get("Item Description");
				String strength = record.get("Strength");

				if (StringUtils.isNotEmpty(strength)) {
					name += " (" + strength + ")";
				}*/
				
				/*if(name.contains("Amoxicillin")){
					System.out.println(record);
					System.out.println("Item Code == " + itemCode);
					System.out.println("Update name == " + name);
					System.out.println("Item Id == " + itemId);
					
				}*/

				/*System.out.println("Update name == " + name);
				queries.updateItemName(itemId, name);*/
				//break;
			}


		} catch (Exception e) {
			errorCount++;
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

		System.out.println("Found " + count);
		System.out.println("Total " + total);
		System.out.println("Error  " + errorCount);
	}

	private static void createItem(CSVRecord record) {
		try{
			String departmentName  = record.get("Department");
			int departmentId = queries.getDepartmentId(departmentName);

			System.out.println("Insert" +departmentId);

			String name = record.get("Name").trim().replace("\"","\\\"");
			System.out.println("The escaped name = " +name);

			int itemId = queries.insertNewItem(name,departmentId, record.get("Buying Price"));
			String codeItem = record.get("Codes");
			String itemCode = queries.getItemCode(itemId);

			if (itemCode != null) {
				if (!codeItem.equalsIgnoreCase(itemCode)){
					queries.updateItemCode(itemId,codeItem);
				}
			} else if (codeItem != null) {
				queries.createItemCode(itemId, codeItem);
			}

			String cashValue = record.get("Cash");
			String nhifStandardValue = record.get("NHIF Standard");
			String nhifCivilValue = record.get("NHIF Civil Servant");
			String insuranceValue = record.get("Insurance");
			String transferValue = record.get("Transfer");

			if (cashValue != null && StringUtils.isNotEmpty(cashValue)) {
				queries.createItemPrice(itemId,cashValue,"Cash");
			}

			if (nhifStandardValue != null && StringUtils.isNotEmpty(nhifStandardValue)) {
				queries.createItemPrice(itemId,nhifStandardValue,"NHIF Standard");
			}

			if (nhifCivilValue != null && StringUtils.isNotEmpty(nhifCivilValue)) {
				queries.createItemPrice(itemId,nhifCivilValue,"NHIF Civil Servant");
			}

			if (insuranceValue != null && StringUtils.isNotEmpty(insuranceValue)) {
				queries.createItemPrice(itemId,insuranceValue,"Insurance");
			}

			if (transferValue != null && StringUtils.isNotEmpty(transferValue)) {
				queries.createItemPrice(itemId,transferValue,"Transfer");
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	private static void  updateItem(CSVRecord record, Integer itemId) {
		try{
			String departmentName  = record.get("Department");
			int departmentId = queries.getDepartmentId(departmentName);

			String buyingPrice = record.get("Buying Price");
			String price = null;

			if (StringUtils.isNotEmpty(buyingPrice)) {
				price = buyingPrice;
			}

			String name = record.get("Name").trim().replace("\"","\\\"");
			System.out.println("The escaped name = " +name);

			queries.updateItem(itemId,departmentId,name, price);
			String codeItem = record.get("Codes");
			String itemCode = queries.getItemCode(itemId);

			if (itemCode != null) {
				if (!codeItem.equalsIgnoreCase(itemCode)){
					queries.updateItemCode(itemId,codeItem);
				}
			} else if (codeItem != null) {
				queries.createItemCode(itemId, codeItem);
			}

			String cashValue = record.get("Cash");
			String nhifStandardValue = record.get("NHIF Standard");
			String nhifCivilValue = record.get("NHIF Civil Servant");
			String insuranceValue = record.get("Insurance");
			String transferValue = record.get("Transfer");

			if (cashValue != null && StringUtils.isNotEmpty(cashValue)) {
				queries.createItemPrice(itemId,cashValue,"Cash");
			}

			if (nhifStandardValue != null && StringUtils.isNotEmpty(nhifStandardValue)) {
				queries.createItemPrice(itemId,nhifStandardValue,"NHIF Standard");
			}

			if (nhifCivilValue != null && StringUtils.isNotEmpty(nhifCivilValue)) {
				queries.createItemPrice(itemId,nhifCivilValue,"NHIF Civil Servant");
			}

			if (insuranceValue != null && StringUtils.isNotEmpty(insuranceValue)) {
				queries.createItemPrice(itemId,insuranceValue,"Insurance");
			}

			if (transferValue != null && StringUtils.isNotEmpty(transferValue)) {
				queries.createItemPrice(itemId,transferValue,"Transfer");
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
