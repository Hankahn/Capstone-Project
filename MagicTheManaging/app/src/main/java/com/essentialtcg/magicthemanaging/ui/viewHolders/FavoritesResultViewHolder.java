package com.essentialtcg.magicthemanaging.ui.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.essentialtcg.magicthemanaging.R;

/**
 * Created by Shawn on 5/3/2016.
 */
public class FavoritesResultViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

    public ImageView croppedImageView;
    public TextView nameTextView;
    public TextView setRarityTextView;
    public TextView typeTextView;
    public LinearLayout rightContainer;
    public TextView manaCostTextView;
    public TextView featuredStatTextView;

    public FavoritesResultViewHolder(View itemView) {
        super(itemView);

        croppedImageView = (ImageView) itemView.findViewById(R.id.cropped_image_view);
        nameTextView = (TextView) itemView.findViewById(R.id.card_name_text_view);
        setRarityTextView = (TextView) itemView.findViewById(R.id.set_rarity_text_view);
        typeTextView = (TextView) itemView.findViewById(R.id.type_text_view);
        rightContainer = (LinearLayout) itemView.findViewById(R.id.right_container);
        manaCostTextView = (TextView) itemView.findViewById(R.id.mana_cost_text_view);
        featuredStatTextView = (TextView) itemView.findViewById(R.id.featured_stat_text_view);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        /*Context context = view.getContext();

        int position = this.getLayoutPosition();

        Intent viewCardIntent = new Intent(context, CardViewActivity.class);

        View croppedImageView = view.findViewById(R.id.cropped_image_view);

        Bundle bundle = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(new Fade());

            String transitionName = croppedImageView.getTransitionName();
            bundle = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            context,
                            croppedImageView,
                            croppedImageView.getTransitionName())
                    .toBundle();
        }

        mCursor.moveToPosition(position);

        viewCardIntent.putExtra(CardViewActivity.INITIAL_CARD_POSITION, position);
        viewCardIntent.putExtra(CardViewActivity.SELECTED_ITEM_ID,
                mCursor.getLong(CardLoader.Query._ID));
        viewCardIntent.putExtra(CardViewActivity.SEARCH_PARAMETERS, mSearchParameters);

        startActivity(viewCardIntent, bundle);

        Log.d("MtMT", croppedImageView.getTransitionName() + " -> " +
                croppedImageView.getTransitionName());*/

                /*CardViewFragment cardViewFragment = CardViewFragment.newInstance(
                position, mSearchParameters);*/

                /*cardViewFragment.setSharedElementEnterTransition(new DetailTransition());
                setSharedElementEnterTransition(new DetailTransition());
                cardViewFragment.setEnterTransition(new Fade());
                setExitTransition(new Fade());
                setSharedElementReturnTransition(new DetailTransition());*/

                /*FragmentTransitionUtil.getInstance(getFragmentManager())
                        .transition(R.id.fragment_container, this, cardViewFragment, croppedImageView,
                                croppedImageView.getTransitionName());*/

                /*getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        //.addSharedElement(croppedImageView, croppedImageView.getTransitionName())// destinationTransitionName)
                        //.add(R.id.fragment_container, cardViewFragment)
                        .replace(R.id.fragment_container, cardViewFragment)
                        .addToBackStack(null)
                        .commit();*/
    }
}
