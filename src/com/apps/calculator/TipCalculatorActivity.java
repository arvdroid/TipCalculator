package com.apps.calculator;

import java.text.NumberFormat;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
	
	private SeekBar mSeekBar;
	private TextView mTipText;
	private TextView mBillAmount;
	private TextView mTipAmount;
	private TextView mSplitAmount;
	private TextView mSplitTip;
	private EditText mBillEdit;
	private Spinner mSplitSpinner;
	private Button mButtonPlus;
	private Button mButtonMinus;
    
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip_calculator);
		
		mBillEdit = (EditText)findViewById(R.id.billEditText);
		mBillEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {				
				double bA = getBillAmount();
				if(bA>1000)
					mTipText.setText(String.valueOf("30"));
				else if (bA > 500)
					mTipText.setText(String.valueOf("20"));
				else if (bA > 100)
					mTipText.setText(String.valueOf("15"));
				else
					mTipText.setText(String.valueOf("10"));
				updateSeekBarPosition();
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
        mTipText = (TextView)findViewById(R.id.tipProgress);
        mTipText.setText(String.valueOf(mSeekBar.getProgress()*5));
        
        String[] splitP = new String[]{"0", "2", "3", "4", "5", "6", "7"};
        
        mSplitSpinner = (Spinner)findViewById(R.id.splitSpinner);
        mSplitSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long arg3) {
				
				String value = parent.getItemAtPosition(pos).toString();
				setBillAmountPerPerson(Integer.valueOf(value));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
        	
        });
        ArrayAdapter<String> adapters = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splitP);
        mSplitSpinner.setAdapter(adapters);
        
        mBillAmount = (TextView)findViewById(R.id.billAmount);
        mBillAmount.setText(currencyFormatter.format(0));
        
        mTipAmount = (TextView)findViewById(R.id.totalTipAmount);
        mTipAmount.setText(currencyFormatter.format(0));
        
        mSplitAmount = (TextView)findViewById(R.id.splitTotal);
        mSplitAmount.setText(currencyFormatter.format(0));
        
        mSplitTip = (TextView)findViewById(R.id.splitTip);
        mSplitTip.setText(currencyFormatter.format(0));
        
        mButtonPlus = (Button)findViewById(R.id.buttonPlus);
        mButtonMinus = (Button)findViewById(R.id.buttonMinus);
        
        mButtonMinus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String value = mTipText.getText().toString();
				int tip = Integer.parseInt(value);
				if(tip>0){
					mTipText.setText(String.valueOf(tip-1));
					setBillAmount();
					updateSeekBarPosition();
				}
			}
		});
        
        mButtonPlus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String value = mTipText.getText().toString();
				int tip = Integer.parseInt(value);
				mTipText.setText(String.valueOf(tip+1));
				setBillAmount();
				updateSeekBarPosition();
			}
		});
	}
	
	public double getBillAmount(){
		String bill = mBillEdit.getText().toString();
		double bA = bill.isEmpty()?0.00:Double.parseDouble(bill);
		return bA;
	}
	
	public double getTotalBillAmount(){
		double total = getBillAmount() + getTipAmount();
		return total;
	}
	
	public double getTipAmount(){
		String bill = mBillEdit.getText().toString();
		double bA = bill.isEmpty()?0.00:Double.parseDouble(bill);
		int tip = Integer.parseInt(mTipText.getText().toString());
		double totalTipA = bA!=0?(bA * tip)/100 :0.00;
		return totalTipA;
	}
	
	public void setBillAmount(){
		double total = getTotalBillAmount();
		double totalTip = getTipAmount();
		
		mBillAmount.setText(currencyFormatter.format((total)));
		mTipAmount.setText(currencyFormatter.format((totalTip)));

		String value = mSplitSpinner.getSelectedItem().toString();		
		setBillAmountPerPerson(Integer.valueOf(value));
	}
	
	public void setBillAmountPerPerson(int split){
		double billA = getTotalBillAmount();
		double sA = split!=0?billA/split:billA;
		
		double billTA = getTipAmount();
		double sT = split!=0?billTA/split:billTA;
		
		mSplitAmount.setText(currencyFormatter.format(sA));
		mSplitTip.setText(currencyFormatter.format(sT));
		
	}
	
	private void updateSeekBarPosition(){
		int tip = Integer.parseInt(mTipText.getText().toString());
		int progress = tip/5;
		boolean update = tip%5==0;
		if(update){
			if(progress<=10){
				mSeekBar.setProgress(progress);
			}else
				mSeekBar.setProgress(10);
		}
	}
	
	private class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			mTipText.setText(String.valueOf(progress*5));
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
