package io.github.weightrack.utils;

import io.github.weightrack.module.PoundBillModel;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    public static List<PoundBillModel> insertPoundBillByExcel() throws Exception {
        //  NUMERIC	 STRING	 STRING	 STRING	 NUMERIC(毛重)	 NUMERIC	 FORMULA	 NUMERIC	 FORMULA	 NUMERIC	 STRING	 STRING	 STRING	 BLANK
        List<PoundBillModel> poundBillModels = new ArrayList<>();

        FileInputStream fileIn = new FileInputStream("C:\\Users\\34696\\Desktop\\鸿聚一号煤场库存出货台账.xlsx");
        Workbook workbook = new XSSFWorkbook(fileIn);
        Sheet sheet = workbook.getSheet("入库明细");

        int length = sheet.getLastRowNum();
        System.out.println("sheet长度：" + length);
        for (int i = 10; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            PoundBillModel poundBillModel = new PoundBillModel();
            if (row.getCell(0) == null) {
                continue;
            }
            if (row.getCell(0).getCellType().equals(CellType.NUMERIC)) {
                int excelDate = (int) row.getCell(0).getNumericCellValue();
                LocalDate baseDate = LocalDate.of(1900, 1, 1);
                long daysToAdd = (long) excelDate - 2;
                LocalDate date = baseDate.plusDays(daysToAdd);

                double printTime;
                try {
                    printTime = row.getCell(9).getNumericCellValue();
                } catch (Exception e) {
                    continue;
                }
                LocalDateTime localDateTime = getLocalDateTime(printTime, date);
                poundBillModel.setCreatTime(localDateTime);
                poundBillModel.setPrintTime(localDateTime);
                poundBillModel.setModifyTime(localDateTime);

                String poundID = row.getCell(1).getStringCellValue();
                poundBillModel.setPoundID(poundID);

                poundBillModel.setCoalType(row.getCell(2).getStringCellValue());
                poundBillModel.setPlateNumber(row.getCell(3).getStringCellValue());

                boolean grossFlag = false;
                boolean tareFlag = false;
                boolean primaryFlag = false;

                try {
                    poundBillModel.setGrossWeight((float) row.getCell(4).getNumericCellValue());
                    grossFlag = true;
                } catch (Exception e) {

                }
                try {
                    poundBillModel.setTareWeight((float) row.getCell(5).getNumericCellValue());
                    tareFlag = true;
                } catch (Exception e) {

                }
                try {
                    poundBillModel.setPrimaryWeight((float) row.getCell(7).getNumericCellValue());
                    primaryFlag = true;
                } catch (Exception e) {

                }

                if (grossFlag && tareFlag) {
                    poundBillModel.setNetWeight(poundBillModel.getGrossWeight() - poundBillModel.getTareWeight());

                    if (primaryFlag) {
                        poundBillModel.setProfitLossWeight(poundBillModel.getNetWeight() - poundBillModel.getPrimaryWeight());
                    }
                }
                if (row.getCell(10) != null) {
                    poundBillModel.setOutputUnit(row.getCell(10).getStringCellValue());
                }
                if (row.getCell(11) != null) {
                    poundBillModel.setInputUnit(row.getCell(11).getStringCellValue());
                }
                if (row.getCell(12) != null) {
                    poundBillModel.setWeigher(row.getCell(12).getStringCellValue());
                }

                poundBillModel.setIOType("1");
                poundBillModel.setPrinted(true);
            }
            poundBillModels.add(poundBillModel);
        }

        workbook.close();
        fileIn.close();
        return poundBillModels;
    }

    @NotNull
    private static LocalDateTime getLocalDateTime(double printTime, LocalDate date) {
        double timePart = printTime - Math.floor(printTime);  // 获取小数部分

        // 将小数部分转换为 LocalTime
        int hours = (int) (timePart * 24); // 计算小时
        int minutes = (int) ((timePart * 24 * 60) % 60); // 计算分钟
        int seconds = (int) ((timePart * 24 * 3600) % 60); // 计算秒

        // 创建 LocalTime 实例
        LocalTime localTime = LocalTime.of(hours, minutes, seconds);

        return LocalDateTime.of(date, localTime);
    }
}