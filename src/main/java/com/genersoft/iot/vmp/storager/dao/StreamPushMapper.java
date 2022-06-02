package com.genersoft.iot.vmp.storager.dao;

import com.genersoft.iot.vmp.gb28181.bean.GbStream;
import com.genersoft.iot.vmp.media.zlm.dto.StreamPushItem;
import org.apache.ibatis.annotations.*;
// import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface StreamPushMapper {

    @Insert("INSERT INTO stream_push (app, stream, total_reader_count, origin_type, origin_type_str, " +
            "create_stamp, alive_second, media_server_id) VALUES" +
            "('${app}', '${stream}', '${totalReaderCount}', '${originType}', '${originTypeStr}', " +
            "'${createStamp}', '${aliveSecond}', '${mediaServerId}' )")
    int add(StreamPushItem streamPushItem);

    @Update("UPDATE stream_push " +
            "SET app=#{app}," +
            "stream=#{stream}," +
            "media_server_id=#{mediaServerId}," +
            "total_reader_count=#{totalReaderCount}, " +
            "origin_type=#{originType}," +
            "origin_type_str=#{originTypeStr}, " +
            "create_stamp=#{createStamp}, " +
            "alive_second=#{aliveSecond} " +
            "WHERE app=#{app} AND stream=#{stream}")
    int update(StreamPushItem streamPushItem);

    @Delete("DELETE FROM stream_push WHERE app=#{app} AND stream=#{stream}")
    int del(String app, String stream);

    @Delete("<script> "+
            "DELETE sp FROM stream_push sp left join gb_stream gs on sp.app = gs.app AND sp.stream = gs.stream where " +
            "<foreach collection='streamPushItems' item='item' separator='or'>" +
            "(sp.app=#{item.app} and sp.stream=#{item.stream} and gs.gbId is null) " +
            "</foreach>" +
            "</script>")
    int delAllWithoutGBId(List<StreamPushItem> streamPushItems);

    @Delete("<script> "+
            "DELETE FROM stream_push where " +
            "<foreach collection='streamPushItems' item='item' separator='or'>" +
            "(app=#{item.app} and stream=#{item.stream}) " +
            "</foreach>" +
            "</script>")
    int delAll(List<StreamPushItem> streamPushItems);

    @Delete("<script> "+
            "DELETE FROM stream_push where " +
            "<foreach collection='gbStreams' item='item' separator='or'>" +
            "(app=#{item.app} and stream=#{item.stream}) " +
            "</foreach>" +
            "</script>")
    int delAllForGbStream(List<GbStream> gbStreams);


    @Select(value = {" <script>" +
            "SELECT " +
            "st.*, " +
            "gs.gbId, gs.status, gs.name, gs.longitude, gs.latitude, gs.gb_stream_id " +
            "from " +
            "stream_push st " +
            "LEFT JOIN gb_stream gs " +
            "on st.app = gs.app AND st.stream = gs.stream " +
            "WHERE " +
            "1=1 " +
            " <if test='query != null'> AND (st.app LIKE '%${query}%' OR st.stream LIKE '%${query}%' OR gs.gbId LIKE '%${query}%' OR gs.name LIKE '%${query}%')</if> " +
            " <if test='pushing == true' > AND (gs.gb_id is null OR gs.status=1)</if>" +
            " <if test='pushing == false' > AND gs.status=0</if>" +
            " <if test='mediaServerId != null' > AND st.media_server_id=#{mediaServerId} </if>" +
            "order by st.create_stamp desc" +
            " </script>"})
    List<StreamPushItem> selectAllForList(String query, Boolean pushing, String mediaServerId);

    @Select("SELECT st.*, gs.gb_id, gs.status, gs.name, gs.longitude, gs.latitude FROM stream_push st LEFT JOIN gb_stream gs on st.app = gs.app AND st.stream = gs.stream order by st.create_stamp desc")
    List<StreamPushItem> selectAll();

    @Select("SELECT st.*, gs.gb_id, gs.status, gs.name, gs.longitude, gs.latitude FROM stream_push st LEFT JOIN gb_stream gs on st.app = gs.app AND st.stream = gs.stream WHERE st.app=#{app} AND st.stream=#{stream}")
    StreamPushItem selectOne(String app, String stream);

    @Insert("<script>"  +
            "Insert IGNORE INTO stream_push (app, stream, total_reader_count, originType, originType_str, " +
            "create_stamp, alive_second, media_server_id) " +
            "VALUES <foreach collection='streamPushItems' item='item' index='index' separator=','>" +
            "( '${item.app}', '${item.stream}', '${item.totalReaderCount}', #{item.originType}, " +
            "'${item.originTypeStr}',#{item.createStamp}, #{item.aliveSecond}, '${item.mediaServerId}' )" +
            " </foreach>" +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addAll(List<StreamPushItem> streamPushItems);

    @Delete("DELETE FROM stream_push")
    void clear();

    @Delete("DELETE from  stream_push where id in ( select sp.id FROM stream_push sp left join gb_stream gs on gs.app = sp.app and gs.stream= sp.stream WHERE sp.media_server_id=#{mediaServerId} and gs.gb_id is null) ")
    void deleteWithoutGBId(String mediaServerId);

    @Select("SELECT * FROM stream_push WHERE media_server_id=#{mediaServerId}")
    List<StreamPushItem> selectAllByMediaServerId(String mediaServerId);

    @Select("SELECT sp.* FROM stream_push sp left join gb_stream gs on gs.app = sp.app and gs.stream= sp.stream WHERE sp.media_server_id=#{mediaServerId} and gs.gb_id is null")
    List<StreamPushItem> selectAllByMediaServerIdWithOutGbID(String mediaServerId);

}
