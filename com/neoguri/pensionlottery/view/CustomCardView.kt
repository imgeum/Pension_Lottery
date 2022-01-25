package com.neoguri.pensionlottery.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.cardview.widget.CardView
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.databinding.ActivityMainBinding

class CustomCardView(context: Context, attrs: AttributeSet) : CardView(context, attrs) {

    private var mContext = context

    private var xDelta: Int = 0
    private var yDelta: Int = 0

    private lateinit var mBinding: ActivityMainBinding

    fun setBinding(binding: ActivityMainBinding) {
        this.mBinding = binding
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val xx = event.rawX.toInt()
        val yy = event.rawY.toInt()

        val r = Rect()

        mBinding.appBarMainActivity.contentMainActivity.hiddenLayoutGetCenter.getGlobalVisibleRect(r) // RootView 레이아웃을 기준으로한 좌표.

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                xDelta = (xx - mBinding.appBarMainActivity.contentMainActivity.contentMainDown.hiddenLayout.translationX).toInt()
                yDelta = (yy - mBinding.appBarMainActivity.contentMainActivity.contentMainDown.hiddenLayout.translationY).toInt()
            }
            MotionEvent.ACTION_UP -> {
                val leftRightFlag = event.rawX.toInt() - xDelta
                val animation: Animation?

                animation = if (leftRightFlag > 0) {
                    AnimationUtils.loadAnimation(
                        mContext,
                        R.anim.anim_translate_right
                    )
                } else {
                    AnimationUtils.loadAnimation(mContext, R.anim.anim_translate_left)
                }

                animation!!.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {

                        if (leftRightFlag > 0) {
                            mBinding.appBarMainActivity.contentMainActivity.contentMainDown.hiddenLayout.translationX = (mBinding.appBarMainActivity.contentMainActivity.contentMainDown.hiddenLayout.width).toFloat()
                        } else {
                            mBinding.appBarMainActivity.contentMainActivity.contentMainDown.hiddenLayout.translationX = (-mBinding.appBarMainActivity.contentMainActivity.contentMainDown.hiddenLayout.width).toFloat()
                        }

                    }
                })
                animation.duration = 300 //애니메이션 동작시간 MAIN_REFRESH초
                animation.isFillEnabled = true
                mBinding.appBarMainActivity.contentMainActivity.contentMainDown.hiddenLayout.startAnimation(animation)

            }

            MotionEvent.ACTION_MOVE -> {

                mBinding.appBarMainActivity.contentMainActivity.contentMainDown.hiddenLayout.translationX = (xx - xDelta).toFloat()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

}