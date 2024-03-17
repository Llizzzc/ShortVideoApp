package com.tiktok.repository;

import com.tiktok.mo.MessageMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 针对mongodb的操作.
 *
 * @author lzc
 * @version 1.0
 */
@Repository
public interface MessageRepository extends MongoRepository<MessageMO, String> {

    // 通过实现Repository, 自定义条件查询
    List<MessageMO> findAllByToUserIdEqualsOrderByCreateTimeDesc(String toUserId, Pageable pageable);
//    void deleteAllByFromUserIdAndToUserIdAndMsgType();
}