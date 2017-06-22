package org.intellij.plugins.keymapswitcher;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


@State(
        name = "KeymapSwitcherState",
        storages = {@Storage(file = "$APP_CONFIG$/keymap-switcher-state.xml")}
)
public class Settings implements PersistentStateComponent<Settings> {

    public static final String PROXY_URL = "http://localhost:8080";

    @Nullable
    private Map<String, String> keyboardToKeymapMapping;

    {
        // TODO: test settings, remove
        keyboardToKeymapMapping = new HashMap<String, String>() {
            private static final long serialVersionUID = 3682465850618332320L;

            {
                put("6", "Default for GNOME");
                put("3", "Mac OS X");
            }
        };
    }

    @NotNull
    public static Settings getInstance() {
        return ServiceManager.getService(Settings.class);
    }


    @Nullable
    @Override
    public Settings getState() {
        return this;
    }

    @Override
    public void loadState(Settings settings) {
        this.setKeyboardToKeymapMapping(settings.getKeyboardToKeymapMapping());
    }

    @Nullable
    public Map<String, String> getKeyboardToKeymapMapping() {
        return keyboardToKeymapMapping;
    }

    public void setKeyboardToKeymapMapping(@Nullable Map<String, String> keyboardToKeymapMapping) {
        this.keyboardToKeymapMapping = keyboardToKeymapMapping;
    }
}
