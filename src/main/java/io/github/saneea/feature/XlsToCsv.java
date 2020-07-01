package io.github.saneea.feature;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import io.github.saneea.Feature;

public class XlsToCsv implements Feature {

	public static void execute(String inputFileName, String outputFileName, String outputEncoding, String sheetName)
			throws FileNotFoundException, IOException {
		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFileName)); //
				HSSFWorkbook workbook = new HSSFWorkbook(inputStream); //
				Writer outputWriter = new OutputStreamWriter(new BufferedOutputStream(//
						new FileOutputStream(outputFileName)), outputEncoding); //

				ICSVWriter csvWriter = new CSVWriterBuilder(outputWriter).withEscapeChar('\\').build()) {
			HSSFSheet sheet = workbook.getSheet(sheetName);
			for (Row row : sheet) {
				csvWriter.writeNext(rowToStringArray(row));
			}
		}
	}

	private static String[] rowToStringArray(Row row) {
		List<String> strings = new ArrayList<>();
		for (Cell cell : row) {
			strings.add(cell.toString());
		}
		return strings.toArray(new String[strings.size()]);
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			throw new Exception(//
					XlsToCsv.class.getSimpleName() + " usage: " + //
							XlsToCsv.class.getSimpleName() + " <input/file.name> <out/put.filename> <sheet name>");
		}

		execute(args[0], args[1], StandardCharsets.UTF_8.name(), args[2]);
	}

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		Options options = Params.createOptions();

		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine = commandLineParser.parse(options, args);

		String inputFileName = commandLine.getOptionValue(Params.INPUT);
		String outputFileName = commandLine.getOptionValue(Params.OUTPUT);
		String outputEncoding = commandLine.getOptionValue(Params.OUTPUT_ENCODING, StandardCharsets.UTF_8.name());
		String sheetName = commandLine.getOptionValue(Params.SHEET_NAME);

		execute(inputFileName, outputFileName, outputEncoding, sheetName);
	}

	public static class Params {

		public static String INPUT = "input";
		public static String OUTPUT = "output";
		public static String OUTPUT_ENCODING = "outputEncoding";
		public static String SHEET_NAME = "sheetName";

		private static Options createOptions() {
			Options options = new Options()//
					.addOption(Option//
							.builder("i")//
							.longOpt(INPUT)//
							.hasArg(true)//
							.argName("file path")//
							.required(true)//
							.desc("input file path")//
							.build())//
					.addOption(Option//
							.builder("o")//
							.longOpt(OUTPUT)//
							.hasArg(true)//
							.argName("file path")//
							.required(true)//
							.desc("output file path")//
							.build())//
					.addOption(Option//
							.builder("oe")//
							.longOpt(OUTPUT_ENCODING)//
							.hasArg(true)//
							.argName("encoding")//
							.required(false)//
							.desc("output encoding")//
							.build())//
					.addOption(Option//
							.builder("s")//
							.longOpt(SHEET_NAME)//
							.hasArg(true)//
							.argName("sheet name")//
							.required(true)//
							.desc("sheet name for parsing")//
							.build());

			return options;
		}
	}
}
