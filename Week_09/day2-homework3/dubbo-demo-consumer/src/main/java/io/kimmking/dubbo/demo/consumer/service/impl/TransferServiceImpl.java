package io.kimmking.dubbo.demo.consumer.service.impl;

import io.kimmking.dubbo.demo.api.Account;
import io.kimmking.dubbo.demo.api.AccountService;
import io.kimmking.dubbo.demo.consumer.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

    @DubboReference(version = "1.0.0")
    private AccountService accountService;

    @Override
    @HmilyTCC
    @Transactional
    public String doTransfer(int dollarFrz, int rmbFrz) throws Exception {
        log.info("do Transfer");
        Account zhangsan = Account.builder()
                .id("1")
                .name("zhangsan")
                .dollarFrz(dollarFrz)
                .version(1)
                .build();
        Account zhangsanAfter = accountService.dollar2Rmb(zhangsan);

        Account lisi = Account.builder()
                .id("2")
                .name("lisi")
                .rmbFrz(rmbFrz)
                .version(1)
                .build();
        Account lisiAfter = accountService.rmb2Dollar(lisi);
        log.info("lisi version, before={},after={}", lisi.getVersion(), lisiAfter.getVersion());

        // 此处的异常只能用来回滚本地事务，远程事务的回滚只能在远程的try中try中抛出异常才能执行cancel函数。
        if (zhangsanAfter.getVersion() != zhangsan.getVersion()+1
            || lisiAfter.getVersion() != lisi.getVersion() +1) {
            throw new Exception("transfer failed======");
        }
        return "ok";
    }
}
