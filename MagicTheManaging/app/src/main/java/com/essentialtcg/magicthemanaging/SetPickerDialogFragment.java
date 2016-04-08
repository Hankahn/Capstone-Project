package com.essentialtcg.magicthemanaging;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.essentialtcg.magicthemanaging.data.SetItem;
import com.essentialtcg.magicthemanaging.data.SetLoader;
import com.essentialtcg.magicthemanaging.data.SetTransform;

import java.util.ArrayList;

public class SetPickerDialogFragment extends DialogFragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String SET_PICKER_POSITION_TAG = "SET_PICKER_POSITION";
    private static final int SET_PICKER_RESULTS = 0;
    private RecyclerView mRecyclerView;

    private int mPosition = 0;

    private ArrayList<SetItem> mSelectedSets = new ArrayList<>();

    public SetPickerDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_set_picker, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(SET_PICKER_POSITION_TAG);
        } else {
            mPosition = 0;
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.set_picker_recycler_view);

        getDialog().setTitle("Sets");

        LoadData();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int position = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();

        outState.putInt(SET_PICKER_POSITION_TAG, position);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        SetPickerDialogFragmentListener targetFragment =
                (SetPickerDialogFragmentListener) getTargetFragment();
        targetFragment.onReturnValue(mSelectedSets);

        super.onDismiss(dialog);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return SetLoader.newAllSetsInstance(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<SetItem> setItems = SetTransform.transform(cursor);

        RecyclerAdapter adapter = new RecyclerAdapter(setItems);

        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(mPosition);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private void LoadData() {
        getLoaderManager().restartLoader(0, null, this);
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        final int GROUP_HOLDER = 0;
        final int ITEM_HOLDER = 1;

        private ArrayList<SetItem> mSetList;

        public RecyclerAdapter(ArrayList<SetItem> setList) {
            mSetList = setList;
        }

        @Override
        public long getItemId(int position) {
            return mSetList.get(position).getId();
        }

        @Override
        public int getItemViewType(int position) {
            SetItem item = mSetList.get(position);

            if (item.isFirstSet()) {
                return GROUP_HOLDER;
            } else {
                return ITEM_HOLDER;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view;

            RecyclerView.ViewHolder vh;

            if (viewType == GROUP_HOLDER) {
                view = getActivity().getLayoutInflater().inflate(
                        R.layout.list_group_item_set, parent, false);

                vh = new GroupViewHolder(view);
            } else {
                view = getActivity().getLayoutInflater().inflate(
                        R.layout.list_item_set, parent, false);

                vh = new ViewHolder(view);
            }

            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SetItem setItem = mSetList.get(position);

            if (getItemViewType(position) == GROUP_HOLDER) {
                GroupViewHolder viewHolder = (GroupViewHolder) holder;

                if (setItem.getSetType().equals("expansion")) {
                    if (setItem.getBlock().length() > 0) {
                        viewHolder.groupTextView.setText(setItem.getBlock());
                    } else {
                        viewHolder.groupTextView.setText("Pre-Block");
                    }
                } else {
                    viewHolder.groupTextView.setText(setItem.getSetType());
                }

                viewHolder.nameTextView.setText(setItem.getName());

                viewHolder.selectCheckBox.setTag(mSetList.get(position));
                viewHolder.selectCheckBox.setOnClickListener(onSelectSet);
            } else {
                ViewHolder viewHolder = (ViewHolder) holder;

                viewHolder.nameTextView.setText(setItem.getName());

                viewHolder.selectCheckBox.setTag(mSetList.get(position));
                viewHolder.selectCheckBox.setOnClickListener(onSelectSet);
            }
        }

        @Override
        public int getItemCount() {
            return mSetList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public CheckBox selectCheckBox;

        public ViewHolder(View view) {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.set_name_text_view);
            selectCheckBox = (CheckBox) view.findViewById(R.id.set_picker_check_box);
        }
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        public TextView groupTextView;
        public TextView nameTextView;
        public CheckBox selectCheckBox;

        public GroupViewHolder(View view) {
            super(view);

            groupTextView = (TextView) view.findViewById(R.id.set_group_text_view);
            nameTextView = (TextView) view.findViewById(R.id.set_name_text_view);
            selectCheckBox = (CheckBox) view.findViewById(R.id.set_picker_check_box);
        }
    }

    View.OnClickListener onSelectSet = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CheckBox checkBox = (CheckBox) view;
            SetItem set = (SetItem) checkBox.getTag();

            if (checkBox.isChecked()) {
                mSelectedSets.add(set);
            } else {
                mSelectedSets.remove(set);
            }
        }
    };

    public interface SetPickerDialogFragmentListener {
        public void onReturnValue(ArrayList<SetItem> selectedSets);
    }

}
