package monocleintro;


import java.util.ArrayList;
import java.util.List;

public class IntroJavaExample {

    public static List<Integer> example(List<Integer> list) {
        ArrayList<Integer> newList = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++){
            Integer newValue = list.get(i) + 1;
            if(newValue > 0) newList.add(newValue);
        }
        return newList;
    }
}
