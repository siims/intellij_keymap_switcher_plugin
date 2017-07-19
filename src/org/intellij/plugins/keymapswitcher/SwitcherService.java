package org.intellij.plugins.keymapswitcher;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import org.jetbrains.annotations.NotNull;

public class SwitcherService {

    private static final Logger LOG = Logger.getInstance(SwitcherService.class);

    @NotNull
    public static SwitcherService getInstance() {
        return ServiceManager.getService(SwitcherService.class);
    }

    public void switchTo(String selectedKeymap) {


        Keymap[] allKeymaps = KeymapManagerEx.getInstanceEx().getAllKeymaps();
        Keymap newKeymap = null;
        for (Keymap keymap : allKeymaps) {
            if (keymap.getName().equals(selectedKeymap) && !keymap.equals(KeymapManagerEx.getInstanceEx().getActiveKeymap())) {
                newKeymap = keymap;
                break;
            }
        }

        if (newKeymap == null) {
            return;
        }

        switchKeymap(newKeymap);
    }

    // need to create background thread to be able to alter keymap change (event dispatch thread only allowed)
    private void switchKeymap(final Keymap newKeymap) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        System.out.println("Switched keymap from " +
                                KeymapManagerEx.getInstanceEx().getActiveKeymap().getName() + " to " + newKeymap
                                .getName());
                        LOG.debug("Switched keymap from {} to {}.",
                                KeymapManagerEx.getInstanceEx().getActiveKeymap().getName(), newKeymap.getName());
                        KeymapManagerEx.getInstanceEx().setActiveKeymap(newKeymap);
                    }
                });
            }
        });
    }
}
