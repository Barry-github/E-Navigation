package cn.ehanghai.route.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tool {

    public  static Integer toBinNum(List<Integer> values){
        Integer data=0;
        for (Integer value: values) {
            data=data|(int)(Math.pow(2,value-1));
        }
      return  data;
    }

    public  static  List<Integer> toBinValues(Integer data,Integer count)
    {

        List<Integer> values=new ArrayList<>();
        for(int i=0;i<count;++i)
        {
            Integer value=data&(int)(Math.pow(2,i));

            if(value!=0)
            values.add(i+1);
//            else
//                values.add(0);

        }
        return values;

    }



    public static List<List<Integer>> combinations(List<Integer> data)
    {
        List<List<Integer>> result=new ArrayList<>();
        for(int i=1;i<=data.size();++i)
        {
            combinations(new ArrayList<Integer>(), data,result, i);
        }

        return result;

    }

    private static void combinations(List<Integer> selected, List<Integer> data,List<List<Integer>> result, int n) {
        //initial value for recursion
        //how to select elements
        //how to output
        if (n == 0) {
            List<Integer> tmpdatas=new ArrayList<>();
            for (Integer i : selected) {
                tmpdatas.add(i);
            }
            result.add(tmpdatas);
            return;
        }
        if (data.isEmpty()) return;

        //select element 0
        selected.add(data.get(0));//将第一个值添加上去
        combinations(selected, data.subList(1, data.size()),result, n - 1);
        //un-select element 0

        selected.remove(selected.size() - 1);
        combinations(selected, data.subList(1, data.size()),result, n);
    }
    public static void main(String[] args) {

//        List<List<Integer>> result= combinations( Arrays.asList(1, 2, 3));
//        System.out.println("result = " + result);

        List<Integer> values=Arrays.asList(1, 0, 3,0);
//        List<Integer> values=Arrays.asList(1, 2, 3);
       int data= toBinNum(values);

        System.out.println("data = " + data);

        values=toBinValues( data,4);

        System.out.println("values = " + values);
    }
}
