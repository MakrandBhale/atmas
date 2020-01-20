package com.makarand.atmas.Events;

import java.util.ArrayList;

import Model.Subject;

public interface OnSubjectListViewReady {
    /*An interface instantiated in SubjectChoice Fragment which helps
    * communicating with Teacher Activity
    * */


    /*gets subject array from Teacher activity*/
    /*ArrayList<Subject> getSubjectListArray();*/

    /* returns subject object which was clicked*/
    void onSubjectSelected(Subject subject);

}