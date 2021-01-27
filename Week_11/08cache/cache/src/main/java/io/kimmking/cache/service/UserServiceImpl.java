package io.kimmking.cache.service;

import io.kimmking.cache.entity.User;
import io.kimmking.cache.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    // 开启spring cache
    @Cacheable(key="#id",value="userCache")
    public User find(int id) {
        System.out.println(" ==> find " + id);
        return userMapper.find(id);
    }

    // 开启spring cache
    @Cacheable (key="methodName",value="userCache")
    public List<User> list(){
        System.out.println("user/list");
        return userMapper.list();
    }

    @Cacheable (key="methodName",value="userCache")
    public List<User> list1(){
        System.out.println("user/list1");
        return userMapper.list();
    }

    @Override
    // @CachePut 只会更新 userCache::user.id的缓存，不会更新userCache::list的缓存
//    @CachePut(key="#user.id",value = "userCache")
    @CacheEvict(allEntries = true,value = "userCache") // 表示对 userCache 这个域的所有key都失效， 相同名字的 域只能在同一个类中，否则会失效
    public User update(User user) {
        userMapper.update(user);
        User user1 = userMapper.find(user.getId());
        return user1;
    }

}
