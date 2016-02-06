package com.example.graham.crunchtime;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] exercises = new String[]{
                "Calories Burned",
                "Pushups",
                "Situps",
                "Squats",
                "Minutes Doing Leg-lifts",
                "Minutes Planking",
                "Minutes Doing Jumping Jacks",
                "Pullups",
                "Minutes Cycling",
                "Minutes Walking",
                "Minutes Jogging",
                "Minutes Swimming",
                "Minutes Climbing Stairs"
        };

        String[] exercises_singular = new String[]{
                "Calorie Burned",
                "Pushup",
                "Situp",
                "Squat",
                "Minute Doing Leg-lifts",
                "Minute Planking",
                "Minute Doing Jumping Jacks",
                "Pullup",
                "Minute Cycling",
                "Minute Walking",
                "Minute Jogging",
                "Minute Swimming",
                "Minute Climbing Stairs"
        };

        Spinner topSpinner = (Spinner) findViewById(R.id.selectionSpinner);
        TextView rightText = (TextView) findViewById(R.id.rightText);
        ListView valuesList = (ListView) findViewById(R.id.values);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, exercises);
        valuesList.setAdapter(listAdapter);

        final EditText topAmount = (EditText) findViewById(R.id.firstAmount);
        topAmount.setOnFocusChangeListener(new OnFocusChangeListener(topAmount));
        topAmount.addTextChangedListener(new InputTextWatcher(topSpinner, valuesList, exercises,
                exercises_singular, rightText));

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.exercises, R.layout.multiline_spinner_dropdown_item);
        topSpinner.setAdapter(spinnerAdapter);

        SpinnerItemSelectedListener topSpinnerCallback = new SpinnerItemSelectedListener(topSpinner, topAmount);
        topSpinner.setOnItemSelectedListener(topSpinnerCallback);
    }
}

class OnFocusChangeListener implements View.OnFocusChangeListener {
    private EditText self;

    public OnFocusChangeListener (EditText thisField) {
        this.self = thisField;
    }

    // https://xjaphx.wordpress.com/2011/11/23/clear-edittext-content-on-focus/
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            self.setText("", TextView.BufferType.EDITABLE);
        }
    }
}

class InputTextWatcher implements TextWatcher {

    public float[] exercisesToCalories = new float[] {
            1f,
            (100/350f),
            (100/200f),
            (100/225f),
            4f,
            4f,
            10f,
            1f,
            (100/12f),
            5f,
            (100/12f),
            (100/13f),
            (100/15f)
    };
    private String[] exercises;
    private String[] exercises_singular;
    private Spinner spinner;
    private TextView rightText;
    private ListView outputListView;

    public InputTextWatcher(Spinner thisSpinner, ListView outputList, String[] exercises,
                            String[] exercises_singular, TextView rightText) {
        this.exercises = exercises;
        this.exercises_singular = exercises_singular;
        this.spinner = thisSpinner;
        this.outputListView = outputList;
        this.rightText = rightText;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    public void afterTextChanged(Editable s) {
        Log.i("Firing", "Callback");
        this.outputListView.smoothScrollToPosition(0);
        float amountInput;
        try {
            amountInput = Float.parseFloat(s.toString());
        } catch (Exception e) {
            amountInput = 0f;
        }

        if (amountInput == 1f) {
            this.rightText.setText("is...");
        } else {
            this.rightText.setText("are...");
        }

        int pos = spinner.getSelectedItemPosition();
        long id = spinner.getSelectedItemId();

        List<String> values = new ArrayList<String>();
        float ratio;
        int converted;
        for (int i = 0; i < 12; ++i) {
            ratio = this.getRatio(pos, i);
            converted = Math.round(amountInput * ratio);
            if (converted == 1) {
                values.add(Integer.toString(converted) + " " + this.exercises_singular[i]);
            } else {
                values.add(Integer.toString(converted)+ " " + this.exercises[i]);
            }
        }

        String[] valuesArray = values.toArray(new String[values.size()]);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this.outputListView.getContext(),
                android.R.layout.simple_list_item_1, valuesArray);
        this.outputListView.setAdapter(listAdapter);
    }


    // Return ratio of reps/minutes performed to calories or some other exercise
    // based of off currently selected spinner positions
    public float getRatio(int thisPos, int otherPos) {
        assert thisPos >= 1 : thisPos <= 12;
        assert otherPos >= 1: otherPos <= 12;
        return this.exercisesToCalories[thisPos] / this.exercisesToCalories[otherPos];
    }
}

class SpinnerItemSelectedListener extends Activity implements AdapterView.OnItemSelectedListener {

    private EditText amount;

    public SpinnerItemSelectedListener (Spinner self, EditText amount) {
        this.amount = amount;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // Force input field's callback to trigger by setting text to same value that
        // was there previously
        amount.setText(amount.getText().toString());
    }

    public void onNothingSelected(AdapterView<?> parent) {}
}