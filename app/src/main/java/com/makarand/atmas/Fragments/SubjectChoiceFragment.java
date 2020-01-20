package com.makarand.atmas.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.reflect.TypeToken;
import com.makarand.atmas.Events.OnSubjectListViewReady;
import com.makarand.atmas.R;

import java.util.ArrayList;
import java.util.List;

import Constants.Constants;
import Helper.LocalStorage;
import Model.Subject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubjectChoiceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubjectChoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubjectChoiceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ListView subjectListView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context ActivityContext;
    private OnFragmentInteractionListener mListener;
    private OnSubjectListViewReady onSubjectSelectedListener;
    private OnSubjectListViewReady onSubjectListViewReady;
    public SubjectChoiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubjectChoiceFragment.
     */
    // TODO: Rename and change types and number of parameters


    public static SubjectChoiceFragment newInstance(String param1, String param2) {
        SubjectChoiceFragment fragment = new SubjectChoiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subject_choice, container, false);
        subjectListView = view.findViewById(R.id.subject_list_view);

        prepareSubjectList();

        return view;
    }

    private void prepareSubjectList(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user/"+auth.getCurrentUser().getUid()+"/subjectList");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Subject> subjectArrayList = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    subjectArrayList.add(ds.getValue(Subject.class));
                }
                prepareListView(subjectArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void prepareListView(final ArrayList<Subject> subjectArrayList) {
        List<String> subjectNameList= new ArrayList<>();
        for(Subject subject : subjectArrayList){
            subjectNameList.add(subject.getSubjectName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                subjectNameList
        );

        subjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSubjectSelectedListener.onSubjectSelected(subjectArrayList.get(position));
                //Toast.makeText(getContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
            }
        });

        subjectListView.setAdapter(arrayAdapter);

    }

    /*    private ArrayList<Subject> getSubjectList(){
            LocalStorage localStorage = LocalStorage.getInstance(getContext());
            return localStorage.getList(Constants.TEACHER_SUBJECT_LIST, new TypeToken<ArrayList<Subject>>(){}.getType());
        }*/
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach( Context context) {
        ActivityContext = context;
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        try{
            onSubjectSelectedListener = (OnSubjectListViewReady) context;
            onSubjectListViewReady = (OnSubjectListViewReady) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
