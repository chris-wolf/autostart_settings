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
        //    Pair("com.android.settings",  "com.android.settings.Settings\$HighPowerApplicationsActivity") testen on OnePlus, but default is battery safer off for apps so no need to open it
            ).map { Intent().setComponent(ComponentName(it.first, it.second)) }.toTypedArray(),
        // OnePlus alternate method (Intent action-based)
        Intent("com.android.settings.action.BACKGROUND_OPTIMIZE").addCategory(Intent.CATEGORY_DEFAULT),
        )

    private fun canOpenAutoStartSettings(): Boolean {
        // Return true if any intent in the list can be opened.
        // The '::' creates a reference to the canOpenIntent function.
        return autoStartActivities.any(::canOpenIntent)
    }

    private fun canOpenbatterySaferSettings(): Boolean {
        // This is equivalent to: batterySaferActivity.any { intent -> canOpenIntent(intent) }
        return batterySaferActivity.any(::canOpenIntent)
    }


    private fun openAutoStartSettings(): Boolean {
        return tryOpenFirstAvailableIntent(autoStartActivities)
    }

    private fun openBatterySaferSettings(): Boolean {
        return tryOpenFirstAvailableIntent(batterySaferActivity)
    }

    /**
     * Tries to find the first resolvable and safe-to-open intent from a list
     * and starts the corresponding activity.
     *
     * @param intents The list of Intents to try.
     * @return `true` if an activity was successfully started, `false` otherwise.
     */
    private fun tryOpenFirstAvailableIntent(intents: List<Intent>): Boolean {
        try {
            // Find the first intent that passes our validation using the helper function.
            // The 'let' block executes only if a valid intent is found.
            intents.firstOrNull(::canOpenIntent)?.let { intentToOpen ->
                binding?.activity?.startActivity(intentToOpen)
                return true // Activity was started successfully.
            }
        } catch (e: Exception) {
            // Log any exception that occurs during startActivity
            Log.e("IntentLauncher", "Could not start activity from intent list.", e)
        }
        // Return false if no suitable intent was found or if an exception occurred.
        return false
    }

    /**
     * Checks if a single Intent can be resolved to an activity
     * that is exported and does not require a permission.
     *
     * @param intent The Intent to check.
     * @return `true` if the intent resolves to a suitable activity, `false` otherwise.
     */
    private fun canOpenIntent(intent: Intent): Boolean {
        val pm = binding?.activity?.packageManager ?: return false

        // Resolve the activity for the given intent
        val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

        // Use a null-safe let block to check the activity's properties
        return resolveInfo?.activityInfo?.let { activityInfo ->
            // The activity is valid if it's exported and has no permission requirement
            activityInfo.exported && activityInfo.permission == null
        } ?: false // If resolveInfo or activityInfo is null, the result is false.
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