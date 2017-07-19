package org.intellij.plugins.keymapswitcher;

import com.intellij.openapi.diagnostic.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.ide.RestService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class KeyboardTypeListener extends RestService {

    private static final Logger LOG = Logger.getInstance(KeyboardTypeListener.class);

    static final String SERVICE_NAME = "keymapSwitcher";

    @NotNull
    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected boolean isMethodSupported(@NotNull HttpMethod httpMethod) {
        return httpMethod == HttpMethod.GET;
    }

    @Nullable
    @Override
    public String execute(@NotNull QueryStringDecoder queryStringDecoder,
                          @NotNull FullHttpRequest fullHttpRequest,
                          @NotNull ChannelHandlerContext channelHandlerContext) throws IOException {

        Optional<String> device = parseDeviceNumber(fullHttpRequest);
        if (!device.isPresent()) {
            return "Device number not found. Try adding '/{inputEventNumber}' to request (ex: 127.0.0.1:63330/api" +
                    "." + SERVICE_NAME + "/3)";
        }

        String selectedKeymap = Settings.getInstance().getKeyboardToKeymapMapping().get(device.get());

        SwitcherService.getInstance().switchTo(selectedKeymap);

        sendOk(fullHttpRequest, channelHandlerContext);
        return null;

    }

    @Override
    protected boolean isHostTrusted(@NotNull FullHttpRequest request) throws InterruptedException, InvocationTargetException {
        return true;
    }

    private Optional<String> parseDeviceNumber(@NotNull FullHttpRequest fullHttpRequest) {
        String uri = fullHttpRequest.uri();
        String DEVICE_EXTRACTION_PATTERN = ".*" + SERVICE_NAME + "/";
        String possibleDevice = uri.replaceAll(DEVICE_EXTRACTION_PATTERN, "");
        String device = null;

        try {
            device = String.valueOf(new Integer(possibleDevice));
        } catch (NumberFormatException e) {
            LOG.warn("Input device not valid integer '" + possibleDevice + "'");
        }

        return Optional.ofNullable(device);
    }
}
