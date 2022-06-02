package com.genersoft.iot.vmp.storager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.genersoft.iot.vmp.gb28181.bean.Device;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于存储设备信息
 */
@Mapper
@Repository
public interface DeviceMapper extends BaseMapper<Device>{

        @Select("SELECT * FROM device WHERE device_id = #{deviceId}")
        Device getDeviceByDeviceId(String deviceId);


        @Select("SELECT *, (SELECT count(0) FROM device_channel WHERE device_id=de.device_id) as channel_count  FROM device de")
        List<Device> getDevices();

        @Delete("DELETE FROM device WHERE device_id=#{deviceId}")
        int del(String deviceId);

        @Update("UPDATE device SET online='0'")
        int outlineForAll();

        @Select("SELECT * FROM device WHERE online = '1'")
        List<Device> getOnlineDevices();
        @Select("SELECT * FROM device WHERE ip = #{host} AND port=${port}")
        Device getDeviceByHostAndPort(String host, int port);
    }

