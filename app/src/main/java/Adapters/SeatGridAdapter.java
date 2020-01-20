package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.makarand.atmas.R;


import java.util.ArrayList;

import Model.Seat;


public class SeatGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Seat> seats;
    private LinearLayout seatCard;
    private TextView roll_number;

    public SeatGridAdapter(Context mContext, ArrayList<Seat> seats) {
        this.mContext = mContext;
        this.seats = seats;
    }

    @Override
    public int getCount() {
        return seats.size();
    }

    @Override
    public Object getItem(int position) {
        return seats.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view;


        if(convertView == null){
            view = inflater.inflate(R.layout.seats_grid_layout, null);
        } else {
            view = convertView;
        }


        seatCard = view.findViewById(R.id.seat_card);
        roll_number =  view.findViewById(R.id.roll_placeholder);

        final Seat seat = seats.get(position);
        roll_number.setText(String.valueOf(seat.getRoll() + 1));

        if(seat.isPresent()){
            seatCard.setBackgroundResource(R.drawable.card_edge_selected);
            roll_number.setTextColor(Color.parseColor("#ffffff"));
        } else {
            seatCard.setBackgroundResource(R.drawable.card_edge);
            roll_number.setTextColor(Color.parseColor("#505050"));

        }

        seatCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*To create a toggle Switch*/
                if(seat.isPresent()){
                    /* seat is present make it absent, make changes to the array.*/
                    seat.setPresent(false);
                    notifyDataSetChanged();
                } else {
                    seat.setPresent(true);
                    notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    public void setPresent(int position){
        Seat seat = (Seat) getItem(position);
        seat.setPresent(true);
        notifyDataSetChanged();
    }

    public void setAbsent(int position){
        Seat seat = (Seat) getItem(position);
        seat.setPresent(false);
        notifyDataSetChanged();
    }
}
