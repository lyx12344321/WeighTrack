package io.github.weightrack.controller;

import io.github.weightrack.module.PoundBillModel;
import io.github.weightrack.module.User;
import io.github.weightrack.service.CoalTypeService;
import io.github.weightrack.service.PoundBillService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PoundBillController {

    @Autowired
    private PoundBillService poundBillService;

    @Autowired
    private CoalTypeService coalTypeService;

    @PostMapping("/creat")
    public String getForm(
            @RequestParam("IOType") String IOType,
            @RequestParam("coalType") String coalType,
            @RequestParam("plate-number") String plateNumber,
            @RequestParam("gross-weight") String grossWeight,
            @RequestParam("tare") String tare,
            @RequestParam("primary-weight") String primaryWeight,
            @RequestParam("output-unit") String outputUnit,
            @RequestParam("input-unit") String inputUnit,
            @RequestParam("weigher") String weigher,
            @RequestParam("other-coal-type") String otherCoalType,
            HttpServletRequest request) {

        if (coalType.equals("other")) {
            coalType = otherCoalType.strip();
            coalTypeService.insertCoalType(coalType);
        }

        PoundBillModel poundBillModel = PoundBillModel.createPoundBillModel(IOType, coalType, plateNumber, grossWeight, tare, primaryWeight, outputUnit, inputUnit, weigher);
        Object user = request.getSession().getAttribute("user");
        if (user instanceof User) {
            poundBillModel.setCreatorId(((User) user).getId());
        } else {
            return "redirect:/login";
        }

        poundBillService.insertPoundBill(poundBillModel);

        // 返回视图名称
        return "redirect:/";
    }

    @GetMapping("/update/{id}")
    public String getPoundBillById(
            Model model,
            @PathVariable("id") int id) {
        model.addAttribute("poundBillModel", poundBillService.selectById(id));
        model.addAttribute("coalTypes", coalTypeService.getCoalTypes());

        return "update";
    }

    @PostMapping("/update/{id}")
    public String updatePoundBillById(@PathVariable("id") int id,
                                      @RequestParam("IOType") String IOType,
                                      @RequestParam("coalType") String coalType,
                                      @RequestParam("plate-number") String plateNumber,
                                      @RequestParam("gross-weight") String grossWeight,
                                      @RequestParam("tare") String tare,
                                      @RequestParam("primary-weight") String primaryWeight,
                                      @RequestParam("output-unit") String outputUnit,
                                      @RequestParam("input-unit") String inputUnit,
                                      @RequestParam("print-time") String printTime,
                                      @RequestParam("weigher") String weigher) {

        PoundBillModel poundBillModel = PoundBillModel.createPoundBillModel(IOType, coalType, plateNumber, grossWeight, tare, primaryWeight, outputUnit, inputUnit, weigher);

        poundBillService.updateById(poundBillModel, id, printTime);
        return "redirect:/showList/in/1";
    }

    @ResponseBody
    @GetMapping("/delete/{id}")
    public void deletePoundBillById(@PathVariable("id") int id) {
        poundBillService.deleteById(id);
    }
}
