package com.tiktok.service;

import com.tiktok.bo.VlogBO;
import com.tiktok.pojo.Vlog;
import com.tiktok.utils.PagedGridResult;
import com.tiktok.vo.IndexVlogVO;

/**
 * 针对表vlog的service接口.
 *
 * @author lzc
 * @version 1.0
*/
public interface VlogService {

    /**
     * 新增vlog视频.
     *
     * @param vlogBO 短视频对象
     */
    void createVlog(VlogBO vlogBO);

    /**
     * 获取短视频.
     *
     * @param vlogId 短视频id
     * @return 短视频对象
     */
    Vlog getVlog(String vlogId);

    /**
     * 查询首页, 搜索的vlog列表.
     *
     * @param userId 用户id
     * @param search 搜索条件
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页信息
     */
    PagedGridResult getIndexVlogList(String userId, String search, Integer page, Integer pageSize);

    /**
     * 根据视频主键查询vlog.
     *
     * @param userId 用户id
     * @param vlogId 视频id
     * @return 视频信息
     */
    IndexVlogVO getVlogDetailById(String userId, String vlogId);

    /**
     * 用户把视频改为公开, 私密的视频.
     *
     * @param userId 用户id
     * @param vlogId 视频id
     * @param type 视频类型
     */
    void changeToPrivateOrPublic(String userId, String vlogId, Integer type);

    /**
     * 查询用户的公开, 私密的视频列表.
     *
     * @param userId 用户id
     * @param type 视频类型
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页列表
     */
    PagedGridResult queryMyVlogList(String userId, Integer type, Integer page, Integer pageSize);

    /**
     * 获得视频点赞的总数.
     *
     * @param vlogId 视频id
     * @return 视频点赞总数
     */
    Integer getVlogBeLikedCounts(String vlogId);

    /**
     * 判断我是否点赞过视频.
     *
     * @param myId 用户id
     * @param vlogId 视频id
     * @return 点赞返回true, 否则false
     */
    boolean doILikeVlog(String myId, String vlogId);

    /**
     * 查询用户关注的博主发布的短视频列表.
     *
     * @param myId 用户id
     * @param page 页码
     * @param pageSize 每页个数
     * @return 分页信息
     */
    PagedGridResult getMyFollowVlogList(String myId, Integer page, Integer pageSize);

    /**
     * 查询朋友发布的短视频列表.
     *
     * @param myId 用户id
     * @param page 页码
     * @param pageSize 每页个数
     * @return 分页信息
     */
    PagedGridResult getMyFriendVlogList(String myId, Integer page, Integer pageSize);
}