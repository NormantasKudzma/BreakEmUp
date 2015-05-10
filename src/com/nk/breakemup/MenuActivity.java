package com.nk.breakemup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_layout);
		
		setUpButtonListeners();
	}
	
	private void setUpButtonListeners(){
		findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MenuActivity.this, GameActivity.class);
				startActivity(i);
			}
			
		});
	}
}
