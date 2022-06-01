package com.genersoft.iot.vmp.storager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.genersoft.iot.vmp.media.zlm.dto.MediaServerItem;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MediaServerMapper extends BaseMapper<MediaServerItem>{

}