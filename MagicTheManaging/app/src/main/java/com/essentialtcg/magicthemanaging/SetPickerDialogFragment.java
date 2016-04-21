package com.essentialtcg.magicthemanaging;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.data.SetItem;
import com.essentialtcg.magicthemanaging.data.SetLoader;
import com.essentialtcg.magicthemanaging.data.SetTransform;
import com.essentialtcg.magicthemanaging.utils.SetArrayList;

import java.util.ArrayList;

public class SetPickerDialogFragment extends DialogFragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private final String TAG = "SetPickerDialogFragment";

    private CardSearchParameters mSearchParameters;

    private static final String SET_PICKER_POSITION_TAG = "SET_PICKER_POSITION";
    private static final String SEARCH_PARAMETERS_TAG = "SEARCH_PARAMETERS";
    private RecyclerView mRecyclerView;

    private int mPosition = 0;

    private SetArrayList mSelectedSets = new SetArrayList();

    public SetPickerDialogFragment() {
    }

    public static SetPickerDialogFragment newInstance(CardSearchParameters searchParameters) {
        SetPickerDialogFragment fragment = new SetPickerDialogFragment();
        Bundle arguments = new Bundle();

        arguments.putParcelable(SEARCH_PARAMETERS_TAG, searchParameters);

        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(SET_PICKER_POSITION_TAG)) {
            mPosition = savedInstanceState.getInt(SET_PICKER_POSITION_TAG);
        }

        if (getArguments().containsKey(SEARCH_PARAMETERS_TAG)) {
            mSearchParameters = getArguments().getParcelable(SEARCH_PARAMETERS_TAG);
            mSelectedSets = mSearchParameters.getSetFilter() != null ?
                    mSearchParameters.getSetFilter() : new SetArrayList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_set_picker, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /*if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(SET_PICKER_POSITION_TAG);

            if (savedInstanceState.containsKey(SEARCH_PARAMETERS_TAG)) {
                mSearchParameters = savedInstanceState.getParcelable(SEARCH_PARAMETERS_TAG);
            }
        } else {
            mPosition = 0;
            mSearchParameters = new CardSearchParameters();
        }*/

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.set_picker_recycler_view);

        getDialog().setTitle(R.string.SET_PICKER_DIALOG_TITLE);

        LoadData();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int position = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();

        outState.putInt(SET_PICKER_POSITION_TAG, position);
        outState.putParcelable(SEARCH_PARAMETERS_TAG, mSearchParameters);
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

            ViewHolder viewHolder;

            if (getItemViewType(position) == GROUP_HOLDER) {
                GroupViewHolder gvh = (GroupViewHolder) holder;

                if (setItem.getSetType().equals("expansion")) {
                    if (setItem.getBlock().length() > 0) {
                        gvh.groupTextView.setText(setItem.getBlock());
                    } else {
                        gvh.groupTextView.setText(R.string.SET_GROUP_EXPANSION_DEFAULT);
                    }
                } else {
                    gvh.groupTextView.setText(setItem.getSetType());
                }

                viewHolder = gvh;
            } else {
                viewHolder = (ViewHolder) holder;
            }

            if (mSearchParameters.getSetFilter().containsSetItem(setItem)) {
                viewHolder.selectCheckBox.setChecked(true);
            } else {
                viewHolder.selectCheckBox.setChecked(false);
            }

            try {
                int resourceId = CardUtil.parseSetRarity(setItem.getCode(), "Common");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.setIconImageView.setImageDrawable(
                            getResources().getDrawable(resourceId, null));
                } else {
                    viewHolder.setIconImageView.setContentDescription(setItem.getCode());
                    viewHolder.setIconImageView.setImageDrawable(
                            getResources().getDrawable(resourceId));
                }

                viewHolder.setIconImageView.setVisibility(View.VISIBLE);
            } catch (Exception ex) {
                viewHolder.setIconImageView.setVisibility(View.INVISIBLE);
                Log.d(TAG, String.format("onBindViewHolder: Unable to find icon: %s",
                        setItem.getCode()));
            }

            viewHolder.nameTextView.setText(setItem.getName());

            viewHolder.selectCheckBox.setTag(mSetList.get(position));
            viewHolder.itemView.setOnClickListener(onViewSelect);
            viewHolder.selectCheckBox.setOnClickListener(onCheckBoxSelect);
        }

        @Override
        public int getItemCount() {
            return mSetList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public CheckBox selectCheckBox;
        public ImageView setIconImageView;

        public ViewHolder(View view) {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.set_name_text_view);
            selectCheckBox = (CheckBox) view.findViewById(R.id.set_picker_check_box);
            setIconImageView = (ImageView) view.findViewById(R.id.set_picker_set_image_view);
        }
    }

    public static class GroupViewHolder extends ViewHolder {

        public TextView groupTextView;

        public GroupViewHolder(View view) {
            super(view);

            groupTextView = (TextView) view.findViewById(R.id.set_group_text_view);
            nameTextView = (TextView) view.findViewById(R.id.set_name_text_view);
            selectCheckBox = (CheckBox) view.findViewById(R.id.set_picker_check_box);
            setIconImageView = (ImageView) view.findViewById(R.id.set_picker_set_image_view);
        }
    }

    View.OnClickListener onViewSelect = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.set_picker_check_box);
            //CheckBox checkBox = (CheckBox) view;
            SetItem set = (SetItem) checkBox.getTag();

            checkBox.setChecked(!checkBox.isChecked());

            if (checkBox.isChecked()) {
                mSelectedSets.add(set);
            } else {
                mSelectedSets.removeSetItem(set);
            }

            mSearchParameters.setSetFilter(mSelectedSets);
        }
    };

    View.OnClickListener onCheckBoxSelect = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CheckBox checkBox = (CheckBox) view;
            SetItem set = (SetItem) checkBox.getTag();

            if (checkBox.isChecked()) {
                mSelectedSets.add(set);
            } else {
                mSelectedSets.removeSetItem(set);
            }

            mSearchParameters.setSetFilter(mSelectedSets);
        }
    };

    public interface SetPickerDialogFragmentListener {
        void onReturnValue(SetArrayList selectedSets);
    }

}
