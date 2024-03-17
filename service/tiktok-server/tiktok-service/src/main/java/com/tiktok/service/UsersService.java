package com.tiktok.service;

import com.tiktok.bo.UpdatedUserBO;
import com.tiktok.pojo.Users;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 针对User表的service层接口.
 *
 * @author lzc
 * @version 1.0
 */
public interface UsersService {
    /**
     * 判断该手机号是否存在.
     *
     * @param mobile 用户手机号
     * @return 存在则返回用户信息
     */
     Users queryIsMobileExist(String mobile);

    /**
     * 判断该视频号是否存在.
     *
     * @param account 用户视频号
     * @return 存在则返回用户信息
     */
     Users queryIsAccountExist(String account);

    /**
     * 根据手机号创建用户.
     *
     * @param mobile 用户手机号
     * @return 用户信息
     */
     Users createUser(String mobile);

    /**
     * 根据主键获取用户信息.
     *
     * @param userId 主键
     * @return 用户信息
     */
     Users getUser(String userId);

    /**
     * 用户信息修改.
     *
     * @param updatedUserBO 更新的用户信息
     * @return 用户信息
     */
    Users updateUserInfo(UpdatedUserBO updatedUserBO);

    /**
     * 用户信息修改, 指定修改类型.
     *
     * @param updatedUserBO 更新的用户信息
     * @param type 要修改的类型
     * @return 用户信息
     */
    Users updateUserInfo(UpdatedUserBO updatedUserBO, Integer type);
}