package com.tiktok.utils;

import lombok.Data;
import java.util.List;

/**
 * 用来返回分页Grid的数据格式.
 *
 * @author lzc
 * @version 1.0
 */
@Data
public class PagedGridResult {
	
	private int page;			// 当前页数
	private long total;			// 总页数
	private long records;		// 总记录数
	private List<?> rows;		// 每行显示的内容

}