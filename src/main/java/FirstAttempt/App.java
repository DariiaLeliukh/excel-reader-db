package FirstAttempt;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class App
{

    public static void main( String[] args ) throws Exception{
        try {
            String userDir = System.getProperty("user.dir");
            String filePath = userDir + File.separator + "Inventory.xlsx";

            //Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dariial?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "dariiaL", "Machaon1");
            System.out.println("Successfully connected to the database!");

            FileInputStream fis = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            CellReference cellReference = new CellReference("A1");
            Row row = sheet.getRow(cellReference.getRow());
            Cell cell = row.getCell(cellReference.getCol());
            int amountOfRows = sheet.getPhysicalNumberOfRows();
            System.out.println("Amount of rows in file: " + amountOfRows);


            Statement statement = con.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM item");
            results.next();
            int data = results.getInt("barcode");
            System.out.println("Last barcode is: " + data);
            statement.close();

            int barcode = data-1;    // last barcode in DB
            PreparedStatement ps = con.prepareStatement("insert into item() values(?, ?, ?, ?)");
            String imagePath = userDir + File.separator + "images" +File.separator + "merry-christmas.jpg";

            //List of Images
            List lst = workbook.getAllPictures();
            int imageCount = 0;
            for (Iterator it = lst.iterator(); it.hasNext(); ) {
                PictureData pict = (PictureData)it.next();
                String ext = pict.suggestFileExtension();
                byte[] dataPic = pict.getData();
                System.out.println("EXT " + ext);
                if (ext.equals("jpg")){
                    FileOutputStream out = new FileOutputStream("pict"+ imageCount +"."+ ext);
                    out.write(dataPic);
                    out.close();
                    imageCount++;
                }
            }
            System.out.println("Amount of pictures: " + lst.size() + " and imageCount is " + imageCount);


            for (int i = 0; i < amountOfRows; i++) {
                row = sheet.getRow(i);
                if(row != null) {
                        InputStream img = new FileInputStream(new File(imagePath));
                        //InputStream img = new FileInputStream();
                        ps.setInt(1, barcode--);
                        ps.setString(2, row.getCell(1).toString());
                        ps.setString(3, row.getCell(2).toString());
                        ps.setBlob(4, img); //HOW TO GET AN IMAGE FROM EXCEL AS AN OBJECT?

                        ps.executeUpdate();
                        img = null;
                }
            }
            fis.close();
            ps.close();
            con.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
        JOptionPane.showMessageDialog(null, "Data Inserted");
    }
}
