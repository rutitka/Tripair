package com.example.recycleViewPack;

public interface OnRecyclerTripClickListener {

    /**
     * Called when any item with in recyclerview or any item with in item
     * clicked
     *
     * @param position
     *            The position of the item
     * @param id
     *            The id of the view which is clicked with in the item or
     *            -1 if the item itself clicked
     */
    public void onRecyclerViewItemClicked(int position, int id);

    public void onRecycleViewItemDeleteClicked(int position,int id);
}
