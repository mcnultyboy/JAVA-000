package io.kimmking.rpcfx.demo.api;

import io.kimmking.rpcfx.proxy.RpcfxService;

@RpcfxService
public interface UserService {

    User findById(int id);

}
