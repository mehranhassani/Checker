
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;



public class Main {
	//use ASTParse to parse string
	static int OvercatchesCopunt=0;
	static int FixmeCount=0;
	static int EmptyCount=0;

	public static void parse(String str) 
	{
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final ArrayList <CatchClause> catches =new ArrayList<>();
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() 
		{
			public boolean visit(CatchClause mycatch) 
			{  
				//System.out.println(mycatch.toString());
				catches.add(mycatch);
				return super.visit(mycatch);
			}

		});


		for (CatchClause c : catches){
			if (c.getException().getType().toString().equalsIgnoreCase("Exception")){
				System.out.println("Overcatch="+c.toString());
				OvercatchesCopunt++;
			}

			if (c.getBody().statements().isEmpty()){
				System.out.println("Empty Statment="+c.toString());
				EmptyCount++;
			}

			int start = c.getStartPosition();
			int end = start + c.getLength();
			String catchstring = str.substring(start, end);
			if (catchstring.toLowerCase().contains("todo")){
				System.out.println("Todo or FIXME=" + c.toString());
				FixmeCount++;
			}
		}


	}
	public static String parseOneFile(String str) 
	{
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final ArrayList <CatchClause> catches =new ArrayList<>();
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() 
		{
			public boolean visit(CatchClause mycatch) 
			{  
				//System.out.println(mycatch.toString());
				catches.add(mycatch);
				return super.visit(mycatch);
			}

		});

		String out = new String();
		out = "";
		for (CatchClause c : catches){

			out += c.getException().getType().toString()+"<Split>";
			out += c.getBody().toString();
			out +="<Next>";

		}
		return out;

	}

	//read file content into a string
	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return  fileData.toString();	
	}

	//loop directory to get file list
	public static void ParseFilesInDir(String path) throws IOException{
		String dirPath = path;
		ArrayList<File> files = new ArrayList<>();
		listf(dirPath,files);
		String filePath = null;
		for (File f : files ) {
			filePath = f.getAbsolutePath();
			if(f.isFile()){
				String c =readFileToString(filePath);
				System.out.println(parseOneFile(c));
			}
		}

	}

	public static void listf(String directoryName, ArrayList<File> files) {
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()&&file.getName().contains(".java")) {
				files.add(file);
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), files);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		//		ParseFilesInDir(args[0]);
		parseOneFile(args[0]);
	}

}
