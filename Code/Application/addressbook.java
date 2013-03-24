import java.util.Scanner;
import java.awt.Point;
import java.io.File;
import javax.swing.JOptionPane;

public class addressbook {
	private static Scanner y1,y2; 
	static final int ROWS = 3;
	static final int COLS = 2;
	static String[][] infoTable;
	static String resultHost;
	static String resultIP;
	
	public static void main(String args[]){
	openFile();
	readFile();
	closeFile();
	givenhostFindIP(resultIP);
	givenipFindHost(resultHost);
	}
	public static void openFile(){
		//obviously that file names will need to be changed to match those that will actually be used
		try{
			y1 = new Scanner(new File("host_names1.txt"));
		}catch(Exception e){
			System.out.println("Did not open hostnames file!");
		}
		try{
			y2 = new Scanner(new File("ip_addresses1.txt"));
		}catch(Exception e){
			System.out.println("Did not open IP addresses file!");
		}
	}
	public static void  readFile(){
		/*you can probably comment out everything in the method except for "infoTable =..."
		 * unless you want the system to print out the table
		 */
		String output = "";
		infoTable = new String[][] {{y1.next(),y2.next()},{y1.next(),y2.next()},{y1.next(),y2.next()}};
		for(int row = 0; row < ROWS; row++){
			for(int col = 0; col < COLS; col++){
				output += " " +infoTable[row][col];
			}
			output += "\n";
		}
		JOptionPane.showMessageDialog(null, output);		
	}
	public static void closeFile(){
		y1.close();
		y2.close();
	}
    private static Point find2DIndex(Object[][] infoTable, Object search){
		//don't ask me to explain anything in this method. pure copy and paste
		if(search == null || infoTable == null) return null;
		
		for(int rowIndex = 0; rowIndex < infoTable.length; rowIndex++){
			Object[] row = infoTable[rowIndex];
			if(row != null){
				for(int columnIndex = 0; columnIndex < row.length; columnIndex++){
					if(search.equals(row[columnIndex])){
						return new Point(rowIndex, columnIndex);
					}
				}
			}
		}
		return null;
	}

	public static String givenhostFindIP(String name){

		Point index = find2DIndex(infoTable, name);
		if(index != null){
			String output = "";
			output += " " +infoTable[index.x][1+index.y];
			JOptionPane.showMessageDialog(null, output);//can be omitted
			resultIP = output;
		}else{
			System.out.println("it isn't here");
		}return resultIP;
	}
	public static String givenipFindHost(String ip){
		Point index = find2DIndex(infoTable, ip);
		if(index != null){
			String output = "";
			output += " " +infoTable[index.x][1-index.y];
			JOptionPane.showMessageDialog(null, output);//can be omitted
			resultHost = output;
		}else{
			System.out.println("it isn't here");
		}return resultHost;
	}
	
}
