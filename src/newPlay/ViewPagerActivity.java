package com.example.newPlay;

import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ViewPagerActivity extends Activity implements
		OnSeekBarChangeListener {

	private static int NUM_OF_VIEWS = 4;
	private Context context = this;
	Vector<Node> vec;
	@SuppressWarnings("unused")
	private Object mActionMode;
	private LinearLayout titles, lSeekBar;
	public static String selectedImagePath;
	static int funToRun, ivBitmap;
	public static boolean progressBarFlag;
	public static double skbaarlevel, skBaarRed, skBaarGreen, skBaarBlue,
			softFocusX, softFocusY;
	public static float softFocusFlag;
	private ViewPager awesomePager;
	private static ImageButton titlesButton[], ibSubSeekBar, ibAddSeekBar;
	private ImageView ivOrig;
	private Button bCrop, bInvert, bBrightness, bSaturation, bContrast, bTint,
			bHue, bColorBoost, bBandW, bNegative, bSepia, bRGBSwap, bSmooth,
			bSharp, bSoftFocus, bBlur;
	private SeekBar seekBar, seekBarRed, seekBarGreen, seekBarBlue;
	private LinearLayout lCrop, lBright, lSmooth, lEffects, lColorBoost;
	private TextView tvBitmap, seekBarText, tvSeekBarValue;
	private HorizontalScrollView svBright;
	public static ProgressDialog progressDialog = null;

	Functions func;
	Thread thr;
	Intent cropIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewpager);
		selectedImagePath = MainActivity.selectedImagePath;
		titles = (LinearLayout) findViewById(R.id.vp_title);
		lSeekBar = (LinearLayout) findViewById(R.id.lSeekBar);
		lColorBoost = (LinearLayout) findViewById(R.id.lColorBoost);
		awesomePager = (ViewPager) findViewById(R.id.vp_pager);
		ivOrig = (ImageView) findViewById(R.id.ivOrig);
		tvBitmap = (TextView) findViewById(R.id.tvBitmap);
		seekBarText = (TextView) findViewById(R.id.seekBarText);
		tvSeekBarValue = (TextView) findViewById(R.id.tvSeekBarValue);
		seekBar = (SeekBar) findViewById(R.id.seekBarAll);
		seekBarRed = (SeekBar) findViewById(R.id.seekBarRed);
		seekBarGreen = (SeekBar) findViewById(R.id.seekBarGreen);
		seekBarBlue = (SeekBar) findViewById(R.id.seekBarBlue);
		ibAddSeekBar = (ImageButton) findViewById(R.id.ibAddSeekBar);
		ibSubSeekBar = (ImageButton) findViewById(R.id.ibSubSeekBar);
		titlesButton = new ImageButton[NUM_OF_VIEWS];
		cropIntent = new Intent(getApplicationContext(), CropIntent.class);

		initializeLayouts();
		loadTitles();
		setListeners();
		ivBitmap = 0;
        progressBarFlag=false;
		// cropIntent = new Intent(getApplicationContext(),CropIntent.class);
		func = new Functions(context);
		refresh();
		awesomePager.setAdapter(new AwesomePagerAdapter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.main, menu);

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save:
			funToRun = Functions.SAVELARGE;
			new AddTask().execute();
			return (true);
		case R.id.undo:
			funToRun = Functions.UNDOONE;
			new AddTask().execute();
			return (true);
		case R.id.reset:
			funToRun = Functions.RELOAD;
			new AddTask().execute();
			return (true);

		}

		return (super.onOptionsItemSelected(item));
	}

	private void refresh() {
		tvBitmap.setVisibility(View.INVISIBLE);
		ivOrig.setImageBitmap(Functions.newBitmap);
		// ivConv.setImageBitmap(Functions.newBitmap);
        if(funToRun==Functions.SATURATION)
        {
        	int i=20;
        	while((i--)!=0)
        	{
        		ivOrig.setImageBitmap(Functions.newBitmap);
        	}
        }
	}

	private void initializeLayouts() {

		lCrop = new LinearLayout(context);
		lCrop.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lCrop.setOrientation(LinearLayout.HORIZONTAL);
		lCrop.setVisibility(View.VISIBLE);

		bCrop = new Button(context);
		bCrop.setText("CROP");
		bCrop.setBackgroundColor(Color.TRANSPARENT);
		bCrop.setTextColor(Color.WHITE);
		bCrop.setTypeface(Typeface.DEFAULT_BOLD);
		bCrop.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lCrop.addView(bCrop);
		bInvert = new Button(context);
		bInvert.setText("MIRROR");
		bInvert.setBackgroundColor(Color.TRANSPARENT);
		bInvert.setTextColor(Color.WHITE);
		bInvert.setTypeface(Typeface.DEFAULT_BOLD);
		bInvert.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lCrop.addView(bInvert);

		svBright = new HorizontalScrollView(context);
		svBright.setHorizontalScrollBarEnabled(true);
		lBright = new LinearLayout(context);
		lBright.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lBright.setBackgroundColor(Color.TRANSPARENT);
		lBright.setOrientation(LinearLayout.HORIZONTAL);
		lBright.setVisibility(View.VISIBLE);
		lBright.setHorizontalScrollBarEnabled(true);
		svBright.addView(lBright);

		bBrightness = new Button(context);
		bBrightness.setText("Brightness");
		bBrightness.setTextColor(Color.WHITE);
		bBrightness.setBackgroundColor(Color.TRANSPARENT);
		bBrightness.setTypeface(Typeface.DEFAULT_BOLD);
		bBrightness.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lBright.addView(bBrightness);
		bSaturation = new Button(context);
		bSaturation.setText("Saturation");
		bSaturation.setTextColor(Color.WHITE);
		bSaturation.setBackgroundColor(Color.TRANSPARENT);
		bSaturation.setTypeface(Typeface.DEFAULT_BOLD);
		bSaturation.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lBright.addView(bSaturation);
		bContrast = new Button(context);
		bContrast.setText("Contrast");
		bContrast.setTextColor(Color.WHITE);
		bContrast.setBackgroundColor(Color.TRANSPARENT);
		bContrast.setTypeface(Typeface.DEFAULT_BOLD);
		bContrast.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lBright.addView(bContrast);
		bTint = new Button(context);
		bTint.setText("Tint");
		bTint.setBackgroundColor(Color.TRANSPARENT);
		bTint.setTextColor(Color.WHITE);
		bTint.setTypeface(Typeface.DEFAULT_BOLD);
		bTint.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lBright.addView(bTint);
		bHue = new Button(context);
		bHue.setText("Hue");
		bHue.setBackgroundColor(Color.TRANSPARENT);
		bHue.setTextColor(Color.WHITE);
		bHue.setTypeface(Typeface.DEFAULT_BOLD);
		bHue.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lBright.addView(bHue);
		bColorBoost = new Button(context);
		bColorBoost.setText("ColorBoost");
		bColorBoost.setTextColor(Color.WHITE);
		bColorBoost.setBackgroundColor(Color.TRANSPARENT);
		bColorBoost.setTypeface(Typeface.DEFAULT_BOLD);
		bColorBoost.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lBright.addView(bColorBoost);

		lEffects = new LinearLayout(context);
		lEffects.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lEffects.setOrientation(LinearLayout.HORIZONTAL);
		lEffects.setVisibility(View.VISIBLE);

		bBandW = new Button(context);
		bBandW.setText("BandW");
		bBandW.setTextColor(Color.WHITE);
		bBandW.setBackgroundColor(Color.TRANSPARENT);
		bBandW.setTypeface(Typeface.DEFAULT_BOLD);
		bBandW.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lEffects.addView(bBandW);
		bNegative = new Button(context);
		bNegative.setText("Negative");
		bNegative.setTextColor(Color.WHITE);
		bNegative.setBackgroundColor(Color.TRANSPARENT);
		bNegative.setTypeface(Typeface.DEFAULT_BOLD);
		bNegative.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lEffects.addView(bNegative);
		bSepia = new Button(context);
		bSepia.setText("Sepia");
		bSepia.setTextColor(Color.WHITE);
		bSepia.setTypeface(Typeface.DEFAULT_BOLD);
		bSepia.setBackgroundColor(Color.TRANSPARENT);
		bSepia.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lEffects.addView(bSepia);
		bRGBSwap = new Button(context);
		bRGBSwap.setText("RGB Swap");
		bRGBSwap.setTextColor(Color.WHITE);
		bRGBSwap.setBackgroundColor(Color.TRANSPARENT);
		bRGBSwap.setTypeface(Typeface.DEFAULT_BOLD);
		bRGBSwap.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lEffects.addView(bRGBSwap);

		lSmooth = new LinearLayout(context);
		lSmooth.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lSmooth.setOrientation(LinearLayout.HORIZONTAL);
		lSmooth.setVisibility(View.VISIBLE);

		bSmooth = new Button(context);
		bSmooth.setText("Smooth");
		bSmooth.setBackgroundColor(Color.TRANSPARENT);
		bSmooth.setTextColor(Color.WHITE);
		bSmooth.setTypeface(Typeface.DEFAULT_BOLD);
		bSmooth.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lSmooth.addView(bSmooth);
		bSharp = new Button(context);
		bSharp.setText("Sharp");
		bSharp.setTextColor(Color.WHITE);
		bSharp.setTypeface(Typeface.DEFAULT_BOLD);
		bSharp.setBackgroundColor(Color.TRANSPARENT);
		bSharp.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lSmooth.addView(bSharp);
		bSoftFocus = new Button(context);
		bSoftFocus.setText("SoftFocus");
		bSoftFocus.setBackgroundColor(Color.TRANSPARENT);
		bSoftFocus.setTextColor(Color.WHITE);
		bSoftFocus.setTypeface(Typeface.DEFAULT_BOLD);
		bSoftFocus.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lSmooth.addView(bSoftFocus);
		bBlur = new Button(context);
		bBlur.setText("Blur");
		bBlur.setTextColor(Color.WHITE);
		bBlur.setTypeface(Typeface.DEFAULT_BOLD);
		bBlur.setBackgroundColor(Color.TRANSPARENT);
		bBlur.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		lSmooth.addView(bBlur);

	}

	private void loadTitles() {
		final Display display = getWindowManager().getDefaultDisplay();
		Point outSize = new Point();
		display.getSize(outSize);
		final int width = outSize.x;

		for (int i = 0; i < NUM_OF_VIEWS; i++) {
			ImageButton ib = new ImageButton(context);

			switch (i) {
			case 0:
				ib.setImageResource(R.drawable.images_crop);
				break;
			case 1:
				ib.setImageResource(R.drawable.action_settings);
				break;

			case 2:
				ib.setImageResource(R.drawable.content_edit);
				break;

			case 3:
				ib.setImageResource(R.drawable.rating_half_important);
				break;

			}
			ib.setBackgroundColor(Color.TRANSPARENT);
			ib.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			ib.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			titlesButton[i] = ib;
			ib.setLayoutParams(new LayoutParams(width / 4,
					LayoutParams.WRAP_CONTENT));
			titles.addView(ib);
			final int position = i;
			ib.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					awesomePager.setCurrentItem(position, true);
				}
			});

		}

		moveTitle(0);
	}

	private void moveTitle(int position) {
		// WRITE THE CODE OF HIGLIGHTING THE CHOSEN ICON HERE FROM TITLES
		// neeche sab bakwas h
		/*
		 * final Display display = getWindowManager().getDefaultDisplay(); Point
		 * outSize = new Point(); display.getSize(outSize); final int width =
		 * outSize.x;
		 */
		ImageButton ib = (ImageButton) titles.getChildAt(position);
		for (int i = 0; i < titlesButton.length; i++) {
			if (ib == titlesButton[i]) {
				titlesButton[i].setAlpha((float) 1);
				titlesButton[i].setBackgroundColor(0x88008888);
			} else {
				titlesButton[i].setAlpha((float) 0.35);
				titlesButton[i].setBackgroundColor(0x00000000);
			}
		}

	}

	private void setListeners() {
		awesomePager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				moveTitle(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});

		bCrop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cropIntent.putExtra("image-path", selectedImagePath);
				cropIntent.putExtra("scale", false);
				cropIntent.putExtra("scaleUpIfNeeded", false);
				Log.e("bCropListener", "i m here bhayii");
				funToRun = Functions.CROPIMAGE;
				new AddTask().execute();
			}
		});
		bInvert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				funToRun = Functions.INVERTED;
				new AddTask().execute();
			}
		});
		bBrightness.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				funToRun = Functions.BRIGHTNESS;
				seekBar.setMax(200);
				seekBarText.setText("Brightness: ");
				seekBar.setProgress(100);

				showSeekbar();
			}

		});
		bSaturation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				funToRun = Functions.SATURATION;
				seekBar.setMax(250);
				seekBarText.setText("Saturation: ");
				seekBar.setProgress(50);
				showSeekbar();
			}
		});
		bContrast.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				funToRun = Functions.CONTRAST;
				seekBar.setMax(200);
				seekBarText.setText("Contrast: ");
				seekBar.setProgress(100);
				showSeekbar();
			}
		});
		bTint.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				funToRun = Functions.TINT;
				seekBar.setMax(360);
				seekBarText.setText("Tint: ");
				seekBar.setProgress(180);
				showSeekbar();
			}
		});
		bHue.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				funToRun = Functions.HUE;
				seekBar.setMax(360);
				seekBar.setProgress(180);
				showSeekbar();
				seekBarText.setText("Hue: ");
			}
		});
		bColorBoost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				skBaarRed = skBaarBlue = skBaarGreen = 100;
				funToRun = Functions.COLORBOOST;
				seekBarRed.setMax(200);
				seekBarRed.setProgress(100);
				seekBarGreen.setMax(200);
				seekBarGreen.setProgress(100);
				seekBarBlue.setMax(200);
				seekBarBlue.setProgress(100);
				seekBarText.setText("Color Boost:");
				mActionMode = ViewPagerActivity.this
						.startActionMode(mActionModeCallback);
				lColorBoost.setVisibility(View.VISIBLE);
				awesomePager.setVisibility(View.INVISIBLE);
				titles.setVisibility(View.INVISIBLE);
			}
		});
		bBandW.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				funToRun = Functions.BLACKANDWHITE;
				new AddTask().execute();
			}
		});
		bNegative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				funToRun = Functions.NEGATIVE;
				new AddTask().execute();
			}
		});
		bSepia.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				funToRun = Functions.SEPIA;
				new AddTask().execute();
			}
		});
		bRGBSwap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				funToRun = Functions.RGBSWAP;
				new AddTask().execute();
			}
		});
		bSmooth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				funToRun = Functions.SMOOTH;
				seekBar.setMax(5);
				seekBar.setProgress(0);
				seekBarText.setText("Smooth: ");
				showSeekbar();
			}
		});
		bSharp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				funToRun = Functions.SHARPENING;
				seekBar.setMax(5);
				seekBarText.setText("Sharp: ");
				seekBar.setProgress(0);
				showSeekbar();
			}
		});
		bSoftFocus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				softFocusFlag = 1;
				Toast.makeText(getApplicationContext(),
						"Click On Image for Soft Focus", Toast.LENGTH_SHORT)
						.show();
			}
		});
		bBlur.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				funToRun = Functions.BLUR;
				new AddTask().execute();
			}
		});
		ibAddSeekBar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				seekBar.setProgress(seekBar.getProgress() + 1);
				tvSeekBarValue.setText("" + seekBar.getProgress());
				new AddTask().execute();
			}
		});
		ibAddSeekBar.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				return false;
			}
		});
		ibSubSeekBar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				seekBar.setProgress(seekBar.getProgress() - 1);
				tvSeekBarValue.setText("" + seekBar.getProgress());
				new AddTask().execute();

			}
		});
		seekBar.setOnSeekBarChangeListener(this);
		seekBarRed.setOnSeekBarChangeListener(this);
		seekBarGreen.setOnSeekBarChangeListener(this);
		seekBarBlue.setOnSeekBarChangeListener(this);
		ivOrig.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int h, w;

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					if (softFocusFlag == 1) {
						h = Functions.height;
						w = Functions.width;
						if (((double) v.getHeight() / (double) v.getWidth()) > ((double) h / (double) w)) // landscape
																											// mode
						{
							Log.e("landscape mode",
									((double) v.getHeight() / (double) v
											.getWidth())
											+ ">"
											+ ((double) h / (double) w));
							h = (int) (((double) v.getWidth() / (double) w) * h);
							w = v.getWidth();
						} else // portrait
						{
							Log.e("portrait mode",
									((double) v.getHeight() / (double) v
											.getWidth())
											+ "<"
											+ ((double) h / (double) w));
							w = (int) (((double) v.getHeight() / (double) h) * w);
							h = v.getHeight();
						}

						softFocusX = ((double) (event.getX()) - ((double) v
								.getWidth() / 2.0)) / w + 0.5;
						softFocusY = ((double) (event.getY()) - ((double) v
								.getHeight() / 2.0)) / h + 0.5;
						// Toast.makeText(getApplicationContext(),softFocusX+" ~ "+softFocusY,Toast.LENGTH_SHORT).show();
						Log.e("softFocus", softFocusX + "~" + softFocusY);
						softFocusFlag = 0;
						funToRun = Functions.SOFTFOCUS;
						new AddTask().execute();
					} else if (ivBitmap == 0) {
						tvBitmap.setVisibility(View.VISIBLE);
						tvBitmap.bringToFront();
						ivOrig.setImageBitmap(Functions.bOrig);
					} else if (ivBitmap == 1) {
						tvBitmap.setVisibility(View.INVISIBLE);
						ivOrig.setImageBitmap(Functions.newBitmap);
					}
					ivBitmap = 1 - ivBitmap;
				}

				return false;
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			// Display the modified values
			funToRun = Functions.SAVECROP;
			new AddTask().execute();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showSeekbar() {
		// TODO Auto-generated method stub
		mActionMode = ViewPagerActivity.this
				.startActionMode(mActionModeCallback);
		lSeekBar.setVisibility(View.VISIBLE);
		tvSeekBarValue.setText("" + seekBar.getProgress());
		awesomePager.setVisibility(View.INVISIBLE);
		titles.setVisibility(View.INVISIBLE);
		Functions.undoFlag = 1;
	}

	private void hideSeekbar() {
		// TODO Auto-generated method stub
		lColorBoost.setVisibility(View.INVISIBLE);
		titles.setVisibility(View.VISIBLE);
		awesomePager.setVisibility(View.VISIBLE);
		lSeekBar.setVisibility(View.INVISIBLE);
	}

	

	class AddTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.e("preExecute", "here");
			if(funToRun==Functions.SAVELARGE)
			progressDialog = ProgressDialog.show(ViewPagerActivity.this,"Please Wait...", "Applying Effects...");
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			runFunctionsHere();
            
			Log.e("doInBackgroud", "here");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.e("postExecute", "here");
			if (progressDialog!=null)
			if (progressDialog.isShowing())   progressDialog.dismiss();
				
			refresh();
		}

	}

	private void runFunctionsHere() {

		try {
			func.runFunction();

			if (funToRun == Functions.SAVELARGE
					|| funToRun == Functions.CROPIMAGE) {
				vec = Functions.vec;
				while (!vec.isEmpty()) {
					try {
						funToRun = (vec.firstElement()).fun;
						Functions.flag = (vec.firstElement()).val;
						vec.remove((vec.firstElement()));

						if (funToRun == Functions.CROPIMAGE) {
							Functions.savingFlag = 0;
							funToRun = Functions.SAVEIMAGE;
						}
						if (funToRun == Functions.SAVELARGE)
							funToRun = Functions.SAVEIMAGE;

						func.runFunction();

						if (funToRun == Functions.SAVEIMAGE)
							vec.removeAllElements();
					} catch (Exception e) {
						Log.e("Exception at SAveLarg", funToRun + "" + e);
					}
				}
				if (Functions.savingFlag == 1) {
					Functions.savingFlag = 0;
					finish();
				} else {

					startActivityForResult(cropIntent, 1);
				}

			}
		} catch (Exception e) {
			Log.e("runFunctions", e + "");
		}
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			// Assumes that you have "contexual.xml" menu resources
			inflater.inflate(R.menu.contextualbar, menu);
			return true;
		}

		// Called each time the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.cancel:
				funToRun = Functions.UNDOONE;
				new AddTask().execute();
				mode.finish(); // Action picked, so close the CAB
				return true;
			default:
				return false;
			}
		}

		// Called when the user exits the action mode
		public void onDestroyActionMode(ActionMode mode) {
			hideSeekbar();
			mActionMode = null;
		}
	};

	private class AwesomePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return NUM_OF_VIEWS;
		}

		@Override
		public Object instantiateItem(ViewGroup collection, int position) {

			switch (position) {
			case 0:
				collection.addView(lCrop, 0);
				return (lCrop);
			case 1:
				collection.addView(svBright, 0);
				return (svBright);
			case 2:
				collection.addView(lEffects, 0);
				return (lEffects);
			case 3:
				collection.addView(lSmooth, 0);
				return (lSmooth);
			}

			return (null);
		}

		@Override
		public void destroyItem(ViewGroup collection, int position, Object view) {
			collection.removeView((View) view);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}

		@Override
		public void finishUpdate(ViewGroup arg0) {
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(ViewGroup arg0) {
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		Functions.undoFlag = 0;
		if (seekBar == seekBarRed) {
			skBaarRed = progress;
		} else if (seekBar == seekBarGreen) {
			skBaarGreen = progress;
		} else if (seekBar == seekBarBlue) {
			skBaarBlue = progress;
		} else {
			skbaarlevel = progress;
			tvSeekBarValue.setText("" + progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		new AddTask().execute();
	}
}