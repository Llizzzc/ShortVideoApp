package com.tiktok.service;

import com.tiktok.utils.PagedGridResult;

/**
 * 针对fans表的service层接口.
 *
 * @author lzc
 * @version 1.0
 */
public interface FansService {

    /**
     * 关注.
     *
     * @param myId 当前用户id
     * @param vlogerId 视频发布者id
     */
     void doFollow(String myId, String vlogerId);

    /**
     * 取关.
     *
     * @param myId 当前用户id
     * @param vlogerId 视频发布者id
     */
     void doCancel(String myId, String vlogerId);


    /**
     * 查询用户是否关注博主.
     *
     * @param myId 当前用户id
     * @param vlogerId 视频发布者id
     * @return 关注返回true, 否则false
     */
     boolean queryDoIFollowVloger(String myId, String vlogerId);

    /**
     * 查询我关注的博主列表.
     *
     * @param vlogerId 博主id
     * @param currentUserId 当前用户id
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页信息
     */
     PagedGridResult queryMyFollows(String vlogerId, String currentUserId, Integer page, Integer pageSize);

    /**
     * 查询我的粉丝列表.
     *
     * @param vlogerId 博主id
     * @param currentUserId 当前用户id
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页信息
     */
     PagedGridResult queryMyFans(String vlogerId, String currentUserId, Integer page, Integer pageSize);
}