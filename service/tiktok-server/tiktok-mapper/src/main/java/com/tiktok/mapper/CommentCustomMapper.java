package com.tiktok.mapper;

import com.tiktok.vo.CommentVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 针对comment表的Mapper操作.
 *
 * @author lzc
 * @version 1.0
 */
public interface CommentCustomMapper {
    List<CommentVO> getCommentList(@Param("paramMap") Map<String, Object> map);
}
