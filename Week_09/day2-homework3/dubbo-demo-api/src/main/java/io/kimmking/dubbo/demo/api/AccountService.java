package io.kimmking.dubbo.demo.api;

import org.dromara.hmily.annotation.Hmily;

/***
 * 账户服务
 *
 * @auther yb
 * @date 2020/12/29 11:46
 */
public interface AccountService {

    /**美元兑换人民币*/
    @Hmily
    Account dollar2Rmb(Account account);
    /**人民币兑换美元*/
    @Hmily
    Account rmb2Dollar(Account account);
}
