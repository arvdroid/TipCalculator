package com.apps.calculator;

import java.text.NumberFormat;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class TipCalculatorActivity extends ActionBarActivity {
	
	SeekBar mSeekBar;
    TextView mProgressText;
    TextView mBillAmount;
    TextView mTipAmount;
    TextView mSplitAmount;
    TextView mSplitTip;
    EditText mBillEdit;
    Spinner mSplitSpinner;
    Button mButtonPlus;
    Button mButtonMinus;
    
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip_calculator);
		
		mBillEdit = (EditText)findViewById(R.id.billEditText);
		mBillEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {				
				setBillAmount();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable edittext) {
				String str= edittext.toString();
		        int posDot = str.indexOf(".");
		        if (posDot <= 0) return;
		        if (str.length() - posDot - 1 > 2)
		        {
		        	edittext.delete(posDot + 3, posDot + 4);
		        }
			}
		});
		
		mSeekBar = (SeekBar)findViewById(R.id.tipSeekBar);
		
		SeekBarListener sbl = new SeekBarListener();
        mSeekBar.setOnSeekBarChangeListener(sbl);
        mProgressText = (TextView)findViewById(R.id.tipProgress);// what is secondary progress
        
        String[] splitP = new String[]{"0", "2", "3", "4"};
        
        mSplitSpinner = (Spinner)findViewById(R.id.splitSpinner);
        mSplitSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long arg3) {
				
				String value = parent.getItemAtPosition(pos).toString();				
				Log.d("split", value);
				setBillAmountPerPerson(Integer.valueOf(value));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
        	
        });
        ArrayAdapter<String> adapters = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splitP);
        mSplitSpinner.setAdapter(adapters);
        
        mBillAmount = (TextView)findViewById(R.id.billAmount);
        mTipAmount = (TextView)findViewById(R.id.totalTipAmount);
        mSplitAmount = (TextView)findViewById(R.id.splitTotal);
        mSplitTip = (TextView)findViewById(R.id.splitTip);
        
        mButtonPlus = (Button)findViewById(R.id.buttonPlus);
        mButtonMinus = (Button)findViewById(R.id.buttonMinus);
        
        mButtonMinus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String value = mProgressText.getText().toString();
				int tip = Integer.parseInt(value);
				if(tip>0){
					mProgressText.setText(String.valueOf(tip-1));
					setBillAmount();
				}
			}
		});
        
        mButtonPlus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String value = mProgressText.getText().toString();
				int tip = Integer.parseInt(value);
				mProgressText.setText(String.valueOf(tip+1));
				setBillAmount();
			}
		});
	}
	
	public double getBillAmount(){
		String bill = mBillEdit.getText().toString();
		double bA = bill.isEmpty()?0.00:Double.parseDouble(bill);
		double total = bA + getTipAmount();
		return total;
	}
	
	public double getTipAmount(){
		String bill = mBillEdit.getText().toString();
		double bA = bill.isEmpty()?0.00:Double.parseDouble(bill);
		//int tip = mSeekBar.getProgress();
		//String tipV = mProgressText.getText().toString();
		int tip = Integer.parseInt(mProgressText.getText().toString());
		double totalTipA = bA!=0?(bA * tip)/100 :0.00;//tip*5
		return totalTipA;
	}
	
	public void setBillAmount(){
		double total = getBillAmount();
		double totalTip = getTipAmount();
		
		mBillAmount.setText(currencyFormatter.format((total)));
		mTipAmount.setText(currencyFormatter.format((totalTip)));

		String value = mSplitSpinner.getSelectedItem().toString();		
		setBillAmountPerPerson(Integer.valueOf(value));
	}
	
	public void setBillAmountPerPerson(int split){
		double billA = getBillAmount();
		double sA = split!=0?billA/split:billA;
		
		double billTA = getTipAmount();
		double sT = split!=0?billTA/split:billTA;
		
		mSplitAmount.setText(currencyFormatter.format(sA));
		mSplitTip.setText(currencyFormatter.format(sT));
		
	}
	
	private class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			mProgressText.setText(String.valueOf(progress*5));
			setBillAmount();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tip_calculator, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
