package com.computer.service.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.computer.service.R;

public class ActivitySplash extends AppCompatActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		/** Creates a count down timer, which will be expired after 5000 milliseconds */
		new CountDownTimer(5000, 1000) {

			/** This method will be invoked on finishing or expiring the timer */
			@Override
			public void onFinish() {
				/** Creates an intent to start new activity */
				Intent intent = new Intent(getBaseContext(), MainActivity.class);

				//memulai activity baru ketika waktu timer habis
				startActivity(intent);

				//menutup layar activity
				finish();

			}

			@Override
			public void onTick(long millisUntilFinished) {

			}
		}.start();

	}
}