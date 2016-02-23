package de.randombyte.xpit.hooks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.randombyte.xpit.Commons;
import de.randombyte.xpit.R;
import de.randombyte.xpit.Xpit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Shows the number of "Thanks" given for a post.
 */
public class ShowThanksCount extends ActivatableHook {

    public ShowThanksCount() {
        super("thanksCount", "Danke-Anzahl", true);
    }

    @Override
    public void init(XC_LoadPackage.LoadPackageParam param) {
        registerHook(Commons.forumPost_toView, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //Find existing Views
                Button answerButton = (Button) XposedHelpers.getObjectField(
                        ((View) param.getResult()).getTag(), "answerButton");
                ViewGroup buttonGroup = (ViewGroup) answerButton.getParent();
                ViewGroup buttonGroupParent = (ViewGroup) buttonGroup.getParent();

                //Get value
                int rating1 = XposedHelpers.getIntField(param.thisObject, "rating");
                int rating2 = XposedHelpers.getIntField(param.thisObject, "numUserRatingsPositive");
                int rating = rating1 == 0 ? rating2 : rating1;

                //Inflate own layout
                RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(Xpit.OWN_CONTEXT)
                        .inflate(R.layout.thanks_number_textview, null);
                TextView textView = (TextView) relativeLayout.findViewById(R.id.thanks_number);
                textView.setText("Danke: " + rating);

                //Add Views to new container
                buttonGroupParent.removeView(buttonGroup);
                relativeLayout.addView(buttonGroup);
                buttonGroupParent.addView(relativeLayout);
            }
        });
    }
}
