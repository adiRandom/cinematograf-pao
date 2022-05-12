package services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CSVService<T> {
    private final String path;

    public CSVService(String fileName) {
        this.path = SerializationService.getFilePath(fileName);
    }

    public void write(List<T> items, String[] ignoredProperties) throws IOException {
        if (items.size() == 0) {
            return;
        }
        FileWriter out = new FileWriter(this.path);
        List<String> headersList = new LinkedList<>();

        // Get all the fields if the class chain util Object
        Class currentClass = items.get(0).getClass();
        while (currentClass != Object.class) {
            Arrays.stream(currentClass.getDeclaredFields()).map(field -> {
                        field.setAccessible(true);
                        return field.getName();
                    })
                    // Filter out all the unwanted properties
                    .filter(fieldName -> Arrays.binarySearch(ignoredProperties, fieldName) < 0)
                    .forEach(fieldName -> headersList.add(fieldName));
            currentClass = currentClass.getSuperclass();
        }


        String[] headers = new String[headersList.size()];
        headersList.toArray(headers);

        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers))) {
            // Get all the values of each object for each field and print them to the CSV file
            for (T item : items) {
                List<String> fieldValuesList = new LinkedList<>();
                // Recursively get all the fields values for the entire class chain
                currentClass = item.getClass();
                while (currentClass != Object.class) {
                    Arrays.stream(currentClass.getDeclaredFields())
                            // Filter out all the unwanted properties
                            .filter(field -> {
                                field.setAccessible(true);
                                String fieldName = field.getName();
                                return Arrays.binarySearch(ignoredProperties, fieldName) < 0;
                            })
                            .map(field -> {
                                try {
                                    return field.get(item).toString();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                return "";
                            }).forEach(fieldName -> fieldValuesList.add(fieldName));
                    currentClass = currentClass.getSuperclass();
                }
                String[] fieldValues = new String[fieldValuesList.size()];
                fieldValuesList.toArray(fieldValues);
                printer.printRecord(fieldValues);
            }
        }
        out.close();
        System.out.println("Report saved in " + this.path);
    }


}
