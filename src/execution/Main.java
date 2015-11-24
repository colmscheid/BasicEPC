package execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import model.EPC_Element;
import model.Warning;
import converter.ConverterException;
import converter.Parser;
import converter.EPMLGenerator;
import converter.Lexer;

public class Main {

	public static void main(String[] args) throws IOException, ConverterException {

		/* Read from the given file */
		String path = args[0];
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(path));
		} catch (Exception e) {
			System.out.println("Die angegebene Datei kann nicht gefunden werden!");
			return;
		}

		/* Create EPC_Element */
		Lexer lexer = new Lexer(reader);
		Parser parser = new Parser(lexer);
		ArrayList<Warning> warnings = parser.getWarnings();
		EPC_Element start = parser.parse();
		
		/* Create a EPML File */
		if (path.endsWith(".txt"))
			path = path.substring(0, path.length()-4);
		if (path.endsWith(".bepc"))
			path = path.substring(0, path.length()-5);
		if (! path.endsWith(".epml"))
			path += ".epml";
		File output = new File(path);
		
		/* Name of the EPC */
		String[] splits = path.split("\\\\");
		String name = splits[splits.length - 1];
		splits = name.split("/");
		name = splits[splits.length - 1];
		name = name.substring(0, name.length() - 5);
		
		/* Write EPML File */
		EPMLGenerator generator = new EPMLGenerator();
		FileWriter writer = new FileWriter(output);
		generator.generateEPMLFile(start, name, writer);
		writer.close();
		
		/* Print warnings */
		for (Warning warning : warnings)
			System.out.println(warning);

		reader.close();
	}

}