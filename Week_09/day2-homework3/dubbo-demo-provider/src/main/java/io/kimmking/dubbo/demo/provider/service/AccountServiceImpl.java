package io.kimmking.dubbo.demo.provider.service;

import io.kimmking.dubbo.demo.api.Account;
import io.kimmking.dubbo.demo.api.AccountService;
import io.kimmking.dubbo.demo.provider.mapper.AccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@DubboService(version = "1.0.0")
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;

    @Override
    @HmilyTCC(confirmMethod = "dollarConfirm", cancelMethod = "dollarCancel")
    public Account dollar2Rmb(Account account) {
        log.info("try dollar2Rmb");
        // mapper更新的数据不会自动回填，需要重新查询或者自己封装
        int updateNum = accountMapper.dollarFrz(account);
        if (updateNum == 1){
            account.setVersion(account.getVersion() + 1);
        } else {
            throw new RuntimeException();
        }
        log.info("account in dollar = {}", account);
        return account;
    }

    @Override
    @HmilyTCC (confirmMethod = "rmbConfirm", cancelMethod = "rmbCancel")
    public Account rmb2Dollar(Account account) {
        log.info("try rmb2Dollar");
        int updateNum = accountMapper.rmbFrz(account);
        if (updateNum == 1){
            account.setVersion(account.getVersion() + 1);
        } else {
            throw new RuntimeException();
        }
        log.info("account in rmb = {}", account);
        return account;
    }

    public void dollarConfirm(Account account){
        log.info("dollar confirm");
        accountMapper.dollarComfire(account);
    }

    public void rmbConfirm(Account account){
        log.info("rmb confirm");
        accountMapper.rmbComfire(account);
    }


    public void dollarCancel(Account account){
        log.info("dollar cancel");
        accountMapper.dollarCancel(account);
    }

    public void rmbCancel(Account account){
        log.info("rmb cancel");
        accountMapper.rmbCancel(account);
    }
}
