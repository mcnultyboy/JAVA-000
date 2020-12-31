package io.kimmking.dubbo.demo.provider.mapper;

import io.kimmking.dubbo.demo.api.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AccountMapper {

    /**冻结美元*/
    @Update("update account set dollarFrz = dollarFrz + #{dollarFrz},version = version +1, updateTime=now() where dollarAmt >= #{dollarFrz} and id=#{id} and version=#{version}")
    int dollarFrz(Account account);

    /**冻结人民币*/
    @Update("update account set rmbFrz = rmbFrz + #{rmbFrz},version = version +1,updateTime=now() where rmbAmt >= #{rmbFrz} and id=#{id} and version=#{version}")
    int rmbFrz(Account account);

    /**美元确认*/
    @Update("update account set dollarAmt = dollarAmt - #{dollarFrz},rmbAmt= rmbAmt + #{dollarFrz}*7,dollarFrz = dollarFrz -#{dollarFrz},updateTime=now() where version=#{version} and id=#{id}")
    int dollarComfire(Account account);

    /**人名币确认*/
    @Update("update account set rmbAmt = rmbAmt - #{dollarFrz},dollarAmt= dollarAmt + #{rmbFrz}/7,rmbFrz = rmbFrz -#{rmbFrz},updateTime=now() where version=#{version} and id=#{id}")
    int rmbComfire(Account account);

    /**美元取消*/
    @Update("update account set dollarFrz = dollarFrz - #{dollarFrz},version=version -1,updateTime=now() where id=#{id} and version=#{version}")
    int dollarCancel(Account account);

    /**人名币取消*/
    @Update("update account set rmbFrz = rmbFrz - #{rmbFrz},version=version -1,updateTime=now() where id=#{id} and version=#{version}")
    int rmbCancel(Account account);

}
