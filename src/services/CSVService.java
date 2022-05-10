package services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class CSVService {
    private final String path;

    CSVService(String path) {
        this.path = path;
    }

    public void write(List<Map<String,String>> items) throws IOException {
        if (items.size() == 0) {
            return;
        }
        FileWriter out = new FileWriter(this.path);
        String[] headers = (String[]) items.get(0).keySet().toArray();
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers))) {
            for (var item : items) {
                var fieldsList = item.values();
                printer.printRecord(fieldsList);
            }

        }
        out.close();
    }

    public List<Map<String,String>> read(String[] headers) throws IOException {
        var result = new ArrayList<Map<String,String>>();
        Reader in = new FileReader(this.path);

        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(headers)
                .withFirstRecordAsHeader()
                .parse(in);
        for (var record : records) {
            var parsedRecord = new HashMap<String, String>();
            for (var header : headers) {
                parsedRecord.put(header, record.get(header));
            }
            result.add(parsedRecord);
        }

        return result;
    }

}
