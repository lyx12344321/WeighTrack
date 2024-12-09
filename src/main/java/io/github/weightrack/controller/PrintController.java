package io.github.weightrack.controller;

import io.github.weightrack.service.PoundBillService;
import io.github.weightrack.service.PrintService;
import io.github.weightrack.module.PoundBillModel;
import io.github.weightrack.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Controller
public class PrintController {

    @Autowired
    PrintService printService;

    @Autowired
    PoundBillService poundBillService;

    @GetMapping("/print/{id}")
    public String print(@PathVariable("id") int id, Model model) {
        PoundBillModel poundBillModel = printService.selectById(id);
        model.addAttribute("poundBillModel", poundBillModel);
        return "print";
    }

    @PostMapping("/print/{id}")
    public String printWork(@PathVariable("id") int id, @RequestParam("update-print-time") String updatePrintTime, Model model) {
        LocalDateTime now = LocalDateTime.now();
        PoundBillModel poundBillModel = printService.selectById(id);
        if (updatePrintTime.isEmpty()) {
            if (poundBillModel.getPrintTime() == null) {
                poundBillModel.setPrintTime(now);
            }
        } else {
            if (updatePrintTime.contains("：")) {
                updatePrintTime = updatePrintTime.replace("：", ":");
            }
            String currentDateTimeString = now.toLocalDate() + " " + updatePrintTime;
            LocalDateTime dateTime = LocalDateTime.parse(currentDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            poundBillModel.setPrintTime(dateTime);
            log.info("id: {} update print time to {}", poundBillModel.getId(), poundBillModel.getPrintTime());
        }

        String[] data = new String[11];
        data[0] = now.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
        data[1] = poundBillModel.getPoundID();
        data[2] = poundBillModel.getPlateNumber();
        data[3] = poundBillModel.getCoalType();
        data[4] = poundBillModel.getOutputUnit();
        data[5] = poundBillModel.getInputUnit();
        data[6] = String.valueOf(poundBillModel.getGrossWeight());
        data[7] = String.valueOf(poundBillModel.getTareWeight());
        data[8] = String.valueOf(poundBillModel.getNetWeight());
        data[9] = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        data[10] = poundBillModel.getWeigher();

        BufferedImage image = ImageUtil.createImage(data);
        ImageUtil.printRun(image);
        poundBillModel.setPrinted(true);
        poundBillService.updateById(poundBillModel, poundBillModel.getId(), null);

        model.addAttribute("message", "已请求打印任务");
        model.addAttribute("poundBillModel", poundBillModel);
        return "print";
    }
}
