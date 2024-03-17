package com.tiktok.service;

import com.tiktok.bo.CommentBO;
import com.tiktok.pojo.Comment;
import com.tiktok.utils.PagedGridResult;
import com.tiktok.vo.CommentVO;

/**
 * 针对comment表的service层接口.
 *
 * @author lzc
 * @version 1.0
 */
public interface CommentService {

    /**
     * 发表评论.
     *
     * @param commentBO 前端数据映射实体类
     * @return 前端视图类
     */
    CommentVO createComment(CommentBO commentBO);

    /**
     * 查询评论的列表.
     *
     * @param vlogId 视频id
     * @param userId 用户id
     * @param page 页码
     * @param pageSize 每页个数
     * @return 分页信息
     */
    PagedGridResult queryVlogComments(String vlogId, String userId, Integer page, Integer pageSize);

    /**
     * 删除评论.
     *
     * @param commentUserId 评论用户id
     * @param commentId 评论id
     * @param vlogId 视频id
     */
    void deleteComment(String commentUserId, String commentId, String vlogId);

    /**
     * 根据主键查询评论.
     *
     * @param id 主键
     * @return 评论
     */
    Comment getComment(String id);
}