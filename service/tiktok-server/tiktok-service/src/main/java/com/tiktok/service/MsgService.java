package com.tiktok.service;

import com.tiktok.mo.MessageMO;
import java.util.List;
import java.util.Map;

/**
 * 针对mongodb的service层接口.
 *
 * @author lzc
 * @version 1.0
 */
public interface MsgService {

    /**
     * 创建消息.
     *
     * @param fromUserId 来源用户
     * @param toUserId 目的用户
     * @param type 消息类型
     * @param msgContent 消息内容
     */
    void createMsg(String fromUserId, String toUserId, Integer type, Map msgContent);

    /**
     * 查询消息列表.
     *
     * @param toUserId 目的用户
     * @param page 页码
     * @param pageSize 每页个数
     * @return 分页信息
     */
    List<MessageMO> queryList(String toUserId, Integer page, Integer pageSize);
}