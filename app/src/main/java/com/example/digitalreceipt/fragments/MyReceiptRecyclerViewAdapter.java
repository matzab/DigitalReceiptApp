package com.example.digitalreceipt.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.digitalreceipt.R;
import com.example.digitalreceipt.fragments.ReceiptFragment.OnListFragmentInteractionListener;
import com.example.digitalreceipt.model.ReceiptPDF;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyReceiptRecyclerViewAdapter extends RecyclerView.Adapter<MyReceiptRecyclerViewAdapter.ViewHolder> {

    private final List<ReceiptPDF> myReceipts;
    private final OnListFragmentInteractionListener mListener;

    public MyReceiptRecyclerViewAdapter(List<ReceiptPDF> myReceipts, OnListFragmentInteractionListener listener) {
        this.myReceipts = myReceipts;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_receipt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = myReceipts.get(position);
        holder.mIdView.setText(myReceipts.get(position).getTitle());
        holder.mContentView.setText("Received on: " + myReceipts.get(position).getTitle().substring(0,10));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myReceipts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public ReceiptPDF mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
