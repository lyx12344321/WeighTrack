package io.github.weightrack.service;

import io.github.weightrack.mapper.PoundBillMapper;
import io.github.weightrack.mapper.printMapper;
import io.github.weightrack.module.PoundBillModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;

@Service
public class PrintService {

    @Autowired
    PoundBillMapper poundBillMapper;
    @Autowired
    printMapper printMapper;

    public PoundBillModel selectById(int id) {
        PoundBillModel poundBillModel = poundBillMapper.selectById(id);
        int count = printMapper.getTodayPrintedCount();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        String dateStr = dateFormat.format(new Date(System.currentTimeMillis()));

        String countStr = String.format("%04d", count + 1);
        String poundID;
        if (poundBillModel.getIOType().equals("1")) {
            poundID = "R" + dateStr + countStr;
        } else {
            poundID = "C" + dateStr + countStr;
        }
        poundBillModel.setPoundID(poundID);
        poundBillModel.setPrinted(true);
        return poundBillModel;
    }
}
