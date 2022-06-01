package com.genersoft.iot.vmp.media.zlm.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.genersoft.iot.vmp.gb28181.session.SsrcConfig;
import com.genersoft.iot.vmp.media.zlm.ZLMServerConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;

    /**
     * <p>
     *
     * </p>
     *
     * @author n
     * @since 2022-05-31
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    @TableName(value="media_server")
    public class MediaServerItem implements Serializable{

        private static final long serialVersionUID = 1L;

        @TableId(value = "id", type = IdType.ASSIGN_ID)
        private String id;

        private String ip;

        private String hookIp;

        private String sdpIp;

        private String streamIp;

        private Integer httpPort;
        @TableField(value = "http_ssl_port")
        private Integer httpSSlPort;

        private Integer rtmpPort;

        @TableField(value = "rtmp_ssl_port")
        private Integer rtmpSSlPort;

        private Integer rtpProxyPort;

        private Integer rtspPort;
        @TableField(value = "rtsp_ssl_port")
        private Integer rtspSSLPort;

        private boolean autoConfig;

        private String secret;
        @TableField(value = "stream_none_reader_delay_ms")
        private Integer streamNoneReaderDelayMS;

        private boolean rtpEnable;

        private String rtpPortRange;

        private String sendRtpPortRange;

        private Integer recordAssistPort;

        private boolean defaultServer;

        private String createTime;

        private String updateTime;

        private Integer hookAliveInterval;


        @ApiModelProperty(hidden = true)
        @TableField(exist = false)
        private SsrcConfig ssrcConfig;
        @ApiModelProperty(hidden = true)
        @TableField(exist = false)
        private int currentPort;
        @ApiModelProperty(hidden = true)
        @TableField(exist = false)
        private boolean status;
        @ApiModelProperty(hidden = true)
        @TableField(exist = false)
        private String lastKeepaliveTime;
        /**
         * 每一台ZLM都有一套独立的SSRC列表
         * 在ApplicationCheckRunner里对mediaServerSsrcMap进行初始化
         */
        @ApiModelProperty(hidden = true)
        @TableField(exist = false)
        private HashMap<String, SsrcConfig> mediaServerSsrcMap;

        public MediaServerItem() {
        }

        public MediaServerItem(ZLMServerConfig zlmServerConfig,String sipIp){
            id=zlmServerConfig.getGeneralMediaServerId();
            ip=zlmServerConfig.getIp();
            hookIp=StringUtils.isEmpty(zlmServerConfig.getHookIp())?sipIp:zlmServerConfig.getHookIp();
            sdpIp=StringUtils.isEmpty(zlmServerConfig.getSdpIp())?zlmServerConfig.getIp():zlmServerConfig.getSdpIp();
            streamIp=StringUtils.isEmpty(zlmServerConfig.getStreamIp())?zlmServerConfig.getIp():zlmServerConfig.getStreamIp();
            httpPort=zlmServerConfig.getHttpPort();
            httpSSlPort=zlmServerConfig.getHttpSSLport();
            rtmpPort=zlmServerConfig.getRtmpPort();
            rtmpSSlPort=zlmServerConfig.getRtmpSslPort();
            rtpProxyPort=zlmServerConfig.getRtpProxyPort();
            rtspPort=zlmServerConfig.getRtspPort();
            rtspSSLPort=zlmServerConfig.getRtspSSlport();
            autoConfig=true; // 默认值true;
            secret=zlmServerConfig.getApiSecret();
            streamNoneReaderDelayMS=zlmServerConfig.getGeneralStreamNoneReaderDelayMS();
            hookAliveInterval=zlmServerConfig.getHookAliveInterval();
            rtpEnable=false; // 默认使用单端口;直到用户自己设置开启多端口
            rtpPortRange=zlmServerConfig.getPortRange().replace("_",","); // 默认使用30000,30500作为级联时发送流的端口号
            sendRtpPortRange="30000,30500"; // 默认使用30000,30500作为级联时发送流的端口号
            recordAssistPort=0; // 默认关闭
           }
        }