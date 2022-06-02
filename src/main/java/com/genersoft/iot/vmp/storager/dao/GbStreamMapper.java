package com.genersoft.iot.vmp.storager.dao;

import com.genersoft.iot.vmp.gb28181.bean.GbStream;
import com.genersoft.iot.vmp.media.zlm.dto.StreamProxyItem;
import com.genersoft.iot.vmp.media.zlm.dto.StreamPushItem;
import com.genersoft.iot.vmp.service.bean.GPSMsgInfo;
import com.genersoft.iot.vmp.vmanager.bean.StreamPushExcelDto;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GbStreamMapper {

    @Insert("REPLACE INTO gb_stream (app, stream, gbId, name, " +
            "longitude, latitude, stream_type, media_server_id, status, create_stamp) VALUES" +
            "('${app}', '${stream}', '${gbId}', '${name}', " +
            "'${longitude}', '${latitude}', '${streamType}', " +
            "'${mediaServerId}', ${status}, ${createStamp})")
    @Options(useGeneratedKeys = true, keyProperty = "gbStreamId", keyColumn = "gbStreamId")
    int add(GbStream gbStream);

    @Update("UPDATE gb_stream " +
            "SET app=#{app}," +
            "stream=#{stream}," +
            "gbId=#{gbId}," +
            "name=#{name}," +
            "stream_type=#{streamType}," +
            "longitude=#{longitude}, " +
            "latitude=#{latitude}," +
            "media_server_id=#{mediaServerId}," +
            "status=${status} " +
            "WHERE app=#{app} AND stream=#{stream}")
    int updateByAppAndStream(GbStream gbStream);

    @Update("UPDATE gb_stream " +
            "SET app=#{app}," +
            "stream=#{stream}," +
            "gbId=#{gbId}," +
            "name=#{name}," +
            "stream_type=#{streamType}," +
            "longitude=#{longitude}, " +
            "latitude=#{latitude}," +
            "media_server_id=#{mediaServerId}," +
            "status=${status} " +
            "WHERE gb_stream_id=#{gbStreamId}")
    int update(GbStream gbStream);

    @Delete("DELETE FROM gb_stream WHERE app=#{app} AND stream=#{stream}")
    int del(String app, String stream);

    @Select("<script> "+
            "SELECT gs.* FROM gb_stream gs " +
            "WHERE " +
            "1=1 " +
            " <if test='catalogId != null'> AND gs.gb_stream_id in" +
            "(select pgs.gb_stream_id from platform_gb_stream pgs where pgs.platform_id = #{platformId} and pgs.catalog_d=#{catalogId})</if> " +
            " <if test='catalogId == null'> AND gs.gb_stream_id not in" +
            "(select pgs.gb_stream_id from platform_gb_stream pgs where pgs.platform_id = #{platformId}) </if> " +
            " <if test='query != null'> AND (gs.app LIKE '%${query}%' OR gs.stream LIKE '%${query}%' OR gs.gb_id LIKE '%${query}%' OR gs.name LIKE '%${query}%')</if> " +
            " <if test='pushing == true' > AND gs.status=1</if>" +
            " <if test='pushing == false' > AND gs.status=0</if>" +
            " <if test='mediaServerId != null' > AND gs.media_server_id=#{mediaServerId} </if>" +
            " order by gs.gb_stream_id asc " +
            "</script>")
    List<GbStream> selectAll(String platformId, String catalogId, String query, Boolean pushing, String mediaServerId);

    @Select("SELECT * FROM gb_stream WHERE app=#{app} AND stream=#{stream}")
    StreamProxyItem selectOne(String app, String stream);

    @Select("SELECT * FROM gb_stream WHERE gbId=#{gbId}")
    List<GbStream> selectByGBId(String gbId);

    @Select("SELECT gs.*, pgs.platform_id as platformId, pgs.catalog_id as catalogId FROM gb_stream gs " +
            "LEFT JOIN platform_gb_stream pgs ON gs.gb_stream_id = pgs.gb_stream_id " +
            "WHERE gs.gb_id = '${gbId}' AND pgs.platform_id = '${platformId}'")
    GbStream queryStreamInPlatform(String platformId, String gbId);

    @Select("SELECT gs.*, pgs.platform_id as platformId, pgs.catalog_id as catalogId FROM gb_stream gs " +
            "LEFT JOIN platform_gb_stream pgs ON gs.gb_stream_id = pgs.gb_stream_id " +
            "WHERE pgs.platform_id = #{platformId}")
    List<GbStream> queryGbStreamListInPlatform(String platformId);


    @Select("SELECT gs.* FROM gb_stream gs LEFT JOIN platform_gb_stream pgs " +
            "ON gs.gb_stream_id = pgs.gb_stream_id WHERE pgs.gb_stream_id is NULL")
    List<GbStream> queryStreamNotInPlatform();

    @Update("UPDATE gb_stream " +
            "SET status=${status} " +
            "WHERE app=#{app} AND stream=#{stream}")
    int setStatus(String app, String stream, boolean status);

    @Update("UPDATE gb_stream " +
            "SET status=${status} " +
            "WHERE media_server_id=#{mediaServerId} ")
    void updateStatusByMediaServerId(String mediaServerId, boolean status);

    @Delete("DELETE FROM gb_stream WHERE stream_type=#{type} AND gb_id=NULL AND media_server_id=#{mediaServerId}")
    void deleteWithoutGBId(String type, String mediaServerId);

    @Delete("<script> "+
            "DELETE FROM gb_stream where " +
            "<foreach collection='streamProxyItemList' item='item' separator='or'>" +
            "(app=#{item.app} and stream=#{item.stream}) " +
            "</foreach>" +
            "</script>")
    void batchDel(List<StreamProxyItem> streamProxyItemList);

    @Delete("<script> "+
            "DELETE FROM gb_stream where " +
            "<foreach collection='gbStreams' item='item' separator='or'>" +
            "(app=#{item.app} and stream=#{item.stream}) " +
            "</foreach>" +
            "</script>")
    void batchDelForGbStream(List<GbStream> gbStreams);

    @Insert("<script> " +
            "INSERT IGNORE into gb_stream " +
            "(app, stream, gb_id, name, " +
            "longitude, latitude, stream_type, media_server_id, status, create_stamp)" +
            "values " +
            "<foreach collection='subList' index='index' item='item' separator=','> " +
            "('${item.app}', '${item.stream}', '${item.gbId}', '${item.name}', " +
            "'${item.longitude}', '${item.latitude}', '${item.streamType}', " +
            "'${item.mediaServerId}', ${item.status}, ${item.createStamp}) "+
            "</foreach> " +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "gbStreamId", keyColumn = "gb_stream_id")
    void batchAdd(List<StreamPushItem> subList);

    @Update({"<script>" +
            "<foreach collection='gpsMsgInfos' item='item' separator=';'>" +
            " UPDATE" +
            " gb_stream" +
            " SET longitude=${item.lng}, latitude=${item.lat} " +
            "WHERE gb_id=#{item.id}"+
            "</foreach>" +
            "</script>"})
    int updateStreamGPS(List<GPSMsgInfo> gpsMsgInfos);

    @Select("<script> "+
                   "SELECT * FROM gb_stream where " +
                   "<foreach collection='streamPushItems' item='item' separator='or'>" +
                   "(app=#{item.app} and stream=#{item.stream}) " +
                   "</foreach>" +
                   "</script>")
    List<GbStream> selectAllForAppAndStream(List<StreamPushItem> streamPushItems);
}
