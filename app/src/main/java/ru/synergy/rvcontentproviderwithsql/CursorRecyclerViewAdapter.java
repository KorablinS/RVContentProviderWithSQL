package ru.synergy.rvcontentproviderwithsql;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context mContext;

    private Cursor mCursor;

    private boolean mDataValid;

    private int mRowIdColumn;

    private final DataSetObserver mDataSetObserver;

    public CursorRecyclerViewAdapter(Context context, Cursor cursor){

        mContext = context;
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id"): -1;
        mDataSetObserver = new NotyfyingDataSetObserver(this);



    }

    public Cursor getCursor(){
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if(mDataValid && mCursor != null){
            return mCursor.getCount();
        }
        return 0;
    }
    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public static final String TAG = CursorRecyclerViewAdapter.class.getSimpleName();

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(viewHolder, mCursor);
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    public void setDataValid(boolean mDataValid) {
        this.mDataValid = mDataValid;
    }



}


class NotifyingDataSetObserver extends DataSetObserver{
    private RecyclerView.Adapter adapter;

    public NotifyingDataSetObserver(RecyclerView.Adapter adapter){
        this.adapter = adapter;
    }

}
        class NotyfyingDataSetObserver extends DataSetObserver{
    private RecyclerView.Adapter adapter;

    public NotyfyingDataSetObserver(RecyclerView.Adapter adapter){
        this.adapter = adapter;
    }

            @Override
            public void onChanged() {
                super.onChanged();
                ((CursorRecyclerViewAdapter) adapter).setDataValid(true);
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onInvalidated() {
                super.onInvalidated();
                ((CursorRecyclerViewAdapter) adapter).setDataValid(false);
            }
        }