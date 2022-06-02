package com.genersoft.iot.vmp.storager.dao;

import com.genersoft.iot.vmp.gb28181.bean.DeviceAlarm;
import com.genersoft.iot.vmp.gb28181.bean.DeviceChannel;
import com.genersoft.iot.vmp.vmanager.gb28181.platform.bean.ChannelReduce;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于存储设备的报警信息
 */
@Mapper
@Repository
public interface DeviceAlarmMapper {

    @Insert("INSERT INTO device_alarm (device_id, channel_id, alarm_priority, alarm_method, alarm_time, alarm_description, longitude, latitude, alarm_type ) " +
            "VALUES ('${deviceId}', '${channelId}', '${alarmPriority}', '${alarmMethod}', '${alarmTime}', '${alarmDescription}', ${longitude}, ${latitude}, '${alarmType}')")
    int add(DeviceAlarm alarm);


    @Select(value = {" <script>" +
            " SELECT * FROM device_alarm " +
            " WHERE 1=1 " +
            " <if test=\"deviceId != null\" >  AND device_id = '${deviceId}'</if>" +
            " <if test=\"alarmPriority != null\" >  AND alarm_priority = '${alarmPriority}' </if>" +
            " <if test=\"alarmMethod != null\" >  AND alarm_method = '${alarmMethod}' </if>" +
            " <if test=\"alarmType != null\" >  AND alarm_type = '${alarmType}' </if>" +
            " <if test=\"startTime != null\" >  AND alarm_time &gt;= '${startTime}' </if>" +
            " <if test=\"endTime != null\" >  AND alarm_time &lt;= '${endTime}' </if>" +
            " ORDER BY alarm_time ASC " +
            " </script>"})
    List<DeviceAlarm> query(String deviceId, String alarmPriority, String alarmMethod,
                            String alarmType, String startTime, String endTime);


    @Delete(" <script>" +
            "DELETE FROM device_alarm WHERE 1=1 " +
            " <if test=\"deviceIdList != null and id == null \" > AND deviceId in " +
            "<foreach collection='deviceIdList'  item='item'  open='(' separator=',' close=')' > '${item}'</foreach>" +
            "</if>" +
            " <if test=\"time != null and id == null \" > AND alarmTime &lt;= '${time}'</if>" +
            " <if test=\"id != null\" > AND id = ${id}</if>" +
            " </script>"
            )
    int clearAlarmBeforeTime(Integer id, List<String> deviceIdList, String time);
}
