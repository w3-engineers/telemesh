package com.w3engineers.unicef.util.lib.circle_checkbox;

import android.content.Context;
import android.support.annotation.NonNull;

class CompatUtils {
    static int dp2px(@NonNull Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
