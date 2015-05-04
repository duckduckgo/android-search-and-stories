package com.duckduckgo.mobile.android.views.autocomplete;

import android.content.Context;
import android.support.v7.view.ActionMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public class DDGAutoCompleteTextView extends AutoCompleteTextView implements View.OnLongClickListener {

    private android.view.ActionMode actionMode;
    private android.view.ActionMode.Callback actionModeCallback;

	public DDGAutoCompleteTextView(Context context) {
		super(context);
        //this.setOnLongClickListener(this);
	}

	public DDGAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
        //this.setOnLongClickListener(this);
	}

	public DDGAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        //this.setOnLongClickListener(this);
	}

	private BackButtonPressedEventListener backButtonPressedEventListener;

	public void setOnBackButtonPressedEventListener(BackButtonPressedEventListener eventListener) {
		backButtonPressedEventListener = eventListener;
	}
	
	public String getTrimmedText(){
		return getText().toString().trim();
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
			backButtonPressedEventListener.onBackButtonPressed();
			return false;
		}
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
            super.onEditorAction(EditorInfo.IME_ACTION_SEARCH);
            return true;
        }
		return super.dispatchKeyEvent(event);
	}

	public void addBang() {
		if(isCursorAtEnd() && !lastCharIsSpaceOrNull()){
            Log.e("aaa", "add bang 1");
            getText().insert(getSelectionStart(), " !");
		}else{
            Log.e("aaa", "add bang 2");
			getText().replace(getSelectionStart(), getSelectionEnd(), "!");
			//setSelection(getSelectionStart());//fix 1
            setSelection(getSelectionEnd());
		}
	}

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

	private boolean lastCharIsSpaceOrNull(){
		return !hasText() || getText().charAt(getText().length() - 1) == ' ';
	}

	private boolean hasText() {
		return getText().length() > 0;
	}

	private boolean isCursorAtEnd() {
		return getSelectionStart() == getText().length();
	}

	public void addTextWithTrailingSpace(String phrase) {
		setText(phrase.trim() + " ");
        setCursorAtEnd();
    }

    private void setCursorAtEnd() {
        setSelection(getText().length());
    }

    public void pasteQuery(String suggestion) {
        releaseFocus();
        setText(suggestion);
        append(" ");
        obtainFocus();
        setCursorAtEnd();
    }

    private void releaseFocus() {
        setFocusable(false);
        setFocusableInTouchMode(false);
    }

    private void obtainFocus() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.requestFocus();
    }

}
