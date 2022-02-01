package com.my.wallet;

import java.util.*;

public class groupingMapByKey<K, T> {
    private HashMap<K, ArrayList<T>> completedExercises;

    public groupingMapByKey() {
        this.completedExercises = new HashMap<>();
    }

    public void add(K user, T exercise) {
        // an empty list has to be added for a new user if one has not already been added
        //this.completedExercises.putIfAbsent(user, new ArrayList<>());
        if (this.completedExercises.get(user) == null) {
            this.completedExercises.put(user, new ArrayList<>());
        }

        // let's first retrieve the list containing the exercises completed by the user and add to it
        ArrayList<T> completed = this.completedExercises.get(user);
        completed.add(exercise);

        // the previous would also work without the helper variable as follows
        // this.completedExercises.get(user).add(exercise);
    }

    public void print() {
        for (K name: completedExercises.keySet()) {
            System.out.println(name + ": " + completedExercises.get(name));
        }
    }

    public Set<K> keySet(){
        return completedExercises.keySet();
    }

    public ArrayList<T> get(K user){
        return completedExercises.get(user);
    }

}
