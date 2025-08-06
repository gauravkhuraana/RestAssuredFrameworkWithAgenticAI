package com.api.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Test data utility for reading data from various sources
 */
public class TestDataUtils {
    private static final Logger logger = LoggerFactory.getLogger(TestDataUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Read JSON test data from file
     */
    public static <T> T readJsonTestData(String filePath, Class<T> clazz) {
        try (InputStream inputStream = getResourceAsStream(filePath)) {
            return objectMapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            logger.error("Error reading JSON test data from {}: {}", filePath, e.getMessage(), e);
            throw new RuntimeException("Error reading JSON test data", e);
        }
    }

    /**
     * Read JSON test data as List
     */
    public static <T> List<T> readJsonTestDataAsList(String filePath, Class<T> clazz) {
        try (InputStream inputStream = getResourceAsStream(filePath)) {
            return objectMapper.readValue(inputStream, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            logger.error("Error reading JSON test data list from {}: {}", filePath, e.getMessage(), e);
            throw new RuntimeException("Error reading JSON test data list", e);
        }
    }

    /**
     * Read JSON test data as Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> readJsonTestDataAsMap(String filePath) {
        try (InputStream inputStream = getResourceAsStream(filePath)) {
            return objectMapper.readValue(inputStream, Map.class);
        } catch (IOException e) {
            logger.error("Error reading JSON test data map from {}: {}", filePath, e.getMessage(), e);
            throw new RuntimeException("Error reading JSON test data map", e);
        }
    }

    /**
     * Read CSV test data
     */
    public static List<Map<String, String>> readCsvTestData(String filePath) {
        List<Map<String, String>> data = new ArrayList<>();
        
        try (InputStream inputStream = getResourceAsStream(filePath);
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {
            
            for (CSVRecord record : parser) {
                Map<String, String> row = new HashMap<>();
                for (String header : parser.getHeaderNames()) {
                    row.put(header, record.get(header));
                }
                data.add(row);
            }
            
            logger.info("Read {} rows from CSV file: {}", data.size(), filePath);
            return data;
            
        } catch (IOException e) {
            logger.error("Error reading CSV test data from {}: {}", filePath, e.getMessage(), e);
            throw new RuntimeException("Error reading CSV test data", e);
        }
    }

    /**
     * Read Excel test data
     */
    public static List<Map<String, Object>> readExcelTestData(String filePath, String sheetName) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        try (InputStream inputStream = getResourceAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }
            
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Header row not found in sheet: " + sheetName);
            }
            
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValueAsString(cell));
            }
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, Object> rowData = new HashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        String header = headers.get(j);
                        Object value = getCellValue(cell);
                        rowData.put(header, value);
                    }
                    data.add(rowData);
                }
            }
            
            logger.info("Read {} rows from Excel file: {}, sheet: {}", data.size(), filePath, sheetName);
            return data;
            
        } catch (IOException e) {
            logger.error("Error reading Excel test data from {}: {}", filePath, e.getMessage(), e);
            throw new RuntimeException("Error reading Excel test data", e);
        }
    }

    /**
     * Read Excel test data from first sheet
     */
    public static List<Map<String, Object>> readExcelTestData(String filePath) {
        try (InputStream inputStream = getResourceAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            String sheetName = sheet.getSheetName();
            logger.info("Reading data from first sheet: {}", sheetName);
            
        } catch (IOException e) {
            logger.error("Error getting first sheet from Excel file: {}", filePath, e);
            throw new RuntimeException("Error reading Excel file", e);
        }
        
        return readExcelTestData(filePath, getFirstSheetName(filePath));
    }

    /**
     * Get first sheet name from Excel file
     */
    private static String getFirstSheetName(String filePath) {
        try (InputStream inputStream = getResourceAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            return workbook.getSheetAt(0).getSheetName();
            
        } catch (IOException e) {
            logger.error("Error getting first sheet name from Excel file: {}", filePath, e);
            throw new RuntimeException("Error reading Excel file", e);
        }
    }

    /**
     * Get cell value as Object
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return null;
        }
    }

    /**
     * Get cell value as String
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }

    /**
     * Get resource as InputStream
     */
    private static InputStream getResourceAsStream(String filePath) {
        InputStream inputStream = TestDataUtils.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) {
            inputStream = TestDataUtils.class.getClassLoader().getResourceAsStream("testdata/" + filePath);
        }
        if (inputStream == null) {
            throw new RuntimeException("Test data file not found: " + filePath);
        }
        return inputStream;
    }

    /**
     * Write test data to JSON file
     */
    public static void writeJsonTestData(String filePath, Object data) {
        try {
            File file = new File("src/test/resources/testdata/" + filePath);
            file.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            logger.info("Test data written to: {}", file.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Error writing JSON test data to {}: {}", filePath, e.getMessage(), e);
            throw new RuntimeException("Error writing JSON test data", e);
        }
    }

    /**
     * Generate random test data
     */
    public static Map<String, Object> generateRandomTestData() {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();
        
        data.put("id", random.nextInt(1000) + 1);
        data.put("name", "Test User " + random.nextInt(100));
        data.put("email", "testuser" + random.nextInt(100) + "@example.com");
        data.put("active", random.nextBoolean());
        data.put("timestamp", System.currentTimeMillis());
        
        return data;
    }
}
