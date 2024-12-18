package io.github.weightrack;

import io.github.weightrack.module.PoundBillModel;
import io.github.weightrack.service.PoundBillService;
import io.github.weightrack.utils.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class WeighTrackApplicationTests {

    @Autowired
    PoundBillService poundBillService;

    @Test
    void contextLoads() throws Exception {
        List<PoundBillModel> poundBillModels = ExcelUtil.insertPoundBillByExcel_OUT();
        for (PoundBillModel poundBillModel : poundBillModels) {
            if (poundBillModel.getCoalType() != null) {
                poundBillService.insertPoundBill(poundBillModel);
                log.info("insert into poundBill {}", poundBillModel);
            }
        }
    }

}
