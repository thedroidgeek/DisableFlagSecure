package fi.veetipaananen.android.disableflagsecure;

import android.os.Build;
import android.view.Display;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DisableFlagSecureModule implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedHelpers.findAndHookMethod(Window.class, "setFlags", int.class, int.class,
                mRemoveSecureFlagHook);
        XposedHelpers.findAndHookMethod(Window.class, "addFlags", int.class,
                mRemoveAddFlagsHook);
        if (Build.VERSION.SDK_INT >= 17) {
            XposedHelpers.findAndHookMethod(SurfaceView.class, "setSecure", boolean.class,
                    mRemoveSetSecureHook);
            XposedHelpers.findAndHookMethod(Display.class, "getFlags", mAddDisplaySecureFlagHook);
        }
    }

    private final XC_MethodHook mRemoveSecureFlagHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            Integer flags = (Integer) param.args[0];
            flags &= ~WindowManager.LayoutParams.FLAG_SECURE;
            param.args[0] = flags;

            Integer mask = (Integer) param.args[1];
            mask &= ~WindowManager.LayoutParams.FLAG_SECURE;
            param.args[1] = mask;
        }
    };

    private final XC_MethodHook mRemoveAddFlagsHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            Integer flags = (Integer) param.args[0];
            flags &= ~WindowManager.LayoutParams.FLAG_SECURE;
            param.args[0] = flags;

        }
    };

    private final XC_MethodHook mRemoveSetSecureHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            param.args[0] = false;
        }
    };

    private final XC_MethodHook mAddDisplaySecureFlagHook = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            param.setResult((int)param.getResult() | Display.FLAG_SECURE | Display.FLAG_SUPPORTS_PROTECTED_BUFFERS);
        }
    };
}
