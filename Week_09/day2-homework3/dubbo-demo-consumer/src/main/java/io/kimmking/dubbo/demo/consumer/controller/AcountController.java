package io.kimmking.dubbo.demo.consumer.controller;

import io.kimmking.dubbo.demo.consumer.service.TransferService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AcountController {

    @Autowired
    private TransferService transferService;

    @PostMapping("/transfer/{dollarFrz}/{rmbFrz}")
    public String doTransfer(@PathVariable("dollarFrz") int dollarFrz, @PathVariable("rmbFrz") int rmbFrz) throws Exception {
        transferService.doTransfer(dollarFrz, rmbFrz);
        return "ok";
    }


}
