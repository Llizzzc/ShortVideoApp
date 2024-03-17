package com.tiktok.service;

/**
 * 针对my_liked_vlog表的service层接口.
 *
 * @author lzc
 * @version 1.0
 */
public interface MyLikedVlogService {

    /**
     * 用户点赞.
     *
     * @param userId 用户id
     * @param vlogId 视频id
     */
    void userLikeVlog(String userId, String vlogId);

    /**
     * 用户取消点赞.
     *
     * @param userId 用户id
     * @param vlogId 视频id
     */
    void userUnLikeVlog(String userId, String vlogId);
}
