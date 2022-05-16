package com.example.in2000_team32.ui.map


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.*
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team32.api.DataSourceRepository
import com.example.in2000_team32.databinding.FragmentMapBinding
import java.util.*


class MapFragment : Fragment() {
    private var VARSEL_TID: Long = 3600000
    private var tidText : TextView? = null
    private var cdTimer: CountDownTimer? = null
    private var cdtRunning: Boolean = false
    private var cdtTimeLeft: Long = VARSEL_TID
    private var cdtEndTime: Long? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private lateinit var binding: FragmentMapBinding
    private lateinit var dataSourceRepository: DataSourceRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dataSourceRepository = DataSourceRepository(requireContext())

        //Viser og gjemmer brent seg tips
        binding.brentSegShow.setOnClickListener() {
            // previously invisible view
            val myView: View = binding.brentSegTips

            // Check if the runtime version is at least Lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // get the center for the clipping circle
                val cx = myView.width / 2
                val cy = myView.height / 2

                // get the final radius for the clipping circle
                val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

                // create the animator for this view (the start radius is zero)
                val anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius)
                // make the view visible and start the animation
                myView.visibility = View.VISIBLE
                anim.start()
            } else {
                // set the view to invisible without a circular reveal animation below Lollipop
                myView.visibility = View.GONE
            }

        }
        //Gjemmer
        binding.brentSegHide.setOnClickListener() {
            // previously visible view
            val myView: View = binding.brentSegTips

            // Check if the runtime version is at least Lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // get the center for the clipping circle
                val cx = myView.width / 2
                val cy = myView.height / 2

                // get the initial radius for the clipping circle
                val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

                // create the animation (the final radius is zero)
                val anim =
                    ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0f)

                // make the view invisible when the animation is done
                anim.addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        myView.visibility = View.GONE
                    }
                })

                // start the animation
                anim.start()
            } else {
                // set the view to visible without a circular reveal animation below Lollipop
                myView.visibility = View.VISIBLE
            }

        }

        binding.smurtSegButton.setOnClickListener{
            timer()
        }
        binding.smurtSegAvbryt.setOnClickListener{
            stoppTimer()
        }

        createNotificationChannel()
        val smurtButton = binding.smurtSegButton
        tidText = binding.tidIgjenTid


        smurtButton.setOnClickListener {

            if (dataSourceRepository.getNotifPref()){
                //Setter opp notification
                val intent = Intent(activity, Notification::class.java)

                val pendingIntent = PendingIntent.getBroadcast(activity, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

                val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val timeAtBtnClick = System.currentTimeMillis()
                val secondsInMillis = VARSEL_TID

                alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtBtnClick + secondsInMillis, pendingIntent)
            }

            //Setter opp nedtelling i appen
            if (cdtRunning) {
                restartTimer()
            } else {
                cdtTimeLeft = VARSEL_TID
                startTimer()
            }

        }

        return root
    }

    fun timer(){
        binding.smurtSegButton.visibility = View.GONE
        binding.smurtSegAvbryt.visibility = View.VISIBLE
        binding.smurtSegIgjen.visibility = View.VISIBLE
    }

    fun stoppTimer(){
        binding.smurtSegButton.visibility = View.VISIBLE
        binding.smurtSegAvbryt.visibility = View.GONE
        binding.smurtSegIgjen.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()

        val prefs: SharedPreferences? = activity?.getSharedPreferences("tidIgjen", MODE_PRIVATE)

        if (prefs != null) {
            with (prefs.edit()) {
                putLong("millisLeft", cdtTimeLeft)
                putBoolean("timerRunning", cdtRunning)
                cdtEndTime?.let { putLong("endTime", it) }
                apply()
            }
        }
        cdTimer?.cancel()

    }

    override fun onStart() {
        super.onStart()

        //Henter nedtellings data
        val prefs: SharedPreferences? = activity?.getSharedPreferences("tidIgjen", MODE_PRIVATE)
        cdtTimeLeft = prefs!!.getLong("millisLeft", VARSEL_TID)
        cdtRunning = prefs.getBoolean("timerRunning", false)

        //Oppdaterer texten på siden
        var minutes = ((cdtTimeLeft / 1000) / 60).toInt()
        var seconds = ((cdtTimeLeft / 1000) % 60).toInt()
        var timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        tidText!!.setText(timeLeft)

        if (cdtRunning){
            cdtEndTime = prefs.getLong("endTime", 0)
            cdtTimeLeft = cdtEndTime!! - System.currentTimeMillis()

            if (cdtTimeLeft < 0){
                cdtTimeLeft = 0
                cdtRunning = false

                minutes = ((cdtTimeLeft / 1000) / 60).toInt()
                seconds = ((cdtTimeLeft / 1000) % 60).toInt()
                timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                tidText!!.setText(timeLeft)
            } else {
                startTimer()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Solbrent notification"
            val descriptionText = "Husk på å ta på mer solkrem"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notifySolbrent", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager = activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startTimer() {

        cdtEndTime = System.currentTimeMillis() + cdtTimeLeft
        cdTimer?.cancel()

        cdTimer = object: CountDownTimer(cdtTimeLeft, 1000){
            override fun onTick(millisUntillFinished: Long) {
                cdtTimeLeft = millisUntillFinished
                val minutes = ((cdtTimeLeft / 1000) / 60).toInt()
                val seconds = ((cdtTimeLeft / 1000) % 60).toInt()

                val timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                tidText!!.setText(timeLeft)
            }

            override fun onFinish() {
                cdtRunning = false
                cdtTimeLeft = VARSEL_TID
            }

        }.start()

        cdtRunning = true
    }

    private fun restartTimer() {
        cdtTimeLeft = VARSEL_TID
        cdTimer?.cancel()
        startTimer()
    }
}

