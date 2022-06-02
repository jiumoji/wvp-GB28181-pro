package com.genersoft.iot.vmp.storager.dao;

import java.util.List;

import com.genersoft.iot.vmp.gb28181.bean.MobilePosition;
import org.apache.ibatis.annotations.*;
//import org.springframework.stereotype.Repository;

@Mapper
//@Repository
public interface DeviceMobilePositionMapper {

    @Insert("INSERT INTO device_mobile_position (device_id,channel_id, device_name, time, longitude, latitude, altitude, speed, direction, report_source, geodetic_system, cnLng, cnLat) " +
            "VALUES ('${deviceId}','${channelId}', '${deviceName}', '${time}', ${longitude}, ${latitude}, ${altitude}, ${speed}, ${direction}, '${reportSource}', '${geodeticSystem}', '${cnLng}', '${cnLat}')")
    int insertNewPosition(MobilePosition mobilePosition);

    @Select(value = {" <script>" +
    "SELECT * FROM device_mobile_position" +
    " WHERE device_id = #{deviceId} and channel_id = #{channelId} " +
    "<if test=\"startTime != null\"> AND time&gt;=#{startTime}</if>" +
    "<if test=\"endTime != null\"> AND time&lt;=#{endTime}</if>" +
    " ORDER BY time ASC" +
    " </script>"})
    List<MobilePosition> queryPositionByDeviceIdAndTime(String deviceId, String channelId, String startTime, String endTime);

    @Select("SELECT * FROM device_mobile_position WHERE device_id = #{deviceId}" +
            " ORDER BY time DESC LIMIT 1")
    MobilePosition queryLatestPositionByDevice(String deviceId);

    @Delete("DELETE FROM device_mobile_position WHERE device_id = #{deviceId}")
    int clearMobilePositionsByDeviceId(String deviceId);

}
