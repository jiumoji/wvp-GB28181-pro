package com.genersoft.iot.vmp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.genersoft.iot.vmp.gb28181.bean.DeviceAlarm;
import com.genersoft.iot.vmp.service.ILogService;
import com.genersoft.iot.vmp.storager.dao.LogMapper;
import com.genersoft.iot.vmp.storager.dao.dto.LogDto;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, LogDto> implements ILogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public PageInfo<LogDto> getAll(int page, int count, String query, String type, String startTime, String endTime) {
        PageHelper.startPage(page, count);
     //   List<LogDto> all = logMapper.query(query, type, startTime, endTime);
       // return new PageInfo<>(all);
        QueryWrapper<LogDto> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNoneBlank(query)){
            queryWrapper.like("name",query);
        }
        if(StringUtils.isNoneBlank(type)){
            queryWrapper.eq("type",type.toUpperCase());
        }
        if(StringUtils.isNoneBlank(endTime)){
            queryWrapper.le("create_time",endTime);
        }
        if(StringUtils.isNoneBlank(startTime)){
            queryWrapper.ge("create_time",startTime);
        }
        queryWrapper.orderByDesc("create_time");
        final PageInfo<LogDto> objectPageInfo=PageHelper.startPage(page,count).doSelectPageInfo(()->logMapper.selectList(queryWrapper));
        return objectPageInfo;
    }

    @Override
    public void add(LogDto logDto) {
        logMapper.insert(logDto);
    }

    @Override
    public boolean  clear() {
        return remove(new QueryWrapper<>());
    }
}
