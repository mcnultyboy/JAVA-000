package io.kimmking.rpcfx.demo.provider;

import io.kimmking.rpcfx.api.RpcfxException;
import io.kimmking.rpcfx.demo.api.User;
import io.kimmking.rpcfx.demo.api.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        if (id == 2) {
            System.out.println("no user for id=" + id);
            throw new RpcfxException("no user for id=2");
        }
        return new User(id, "KK" + System.currentTimeMillis());
    }
}
