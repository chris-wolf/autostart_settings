package dev.cwolf.autostartSettings.autostart_settings

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.Log
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding


/** AutostartSettingsPlugin */
class AutostartSettingsPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private var binding: ActivityPluginBinding? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "autostart_settings")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "canOpen" -> {
                val autoStart: Boolean = call.argument("autoStart") as? Boolean ?: true
                val batterySafer: Boolean = call.argument("batterySafer") as? Boolean ?: true
                if (autoStart && canOpenAutoStartSettings()) {
                    result.success(true)
                    return
                }
                if (batterySafer && canOpenbatterySaferSettings()) {
                    result.success(true)
                    return
                }
                result.success(false)
            }
            "open" -> {
                val autoStart: Boolean = call.argument("autoStart") as? Boolean ?: true
                val batterySafer: Boolean = call.argument("batterySafer") as? Boolean ?: true
                if (autoStart && openAutoStartSettings()) {
                    result.success(true)
                    return
                }
                if (batterySafer && openBatterySaferSettings()) {
                    result.success(true)
                    return
                }
                result.success(false)
            }
            else -> {
                result.notImplemented()
            }
        }
    }


    /**
     * List of autostart activities by manufacturers, partly gathered from:
     *  https://stackoverflow.com/a/53904589
     *  https://stackoverflow.com/questions/48945300/how-to-open-window-of-autostart-application-for-all-devices
     *  https://stackoverflow.com/questions/39366231/how-to-check-miui-autostart-permission-programmatically
     *  http://lastwarmth.win/2019/09/06/keepalive/
     *
     *  if you want to contribute, you can figure out the current actity on the device with adb:
     *  list all activityName:  adb shell dumpsys package | grep Activity
     *  finding current active/resumed activity: adb shell dumpsys activity activities | grep mResumedActivity
     */
    private val autoStartActivities = arrayListOf(
        *arrayListOf(
            // Xiaomi  Remi
            Pair(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            ),
            // Oppo / RealMe
            Pair(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            ),
            Pair(
                "com.coloros.safecenter",
                "com.coloros.safecenter.startupapp.StartupAppListActivity"
            ),
            Pair("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"),
            Pair(
                "com.coloros.safecenter",
                "com.coloros.privacypermissionsentry.PermissionTopActivity"
            ),
            Pair("com.coloros.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"),
            Pair(
                "com.coloros.safecenter",
                "com.coloros.privacypermissionsentry.PermissionTopActivity.Startupmanager"
            ),
            // Vivo
            Pair(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
            ),
            // LeEco auto-start activity path
            Pair("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"),
            // MediaTek
            Pair(
                "com.mediatek.duraspeed",
                "com.mediatek.duraspeed.view.RunningBoosterMainActivity"
            ),
            // iQOO
            Pair("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"),
            // Tecno
            Pair("com.transsion.phonemaster", "com.cyin.himgr.autostart.AutoStartActivity"),
            // Huawei
            Pair(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"
            ),
            Pair(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
            ),
            Pair(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity"
            ),
            Pair("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"),

        ).map { Intent().setComponent(ComponentName(it.first, it.second)) }.toTypedArray(),
        // Xiaomi  Remi
        Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT),
        // Asus
        Intent().setComponent(
            ComponentName(
                "com.asus.mobilemanager",
                "com.asus.mobilemanager.entry.FunctionActivity"
            )
        ).setData(
            Uri.parse("mobilemanager://function/entry/AutoStart")
        ),
        Intent().setComponent(ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity")),
        Intent().setComponent(
            ComponentName(
                "com.asus.mobilemanager",
                "com.asus.mobilemanager.autostart.AutoStartActivity"
            )
        ),
        // Meizu
        Intent().setComponent(ComponentName.unflattenFromString("com.meizu.safe/.SecurityCenterActivity")),
        // OnePlus
        Intent().setComponent(
            ComponentName
                .unflattenFromString("com.oneplus.security/.chainlaunch.view.ChainLaunchAppListActivity")
        ),
        // HTC
        Intent().setComponent(
            ComponentName.unflattenFromString("com.htc.pitroad/.landingpage.activity.LandingPageActivity")
        )
    )

    val batterySaferActivity = arrayListOf(
        *arrayListOf(
        // Samsung
        Pair("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"),
        Pair("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity"),
        Pair("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.battery.BatteryActivity"),
        Pair("com.samsung.android.sm", "com.samsung.android.sm.battery.ui.usage.CheckableAppListActivity"),
        Pair("com.samsung.android.sm", "com.samsung.android.sm.battery.ui.BatteryActivity"),
    // iQOO
        Pair("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"),
        // Nokia
        Pair(
            "com.evenwell.powersaving.g3",
            "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity"
        ),
        Pair("com.asus.mobilemanager", "com.asus.mobilemanager.powersaver.PowerSaverSettings"),
        // LG (Older devices)
            Pair(
                "com.android.settings",
                "com.android.settings.Settings\$HighPowerApplicationsActivity"
            )
            ).map { Intent().setComponent(ComponentName(it.first, it.second)) }.toTypedArray(),
        // OnePlus alternate method (Intent action-based)
        Intent("com.android.settings.action.BACKGROUND_OPTIMIZE").addCategory(Intent.CATEGORY_DEFAULT),
        )

    private fun canOpenAutoStartSettings(): Boolean {
        val pm = binding?.activity?.packageManager ?: return false
        for (intent in autoStartActivities) {
            val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveInfo?.activityInfo != null) {
                val activityInfo = resolveInfo.activityInfo
                // Check if exported and no permission is required
                if (activityInfo.exported && activityInfo.permission == null) {
                    return true
                }
            }
        }
        return false
    }

    private fun canOpenbatterySaferSettings(): Boolean {
        val pm = binding?.activity?.packageManager ?: return false
        for (intent in batterySaferActivity) {
            val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveInfo?.activityInfo != null) {
                val activityInfo = resolveInfo.activityInfo
                // Check if exported and no permission is required
                if (activityInfo.exported && activityInfo.permission == null) {
                    return true
                }
            }
        }
        return false
    }

    private fun openAutoStartSettings(): Boolean {
        try {
            autoStartActivities.firstOrNull { isIntentResolved(it) }?.let { intent ->
                val list: List<ResolveInfo> =
                    binding?.activity?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY) ?: emptyList()
                if (list?.isNotEmpty() == true) {
                    binding?.activity?.startActivity(intent)
                    return true;
                }
            }
        } catch (e: Exception) {
            Log.e("exc", e.toString())
        }
        return false
    }

    private fun openBatterySaferSettings(): Boolean {
        try {
            batterySaferActivity.firstOrNull { isIntentResolved(it) }?.let { intent ->
                val list: List<ResolveInfo> =
                    binding?.activity?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY) ?: emptyList()
                if (list.isNotEmpty()) {
                    binding?.activity?.startActivity(intent)
                    return true;
                }
            }
        } catch (e: Exception) {
            Log.e("exc", e.toString())
        }
        return false
    }


    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.binding = binding
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.binding = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        this.binding = binding
    }

    override fun onDetachedFromActivity() {
        this.binding = null
    }

}
