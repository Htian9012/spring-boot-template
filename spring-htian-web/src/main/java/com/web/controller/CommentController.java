package com.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bean.comment.CommentListVO;
import com.dao.entity.BComment;
import com.dao.entity.BLog;
import com.dao.mapper.BCommentMapper;
import com.dao.mapper.BLogMapper;
import com.web.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lizehao
 */
@Api(value = "LogController", description = "")
@RestController
@RequestMapping("/rest/comment")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private BCommentMapper commentMapper;


    @ApiOperation(value = "新增评论")
    @RequestMapping(value = "/insert", method = RequestMethod.GET)
    public Result<BLog> selectOne(@RequestBody BComment record) {
        int flag = commentMapper.insert(record);
        return flag == 1 ? Result.success(): Result.error();
    }

    @ApiOperation(value = "查询文章评论")
    @RequestMapping(value = "/getCommentList", method = RequestMethod.GET)
    public Result<List<CommentListVO>> getCommentList(@RequestParam Long blogId) {
        List<BComment> sourceList = commentMapper.selectList(new QueryWrapper<BComment>().eq("blog_id",blogId).orderByAsc("flag_id","offset"));
        return Result.success(genarateComment(sourceList));
    }

    public static List<CommentListVO> genarateComment( List<BComment> sourceList) {

        //数据集处理
        Map<Integer,CommentListVO> map = new HashMap<>();
        int floor = 0;
        for(BComment index: sourceList){
            CommentListVO commentListVO = map.get(floor);
            if(null == commentListVO || 1 == index.getIsRoot()){
                floor++;
                commentListVO = new CommentListVO();
                commentListVO.setFloor(floor);
            }
            commentListVO.getComments().add(index);
            map.put(floor,commentListVO);
        }

        //返回前端VO
        List<CommentListVO> comments = map.values().stream().collect(Collectors.toList());
        return comments;
    }

}
