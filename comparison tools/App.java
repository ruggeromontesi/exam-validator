import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;

public class App{
	public static void main(String[] args) throws IOException {
		
		//i = 67, ..., 83
		BufferedWriter writer = new BufferedWriter(new FileWriter("formule-excel-hor.txt"));
		for(int s1 = 1; s1 < 62; s1++){
			for(int s2 = 1; s2 < 63 && s2!= s1; s2++){
				String str = "=";
				for(int i = 67; i<83; i++) {
					//System.out.println((char)i);
					char ch = (char)i;
					str += "IF(" + "$" + ch + s1 + "=$" + ch + s2 + ",1,0)";
					if(i != 82) {
						str += "+";
					}
				}
				writer.write("Studentas" + s1 + "----" + "Studentas" + s2 + "\n");
				writer.write(str + "\n\n");
				
			}
			writer.write("\n\n\n\n");
			
		}

		
		//System.out.println(str);
		
		//writer.write(str + "\n");
		writer.close();
	}
}