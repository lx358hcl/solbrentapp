package com.example.in2000_team32

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.in2000_team32.databinding.ActivityOnboardingBinding
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPager : ViewPager
    private lateinit var viewPagerAdapter : ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val relativeLayout = binding.layout1
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)
        var btn = findViewById<Button>(R.id.btn)

        val animationDrawable = relativeLayout.background as AnimationDrawable
        addAnimationDrawable(animationDrawable)

        viewPager = findViewById(R.id.viewpager)
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        dotsIndicator.setViewPager(viewPager)


        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0){
                    relativeLayout.setBackgroundResource(R.drawable.gradient_animation_123)
                    val animationDrawable = relativeLayout.background as AnimationDrawable
                    addAnimationDrawable(animationDrawable)
                }

                if (position == 1){
                    relativeLayout.setBackgroundResource(R.drawable.gradient_animation_456)
                    val animationDrawable = relativeLayout.background as AnimationDrawable
                    addAnimationDrawable(animationDrawable)
                }

                if (position == 2){
                    relativeLayout.setBackgroundResource(R.drawable.gradient_animation_789)
                    val animationDrawable = relativeLayout.background as AnimationDrawable
                    addAnimationDrawable(animationDrawable)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }
    fun addAnimationDrawable(animationDrawable: AnimationDrawable){
        animationDrawable.setEnterFadeDuration(100)
        animationDrawable.setExitFadeDuration(2000)
        animationDrawable.start()
    }
}