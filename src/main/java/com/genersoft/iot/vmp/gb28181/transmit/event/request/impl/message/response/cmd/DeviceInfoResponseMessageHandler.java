package com.genersoft.iot.vmp.gb28181.transmit.event.request.impl.message.response.cmd;

import com.genersoft.iot.vmp.common.VideoManagerConstants;
import com.genersoft.iot.vmp.conf.SipConfig;
import com.genersoft.iot.vmp.gb28181.bean.Device;
import com.genersoft.iot.vmp.gb28181.bean.ParentPlatform;
import com.genersoft.iot.vmp.gb28181.event.EventPublisher;
import com.genersoft.iot.vmp.gb28181.transmit.callback.DeferredResultHolder;
import com.genersoft.iot.vmp.gb28181.transmit.callback.RequestMessage;
import com.genersoft.iot.vmp.gb28181.transmit.event.request.SIPRequestProcessorParent;
import com.genersoft.iot.vmp.gb28181.transmit.event.request.impl.message.IMessageHandler;
import com.genersoft.iot.vmp.gb28181.transmit.event.request.impl.message.response.ResponseMessageHandler;
import com.genersoft.iot.vmp.service.IDeviceService;
import com.genersoft.iot.vmp.storager.IRedisCatchStorage;
import com.genersoft.iot.vmp.storager.IVideoManagerStorage;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;

import static com.genersoft.iot.vmp.gb28181.utils.XmlUtil.getText;

/**
 * @author lin
 */
@Component
public class DeviceInfoResponseMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(DeviceInfoResponseMessageHandler.class);
    private final String cmdType = "DeviceInfo";

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Autowired
    private DeferredResultHolder deferredResultHolder;

    @Autowired
    private SipConfig config;

    @Autowired
    private EventPublisher publisher;

    @Autowired
    private IDeviceService deviceService;

    @Override
    public void afterPropertiesSet() throws Exception {
        responseMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, Device device, Element rootElement) {
        logger.debug("接收到DeviceInfo应答消息");
        // 检查设备是否存在， 不存在则不回复
        if (device == null || "0".equals(device.getOnline())) {
            logger.warn("[接收到DeviceInfo应答消息,但是设备已经离线]：" + (device != null ? device.getDeviceId():"" ));
            return;
        }
        try {
            rootElement = getRootElement(evt, device.getCharset());
            Element deviceIdElement = rootElement.element("DeviceID");
            String channelId = deviceIdElement.getTextTrim();
            String key = DeferredResultHolder.CALLBACK_CMD_DEVICEINFO + device.getDeviceId() + channelId;
            device.setName(getText(rootElement, "DeviceName"));

            device.setManufacturer(getText(rootElement, "Manufacturer"));
            device.setModel(getText(rootElement, "Model"));
            device.setFirmware(getText(rootElement, "Firmware"));
            if (StringUtils.isEmpty(device.getStreamMode())) {
                device.setStreamMode("UDP");
            }
            deviceService.updateDevice(device);
//            storager.updateDevice(device);

            RequestMessage msg = new RequestMessage();
            msg.setKey(key);
            msg.setData(device);
            deferredResultHolder.invokeAllResult(msg);
            // 回复200 OK
            responseAck(evt, Response.OK);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element rootElement) {

    }
}
