package com.gucheng.statistichelper.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021/10/27.
 */
class MoveFloatingActionButton extends FloatingActionButton {

    public MoveFloatingActionButton(@NonNull @NotNull Context context) {
        super(context);
    }

    public MoveFloatingActionButton(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MoveFloatingActionButton(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float lastX = 0;
    float lastY = 0;

    @Override
    public boolean onTouchEvent(@NonNull @NotNull MotionEvent ev) {
        Log.d("liuwei_action", "action is " + ev.getAction()
                + ",RawX is " + ev.getRawX() + ", deltaX is " + (ev.getRawX() - lastX));
        Log.d("liuwei_trans", "transx is " + getTranslationX() + ",trannsY is " + getTranslationY());
        lastX = ev.getRawX();
        lastY = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("liuwei", "ev.getRawX() is " + ev.getRawX() + ", lastX is " + lastX);
//                ObjectAnimator.ofFloat(this, "translationX", ev.getRawX() + getTranslationX() - lastX).start();
//                ObjectAnimator.ofFloat(this, "translationY", ev.getRawY() + getTranslationY() - lastY).start();
//                setTranslationX(ev.getRawX() + getTranslationX() - lastX);
//                setTranslationY(ev.getRawY() + getTranslationY() - lastY);
//                setTranslationY(getTranslationX() + ev.getRawY() - lastY);
                float deltaX = ev.getRawX() - lastX;
                float deltaY = ev.getRawY() - lastY;
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)getLayoutParams();
                layoutParams.leftMargin = layoutParams.leftMargin + (int)deltaX;
                layoutParams.topMargin = layoutParams.topMargin + (int)deltaY;
                setLayoutParams(layoutParams);

                break;
            case MotionEvent.ACTION_UP:
//                invalidate();
                break;
        }
        lastX = ev.getRawX();
        lastY = ev.getRawY();
//        invalidate();
        return true;
    }
}
