package org.intellij.plugins.keymapswitcher;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.ide.BuiltInServerManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SubscribeToSwitcherProxyService implements ApplicationComponent {

    private static final Logger LOG = Logger.getInstance(SubscribeToSwitcherProxyService.class);

    private static final String SERVICE_NAME = "subscribeToSwitcherProxyService";
    private static final String IDEA_URL = "http://localhost";
    private int IDEA_PORT;


    @Override
    public void initComponent() {
        BuiltInServerManager server = ServiceManager.getService(BuiltInServerManager.class);
        IDEA_PORT = server.getPort();

        subscribe();
    }

    @Override
    public void disposeComponent() {
        unsubscribe();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return SERVICE_NAME;
    }

    private void subscribe() {
        makeRequest(String.format("%s/subscribe?url=%s:%d/api.%s",
                Settings.PROXY_URL, IDEA_URL, IDEA_PORT, KeyboardTypeListener.SERVICE_NAME));
    }

    private void unsubscribe() {
        makeRequest(String.format("%s/unsubscribe?url=%s:%d/api.%s",
                Settings.PROXY_URL, IDEA_URL, IDEA_PORT, KeyboardTypeListener.SERVICE_NAME));
    }

    private void makeRequest(String url) {
        try {
            URL keymapSwitcherProxySubscribeUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) keymapSwitcherProxySubscribeUrl.openConnection();
            con.setRequestMethod("POST");
            LOG.debug("Response code: " + String.valueOf(con.getResponseCode()));
        } catch (IOException e) {
            LOG.error("Something went wrong during request '" + url + "'", e);
        }
    }
}
