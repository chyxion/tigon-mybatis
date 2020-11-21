package me.chyxion.tigon.mybatis.mapper;

import java.util.List;
import me.chyxion.tigon.mybatis.Search;
import me.chyxion.tigon.mybatis.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import me.chyxion.tigon.mybatis.entity.User;

/**
 * @author Donghuang
 * @date Nov 15, 2020 22:42:55
 */
@Mapper
public interface UserMapper extends BaseMapper<Integer, User> {

    /**
     * find user by account
     *
     * @param account account
     * @return user found
     */
    default User findByAccount(String account) {
        return find(new Search("account", account));
    }

    /**
     * list user by name
     * @param name name
     * @return users found
     */
    List<User> listByName(@Param("name") String name);
}
