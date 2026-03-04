package top.niunaijun.blackbox.core.system.notification;

import android.app.NotificationChannel;
import android.os.Build;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages notification channels for virtual apps.
 */
public class NotificationChannelManager {
    // packageName -> channelId -> NotificationChannel
    private final Map<String, Map<String, NotificationChannel>> mChannels = new HashMap<>();

    public void createChannel(String packageName, NotificationChannel channel) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        mChannels.computeIfAbsent(packageName, k -> new HashMap<>())
                .put(channel.getId(), channel);
    }

    public NotificationChannel getChannel(String packageName, String channelId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return null;
        Map<String, NotificationChannel> pkgChannels = mChannels.get(packageName);
        if (pkgChannels == null) return null;
        return pkgChannels.get(channelId);
    }
}
