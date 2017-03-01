package com.pbasolutions.android.control;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.pbasolutions.android.R;
import com.pbasolutions.android.model.MProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchableSpinnerForRequisition extends Spinner implements View.OnTouchListener,
        SearchableListDialogForRequisition.SearchableItem {

    public static final int NO_ITEM_SELECTED = -1;
    private Context _context;
    private List _items;
    private SearchableListDialogForRequisition _searchableListDialog;

    private boolean _isDirty;
    private SimpleAdapter _arrayAdapter;
    private String _strHintText;
    private boolean _isFromInit;
    private String _dlgtitle;

    public SearchableSpinnerForRequisition(Context context) {
        super(context);
        this._context = context;
        init();
    }

    public SearchableSpinnerForRequisition(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchableSpinner);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SearchableSpinner_hintText) {
                _strHintText = a.getString(attr);
            } else if (attr == R.styleable.SearchableSpinner_dlgTitle) {
                _dlgtitle = a.getString(attr);
            }
        }

        a.recycle();
        init();
    }

    public SearchableSpinnerForRequisition(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._context = context;
        init();
    }

    private void init() {
        _items = new ArrayList();
        _searchableListDialog = SearchableListDialogForRequisition.newInstance
                (_items);
        _searchableListDialog.setOnSearchableItemClickListener(this);
        setOnTouchListener(this);
        _searchableListDialog.setTitle(_dlgtitle);

        _arrayAdapter = (SimpleAdapter) getAdapter();
        if (!TextUtils.isEmpty(_strHintText)) {
            List<Map<String, String>> prodList = new ArrayList<>();
            HashMap temp = new HashMap<>(2);
            temp.put(MProduct.NAME_COL, _strHintText);
            prodList.add(temp);
            SimpleAdapter arrayAdapter = new SimpleAdapter(_context, prodList, android.R.layout
                    .simple_list_item_2, new String[] {MProduct.NAME_COL, MProduct.VALUE_COL}, new int[] {android.R.id.text1, android.R.id.text2});
            _isFromInit = true;
            setAdapter(arrayAdapter);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (null != _arrayAdapter) {

                // Refresh content #6
                // Change Start
                // Description: The items were only set initially, not reloading the data in the
                // spinner every time it is loaded with items in the adapter.
                _items.clear();
                for (int i = 0; i < _arrayAdapter.getCount(); i++) {
                    _items.add(_arrayAdapter.getItem(i));
                }
                // Change end.

                _searchableListDialog.show(scanForActivity(_context).getFragmentManager(), "TAG");
            }
        }
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {

        if (!_isFromInit) {
            _arrayAdapter = (SimpleAdapter) adapter;
            if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
                List<Map<String, String>> prodList = new ArrayList<>();
                HashMap temp = new HashMap<>(2);
                temp.put(MProduct.NAME_COL, _strHintText);
                prodList.add(temp);
                SimpleAdapter arrayAdapter = new SimpleAdapter(_context, prodList, android.R.layout
                        .simple_list_item_2, new String[] {MProduct.NAME_COL, MProduct.VALUE_COL}, new int[] {android.R.id.text1, android.R.id.text2});
                super.setAdapter(arrayAdapter);
            } else {
                super.setAdapter(adapter);
            }

        } else {
            _isFromInit = false;
            super.setAdapter(adapter);
        }
    }

    @Override
    public void onSearchableItemClicked(Object item, int position) {
        setSelection(_items.indexOf(item));

        if (!_isDirty) {
            _isDirty = true;
            setAdapter(_arrayAdapter);
            setSelection(_items.indexOf(item));
        }
    }

    public void setTitle(String strTitle) {
        _searchableListDialog.setTitle(strTitle);
    }

    public void setPositiveButton(String strPositiveButtonText) {
        _searchableListDialog.setPositiveButton(strPositiveButtonText);
    }

    public void setPositiveButton(String strPositiveButtonText, DialogInterface.OnClickListener onClickListener) {
        _searchableListDialog.setPositiveButton(strPositiveButtonText, onClickListener);
    }

    public void setOnSearchTextChangedListener(SearchableListDialogForRequisition.OnSearchTextChanged onSearchTextChanged) {
        _searchableListDialog.setOnSearchTextChangedListener(onSearchTextChanged);
    }

    private Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

    @Override
    public int getSelectedItemPosition() {
        if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
            return NO_ITEM_SELECTED;
        } else {
            return super.getSelectedItemPosition();
        }
    }

    @Override
    public Object getSelectedItem() {
        if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
            return null;
        } else {
            return super.getSelectedItem();
        }
    }
}