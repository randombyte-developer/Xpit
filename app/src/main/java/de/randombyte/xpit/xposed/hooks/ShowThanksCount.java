package de.randombyte.xpit.xposed.hooks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.randombyte.xpit.R;
import de.randombyte.xpit.xposed.Commons;
import de.randombyte.xpit.xposed.Xpit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Shows the number of "Thanks" given for a post.
 */
public class ShowThanksCount extends HookProvider {

    @Override
    public void initHooks(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        super.initHooks(loadPackageParam);

        Commons.forumPost_toView.hook(new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //Find existing Views
                Button answerButton = (Button) XposedHelpers.getObjectField(
                        ((View) param.getResult()).getTag(), "answerButton");
                ViewGroup buttonGroup = (ViewGroup) answerButton.getParent();
                ViewGroup buttonGroupParent = (ViewGroup) buttonGroup.getParent();

                //Inflate own layout
                RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(Xpit.ownContext)
                        .inflate(R.layout.thanks_number_textview, null);
                TextView textView = (TextView) relativeLayout.findViewById(R.id.thanks_number);
                textView.setText("Danke: " + XposedHelpers.getIntField(param.thisObject, "rating"));

                //Add Views to new container
                buttonGroupParent.removeView(buttonGroup);
                relativeLayout.addView(buttonGroup);
                buttonGroupParent.addView(relativeLayout);
            }
        });
    }
}
